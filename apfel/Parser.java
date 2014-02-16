package parser;

import java.io.*;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws FileNotFoundException, IOException {
	StringBuilder tmp = MergeFiles.mergeAll(args);
	//System.out.println(tmp.toString());
	//VALUE v = new VALUE("SINT");
	//VALUE.SINT i = (VALUE.SINT)v.val.getVALUE();
	//i.setValue(12);
	//System.out.println(i.getValue());
	Symbols s = new Symbols(tmp);
	System.out.println(" ");
	System.out.println(" ");
System.out.println(" ");
System.out.println(" ");
	for (Map.Entry<String, Object> item : s.map.entrySet()) {
	    System.out.print(item.getKey());
	    System.out.print(" : ");
	    System.out.println(item.getValue());
	}
    }
}
