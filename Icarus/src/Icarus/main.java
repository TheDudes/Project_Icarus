package Icarus;

import parser.*;
import Ninti.*;
import linc.*;

import java.net.*;

public class main{
    public static void main(String[] args) throws Exception{
	
	String[] path = new String[1];

	/*
	 * To make testing easyer for all of us, add your hostname to this logic
	 */
	String hostname = InetAddress.getLocalHost().getHostName();
	switch (hostname) {
	case "beelzebub":
	    path[0] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/franzke_files/plc_prg.st";
	    break;
	/*
	case "yourhostname":
            path[0] = "/your/path/to/the/file";
            break;
	*/
	default:
	    System.out.println("Hey stupid, take a look in the code!");
	    System.exit(0);
	}
	System.out.println(path[0]);
        InfoCollector infR = new InfoCollector(path);
        System.out.println(infR.getAllTheCode());
    }
}
