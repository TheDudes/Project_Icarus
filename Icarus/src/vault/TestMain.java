/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vault;


/**
 *
 * @author vault
 */
public class TestMain {
        
    public static void main(String[] args) {
        LogWriter logwriter = new LogWriter();
        int peak = 0;
        for(int i = 0; i < 10; i++){
            logwriter.log("LogLine "+i, 1, "OMAGOD"); 
            if(peak < logwriter.queueSize){
                peak = logwriter.queueSize;
            }
        }
        System.out.println(peak);
        
    }
}
