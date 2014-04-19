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
import vault.*;
import parser.*;

/**
 *
 * @author apfel
 */
public class TestMain {
    
    
    
    public static void main(String[] args) throws UnknownHostException, Exception {
        
        String[] path = new String[2];

        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname = InetAddress.getLocalHost().getHostName();
        switch (hostname) {
            case "beelzebub":
                path[0] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/franzke_files/plc_prg.st";
                path[1] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/franzke_files/drilling_manager.st";
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
        ArrayList<Integer> liste = new ArrayList<>();
        
        
        /*
        * First Test
        */
        System.out.println("Print out all lists");
        List<ArrayList<Integer>> list = infR.giveMeAllTheLists();
        
        for (ArrayList<Integer> elem : list) {
            System.out.println("A List:");
            for (Integer item : elem) {
                System.out.println(item);
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test if functions
        */
        System.out.println("Test if functions");
        liste = infR.getIfs();
        if (liste.isEmpty()) {
            System.out.println("No ifs in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open IF: " + item);
                System.out.println("Close IF: " + infR.getEndIf(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test case functions
        */
        System.out.println("Test case functions");
        liste = infR.getCases();
        if (liste.isEmpty()) {
            System.out.println("No cases in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open case: " + item);
                System.out.println("Cases here ... ");
                System.out.println("Close case: " + infR.getEndCase(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test var functions
        */
        System.out.println("Test var functions");
        liste = infR.getVars();
        if (liste.isEmpty()) {
            System.out.println("No vars in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open VAR: " + item);
                System.out.println("Is a: " + infR.getVarStart(item));
                System.out.println("Close VAR: " + infR.getEndVar(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test progam functions
        */
        System.out.println("Test program functions");
        liste = infR.getPrograms();
        if (liste.isEmpty()) {
            System.out.println("No programs in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open Program: " + item);
                System.out.println("Close Program: " + infR.getEndProgram(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test function functions
        */
        System.out.println("Test function functions");
        liste = infR.getFunctions();
        if (liste.isEmpty()) {
            System.out.println("No functions in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open Function: " + item);
                System.out.println("Close Function: " + infR.getEndFunction(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test function block functions
        */
        System.out.println("Test function block functions");
        liste = infR.getFunctionBlocks();
        if (liste.isEmpty()) {
            System.out.println("No function blocks in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open Function Block: " + item);
                System.out.println("Close Function Block: " + infR.getEndFunctionBlock(item));
            }
        }
        System.out.println("-------------------------");

        
        /*
        * Test for functions
        */
        System.out.println("Test for functions");
        liste = infR.getFors();
        if (liste.isEmpty()) {
            System.out.println("No fors in here.");
        } else {
            for (Integer item : liste) {
                System.out.println("Open For: " + item);
                System.out.println("Close For: " + infR.getEndFor(item));
            }
        }
        System.out.println("-------------------------");
        
        
        /*
        * Test while functions
        */
        System.out.println("Test while functions");
        if (infR.getRepeats() == null) {
            System.out.println("No whiles in here.");
        } else {
            for (Integer item : infR.getWhiles()) {
                System.out.println("Open While: " + item);
                System.out.println("Close While: " + infR.getEndWhile(item));
            }
        }
        System.out.println("-------------------------");
        

        /*
        * Test repeat functions
        */
        System.out.println("Test repeat functions");
        if (infR.getRepeats() == null) {
            System.out.println("No repeats in here.");
        } else {
            for (Integer item : infR.getRepeats()) {
                System.out.println("Open Repeat: " + item);
                System.out.println("Close Repeat: " + infR.getEndRepeat(item));
            }
        }
        System.out.println("-------------------------");
        
        
        
    }
}
