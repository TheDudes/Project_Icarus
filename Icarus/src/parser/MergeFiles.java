/*
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package parser;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import java.io.*;
import vault.*;
/**
 * This class merges all files together and removes the unneeded chars
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class MergeFiles {
    
    // logger
    //private static boolean logstat;
    private LogWriter log;
    private String mainkey = "parser";
    private String subkey = "MergeFiles";
    private String key = mainkey+"-"+subkey;
    
    
    public MergeFiles(LogWriter log) {
        this.log = log;
    }
    
    /**
     * mergeAll goes through all the files byte by byte and filters out the unneeded
     * charakters. The final String will be saved in a StringBuilder. StringBuilder should
     * be faster then String.
     *
     * @param args String[], file names+paths
     * @return StringBuilder
     * @throws FileNotFoundException, IOException
     */
    public StringBuilder mergeAll(String[] args) throws FileNotFoundException, IOException {
        //logstat = log.isInitialized();     // get status of the logger
        log.log(key, 3, "Inside mergeAll()");
	StringBuilder builder = new StringBuilder();
	FileReader r;
	boolean flag = false;
	for (String item : args) {
	    try { 
		r = new FileReader(item); 
		/*
		 * Loop through all bytes of "item" an check every byte
		 */
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
        log.log(key, 4, "Merged code: \n"+builder.toString());
	return builder;
    }
}
