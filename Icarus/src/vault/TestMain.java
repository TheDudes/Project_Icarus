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
        logwriter.log("Verbose5", 5, "Starting the testrun");
        logwriter.log("Verbose1", 1, "Should be printed out");
        logwriter.log("Verbose29", 29, "Nobody will ever read this");
    }
}
