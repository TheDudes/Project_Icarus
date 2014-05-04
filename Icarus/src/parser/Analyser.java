
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

import java.util.*;
import vault.*;

/**
 * This class locates all block beginnings and endings
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class Analyser {

	/* These list are holding pointers, these pointers are indexes of the programmstring. */
	private final ArrayList<Integer>  program_cursor         = new ArrayList<>();
	private final ArrayList<Integer>  function_cursor        = new ArrayList<>();
	private final ArrayList<Integer>  function_block_cursor  = new ArrayList<>();
	private final ArrayList<Integer>  global_cursor          = new ArrayList<>();
	private final ArrayList<Integer>  config_cursor          = new ArrayList<>();
	private final ArrayList<Integer>  var_all                = new ArrayList<>();
	private final ArrayList<Integer>  if_all                 = new ArrayList<>();
	private final ArrayList<Integer>  case_all               = new ArrayList<>();
	private final ArrayList<Integer>  for_all                = new ArrayList<>();
	private final ArrayList<Integer>  while_all              = new ArrayList<>();
	private final ArrayList<Integer>  repeat_all             = new ArrayList<>();
	//private final ArrayList<Integer> exit_all = new ArrayList<>();
	private final ArrayList<Integer>  var_input_all          = new ArrayList<>();
	private final ArrayList<Integer>  var_output_all         = new ArrayList<>();

	private final StringBuilder builder;

	/* iterration preperation for our program string  */
	private final int                       LISTCOUNT  = 13;
	private final List<ArrayList<Integer>>  blocks     = new ArrayList<>();
	private final String[][]                keywords   = {
		{"PROGRAM", "END_PROGRAM"},
		{"FUNCTION", "END_FNUNCTION"},
		{"FUNCTION_BLOCK", "END_FUNCTION_BLOCK"},
		{"VAR_GLOBAL", "END_VAR"},
		{"VAR_CONFIG", "END_VAR"},
		{"CASE", "END_CASE"},
		{"FOR", "END_FOR"},
		{"WHILE", "END_WHILE"},
		{"REPEAT", "END_REPEAT"},
		{"VAR_INPUT", "END_VAR"},
		{"VAR_OUTPUT", "END_VAR"},
		{"IF", "END_IF"},
		{"VAR", "END_VAR"}
	};

	/* logger */
	private final LogWriter  log;
	private final String     mainkey  = "parser";
	private final String     subkey   = "Analyser";
	private final String     key      = mainkey + "-" + subkey;

	private void
	find_all_keywords()
	{ /* think of the IF, its a special case */
        
		log.log(key, 3, "find_all_keywords called.");

		ArrayList<Integer>  block;
		int  i;

		log.log(key, 4, "String: "+builder.toString());
		
		for (i = 0; i < LISTCOUNT; i++) {
			block = blocks.get(i);
			String  start  = keywords[i][0];
			String  stop   = keywords[i][1];
			// int     endpointer;
			log.log(key, 4, "Keyword pair: "+start+","+stop+",count:"+i);
			if (i == 0) {
				boolean flag = true;
			        log.log(key, 4, "count: "+i);
				for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
					if (flag) {
						flag = false;
						block.add(pointer);
						log.log(key, 4, start+" index: "+(pointer));
					} else if (builder.charAt(pointer - 1) == '_') {
						block.add(pointer - 4);
						log.log(key, 4, stop+" index: "+(pointer-4));
					} else {
						block.add(pointer);
						log.log(key, 4, start+" index: "+pointer);
					}
				}
			} else if (i == 11) { 
				for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
					if (builder.charAt(pointer - 1) == '_') {
						block.add(pointer - 4);
						log.log(key, 4, stop+ " index "+(pointer-4));
					} else if (builder.charAt(pointer - 1) == 'E') {
                        
					} else {
						block.add(pointer);
						log.log(key, 4, start+ " index " + (pointer -4 ));
					}
				}
		        } else if (i == 12) {
				log.log(key, 4, "count: "+i);
				for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
					if (builder.charAt(pointer - 1) == '_') {
						block.add(pointer - 4);
						log.log(key, 4, stop+" index: "+(pointer-4));
					} else if (builder.charAt(pointer + 1) == '_') {
						
					} else {
						block.add(pointer);
						log.log(key, 4, start+" index: "+pointer);
					}
				}
		        } else if (i<11 && i > 0/*i == 0 || i == 1 || i == 5 || i == 6 || i == 7 || i == 8*/) {
				log.log(key, 4, "count: "+i);
				for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
					if (builder.charAt(pointer - 1) == '_') {
						block.add(pointer - 4);
						log.log(key, 4, stop+" index: "+(pointer-4));
					} else {
						block.add(pointer);
						log.log(key, 4, start+" index: "+pointer);
					}
				}
		        }
			
			log.log(key, 3, "Found " + block.size()/2 + " " + start + " and " + stop + " pairs");
			log.log(key, 4, "All " + start + " and " + stop + " Indexes: " + Arrays.toString(block.toArray()));
			log.log(key, 4, "Next -----------");
		}
	}
    
	/**
	 * giveMaAllTheLists returns a list of list filled with Integers,
	 * these Integers are the indexes of all the block beginnings and
	 * endings.
	 *
	 * @return List<ArrayList<Integer>> 
	 */
	public List<ArrayList<Integer>>
	give_me_all_the_lists()
	{
		log.log(key, 3, "give_me_all_the_lists called.");
		return blocks;
	}
	
	/**
	 * the Constructor takes a StringBuilder, which was prepared by MergeAllFiles
	 *
	 * @param builder StringBuilder prepared by MergeAllFiles
	 * @param log LogWriter from above
	 */
	public
	Analyser(StringBuilder builder, LogWriter log)
	{
		this.log      = log;
		this.builder  = builder;
		
		blocks.add(program_cursor); //0
		blocks.add(function_cursor); //1
		blocks.add(function_block_cursor); //2
		blocks.add(global_cursor); //3
		blocks.add(config_cursor); //4
		blocks.add(case_all); //5
		blocks.add(for_all); //6
		blocks.add(while_all); //7
		blocks.add(repeat_all); //8
		blocks.add(var_input_all); //9
		blocks.add(var_output_all); //10
		blocks.add(if_all); //11
		blocks.add(var_all); //12
        
		find_all_keywords();
	}
}

