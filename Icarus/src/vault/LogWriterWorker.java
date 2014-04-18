/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vault;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vault
 */
public class LogWriterWorker implements Runnable{
    
    private LinkedBlockingQueue<String> lbq;
    
    //needs to be implemented//
    private String filePath = "/home/vault/programing/NetBeansProjects/Project_Icarus/Icarus/src/vault/LogFile.txt";
    
    public LogWriterWorker(LinkedBlockingQueue lbq){
        this.lbq = lbq;
    }
    
    @Override
    public void run() {
        try (Writer fWriter = new BufferedWriter(new FileWriter(filePath, true))) {
            while (true) {
                fWriter.write(lbq.take());
                fWriter.flush();
            }
        } catch (IOException e) { 
            System.out.println("Error while writing File!");
        } catch (InterruptedException ex) {
            Logger.getLogger(LogWriterWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }
}
