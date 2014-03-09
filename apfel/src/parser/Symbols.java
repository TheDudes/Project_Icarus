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
 * This finds all var block and the corresponding context in which they life
 * an creates some hashmaps in hashmaps to hold there information
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */

public class Symbols {
    
    private StringBuilder temp; // a temporary StringBuilder to save something in some functions
    private StringBuilder builder; // the whole programm code
    private Match match; // the matcher, this class holds some importand Informations
    private ArrayList<Integer[]> deleteme;  // the list of all marked var blocks as arrays with first index and very last index
    private ArrayList<String> symbolnames;  // list with all the symbolnames
    private int id = 0; // the global variable id

    //***
    // Fucked up containers are my bussines
    //***
    private HashMap<String,HashMap<String,Integer>> contextstore; // String for context, String for variable name, Integer for variable id
    private HashMap<Integer,String> typebyid; // Integer id, String variable type
    private HashMap<Integer,Object> valuebyid; // Integer id, Object variable value

    /**
     * findContextVars adds, depending on the context type, all the vars to the correct container
     * <p>
     * This function is private
     * @param kind String like PROGRAM or FUNCTION
     */
    private void findContextVars(String kind) {
	ArrayList<Integer> varlist;
	StringBuilder block = new StringBuilder();
	StringBuilder tmpstr;
	String context;
	int tmpint;
	switch (kind) {
	case "PROGRAM":
	    for (int k : match.getPrograms()) {
		varlist = new ArrayList<>();
		for (int j : match.getVars()) {
		    if (j < match.getEndProgram(k) && j > k) { // room for improvement here! but, fuck it.
			varlist.add(j);
		    }
		}
		context = builder.substring(k+7, varlist.get(0));  // +7 == "PROGRAM".length(), i should replace all these with final constants, btw. finds out name of the context
		for (int i : varlist) {
		    tmpint = match.getEndVar(i);
		    block.append(builder.substring(i+match.getVarStart(i).length(), tmpint));  
		    deleteme.add(new Integer[] {i, tmpint+7});  // +7 == "END_VAR".length()
		}
		fillUpTheContainers(context, block);
		varlist = null;
	    }
	    break;
	case "FUNCTION":
	    for (int k : match.getFunctions()) {
		varlist = new ArrayList<>();
		for (int j : match.getVars()) {
		    if (j < match.getEndFunction(k) && j > k) { // room for improvement here! but, fuck it.
			varlist.add(j);
		    }
		}
		context = builder.substring(k+8, varlist.get(0));  // +8 == "FUNCTION".length(), i should replace all these with final constants, btw. finds out name of the context
		for (int i : varlist) {
		    tmpint = match.getEndVar(i);
		    block.append(builder.substring(i+match.getVarStart(i).length(), tmpint));  
		    deleteme.add(new Integer[] {i, tmpint+7});  // +7 == "END_VAR".length()
		}
		fillUpTheContainers(context, block);
		varlist = null;
	    }
	    break;
	case "FUNCTION_BLOCK":
	    for (int k : match.getFunctionBlocks()) {
		varlist = new ArrayList<>();
		for (int j : match.getVars()) {
		    if (j < match.getEndFunctionBlock(k) && j > k) { // room for improvement here! but, fuck it.
			varlist.add(j);
		    }
		}
		context = builder.substring(k+14, varlist.get(0));  // +14 == "FUNCTION_BLOCK".length(), i should replace all these with final constants, btw. finds out name of the context
		for (int i : varlist) {
		    tmpint = match.getEndVar(i);
		    block.append(builder.substring(i+match.getVarStart(i).length(), tmpint));  
		    deleteme.add(new Integer[] {i, tmpint+7});  // +7 == "END_VAR".length()
		}
		fillUpTheContainers(context, block);
		varlist = null;
	    }
	    break;
	default:
	    System.err.println("What are you doing stupid FAG!?");
	}
	
    }
    
