
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
import vault.Log;
import vault.LogWriter;

/**
 * This class locates all block beginnings and endings
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class Analyser {

    private ArrayList<Integer> program_cursor = new ArrayList<>();
    private ArrayList<Integer> function_cursor = new ArrayList<>();
    private ArrayList<Integer> function_block_cursor = new ArrayList<>();
    private ArrayList<Integer> global_cursor = new ArrayList<>();
    private ArrayList<Integer> config_cursor = new ArrayList<>();
    private ArrayList<Integer> var_all = new ArrayList<>();
    private ArrayList<Integer> if_all = new ArrayList<>();
    private ArrayList<Integer> case_all = new ArrayList<>();
    private ArrayList<Integer> for_all = new ArrayList<>();
    private ArrayList<Integer> while_all = new ArrayList<>();
    private ArrayList<Integer> repeat_all = new ArrayList<>();
    private ArrayList<Integer> exit_all = new ArrayList<>();
    private ArrayList<Integer> var_input_all = new ArrayList<>();
    private ArrayList<Integer> var_output_all = new ArrayList<>();

    private final StringBuilder builder;

    private final int LISTCOUNT = 13;
    private List<ArrayList<Integer>> blocks = new ArrayList<>();
    private final String[][] keywords = {
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

    // logger
    //private boolean logstat;
    private LogWriter log;
    private String mainkey = "parser";
    private String subkey = "Analyser";
    private String key = mainkey + "-" + subkey;

    private void findAllKeywords() { // think of the IF, its a special case
        
            log.log(key, 3, "findAllKeywords called.");
        
        int i;
        for (i = 0; i < LISTCOUNT; i++) {
            ArrayList<Integer> block = blocks.get(i);
            String start = keywords[i][0];
            String stop = keywords[i][1];
            int endpointer;
            if (i < 11) {
                for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer + stop.length())) {
                    block.add(new Integer(pointer));
                    endpointer = builder.indexOf(stop, pointer);
                    block.add(new Integer(endpointer));
                }
                //blocks.add(i, block);
                
                    log.log(key, 3, "Found " + block.size() + " " + start + " and " + stop);
                
                
                    log.log(key, 4, start + " and " + stop + " Indexes: " + Arrays.toString(block.toArray()));
                
            } else if (i == 11) { // normaly not needed, should work anyway
                block = blocks.get(i);
                start = keywords[i][0];
                stop = keywords[i][1];
                for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
                    if (builder.charAt(pointer - 1) == '_') {
                        block.add(new Integer(pointer - 4));
                    } else if (builder.charAt(pointer - 1) == 'E') {
                        
                    } else {
                        block.add(new Integer(pointer));
                    }
                }
                //blocks.add(i, block);
                
                    log.log(key, 3, "Found " + block.size() + " " + start + " and " + stop);
                
                
                    log.log(key, 4, start + " and " + stop + " Indexes: " + Arrays.toString(block.toArray()));
                
            } else if (i == 12) {
                block = blocks.get(i);
                start = keywords[i][0];
                stop = keywords[i][1];
                for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer + start.length())) {
                    if (builder.charAt(pointer - 1) == '_') {
                        block.add(new Integer(pointer - 4));
                    } else if (builder.charAt(pointer + 1) == '_') {
                        continue;
                    } else {
                        block.add(new Integer(pointer));
                    }
                }
                //blocks.add(i, block);
                
                    log.log(key, 3, "Found " + block.size() + " " + start + " and " + stop);
                
                
                    log.log(key, 4, start + " and " + stop + " Indexes: " + Arrays.toString(block.toArray()));
                
            }
        }
    }
    
    /**
     * findAllPrograms detects all program blocks and writes all startindexes of those
     * PROGRAM and END_PROGRAM statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*private void findAllPrograms() {
        if (logstat) { log.log(key, 3, "findAllPrograms called."); }
	String start = "PROGRAM";
	String stop = "END_PROGRAM";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    program_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    program_cursor.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Program Indexes: "+Arrays.toString(program_cursor.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+program_cursor.size()/2+" Programs."); }
    }
    */
    /**
     * findAllFunctions detects all function blocks and writes all startindexes of those
     * FUNCTION and END_FUNCTION statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*private void findAllFunctions() {
        if (logstat) { log.log(key, 3, "findAllFunctions called."); }
	String start = "FUNCTION";
	String stop = "END_FUNCTION";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    if (builder.charAt(pointer+start.length()) == '_')
		{
		    endpointer = builder.indexOf(stop, pointer);
		    continue;
		}
	    endpointer = builder.indexOf(stop, pointer);
      	    function_cursor.add(new Integer(pointer));
	    function_cursor.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Function Indexes: "+Arrays.toString(function_cursor.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+function_block_cursor.size()/2+" Functions."); }
    }
    */
     /**
     * findAllFunctionBlocks detects all function_block blocks and writes all startindexes of those
     * FUNCTION_BLOCK and END_FUNCTION_BLOCK statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllFunctionBlocks() {
        if (logstat) { log.log(key, 3, "findAllFunctionBlocks called."); }
	String start = "FUNCTION_BLOCK";
	String stop = "END_FUNCTION_BLOCK";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    function_block_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    function_block_cursor.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Function Block Indexes: "+Arrays.toString(function_block_cursor.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+function_block_cursor.size()/2+" Function Blocks"); }
    }
    */
     /**
     * findAllPrograms detects all var_global blocks and writes all startindexes of those
     * VAR_GLOBAL and END_VAR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllGlobals() {
        if (logstat) { log.log(key, 3, "findAllGlobals called."); }
	String start = "VAR_GLOBAL";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    global_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    global_cursor.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Var Global Indexes: "+Arrays.toString(global_cursor.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+global_cursor.size()/2+" Var Global"); }
    }
    */
    /**
     * findAllConfigs detects all var_config blocks and writes all startindexes of those
     * VAR_CONFIG and END_VAR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllConfigs() {
        if (logstat) { log.log(key, 3, "findAllConfigs called."); }
	String start = "VAR_CONFIG";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    config_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    config_cursor.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Var Config Indexes: "+Arrays.toString(config_cursor.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+config_cursor.size()/2+" Var Config"); }
    }
    */
    /**
     * findAllVars detects all var blocks and writes all startindexes of those
     * VAR and END_VAR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllVars() {
        if (logstat) { log.log(key, 3, "findAllVars called."); }
	String start = "VAR";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    if (builder.charAt(pointer+start.length()) == '_') {
		endpointer = builder.indexOf(stop, pointer);
		continue;
	    }
	    endpointer = builder.indexOf(stop, pointer);
	    var_all.add(new Integer(pointer));
	    var_all.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Var Indexes: "+Arrays.toString(var_all.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+var_all.size()/2+" Var"); }
    }
    */
    /**
     * findAllIfs detects all if blocks and writes all startindexes of those
     * IF and END_IF statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllIfs() {
        if (logstat) { log.log(key, 3, "findAllIfs called."); }
	String start = "IF";
	String stop = "END_IF";
	System.out.println("Parser: Analyser: findAllIfs(): bevor for loop");
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, pointer+start.length())) {
	    System.out.println("Parser: Analyser: findAllIfs(): in for loop pointer: "+pointer);
	    if (builder.charAt(pointer-1) == '_') {
		if_all.add(new Integer(pointer-4));
	    } else if (builder.charAt(pointer-1) == 'E') {

	    } else {
		if_all.add(new Integer(pointer));
	    }
	    //endpointer = builder.indexOf(stop, pointer);
	    //if_all.add(new Integer(pointer));
	    //if_all.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "If Indexes: "+Arrays.toString(if_all.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+if_all.size()/2+" If"); }
    }
    */
    /**
     * findAllCases detects all case blocks and writes all startindexes of those
     * CASE and END_CASE statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllCases() {
        if (logstat) { log.log(key, 3, "findAllCases called."); }
	String start = "CASE";
	String stop = "END_CASE";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    case_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    case_all.add(new Integer(endpointer));
	}
        if (logstat) { log.log(key, 4, "Case Indexes: "+Arrays.toString(case_all.toArray())); }
        if (logstat) { log.log(key, 3, "Found "+case_all.size()/2+" If"); }
    }
    */
    /**
     * findAllFors detects all for blocks and writes all startindexes of those
     * FOR and END_FOR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllFors() {
	String start = "FOR";
	String stop = "END_FOR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    for_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    for_all.add(new Integer(endpointer));
	}
    }
    */
    /**
     * findAllWhiles detects all while blocks and writes all startindexes of those
     * WHILE and END_WHILE statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllWhiles() {
	String start = "WHILE";
	String stop = "END_WHILE";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    while_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    while_all.add(new Integer(endpointer));
	}
    }
    */
    /**
     * findAllRepeats detects all repeat blocks and writes all startindexes of those
     * REPEAT and END_REPEAT statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllRepeats() {
        System.out.println("Parser: Analyser: findAllRepeats: beginning");
	String start = "REPEAT";
	String stop = "END_REPEAT";
	int endpointer;
        System.out.println("Parser: Analyser: findAllRepeats: bevor loop");
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
            System.out.println("Parser: Analyser: findAllRepeats: inside loop");
	    repeat_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    repeat_all.add(new Integer(endpointer));
	}
        System.out.println("Parser: Analyser: findAllRepeats: end");
    }
    */
    /**
     * findAllVarInputs detects all var blocks and writes all startindexes of those
     * VAR_INPUT and END_VAR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllVarInputs() {
	String start = "VAR_INPUT";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    var_input_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    var_input_all.add(new Integer(endpointer));
	}
    }
    */
    /**
     * findAllVarOutputs detects all var blocks and writes all startindexes of those
     * VAR_OUTPUT and END_VAR statements in an ArrayList
     * <p>
     * This function is private.
     */
    /*
    private void findAllVarOutputs() {
	String start = "VAR_OUTPUT";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    var_output_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    var_output_all.add(new Integer(endpointer));
	}
    } 
    */
    /*private void printAll() {
	int count = 0;
	for (Integer item : program_cursor) {
	    System.out.println("program_cursor: "+item);
	    count++;
	}
	System.out.println("programcount: "+count);
	count = 0;

	for (Integer item : function_cursor) {
	    System.out.println("function_cursor: "+item);
	    count++;
	}
	System.out.println("functioncount: "+count);
	count = 0;

	for (Integer item : function_block_cursor) {
	    System.out.println("function_block_cursor: "+item);
	    count++;
	}
	System.out.println("functionblockcount: "+count);
	count = 0;

	for (Integer item : global_cursor) {
	    System.out.println("global_cursor: "+item);
	    count++;
	}
	System.out.println("globalcount: "+count);
	count = 0;

	for (Integer item : config_cursor) {
	    System.out.println("config_cursor: "+item);
	    count++;
	}
	System.out.println("configcount: "+count);
	count = 0;

	for (Integer item : var_all) {
	    System.out.println("var_all: "+item);
	    count++;
	}
	System.out.println("varcount: "+count);
	count = 0;

	for (Integer item : if_all) {
	    System.out.println("if_all: "+item);
	    count++;
	}
	System.out.println("ifcount: "+count);
	count = 0;

	for (Integer item : case_all) {
	    System.out.println("case_all: "+item);
	    count++;
	}
	System.out.println("casecount: "+count);
	count = 0;

	for (Integer item : for_all) {
	    System.out.println("for_all: "+item);
	    count++;
	}
	System.out.println("forcount: "+count);
	count = 0;

	for (Integer item : while_all) {
	    System.out.println("while_all: "+item);
	    count++;
	}
	System.out.println("whilecount: "+count);
	count = 0;

	for (Integer item : repeat_all) {
	    System.out.println("repeat_all: "+item);
	    count++;
	}
	System.out.println("repeatcount: "+count);
	count = 0;
    }*/

    /**
     * giveMaAllTheLists returns a list of list filled with Integers,
     * these Integers are the indexes of all the block beginnings and
     * endings.
     *
     * @return List<ArrayList<Integer>> 
     */
    public List<ArrayList<Integer>> giveMeAllTheLists() {
	/*List<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
	list.add(program_cursor);
	list.add(function_cursor);
	list.add(function_block_cursor);
	list.add(global_cursor);
	list.add(config_cursor);
	list.add(var_all);
	list.add(if_all);
	list.add(case_all);
	list.add(for_all);
	list.add(while_all);
	list.add(repeat_all);
	list.add(exit_all); /// ??? Oo
	list.add(var_input_all);
	list.add(var_output_all);
	return list; */
        return blocks;
    }
	
    /**
     * the Constructor takes a StringBuilder, which was prepared by MergeAllFiles
     *
     * @param builder StringBuilder prepared by MergeAllFiles
     */
    public Analyser(StringBuilder builder, LogWriter log) {
        //logstat = log.isInitialized();     // get status of the logger
        
        this.log = log;
        
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
        
	this.builder = builder;
	/*System.out.println("Parser: Analyser: findAllPrograms()");
	findAllPrograms(); // matcher done
	System.out.println("Parser: Analyser: findAllFunctions()");
	findAllFunctions(); // matcher done
	System.out.println("Parser: Analyser: findAllFunctionBlocks()");
	findAllFunctionBlocks(); // matcher done
	System.out.println("Parser: Analyser: findAllGlobals()");
	findAllGlobals(); // matcher done
	System.out.println("Parser: Analyser: findAllConfigs()");
	findAllConfigs(); // matcher done
	System.out.println("Parser: Analyser: findAllVars()");
	findAllVars(); // matcher done
	System.out.println("Parser: Analyser: findAllIfs()");
	findAllIfs(); // matcher done
	System.out.println("Parser: Analyser: findAllCases()");
	findAllCases(); // matcher done
	System.out.println("Parser: Analyser: findAllFors()");
	findAllFors(); // matcher done
	System.out.println("Parser: Analyser: findAllWhiles()");
	findAllWhiles(); // matcher done
	System.out.println("Parser: Analyser: findAllRepeats()");
	findAllRepeats(); // matcher done
	System.out.println("Parser: Analyser: findAllVarINputs()");
	findAllVarInputs(); // matcher done
	System.out.println("Parser: Analyser: findAllVarOutputs()");
	findAllVarOutputs(); // matcher done
	//printAll();*/
        findAllKeywords();
/*        for (String[] item : blocks.toArray(keywords)) {
            System.out.println(Arrays.toString(item));
        }*/
    }
}

// input output global config
