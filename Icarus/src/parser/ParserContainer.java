/**
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

import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;

import config.Config_Reader;
import logger.*;

/**
 * This class is an Interfaceclass to all the functions which a potentionally needed
 * outside of my classcontruct.
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class ParserContainer {
	private final StringBuilder  allthecode;
	private final Analyser       analyser;
	private final Logger         log;

	/* logger */
	private final String  mainkey = "parser";
	private final String  subkey = "ParserContainer";
	private final String  key = " ["+mainkey+"-"+subkey+"] ";
        private final int     log_level = Integer.parseInt(System.getProperty("loglevel"));
        
	/**
	 * ParserContainer is a Interfaceclass which has all the functions from a
	 * lot of different classes to make the life for others easyer
	 * <p>
	 * the Constructor simply takes a array of String which are filenames.
	 * Then different operations are run on these files to gether a lot Offset_Handler
	 * Informations.
	 * @param configreader Config_Reader from above
	 * @param log logwriter from above
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 * @throws java.lang.Exception
	 * @see MergeFiles
	 * @see Analyser
	 * @see Config_Reader
	 */
	public ParserContainer (Config_Reader configreader, Logger log)
        throws FileNotFoundException, IOException, Exception
        {
		this.log = log;

		if(log_level >= 1) log.log(1, key, "parsing file ...", "\n");

		if(log_level >= 2) log.log(2, key, "merge all Files ...", "\n");
		allthecode = new MergeFiles(log, configreader.get_st_filepaths()).merge_all();
		if(log_level >= 2) log.log(2, key, "Files merged.", "\n");

		if(log_level >= 2) log.log(2, key, "analyse the code ...", "\n");
		analyser = new Analyser(allthecode, log);
		if(log_level >= 2) log.log(2, key, "Analysed.", "\n");

		if(log_level >= 1) log.log(1, key, "file parsed.", "\n");
	}

        /* local functions */
        
	/**
	 * getAllTheCode will give you all the preprocessed code
	 * @return a StringBuilder with all the preprocessed code inside
	 */
	public StringBuilder
	get_all_the_code()
	{
		if(log_level >= 4) log.log(4, key, "getAllTheCode called.", "\n");
		return allthecode;
	}

        
	/* functions from match class */
	/**
	 * getEndIf takes the index of an IF and returns the index of the
	 * corresponding END_IF at the E
	 * @param a index of IF as int
	 * @return the index of the corresponding END_IF at the E
	 */
	public int
	get_end_if(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndIf called.", "\n");
		return analyser.get_end_if(a);
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
	 * @throws Exception
	 */
	public Integer[]
	get_case_coordinates(int caseopen, int value) throws Exception
	{
		if(log_level >= 4) log.log(4, key, "getCaseCoordinates called.", "\n");
		return analyser.get_case_coordinates(caseopen, value);
	}

	/**
	 * getEndCase takes the index of the CASE and returns the index of the END_CASE
	 * @param a index of a CASE
	 * @return index of the END_CASE
	 */
	public int
	get_end_case(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndCase called.", "\n");
		return analyser.get_end_case(a);
	}

	/**
	 * getEndVar takes the index of the VAR and returns the index of the END_VAR
	 * @param a index of a VAR
	 * @return index of the END_VAR
	 */
	public int
	get_end_var(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndVar called.", "\n");
		return analyser.get_end_var(a);
	}

	/**
	 * getEndProgram takes the index of the PROGRAM and returns the index of the END_PROGRAM
	 * @param a index of a PROGRAM
	 * @return index of the END_PROGRAM
	 */
	public int
	get_end_program(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndProgram called.", "\n");
		return analyser.get_end_program(a);
	}

	/**
	 * getEndFunction takes the index of the FUNCTION and returns the index of the END_FUNCTION
	 * @param a index of a FUNCTION
	 * @return index of the END_FUNCTION
	 */
	public int
	get_end_function(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndFunction called.", "\n");
		return analyser.get_end_function(a);
	}

	/**
	 * getEndFunctionBlock takes the index of the FUNCTION_BLOCK and returns the index of the END_FUNCTION_BLOCK
	 * @param a index of a FUNCTION_BLOCK
	 * @return index of the END_FUNCTION_BLOCK
	 */
	public int
	get_end_function_block(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndFunctionBlock called.", "\n");
		return analyser.get_end_function_block(a);
	}

	/**
	 * getEndFor takes the index of the FOR and returns the index of the END_FOR
	 * @param a index of a FOR
	 * @return index of the END_FOR
	 */
	public int
	get_end_for(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndFor called.", "\n");
		return analyser.get_end_for(a);
	}

	/**
	 * getEndWhile takes the index of the WHILE and returns the index of the END_WHILE
	 * @param a index of a WHILE
	 * @return index of the END_WHILE
	 */
	public int
	get_end_while(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndWhile called.", "\n");
		return analyser.get_end_while(a);
	}

	/**
	 * getEndRepeat takes the index of the REPEAT and returns the index of the END_REPEAT
	 * @param a index of a REPEAT
	 * @return index of the END_REPEAT
	 */
	public int
	get_end_repeat(int a)
	{
		if(log_level >= 4) log.log(4, key, "getEndRepeat called.", "\n");
		return analyser.get_end_repeat(a);
	}

	/* functions from symbols class */
	/**
	 * replaceVars replaces all variable names in a given string with its values
	 * @param input string with variable names in it
	 * @param context the context in which this variables should be
	 * @return the input string with all replaced variables
	 */
        public String
	replace_vars(String input, String context)
	{
		if(log_level >= 4) log.log(4, key, "replaceVars called.", "\n");
		return analyser.replace_vars(input, context);
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
		if(log_level >= 4) log.log(4, key, "setValue called.", "\n");
		analyser.set_value(input, context);
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
		if(log_level >= 4) log.log(4, key, "addVar called.", "\n");
		analyser.add_var(input, context);
	}

	/**
	 * get_com_channel_queue returns the linkedblockingqueue for the IOInterface
	 * communikation.
	 * <p>
	 * the LinkedBlockingQueue with the type IO_Package
	 * @return LinkedBlockingQueue with IO_Package Type
	 * @see LinkedBlockingQueue
	 * @see IO_Package
	 */
	public LinkedBlockingQueue<IO_Package>
	get_com_channel_queue()
	{
		return analyser.get_com_channel_queue();
	}

        /**
         * update_device can update the informations acording to the byte address
         * @param byte_address the byte address as a String
         * @param value the byte to set
         */
	public synchronized void 
	update_device(String byte_address, byte value)
	{
		analyser.update_device(byte_address, value);
	}

        /**
         * call_function_or_program is the function which processes a function and his parameters
         * it will return the index of the FUNCTION in the code and sets the variable values
         * @param  function_call the function or program name and the parameters as array
         * @return the F in FUNCTION
         */
        public int
        call_function_or_program(String... function_call)
        {
                return analyser.call_function_or_program(function_call);
        }

        /**
         * reset_function will reset the function variables to there default values
         * @param context the contextname as String
         */
        public void
        reset_function(String context)
        {
                analyser.reset_function(context);
        }

        /**
         * get_timer will return the current STTimer
         * @param context the current context as String
         * @param var_name the variable name as String
         * @return the current timer
         */
        public STTimer
        get_timer(String context, String var_name)
        {
                return analyser.get_timer(context, var_name);
        }
}
