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
import java.io.File;
import java.util.regex.Pattern;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * Logger, used to write log files.
 * <p>
 * @version 0.8
 */
public class Logger
{
    final private LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<>(8192);
    final private SimpleDateFormat              sdf   = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
    final private String  log_file_name        = "Icarus_latest";
    final private String  log_file_backup_name = "Icarus_backup";
    final private String  log_file_ending      = ".log";
    final private String  log_key              = " [Logger]: ";
          private String  path_to_log_file_folder;
    final private String  path_to_log_file;
    final private boolean silent;
    final private Thread  thread;
    final private int     verboseLevel;
    final private int     max_backup_files;
          private boolean alive = true;

    /**
     * LogWriter Constructor
     * @param config needed for verbosity_level and silent value.
     */
    public Logger(Config_Reader config)
    {
        thread = new Thread(new Log_Thread());

        verboseLevel     = config.get_int("verbosity_level");
        silent           = config.get_boolean("silent");
        /* --fixme-- */
        /* add config value here */
        max_backup_files = 20;

        path_to_log_file_folder = config.get_string("LogWriter");
        path_to_log_file = file_rotation();

        thread.setName("Log_Thread");
        thread.start();
        log(2, log_key, "initialized Logger.\n");
        config.set_Logger(this);
    }

    /**
     * this function will rename the last logfile to log_file_backup_name,
     * and return a string containing the full path to the new log file.
     * @return string containing the full path to the new log file.
     */
    private String file_rotation()
    {
        if(!path_to_log_file_folder.endsWith("/"))
            path_to_log_file_folder += "/";

        File file = new File(path_to_log_file_folder + log_file_name + log_file_ending);
        if(!file.exists())
            return new String(path_to_log_file_folder + log_file_name + log_file_ending);

        move(0);
        return new String(path_to_log_file_folder + log_file_name + log_file_ending);
    }

    private void move(int count)
    {
        if (count == max_backup_files)
            return;

        File file_from;
        File file_to;
        if (count == 0)
            file_from = new File(path_to_log_file_folder + log_file_name + log_file_ending);
        else
        {
            if(count < 10)
                file_from = new File(path_to_log_file_folder + log_file_backup_name
                        + "_00" + new Integer(count).toString()
                        + log_file_ending);
            else if(count < 100)
                file_from = new File(path_to_log_file_folder + log_file_backup_name
                        + "_0" + new Integer(count).toString()
                        + log_file_ending);
            else
                file_from = new File(path_to_log_file_folder + log_file_backup_name
                        + "_" + new Integer(count).toString()
                        + log_file_ending);
        }

        if(count < 9)
            file_to = new File(path_to_log_file_folder + log_file_backup_name
                    + "_00" + new Integer(count + 1).toString()
                    + log_file_ending);
        else if(count < 99)
            file_to = new File(path_to_log_file_folder + log_file_backup_name
                    + "_0" + new Integer(count + 1).toString()
                    + log_file_ending);
        else
            file_to = new File(path_to_log_file_folder + log_file_backup_name
                    + "_" + new Integer(count + 1).toString()
                    + log_file_ending);

        if(file_to.exists())
            move(count + 1);

        log(2, log_key, "moving file:", file_from.getAbsolutePath(), "\n");
        log(2, log_key, "         to:", file_to.getAbsolutePath(),   "\n");

        if(!file_from.renameTo(file_to))
        {
            System.out.println(
                    "error near logfile rotating, could not move file");
            System.exit(1);
        }
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
     * @param key message key
     * @param msgVerboseLevel  incomming message verbosity
     * @param logMessage log message
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
        log(0, log_key, "exiting Logger.\n");
    }

    private class Log_Thread implements Runnable
    {
        @Override
        public void run()
        {
            String message = new String();
            String queue_element[];
            int queue_size;
            try (Writer file_writer =
                    new BufferedWriter(
                        new FileWriter(path_to_log_file, true)))
            {
                while( alive || !queue.isEmpty() )
                {
                    queue_element = queue.take();

                    message = sdf.format(new Long(new Date().getTime()));
                    for(String buff : queue_element)
                        message += buff;

                    if(verboseLevel == 4)
                    {
                        queue_size = queue.size();
                        if (queue_size < 10)
                            message = "   " + queue.size() + "| " + message;
                        else if (queue_size < 100)
                            message = "  " + queue.size() + "| " + message;
                        else if (queue_size < 1000)
                            message = " " + queue.size() + "| " + message;
                        else
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
                System.out.println("LogWriter: Could not open/write/create log file at path: " + path_to_log_file);
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