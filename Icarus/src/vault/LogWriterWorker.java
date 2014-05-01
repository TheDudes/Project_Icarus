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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import linc.*;

/**
 * @author Aleksej Weinberg <weinberg.aleksej@yahoo.de>
 * <p>
 * This class creates a logfile and writes logs
 * <p>
 * @version 1.0
 */
public class LogWriterWorker implements Runnable
{
    private LinkedBlockingQueue<String> lbq;
    private Config_Reader confReader;
    private Date date;
    private SimpleDateFormat sdf;
    private boolean alive = true;
    private String tmpFilePath;
    private String pathToLogfiles;
    private String message;
    private int verboseLevel;

    /**
     * LogWriterWorker constructor
     * @param  pathToLogfile Path to where the log file is being saved
     * @param lbq LinkedBlockingQueue in which the log messages will be added
     */
    public LogWriterWorker(Config_Reader confReader, LinkedBlockingQueue<String> lbq)
    {
        this.confReader = confReader;
        this.lbq        = lbq;
        tmpFilePath     = confReader.get_path("LogWriter");
        verboseLevel    = confReader.get_int("verbosity_level");
        pathToLogfiles  = tmpFilePath+getTimestamp() + ".log";
    }

    /**
     * getTimeStamp returns the current time to use it in the timestamp
     * @return String
     */
    private String getTimestamp() {
        date = new Date(System.currentTimeMillis());
        sdf  = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    public void run() {
        try (Writer fWriter = new BufferedWriter(new FileWriter(pathToLogfiles, true))) {
            while( !lbq.isEmpty() || alive )
            {
                if(verboseLevel == 4)
                {
                    message = lbq.size() + " " + lbq.take();
                    fWriter.write(message);
                    fWriter.flush();
                }
                else
                {
                    fWriter.write(lbq.take());
                    fWriter.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while writing File!");
        } catch (InterruptedException ex) {
            Logger.getLogger(LogWriterWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * kill will stop the thread
     */
    public void kill()
    {
        alive = false;
    }
}
