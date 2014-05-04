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
import java.io.*;
import vault.*;

/**
 * This class is an Interfaceclass to all the functions which a potentionally needed
 * outside of my classcontruct.
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class InfoCollector {
	private final StringBuilder  allthecode;
	private final Analyser       analyser;
	private final Match          match;
	private final Symbols        symb;
	private final LogWriter      log;
	
	/* logger */
	private final String  mainkey = "parser";
	private final String  subkey = "InfoCollector";
	private final String  key = mainkey+"-"+subkey;
    
	/**
	 * InfoCollector is a Interfaceclass which has all the functions from a
	 * lot of different classes to make the life for others easyer
	 * <p>
	 * the Constructor simply takes a array of String which are filenames.
	 * Then different operations are run on these files to gether a lot of
	 * Information.
	 * @param files array of filenames to process
	 * @param log logwriter from above
	 * @throws java.io.FileNotFoundException
	 * @see MergeFiles
	 * @see Analyser
	 * @see Match
	 * @see Symbols
	 */
	public InfoCollector (String[] files, LogWriter log) throws FileNotFoundException, IOException, Exception {
		this.log = log;

		log.log(key, 1, "parsing file ...");
        
		log.log(key, 2, "merge all Files ...");
		allthecode = new MergeFiles(log, files).merge_all();
		log.log(key, 2, "Files merged.");
        
		log.log(key, 2, "analyse the code ...");
		analyser = new Analyser(allthecode, log);
		log.log(key, 2, "Analysed.");
        
		log.log(key, 2, "matching open and close tags ...");
		match = new Match(analyser, allthecode, log);
		log.log(key, 2, "Matched.");
        
		log.log(key, 2, "find all symbols in the code ...");
		symb = new Symbols(allthecode, match, log);
		log.log(key, 2, "Symbols stored.");
        
		log.log(key, 1, "file parsed.");
	}

	/* function from analyser class */
	/**
	 * give_me_all_the_lists is only used internaly, it returns a list of list with
	 * a lot of information.
	 * @return returns list of lists
	 * @see Analyser
	 * @see ArrayList
	 */
	public List<ArrayList<Integer>>
	give_me_all_the_lists()
	{
		log.log(key, 4, "give_meAllTheLists called.");
		return analyser.give_me_all_the_lists();
	}

	/* functions from match class */
	/**
	 * getEndIf takes the index of an IF and returns the index of the
	 * corresponding END_IF at the E
	 * @param a index of IF as int
	 * @return the index of the corresponding END_IF at the E
	 * @see Match
	 */
	public int
	get_end_if(int a)
	{
		log.log(key, 4, "getEndIf called.");
		return match.get_end_if(a);
	}
    
	/**
	 * getIfs returns a list with all the IF indexes
	 * @return list with all IF indexes
	 * @see Match
	 */
	public ArrayList<Integer>
	get_ifs()
	{
		log.log(key, 4, "getIfs called.");
		return match.get_ifs();
	}

	/**
	 * getCaseCoordinates takes the startindex of a case and the int value
	 * to evaluate and returns the startindex at [0] which is behind the collon (:)
	 * of a case and the endindex which is the startindex of the next case
	 * <p>
	 * var := 5;
	 * CASE (var) OF
	 * 1,2,3:
	 *      dosomething ...
	 * 4:
	 *      dosomething ...
	 * 5:
	 *      dosomething ....
	 * END_CASE
	 * <p>
	 * in this example it will return a startindex pointing at the char behind "5:"
	 * and a endindex pointing to the first E in END_CASE
	 * @param caseopen index of the C in the CASE
	 * @param value int value of the variable we run this CASE for
	 * @return an Integer array, [0] is the startindex and [1] is the endindex
	 * @see Match
	 */
	public Integer[]
	get_case_coordinates(int caseopen, int value)
	{
		log.log(key, 4, "getCaseCoordinates called.");
		return match.get_case_coordinates(caseopen, value);
	}

	/**
	 * getEndCase takes the index of the CASE and returns the index of the END_CASE
	 * @param a index of a CASE
	 * @return index of the END_CASE
	 * @see Match
	 */
	public int
	get_end_case(int a)
	{
		log.log(key, 4, "getEndCase called.");
		return match.get_end_case(a);
	}

	/**
	 * getCases returns a list with all the cases
	 * @return Integer list representing all the cases
	 * @see Match
	 */
	public ArrayList<Integer>
	get_cases()
	{
		log.log(key, 4, "getCases called.");
		return match.get_cases();
	}

	/**
	 * getEndVar takes the index of the VAR and returns the index of the END_VAR
	 * @param a index of a VAR
	 * @return index of the END_VAR
	 * @see Match
	 */
	public int
	get_end_var(int a)
	{
		log.log(key, 4, "getEndVar called.");
		return match.get_end_var(a);
	}

	/**
	 * getVars returns a list with all the vars
	 * @return Integer list representing all the vars
	 * @see Match
	 */
	public ArrayList<Integer>
	get_vars()
	{
		log.log(key, 4, "getVars called.");
		return match.get_vars();
	}

	/**
	 * getVarStart takes the index of the VAR and returns the String of the VAR_* declaration
	 * @param a index of a VAR
	 * @return name of VAR declaration type (VAR_INPUT, VAR_OUTPUT, ...)
	 * @see Match
	 */
	public String
	get_var_start(int a)
	{
		log.log(key, 4, "getVarStart called.");
		return match.get_var_start(a);
	}

	/**
	 * getEndProgram takes the index of the PROGRAM and returns the index of the END_PROGRAM
	 * @param a index of a PROGRAM
	 * @return index of the END_PROGRAM
	 * @see Match
	 */
	public int
	get_end_program(int a)
	{
		log.log(key, 4, "getEndProgram called.");
		return match.get_end_program(a);
	}

	/**
	 * getPrograms returns a list with all the programs
	 * @return Integer list representing all the programs
	 * @see Match
	 */
	public ArrayList<Integer>
	get_programs()
	{
		log.log(key, 4, "getPrograms called.");
		return match.get_programs();
	}

	/**
	 * getEndFunction takes the index of the FUNCTION and returns the index of the END_FUNCTION
	 * @param a index of a FUNCTION
	 * @return index of the END_FUNCTION
	 * @see Match
	 */
	public int
	get_end_function(int a)
	{
		log.log(key, 4, "getEndFunction called.");
		return match.get_end_function(a);
	}

	/**
	 * getFunctions returns a list with all the functions
	 * @return Integer list representing all the functions
	 * @see Match
	 */
	public ArrayList<Integer>
	get_functions()
	{
		log.log(key, 4, "getFunctions called.");
		return match.get_functions();
	}

	/**
	 * getEndFunctionBlock takes the index of the FUNCTION_BLOCK and returns the index of the END_FUNCTION_BLOCK
	 * @param a index of a FUNCTION_BLOCK
	 * @return index of the END_FUNCTION_BLOCK
	 * @see Match
	 */
	public int
	get_end_function_block(int a)
	{
		log.log(key, 4, "getEndFunctionBlock called.");
		return match.get_end_function_block(a);
	}

	/**
	 * getFunctionBlocks returns a list with all the functionsblocks
	 * @return Integer list representing all the functionblocks
	 * @see Match
	 */
	public ArrayList<Integer>
	get_function_blocks()
	{
		log.log(key, 4, "getFunctionBlock called.");
		return match.get_function_blocks();
	}

	/**
	 * getEndFor takes the index of the FOR and returns the index of the END_FOR
	 * @param a index of a FOR
	 * @return index of the END_FOR
	 * @see Match
	 */
	public int
	get_end_for(int a)
	{
		log.log(key, 4, "getEndFor called.");
		return match.get_end_for(a);
	}

	/**
	 * getFors returns a list with all the fors
	 * @return Integer list representing all the fors
	 * @see Match
	 */
	public ArrayList<Integer>
	get_fors()
	{
		log.log(key, 4, "getFors called.");
		return match.get_fors();
	}

	/**
	 * getEndWhile takes the index of the WHILE and returns the index of the END_WHILE
	 * @param a index of a WHILE
	 * @return index of the END_WHILE
	 * @see Match
	 */
	public int
	get_end_while(int a)
	{
		log.log(key, 4, "getEndWhile called.");
		return match.get_end_while(a);
	}

	/**
	 * getWhiles returns a list with all the whiles
	 * @return Integer list representing all the whiles
	 * @see Match
	 */
	public ArrayList<Integer>
	get_whiles()
	{
		log.log(key, 4, "getWhiles called.");
		return match.get_whiles();
	}

	/**
	 * getEndRepeat takes the index of the REPEAT and returns the index of the END_REPEAT
	 * @param a index of a REPEAT
	 * @return index of the END_REPEAT
	 * @see Match
	 */
	public int
	get_end_repeat(int a)
	{
		log.log(key, 4, "getEndRepeat called.");
		return match.get_end_repeat(a);
	}

	/**
	 * getRepeats returns a list with all the repeats
	 * @return Integer list representing all the repeats
	 * @see Match
	 */
	public ArrayList<Integer>
	get_repeats()
	{
		log.log(key, 4, "getRepeats called.");
		return match.get_repeats();
	}

	/* functions from symbols class */
	/**
	 * replaceVars replaces all variable names in a given string with its values
	 * @param input string with variable names in it
	 * @param context the context in which this variables should be
	 * @return the input string with all replaced variables
	 * @see Symbols
	 */
	public String
	replace_vars(String input, String context)
	{
		log.log(key, 4, "replaceVars called.");
		return symb.replace_vars(input, context);
	}
    
	/**
	 * setValue accepts whole variable lines like:
	 * var,var1,var2:=5;
	 * OR
	 * var3:=3.45;
	 * and sets the new value in the right containers depending on there context.
	 * @param input String with variable line.
	 * @param context String representing the context
	 * @throws java.lang.Exception
	 */
	public void
	set_value(String input, String context) throws Exception
	{
		log.log(key, 4, "setValue called.");
		symb.set_value(input, context);
	}

	/**
	 * addVar will add a variable to a context like setValue
	 * @param input String with variable line.
	 * @param context String representing the context
	 * @throws java.lang.Exception
	 */
	public void
	add_var(String input, String context) throws Exception
	{
		log.log(key, 4, "addVar called.");
		symb.add_var(input, context);
	}
    
	/**
	 * getAllTheCode will give you all the preprocessed code
	 * @return a StringBuilder with all the preprocessed code inside
	 */
	public StringBuilder
	get_all_the_code()
	{
		log.log(key, 4, "getAllTheCode called.");
		return allthecode;
	}
	
}
