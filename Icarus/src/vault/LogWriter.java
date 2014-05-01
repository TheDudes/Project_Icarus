
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
import linc.*;

/**
 * @author Aleksej Weinberg <weinberg.aleksej@yahoo.de>
 * <p>
 * This class gathers the information about one log line and hands it over to
 * the worker class
 * <p>
 * @version 1.0
 */
public class LogWriter {

    private LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(1024);
    private String host; //Hostname
    private Date date; 
    private SimpleDateFormat sdf; 
    private int verboseLevel;
    private Thread worker;


    /**
     * LogWriter Constructor
     * @param confReader gets the Path from the configfile and returns it
     * @param verboseLevel this variable indicates how important the log message is
     */
    public LogWriter(Config_Reader confReader, int verboseLevel){
        worker = new Thread(new LogWriterWorker(confReader.get_path("LogWriter"), lbq));
        worker.start();
        this.verboseLevel = verboseLevel;
        getHostname();
    }

    public LogWriter(String path, int verboseLevel){
        worker = new Thread(new LogWriterWorker(path, lbq));
        worker.start();
        this.verboseLevel = verboseLevel;
        getHostname();
    }

    /**
     * getHostname gets the name of the host
     */
    private void getHostname() {
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            host = "localhost";
        }
    }

    /**
     * getTimeStamp returns the current time to use it in the timestamp
     *
     * @return String
     */
    private String getTimestamp(){ 
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * log generates one log line and adds it to the LinkeBlockingQueue lbq
     * @param key this variable tells you where the log message is coming from
     * @param verboseLevel this variable indicates how important the log message is
     * @param logMessage this is the message that will be written in the log file 
     */
    public void log(String key, int verboseLevel, String logMessage) {
        if(verboseLevel <= this.verboseLevel){

            String logLine ="["+getTimestamp()+"]"+" ["+
                                host+"] "+"["+
                                key+"]"+": "+
                                logMessage+"\n";
            lbq.offer(logLine);
        }
    }

    /* --warning--
     * thread.kill() is deprecated, more information:
     * http://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html
     */
    /**
     * kill will stop the thread
     */
    public void kill()
    {
        worker.stop();
    }
}
