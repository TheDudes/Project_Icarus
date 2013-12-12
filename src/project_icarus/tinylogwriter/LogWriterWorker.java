/*
 * Copyright (c) 2013, Simon Mages <mages.simon@googlemail.com>
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

package project_icarus.tinylogwriter;

import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogWriterWorker expects an LickedBlockingQueue and a PrintWriter Object.
 * It will write the head of the Queue to the logfile until the queue is
 * empty, when it's empty it will wait until there are new elements.
 * 
 * @author Simon Mages <mages.simon@googlemail.com>
 */
public class LogWriterWorker implements Runnable {
    
    private final LinkedBlockingQueue<String> queue;
    private final PrintWriter printwriter;
    private boolean over = false;
    
    /**
     * Initializes the Object with the needed Parameters. 
     * 
     * @param queue         the queue from where the worker gets his strings to write
     * @param printwriter   the object which controls the file writing
     */
    public LogWriterWorker(LinkedBlockingQueue<String> queue, PrintWriter printwriter)
    {
        this.queue = queue;
        this.printwriter = printwriter;
    }
    
    /**
     * In this run Method we simply take the head of the queue and write it to
     * the logfile. But if one line equals "stopthat" the loop will end.
     */
    @Override public void run() {
        try {
            boolean flag = true;
            while (flag) {
                String currentline = queue.take();
                if ("stopthat".equals(currentline)) {
                    flag = false;
                } else {
                    writeLog(currentline);
                }
            }
        } catch (Exception e) { System.out.println(e); }
        over = true;
    }
    
    /**
     * writeLog simply writes to the logfile
     * 
     * @param output should be the head of the queue
     */
    private void writeLog (String output)
    {
        printwriter.println(output);
        printwriter.flush();
    }
    
    /**
     * getOver will return true if the run method is passed
     * 
     * @return  returns true if the run is passed
     */
    public boolean getOver (){
        return over;
    }
}
