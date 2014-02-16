package parser;

import java.io.*;

public class MergeFiles {
    public static StringBuilder mergeAll(String[] args) throws FileNotFoundException, IOException {
	StringBuilder builder = new StringBuilder();
	FileReader r;
	boolean flag = false;
	for (String item : args) {
	    try { 
		r = new FileReader(item); 
		for(int i = r.read(); i != -1; i = r.read()) {
		    switch (i) {
		    case 10: //newline
		    case 13: //carriagereturn
			break;
		    case 32: //space
			break;
		    case 9: //tabulator
			break;
		    case 40: //parentesis open
			i = r.read();
			if ( i == 42 ) //asterisk
			    {
				flag = true;
			    }
			else
			    {
				builder.append('(');
				builder.append((char)i);
			    }
			break;
		    case 42: //asterisk
			i = r.read();
			if ( i == 41 ) //parentesis close
			    {
				flag = false;
			    }
			break;
		    default:
			if(!flag)
			    {
				builder.append((char)i);
				//System.out.println((char)i);
			    }
			break;
		    }
		}
	    }
	    catch (FileNotFoundException fnfe) {
		System.err.println("File not found: " + item);
	    }
	    catch (IOException ioe) {
		System.err.println("Can't read from file: " + item);
	    }
	}
	r = null;
	return builder;
    }
}
