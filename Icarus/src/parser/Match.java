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


/**
 * This class Matches all the pairs of starting blocks and ending blocks
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class Match {
    private Analyser anal;
    private StringBuilder builder;
    List<ArrayList<Integer>> list; // list with all lists from Analyser
    TreeMap<Integer,Integer> ifmatching = new TreeMap<>(); // map with if - end_if pairs
    TreeMap<Integer,Integer> casematching = new TreeMap<>(); // map with case - end_case pairs
    TreeMap<Integer,Integer> varmatching = new TreeMap<>(); // map with var* - end_var pairs
    TreeMap<Integer,Integer> programmatching = new TreeMap<>(); // map with programm - end_programm pairs
    TreeMap<Integer,Integer> functionmatching = new TreeMap<>(); // map with function - end_function pairs
    TreeMap<Integer,Integer> functionblockmatching = new TreeMap<>(); // map with funcion_block - end_function_block pairs
    TreeMap<Integer,Integer> formatching = new TreeMap<>(); // map with for - end_for pairs
    TreeMap<Integer,Integer> whilematching = new TreeMap<>(); // map with while - end_while pairs
    TreeMap<Integer,Integer> repeatmatching = new TreeMap<>(); // map with repeat - end_repeat pairs
    Stack<Integer> stack; // a temp stack to find the pairs
    ArrayList<Integer> ifs; // list with all the ifs
    ArrayList<Integer> cases; // list with all the cases
    ArrayList<Integer> vars; // list with all the vars
    ArrayList<Integer> programs; // list with all the programs
    ArrayList<Integer> functions; // list with all the functions
    ArrayList<Integer> functionblocks; // list with all the function_blocks
    ArrayList<Integer> fors; // list with all the fors
    ArrayList<Integer> whiles; // list with all the whiles
    ArrayList<Integer> repeats; // list with all the repeats
    TreeMap<Integer,String> ifendif; // map with if index and if keyword
    TreeMap<Integer,String> caseendcase; // map with case index and case keyword
    TreeMap<Integer,String> varendvar; // map with var index and var keyword
    TreeMap<Integer,String> programendprogram; // map with program index and programm keyword
    TreeMap<Integer,String> functionendfunction; // map with function index and function keyword
    TreeMap<Integer,String> functionblockendfunctionblock; // map with function_block index and function_block keyword
    TreeMap<Integer,String> forendfor; // map with for index and for keyword
    TreeMap<Integer,String> whileendwhile; // map with while index and while keyword
    TreeMap<Integer,String> repeatendrepeat; // map with repeat index and repeat keyword
    ArrayList<Integer> tmp; // temp list to reorder the old lists
    TreeMap<Integer,Integer> allcases = new TreeMap<>();
    TreeMap<Integer,TreeMap<Integer,Integer[]>> casevalue = new TreeMap<>();


    /**
     * Matcher needs the Analyser object with the results from his analyses of
     * the Second argument StringBuilder builder.
     *
     * @param anal the analyser results
     * @param builder the while program code
     */
    public Match(Analyser anal, StringBuilder builder) {
	this.anal = anal;
	this.builder = builder;
	list = anal.giveMeAllTheLists();
	
	getherIfList();
	findIfEndIfPairs();
	
	getherCaseList();
	findCaseEndCasePairs();
	findAllCasees();
	createCaseStructure();
	
	getherAllVarList();
	findVarEndVarPairs();
	
	getherAllProgramList();
	findProgramEndProgramPairs();

	getherAllFunctionList();
	findFunctionEndFunctionPairs();

	getherAllFunctionBlockList();
	findFunctionBlockEndFunctionBlockPairs();

	getherAllForList();
	findForEndForPairs();
	
	getherAllWhileList();
	findWhileEndWhilePairs();
    }

    // if
    /**
     * Get the list with the if blocks and put the indexes with the matching keyword in a
     * TreeMap.
     * 
     * This function is private.
     */
    private void getherIfList() {
	ifs = list.get(6);
	ifendif = new TreeMap<>();
	//String ifendif;
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
    private void findIfEndIfPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	//for (Integer item : ifendif.keySet()) {
	for (Integer item : new TreeSet<Integer>(ifendif.keySet())) {
	    if ( ifendif.get(item).equals("IF") ) {
		stack.push(item);
		tmp.add(item);
	    } else {
		ifmatching.put(stack.pop(), item);
	    } // will throw, if_not_closed_exception
	}
	ifs = tmp;
    }
    
    /**
     * lookup function, throw in the index of a if and geht the index of a end_if
     *
     * @param a index of an if
     * @return int
     */
    public int getEndIf(int a) {
	return (int)ifmatching.get(new Integer(a));
    }
    
    /**
     * getIfs returns a list of all the ifs found in the code.
     * @return list of indexes of ifs
     */
    public ArrayList<Integer> getIfs() {
	return ifs;
    }
    // case
    /**
     * Get the list with the case blocks and put the indexes with the matching keyword in a
     * TreeMap.
     *
     * This function is private.
     */
    private void getherCaseList() {
	cases = list.get(7);
	caseendcase = new TreeMap<>();
	//String ifendif;
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
    private void findCaseEndCasePairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(caseendcase.keySet())) {
	    if ( caseendcase.get(item).equals("CASE") ) {
		stack.push(item);
		tmp.add(item);
	    } else {
		casematching.put(stack.pop(), item);
	    } // will throw, case_not_closed_exception
	}
	cases = tmp;
    }

    /**
     * findAllCasees loops through all the case statements and finds all cases which match
     * the given pattern.
     * <p>
     * the pattern is a regular expression.
     */
    private void findAllCasees() {
	String casedef = "((\\d+(,\\d+)*)+(...\\d+(,\\d+)*)*)+:";
	Pattern pattern = Pattern.compile(casedef);
	Matcher matcher = pattern.matcher(builder);
	while(matcher.find()) {
	    allcases.put(matcher.start(), matcher.end());
	}
    }
    
    /**
     * evalCasees evaluates all found cases und returns all the values they can have
     * for example:
     * 3,5...7:
     * this will return a list with 3,5,6,7 in it.
     * @param start startindex of a case
     * @param stop stopindex of a case
     * @return list with all case values
     */
    private ArrayList<Integer> evalCasees(int start, int stop) {
	ArrayList<Integer> intlist = new ArrayList<>();
	String tmp = builder.substring(start, stop);
	boolean series = false;
	int inttmp = 0;
	int intstart = 0;
	for (char c : tmp.toCharArray()) {
	    if (c == ',' || c == ':') {
		if (series) {
		    for (; intstart <= inttmp; intstart++) {
			intlist.add(intstart);
		    }
		    series = false;
		    continue;
		} else {
		    intlist.add(inttmp);
		    continue;
		}
	    } else if (c == '.') {
		if (series) {
		    continue;
		} else {
		    intstart = inttmp;
		    series = true;
		    continue;
		}
	    } else {
		inttmp *= 10;
		inttmp += Character.getNumericValue(c);
		continue;
	    }
	}
	return intlist;
    }

    /**
     * createCaseStructure will fill up the containers to hold the needed Information for the cases
     * <p>
     * This function is private.
     */
    private void createCaseStructure() {
	int caseposstart;
	int caseposstop;
	int[] startstop = {-1, -1};
	int[] casetoeval = {-1, -1};
	TreeMap<Integer,Integer[]> valuecase;
	for (Map.Entry<Integer,Integer> casestruct : casematching.entrySet()) {
	    valuecase = new TreeMap<>();
	    for (Map.Entry<Integer,Integer> evalcase : allcases.entrySet()) {
		caseposstart = evalcase.getKey();
		caseposstop = evalcase.getValue();
		if (caseposstart > casestruct.getKey() && caseposstart < casestruct.getValue()) {
		    if (startstop[0] == -1) {
			startstop[0] = caseposstop;
			casetoeval[0] = caseposstart;
			casetoeval[1] = caseposstop;
		    } else {
			startstop[1] = caseposstart;
			for (Integer val : evalCasees(casetoeval[0], casetoeval[1])) {
			    valuecase.put(val, new Integer[] {startstop[0], startstop[1]});
			}
			startstop[0] = caseposstop;
			casetoeval[0] = caseposstart;
			casetoeval[1] = caseposstop;
		    }
		}
	    }
	    for (Integer val : evalCasees(casetoeval[0], casetoeval[1])) {
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
    public Integer[] getCaseCoordinates(int caseopen, int value) {
	return casevalue.get(caseopen).get(value);
    }

    /**
     * lookup function, throw in the index of a case and geht the index of an end_case
     *
     * @param a index of a case
     * @return int
     */
    public int getEndCase(int a) {
	return (int)casematching.get(new Integer(a));
    }

    /**
     * getCases returns a list of all the casees found in the code.
     * @return list of indexes of casees
     */
    public ArrayList<Integer> getCases() {
	return cases;
    }


    /**
     * Get the lists with the var blocks and put the indexes with the matching keyword in a
     * TreeMap.
     * <p>
     * This function is private.
     */
    private void getherAllVarList() {
	String var;
	vars = new ArrayList<>();
	vars.addAll(list.get(3));
	vars.addAll(list.get(4));
	vars.addAll(list.get(5));
	vars.addAll(list.get(12));
	vars.addAll(list.get(13));
	varendvar = new TreeMap<>();
	for (Integer item : vars) {
	    if (builder.substring(item, item+4).equals("VAR_")) {
		// inner if block
		if (builder.substring(item, item+9).equals("VAR_INPUT")) {
		    varendvar.put(item, "VAR_INPUT");
		} else if (builder.substring(item, item+10).equals("VAR_OUTPUT")) {
		    varendvar.put(item, "VAR_OUTPUT");
		} else if (builder.substring(item, item+10).equals("VAR_GLOBAL")) {
		    varendvar.put(item, "VAR_GLOBAL");
		} else if (builder.substring(item, item+10).equals("VAR_CONFIG")) {
		    varendvar.put(item, "VAR_CONFIG");
		} else {
		    //throws, you realy fucked it up!
		}
		// end inner if block
	    } else if (builder.substring(item, item+3).equals("VAR")) {
		varendvar.put(item, "VAR");
	    } else {
		varendvar.put(item, "END_VAR");
	    }
	}
    }


    /**
     * Find the index pairs of the VAR* and END_VAR blocks and puts them in a TreeMap with
     * var* index as key and end_if index as value.
     * <p>
     * This function is private.
     */
    private void findVarEndVarPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(varendvar.keySet())) {
	    if (Pattern.matches( "VAR.*", varendvar.get(item))) {
		stack.push(item);
		tmp.add(item);
	    } else {
		varmatching.put(stack.pop(), item);
	    } // will throw, var_not_closed_exception
	}
	vars = tmp;
    }

    /**
     * lookup function, throw in the index of a var and geht the index of a end_var
     *
     * @param a index of a var
     * @return int
     */
    public int getEndVar(int a) {
	return (int)varmatching.get(new Integer(a));
    }

    /**
     * getVars returns a list of all the Vars found in the code.
     * @return list of indexes of vars
     */
    public ArrayList<Integer> getVars() {
	return vars;
    }

    /**
     * getVarStart takes the index of the VAR and returns the String of the VAR_* declaration
     * @param a index of a VAR
     * @return name of VAR declaration type (VAR_INPUT, VAR_OUTPUT, ...)
     */
    public String getVarStart(int a) {
	return varendvar.get(a);
    }

    /**
     * Get the list with the program blocks and put the indexes with the matching keyword in a
     * TreeMap.
     * <p>
     * This function is private.
     */
    private void getherAllProgramList() {
	programs = list.get(0);
	programendprogram = new TreeMap<>();
	//String ifendif;
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
    private void findProgramEndProgramPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(programendprogram.keySet())) {
	    if (programendprogram.get(item).equals("PROGRAM")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		programmatching.put(stack.pop(), item);
	    } // will throw, program_not_closed_exception
	}
	programs = tmp;
    }

    /**
     * lookup function, throw in the index of a program and geht the index of a end_program
     *
     * @param a index of a program
     * @return int
     */
    public int getEndProgram(int a) {
	return (int)programmatching.get(new Integer(a));
    }
    
    /**
     * getPrograms returns a list of all the Programs found in the code.
     * @return list of indexes of programs
     */
    public ArrayList<Integer> getPrograms() {
	return programs;
    }

    /**
     * Get the list with the function blocks and put the indexes with the matching keyword in a
     * TreeMap.
     * <p>
     * This function is private.
     */
    private void getherAllFunctionList() {
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
    private void findFunctionEndFunctionPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(functionendfunction.keySet())) {
	    if(functionendfunction.get(item).equals("FUNCTION")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		functionmatching.put(stack.pop(), item);
	    }
	}
	functions = tmp;
    }

    /**
     * lookup function, throw in the index of a function and geht the index of a end_function
     *
     * @param a index of a function
     * @return int
     */
    public int getEndFunction(int a) {
	return (int)functionmatching.get(new Integer(a));
    }

    /**
     * getFunctions returns a list of all the Functions found in the code.
     * @return list of indexes of functions
     */
    public ArrayList<Integer> getFunctions() {
	return functions;
    }

    /**
     * Get the list with the function_block blocks and put the indexes with the matching keyword in a
     * TreeMap.
     * <p>
     * This function is private.
     */
    private void getherAllFunctionBlockList() {
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
    private void findFunctionBlockEndFunctionBlockPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(functionblockendfunctionblock.keySet())) {
	    if(functionblockendfunctionblock.get(item).equals("FUNCTION_BLOCK")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		functionblockmatching.put(stack.pop(), item);
	    }
	}
	functionblocks = tmp;
    }

    /**
     * lookup function, throw in the index of a function_block and geht the index of a end_function_block
     *
     * @param a index of a function_block
     * @return int
     */
    public int getEndFunctionBlock(int a) {
	return (int)functionblockmatching.get(new Integer(a));
    }

    /**
     * getFunctionBlocks returns a list of all the FunctionBlocks found in the code.
     * @return list of indexes of FunctionBlocks
     */
    public ArrayList<Integer> getFunctionBlocks() {
	return functionblocks;
    }

    /**
     * Get the list with the for blocks and put the indexes with the matching keyword in a
     * TreeMap.
     *
     * This function is private.
     */
    private void getherAllForList() {
	fors = list.get(8);
	forendfor = new TreeMap<>();
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
    private void findForEndForPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(forendfor.keySet())) {
	    if (forendfor.get(item).equals("FOR")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		formatching.put(stack.pop(), item);
	    }
	}
	fors = tmp;
    }

    /**
     * lookup function, throw in the index of a for and geht the index of a end_for
     *
     * @param a index of a for
     * @return int
     */
    public int getEndFor(int a) {
	return (int)formatching.get(new Integer(a));
    }

    /**
     * getFors returns a list of all the Fors found in the code.
     * @return list of indexes of fors
     */
    public ArrayList<Integer> getFors() {
	return fors;
    }

    /**
     * Get the list with the while blocks and put the indexes with the matching keyword in a
     * TreeMap.
     *
     * This function is private.
     */
    private void getherAllWhileList() {
	whiles = list.get(9);
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
    private void findWhileEndWhilePairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(whileendwhile.keySet())) {
	    if (whileendwhile.get(item).equals("WHILE")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		whilematching.put(stack.pop(), item);
	    }
	}
	whiles = tmp;
    }

    /**
     * lookup function, throw in the index of a while and geht the index of a end_while
     *
     * @param a index of a while
     * @return int
     */
    public int getEndWhile(int a) {
	return (int)whilematching.get(new Integer(a));
    }

    /**
     * getWhiles returns a list of all the Whiles found in the code.
     * @return list of indexes of whiles
     */
    public ArrayList<Integer> getWhiles() {
	return whiles;
    }

    /**
     * Get the list with the repeat blocks and put the indexes with the matching keyword in a
     * TreeMap.
     *
     * This function is private.
     */
    private void getherAllRepeatList() {
        System.out.println("Parser: Match: bevor get list");
	repeats = list.get(10);
	System.out.println("Parser: Match: after get list");
        repeatendrepeat = new TreeMap<>();
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
    private void findRepeatEndRepeatPairs() {
	tmp = new ArrayList<>();
	stack = new Stack<>();
	for (Integer item : new TreeSet<Integer>(repeatendrepeat.keySet())) {
	    if (repeatendrepeat.get(item).equals("REPEAT")) {
		stack.push(item);
		tmp.add(item);
	    } else {
		repeatmatching.put(stack.pop(), item);
	    }
	}
	repeats = tmp;
    }

    /**
     * lookup function, throw in the index of a repeat and geht the index of a end_repeat
     *
     * @param a index of a repeat
     * @return int
     */
    public int getEndRepeat(int a) {
	return (int)repeatmatching.get(new Integer(a));
    }

    /**
     * getRepeats returns a list of all the Repeats found in the code.
     * @return list of indexes of repeats
     */
    public ArrayList<Integer> getRepeats() {
	return repeats;
    }
}
