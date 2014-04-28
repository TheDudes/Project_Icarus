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
 *
 * @author: d4ryus - https://github.com/d4ryus/
 * @version 0.2
 */

package Icarus;

import Ninti.*;
import vault.*;
import linc.*;
import parser.*;

import java.io.*;
import java.net.*;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class main
{

    static InfoCollector container;
    static LogWriter log;
    static int verbose_level = 5;

    /* stacks */
    static Stack<String>  context_stack;
    static Stack<Container_LOOP> loop_stack;
    static Stack<Boolean> if_stack;
    static Stack<Integer> if_position_stack;

    public static void main(String[] args) throws Exception 
    {

        String[] path = new String[2];
        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname    = InetAddress.getLocalHost().getHostName();

        switch (hostname)
        {
            case "beelzebub":
                path[0] = "/home/apfel/Documents/StudienProjekt/ StudienProjekt/sp_ 2013_ 10/Project_Icarus/franzke_files/plc_prg.st";
                System.out.println("Hey tux, would you mind taking a look at the code?");
                break;
            case "d4ryus":
                path[0] = "/home/d4ryus/coding/Project_Icarus/Icarus/test.st";
                path[1] = "/home/d4ryus/coding/Project_Icarus/Icarus/logs/";
                break;
            /*
             case "yourhostname":
             path[0] = "/your/path/to/the/st/file";
             path[1] = "/your/path/to/the/logfolder/";
             break;
             */
            default:
                System.out.println("Hey stupid(faggot), take a look in the code!");
                System.exit(0);
        }
        System.out.print("your st file path: " + path[0] + "\n");
        System.out.print("your log file path: " + path[1] + "\n");

        log = new LogWriter(path[1], verbose_level);

        container = new InfoCollector(path);
        String code = container.getAllTheCode().toString();

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine_warmup(engine);

        interpret(code, 0, code.length(), engine);

        log.kill();
    }

    /**
     * will interpret given code from given start till end INDEX.
     * @param string structure text code after parser
     * @param start INDEX where interpreter will start
     * @param end INDEX where interpreter will stop
     * @param engine ScriptEngine which will be used.
     */
    public static void interpret (String string, int start, int end, ScriptEngine engine) throws Exception 
    {

        log.log("interpreter", 4, "started with start: " + start + ", end: " + end);

        log.log("interpreter", 4, "init stacks...");

        context_stack     = new Stack<>();
        loop_stack        = new Stack<>();
        if_stack          = new Stack<>();
        if_position_stack = new Stack<>();

        log.log("interpreter", 4, "init stacks done.");

        StringBuilder code = new StringBuilder(string);

        String context = "";

        for(int INDEX = start; INDEX < end; INDEX++) 
        {

            log.log("interpreter", 4, "for_loop_top, INDEX = " + INDEX);

            if (      (code.charAt(INDEX)     == 'P') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'O') &&
                      (code.charAt(INDEX + 3) == 'G') &&
                      (code.charAt(INDEX + 4) == 'R') &&
                      (code.charAt(INDEX + 5) == 'A') &&
                      (code.charAt(INDEX + 6) == 'M') )
            {
                log.log("interpreter", 4, "found PROGRAM, INDEX = " + INDEX);
                int jump = container.getEndVar(INDEX);
                INDEX += 6;
                context = code.substring(INDEX, get_var(INDEX, code));
                INDEX = jump;
                context_stack.push(context);
            }
            else if ( (code.charAt(INDEX)     == 'I') &&
                      (code.charAt(INDEX + 1) == 'F') )
            {
                log.log("interpreter", 4, "found IF, INDEX = " + INDEX);
                if_position_stack.push(INDEX);
                int then_position = get_then(INDEX, code);
                String condition = code.substring(INDEX + 2, then_position - 1);
                if_stack.push((Boolean)engine.eval(convert_condition(container.replaceVars(condition, context_stack.peek()))));
                if (if_stack.peek()) {
                    INDEX = then_position + 3;
                    continue;
                } else {
                    INDEX = get_next_keyword(INDEX, code) - 1;
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'I') &&
                      (code.charAt(INDEX + 5) == 'F') )
            {
                log.log("interpreter", 4, "found END_IF, INDEX = " + INDEX);
                INDEX += 5;
                if_stack.pop();
                if_position_stack.pop();
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'E') )
            {
                log.log("interpreter", 4, "found ELSE, INDEX = " + INDEX);
                if(if_stack.peek()) {
                    INDEX = container.getEndIf(if_position_stack.pop()) + 5;
                    continue;
                } else {
                        INDEX += 3;
                        continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'F') )
            {
                log.log("interpreter", 4, "found ELSIF, INDEX = " + INDEX);
                if(if_stack.peek()) {
                    INDEX = container.getEndIf(if_position_stack.pop()) + 5;
                    continue;
                } else {
                    INDEX += 5;
                    int then_position = get_then(INDEX, code);
                    String condition = code.substring(INDEX + 2, then_position - 1);
                    if_stack.pop();
                    if_stack.push((Boolean)engine.eval(convert_condition(container.replaceVars(condition, context_stack.peek()))));
                    if (if_stack.peek()) {
                        INDEX = then_position + 3;
                        continue;
                    } else {
                        INDEX = get_next_keyword(INDEX, code) - 1;
                        continue;
                    }
                }
            }
            else if ( (code.charAt(INDEX)     == 'W') &&
                      (code.charAt(INDEX + 1) == 'H') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'L') &&
                      (code.charAt(INDEX + 4) == 'E') )
            {
                log.log("interpreter", 4, "found WHILE, INDEX = " + INDEX);

                Container_LOOP obj = new Container_LOOP();
                obj.type           = 0;
                obj.INDEX          = INDEX;
                obj.do_index       = get_do(INDEX, code);
                obj.end_index      = container.getEndWhile(INDEX) + 8;

                obj.condition      = code.substring(INDEX + 5, obj.do_index - 1);

                loop_stack.push(obj);

                if((Boolean)engine.eval(convert_condition(container.replaceVars(obj.condition, context_stack.peek())))) {
                    INDEX = obj.do_index + 1;
                    continue;
                } else {
                    INDEX = loop_stack.pop().end_index;
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'W') &&
                      (code.charAt(INDEX + 5) == 'H') &&
                      (code.charAt(INDEX + 6) == 'I') &&
                      (code.charAt(INDEX + 7) == 'L') &&
                      (code.charAt(INDEX + 8) == 'E') )
            {
                log.log("interpreter", 4, "found END_WHILE, INDEX = " + INDEX);
                INDEX += 8;
                if((Boolean)engine.eval(convert_condition(container.replaceVars(loop_stack.peek().condition, context_stack.peek())))) {
                    INDEX = loop_stack.peek().do_index + 1;
                    continue;
                } else {
                    INDEX = loop_stack.pop().end_index;
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'F') &&
                      (code.charAt(INDEX + 1) == 'O') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                log.log("interpreter", 4, "found FOR, INDEX = " + INDEX);

                Container_LOOP obj = new Container_LOOP();
                obj.type           = 1;
                obj.INDEX          = INDEX;
                obj.do_index       = get_do(INDEX, code);
                obj.do_index       = container.getEndFor(INDEX);

                int to_position    = get_to(INDEX, code);
                int by_position    = get_by(to_position + 2, code);
                String condition   = code.substring(INDEX + 3, to_position - 1);
                int colon;

                if(condition.contains(":="))
                {
                    colon          = get_colon(condition);
                    obj.name_given = true;
                    obj.name       = condition.substring(0, colon - 1);
                    obj.count      = Integer.parseInt(condition.substring(colon + 2, condition.length()));
                    container.addVar(condition + ";", context);
                } else
                {

                    int count = Integer.parseInt(container.replaceVars(condition, context));
                    obj.count = count;
                    if (condition.equals(Integer.toString(count)))
                    {
                        obj.name_given = false;
                    } else
                    {
                        obj.name_given = true;
                        obj.name       = condition;
                    }
                }

                int do_position;
                if (by_position == -1)
                {
                    do_position = get_do(to_position + 2, code);
                    obj.limit   = Integer.parseInt(container.replaceVars(code.substring(to_position + 2, do_position - 1), context));
                    obj.by      = 1;
                }
                else
                {
                    do_position = get_do(by_position + 2, code);
                    obj.limit   = Integer.parseInt(container.replaceVars(code.substring(to_position + 2, by_position - 1), context));
                    obj.by      = Integer.parseInt(container.replaceVars(code.substring(by_position + 2, do_position - 1), context));
                }
                loop_stack.push(obj);

                if(loop_stack.peek().count >= loop_stack.peek().limit)
                {
                    INDEX = loop_stack.pop().end_index;
                    continue;
                } else
                {
                    INDEX = loop_stack.peek().do_index + 1;
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'F') &&
                      (code.charAt(INDEX + 5) == 'O') &&
                      (code.charAt(INDEX + 6) == 'R') )
            {
                log.log("interpreter", 4, "found END_FOR, INDEX = " + INDEX);
                loop_stack.peek().count += loop_stack.peek().by;

                if(loop_stack.peek().name_given)
                    container.setValue(loop_stack.peek().name + ":=" + loop_stack.peek().count, context);
                if(loop_stack.peek().count >= loop_stack.peek().limit)
                {
                    INDEX = loop_stack.pop().end_index;
                    continue;
                } else
                {
                    INDEX = loop_stack.peek().do_index + 1;
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'R') &&
                      (code.charAt(INDEX + 1) == 'E') &&
                      (code.charAt(INDEX + 2) == 'P') &&
                      (code.charAt(INDEX + 3) == 'E') &&
                      (code.charAt(INDEX + 4) == 'A') &&
                      (code.charAt(INDEX + 5) == 'T') )
            {
                log.log("interpreter", 4, "found REPEAT, INDEX = " + INDEX);

                Container_LOOP obj = new Container_LOOP();
                obj.type           = 2;
                obj.INDEX          = INDEX;
                obj.do_index       = INDEX + 5;
                obj.end_index      = container.getEndRepeat(INDEX) + 9;
                loop_stack.push(obj);
            }
            else if ( (code.charAt(INDEX)     == 'U') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'T') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'L') )
            {
                log.log("interpreter", 4, "found UNTIL, INDEX = " + INDEX);

                String condition = code.substring(INDEX + 6, loop_stack.peek().end_index - 10);
                if((Boolean)engine.eval(convert_condition(container.replaceVars(condition, context_stack.peek()))))
                {
                    INDEX = loop_stack.peek().do_index;
                    continue;
                } else {
                    INDEX = loop_stack.peek().end_index;
                    loop_stack.pop();
                    continue;
                }
            }
            else if ( (code.charAt(INDEX)     == 'B') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'E') &&
                      (code.charAt(INDEX + 3) == 'A') &&
                      (code.charAt(INDEX + 4) == 'K') )
            {
                log.log("interpreter", 4, "found BREAK, INDEX = " + INDEX);
                INDEX = loop_stack.pop().end_index;
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'P') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'N') &&
                      (code.charAt(INDEX + 4) == 'T') )
            {
                log.log("interpreter", 4, "found PRINT, INDEX = " + INDEX);
                INDEX += 5;

                int semicolon_position = get_semicolon(INDEX, code);
                String print = code.substring(INDEX + 1, semicolon_position - 1);

                print = container.replaceVars(print, context);
                log.log("PRINT", 0, print);
                System.out.println("PRINT: " + print + "\n");
                INDEX = semicolon_position;
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'V') &&
                      (code.charAt(INDEX + 1) == 'A') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                log.log("interpreter", 4, "found VAR, INDEX = " + INDEX);
                INDEX = container.getEndVar(INDEX) + 6;
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'P') &&
                      (code.charAt(INDEX + 5) == 'R') &&
                      (code.charAt(INDEX + 6) == 'O') &&
                      (code.charAt(INDEX + 7) == 'G') &&
                      (code.charAt(INDEX + 8) == 'R') &&
                      (code.charAt(INDEX + 9) == 'A') &&
                      (code.charAt(INDEX + 10)== 'M') )
            {
                log.log("interpreter", 4, "found END_PROGRAM, INDEX = " + INDEX);
                break;
            }
            else /* if no match is found */
            {
                log.log("error", 0, "could not find: " + code.charAt(INDEX));
                System.out.println("error: could not find: " + code.charAt(INDEX));
                continue;
            }
        } /* end main for loop   */
    }

    /**
     * will return the next keyword found, these are ELSEIF ELSE END_IF
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found keyword
     */
    public static int get_next_keyword(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_next_keyword call, INDEX = " + INDEX);
        for(;;INDEX++) 
        {
            if ( ( (code.charAt(INDEX)     == 'E') &&
                   (code.charAt(INDEX + 1) == 'L') &&
                   (code.charAt(INDEX + 2) == 'S') &&
                   (code.charAt(INDEX + 3) == 'I') &&
                   (code.charAt(INDEX + 4) == 'F') )

              || ( (code.charAt(INDEX)     == 'E') &&
                   (code.charAt(INDEX + 1) == 'L') &&
                   (code.charAt(INDEX + 2) == 'S') &&
                   (code.charAt(INDEX + 3) == 'E') )

              || ( (code.charAt(INDEX)     == 'E') &&
                   (code.charAt(INDEX + 1) == 'N') &&
                   (code.charAt(INDEX + 2) == 'D') &&
                   (code.charAt(INDEX + 3) == '_') &&
                   (code.charAt(INDEX + 4) == 'I') &&
                   (code.charAt(INDEX + 5) == 'F') ) )
            {
                log.log("interpreter", 4, "get_next_keyword return, INDEX = " + INDEX);
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = get_end_if(INDEX, code) + 5;
                continue;
            }
        }
    }

    /**
     * will return next VAR index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found VAR
     */
    public static int get_var(int INDEX, StringBuilder code) 
    {
        log.log("interpreter", 4, "get_var call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'V') &&
                 (code.charAt(INDEX + 1) == 'A') &&
                 (code.charAt(INDEX + 2) == 'R') )
            {
                log.log("interpreter", 4, "get_var return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return next TO index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found TO
     */
    public static int get_to(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_to call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log("interpreter", 4, "get_to return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return next colon index
     * @param string containing colon
     * @return ińdex of colon
     */
    public static int get_colon(String string)
    {
        log.log("interpreter", 4, "get_colon call, string: " + string);
        for(int i = 0;;i++) {
            if (string.charAt(i) == ':');
            {
                log.log("interpreter", 4, "get_colon return, i = " + i);
                return i;
            }
        }
    }

    /**
     * will return next BY index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found BY, -1 if DO is found
     */
    public static int get_by(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_by call, INDEX = " + INDEX);
        int count = 0;
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log("interpreter", 4, "get_by return (DO found): -1");
                return -1;
            }
            if ( (code.charAt(INDEX)     == 'B') &&
                 (code.charAt(INDEX + 1) == 'Y') )
            {
                log.log("interpreter", 4, "get_by return, (BY found) INDEX = "
                                                                      + INDEX);
                return INDEX;
            }
            else
            {
                count++;
            }
        }

    }

    /**
     * will return next Semicolon index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found semicolon
     */
    public static int get_semicolon(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_semicolon call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == ';') )
            {
                log.log("interpreter", 4, "get_semicolon return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return next DO index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found DO
     */
    public static int get_do(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_do call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log("interpreter", 4, "get_do return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return next THEN index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found THEN
     */
    public static int get_then(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_then call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'H') &&
                 (code.charAt(INDEX + 2) == 'E') &&
                 (code.charAt(INDEX + 3) == 'N') )
            {
                log.log("interpreter", 4, "get_then return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return END_IF index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found END_IF
     */
    public static int get_end_if(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_end_if call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'E') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'D') &&
                  (code.charAt(INDEX + 3) == '_') &&
                  (code.charAt(INDEX + 4) == 'I') &&
                  (code.charAt(INDEX + 5) == 'F') )
            {
                log.log("interpreter", 4, "get_end_if return, INDEX = " + INDEX);
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = get_end_if(INDEX, code) + 5;
                continue;
            }
        }
    }

    /**
     * will return UNTIL index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found UNTIL
     */
    public static int get_until(int INDEX, StringBuilder code)
    {
        log.log("interpreter", 4, "get_until call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'U') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'T') &&
                  (code.charAt(INDEX + 3) == 'I') &&
                  (code.charAt(INDEX + 4) == 'L') )
            {
                log.log("interpreter", 4, "get_until return, INDEX = " + INDEX);
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'R') &&
                        (code.charAt(INDEX + 1) == 'E') &&
                        (code.charAt(INDEX + 2) == 'P') &&
                        (code.charAt(INDEX + 3) == 'E') &&
                        (code.charAt(INDEX + 4) == 'A') &&
                        (code.charAt(INDEX + 5) == 'T') )
            {
                INDEX = get_until(INDEX, code) + 5;
                continue;
            }
        }
    }

    /**
     * used to warmup the Java ScriptEngine.
     * @param engine the engine which will be used
     */
    public static void engine_warmup(ScriptEngine engine) throws Exception
    {

        int total_evaluations = 2000;
        double avg = 0.0;

        log.log("engine", 1,"starting engine warmup...");
        long start = System.currentTimeMillis();

        for(int i = 0; i < total_evaluations; i++) {

            String test0 = "true == false != true && false";
            String test1 = "true";
            String test2 = "false";
            String test3 = (Math.random() * 1000) % 89 + " + 7";
            String test4 = (Math.random() * 1000) % 83 + " + 1";
            String test5 = (Math.random() * 1000) % 67 + " + 901";
            String test6 = (Math.random() * 1000) % 51 + " > 5";
            String test7 = (Math.random() * 1000) % 39 + " < 123.122134";
            String test8 = (Math.random() * 1000) % 27 + " <= 10987123";
            String test9 = (Math.random() * 1000) % 97 + " < 18723 >= " + (Math.random() * 100);

            long now = System.currentTimeMillis();

            boolean result0 = (boolean)engine.eval(test0);
            boolean result1 = (boolean)engine.eval(test1);
            boolean result2 = (boolean)engine.eval(test2);
            double  result3 = (double)engine.eval(test3);
            double  result4 = (double)engine.eval(test4);
            double  result5 = (double)engine.eval(test5);
            boolean result6 = (boolean)engine.eval(test6);
            boolean result7 = (boolean)engine.eval(test7);
            boolean result8 = (boolean)engine.eval(test8);
            boolean result9 = (boolean)engine.eval(test9);

            now = System.currentTimeMillis() - now;

            avg += now;
            if (i % 100 == 0 && i != 0) {
                avg = avg / 100;
                try {
                    log.log("engine", 2,"100 evaluations done, avg time: (" + avg + "ms avg)");
                } catch (Exception e) {
                }
            }
        }
        log.log("engine", 2, "100 evaluations done, avg time: (" + avg / 100 + "ms avg)");
        log.log("engine", 1, "engine warmup finished, total evaluations: "
                                    + total_evaluations
                                    + ", total time: "
                                    + (System.currentTimeMillis() - start)
                                    + "ms");
    }

    /**
     * convert given string so that the Java Script Engine can Interpret it,
     * will replace following matches: 
     * FROM     | TO
     * ---------+--------------
     * '='      | ' == '
     * 'TRUE'   | ' true '
     * 'FALSE'  | ' false '
     * 'XOR'    | ' ^ '
     * 'AND'    | ' && '
     * 'NOT'    | ' !'
     * '&'      | ' && '
     * '<>'     | ' != '
     * 'MOD'    | ' % '
     * 'OR'     | ' || '
     * 'SIN()'  | 'Math.sin()'
     * 'COS()'  | 'Math.cos()'
     * 'TAN()'  | 'Math.tan()'
     * 'ASIN()' | 'Math.asin()'
     * 'ACOS()' | 'Math.acos()'
     * 'ATAN()' | 'Math.atan()'
     * 'LOG()'  | 'Math.log10()'
     * 'EXP()'  | 'Math.exp()'
     * 'LN()'   | 'Math.log()'
     * 'SQRT()' | 'Math.sqrt()'
     * ---------+---------------
     * @param code will be the full condition as a string
     * @return string with the converted condition
     */
    public static String convert_condition(String code)
    {

        String final_condition = "";
        int spot = 0;

        for( ; spot < code.length(); spot++ ) {
            if ( (code.charAt(spot) == '=') )
            {
                final_condition += " == ";
                continue;
            } else if ( (code.charAt(spot)     == 'T') &&
                        (code.charAt(spot + 1) == 'R') &&
                        (code.charAt(spot + 2) == 'U') &&
                        (code.charAt(spot + 3) == 'E') )
            {
                final_condition += " true ";
                spot += 3;
                continue;
            } else if ( (code.charAt(spot)     == 'F') &&
                        (code.charAt(spot + 1) == 'A') &&
                        (code.charAt(spot + 2) == 'L') &&
                        (code.charAt(spot + 3) == 'S') &&
                        (code.charAt(spot + 4) == 'E') )
            {
                final_condition += " false ";
                spot += 4;
                continue;
            } else if ( (code.charAt(spot)     == 'X') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'R') )
            {
                final_condition += " ^ ";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'N') &&
                        (code.charAt(spot + 2) == 'D') )
            {
                final_condition += " && ";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'N') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'T') )
            {
                final_condition += " !";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot) == '&') )
            {
                final_condition += " && ";
                continue;
            } else if ( (code.charAt(spot)     == '<') &&
                        (code.charAt(spot + 1) == '>') )
            {
                final_condition += " != ";
                spot += 1;
                continue;
            } else if ( (code.charAt(spot)     == 'M') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'D') )
            {
                final_condition += " % ";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'O') &&
                        (code.charAt(spot + 1) == 'R') )
            {
                final_condition += " || ";
                spot += 1;
                continue;
            } else if ( (code.charAt(spot)     == 'S') &&
                        (code.charAt(spot + 1) == 'I') &&
                        (code.charAt(spot + 2) == 'N') )
            {
                final_condition += " Math.sin";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'T') &&
                        (code.charAt(spot + 1) == 'A') &&
                        (code.charAt(spot + 2) == 'N') )
            {
                final_condition += " Math.tan";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'S') &&
                        (code.charAt(spot + 2) == 'I') &&
                        (code.charAt(spot + 3) == 'N') )
            {
                final_condition += " Math.asin";
                spot += 3;
                continue;
            } else if ( (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'C') &&
                        (code.charAt(spot + 2) == 'O') &&
                        (code.charAt(spot + 3) == 'S') )
            {
                final_condition += " Math.acos";
                spot += 3;
                continue;
            } else if ( (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'T') &&
                        (code.charAt(spot + 2) == 'A') &&
                        (code.charAt(spot + 3) == 'N') )
            {
                final_condition += " Math.atan";
                spot += 3;
                continue;
            } else if ( (code.charAt(spot)     == 'L') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'G') )
            {
                final_condition += " Math.log10";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'E') &&
                        (code.charAt(spot + 1) == 'X') &&
                        (code.charAt(spot + 2) == 'P') )
            {
                final_condition += " Math.exp";
                spot += 2;
                continue;
            } else if ( (code.charAt(spot)     == 'L') &&
                        (code.charAt(spot + 1) == 'N') )
            {
                final_condition += " Math.log";
                spot += 1;
                continue;
            } else if ( (code.charAt(spot)     == 'S') &&
                        (code.charAt(spot + 1) == 'Q') &&
                        (code.charAt(spot + 2) == 'R') &&
                        (code.charAt(spot + 3) == 'T') )
            {
                final_condition += " Math.sqrt";
                spot += 3;
                continue;
            } else if ( (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'B') &&
                        (code.charAt(spot + 2) == 'S') )
            {
                final_condition += " Math.abs";
                spot += 2;
                continue;
            }
            final_condition += code.charAt(spot);
        }
        return final_condition;
    }
}
