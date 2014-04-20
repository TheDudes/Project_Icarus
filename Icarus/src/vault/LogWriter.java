
/*
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

package vault;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class gathers the information about one log line and hands it over to
 * the worker class
 * @author Aleksej Weinberg <weinberg.aleksej@yahoo.de>
 * @version 1.0
 */
public class LogWriter {

    private LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(1024);
    private String host; //Hostname
    private Date date; 
    private SimpleDateFormat sdf; 
    private int verboseLevel;
    private Thread worker;

    public LogWriter(String pathToLogfile, int verboseLevel){
        worker = new Thread(new LogWriterWorker(lbq, pathToLogfile));
        worker.start();
        this.verboseLevel = verboseLevel;
        getHostname();
    }
    //Gets the Hostname
    private void getHostname() {
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            host = "localhost";
        }
    }
    //Generates the Timestamp
    private String getTimestamp(){ 
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        return sdf.format(date);
    }
    //Writes to the LogFile
    public void log(String key, int verboseLevel, String logMessage) {
        if(verboseLevel <= this.verboseLevel){

            String logLine ="["+getTimestamp()+"]"+" ["+
                                host+"] "+"["+
                                key+"]"+": "+
                                logMessage+"\n";
            lbq.offer(logLine); 
        }
    }
    //kills the Log Thread
    public void kill(){
        worker.stop();
    }

}
