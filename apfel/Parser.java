package parser;

import java.io.*;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws FileNotFoundException, IOException {
	//	StringBuilder tmp = MergeFiles.mergeAll(args);
	//System.out.println(tmp.toString());
	//VALUE v = new VALUE("SINT");
	//VALUE.SINT i = (VALUE.SINT)v.val.getVALUE();
	//i.setValue(12);
	//System.out.println(i.getValue());
	//      Symbols s = new Symbols(tmp);
	/*
	for (Map.Entry<String, Object> item : s.map.entrySet()) {
	    System.out.print(item.getKey());
	    System.out.print(" : ");
	    System.out.println(item.getValue());
	    }*/
	StringBuilder tmp = new StringBuilder("DasIstEinTest");
	int i = tmp.indexOf("Ist");
	System.out.println(tmp.toString());
	tmp.delete(3, 6);
	System.out.println(tmp.toString());
	tmp.insert(3, 132442);
	System.out.println(tmp.toString());
	System.out.println(i);
    }
}
