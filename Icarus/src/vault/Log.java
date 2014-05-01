/*
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FORimport static vault.Log.*;

 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package vault;

/**
 *
 * @author apfel
 * <p>
 * this is a little helper class, from now on I can call Log.log from everywhere without
 * handling an object ...
 * <p>
 * Usage:
 * first import vault.*; and then initialize Log with the init function, 
 * you only have to do it once, somewhere at the beginning of your project, after 
 * that you can use log() everywhere.
 */
public class Log {
    private static LogWriter log;                  // the logger
    private static boolean initialized = false;    // will be true if logger is ready

    /**
     * init initializes the Log subsystem, it will create a static context where
     * Log lives. So that it can be called From everywhere.
     *
     * @param pathToLogfile Path to where you want the logfile to be saved as String
     * @param verboseLevel here you can configure the loglevel of the messages
     */
    public static void init(String path, int verboseLevel) {
        log = new LogWriter(path, verboseLevel);
        initialized = true;
    }

    /**
     * returns current LogWriter
     *
     * @return current LogWriter
     */
    public static LogWriter get_log() {
        return log;
    }

    /**
     * log simply writes a message to the logfile.
     *
     * @param key The key for this Message
     * @param verboseLevel The verbosity level
     * @param message the message
     */
    public static void log(String key, int verboseLevel, String message) {
        log.log(key, verboseLevel, message);
    }

    /**
     * isInitialized will return the logger status
     * @return true if the logger is ready and false if its not
     */
    public static boolean isInitialized() {
        return initialized;
    }

    public static void kill()
    {
        log.kill();
    }
}
