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
import java.util.*;

import logger.*;

/**
 * This class locates all block beginnings and endings
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class
Analyser
{
        /* all the states of my statemachines */
        private enum analyse_states {mainloop, find_context, var_handling, case_handling, ignore_string}
        private enum var_states {find_semicollon, type_or_value_or_name, get_type, get_value, get_name, get_last_name, write_to_structure}
        private enum config_states {start, find_context, find_var_name, find_type, find_address, find_var_type, find_AT_percent, find_IN_or_OUT}

        /* all the code as a String */
        private final String code;

        /* stacks */
        private final intStack program_stack;
        private final intStack function_stack;
        private final intStack function_block_stack;
        private final intStack case_stack;
        private final intStack for_stack;
        private final intStack while_stack;
        private final intStack repeat_stack;
        private final intStack if_stack;
        private final intStack var_stack;

        /* tree maps */
        private final TreeMap<Integer,Integer>      program_map;
        private final TreeMap<Integer,Integer>      function_map;
        private final TreeMap<Integer,Integer>      function_block_map;
        private final TreeMap<Integer,Integer>      var_map;
        private final TreeMap<Integer,CaseHandling> case_map;
        private final TreeMap<Integer,Integer>      for_map;
        private final TreeMap<Integer,Integer>      while_map;
        private final TreeMap<Integer,Integer>      repeat_map;
        private final TreeMap<Integer,Integer>      if_map;

        /* variable container */
        private final HashMap<String,MappedByte>               address_mappedbyte_in;
        private final HashMap<String,MappedByte>               address_mappedbyte_out;
        private final HashMap<String,HashMap<String,Variable>> context_varname_var;
        private       int var_config_start;
        private       int var_global_start;
        private final HashMap<String,HashMap<Integer,Variable>> functioname_inputid_var;
        private final HashMap<String,Integer>                   program_startpoint;
        private final HashMap<String,Integer>                   function_startpoint;

        /* device lbq */
        private final  LinkedBlockingQueue<IO_Package>  com_channel_queue;

        /* different things */
        private ArrayList<String> symbolnames;
        private       int   id = 0;
        private final TYPES types;

	/* logger */
	private final Logger  log;
	private final String  mainkey  = "parser";
	private final String  subkey   = "Analyser";
	private final String  key      = " ["+mainkey + "-" + subkey+"] ";
        private final int     log_level = Integer.parseInt(System.getProperty("loglevel"));

	/**
	 * the Constructor takes a StringBuilder, which was prepared by MergeAllFiles
	 * @param builder StringBuilder prepared by MergeAllFiles
	 * @param log Logger from above
	 */
	public
	Analyser(StringBuilder builder, Logger log)
	{
                this.log      = log;
                this.code     = builder.toString();
                if(log_level >= 3) log.log(3, key, "Analyser is doing stuff please wait ...", "\n");

                types = new TYPES(log);

                program_stack        = new intStack(100);
                function_stack       = new intStack(100);
                function_block_stack = new intStack(100);
                var_stack            = new intStack(100);
                case_stack           = new intStack(100);
                for_stack            = new intStack(100);
                while_stack          = new intStack(100);
                repeat_stack         = new intStack(100);
                if_stack             = new intStack(100);

                program_map        = new TreeMap<>();
                function_map       = new TreeMap<>();
                function_block_map = new TreeMap<>();
                var_map            = new TreeMap<>();
                case_map           = new TreeMap<>();
                for_map            = new TreeMap<>();
                while_map          = new TreeMap<>();
                repeat_map         = new TreeMap<>();
                if_map             = new TreeMap<>();

                context_varname_var     = new HashMap<>();
                address_mappedbyte_in   = new HashMap<>();
                address_mappedbyte_out  = new HashMap<>();
                com_channel_queue       = new LinkedBlockingQueue<>();
                functioname_inputid_var = new HashMap<>();
                program_startpoint      = new HashMap<>();
                function_startpoint     = new HashMap<>();

                var_global_start = -1;
                var_config_start = -1;

                if(log_level >= 4) log.log(4, key, "run analyse()", "\n");
                analyse();
                if(log_level >= 4) log.log(4, key, "analyse() finished", "\n");
                if(log_level >= 4) log.log(4, key, "run build_function_structure()", "\n");
                build_function_structure();
                if(log_level >= 4) log.log(4, key, "build_function_structure finished", "\n");

                generate_symbols_list();
	}


        /**
         * analyse is the main function here, analyse iterates through the code with a statemachine
         * and stores all information found in the correct structures
         */
        private void
        analyse()
        {
                if(log_level >= 4) log.log(4, key, "\tanalyse() called", "\n");
                analyse_states state        = analyse_states.mainloop;
                String         context      = "";
                String         context_type = "";
                String         var_block    = "";
                Integer        temp_start   = null;


                for (int index = 0; index < code.length();)
                {

                        switch (state){
                        case mainloop:
                                if(log_level >= 4) log.log(4, key, "\tState: mainloop", "\n");

                                if(code.charAt(index) == '"'){
                                        state = analyse_states.ignore_string;
                                        index += 1;
                                }
                                /*end checks*/
                                else if (code.charAt(index  ) == 'E' &&
                                    code.charAt(index+1) == 'N' &&
                                    code.charAt(index+2) == 'D' &&
                                    code.charAt(index+3) == '_')
                                {
                                        if(code.charAt(index+4) == 'I' &&
                                           code.charAt(index+5) == 'F')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_IF index: ", tmp.toString(), "\n");
                                                if_map.put(new Integer(if_stack.pop()), tmp);
                                                index += 6;
                                        }
                                        else if(code.charAt(index+4 ) == 'P' &&
                                                code.charAt(index+5 ) == 'R' &&
                                                code.charAt(index+6 ) == 'O' &&
                                                code.charAt(index+7 ) == 'G' &&
                                                code.charAt(index+8 ) == 'R' &&
                                                code.charAt(index+9 ) == 'A' &&
                                                code.charAt(index+10) == 'M')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_PROGRAM index: ", tmp.toString(), "\n");
                                                program_map.put(new Integer(program_stack.pop()), tmp);
                                                context = "";
                                                context_type = "";
                                                index += 11;
                                        }
                                        else if(code.charAt(index+4 ) == 'F' && // FUNCTION
                                                code.charAt(index+5 ) == 'U' &&
                                                code.charAt(index+6 ) == 'N' &&
                                                code.charAt(index+7 ) == 'C' &&
                                                code.charAt(index+8 ) == 'T' &&
                                                code.charAt(index+9 ) == 'I' &&
                                                code.charAt(index+10) == 'O' &&
                                                code.charAt(index+11) == 'N')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_FUNCTION index: ", tmp.toString(), "\n");
                                                function_map.put(new Integer(function_stack.pop()), tmp);
                                                context = "";
                                                context_type = "";
                                                index += 12;
                                        }
                                        else if(code.charAt(index+4 ) == 'F' && // FUNCTION_BLOCK
                                                code.charAt(index+5 ) == 'U' &&
                                                code.charAt(index+6 ) == 'N' &&
                                                code.charAt(index+7 ) == 'C' &&
                                                code.charAt(index+8 ) == 'T' &&
                                                code.charAt(index+9 ) == 'I' &&
                                                code.charAt(index+10) == 'O' &&
                                                code.charAt(index+11) == 'N' &&
                                                code.charAt(index+12) == '_' &&
                                                code.charAt(index+13) == 'B' &&
                                                code.charAt(index+14) == 'L' &&
                                                code.charAt(index+15) == 'O' &&
                                                code.charAt(index+16) == 'C' &&
                                                code.charAt(index+17) == 'K')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_FUNCTION_BLOCK index: ", tmp.toString(), "\n");
                                                function_block_map.put(new Integer(function_block_stack.pop()), tmp);
                                                context = "";
                                                context_type = "";
                                                index += 18;
                                        }
                                        else if(code.charAt(index+4) == 'F' && //FOR
                                                code.charAt(index+5) == 'O' &&
                                                code.charAt(index+6) == 'R')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_FOR index: ", tmp.toString(), "\n");
                                                for_map.put(new Integer(for_stack.pop()), tmp);
                                                index += 7;
                                        }
                                        else if(code.charAt(index+4) == 'W' && //WHILE
                                                code.charAt(index+5) == 'H' &&
                                                code.charAt(index+6) == 'I' &&
                                                code.charAt(index+7) == 'L' &&
                                                code.charAt(index+8) == 'E')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_WHILE index: ", tmp.toString(), "\n");
                                                while_map.put(new Integer(while_stack.pop()), tmp);
                                                index += 9;
                                        }
                                        else if(code.charAt(index+4) == 'R' && //REPEAT
                                                code.charAt(index+5) == 'E' &&
                                                code.charAt(index+6) == 'P' &&
                                                code.charAt(index+7) == 'E' &&
                                                code.charAt(index+8) == 'A' &&
                                                code.charAt(index+9) == 'T')
                                        {
                                                Integer tmp = new Integer(index);
                                                if(log_level >= 4) log.log(4, key, "\tEND_REPEAT index: ", tmp.toString(), "\n");
                                                repeat_map.put(new Integer(repeat_stack.pop()), tmp);
                                                index += 10;
                                        }


                                }
                                /*normal checks*/
                                else if(code.charAt(index  ) == 'I' &&
                                        code.charAt(index+1) == 'F')
                                {
                                        if (code.charAt(index-1) == 'E' &&
                                            code.charAt(index-2) == 'S' &&
                                            code.charAt(index-3) == 'L' &&
                                            code.charAt(index-4) == 'E')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tELSEIF index: ", new Integer(index).toString(), "\n");
                                                index += 2;
                                        } else {
                                                if(log_level >= 4) log.log(4, key, "\tIF index: ", new Integer(index).toString(), "\n");
                                                if_stack.push(index);
                                                index += 2;
                                        }
                                }
                                else if(code.charAt(index  ) == 'P' &&
                                        code.charAt(index+1) == 'R' &&
                                        code.charAt(index+2) == 'O' &&
                                        code.charAt(index+3) == 'G' &&
                                        code.charAt(index+4) == 'R' &&
                                        code.charAt(index+5) == 'A' &&
                                        code.charAt(index+6) == 'M')
                                {
                                        Integer tmp = new Integer(index);
                                        if(log_level >= 4) log.log(4, key, "\tPROGRAM index: ", tmp.toString(), "\n");
                                        program_stack.push(index);
                                        state = analyse_states.find_context;
                                        context_type = "PROGRAM";
                                        temp_start = tmp;
                                        index += 7;
                                }
                                else if(code.charAt(index  ) == 'F' && //FUNCTION
                                        code.charAt(index+1) == 'U' &&
                                        code.charAt(index+2) == 'N' &&
                                        code.charAt(index+3) == 'C' &&
                                        code.charAt(index+4) == 'T' &&
                                        code.charAt(index+5) == 'I' &&
                                        code.charAt(index+6) == 'O' &&
                                        code.charAt(index+7) == 'N')
                                {
                                        Integer tmp = new Integer(index);
                                        if(log_level >= 4) log.log(4, key, "\tFUNCTION index: ", tmp.toString(), "\n");
                                        function_stack.push(index);
                                        state = analyse_states.find_context;
                                        context_type = "FUNCTION";
                                        temp_start = tmp;
                                        index += 8;
                                }
                                else if(code.charAt(index   ) == 'F' && //FUNCTION_BLOCK
                                        code.charAt(index+1 ) == 'U' &&
                                        code.charAt(index+2 ) == 'N' &&
                                        code.charAt(index+3 ) == 'C' &&
                                        code.charAt(index+4 ) == 'T' &&
                                        code.charAt(index+5 ) == 'I' &&
                                        code.charAt(index+6 ) == 'O' &&
                                        code.charAt(index+7 ) == 'N' &&
                                        code.charAt(index+8 ) == '_' &&
                                        code.charAt(index+9 ) == 'B' &&
                                        code.charAt(index+10) == 'L' &&
                                        code.charAt(index+11) == 'O' &&
                                        code.charAt(index+12) == 'C' &&
                                        code.charAt(index+13) == 'K')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tFUNCTION_BLOCK index: ", new Integer(index).toString(), "\n");
                                        function_block_stack.push(index);
                                        state = analyse_states.find_context;
                                        context_type = "FUNCTION_BLOCK";
                                        index += 14;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR_GLOBAL
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R' &&
                                        code.charAt(index+3) == '_' &&
                                        code.charAt(index+4) == 'G' &&
                                        code.charAt(index+5) == 'L' &&
                                        code.charAt(index+6) == 'O' &&
                                        code.charAt(index+7) == 'B' &&
                                        code.charAt(index+8) == 'A' &&
                                        code.charAt(index+9) == 'L')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR_BLOCK index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 10;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR_CONFIG
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R' &&
                                        code.charAt(index+3) == '_' &&
                                        code.charAt(index+4) == 'C' &&
                                        code.charAt(index+5) == 'O' &&
                                        code.charAt(index+6) == 'N' &&
                                        code.charAt(index+7) == 'F' &&
                                        code.charAt(index+8) == 'I' &&
                                        code.charAt(index+9) == 'G')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR_CONFIG index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 10;
                                }
                                else if(code.charAt(index  ) == 'C' && //CASE
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'S' &&
                                        code.charAt(index+3) == 'E')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tCASE index: ", new Integer(index).toString(), "\n");
                                        state = analyse_states.case_handling;
                                        case_stack.push(index);
                                        index += 4;
                                }
                                else if(code.charAt(index  ) == 'F' && //FOR
                                        code.charAt(index+1) == 'O' &&
                                        code.charAt(index+2) == 'R')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tFOR index: ", new Integer(index).toString(), "\n");
                                        for_stack.push(index);
                                        index += 3;
                                }
                                else if(code.charAt(index  ) == 'W' && //WHILE
                                        code.charAt(index+1) == 'H' &&
                                        code.charAt(index+2) == 'I' &&
                                        code.charAt(index+3) == 'L' &&
                                        code.charAt(index+4) == 'E')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tWHILE index: ", new Integer(index).toString(), "\n");
                                        while_stack.push(index);
                                        index += 5;
                                }
                                else if(code.charAt(index  ) == 'R' && //REPEAT
                                        code.charAt(index+1) == 'E' &&
                                        code.charAt(index+2) == 'P' &&
                                        code.charAt(index+3) == 'E' &&
                                        code.charAt(index+4) == 'A' &&
                                        code.charAt(index+5) == 'T')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tREPEAT index: ", new Integer(index).toString(), "\n");
                                        repeat_stack.push(index);
                                        index += 6;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR_INPUT
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R' &&
                                        code.charAt(index+3) == '_' &&
                                        code.charAt(index+4) == 'I' &&
                                        code.charAt(index+5) == 'N' &&
                                        code.charAt(index+6) == 'P' &&
                                        code.charAt(index+7) == 'U' &&
                                        code.charAt(index+8) == 'T')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR_INPUT index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 9;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR_OUTPUT
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R' &&
                                        code.charAt(index+3) == '_' &&
                                        code.charAt(index+4) == 'O' &&
                                        code.charAt(index+5) == 'U' &&
                                        code.charAt(index+6) == 'T' &&
                                        code.charAt(index+7) == 'P' &&
                                        code.charAt(index+8) == 'U' &&
                                        code.charAt(index+9) == 'T')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR_OUTPUT index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 10;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR_GLOBAL
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R' &&
                                        code.charAt(index+3) == '_' &&
                                        code.charAt(index+4) == 'I' &&
                                        code.charAt(index+5) == 'N' &&
                                        code.charAt(index+6) == '_' &&
                                        code.charAt(index+7) == 'O' &&
                                        code.charAt(index+8) == 'U' &&
                                        code.charAt(index+9) == 'T')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR_IN_OUT index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 10;
                                }
                                else if(code.charAt(index  ) == 'V' && //VAR
                                        code.charAt(index+1) == 'A' &&
                                        code.charAt(index+2) == 'R')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tVAR index: ", new Integer(index).toString(), "\n");
                                        var_stack.push(index);
                                        state = analyse_states.var_handling;
                                        index += 3;
                                } else {
                                    index += 1;
                                }
                                break;
                        case find_context:
                                if(code.charAt(index  ) == 'V' &&
                                   code.charAt(index+1) == 'A' &&
                                   code.charAt(index+2) == 'R')
                                {
                                        if (context_type.equals("PROGRAM")){
                                                if(log_level >= 4) log.log(4, key, "\tPROGRAM found index", temp_start.toString(), ", NAME", context, "\n");
                                                program_startpoint.put(context, temp_start);
                                                temp_start = null;
                                        } else if (context_type.equals("FUNCTION")) {
                                                if(log_level >= 4) log.log(4, key, "\tFUNCTION found index", temp_start.toString(), ", NAME", context, "\n");
                                                function_startpoint.put(context, temp_start);
                                                temp_start = null;
                                        }
                                        state = analyse_states.mainloop;
                                } else {
                                        context += code.charAt(index);
                                        index += 1;
                                }
                                break;
                        case var_handling:
                                if(code.charAt(index  ) == 'E' &&
                                   code.charAt(index+1) == 'N' &&
                                   code.charAt(index+2) == 'D' &&
                                   code.charAt(index+3) == '_')
                                {
                                        if(code.charAt(index+4 ) == 'V' && //VAR_INPUT
                                           code.charAt(index+5 ) == 'A' &&
                                           code.charAt(index+6 ) == 'R' &&
                                           code.charAt(index+7 ) == '_' &&
                                           code.charAt(index+8 ) == 'I' &&
                                           code.charAt(index+9 ) == 'N' &&
                                           code.charAt(index+10) == 'P' &&
                                           code.charAt(index+11) == 'U' &&
                                           code.charAt(index+12) == 'T')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR_INPUT index: ", new Integer(index).toString(), "\n");
                                                var_map.put(new Integer(var_stack.pop()), new Integer(index+12));
                                                process_vars(context, var_block, "VAR_INPUT", context_type);
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 13;
                                        }
                                        else if(code.charAt(index+4 ) == 'V' && //VAR_OUTPUT
                                                code.charAt(index+5 ) == 'A' &&
                                                code.charAt(index+6 ) == 'R' &&
                                                code.charAt(index+7 ) == '_' &&
                                                code.charAt(index+8 ) == 'O' &&
                                                code.charAt(index+9 ) == 'U' &&
                                                code.charAt(index+10) == 'T' &&
                                                code.charAt(index+11) == 'P' &&
                                                code.charAt(index+12) == 'U' &&
                                                code.charAt(index+13) == 'T')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR_OUTPUT index: ", new Integer(index).toString(), "\n");
                                                var_map.put(new Integer(var_stack.pop()), new Integer(index+13));
                                                process_vars(context, var_block, "VAR_OUTPUR", context_type);
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 14;
                                        }
                                        else if(code.charAt(index+4 ) == 'V' && //VAR_IN_OUT
                                                code.charAt(index+5 ) == 'A' &&
                                                code.charAt(index+6 ) == 'R' &&
                                                code.charAt(index+7 ) == '_' &&
                                                code.charAt(index+8 ) == 'I' &&
                                                code.charAt(index+9 ) == 'N' &&
                                                code.charAt(index+10) == '_' &&
                                                code.charAt(index+11) == 'O' &&
                                                code.charAt(index+12) == 'U' &&
                                                code.charAt(index+13) == 'T')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR_IN_OUT index: ", new Integer(index).toString(), "\n");
                                                var_map.put(new Integer(var_stack.pop()), new Integer(index+13));
                                                process_vars(context, var_block, "VAR_IN_OUT", context_type);
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 14;
                                        }

                                        else if(code.charAt(index+4 ) == 'V' && //VAR_GLOBAL
                                                code.charAt(index+5 ) == 'A' &&
                                                code.charAt(index+6 ) == 'R' &&
                                                code.charAt(index+7 ) == '_' &&
                                                code.charAt(index+8 ) == 'G' &&
                                                code.charAt(index+9 ) == 'L' &&
                                                code.charAt(index+10) == 'O' &&
                                                code.charAt(index+11) == 'B' &&
                                                code.charAt(index+12) == 'A' &&
                                                code.charAt(index+13) == 'L')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR_GLOBAL index: ", new Integer(index).toString(), "\n");
                                                var_global_start = var_stack.pop();
                                                var_map.put(new Integer(var_global_start), new Integer(index+13));
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 14;
                                        }
                                        else if(code.charAt(index+4 ) == 'V' && //VAR_CONFIG
                                                code.charAt(index+5 ) == 'A' &&
                                                code.charAt(index+6 ) == 'R' &&
                                                code.charAt(index+7 ) == '_' &&
                                                code.charAt(index+8 ) == 'C' &&
                                                code.charAt(index+9 ) == 'O' &&
                                                code.charAt(index+10) == 'N' &&
                                                code.charAt(index+11) == 'F' &&
                                                code.charAt(index+12) == 'I' &&
                                                code.charAt(index+13) == 'G')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR_CONFIG index: ", new Integer(index).toString(), "\n");
                                                var_config_start = var_stack.pop();
                                                var_map.put(new Integer(var_config_start), new Integer(index+13));
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 14;
                                        }
                                        else if(code.charAt(index+4) == 'V' && //VAR
                                                code.charAt(index+5) == 'A' &&
                                                code.charAt(index+6) == 'R')
                                        {
                                                if(log_level >= 4) log.log(4, key, "\tEND_VAR index: ", new Integer(index).toString(), "\n");
                                                var_map.put(new Integer(var_stack.pop()), new Integer(index+6)); //BLUB
                                                process_vars(context, var_block, "VAR", context_type);
                                                var_block = "";
                                                state = analyse_states.mainloop;
                                                index += 7;
                                        }
                                        else
                                        {
                                                        log.log(0, key,
                                                                "\n\n",
                                                                "ERROR: END_* in VAR_* block ....\n",
                                                                "DETAILED ERROR: END_ block in VAR block detected\n",
                                                                "DUMP: ", "\n"
                                                                );
                                                        log.kill();
                                                        System.exit(1);
                                        }
                                } else {
                                        var_block += code.charAt(index);
                                        index += 1;
                                }
                                break;
                        case case_handling:
                                if(code.charAt(index  ) == 'E' &&
                                   code.charAt(index+1) == 'N' &&
                                   code.charAt(index+2) == 'D' &&
                                   code.charAt(index+3) == '_' &&
                                   code.charAt(index+4) == 'C' && //CASE
                                   code.charAt(index+5) == 'A' &&
                                   code.charAt(index+6) == 'S' &&
                                   code.charAt(index+7) == 'E')
                                {
                                        if(log_level >= 4) log.log(4, key, "\tEND_CASE index: ", new Integer(index).toString(), "\n");
                                        int case_start = case_stack.pop();
                                        case_map.put(new Integer(case_start), new CaseHandling(code.substring(case_start, index+8), case_start));
                                        index += 8;
                                        state = analyse_states.mainloop;
                                } else {
                                    index += 1;
                                }
                                break;
                        case ignore_string:
                                if(code.charAt(index  ) == '\\' &&
                                   code.charAt(index+1) == '"')
                                {
                                        index += 2;
                                }
                                else if(code.charAt(index) == '"')
                                {
                                        state = analyse_states.mainloop;
                                        index += 1;
                                }
                                else {
                                        index += 1;
                                }
                                break;
                        }
                }
                if (var_global_start != -1)
                        process_vars("GLOBAL", code.substring(var_global_start, var_map.get(new Integer(var_global_start)).intValue()), "VAR_GLOBAL", "GLOBAL");
                /*last but not least, handle the var_config block*/
                if (var_config_start != -1)
                        process_var_config(code.substring(var_config_start, var_map.get(new Integer(var_config_start)).intValue()));
        }


        /**
         * process_var_config loops through a var_config block with a statemachine
         * there it will find all the needed informations and will create
         * the device inforamtions needed for the communication
         * @param var_block the varblock as a String
         */
        private void
        process_var_config(String var_block)
        {
                if(log_level >= 4) log.log(4, key, "process VAR_CONFIG", "\n");

                config_states analyse_states;
                analyse_states = config_states.start;
                //int count;

                String band = "";

                String context   = "";
                String var_name  = "";
                char   in_or_out = ' ';
                //char   type      = ' ';
                String address   = "";
                char   pin       = '9';
                //String var_type  = "";
                MappedByte mbyte;

                for (int index = 0; index < var_block.length();){
                        switch(analyse_states){
                        case start:
                                if(var_block.charAt(index  ) == 'V' &&
                                   var_block.charAt(index+1) == 'A' &&
                                   var_block.charAt(index+2) == 'R' &&
                                   var_block.charAt(index+3) == '_' &&
                                   var_block.charAt(index+4) == 'C' &&
                                   var_block.charAt(index+5) == 'O' &&
                                   var_block.charAt(index+6) == 'N' &&
                                   var_block.charAt(index+7) == 'F' &&
                                   var_block.charAt(index+8) == 'I' &&
                                   var_block.charAt(index+9) == 'G')
                                {
                                        index += 10;
                                        analyse_states = config_states.find_context;
                                } else {
                                        // hey man, you fucked it up .....
                                }
                                break;
                        case find_context:
                                if(var_block.charAt(index) == '.'){
                                        context = band;
                                        index += 1;
                                        band = "";
                                        analyse_states = config_states.find_var_name;
                                } else {
                                        band = band + var_block.charAt(index);
                                        index += 1;
                                }
                                break;
                        case find_var_name:
                                if(var_block.charAt(index  ) == 'A' &&
                                   var_block.charAt(index+1) == 'T')
                                {
                                        var_name = band;
                                        index += 2;
                                        band = "";
                                        analyse_states = config_states.find_AT_percent;
                                } else {
                                        band = band + var_block.charAt(index);
                                        index += 1;
                                }
                                break;
                        case find_AT_percent:
                                if(var_block.charAt(index) == '%')
                                {
                                        index += 1;
                                        analyse_states = config_states.find_IN_or_OUT;
                                } else {
                                                log.log(0, key,
                                                        "\n\n",
                                                        "ERROR: % expected\n",
                                                        "DETAILED ERROR: error in VAR_CONFIG @", context, ".", var_name, "\n",
                                                        "DUMP: ", "\n"
                                                        );
                                                log.kill();
                                                System.exit(1);
                                }
                                break;
                        case find_IN_or_OUT:
                                if(var_block.charAt(index) == 'I' ||
                                   var_block.charAt(index) == 'Q')
                                {
                                        in_or_out = var_block.charAt(index);
                                        index += 1;
                                        analyse_states = config_states.find_type;
                                } else {
                                        log.log(0, key,
                                                "\n\n",
                                                "ERROR: I or Q expected\n",
                                                "DETAILED ERROR: error in VAR_CONFIG @", context, ".", var_name, "\n",
                                                "DUMP: ", "\n"
                                                );
                                        log.kill();
                                        System.exit(1);
                                }
                                break;
                        case find_type:
                                if(var_block.charAt(index) == 'X' ||
                                   var_block.charAt(index) == 'B')
                                {
                                        //type = var_block.charAt(index);
                                        index += 1;
                                        analyse_states = config_states.find_address;
                                } else {
                                        log.log(0, key,
                                                "\n\n",
                                                "ERROR: X or B expected\n",
                                                "DETAILED ERROR: error in VAR_CONFIG @", context, ".", var_name, "\n",
                                                "DUMP: ", "\n"
                                                );
                                        log.kill();
                                        System.exit(1);
                                }
                                break;
                        case find_address:
                                if(var_block.charAt(index) == '.'){
                                        address = band;
                                        band = "";
                                        pin = var_block.charAt(index+1);
                                        index += 2;
                                        analyse_states = config_states.find_var_type;
                                } else if (var_block.charAt(index) == ':') {
                                        address = band;
                                        band = "";
                                        index += 1;
                                        analyse_states = config_states.find_var_type;
                                } else if (var_block.charAt(index) == '0' ||
                                           var_block.charAt(index) == '1' ||
                                           var_block.charAt(index) == '2' ||
                                           var_block.charAt(index) == '3' ||
                                           var_block.charAt(index) == '4' ||
                                           var_block.charAt(index) == '5' ||
                                           var_block.charAt(index) == '6' ||
                                           var_block.charAt(index) == '7' ||
                                           var_block.charAt(index) == '8' ||
                                           var_block.charAt(index) == '9')
                                {
                                        band = band + var_block.charAt(index);
                                        index += 1;
                                } else {
                                        log.log(0, key,
                                                "\n\n",
                                                "ERROR: address format error\n",
                                                "DETAILED ERROR: error in VAR_CONFIG @", context, ".", var_name, "\n",
                                                "DUMP: ", "\n"
                                                );
                                        log.kill();
                                        System.exit(1);
                                }
                                break;
                        case find_var_type:
                                if(var_block.charAt(index) == ':'){
                                        index += 1;
                                } else if (var_block.charAt(index) == ';') {
                                        band = "";
                                        index += 1;
                                        if (pin != '9') {
                                                mbyte = new MappedByte(address);
                                                mbyte.set_bit(""+pin, Boolean.parseBoolean(context_varname_var.get(context).get(var_name).get_value()));
                                                context_varname_var.get(context).get(var_name).set_mapped_byte(mbyte, ""+pin);
                                                if (in_or_out == 'I'){
                                                        address_mappedbyte_in.put(address, mbyte);
                                                }else{
                                                        address_mappedbyte_out.put(address, mbyte);
                                                }
                                                pin = '9';
                                                context = "";
                                                var_name = "";
                                                address = "";
                                                in_or_out = '0';
                                        } else {
                                                mbyte = new MappedByte(address);
                                                mbyte.set_byte(Byte.parseByte(context_varname_var.get(context).get(var_name).get_value()));
                                                context_varname_var.get(context).get(var_name).set_mapped_byte(mbyte);
                                                if (in_or_out == 'I'){
                                                        address_mappedbyte_in.put(address, mbyte);
                                                        IO_Package iop = context_varname_var.get(context).get(var_name).get_IOPackage();
                                                        iop.set_polling_true();
                                                        com_channel_queue.offer(iop);
                                                } else
                                                        address_mappedbyte_out.put(address, mbyte);
                                                context = "";
                                                var_name = "";
                                                address = "";
                                                in_or_out = '0';
                                        }
                                        analyse_states = config_states.find_context;
                                } else {
                                        band = band + var_block.charAt(index);
                                        index += 1;
                                }
                                break;
                        }
                }
        }

        /**
         * process_vars loops through a var_block with a statemachine
         * there it will find all the needed informations and will create
         * the variables in the structures
         * @param context is the current context the variables are in
         * @param var_block the varblock as a String
         * @param var_type the type of the varblock
         * @param context_type the type of the context the varblock is in
         */
        private void
        process_vars(String context, String var_block, String var_type, String context_type)
        {
                if(log_level >= 4) log.log(4, key, "process_vars() called", "\n");

                if(!context_varname_var.containsKey(context))
                    context_varname_var.put(context, new HashMap<String,Variable>());

                var_states state = var_states.find_semicollon;
                int index = 0;
                int semicollon_pos = 0;

                String band  = "";
                String type  = "";
                ArrayList<String> names = new ArrayList<>();
                String value = "";
                Variable tmp;

                for (;index < var_block.length() && index > -1;) {
                        switch (state){
                        case find_semicollon:
                                if(!(var_block.charAt(index) == ';')) {
                                        index += 1;
                                } else {
                                        semicollon_pos = index;
                                        state = var_states.type_or_value_or_name;
                                        index -= 1;
                                }
                                break;
                        case type_or_value_or_name:
                                if(var_block.charAt(index  ) == '=' && // :=
                                   var_block.charAt(index-1) == ':')
                                {
                                        index -= 2;
                                        state = var_states.get_value;
                                }
                                else if((byte)var_block.charAt(index) <= 90 &&
                                        (byte)var_block.charAt(index) >= 65 &&
                                        var_block.charAt(index-1) == ':')
                                {
                                        band = var_block.charAt(index) + band;
                                        index -= 2;
                                        state = var_states.get_type;
                                } else if(var_block.charAt(index) == ';' ||
                                        index == 0) {
                                        if(index == 0) {
                                                band = var_block.charAt(index) + band;
                                        }
                                        state = var_states.get_last_name;
                                } else if(var_block.charAt(index) == ',') {
                                        index -= 1;
                                        state = var_states.get_name;
                                } else {
                                        band = var_block.charAt(index) + band;
                                        index -= 1;
                                }
                                break;
                        case get_value:
                                if(log_level >= 4) log.log(4, key, "\tState: get_value", "\n");
                                value = band;
                                band = "";
                                state = var_states.type_or_value_or_name;
                                if(log_level >= 4) log.log(4, key, "\tvalue: ", value, "\n");
                                break;
                        case get_type:
                                if(log_level >= 4) log.log(4, key, "\tState: get_type", "\n");
                                type = band;
                                band = "";
                                state = var_states.type_or_value_or_name;
                                if(log_level >= 4) log.log(4, key, "\ttype: ", type, "\n");
                                break;
                        case get_name:
                                if(log_level >= 4) log.log(4, key, "\tState: get_name", "\n");
                                names.add(band);
                                state = var_states.type_or_value_or_name;
                                if(log_level >= 4) log.log(4, key, "\tname: ", band, "\n");
                                band = "";
                                break;
                        case get_last_name:
                                if(log_level >= 4) log.log(4, key, "\tState: get_last_name", "\n");
                                names.add(band);
                                state = var_states.write_to_structure;
                                if(log_level >= 4) log.log(4, key, "\tname: ", band, "\n");
                                band = "";
                                break;
                        case write_to_structure:
                                if(log_level >= 4) log.log(4, key, "\tState: write_to_structure", "\n");
                                if(!names.isEmpty() &&
                                   type  != "" &&
                                   value != "")
                                {
                                        if (context_type == "GLOBAL"){
                                                if(log_level >= 4) log.log(4, key, "\tSet in GLOBAL", "\n");
                                                for (String name : names){
                                                tmp = new Variable(types, context, id, type, name, value, context_type, var_type);
                                                        for (HashMap<String,Variable> name_variable : context_varname_var.values()) {
                                                                name_variable.put(name, tmp);
                                                        }
                                                }
                                        } else {
                                                if(log_level >= 4) log.log(4, key, "\tSet int ", context, "\n");
                                                for (String name : names){
                                                        context_varname_var.get(context).put(name, new Variable(types, context, id, type, name, value, context_type, var_type));
                                                }
                                        }
                                        names  = new ArrayList<>();
                                        type  = "";
                                        value = "";
                                        id += 1;
                                        index = semicollon_pos+1;
                                        state = var_states.find_semicollon;
                                }
                                else if(!names.isEmpty() &&
                                        type  != "")
                                {
                                        if (context_type == "GLOBAL"){
                                                if(log_level >= 4) log.log(4, key, "\tSet in GLOBAL", "\n");
                                                for (String name : names){
                                                        tmp = new Variable(types, context, id, type, name, context_type, var_type);
                                                        for (HashMap<String,Variable> name_variable : context_varname_var.values()) {
                                                                name_variable.put(name, tmp);
                                                        }
                                                }
                                        } else {
                                                if(log_level >= 4) log.log(4, key, "\tSet in ", context, "\n");
                                                for (String name : names){
                                                        context_varname_var.get(context).put(name, new Variable(types, context, id, type, name, context_type, var_type));
                                                }
                                        }
                                        names  = new ArrayList<>();
                                        type  = "";
                                        value = "";
                                        id += 1;
                                        index = semicollon_pos+1;
                                        state = var_states.find_semicollon;
                                }
                                else if(!names.isEmpty() &&
                                        value != "")
                                {
                                        if (context_type == "GLOBAL"){
                                                if(log_level >= 4) log.log(4, key, "\tSet in GLOBAL", "\n");
                                                for (String name : names) {
                                                        tmp = new Variable(types, context, id, "INT", name, value, context_type, var_type);
                                                        for (HashMap<String,Variable> name_variable : context_varname_var.values()) {
                                                                name_variable.put(name, tmp);
                                                        }
                                                }
                                        } else if (var_type == "CHANGE"){
                                                if(log_level >= 4) log.log(4, key, "\tSet in CHANGE", "\n");
                                                for (String name : names) {
                                                        context_varname_var.get(context).get(name).set_value(value);
                                                        tmp = context_varname_var.get(context).get(name);
                                                        if(tmp.has_mapping()){
                                                                com_channel_queue.offer(tmp.get_IOPackage());
                                                        }
                                                }
                                        } else {
                                                if(log_level >= 4) log.log(4, key, "\tSet in ", context, "\n");
                                                for (String name : names){
                                                        context_varname_var.get(context).put(name, new Variable(types, context, id, "INT", name, value, context_type, var_type));
                                                }
                                        }
                                        names  = new ArrayList<>();
                                        type  = "";
                                        value = "";
                                        id += 1;
                                        index = semicollon_pos+1;
                                        state = var_states.find_semicollon;
                                } else {
                                        // fucked it up
                                }
                                break;
                        }
                }
        }

        //COMMON FUNCTIONS
        /**
	 * generateSymbolsList creates a list with all the Symbols sorted from long
	 * to short
	 * This function is private
	 */
	private void
	generate_symbols_list()
	{
		symbolnames = new ArrayList<>();

		if(log_level >= 4) log.log(4, key, "generate_symbols_list called.", "\n");
		for (Map.Entry<String, HashMap<String, Variable>> percontext : context_varname_var.entrySet()) {
			for (Map.Entry<String, Variable> value : percontext.getValue().entrySet()) {
				symbolnames.add(value.getKey());
			}
		}
                symbolnames.add("true");
                symbolnames.add("TRUE");
                symbolnames.add("false");
                symbolnames.add("FALSE");
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

		if(log_level >= 4) log.log(4, key, "symbols list: ", Arrays.toString(symbolnames.toArray()), "\n");
	}

        /**
	 * replaceVars replaces all variable names in a given string with its values
	 * @param input string with variable names in it
	 * @param context the context in which this variables should be
	 * @return the input string with all replaced variables
	 */
	public String
	replace_vars(String input, String context)
	{
		if(log_level >= 4) log.log(4, key, "replace_vars called.", "\n");
		if(log_level >= 4) log.log(4, key, "input: ", input, "\n");
		if(log_level >= 4) log.log(4, key, "context: ", context, "\n");

		String tmp = null;
                String rv = null;  // return value

                for (String item:symbolnames) {
                        if(context_varname_var.get(context).containsKey(item)){
                                if (tmp == null) {
                                        rv = context_varname_var.get(context).get(item).get_value();
                                        tmp = input.replaceAll(item, rv);
                                        tmp = tmp.replaceAll("false", "!!!");
                                        tmp = tmp.replaceAll("FALSE", "!!!");
                                        tmp = tmp.replaceAll("true", "###");
                                        tmp = tmp.replaceAll("TRUE", "###");
                                } else {
                                        rv = context_varname_var.get(context).get(item).get_value();
                                        tmp = tmp.replaceAll(item, rv);
                                        tmp = tmp.replaceAll("false", "!!!");
                                        tmp = tmp.replaceAll("FALSE", "!!!");
                                        tmp = tmp.replaceAll("true", "###");
                                        tmp = tmp.replaceAll("TRUE", "###");
                                }
                        }
                }

                tmp = tmp.replaceAll("!!!", "false");
                tmp = tmp.replaceAll("###", "true");

		if(log_level >= 4) log.log(4, key, "return value: ", tmp, "\n");

		return tmp;
	}

        /**
	 * set_value accepts whole variable lines like: var,var1,var2:=5; OR
	 * var3:=3.45; and sets the new value in the right containers depending on
	 * there context.
	 * @param input String with variable line.
	 * @param context String representing
	 * @throws java.lang.Exception the context
	 */
	public void
	set_value(String input, String context) throws Exception
	{
		if(log_level >= 4) log.log(4, key, "set_value called.", "\n");
		if(log_level >= 4) log.log(4, key, "input: ", input, "\n");
		if(log_level >= 4) log.log(4, key, "context: ", context, "\n");

		process_vars(context, input, "CHANGE", "CHANGE");
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
		if(log_level >= 4) log.log(4, key, "add_var called.", "\n");
		if(log_level >= 4) log.log(4, key, "input: ", input, "\n");
		if(log_level >= 4) log.log(4, key, "context: ", context, "\n");

                process_vars(context, input, "RUNTIME", "RUNTIME");

		generate_symbols_list();
	}

        /**
         * get_com_channel_queue returns the linkedblockingqueue for the IOInterface
	 * communikation.
	 * @return LinkedBlockingQueue with IO_Package Type
	 * @see LinkedBlockingQueue
	 * @see IO_Package
         */
	public LinkedBlockingQueue<IO_Package>
	get_com_channel_queue()
	{
		if(log_level >= 4) log.log(4, key, "get_com_channel_queue called.", "\n");
		return com_channel_queue;
	}

        /**
         * update_device can update the informations acording to the byte address
         * @param byte_address the byte address as a String
         * @param value the byte to set
         */
        public void
	update_device(String byte_address, byte value)
	{
                address_mappedbyte_in.get(byte_address).set_byte(value);
	}

        /**
	 * lookup function, throw in the index of a if and geht the index of a end_if
	 * @param if_open index of an if
	 * @return int
	 */
	public int
	get_end_if(int if_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_if called.", "\n");
		int local_tmp = if_map.get(new Integer(if_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_if: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
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
	 * @throws java.lang.Exception
	 */
	public Integer[]
	get_case_coordinates(int caseopen, int value) throws Exception
	{
		if(log_level >= 4) log.log(4, key, "get_case_coordinates called.", "\n");
                Integer[] local_tmp = case_map.get(new Integer(caseopen)).get_case_collon(value);
                if(log_level >= 4) log.log(4, key, "case coordinates: ", Arrays.toString(local_tmp), "\n");
		return local_tmp;
	}

	/**
	 * lookup function, throw in the index of a case and geht the index of an end_case
	 * @param caseopen index of the "C" od CASE
	 * @return int
	 */
	public int
	get_end_case(int caseopen)
	{
		if(log_level >= 4) log.log(4, key, "get_end_case called.", "\n");
                int local_tmp = case_map.get(new Integer(caseopen)).get_end_case();
		if(log_level >= 4) log.log(4, key, "end_case: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a var and geht the index of a end_var
	 * @param var_open index of a var
	 * @return int
	 */
	public int
	get_end_var(int var_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_var called.", "\n");
		int local_tmp = var_map.get(new Integer(var_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_var: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a program and geht the index of a end_program
	 * @param program_open index of a program
	 * @return int
	 */
	public int
	get_end_program(int program_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_program called.", "\n");
		int local_tmp = program_map.get(new Integer(program_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_program: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a function and geht the index of a end_function
	 * @param function_open index of a function
	 * @return int
	 */
	public int
	get_end_function(int function_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_function called.", "\n");
		int local_tmp = function_map.get(new Integer(function_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_function: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a function_block and geht the index of a end_function_block
	 * @param function_block_open index of a function_block
	 * @return int
	 */
	public int
	get_end_function_block(int function_block_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_function_block called.", "\n");
		int local_tmp = function_block_map.get(new Integer(function_block_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_function_block: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a for and geht the index of a end_for
	 * @param for_open index of a for
	 * @return int
	 */
	public int
	get_end_for(int for_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_for called.", "\n");
		int local_tmp = for_map.get(new Integer(for_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_for: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a while and geht the index of a end_while
	 * @param while_open index of a while
	 * @return int
	 */
	public int
	get_end_while(int while_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_while called.", "\n");
		int local_tmp = while_map.get(new Integer(while_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_while: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
	 * lookup function, throw in the index of a repeat and geht the index of a end_repeat
	 * @param repeat_open index of a repeat
	 * @return int
	 */
	public int
	get_end_repeat(int repeat_open)
	{
		if(log_level >= 4) log.log(4, key, "get_end_repeat called.", "\n");
		int local_tmp = repeat_map.get(new Integer(repeat_open)).intValue();
		if(log_level >= 4) log.log(4, key, "end_repeat: ", new Integer(local_tmp).toString(), "\n");
		return local_tmp;
	}

        /**
         * build_function_structure creates a function lookup table
         */
        private void
        build_function_structure()
        {
                if(log_level >= 4) log.log(4, key, "build_function_structure called.", "\n");
                TreeMap<Integer,Variable> temp = new TreeMap<>();
                HashMap<Integer,Variable> helper;
                int count = 0;
                for (String context : function_startpoint.keySet()) {
                        for (Variable variable : context_varname_var.get(context).values()) {
                                if (variable.get_var_type().equals("VAR_INPUT")){
                                        temp.put(new Integer(variable.get_id()), variable);
                                }
                        }

                        for (Variable variable_sorted : temp.values()) {
                                helper = new HashMap<>();
                                helper.put(new Integer(count), variable_sorted);
                                functioname_inputid_var.put(context, helper);
                        }
                        count = 0;
                        temp = new TreeMap<>();
                }
        }

        /**
         * call_function_or_program is the function which processes a function and his parameters
         * it will return the index of the FUNCTION in the code and sets the variable values
         * @param  function_call the function or program name and the parameters as array
         * @return the F in FUNCTION
         */
        public int
        call_function_or_program(String[] function_call)
        {
                if(log_level >= 4) log.log(4, key, "call_function_or_program called.", "\n");
                String[] fun_param;
                String[] fun_param_split;
                boolean  is_program = false;

                if (function_call.length == 2){
                        fun_param = function_call[1].split(",");
                        if(log_level >= 4) log.log(4, key, "\tis function.", "\n");
                } else if (function_call.length > 2) {
                        if(log_level >= 0) log.log(0,key,
                                "\n\n",
                                "ERROR: i expected two params at all",
                                "DETAILED ERROR: i got more then two params",
                                "greetings \"call_function_or_program\"",
                                "DUMP:\n", Arrays.toString(function_call)
                                );
                        log.kill();
                        System.exit(1);
                        return 1;
                } else {
                        if(log_level >= 4) log.log(4, key, "\tis program.", "\n");
                        fun_param = new String[0];
                        is_program = true;
                }

                if (!(fun_param.length == 0)) {
                        for (String para : fun_param){
                                fun_param_split = para.split(":=");
                                if (fun_param_split.length == fun_param.length){
                                        for (int i = 0; i < fun_param_split.length; i++){
                                                functioname_inputid_var.get(function_call[0]).get(new Integer(i)).set_value(fun_param_split[i]);
                                        }
                                } else {
                                        for (int i = 0; i < fun_param_split.length; i++){
                                                context_varname_var.get(function_call[0]).get(fun_param_split[i]).set_value(fun_param_split[i+1]);
                                                i += 1;
                                        }
                                }
                        }
                }

                if (is_program)
                        return program_startpoint.get(function_call[0]).intValue();
                return function_startpoint.get(function_call[0]).intValue();
        }

        /**
         * reset_function will reset the function variables to there default values
         * @param context the contextname as String
         */
        public void
        reset_function(String context)
        {
                if(log_level >= 4) log.log(4, key, "reset_function called for: ", context, "\n");
                for (Variable var : context_varname_var.get(context).values()) {
                        var.set_default_value();
                }
        }
}











