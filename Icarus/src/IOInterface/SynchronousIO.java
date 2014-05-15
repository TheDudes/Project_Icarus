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
import linc.Config_Reader;
import logger.*;
import parser.*;
import java.net.*;

/* --fixme-- */
/* Javadoc: Missing comment for public declaration */
public class SynchronousIO{

    private Synchronous_IO_Worker syncWorker;
    private Thread syncWorkerThread;
    /* --fixme-- */
/* The value of the field SynchronousIO.logger is not used */
    private Logger logger;
    private Socket syncSocket;

    /* --fixme-- */
/* Javadoc: Missing comment for public declaration */
    public SynchronousIO(Logger logger, Config_Reader confReader, InfoCollector infoColl){
       this.logger = logger;
       logger.log("SynchronousIO", 0, "trying to establish a connection to the IO Manager");
       try{
            syncSocket = new Socket(confReader.get_string("hostname"), confReader.get_int("sync_port"));
            logger.log("SynchronousIO", 0, "established the connection to the IO Manager");
            logger.log("SynchronousIO", 4, "creating the worker"); 
            syncWorker = new Synchronous_IO_Worker(logger, infoColl, syncSocket);
            syncWorkerThread = new Thread(syncWorker);
            syncWorkerThread.start();
            logger.log("SynchronousIO", 4, "created the worker, exiting constructor");
       }
       catch(Exception e){
            logger.log("SynchronousIO", 0, "Exception while trying to establish a connection to the IO Manager:" + e );
            System.exit(0);
       }
       finally{
           if(syncSocket != null){
                try{
                    syncSocket.close();
                }
                catch(Exception e){
                    logger.log("SynchronousIO", 0, "Exception in Exception when trying to close the socket: " + e);
                }
            }
       }
    }
    
}
