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

/**
 * This class is a Interfaceclass to all the functions which a potentionally needed
 * outside of my classcontruct.
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class InfoCollector {
    private StringBuilder allthecode;
    private Analyser analyser;
    private Match match;
    private Symbols symb;

    /**
     * InfoCollector is a Interfaceclass which has all the functions from a
     * lot of different classes to make the life for others easyer
     * <p>
     * the Constructor simply takes a array of String which are filenames.
     * Then different operations are run on these files to gether a lot of
     * Information.
     * @param files array of filenames to process
     * @see MergeFiles
     * @see Analyser
     * @see Match
     * @see Symbols
     */
    public InfoCollector (String[] files) throws FileNotFoundException, IOException, Exception {
	System.out.println("Parser: InfoCollector: MergeFiles.mergeAll");
	allthecode = MergeFiles.mergeAll(files);
	System.out.println("Parser: InfoCollector: Analyser");
	analyser = new Analyser(allthecode);
	System.out.println("Parser: InfoCollector: Match");
	match = new Match(analyser, allthecode);
	System.out.println("Parser: InfoCollector: Symbols");
	symb = new Symbols(allthecode, match);
    }

    /* function from analyser class */
    /**
     * giveMeAllTheLists is only used internaly, it returns a list of list with
     * a lot of information.
     * @return returns list of lists
     * @see Analyser
     * @see ArrayList
     */
    public List<ArrayList<Integer>> giveMeAllTheLists() {
	return analyser.giveMeAllTheLists();
    }

    /* functions from match class */
    /**
     * getEndIf takes the index of an IF and returns the index of the
     * corresponding END_IF at the E
     * @param a index of IF as int
     * @return the index of the corresponding END_IF at the E
     * @see Match
     */
    public int getEndIf(int a) {
	return match.getEndIf(a);
    }
    
    /**
     * getIfs returns a list with all the IF indexes
     * @return list with all IF indexes
     * @see Match
     */
    public ArrayList<Integer> getIfs() {
	return match.getIfs();
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
    public Integer[] getCaseCoordinates(int caseopen, int value) {
	return match.getCaseCoordinates(caseopen, value);
    }

    /**
     * getEndCase takes the index of the CASE and returns the index of the END_CASE
     * @param a index of a CASE
     * @return index of the END_CASE
     * @see Match
     */
    public int getEndCase(int a) {
	return match.getEndCase(a);
    }

    /**
     * getCases returns a list with all the cases
     * @return Integer list representing all the cases
     * @see Match
     */
    public ArrayList<Integer> getCases() {
	return match.getCases();
    }

    /**
     * getEndVar takes the index of the VAR and returns the index of the END_VAR
     * @param a index of a VAR
     * @return index of the END_VAR
     * @see Match
     */
    public int getEndVar(int a) {
	return match.getEndVar(a);
    }

    /**
     * getVars returns a list with all the vars
     * @return Integer list representing all the vars
     * @see Match
     */
    public ArrayList<Integer> getVars() {
	return match.getVars();
    }

    /**
     * getVarStart takes the index of the VAR and returns the String of the VAR_* declaration
     * @param a index of a VAR
     * @return name of VAR declaration type (VAR_INPUT, VAR_OUTPUT, ...)
     * @see Match
     */
    public String getVarStart(int a) {
	return match.getVarStart(a);
    }

    /**
     * getEndProgram takes the index of the PROGRAM and returns the index of the END_PROGRAM
     * @param a index of a PROGRAM
     * @return index of the END_PROGRAM
     * @see Match
     */
    public int getEndProgram(int a) {
	return match.getEndProgram(a);
    }

    /**
     * getPrograms returns a list with all the programs
     * @return Integer list representing all the programs
     * @see Match
     */
    public ArrayList<Integer> getPrograms() {
	return match.getPrograms();
    }

    /**
     * getEndFunction takes the index of the FUNCTION and returns the index of the END_FUNCTION
     * @param a index of a FUNCTION
     * @return index of the END_FUNCTION
     * @see Match
     */
    public int getEndFunction(int a) {
	return match.getEndFunction(a);
    }

    /**
     * getFunctions returns a list with all the functions
     * @return Integer list representing all the functions
     * @see Match
     */
    public ArrayList<Integer> getFunctions() {
	return match.getFunctions();
    }

    /**
     * getEndFunctionBlock takes the index of the FUNCTION_BLOCK and returns the index of the END_FUNCTION_BLOCK
     * @param a index of a FUNCTION_BLOCK
     * @return index of the END_FUNCTION_BLOCK
     * @see Match
     */
    public int getEndFunctionBlock(int a) {
	return match.getEndFunctionBlock(a);
    }

    /**
     * getFunctionBlocks returns a list with all the functionsblocks
     * @return Integer list representing all the functionblocks
     * @see Match
     */
    public ArrayList<Integer> getFunctionBlocks() {
	return match.getFunctionBlocks();
    }

    /**
     * getEndFor takes the index of the FOR and returns the index of the END_FOR
     * @param a index of a FOR
     * @return index of the END_FOR
     * @see Match
     */
    public int getEndFor(int a) {
	return match.getEndFor(a);
    }

    /**
     * getFors returns a list with all the fors
     * @return Integer list representing all the fors
     * @see Match
     */
    public ArrayList<Integer> getFors() {
	return match.getFors();
    }

    /**
     * getEndWhile takes the index of the WHILE and returns the index of the END_WHILE
     * @param a index of a WHILE
     * @return index of the END_WHILE
     * @see Match
     */
    public int getEndWhile(int a) {
	return match.getEndWhile(a);
    }

    /**
     * getWhiles returns a list with all the whiles
     * @return Integer list representing all the whiles
     * @see Match
     */
    public ArrayList<Integer> getWhiles() {
	return match.getWhiles();
    }

    /**
     * getEndRepeat takes the index of the REPEAT and returns the index of the END_REPEAT
     * @param a index of a REPEAT
     * @return index of the END_REPEAT
     * @see Match
     */
    public int getEndRepeat(int a) {
	return match.getEndRepeat(a);
    }

    /**
     * getRepeats returns a list with all the repeats
     * @return Integer list representing all the repeats
     * @see Match
     */
    public ArrayList<Integer> getRepeats() {
	return match.getRepeats();
    }

    /* functions from symbols class */
    /**
     * replaceVars replaces all variable names in a given string with its values
     * @param input string with variable names in it
     * @param context the context in which this variables should be
     * @return the input string with all replaced variables
     * @see Symbols
     */
    public String replaceVars(String input, String context) {
	return symb.replaceVars(input, context);
    }
    
    /**
     * setValue accepts whole variable lines like:
     * var,var1,var2:=5;
     * OR
     * var3:=3.45;
     * and sets the new value in the right containers depending on there context.
     * @param input String with variable line.
     * @param context String representing the context
     */
    public void setValue(String input, String context) throws Exception {
	symb.setValue(input, context);
    }

    /**
     * getAllTheCode will give you all the preprocessed code
     * @return a StringBuilder with all the preprocessed code inside
     */
    public StringBuilder getAllTheCode() {
	return allthecode;
    }
}
