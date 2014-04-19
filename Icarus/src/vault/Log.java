/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vault;

/**
 *
 * @author apfel
 * <p>
 * this is a little helper class, from now on i can call Log.log from everywhere without
 * handling an obejct ...
 * <p>
 * Usage:
 * first initialize Log with the init function, you only have to do it once,
 * somewhere at the beginning of your project, after that you can use log()
 * everywhere.
 */
public class Log {
    private static LogWriter log;                  // the logger
    private static boolean initialized = false;    // will be true if logger is ready
    
    /**
     * init initializes the Log subsystem, it will create a static context where
     * Log lives. So that it can be called From everywhere.
     * 
     * @param pathToLogfile Path to logfile as String
     * @param verboseLevel here you can configure the loglevel of the messages
     */
    public static void init(String pathToLogfile, int verboseLevel) {
        log = new LogWriter(pathToLogfile, verboseLevel);
        initialized = true;
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
     * isInitialized will return tha logger status
     * @return true if the logger is ready and false if its not
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
