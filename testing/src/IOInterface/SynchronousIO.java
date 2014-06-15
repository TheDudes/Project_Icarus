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

import config.Config_Reader;
import logger.*;
import parser.*;
import java.net.*;

/**
* our class SynchronousIO, initiates all the IO stuff so we can talk to the other group
*/
public class SynchronousIO{

    private String logKey = " [SynchronousIO]: ";
    private Synchronous_IO_worker syncWorker;
    private Thread syncWorkerThread;
    private ASynchronous_IO_Worker asyncWorker;
    private Thread asyncWorkerThread;
    private Socket syncSocket;

    /**
    * @param logger LogWriter so we can log the stuff we want to print
    * @param confReader Config_Reader so we can get the hostname and port for the Sockets
    * @param infoColl we have to get this object as we have to pass it on to the Synchronous_IO_worker
    */
    public SynchronousIO(Logger logger, Config_Reader confReader, ParserContainer infoColl){
       logger.log(0, logKey, "trying to establish a connection to the IO Manager\n");
       try{
           // syncSocket = new Socket(confReader.get_string("hostname"), confReader.get_int("sync_port", 1, 65536));
            /* --fixme-- */
            /* Potential resource leak: 'aSyncSocket' may not be closed */
           // ServerSocket aSyncSocket = new ServerSocket(confReader.get_int("async_port", 1, 65536));
            logger.log(0, logKey, "established the connection to the IO Manager\n");
            logger.log(4, logKey, "creating the workers\n");
            syncWorker = new Synchronous_IO_worker(logger, infoColl, confReader);
            asyncWorker = new ASynchronous_IO_Worker(logger, infoColl, confReader);
            syncWorkerThread = new Thread(syncWorker);
            syncWorkerThread.start();
            logger.log(4, logKey, "created the syncWorker\n");
            asyncWorkerThread = new Thread(asyncWorker);
            asyncWorkerThread.start();
            logger.log(4, logKey, "created the asyncWorker, exiting constructor\n");
       }
       catch(Exception e){
            logger.log(0, logKey, "Exception while trying to establish a connection to the IO Manager:" , e .getMessage(), "\n");
            System.exit(0);
       }
       finally{
           if(syncSocket != null){
                try{
                    syncSocket.close();
                }
                catch(Exception e){
                    logger.log(0, logKey, "Exception in Exception when trying to close the socket: " , e.getMessage(), "\n");
                }
            }
       }
    }

    /**
    * calls the kill functions of the two workers so they stop, this should also close all of their 
    * resources, as well as those in the SynchronousIO themselves (although there are none atm)
    */
    public void kill(){
        syncWorker.kill();
        asyncWorker.kill();
    }

}
