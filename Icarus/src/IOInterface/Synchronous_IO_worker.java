/**
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package IOInterface;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
/* --fixme-- */
/* The import java.io.InputStream is never used */
import java.io.InputStream;
/* --fixme-- */
/* The import java.io.OutputStream is never used */
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import vault.*;
import parser.*;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class Synchronous_IO_worker implements Runnable {

    private LinkedBlockingQueue<IO_Package> lbq;
    private LogWriter logWriter;
    /* --fixme-- */
/* The value of the field Synchronous_IO_worker.infoCollector is not used */
    private InfoCollector infoCollector;
    private Socket client;
    private String key = "Synchronous_IO_worker";
    private IO_Package ioPackage;
    private boolean alive = true;
    private String letter;
    private String number;
    private byte[] helpArray = new byte[1];
    private byte[] dataOutput = new byte[8];

    /**
     * dataOutput[0] = Device ID dataOutput[1] = Device ID dataOutput[2] = PIN
     * ID dataOutput[3] = PIN ID dataOutput[4] = Namespace ID dataOutput[5] =
     * Count dataOutput[6] = Read/Write Flag dataOutput[7] = Value
     */
    /**
     *
     *
     * @param logWriter
     * @param infoCollector
     * @param client
     */
    public Synchronous_IO_worker(LogWriter logWriter, InfoCollector infoCollector, Socket client) {

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

            while (alive || !lbq.isEmpty()) {

                ioPackage = lbq.take();

                logWriter.log(key, 3, "IO_Package received."
                        + " Device ID: " + ioPackage.device_id
                        + " Pin ID: " + ioPackage.pin_id
                        + " New Value: " + ioPackage.value);

                if (ioPackage.abilities == 0) {

                    logWriter.log(key, 0, "Undefined Ability for Device " + ioPackage.device_id + " at Pin " + ioPackage.pin_id);

//                    throw new Undefined_ability_exception();

                /* --fixme-- */
/* Possible accidental assignment in place of a comparison. A condition expression should not be reduced to an assignment */
                } else if (ioPackage.to_poll == true) {

                    polling_init();

                } else {
                    make_package();
                }

                outToServer.write(dataOutput, 0, 8);
                outToServer.flush();

                /*       for (count = 1; count <= 5; count++) {

                 dataOutput[count] = 0;
                 }
                 */
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    /* --fixme-- */
/* Javadoc: Missing comment for public declaration */
    public void polling_init() {
        convert_deviceID();
        dataOutput[3] = ioPackage.pin_id;
        dataOutput[4] = 1;
        dataOutput[6] = 3;
        dataOutput[7] = 0;

    }

    /* --fixme-- */
/* Javadoc: Missing comment for public declaration */
    public void make_package() {
        convert_deviceID();
        dataOutput[3] = ioPackage.pin_id;
        dataOutput[4] = 1;
        dataOutput[6] = 0;
        dataOutput[7] = ioPackage.value;

    }

    /* --fixme-- */
/* Javadoc: Missing comment for public declaration */
    /* --fixme-- */
    /* Javadoc: Missing comment for public declaration */
    public void convert_deviceID() {

        letter = ioPackage.device_id.substring(0, 1);
        number = ioPackage.device_id.substring(1);
        helpArray = letter.getBytes();
        dataOutput[0] = helpArray[0];
        dataOutput[1] += Integer.parseInt(number);

    }

    /* --fixme-- */
/* Javadoc: Missing comment for public declaration */
    public void kill() {
        alive = false;

    }
    
    
    
    
    
    
    
    
}
