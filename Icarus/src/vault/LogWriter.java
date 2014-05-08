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
    private SimpleDateFormat sdf; 
    private int verboseLevel;
    private LogWriterWorker LogWorker;
    private Thread worker;
    private boolean silent;

    /**
     * LogWriter Constructor
     * @param configReader gets the Path from the configfile and returns it
     */
    public LogWriter(Config_Reader configReader){
        silent       = configReader.get_boolean("silent");
        verboseLevel = configReader.get_int("verbosity_level");
        LogWorker    = new LogWriterWorker(configReader, lbq);
        worker       = new Thread(LogWorker);
        sdf          = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        get_hostname();
        worker.start();
        log("LogWriter", 0, "initialized LogWriter");
        configReader.setLogWriter(this);
    }

    /**
     * get_hostname gets the name of the host
     */
    private void get_hostname() {
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            host = "localhost";
        }
    }
    /*
    private String getTimestamp()
    {
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        return sdf.format(date);
    }
    */

    /**
     * log generates one log line and adds it to the LinkeBlockingQueue lbq
     * @param key this variable tells you where the log message is coming from
     * @param verboseLevel this variable indicates how important the log message is
     * @param logMessage this is the message that will be written in the log file 
     */
    public void log(String key, int verboseLevel, String logMessage)
    {
        /*
        if (!silent && ( verboseLevel == 0 ) )
        {
            System.out.println(logMessage);
        }

        if ( verboseLevel <= this.verboseLevel )
        {
            String logLine ="["+getTimestamp()+"]"+" ["+
                                host+"] "+"["+
                                key+"]"+": "+
                                logMessage+"\n";
            lbq.offer(logLine);
        }
        */
        if (!silent && ( verboseLevel == 0 ) )
        {
            System.out.println(logMessage);
            lbq.offer("[" + sdf.format(new Date().getTime()) + "] [" + host + "] [" + key + "]: " + logMessage + "\n");
        }
        else if ( verboseLevel <= this.verboseLevel )
        {
            lbq.offer("[" + sdf.format(new Date().getTime()) + "] [" + host + "] [" + key + "]: " + logMessage + "\n");
        }
    }

    /**
     * kill will stop the thread
     */
    public void kill()
    {
        LogWorker.kill();
        log("LogWriter", 0, "exiting LogWriter.");
    }
}
