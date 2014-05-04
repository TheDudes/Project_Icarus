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

import java.io.*;
import java.util.Arrays;

import vault.*;
/**
 * This class merges all files together and removes the unneeded chars
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class MergeFiles {

	private final String[] args;
	
	// logger
	//private static boolean logstat;
	private final LogWriter log;
	private final String mainkey = "parser";
	private final String subkey = "MergeFiles";
	private final String key = mainkey+"-"+subkey;
    
    
	public
	MergeFiles(LogWriter log, String[] args)
	{
		this.log = log;
		this.args = args;
	}
	
	/**
	 * merge_all goes through all the files byte by byte and filters out the unneeded
	 * charakters. The final String will be saved in a StringBuilder. StringBuilder should
	 * be faster then String.
	 *
	 * @return StringBuilder
	 * @throws FileNotFoundException, IOException
	 */
	public StringBuilder
	merge_all() throws FileNotFoundException, IOException, Exception
	{
		StringBuilder  builder     = new StringBuilder();
		FileReader     filereader;
		boolean        flag        = false;
		log.log(key, 3, "Inside merge_all()");
		if (args.length <= 0) {
			log.log(key, 0, "No files ... array is empty: "+Arrays.toString(args));
			throw new Exception("No files ....");
		} else {
			filereader = new FileReader(args[0]);
		} /* dirty, but it fixes a warning, will think about it ... */
		
		for (String item : args) {
			try { 
				filereader = new FileReader(item); 
				/*
				 * Loop through all bytes of "item" and check every byte
				 */
				for(int i = filereader.read(); i != -1; i = filereader.read()) {
					switch (i) {
					case 10: //newline
					case 13: //carriagereturn
						break;
					case 32: //space
						break;
					case 9: //tabulator
						break;
					case 40: //parentesis open
						i = filereader.read();
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
						i = filereader.read();
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
				log.log(key, 0, "File not found: "+ item);
				System.err.println("File not found: " + item);
			}
			catch (IOException ioe) {
				log.log(key, 0, "Can't read from file: "+item);
				System.err.println("Can't read from file: " + item);
			}
		}
		
		filereader.close();
		
		log.log(key, 3, "Merged code: "+builder.toString());
		return builder;
	}
}
