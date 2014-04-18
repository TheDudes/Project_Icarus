/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vault;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 * @author vault
 */
public class LogWriter {
    
    private LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<>(1024);
    private String host; //Hostname
    private int verboseLevel = 5; //Needs to be implemented
    private Date date; 
    private SimpleDateFormat sdf;    
    
    //temporär//
    public int queueSize;
    //temporär//
    
    public LogWriter(){
        new Thread(new LogWriterWorker(lbq)).start();
        getHostname();
    }
    //Gets the Hostname
    private void getHostname() {
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            host = "localhost";
        } 
    }
    //Generates the Timestamp
    private String getTimestamp(){ 
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        return sdf.format(date);
    }
    //Writes to the LogFile
    public void log(String key, int verboseLevel, String logMessage) {
        if(verboseLevel <= this.verboseLevel){
            queueSize = lbq.size();
            String logLine = queueSize+" ["+getTimestamp()+"]"+" ["+
                                host+"] "+"["+
                                key+"]"+": "+
                                logMessage+"\n";
            lbq.offer(logLine); 
        } 
    }
    
}
