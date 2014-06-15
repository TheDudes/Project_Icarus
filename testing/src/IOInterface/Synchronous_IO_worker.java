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
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import logger.*;
import parser.*;
import config.*;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class Synchronous_IO_worker implements Runnable {

    private final LinkedBlockingQueue<IO_Package> lbq;
    private final Logger logWriter;
    private Socket client;
    private final Config_Reader configReader;
    private final String key = " [Synchronous_IO_worker]: ";
    private IO_Package ioPackage;
    private boolean alive = true;
    private final int namespaceID = 1;
    private final int count = 1;
    private byte rwFlag;
    private String pollOrWrite;

    /**
     * @param logWriter
     * @param infoCollector
     * @param configReader
     */
    public Synchronous_IO_worker(Logger logWriter, ParserContainer infoCollector, Config_Reader configReader) {

        this.logWriter = logWriter;
        this.lbq = infoCollector.get_com_channel_queue();
        this.configReader = configReader;

    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            client = new Socket(configReader.get_string("hostname"), configReader.get_int("sync_port", 1, 65536));
            BufferedInputStream inFromServer = new BufferedInputStream(client.getInputStream());
            BufferedOutputStream outToServer = new BufferedOutputStream(client.getOutputStream());

            logWriter.log(4, key, "Streams established \n");

            PacketWriter packetWriter = new PacketWriter(outToServer);
            PacketReader packetReader = new PacketReader(inFromServer);
            while (alive) {
                ioPackage = lbq.take();
                if (ioPackage.byte_address.equals("Fake")) {
                    break;
                }
                logWriter.log(3, key, "IO_Package received.", " Device ID: ", ioPackage.byte_address, " Pin ID: ", ioPackage.pin_id, " New Value: ", Byte.toString(ioPackage.value), "\n");

                if (ioPackage.to_poll == true) {

                    polling_init(packetWriter);
                    packetWriter.flush();
                    logWriter.log(3, key, "Wrote IO_Packet with Device ID: ", ioPackage.byte_address, " to the IO Manager for Polling \n");
                } else {
                    write_package(packetWriter);                //writes a value to the ioManager
                    packetWriter.flush();
                    logWriter.log(4, key, "Wrote IO_Packet with Device ID: ", ioPackage.byte_address, " to the IO Mangager for Writing \n");
                }

                IO_Packet ioPacket = packetReader.readPacket();
                if (ioPacket.getRWFlag() == 0) {
                    logWriter.log(0, key, "Error received from IO Manager with Device ID: ", Integer.toString(ioPacket.getGeraeteId()), "\n");
                } else {
                    logWriter.log(3, key, "Succesfully", pollOrWrite, "\n");
                }
            }

            inFromServer.close();

            outToServer.close();

            client.close();

        } catch (IOException e) {
            logWriter.log(0, key,
                    "\n\n",
                    "ERROR: Could not create Socket, is the IO Manager running?\n",
                    "DETAILED ERROR:\n",
                    "    trying to connect to host: ", configReader.get_string("hostname"),
                    "\n    on port: ", ((Integer) configReader.get_int("sync_port")).toString(),
                    "\n    message: ", e.getMessage(), "\n\n"
            );
            logWriter.kill();
            System.exit(1);
        } catch (NumberFormatException e) {
            logWriter.log(0, key,
                    "\n\n",
                    "ERROR: Could not create Socket, NumberFormatException occured\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            logWriter.kill();
            System.exit(1);
        } catch (IllegalArgumentException e) {
            logWriter.log(0, key,
                    "\n\n",
                    "ERROR: Could not create Socket, IllegalArgumentException occured\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            logWriter.kill();
            System.exit(1);
        } catch (Exception e) {
            logWriter.log(0, key,
                    "\n\n",
                    "ERROR: An Exception occured inside of Synchronous_IO_Worker\n",
                    "       thread, but dont know which.\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            logWriter.kill();
            System.exit(1);

        }
    }

    /**
     * Function which takes a PacketWriter and writes a IO_Packet with a
     * Poll-Flag to the IO_Manager
     *
     * @param packetWriter
     * @throws java.io.IOException
     */
    private void polling_init(PacketWriter packetWriter) throws IOException {
        rwFlag = 11;
        pollOrWrite = " initialized IO Package for polling";
      //  logWriter.log(0, key, ioPackage.byte_address, "\t", Integer.toString(ioPackage.value), "\n");
        if (ioPackage.pin_id == null) {
            IO_Packet ioPacket = new IO_Packet(new Integer(ioPackage.byte_address), namespaceID, count, rwFlag, ioPackage.value);
            packetWriter.write(ioPacket);
        } else {
            IO_Packet ioPacket = new IO_Packet(new Integer(ioPackage.byte_address), new Integer(ioPackage.pin_id), namespaceID, count, rwFlag, ioPackage.value);
            packetWriter.write(ioPacket);
        }
    }

    /**
     * Function which takes a PacketWriter and writes a IO_Packet with a
     * Write-Flag to the IO Manager
     *
     * @param packetWriter
     * @throws java.io.IOException
     */
    private void write_package(PacketWriter packetWriter) throws IOException {
        rwFlag = 0;
        pollOrWrite = " wrote IO Package";

        if (ioPackage.pin_id == null) {
            IO_Packet ioPacket = new IO_Packet(new Integer(ioPackage.byte_address), namespaceID, count, rwFlag, ioPackage.value);
            packetWriter.write(ioPacket);
        } else {
            IO_Packet ioPacket = new IO_Packet(new Integer(ioPackage.byte_address), new Integer(ioPackage.pin_id), namespaceID, count, rwFlag, ioPackage.value);
            packetWriter.write(ioPacket);
        }

    }

    /**
     * kills the running thread
     */
    public void kill() {
        logWriter.log(1, key, "Triggered kill() in SyncedWorkerThread\n");
        alive = false;
        IO_Package fakePackage = new IO_Package("Fake", "Fake", rwFlag, 'I', true);
        lbq.add(fakePackage);
    }
}
