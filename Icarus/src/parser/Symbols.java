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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.*;


import linc.Config_Reader;

import vault.*;

/**
 * This finds all var block and the corresponding context in which they life an
 * creates some hashmaps in hashmaps to hold there information
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class Symbols {

	private final  StringBuilder         builder;         /* the whole programm code */
	private final  Match                 match;           /* the matcher, this class holds some importand Informations */
	private final  Config_Reader         confreader;
	private final  ArrayList<Integer[]>  deleteme;        /* the list of all marked var blocks as arrays with first index and very last index */
	private        ArrayList<String>     symbolnames;     /* list with all the symbolnames */
	private final  ArrayList<Integer>    variableIndexes; /* olds getVars() from match */
	private        int                   var_id;      /* the global variable var_id */

	//***
	// Fucked up containers are my bussines
	//***
	private final  HashMap<String, HashMap<String, Integer>>  contextstore;     /* String for context, String for variable name, Integer for variable var_id */
	private final  HashMap<Integer, String>                   typebyid;         /* Integer var_id, String variable type */
	private final  HashMap<Integer, Object>                   valuebyid;        /* Integer var_id, Object variable value */
	
	
	/* Device Handling things here */
        private final  HashMap<String, Integer>                     device_deviceid; /* Integer device var_id and the pins as Integer[] pin is null if not defined. */
	private final  HashMap<Integer, HashMap<Integer, Integer>>  deviceid_pinid_valueid;
	private final  HashMap<Integer, Integer>                    valueid_abilities; /* abilities: 0=undefined, 1=read, 2=write, 3=read/write  */
	private final  LinkedBlockingQueue<IO_Package>              com_channel_queue;
	private        int  device_id;
	private        int  pin_id;
	
	
	/* logger */
	private final  LogWriter  log;
	private final  String     mainkey  = "parser";
	private final  String     subkey   = "Symbols";
	private final  String     key      = mainkey + "-" + subkey;


	/**
	 * Symbols needs a StringBuilder and a Match to work and to do all his
	 * Operations
	 *
	 * @param builder the whole program code
	 * @param match the match class with preprosesd informations
	 * @param log logwriter from abouve
	 * @param confreader Config_Reader from above
	 * @throws java.lang.Exception
	 */
	public
	Symbols(StringBuilder builder, Match match, LogWriter log, Config_Reader confreader) throws Exception
	{
		this.log         = log;
		this.builder     = builder;
		this.match       = match;
		this.confreader  = confreader;

		var_id     = 0;
		device_id  = 0;
		pin_id     = 0;

		variableIndexes = match.get_vars(); /* pulls out the vars from match */
        
		deleteme                = new ArrayList<>();
		symbolnames             = new ArrayList<>();
		contextstore            = new HashMap<>();
		typebyid                = new HashMap<>();
		valuebyid               = new HashMap<>();
		device_deviceid         = new HashMap<>();
		deviceid_pinid_valueid  = new HashMap<>();
		valueid_abilities       = new HashMap<>();
		com_channel_queue       = new LinkedBlockingQueue<>();

		find_context_vars("PROGRAM");
		find_context_vars("FUNCTION");
		find_context_vars("FUNCTION_BLOCK");
		//findContextVars("GLOBAL");
        
		generate_symbols_list();
		create_devices();
	}
	
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
		log.log(key, 4, "find_context_vars called.");
		log.log(key, 4, "kind: "+kind);
		
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

				log.log(key, 4, "Found context: "+context);
				
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

				log.log(key, 4, "Found context: "+context);
				
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

				log.log(key, 4, "Found context: "+context);
				
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
		        log.log(key, 0, "Someting strange happen .... unknown kind: "+kind);
			throw new Exception("Someting strange happen .... unknown kind: "+kind);
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
		log.log(key, 4, "fill_up_the_containers called.");
		
		HashMap<String, Integer>  percontext;

		int      tmpint;
		Integer  tmpint2;
		String   type;
		String[] names;
		String   value;

		log.log(key, 4, "Current var block: "+block.toString());
		
		try {
			for (StringBuilder b = new StringBuilder(block.substring(0, block.indexOf(";") + 1)); true; b = new StringBuilder(block.substring(0, block.indexOf(";") + 1))) {
				block.delete(0, block.indexOf(";") + 1);
				tmpint = b.indexOf(":=");

				log.log(key, 4, "Current line: "+b);
				
				if (tmpint == -1) {
					tmpint = b.indexOf(":");

					/* variable deklaration wrong */
					/*if (tmpint == -1) {
						log.log(key, 0, "Wrong variable Deklaration: "+b.toString());
						throw new Exception("Wrong variable Deklaration: "+b.toString()); 
						}*/
					log.log(key, 4, "Without Initialization.");
					
					names = b.substring(0, tmpint).split(",");
					type = b.substring(tmpint + 1, b.indexOf(";"));
                    
					percontext = contextstore.get(context);
					if (percontext == null) {
						percontext = new HashMap<>();
					}
					for (String name : names) {
						percontext.put(name, var_id);
						typebyid.put(var_id, type);
						valuebyid.put(var_id, TYPES.get_type(type));
						//contextstore.put(context, percontext); /* i leave this for a moment, till i remember what i did there ... */
						var_id++;
					}
					contextstore.put(context, percontext);
				} else if (Pattern.matches("@", block.toString())) {
					
					names       = b.substring(0, b.indexOf(":")).split(",");
					type        = typebyid.get(contextstore.get(context).get(names[0]));
					value       = b.substring(tmpint + 2, b.indexOf(";"));
					percontext  = contextstore.get(context);

					if (percontext == null) {
						percontext = new HashMap<>();  /* hmmm ... */
					}
					for (String name : names) {
						tmpint2 = contextstore.get(context).get(name);
						if (!(tmpint2 == null)) {
							valuebyid.put(tmpint2, TYPES.get_type(type, value));
						} else {
							/* throw unknown symbol in this context exception */
							log.log(key, 4, "Unknown Symbol \"" + name + "\" in: " + context);
							throw new Exception("Unknown Symbol \"" + name + "\" in: " + context);
						}
					}
				} else {
					tmpint2 = b.indexOf(":");

					log.log(key, 4, "With Initialization.");
					
				        /* variable deklaration wrong */
					/*if (tmpint == -1) {
						log.log(key, 0, "Wrong variable Deklaration: "+b.toString());
						throw new Exception("Wrong variable Deklaration: "+b.toString()); 
						}*/
					
					names       = b.substring(0, tmpint2).split(",");
					type        = b.substring(tmpint2 + 1, tmpint);
					value       = b.substring(tmpint + 2, b.indexOf(";"));
					percontext  = contextstore.get(context);

					if (percontext == null) {
						percontext = new HashMap<>();
					}
					for (String name : names) {
						percontext.put(name, var_id);
						typebyid.put(var_id, type);
						valuebyid.put(var_id, TYPES.get_type(type, value));
						var_id++;
					}
					contextstore.put(context, percontext);
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			/* no vars left, just du nothing and go on, very dirty, i will change this ... */
			log.log(key, 4, "Active used Exception! Change/Delete me!!");
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
		symbolnames = new ArrayList<>();
		
		log.log(key, 4, "generate_symbols_list called.");
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
				
				@Override public int
					compare(String a, String b) {
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
		
		log.log(key, 4, "symbols list: "+Arrays.toString(symbolnames.toArray()));
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
		log.log(key, 4, "replace_vars called.");
		log.log(key, 4, "input: "+input);
		log.log(key, 4, "context: "+context);
		
		String tmp = null;

		for (String item : symbolnames) {
			if (tmp == null) {
				tmp = input.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
			} else {
				tmp = tmp.replaceAll(item, valuebyid.get(contextstore.get(context).get(item)).toString());
                
			}
		}

		log.log(key, 4, "return value: "+tmp);
		
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
		log.log(key, 4, "set_value called.");
		log.log(key, 4, "input: "+input);
		log.log(key, 4, "context: "+context);
		
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
		log.log(key, 4, "add_var called.");
		log.log(key, 4, "input: "  +input);
		log.log(key, 4, "context: "+context);
		
		/* type is integer all the time */
		String[]  splitted  = input.split(":=");
		Integer   tmp       = contextstore.get(context).get(splitted[0]);

		if (tmp == null) {
			log.log(key, 4, "variable not yet defined.");
			fill_up_the_containers(context, new StringBuilder(splitted[0] + ":INT:=" + splitted[1]));
		} else {
			log.log(key, 4, "variable allready defined");

/*HashMap<String, Integer> percontext; //= new HashMap<>();
			  typebyid.remove(tmp);
			  valuebyid.remove(tmp);
			  percontext = contextstore.get(context);
			  percontext.remove(splitted[0]);
			  contextstore.put(context, percontext);  // aaaah fuck it, tooo late man .... */
			fill_up_the_containers(context, new StringBuilder(input + "@" ));
		}
		generate_symbols_list();
	}

	private int
	get_varid_by_name(String context, String variable)
	{
		return contextstore.get(context).get(variable);
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
		return com_channel_queue;
	}

	/**
	 * create_devices fills the structures up with all the devices, pins and values 
	 * <p>
	 * this function is private.
	 */
	private void
	create_devices()
	{
		String[]             devices                       = confreader.get_string("devices").split(",");
		ArrayList<String[]>  variable_and_device_with_pin  = new ArrayList<>();
		HashMap<Integer,Integer> pin_to_var;
		int varid;
		int pinid;
		
		for (String item : match.get_var_config_entrys())
		{
			variable_and_device_with_pin.add(item.split("\\.|AT"));
		}

		for (String device : devices)
		{
			for (String[] item : variable_and_device_with_pin)
			{
				if(item[2].equals(device))
				{
					if (!(device_deviceid.get(device)==null))
					{
						pin_to_var = deviceid_pinid_valueid.get(device_id);
					}
					else
					{
						device_deviceid.put(device, device_id);
						pin_to_var = new HashMap<>();
					}
					
					varid = get_varid_by_name(item[0], item[1]);
					pinid = Integer.parseInt(item[3]);
					pin_to_var.put(pinid, varid);
					deviceid_pinid_valueid.put(device_id, pin_to_var);

					for (String abilities : confreader.get_string(device).split(","))
					{
						String[] abilitie = abilities.split(":");
						if (abilitie[0].equals(item[3]))
						{
							valueid_abilities.put(varid,Integer.parseInt(abilitie[1]));
							if (abilitie[1].equals("1") || abilitie[1].equals("3"))
							{
								com_channel_queue.offer(new IO_Package(device, (byte)pinid, (byte)0, Byte.parseByte(abilitie[0]), true));
							}
						}
					}
				}
			}
			device_id++;
		}
	}
	
}
