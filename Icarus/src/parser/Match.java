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
import java.util.regex.*;

import vault.LogWriter;


/**
 * This class Matches all the pairs of starting blocks and ending blocks
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class Match {
	private StringBuilder builder;
	
	List<ArrayList<Integer>> list; /* list with all lists from Analyser */
	
	TreeMap<Integer,Integer>  ifmatching            = new TreeMap<>(); /* map with if - end_if pairs */
	TreeMap<Integer,Integer>  casematching          = new TreeMap<>(); /* map with case - end_case pairs */
	TreeMap<Integer,Integer>  varmatching           = new TreeMap<>(); /* map with var* - end_var pairs */
	TreeMap<Integer,Integer>  programmatching       = new TreeMap<>(); /* map with programm - end_programm pairs */
	TreeMap<Integer,Integer>  functionmatching      = new TreeMap<>(); /* map with function - end_function pairs */
	TreeMap<Integer,Integer>  functionblockmatching = new TreeMap<>(); /* map with funcion_block - end_function_block pairs */
	TreeMap<Integer,Integer>  formatching           = new TreeMap<>(); /* map with for - end_for pairs */
	TreeMap<Integer,Integer>  whilematching         = new TreeMap<>(); /* map with while - end_while pairs */
	TreeMap<Integer,Integer>  repeatmatching        = new TreeMap<>(); /* map with repeat - end_repeat pairs */

	Stack<Integer>  stack; /* a temp stack to find the pairs */

	ArrayList<Integer>  ifs;            /* list with all the ifs */
	ArrayList<Integer>  cases;          /* list with all the cases */
	ArrayList<Integer>  vars;           /* list with all the vars */
	ArrayList<Integer>  programs;       /* list with all the programs */
	ArrayList<Integer>  functions;      /* list with all the functions */
	ArrayList<Integer>  functionblocks; /* list with all the function_blocks */
	ArrayList<Integer>  fors;           /* list with all the fors */
	ArrayList<Integer>  whiles;         /* list with all the whiles */
	ArrayList<Integer>  repeats;        /* list with all the repeats */

	TreeMap<Integer,String>  ifendif;                       /* map with if index and if keyword */
	TreeMap<Integer,String>  caseendcase;                   /* map with case index and case keyword */
	TreeMap<Integer,String>  varendvar;                     /* map with var index and var keyword */
	TreeMap<Integer,String>  programendprogram;             /* map with program index and programm keyword */
	TreeMap<Integer,String>  functionendfunction;           /* map with function index and function keyword */
	TreeMap<Integer,String>  functionblockendfunctionblock; /* map with function_block index and function_block keyword */
	TreeMap<Integer,String>  forendfor;                     /* map with for index and for keyword */
	TreeMap<Integer,String>  whileendwhile;                 /* map with while index and while keyword */
	TreeMap<Integer,String>  repeatendrepeat;               /* map with repeat index and repeat keyword */

	ArrayList<Integer>  tmp; /* temp list to reorder the old lists */

	/* aditional structures to make the case handling more comfortable */
	TreeMap<Integer,Integer>                     allcases   = new TreeMap<>();
	TreeMap<Integer,TreeMap<Integer,Integer[]>>  casevalue  = new TreeMap<>();

	/* logger */
	private final LogWriter  log;
	private final String     mainkey  = "parser";
	private final String     subkey   = "Match";
	private final String     key      = mainkey+"-"+subkey;
    
	/**
	 * Matcher needs the Analyser object with the results from his analyses of
	 * the Second argument StringBuilder builder.
	 *
	 * @param anal the analyser results
	 * @param builder the while program code
	 */
	public
	Match(Analyser anal, StringBuilder builder, LogWriter log)
 	{
		this.log = log;
		this.builder = builder;

		log.log(key, 3, "Match ...");
		
		list = anal.give_me_all_the_lists();
		log.log(key, 4, "got all lists from the analyser.");

		try {
			gather_if_list();
			find_if_end_if_pairs();
	
			gather_case_list();
			find_case_end_case_pairs();
		
			find_all_cases();
			create_case_structure();
	
			gather_all_var_list();
			find_var_end_var_pairs();
	
			gather_all_program_list();
			find_program_end_program_pairs();

			gather_all_function_list();
			find_function_end_function_pairs();

			gather_all_function_block_list();
			find_function_block_end_function_block_pairs();

			gather_all_for_list();
			find_for_end_for_pairs();
	
			gather_all_while_list();
			find_while_end_while_pairs();

			gather_all_repeat_list();
			find_repeat_end_repeat_pairs();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		log.log(key, 3, "Match finisched.");
	}

	// if
	/**
	 * Get the list with the if blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 * 
	 * This function is private.
	 */
	private void
	gather_if_list()
	{
		log.log(key, 4, "gather_if_list called.");
		ifs = list.get(11); /* 11 is hardcoded here, should find a better solution */
		log.log(key, 4, "ifs: "+Arrays.toString(ifs.toArray()));
		
		ifendif = new TreeMap<>();
		for (Integer item : ifs) {
			if (builder.substring(item, item+2).equals("IF")) {
				ifendif.put(item, "IF");
			} else {
				ifendif.put(item, "END_IF");
			}
		}
	}

	/**
	 * Find the index pairs of the IF and END_IF blocks and puts them in a TreeMap with
	 * if index as key and end_if index as value.
	 *
	 * This function is private.
	 */
	private void
	find_if_end_if_pairs() throws Exception
	{
		log.log(key, 4, "find_if_end_if_pairs called.");
		tmp = new ArrayList<>();
		stack = new Stack<>();
		for (Integer item : new TreeSet<Integer>(ifendif.keySet())) {
			if ( ifendif.get(item).equals("IF") ) {
				stack.push(item);
				tmp.add(item);
			} else {
				ifmatching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			ifs = tmp;
			log.log(key, 4, "Found end Sorted IF-END_IF#: "+ifs.size());
		}
		else
		{
			log.log(key, 0, "IF not closed or END_IF to much!");
			throw new Exception("IF not closed or END_IF to much!");
		}
	}
    
	/**
	 * lookup function, throw in the index of a if and geht the index of a end_if
	 *
	 * @param a index of an if
	 * @return int
	 */
	public int
	get_end_if(int a)
	{
		log.log(key, 4, "get_end_if called.");
		int tmp = (int)ifmatching.get(a);
		log.log(key, 4, "end_if: "+tmp);
		return tmp;
	}
	
	/**
	 * getIfs returns a list of all the ifs found in the code.
	 * @return list of indexes of ifs
	 */
	public ArrayList<Integer>
	get_ifs()
	{
		log.log(key, 4, "get_ifs called.");
		log.log(key, 4, "ifs: "+Arrays.toString(ifs.toArray()));
		return ifs;
	}
	// case
	/**
	 * Get the list with the case blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 *
	 * This function is private.
	 */
	private void
	gather_case_list()
	{
		log.log(key, 4, "gather_case_list called.");
		cases = list.get(5);
		caseendcase = new TreeMap<>();
		for (Integer item : cases) {
			if (builder.substring(item, item+4).equals("CASE")) {
				caseendcase.put(item, "CASE");		
			} else {
				caseendcase.put(item, "END_CASE");
			}
		}
	}

	/**
	 * Find the index pairs of the CASE and END_CASE blocks and puts them in a TreeMap with
	 * if index as key and end_if index as value.
	 *
	 * This function is private.
	 */
	private void
	find_case_end_case_pairs() throws Exception
	{
		log.log(key, 4, "find_case_end_case_pairs called.");
		tmp = new ArrayList<>();
		stack = new Stack<>();
		for (Integer item : new TreeSet<>(caseendcase.keySet())) {
			if ( caseendcase.get(item).equals("CASE") ) {
				stack.push(item);
				tmp.add(item);
			} else {
				casematching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			cases = tmp;
			log.log(key, 4, "Found end Sorted CASE-END_CASE#: "+cases.size());
		}
		else
		{
			log.log(key, 0, "CASE not closed or END_CASE to much!");
			throw new Exception("CASE not closed or END_CASE to much!");
		}
	}

	/**
	 * findAllCases loops through all the case statements and finds all cases which match
	 * the given pattern.
	 * <p>
	 * the pattern is a regular expression.
	 */
	private void
	find_all_cases()
	{
		log.log(key, 4, "find_all_cases called.");
		String casedef = "((\\d+(,\\d+)*)+(...\\d+(,\\d+)*)*)+:";
		log.log(key, 4, "case definition: "+casedef);
		Pattern pattern = Pattern.compile(casedef);
		Matcher matcher = pattern.matcher(builder);
		int[] cases = new int[2];
		while(matcher.find()) {
			cases[0] = matcher.start();
			cases[1] = matcher.end();
			log.log(key, 4, "found: "+builder.substring(cases[0], cases[1])+" @ "+cases[0]+","+cases[1]);
			allcases.put(cases[0], cases[1]);
		}
		/* finds all things matching the regex, but this doesn't matter because he knows where the cases are  */
		log.log(key, 4, "cases found#: "+allcases.size());
	}
    
	/**
	 * evalCases evaluates all found cases und returns all the values they can have
	 * for example:
	 * 3,5...7:
	 * this will return a list with 3,5,6,7 in it.
	 * @param start startindex of a case
	 * @param stop stopindex of a case
	 * @return list with all case values
	 */
	private ArrayList<Integer>
	eval_cases(int start, int stop)
	{
		log.log(key, 4, "eval_cases called.");

		ArrayList<Integer>  intlist   = new ArrayList<>();

		String   tmp       = builder.substring(start, stop);
		boolean  series    = false;
		int      inttmp    = 0;
		int      intstart  = 0;

		log.log(key, 4, "case: "+tmp);

		for (char c : tmp.toCharArray()) {
			if (c == ',' || c == ':') {
				if (series) {
					for (; intstart <= inttmp; intstart++) {
						intlist.add(intstart);
					}
					series = false;
				} else {
					intlist.add(inttmp);
				}
			} else if (c == '.') {
				if (!series) {
					intstart = inttmp;
					series = true;
				}
			} else {
				inttmp *= 10;
				inttmp += Character.getNumericValue(c);
			}
		}

		log.log(key, 4, "evals to: "+Arrays.toString(intlist.toArray()));

		return intlist;
	}

	/**
	 * createCaseStructure will fill up the containers to hold the needed Information for the cases
	 * <p>
	 * This function is private.
	 */
	private void
	create_case_structure()
	{
		log.log(key, 4, "create_case_structure called.");

		int    caseposstart;
		int    caseposstop;
		int[]  startstop   = {-1, -1}; /* */
		int[]  casetoeval  = {-1, -1};

		TreeMap<Integer,Integer[]>  valuecase;

		for (Map.Entry<Integer,Integer> casestruct : casematching.entrySet()) {

			valuecase = new TreeMap<>();

			for (Map.Entry<Integer,Integer> evalcase : allcases.entrySet()) {

				caseposstart  = evalcase.getKey();
				caseposstop   = evalcase.getValue();

				if (caseposstart > casestruct.getKey() && caseposstart < casestruct.getValue()) {

					if (startstop[0] == -1) {
						startstop[0]   = caseposstop;
						casetoeval[0]  = caseposstart;
						casetoeval[1]  = caseposstop;
					} else {

						startstop[1]  = caseposstart;

						for (Integer val : eval_cases(casetoeval[0], casetoeval[1])) {
							valuecase.put(val, new Integer[] {startstop[0], startstop[1]});
						}

						startstop[0]   = caseposstop;
						casetoeval[0]  = caseposstart;
						casetoeval[1]  = caseposstop;
					}
				}
			}
			
			for (Integer val : eval_cases(casetoeval[0], casetoeval[1])) {

				valuecase.put(val, new Integer[] {startstop[0], casestruct.getValue()});
			}
			
			startstop[0] = -1;
			casevalue.put(casestruct.getKey(), valuecase);
		}
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
	 */
	public Integer[]
	get_case_coordinates(int caseopen, int value) throws Exception
	{
		log.log(key, 4, "get_case_coordinates called.");
		Integer[] tmp = casevalue.get(caseopen).get(value);
		if (tmp != null) {
			log.log(key, 4, "case coordinates: "+Arrays.toString(tmp));
			return tmp;
		} else {
			int elseincase = builder.toString().indexOf("ELSE", caseopen);
			if (elseincase < get_end_case(caseopen)) {
				return new Integer[] {elseincase+4, builder.toString().indexOf("END_CASE", caseopen) };
			} else {
				log.log(key, 4, "Case is fucked up");
				throw new Exception("Case is facked up");
			}
		}
	}

	/**
	 * lookup function, throw in the index of a case and geht the index of an end_case
	 *
	 * @param a index of a case
	 * @return int
	 */
	public int
	get_end_case(int a)
	{
		log.log(key, 4, "get_end_case called.");
		int tmp = (int)casematching.get(a);
		log.log(key, 4, "end_case: "+ tmp);
		return tmp;
	}

	/**
	 * getCases returns a list of all the cases found in the code.
	 * @return list of indexes of cases
	 */
	public ArrayList<Integer>
	get_cases()
	{
		log.log(key, 4, "get_cases called.");
		log.log(key, 4, "cases: "+Arrays.toString(cases.toArray()));
		return cases;
	}


	/**
	 * Get the lists with the var blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 * <p>
	 * This function is private.
	 */
	private void
	gather_all_var_list() throws Exception
	{
		log.log(key, 4, "gather_all_var_list called.");
		
		varendvar  = new TreeMap<>();
		vars       = new ArrayList<>();
		
		vars.addAll(list.get(3));
		vars.addAll(list.get(4));
		vars.addAll(list.get(9));
		vars.addAll(list.get(10));
		vars.addAll(list.get(12));
		
		for (Integer item : vars) {
			if (builder.substring(item, item+4).equals("VAR_")) {
				/* inner if block */
				if (builder.substring(item, item+9).equals("VAR_INPUT")) {
					log.log(key, 4, "found VAR_INPUT @ "+item);
					varendvar.put(item, "VAR_INPUT");
				} else if (builder.substring(item, item+10).equals("VAR_OUTPUT")) {
					log.log(key, 4, "found VAR_OUTPUT @ "+item);
					varendvar.put(item, "VAR_OUTPUT");
				} else if (builder.substring(item, item+10).equals("VAR_GLOBAL")) {
					log.log(key, 4, "found VAR_GLOBAL @ "+item);
					varendvar.put(item, "VAR_GLOBAL");
				} else if (builder.substring(item, item+10).equals("VAR_CONFIG")) {
					log.log(key, 4, "found VAR_CONFIG @ "+item);
					varendvar.put(item, "VAR_CONFIG");
				} else {
					/* throws, you realy fucked it up! */
					log.log(key, 0, "Your realy fucked it up! Undefined VAR block, ten char cut: \""+builder.substring(item, item+10)+"\" @ "+item);
					throw new Exception("Your realy fucked it up! Undefined VAR block, ten char cut: \""+builder.substring(item, item+10)+"\" @ "+item);
				}
				/* end inner if block */
			} else if (builder.substring(item, item+3).equals("VAR")) {
				log.log(key, 4, "found VAR @ "+item);
				varendvar.put(item, "VAR");
			} else {
				varendvar.put(item, "END_VAR");
			}
		}
		log.log(key, 4, "found VAR-END_VAR#: "+varendvar.size());
	}


	/**
	 * Find the index pairs of the VAR* and END_VAR blocks and puts them in a TreeMap with
	 * var* index as key and end_if index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_var_end_var_pairs() throws Exception
	{
		log.log(key, 4, "find_var_end_var_pairs called.");
		tmp    = new ArrayList<>();
		stack  = new Stack<>();
		for (Integer item : new TreeSet<>(varendvar.keySet())) {
			if (Pattern.matches( "VAR.*", varendvar.get(item))) {
				stack.push(item);
				tmp.add(item);
			} else {
				varmatching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			vars = tmp;
			log.log(key, 4, "Found Sorted VAR*-END_VAR#: "+vars.size());
		}
		else
		{
			log.log(key, 0, "VAR not closed or END_VAR to much!");
			throw new Exception("VAR not closed or END_VAR to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a var and geht the index of a end_var
	 *
	 * @param a index of a var
	 * @return int
	 */
	public int
	get_end_var(int a)
	{
		log.log(key, 4, "get_end_var called.");
		int tmp = (int)varmatching.get(a);
		log.log(key, 4, "end_var: "+tmp);
		return tmp;
	}

	/**
	 * getVars returns a list of all the Vars found in the code.
	 * @return list of indexes of vars
	 */
	public ArrayList<Integer>
	get_vars()
	{
		log.log(key, 4, "get_vars called.");
		log.log(key, 4, "vars: "+Arrays.toString(vars.toArray()));
		return vars;
	}

	/**
	 * getVarStart takes the index of the VAR and returns the String of the VAR_* declaration
	 * @param a index of a VAR
	 * @return name of VAR declaration type (VAR_INPUT, VAR_OUTPUT, ...)
	 */
	public String
	get_var_start(int a)
	{
		log.log(key, 4, "get_var_start called.");
		String tmp = varendvar.get(a);
		log.log(key, 4, "var open: "+tmp);
		return tmp;
	}

	/**
	 * Get the list with the program blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 * <p>
	 * This function is private.
	 */
	private void
	gather_all_program_list()
	{
		log.log(key, 4, "gather_all_program_list called.");
		programs = list.get(0);
		programendprogram = new TreeMap<>();
		for (Integer item : programs) {
			if (builder.substring(item, item+7).equals("PROGRAM")) {
				programendprogram.put(item, "PROGRAM");
			} else {
				programendprogram.put(item, "END_PROGRAM");
			}
		}
	}
    
	/**
	 * Find the index pairs of the PROGRAM and END_PROGRAM blocks and puts them in a TreeMap with
	 * PROGRAM index as key and end_PROGRAM index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_program_end_program_pairs() throws Exception
	{
		log.log(key, 4, "find_program_end_program_pairs called.");

		tmp    = new ArrayList<>();
		stack  = new Stack<>();

		for (Integer item : new TreeSet<>(programendprogram.keySet())) {
			if (programendprogram.get(item).equals("PROGRAM")) {
				stack.push(item);
				tmp.add(item);
			} else {
				programmatching.put(stack.pop(), item);
			}
		}
		if (stack.empty()) {
			programs = tmp;
			log.log(key, 4, "Found end Sorted PROGRAM-END_PROGRAM#: "+programs.size());
		}
		else
		{
			log.log(key, 0, "PROGRAM not closed or END_PROGRAM to much!");
			throw new Exception("PROGRAM not closed or END_PROGRAM to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a program and geht the index of a end_program
	 *
	 * @param a index of a program
	 * @return int
	 */
	public int
	get_end_program(int a)
	{
		log.log(key, 4, "get_end_program called.");
		int tmp = (int)programmatching.get(a);
		log.log(key, 4, "end_program: "+tmp);
		return tmp;
	}
    
	/**
	 * getPrograms returns a list of all the Programs found in the code.
	 * @return list of indexes of programs
	 */
	public ArrayList<Integer>
	get_programs()
	{
		log.log(key, 4, "get_programs called.");
		log.log(key, 4, "programs: "+Arrays.toString(programs.toArray()));
		return programs;
	}

	/**
	 * Get the list with the function blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 * <p>
	 * This function is private.
	 */
	private void
	gather_all_function_list()
	{
		log.log(key, 4, "gather_all_function_list called.");
		functions = list.get(1);
		functionendfunction = new TreeMap<>();
		for (Integer item : functions) {
			if (builder.substring(item, item+8).equals("FUNCTION")) {
				functionendfunction.put(item, "FUNCTION");
			} else {
				functionendfunction.put(item, "END_FUNCTION");
			}
		}
	}

	/**
	 * Find the index pairs of the FUNCTION and END_FUNCTION blocks and puts them in a TreeMap with
	 * FUNCTION index as key and end_FUNCTION index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_function_end_function_pairs() throws Exception
	{
		log.log(key, 4, "find_function_end_function_pairs called.");
		tmp = new ArrayList<>();
		stack = new Stack<>();
		for (Integer item : new TreeSet<>(functionendfunction.keySet())) {
			if(functionendfunction.get(item).equals("FUNCTION")) {
				stack.push(item);
				tmp.add(item);
			} else {
				functionmatching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			functions = tmp;
			log.log(key, 4, "Found end Sorted FUNCTION-END_FUNCTION#: "+functions.size());
		}
		else
		{
			log.log(key, 0, "FUNCTION not closed or END_FUNCTION to much!");
			throw new Exception("FUNCTION not closed or END_FUNCTION to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a function and geht the index of a end_function
	 *
	 * @param a index of a function
	 * @return int
	 */
	public int
	get_end_function(int a)
	{
		log.log(key, 4, "get_end_function called.");
		int tmp = (int)functionmatching.get(a);
		log.log(key, 4, "end_function: "+tmp);
		return tmp;
	}

	/**
	 * getFunctions returns a list of all the Functions found in the code.
	 * @return list of indexes of functions
	 */
	public ArrayList<Integer>
	get_functions()
	{
		log.log(key, 4, "get_functions called.");
		log.log(key, 4, "functions#: "+Arrays.toString(functions.toArray()));
		return functions;
	}

	/**
	 * Get the list with the function_block blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 * <p>
	 * This function is private.
	 */
	private void
	gather_all_function_block_list()
	{
		log.log(key, 4, "gather_all_function_block_list called.");
		functionblocks = list.get(2);
		functionblockendfunctionblock = new TreeMap<>();
		for (Integer item : functionblocks) {
			if (builder.substring(item, item+14).equals("FUNCTION_BLOCK")) {
				functionblockendfunctionblock.put(item, "FUNCTION_BLOCK");
			} else {
				functionblockendfunctionblock.put(item, "END_FUNCTION_BLOCK");
			}
		}
	}

	/**
	 * Find the index pairs of the FUNCTION_BLOCK and END_FUNCTION_BLOCK blocks and puts them in a TreeMap with
	 * FUNCTION_BLOCK index as key and END_FUNCTION_BLOCK index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_function_block_end_function_block_pairs() throws Exception
	{
		log.log(key, 4, "find_function_block_end_function_block_pairs called.");

		tmp    = new ArrayList<>();
		stack  = new Stack<>();
		
		for (Integer item : new TreeSet<>(functionblockendfunctionblock.keySet())) {
			if(functionblockendfunctionblock.get(item).equals("FUNCTION_BLOCK")) {
				stack.push(item);
				tmp.add(item);
			} else {
				functionblockmatching.put(stack.pop(), item);
			}
		} 
		if (stack.size() == 0) {
			functionblocks = tmp;
			log.log(key, 4, "Found end Sorted FUNCTION_BLOCK-END_FUNCTION_BLOCK#: "+functionblocks.size());
		}
		else
		{
			log.log(key, 0, "FUNCTION_BLOCK not closed or END_FUNCTION_BLOCK to much!");
			throw new Exception("FUNCTION_BLOCK not closed or END_FUNCTION_BLOCK to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a function_block and geht the index of a end_function_block
	 *
	 * @param a index of a function_block
	 * @return int
	 */
	public int
	get_end_function_block(int a)
	{
		log.log(key, 4, "get_end_function_block called.");
		int tmp = (int)functionblockmatching.get(a);
		log.log(key, 4, "end_function_block: "+tmp);
		return tmp;
	}

	/**
	 * getFunctionBlocks returns a list of all the FunctionBlocks found in the code.
	 * @return list of indexes of FunctionBlocks
	 */
	public ArrayList<Integer>
	get_function_blocks()
	{
		log.log(key, 4, "get_function_block called");
		log.log(key, 4, "function_block#: "+Arrays.toString(functionblocks.toArray()));
		return functionblocks;
	}

	/**
	 * Get the list with the for blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 *
	 * This function is private.
	 */
	private void
	gather_all_for_list()
	{
		log.log(key, 4, "gather_all_for_list called.");

		fors       = list.get(6);
		forendfor  = new TreeMap<>();

		for (Integer item : fors) {
			if (builder.substring(item, item+3).equals("FOR")) {
				forendfor.put(item, "FOR");
			} else {
				forendfor.put(item, "END_FOR");
			}
		}
	}

	/**
	 * Find the index pairs of the FOR and END_FOR blocks and puts them in a TreeMap with
	 * FOR index as key and END_FOR index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_for_end_for_pairs() throws Exception
	{
		log.log(key, 4, "find_for_end_for_pairs called.");

		tmp    = new ArrayList<>();
		stack  = new Stack<>();

		for (Integer item : new TreeSet<>(forendfor.keySet())) {
			if (forendfor.get(item).equals("FOR")) {
				stack.push(item);
				tmp.add(item);
			} else {
				formatching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			fors = tmp;
			log.log(key, 4, "Found end Sorted FOR-END_FOR#: "+fors.size());
		}
		else
		{
			log.log(key, 0, "FOR not closed or END_FOR to much!");
			throw new Exception("FOR not closed or END_FOR to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a for and geht the index of a end_for
	 *
	 * @param a index of a for
	 * @return int
	 */
	public int
	get_end_for(int a)
	{
		log.log(key, 4, "get_end_for called.");
		int tmp = (int)formatching.get(a);
		log.log(key, 4, "end_for: "+tmp);
		return tmp;
	}

	/**
	 * getFors returns a list of all the Fors found in the code.
	 * @return list of indexes of fors
	 */
	public ArrayList<Integer>
	get_fors()
	{
		log.log(key, 4, "get_fors called.");
		log.log(key, 4, "fors#: "+Arrays.toString(fors.toArray()));
		return fors;
	}

	/**
	 * Get the list with the while blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 *
	 * This function is private.
	 */
	private void
	gather_all_while_list()
	{
		log.log(key, 4, "gather_all_while_list called");
		whiles = list.get(7);
		whileendwhile = new TreeMap<>();
		for (Integer item : whiles) {
			if (builder.substring(item, item+5).equals("WHILE")) {
				whileendwhile.put(item, "WHILE");
			} else {
				whileendwhile.put(item, "END_WHILE");
			}
		}
	}

	/**
	 * Find the index pairs of the WHILE and END_WHILE blocks and puts them in a TreeMap with
	 * WHILE index as key and END_WHILE index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_while_end_while_pairs() throws Exception
	{
		log.log(key, 4, "find_while_end_while_pairs called");

		tmp    = new ArrayList<>();
		stack  = new Stack<>();

		for (Integer item : new TreeSet<>(whileendwhile.keySet())) {
			if (whileendwhile.get(item).equals("WHILE")) {
				stack.push(item);
				tmp.add(item);
			} else {
				whilematching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			whiles = tmp;
			log.log(key, 4, "Found end Sorted WHILE-END_WHILE#: "+whiles.size());
		}
		else
		{
			log.log(key, 0, "WHILE not closed or END_WHILE to much!");
			throw new Exception("WHILE not closed or END_WHILE to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a while and geht the index of a end_while
	 *
	 * @param a index of a while
	 * @return int
	 */
	public int
	get_end_while(int a)
	{
		log.log(key, 4, "get_end_while called.");
		int tmp = (int)whilematching.get(a);
		log.log(key, 4, "end_while: "+tmp);
		return tmp;
	}

	/**
	 * getWhiles returns a list of all the Whiles found in the code.
	 * @return list of indexes of whiles
	 */
	public ArrayList<Integer>
	get_whiles()
	{
		log.log(key, 4, "get_whiles called.");
		log.log(key, 4, "whiles#: "+Arrays.toString(whiles.toArray()));
		return whiles;
	}

	/**
	 * Get the list with the repeat blocks and put the indexes with the matching keyword in a
	 * TreeMap.
	 *
	 * This function is private.
	 */
	private void
	gather_all_repeat_list()
	{
		log.log(key, 4, "gather_all_repeat_list called.");

		repeats          = list.get(8);
		repeatendrepeat  = new TreeMap<>();

		for (Integer item : repeats) {
			if (builder.substring(item, item+5).equals("REPEAT")) {
				repeatendrepeat.put(item, "REPEAT");
			} else {
				repeatendrepeat.put(item, "END_REPEAT");
			}
		}
	}

	/**
	 * Find the index pairs of the REPEAT and END_REPEAT blocks and puts them in a TreeMap with
	 * REPEAT index as key and END_REPEAT index as value.
	 * <p>
	 * This function is private.
	 */
	private void
	find_repeat_end_repeat_pairs() throws Exception
	{
		log.log(key, 4, "find_repeat_end_repeat_pairs called.");

		tmp    = new ArrayList<>();
		stack  = new Stack<>();

		for (Integer item : new TreeSet<>(repeatendrepeat.keySet())) {
			if (repeatendrepeat.get(item).equals("REPEAT")) {
				stack.push(item);
				tmp.add(item);
			} else {
				repeatmatching.put(stack.pop(), item);
			}
		}
		if (stack.size() == 0) {
			repeats = tmp;
			log.log(key, 4, "Found end Sorted REPEAT-END_REPEAT#: "+repeats.size());
		}
		else
		{
			log.log(key, 0, "REPEAT not closed or END_REPEAT to much!");
			throw new Exception("REPEAT not closed or END_REPEAT to much!");
		}
	}

	/**
	 * lookup function, throw in the index of a repeat and geht the index of a end_repeat
	 *
	 * @param a index of a repeat
	 * @return int
	 */
	public int
	get_end_repeat(int a)
	{
		log.log(key, 4, "get_end_repeat called.");
		int tmp = (int)repeatmatching.get(a);
		log.log(key, 4, "end_repeat: "+tmp);
		return tmp;
	}

	/**
	 * getRepeats returns a list of all the Repeats found in the code.
	 * @return list of indexes of repeats
	 */
	public ArrayList<Integer>
	get_repeats()
	{
		log.log(key, 4, "get_repeats called.");
		log.log(key, 4, "repeats#: "+Arrays.toString(repeats.toArray()));
		return repeats;
	}
}

