package parser;

import java.util.*;

/**
 * This class locates all block beginnings and endings
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
    private final StringBuilder builder;

    /**
     * findAllPrograms detects all program blocks and writes all startindexes of those
     * PROGRAM and END_PROGRAM statements in a ArrayList
     *
     * This function is private.
     */
    private void findAllPrograms() {
	String start = "PROGRAM";
	String stop = "END_PROGRAM";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    program_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    program_cursor.add(new Integer(endpointer));
	}
    }

    
    private void findAllFunctions() {
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
      	    function_block_cursor.add(new Integer(pointer));
	    function_block_cursor.add(new Integer(endpointer));
	}
    }

    private void findAllFunctionBlocks() {
	String start = "FUNCTION_BLOCK";
	String stop = "END_FUNCTION_BLOCK";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    function_block_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    function_block_cursor.add(new Integer(endpointer));
	}
    }

    private void findAllGlobals() {
	String start = "VAR_GLOBAL";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    global_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    global_cursor.add(new Integer(endpointer));
	}
    }
    
    private void findAllConfigs() {
	String start = "VAR_CONFIG";
	String stop = "END_VAR";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    config_cursor.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    config_cursor.add(new Integer(endpointer));
	}
    }

    private void findAllVars() {
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
    }
    
    private void findAllIfs() {
	String start = "IF";
	String stop = "END_IF";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    if (builder.substring(pointer-4, pointer)=="ELSE") {
		endpointer = builder.indexOf(stop, pointer);
		continue;
	    }
	    if_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    if_all.add(new Integer(endpointer));
	}
    }

    private void findAllCases() {
	String start = "CASE";
	String stop = "END_CASE";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    case_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    case_all.add(new Integer(endpointer));
	}
    }
    
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

    private void findAllRepeats() {
	String start = "REPEAT";
	String stop = "END_REPEAT";
	int endpointer;
	for (int pointer = builder.indexOf(start); pointer != -1; pointer = builder.indexOf(start, endpointer+stop.length())) {
	    repeat_all.add(new Integer(pointer));
	    endpointer = builder.indexOf(stop, pointer);
	    repeat_all.add(new Integer(endpointer));
	}
    }

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

    public List<ArrayList<Integer>> giveMeAllTheLists() {
	List<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
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
	list.add(exit_all);
	//List< List<IntegerNode>> nodeLists = new LinkedList< List< IntegerNode >>();
	    /*
	list[0] = program_cursor;
	list[1] = function_cursor;
	list[2] = function_block_cursor;
	list[3] = global_cursor;
	list[4] = config_cursor;
	list[5] = var_all;
	list[6] = if_all;
	list[7] = case_all;
	list[8] = for_all;
	list[9] = while_all;
	list[10] = repeat_all;
	list[11] = exit_all;*/
	return list;
    }
	
    public Analyser(StringBuilder builder) {
	this.builder = builder;
	findAllPrograms();
	findAllFunctions();
	findAllFunctionBlocks();
	findAllGlobals();
	findAllConfigs();
	findAllVars();
	findAllIfs();
	findAllCases();
	findAllFors();
	findAllWhiles();
	findAllRepeats();
	//printAll();
    }
}
