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
import vault.*;

/**
 * This finds all var block and the corresponding context in which they life an
 * creates some hashmaps in hashmaps to hold there information
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class Symbols {

	private final StringBuilder         builder;         /* the whole programm code */
	private final Match                 match;           /* the matcher, this class holds some importand Informations */
	private final ArrayList<Integer[]>  deleteme;        /* the list of all marked var blocks as arrays with first index and very last index */
	private final ArrayList<String>     symbolnames;     /* list with all the symbolnames */
	private final ArrayList<Integer>    variableIndexes; /* olds getVars() from match */
	private       int                   id = 0;          /* the global variable id */

	//***
	// Fucked up containers are my bussines
	//***
	private final HashMap<String, HashMap<String, Integer>>  contextstore;     /* String for context, String for variable name, Integer for variable id */
	private final HashMap<Integer, String>                   typebyid;         /* Integer id, String variable type */
	private final HashMap<Integer, Object>                   valuebyid;        /* Integer id, Object variable value */

	/* Device Handling things here */
        //private final HashMap<Integer, Integer[]>                deviceidwithpins; /* Integer device id and the pins as Integer[] pin is null if not defined. */

    
	/* logger */
	//private boolean logstat;
	private final LogWriter  log;
	private final String     mainkey  = "parser";
	private final String     subkey   = "Symbols";
	private final String     key      = mainkey + "-" + subkey;
    
	/**
	 * findContextVars adds, depending on the context type, all the vars to the
	 * correct container
	 * <p>
	 * This function is private
	 *
	 * @param kind String like PROGRAM or FUNCTION
	 */
	private void
	find_context_vars(String kind) throws Exception
	{
		ArrayList<Integer>  varlist;
		StringBuilder       block = new StringBuilder();
		String              context;
		int                 tmpint;
		switch (kind) {
		case "PROGRAM":
			for (int k : match.get_programs()) {
				varlist = new ArrayList<>();
				for (int j : variableIndexes) {
					if (j < match.get_end_program(k) && j > k) { /* room for improvement here! i should take some minutes and think about the variable names .... */
						varlist.add(j);
					}
				}
				context = builder.substring(k + 7, varlist.get(0));  /* +7 == "PROGRAM".length(), i should replace all these with final constants, btw. finds out name of the context */
				for (int i : varlist) {
					tmpint = match.get_end_var(i);
					block.append(builder.substring(i + match.get_var_start(i).length(), tmpint));
					deleteme.add(new Integer[]{i, tmpint + 7});  /* +7 == "END_VAR".length() */
				}
				fill_up_the_containers(context, block);
				varlist = null;
			}
			break;
		case "FUNCTION":
			for (int k : match.get_functions()) {
				varlist = new ArrayList<>();
				for (int j : variableIndexes) {
					if (j < match.get_end_function(k) && j > k) { /* room for improvement here! but, fuck it. */
						varlist.add(j);
					}
				}
				context = builder.substring(k + 8, varlist.get(0));  /* +8 == "FUNCTION".length(), i should replace all these with final constants, btw. finds out name of the context */
				for (int i : varlist) {
					tmpint = match.get_end_var(i);
					block.append(builder.substring(i + match.get_var_start(i).length(), tmpint));
					deleteme.add(new Integer[]{i, tmpint + 7});  /* +7 == "END_VAR".length() */
				}
				fill_up_the_containers(context, block);
				varlist = null;
			}
			break;
		case "FUNCTION_BLOCK":
			for (int k : match.get_function_blocks()) {
				varlist = new ArrayList<>();
				for (int j : variableIndexes) {
					if (j < match.get_end_function_block(k) && j > k) { /* room for improvement again. */
						varlist.add(j);
					}
				}
				context = builder.substring(k + 14, varlist.get(0));  /* +14 == "FUNCTION_BLOCK".length(), i should replace all these with final constants, btw. finds out name of the context */
				for (int i : varlist) {
					tmpint = match.get_end_var(i);
					block.append(builder.substring(i + match.get_var_start(i).length(), tmpint));
					deleteme.add(new Integer[]{i, tmpint + 7});  /* +7 == "END_VAR".length() */
				}
				fill_up_the_containers(context, block);
				varlist = null;
			}
			break;
			/*case "GLOBAL":
			  context = kind;
			  int tmp = builder.indexOf("VAR_GLOBAL");
			  tmpint = match.getEndVar(tmp);
			  block.append(builder.substring(tmp + match.getVarStart(tmp).length(), tmpint));
			  fill_up_the_containers(context, block);
			  break;*/
		default:
			System.err.println("What are you doing stupid FAG!?"); /* here should be some kind of Exception ... */
		}

	}

	/**
	 * fill_up_the_containers is a helper function for findContextVars, this
	 * function has the actual logic to fill the containers with the right
	 * things.
	 * <p>
	 * This function is private
	 *
	 * @param context the Context where the variable block lifes in
	 * @param block the variable block
	 */
	private void
	fill_up_the_containers(String context, StringBuilder block) throws Exception
	{
		HashMap<String, Integer>  percontext;
		int      tmpint;
		Integer  tmpint2;
		String   type;
		String[] names;
		String   value;
		try {
			for (StringBuilder b = new StringBuilder(block.substring(0, block.indexOf(";") + 1)); true; b = new StringBuilder(block.substring(0, block.indexOf(";") + 1))) {
				block.delete(0, block.indexOf(";") + 1);
				tmpint = b.indexOf(":=");
				if (tmpint == -1) {
					tmpint = b.indexOf(":");
					/*
					  if (tmpint == -1)
					  throw the wrong variablen deklaration error or so 
					*/
					names = b.substring(0, tmpint).split(",");
					type = b.substring(tmpint + 1, b.indexOf(";"));
                    
					percontext = contextstore.get(context);
					if (percontext == null) {
						percontext = new HashMap<>();
					}
					for (String name : names) {
						percontext.put(name, id);
						typebyid.put(id, type);
						valuebyid.put(id, TYPES.get_type(type));
						//contextstore.put(context, percontext); /* i leave this for a moment, till i remember what i did there ... */
						id++;
					}
					contextstore.put(context, percontext);
				} else if (Pattern.matches("@", block.toString())) {
					names = b.substring(0, b.indexOf(":")).split(",");
					type = typebyid.get(contextstore.get(context).get(names[0]));
					value = b.substring(tmpint + 2, b.indexOf(";"));
					percontext = contextstore.get(context);
					if (percontext == null) {
						percontext = new HashMap<>();  /* hmmm ... */
					}
					for (String name : names) {
						tmpint2 = contextstore.get(context).get(name);
						if (!(tmpint2 == null)) {
							valuebyid.put(tmpint2, TYPES.get_type(type, value));
						} else {
							/* throw unknown symbol in this context exception */
							throw new Exception("Unknown Symbol \"" + name + "\" in: " + context);
						}
					}
				} else {
					tmpint2 = b.indexOf(":");
					/*
					  if (tmpint == -1)
					  throw the wrong variablen deklaration error or so 
					*/
					names = b.substring(0, tmpint2).split(",");
					type = b.substring(tmpint2 + 1, tmpint);
					value = b.substring(tmpint + 2, b.indexOf(";"));
					percontext = contextstore.get(context);
					if (percontext == null) {
						percontext = new HashMap<>();
					}
					for (String name : names) {
						percontext.put(name, id);
						typebyid.put(id, type);
						valuebyid.put(id, TYPES.get_type(type, value));
						id++;
					}
					contextstore.put(context, percontext);
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			/* no vars left, just du nothing and go on, very dirty, i will change this ... */
		}
	}

	/**
	 * deleteVarBlocks was thought as a cleanup, because nobody realy needs the
	 * variable blocks after the operations this class does, but it would
	 * destroy the whole functionality of the index based logic.
	 * <p>
	 * This function is private and shouldn't be used
	 */
	/*
	  private void deleteVarBlocks() {
	  Collections.sort(deleteme, new Comparator<Integer[]>() {
	  @Override
	  public int compare(Integer[] a, Integer[] b) {
	  if (a[0] > b[0]) {
	  return -1;
	  } else if (a[0] < b[0]) {
	  return 1;
	  } else {
	  return 0;
	  }
	  }
	  }
	  );
	  for (Integer[] item : deleteme) {
	  builder.delete(item[0], item[1]);
	  }
	  }*/

	/**
	 * generateSymbolsList creates a list with all the Symbols sorted from long
	 * to short
	 * <p>
	 * This function is private
	 */
	private void
	generate_symbols_list()
	{
		// String context;
		for (Map.Entry<String, HashMap<String, Integer>> percontext : contextstore.entrySet()) {
			// context = percontext.getKey();
			for (Map.Entry<String, Integer> value : percontext.getValue().entrySet()) {
				symbolnames.add(value.getKey());
			}
		}
		/* now sort the new generated list from long to short, with a dirty inline class ;) */
		/*
		 * negativer Rückgabewert: Der erste Parameter ist untergeordnet
		 * 0 als Rückgabewert: Beide Parameter werden gleich eingeordnet
		 * positiver Rückgabewert: Der erste Parameter ist übergeordnet
		 */
		Collections.sort(symbolnames, new Comparator<String>() {
				private int alen;
				private int blen;

				@Override
					public int compare(String a, String b) {
					alen = a.length();
					blen = b.length();
					if (alen > blen) {
						return -1;
					} else if (alen < blen) {
						return 1;
					} else {
						return 0;
					}
				}
			});
	}

	/**
	 * replaceVars replaces all variable names in a given string with its values
	 *
	 * @param input string with variable names in it
	 * @param context the context in which this variables should be
	 * @return the input string with all replaced variables
	 */
	public String
	replace_vars(String input, String context)
	{
		String tmp = null;
		for (String item : symbolnames) {
			if (tmp == null) {
				tmp = input.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
			} else {
				tmp = tmp.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
                
			}
		}
		/*for (String item : symbolnames) {
		  input.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
		  }*/
		return tmp;
	}

	/**
	 * setValue accepts whole variable lines like: var,var1,var2:=5; OR
	 * var3:=3.45; and sets the new value in the right containers depending on
	 * there context.
	 *
	 * @param input String with variable line.
	 * @param context String representing
	 * @throws java.lang.Exception the context
	 */
	public void
	set_value(String input, String context) throws Exception
	{
		fill_up_the_containers(context, new StringBuilder(input + "@"));
	}

	/**
	 * addVar will add a variable to a context like setValue
	 *
	 * @param input String with variable line.
	 * @param context String representing the context
	 * @throws java.lang.Exception
	 */
	public void
	add_var(String input, String context) throws Exception
	{
		/* type is integer all the time */
		String[] splitted = input.split(":=");
		Integer tmp = contextstore.get(context).get(splitted[0]);
		if (tmp == null) {
			fill_up_the_containers(context, new StringBuilder(splitted[0] + ":INT:=" + splitted[1]));
		} else {
			/*HashMap<String, Integer> percontext; //= new HashMap<>();
			  typebyid.remove(tmp);
			  valuebyid.remove(tmp);
			  percontext = contextstore.get(context);
			  percontext.remove(splitted[0]);
			  contextstore.put(context, percontext);  // aaaah fuck it, tooo late man .... */
			fill_up_the_containers(context, new StringBuilder(input));
		}
		generate_symbols_list();
	}

	/**
	 * Symbols needs a StringBuilder and a Match to work and to do all his
	 * Operations
	 *
	 * @param builder the whole program code
	 * @param match the match class with preprosesd informations
	 * @param log logwriter from abouve
	 * @throws java.lang.Exception
	 */
	public
	Symbols(StringBuilder builder, Match match, LogWriter log) throws Exception
	{
		this.log = log;
		this.builder = builder;
		this.match = match;

		variableIndexes = match.get_vars(); /* pulls out the vars from match */
        
		deleteme = new ArrayList<>();
		symbolnames = new ArrayList<>();
		contextstore = new HashMap<>();
		typebyid = new HashMap<>();
		valuebyid = new HashMap<>();

		find_context_vars("PROGRAM");
		find_context_vars("FUNCTION");
		find_context_vars("FUNCTION_BLOCK");
		//findContextVars("GLOBAL");
        
		generate_symbols_list();
		//deleteVarBlocks(); /* legacy code, i should remove this completele */
	}
}
