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
 *
 * file: Icarus/src/Icarus/main.java
 * @author d4ryus - https://github.com/d4ryus/
 * @version 0.8
 * vim: foldmethod=syntax:foldcolumn=7:
 */

package logger;

import linc.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * Main, here is where the magic happens.
 * <p>
 * @version 0.8
 */
public class Logger
{
    final private LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<>(1024);
    final private SimpleDateFormat sdf;
    final private boolean    silent;
    final private String     path_to_log_files;
    final private Thread     thread;
    final private int        verboseLevel;
          private boolean    alive = true;

    /**
     * LogWriter Constructor
     * @param config needed for silent value and verbosity_level.
     */
    public Logger(Config_Reader config)
    {
        sdf    = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
        thread = new Thread(new Log_Thread());

        path_to_log_files = config.get_string("LogWriter") 
                             + sdf.format(new Long(new Date().getTime()))
                             + ".log";
        verboseLevel      = config.get_int("verbosity_level");
        silent            = config.get_boolean("silent");

        thread.setName("Log_Thread");
        thread.start();
        log(2, " [Logger]: ","initialized Logger.\n");
        config.set_Logger(this);
    }

    /**
     * will check for verbosity and add the String[] to the queue
     * @param verbosity incomming message verbosity
     * @param args message in String array
     */
    public void log(int verbosity, String... args)
    {
        if (( verbosity == 0 ) && !silent )
        {
            String message = new String();
            for (int i = 0; i < args.length; i++)
            {
                if(i == 0) continue;
                message += args[i];
            }
            System.out.print(message);
            queue.offer(args);
        }
        else if ( verbosity <= this.verboseLevel )
            queue.offer(args);
    }

    /**
     * @deprecated
     */
    public void log(String key, int msgVerboseLevel, String logMessage)
    {
        if (( msgVerboseLevel == 0 ) && !silent )
        {
            System.out.println(logMessage);
            String deprecated[] = {" [deprecated] ", ""};
            deprecated[1] = ("[" + key
                           + "]: " + logMessage + "\n");
            queue.offer(deprecated);
        }
        else if ( msgVerboseLevel <= this.verboseLevel )
        {
            String deprecated[] = {" [deprecated] ", ""};
            deprecated[1] = ("[" + key
                           + "]: " + logMessage + "\n");
            queue.offer(deprecated);
        }
    }

    /**
     * kill will stop the thread
     */
    public void kill()
    {
        alive = false;
        log(0," [Logger]: ", "exiting Logger.\n");
    }

    private class Log_Thread implements Runnable
    {
        @Override
        public void run()
        {
            String message = new String();
            String queue_element[];
            try (Writer file_writer =
                    new BufferedWriter(
                        new FileWriter(path_to_log_files, true)))
            {
                while( alive || !queue.isEmpty() )
                {
                    queue_element = queue.take();

                    message = sdf.format(new Long(new Date().getTime()));
                    for(String buff : queue_element)
                        message += buff;

                    if(verboseLevel == 4)
                    {
                        message = queue.size() + "| " + message;
                        file_writer.write(message);
                        file_writer.flush();
                    }
                    else
                    {
                        file_writer.write(message);
                        file_writer.flush();
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
    }
}
