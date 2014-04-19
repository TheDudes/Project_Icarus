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
        LogWriter logwriter = new LogWriter("/home/vault/programing/Logfile", 10);
        logwriter.log("bleefsdfub", 3, "Blsdub");
        logwriter.log("blufewb", 3, "Blusdb");
        logwriter.log("blffwub", 3, "Blwfwub");
        logwriter.log("bleesfub", 3, "fwefBlub");
        logwriter.log("bluasdfb", 3, "Blsdfub");
        logwriter.log("blufdasb", 3, "Blfewub");
        logwriter.log("basdflub", 3, "Bluwefwfeb");
    }
}
