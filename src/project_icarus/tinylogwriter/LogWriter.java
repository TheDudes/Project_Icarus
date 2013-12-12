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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogWriter will initialize all the needed things to run the thread.
 * It also offers some Functions to write to the queue/logfile.
 * 
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 0.8
 */
public class LogWriter {
    
    private int messagebuffer; 
    private String pathtologfile;
    private String nameoflogfile;
    private boolean append;
    private LinkedBlockingQueue<String> queue;
    private LogWriterWorker log;
    private Writer writer;
    private PrintWriter printwriter;
    private Thread t;
    
    /**
     * All parameters are needed to initialize the Object. In the Constructor
     * the most parameters will be checked.
     * 
     * @param pathtologfile path to the logfile
     * @param nameoflogfile name of the logfile
     * @param append        true for append to logfile, false to override
     * @param messagebuffer an integer value to specify the size of the queue
     */
    public LogWriter(String pathtologfile, String nameoflogfile, boolean append, int messagebuffer)
    {
        this.pathtologfile = pathtologfile;
        this.nameoflogfile = nameoflogfile;
        this.append = append;
        this.messagebuffer = messagebuffer;
        queue = new LinkedBlockingQueue<>(this.messagebuffer);
        
        try {
            if ("Windows".equals(System.getProperties().getProperty("os.name"))) {
                this.writer = new FileWriter(pathtologfile+"\\"+nameoflogfile, append);
            } else {
                this.writer = new FileWriter(pathtologfile+"/"+nameoflogfile, append);
            }
            this.printwriter = new PrintWriter(this.writer);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * This Function starts the Worker Thread, which will then start to write to the Logfile
     */
    public void startWorker(){
       this.log = new LogWriterWorker(queue, printwriter);
       t = new Thread(log);
       t.start();
    }
    
    /**
     * This Function will put a String as tail to the queue.
     * It will look like this:
     * (current time)[some key]{some messages}
     * 
     * @param key       a string to identify where a message comes from
     * @param message   the message as String
     */
    public void putOnQueue(String key, String message){
        queue.offer("("+new Date()+")"+"["+key+"]"+"{"+message+"}");
    }
  
    /**
     * stopWorker puts the "stopthat" line at the end of the queue
     * when the LogWriteWorker thread reaches this line the Thread will stop.
     * After that the file handle will be closed.
     */
    public void stopWorker(){
        queue.offer("stopthat");
        try {
            while(!log.getOver()){
                Thread.sleep(200);
            }
            printwriter.close();
            writer.close();
            
            //this.queue = null;
            
        } catch (InterruptedException | IOException e) {
            System.out.println(e);
        }
    }
}

