/**
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */
package IOInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import logger.*;
import parser.*;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class Synchronous_IO_worker implements Runnable {

    private LinkedBlockingQueue<IO_Package> lbq;
    private Logger logWriter;
    private InfoCollector infoCollector;
    private Socket client;
    private String key = "Synchronous_IO_worker";
    private IO_Package ioPackage;
    private boolean alive = true;
    private int namespaceID = 1;
    private int count = 1;
    private byte rwFlag;
    private String letter;
    private String number;

    
    /**
     * @param logWriter
     * @param infoCollector
     * @param client
     */
    public Synchronous_IO_worker(Logger logWriter, InfoCollector infoCollector, Socket client) {

        this.logWriter = logWriter;
        this.infoCollector = infoCollector;
        this.lbq = infoCollector.get_com_channel_queue();
        this.client = client;

    }

    @Override
    public void run() {
        try (
                BufferedInputStream inFromServer = new BufferedInputStream(client.getInputStream());
                BufferedOutputStream outToServer = new BufferedOutputStream(client.getOutputStream());) {

            logWriter.log(key, 4, "Streams established");
            PacketWriter packetWriter = new PacketWriter(outToServer);
            while (alive || !lbq.isEmpty()) {

                ioPackage = lbq.take();

                logWriter.log(key, 3, "IO_Package received."
                        + " Device ID: " + ioPackage.device_id
                        + " Pin ID: " + ioPackage.pin_id
                        + " New Value: " + ioPackage.value);

                if (ioPackage.abilities == 0) {

                    logWriter.log(key, 0, "Undefined Ability for Device " + ioPackage.device_id + " at Pin " + ioPackage.pin_id);

                    throw new Undefined_ability_exception();

                } else if (ioPackage.to_poll == true) {

                    polling_init(packetWriter);                 //initiates polling and writes the package to the ioManager
                    logWriter.log(key, 4, "Wrote IO_Packet to the IO Manager for Polling");

                } else {
                    write_package(packetWriter);                //writes a value to the ioManager
                    logWriter.log(key, 4, "Wrote IO_Packet to the IO Mangager for Writing");

                }

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    /**
     * Function which takes a PacketWriter and writes a IO_Packet with a
     * Poll-Flag to the IO_Manager
     *
     * @param packetWriter
     */
    public void polling_init(PacketWriter packetWriter) throws IOException{
        rwFlag = 11;
        IO_Packet ioPacket = new IO_Packet(ioPackage.device_id, ioPackage.pin_id, namespaceID, count, rwFlag, ioPackage.value);
        packetWriter.write(ioPacket);
    }

    /**
     * Function which takes a PacketWriter and writes a IO_Packet with a
     * Write-Flag to the IO Manager
     *
     * @param packetWriter
     */
    public void write_package(PacketWriter packetWriter) {
        rwFlag = 0;
        IO_Packet ioPacket = new IO_Packet(ioPackage.device_id, ioPackage.pin_id, namespaceID, count, rwFlag, ioPackage.value);
        packetWriter.write(ioPacket);

    }

// 
//    public void convert_deviceID() {
//
//        letter = ioPackage.device_id.substring(0, 1);
//        number = ioPackage.device_id.substring(1);
//        helpArray = letter.getBytes();
//        dataOutput[0] = helpArray[0];
//        dataOutput[1] += Integer.parseInt(number);
//
//    }
    /**
     * transforms an integer into a byteArray with 2 fields, the first one is
     * the integer/255 the second one the integer%255 If you want to get the
     * integer again be advised that due to the fact that if the first byte is 0
     * we do not know if it should be 0 or 256 we wont be able to get the full
     * scope of the integer, we can only get 0-65279, everything higher will be
     * 0 - 255 again which is wrong - so don't use a higher number than 65279
     *
     * @param toTransform the integer we want to transform into a byteArray with
     * 2 fields
     * @return a byteArray with two fields, the first is the int/255 the second
     * is the int%255
     */
    private byte[] getBytes(int toTransform) {
        Integer mod = toTransform % 255;
        Integer times = toTransform / 255;
        byte[] byteArray = new byte[2];
        byteArray[0] = times.byteValue();
        byteArray[1] = mod.byteValue();
        return byteArray;
    }

    /**
     * transforms a byteArray with 2 fields into an integer, the first entry has
     * to be the integer/255, the second entry the integer%255 Be advised: due
     * to the fact that if the first byte is 0 we do not know if it should be 0
     * or 256 we wont be able to get the full scope of the integer, we can only
     * get 0-65279, everything higher will be 0 - 255 again which is wrong - so
     * don't use a higher number than 65279
     *
     * @param toTransform the byteArray we want to transform, first entry is the
     * number/255, the second is number%255
     * @return the integer value we got from the byteArray
     */
    private int getIntFromBytes(byte[] toTransform) {
        int mod = toTransform[1];
        int times = toTransform[0];
        if (mod < 0) {
            mod = 256 + mod;
        }
        if (times < 0) {
            times = 256 + mod;
        }
        int value = times * 255 + mod;
        return value;
    }

    /**
     * kills the running thread
     */
    public void kill() {
        alive = false;

    }
}
