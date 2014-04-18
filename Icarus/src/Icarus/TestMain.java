/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Icarus;

import Ninti.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import linc.*;
import parser.*;

/**
 *
 * @author apfel
 */
public class TestMain {
    
    
    
    public static void main(String[] args) throws UnknownHostException, Exception {
        
        String[] path = new String[1];

        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname = InetAddress.getLocalHost().getHostName();
        switch (hostname) {
            case "beelzebub":
                path[0] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/franzke_files/plc_prg.st";
                break;
            case "d4ryus":
                path[0] = "/home/d4ryus/coding/Project_Icarus/franzke_files/plc_prg.st";
                break;
            /*
             case "yourhostname":
             path[0] = "/your/path/to/the/file";
             break;
             */
            default:
                System.out.println("Hey stupid(faggot), take a look in the code!");
                System.exit(0);
        }
        System.out.println(path[0]);
        InfoCollector infR = new InfoCollector(path);
        StringBuilder code = infR.getAllTheCode();
        System.out.println(code);
        
        /*
        * First Test
        */
        System.out.println("First Test:");
        List<ArrayList<Integer>> list = infR.giveMeAllTheLists();
        
        for (ArrayList<Integer> elem : list) {
            System.out.println("A List:");
            for (Integer item : elem) {
                System.out.println(item);
            }
        }
        
        
        /*
        * Test if functions
        */
        System.out.println("Test if functions");
        for (Integer item : infR.getIfs()) {
            System.out.println("Open IF: "+item);
            System.out.println("Close IF: "+infR.getEndIf(item));
        }
 
        
        /*
        * Test case functions
        */
        System.out.println("Test case functions");
        for (Integer item : infR.getCases()) {
            System.out.println("Open case: "+item);
            System.out.println("Cases here ... " );
            System.out.println("Close case: "+infR.getEndCase(item));
        }

        
        /*
        * Test var functions
        */
        System.out.println("Test var functions");
        for (Integer item : infR.getVars()) {
            System.out.println("Open VAR: "+item);
            System.out.println("Is a: "+infR.getVarStart(item));
            System.out.println("Close VAR: "+infR.getEndVar(item));
        }
        
        
        /*
        * Test progam functions
        */
        System.out.println("Test program functions");
        for (Integer item : infR.getPrograms()) {
            System.out.println("Open Program: "+item);
            System.out.println("Close Program: "+infR.getEndProgram(item));
        }
        
    }
}
