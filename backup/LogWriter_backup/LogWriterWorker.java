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
    final private LinkedBlockingQueue<String> lbq;
    final private SimpleDateFormat sdf;
    final private Date    date;
    final private String  path_to_log_files;
    final private int     verboseLevel;
          private String  message;
          private boolean alive = true;

    /**
     * LogWriterWorker constructor
     * @param config config reader object with all parsed config values
     * @param lbq LinkedBlockingQueue in which the log messages will be added
     */
    public LogWriterWorker(Config_Reader config, LinkedBlockingQueue<String> lbq)
    {
        this.lbq     = lbq;
        verboseLevel = config.get_int("verbosity_level");
        date = new Date(System.currentTimeMillis());
        sdf  = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
        path_to_log_files = config.get_string("LogWriter") + sdf.format(date) + ".log";
    }

    @Override
    public void run()
    {
        try (Writer fWriter = new BufferedWriter(new FileWriter(path_to_log_files, true)))
        {
            while( alive || !lbq.isEmpty() )
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
        }
        catch (IOException e)
        {
            System.out.println("LogWriter: Could not open/write/create log file at path: " + path_to_log_files);
            System.out.println("LogWriter: IOException: " + e);
            System.exit(1);
        }
        catch (InterruptedException ex)
        {
            System.out.println("LogWriter: Could not take Log message from Linked Blocking Queue");
            System.out.println("LogWriter: InterruptedException: " + ex);
            System.exit(1);
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