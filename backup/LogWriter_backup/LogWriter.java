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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import linc.*;

/**
 * @author Aleksej Weinberg <weinberg.aleksej@yahoo.de>
 * <p>
 * This class gathers the information about one log line and hands it over to
 * the worker thread
 * <p>
 * @version 1.0
 */
public class LogWriter
{
    final private LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(1024);
    final private SimpleDateFormat sdf;
    final private LogWriterWorker  LogWorker;
    final private boolean          silent;
    final private Thread           worker;
    final private int              verboseLevel;

    /**
     * LogWriter Constructor
     * @param config needed for silent value and verbosity_level.
     */
    public LogWriter(Config_Reader config)
    {
        verboseLevel = config.get_int("verbosity_level");
        silent       = config.get_boolean("silent");
        LogWorker    = new LogWriterWorker(config, lbq);
        worker       = new Thread(LogWorker);
        sdf          = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        worker.setName("LogWriter");
        worker.start();
        log("LogWriter", 2, "initialized LogWriter");
        config.setLogWriter(this);
    }

    /**
     * log generates one log line and adds it to the LinkeBlockingQueue lbq
     * @param key this variable tells you where the log message is coming from
     * @param msgVerboseLevel this variable indicates how important the log message is
     * @param logMessage this is the message that will be written in the log file 
     */
    public void log(String key, int msgVerboseLevel, String logMessage)
    {
        if (( msgVerboseLevel == 0 ) && !silent )
        {
            System.out.println(logMessage);
            lbq.offer("["   + sdf.format(new Long(new Date().getTime()))
                    + "] [" + key
                    + "]: " + logMessage + "\n");
        }
        else if ( msgVerboseLevel <= this.verboseLevel )
        {
            lbq.offer("["   + sdf.format(new Long(new Date().getTime()))
                    + "] [" + key
                    + "]: " + logMessage + "\n");
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