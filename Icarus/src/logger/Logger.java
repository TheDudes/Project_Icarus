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
 * vim: foldmethod=syntax:
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

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * Logger, used to write log files.
 * <p>
 * @version 0.8
 */
public class Logger
{
    final private LinkedBlockingQueue<String[]>
                          queue = new LinkedBlockingQueue<>(8192);
    final private SimpleDateFormat
                          sdf   = new SimpleDateFormat("dd-MM-yyy_HH:mm:ss");
    final private String  log_file_name;
    final private String  log_file_backup;
    final private String  log_file_ending;
    final private String  log_key = " [Logger]: ";
    final private long    log_file_max_size;
    final private int     log_check_count;
          private String  log_file_path;
          private String  path_to_log_file;
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
        verboseLevel      = config.get_int("verbosity_level");
        silent            = config.get_boolean("silent");
        max_backup_files  = config.get_int("Log_max_files");
        log_file_name     = config.get_string("Log_file_name");
        log_file_backup   = config.get_string("Log_file_backup");
        log_file_ending   = config.get_string("Log_file_ending");
        log_file_path     = config.get_string("Log_file_path");
        log_check_count   = config.get_int("Log_check_count");
        log_file_max_size = evaluate_size(config.get_string("Log_file_max_size"));
        path_to_log_file  = file_rotation();

        thread = new Thread(new Log_Thread());
        thread.setName("Log_Thread");
        thread.start();
        log(2, log_key, "initialized Logger.\n");
        config.set_Logger(this);
    }

    /**
     * this function will rename the last logfile to log_file_backup,
     * and return a string containing the full path to the new log file.
     * @return string containing the full path to the new log file.
     */
    private String file_rotation()
    {
        if(!log_file_path.endsWith("/"))
            log_file_path += "/";

        File file = new File(log_file_path + log_file_name + log_file_ending);
        if(!file.exists())
            return new String(log_file_path + log_file_name + log_file_ending);

        recursive_move(0);
        return new String(log_file_path + log_file_name + log_file_ending);
    }

    /**
     * will move log files recursivly, needed for file rotation
     * @param count needed to keep track of recursion level and file
     */
    private void recursive_move(int count)
    {
        if (count == max_backup_files)
            return;

        File file_from;
        File file_to;
        if (count == 0)
            file_from = new File(log_file_path + log_file_name + log_file_ending);
        else
        {
            if(count < 10)
                file_from = new File(log_file_path + log_file_backup
                        + log_file_ending
                        + ".00" + new Integer(count).toString());
            else if(count < 100)
                file_from = new File(log_file_path + log_file_backup
                        + log_file_ending
                        + ".0" + new Integer(count).toString());
            else
                file_from = new File(log_file_path + log_file_backup
                        + "." + new Integer(count).toString()
                        + log_file_ending);
        }

        if(count < 9)
            file_to = new File(log_file_path + log_file_backup
                    + log_file_ending
                    + ".00" + new Integer(count + 1).toString());
        else if(count < 99)
            file_to = new File(log_file_path + log_file_backup
                    + log_file_ending
                    + ".0" + new Integer(count + 1).toString());
        else
            file_to = new File(log_file_path + log_file_backup
                    + log_file_ending
                    + "." + new Integer(count + 1).toString());

        log(4, log_key, "recursive move call with count: "
                                + new Integer(count).toString() + "\n");

        if(file_to.exists())
            recursive_move(count + 1);

        log(4, log_key, "moving file:", file_from.getAbsolutePath(), "\n");
        log(4, log_key, "         to:", file_to.getAbsolutePath(),   "\n");

        if(!file_from.renameTo(file_to))
        {
            System.out.println(
                    "error near logfile rotating, could not move file");
            System.exit(1);
        }
    }

    private static long evaluate_size(String s)
    {
        if(s.contains("M"))
        {
            return 1024*1024*
                    (long)Long.parseLong(s.substring(0, s.indexOf("M",0)));
        }
        else if(s.contains("B"))
        {
            return (long)Long.parseLong(s.substring(0, s.indexOf("B",0)));
        }
        else if(s.contains("K"))
        {
            return 1024*
                    (long)Long.parseLong(s.substring(0, s.indexOf("K",0)));
        }
        else if(s.contains("G"))
        {
            return 1024*1024*1024*
                    (long)Long.parseLong(s.substring(0, s.indexOf("G",0)));
        }
        else
        {
            System.out.print("could not evalute Logfile Size from: "
                                    + s + "\n");
            System.exit(0);
            return 1;
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
            int count = 0;
            try
            {
                Writer file_writer =
                    new BufferedWriter(
                        new FileWriter(path_to_log_file, true));
                File file = new File(path_to_log_file);
                while( alive || !queue.isEmpty() )
                {
                    count++;
                    queue_element = queue.take();
                    if((count % log_check_count) == 0)
                    {
                        count = 0;
                        if(file.length() >= (log_file_max_size))
                        {
                            file_writer.close();
                            recursive_move(0);
                            file_writer =
                                new BufferedWriter(
                                    new FileWriter(path_to_log_file, true));
                        }
                    }

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
                file_writer.close();
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