    /**
     * fillUpTheContainers is a helper function for findContextVars, this function
     * has the actual logic to fill the containers with the right things.
     * <p>
     * This function is private
     * @param context the Context where the variable block lifes in
     * @param block the variable block
     */
    private void fillUpTheContainers(String context, StringBuilder block) {
	HashMap<String,Integer> percontext;
	int tmpint;
	Integer tmpint2;
	String type;
	String[] names;
	String value;
	try {
	    for (StringBuilder b = block.delete(0, block.indexOf(";")+1); true; b = block.delete(0, block.indexOf(";")+1)) {
		tmpint = b.indexOf(":=");
		if (tmpint == -1) {
		    tmpint = b.indexOf(":");
		    //if (tmpint == -1)
		    // throw the wrong variablen deklaration error or so 
		    names = b.substring(0, tmpint).split(",");
		    type = b.substring(tmpint+1, b.indexOf(";"));
		    percontext = new HashMap<>();
		    for (String name : names) {
			percontext.put(name, id);
			typebyid.put(id, type);
			valuebyid.put(id, TYPES.getType(type));
			contextstore.put(context, percontext);
			id++;
		    }
		} else if (!(Pattern.matches(":.*:", b.toString()))) {
		    names = b.substring(0, tmpint).split(",");
		    type = typebyid.get(contextstore.get(context).get(names[0]));
		    value = b.substring(tmpint+2, b.indexOf(";"));
		    for (String name : names) {
			tmpint2 = contextstore.get(context).get(name);
			if (!(tmpint2 == null)) {
			    valuebyid.put(tmpint2, TYPES.getType(type, value));
			} else {
			    // throw unknown symbol in this context exception
			}
		    }
		} else {
		    tmpint2 = b.indexOf(":");
		    //if (tmpint == -1)
		    // throw the wrong variablen deklaration error or so 
		    names = b.substring(0, tmpint2).split(",");
		    type = b.substring(tmpint2+1, tmpint);
		    value = b.substring(tmpint+2, b.indexOf(";"));
		    percontext = new HashMap<>();
		    for (String name : names) {
			percontext.put(name, id);
			typebyid.put(id, type);
			valuebyid.put(id, TYPES.getType(type, value));
			contextstore.put(context, percontext);
			id++;
		    }
		}
	    }
	    } catch (StringIndexOutOfBoundsException e) {
		// no vars left, just du nothing and go on
	    }
	}

    /**
     * deleteVarBlocks was thought as a cleanup, because nobody realy needs the
     * variable blocks after the operations this class does, but it would destroy
     * the whole functionality of the index based logic.
     * <p>
     * This function is private and shouldn't be used
     */
    private void deleteVarBlocks() {
	Collections.sort(deleteme, new Comparator<Integer[]>() {
		@Override public int compare(Integer[] a, Integer[] b) {
		    if (a[0] > b[0]) { return -1; }
		    else if (a[0] < b[0]) { return 1; }
		    else { return 0; }
		}
	    }
	    );
	for (Integer[] item : deleteme) {
	    builder.delete(item[0], item[1]);
	}
    }

    /**
     * generateSymbolsList creates a list with all the Symbols sorted from long to short
     * <p>
     * This function is private
     */
    private void generateSymbolsList() {
	String context;
	for(Map.Entry<String,HashMap<String,Integer>> percontext : contextstore.entrySet()) {
	    context = percontext.getKey();
	    for(Map.Entry<String,Integer> value : percontext.getValue().entrySet()) {
		symbolnames.add(value.getKey());
	    }
	}
	// now sort the new generated list from long to short, with a dirty inline class ;)
	/*
	 * negativer Rückgabewert: Der erste Parameter ist untergeordnet
	 * 0 als Rückgabewert: Beide Parameter werden gleich eingeordnet
	 * positiver Rückgabewert: Der erste Parameter ist übergeordnet
	 */
	Collections.sort(symbolnames, new Comparator<String>() {
		private int alen;
		private int blen;
		@Override public int compare(String a, String b) {
		    alen = a.length();
		    blen = b.length();
		    if (alen > blen) { return -1; }
		    else if (alen < blen) { return 1; }
		    else { return 0; }
		}
	    });
    }
    
    /**
     * replaceVars replaces all variable names in a given string with its values
     * @param input string with variable names in it
     * @param context the context in which this variables should be
     * @return the input string with all replaced variables
     */
    public String replaceVars(String input, String context) {
	for (String item : symbolnames) {
	    input.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
	}
	return input;
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
    public void setValue(String input, String context) {
	fillUpTheContainers(context, new StringBuilder(input));
    }
    

    /**
     * Symbols needs a StringBuilder and a Match to work and to do all his Operations
     * @param builder the whole program code
     * @param match the match class with preprosesd informations
     */
    public Symbols(StringBuilder builder, Match match) {
	this.builder = builder;
	this.match = match;

	deleteme = new ArrayList<>();
	symbolnames = new ArrayList<>();
	contextstore = new HashMap<String,HashMap<String,Integer>>();
	typebyid = new HashMap<>();
	valuebyid = new HashMap<>();

	findContextVars("PROGRAM");
	findContextVars("FUNCTION");
	findContextVars("FUNCTION_BLOCK");
	generateSymbolsList();
	//deleteVarBlocks();
    }
}
