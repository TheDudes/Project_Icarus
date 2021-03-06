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

import Icarus.Main;
import config.Config_Reader;
import java.io.IOException;
import logger.*;
import parser.*;
import java.net.*;

/**
 * our class IO_Manager, initiates all the IO stuff so we can talk to the other
 * group
 */
public class IO_Manager {

    private String logKey = " [IO_Manager]: ";
    private Synchronous_IO_Worker syncWorker;
    private Thread syncWorkerThread;
    private ASynchronous_IO_Worker asyncWorker;
    private Thread asyncWorkerThread;
    private Socket syncSocket;
    private Logger logger;

    /**
     * @param logger LogWriter so we can log the stuff we want to print
     * @param confReader Config_Reader so we can get the hostname and port for
     * the Sockets
     * @param infoColl we have to get this object as we have to pass it on to
     * the Synchronous_IO_Worker
     */
    public IO_Manager(Logger logger, Config_Reader confReader, ParserContainer infoColl) {
        this.logger = logger;
        logger.log(0, logKey, "trying to establish a connection to the IO Manager\n");
        try {

            logger.log(0, logKey, "established the connection to the IO Manager\n");
            logger.log(4, logKey, "creating the workers\n");
            syncWorker = new Synchronous_IO_Worker(logger, infoColl, confReader);
            asyncWorker = new ASynchronous_IO_Worker(logger, infoColl, confReader);
            syncWorkerThread = new Thread(syncWorker);
            syncWorkerThread.start();
            logger.log(4, logKey, "created the syncWorker\n");
            asyncWorkerThread = new Thread(asyncWorker);
            asyncWorkerThread.start();
            logger.log(4, logKey, "created the asyncWorker, exiting constructor\n");
        } catch (IOException e) {
            logger.log(0, logKey,
                    "\n\n",
                    "ERROR: \n",
                    "    An IOException occured inside of IO_Manager\n",
                    "    thread, but dont know which.\n",
                    "DETAILED ERROR: \n    ",
                    e.getMessage(), "\n\n"
            );
            Main.exit();

        } finally {
            if (syncSocket != null) {
                try {
                    syncSocket.close();
                } catch (Exception e) {
                    Main.exit();
                }
            }
        }
    }

    /**
     * calls the kill functions of the two workers so they stop, this should
     * also close all of their ress, as well as those in the SynchronousIO
     * IO_Managerlthough there are none atm)
     */
    public void kill() {
        logger.log(0, logKey, "exiting IO \n");
        syncWorker.kill();
        asyncWorker.kill();
    }

}
