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

package Icarus;

import Ninti.*;
import linc.*;
import parser.*;

import java.io.*;
import java.net.*;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class main {

static InfoCollector container;

    public static void main(String[] args) throws Exception {

        String[] path = new String[1];

        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname = InetAddress.getLocalHost().getHostName();
        switch (hostname) {
            case "beelzebub":
                path[0] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/franzke_files/plc_prg.st";
                break;
            case "d4ryus":
                path[0] = "/home/d4ryus/coding/Project_Icarus/franzke_files/plc_prg.st";
                break;
            /*
             case "yourhostname":
             path[0] = "/your/path/to/the/file";
             break;
             */
            default:
                System.out.println("Hey stupid(faggot), take a look in the code!");
                System.exit(0);
        }
        System.out.println(path[0]);
        container = new InfoCollector(path);
        String code = container.getAllTheCode().toString();
        System.out.println(code);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        engine_warmup(engine);
    }

    public static void interpret (String string, int start, int end, ScriptEngine engine) throws Exception { 


        /* stacks */
        Stack<String>  context_stack              = new Stack(); // current context
        Stack<Boolean> if_stack                   = new Stack(); // last if condition
        Stack<Integer> if_position_stack          = new Stack(); // last if position

        Stack<Integer> loop_do_position_stack     = new Stack(); // last loop Do
        Stack<String> loop_condition_stack        = new Stack(); // last loop Condition

        Stack<Integer> loop_while_position_stack  = new Stack(); // last loop Condition
        Stack<Integer> loop_repeat_position_stack = new Stack(); // last loop Condition
        Stack<Integer> loop_for_postition_stack   = new Stack(); // last loop Condition

        StringBuilder code = new StringBuilder(string);

        String context = "";

        for(int INDEX = start; INDEX < end; INDEX++) {

            if ( (code.charAt(INDEX)     == 'P') &&
                 (code.charAt(INDEX + 1) == 'R') &&
                 (code.charAt(INDEX + 2) == 'O') &&
                 (code.charAt(INDEX + 3) == 'G') &&
                 (code.charAt(INDEX + 4) == 'A') &&
                 (code.charAt(INDEX + 5) == 'M') )
            {
                INDEX += 6;
                for(;;INDEX++) {
                    if ( (code.charAt(INDEX)     == 'V') &&
                         (code.charAt(INDEX + 1) == 'A') &&
                         (code.charAt(INDEX + 2) == 'R') )
                    {
                        context_stack.push(context);
                        int jump = container.getEndVar(INDEX);
                        INDEX = jump + 6;
                        continue;
                    }
                    context += code.charAt(INDEX);
                }

            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
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
            } else if ( (code.charAt(INDEX)     == 'E') &&
                        (code.charAt(INDEX + 1) == 'N') &&
                        (code.charAt(INDEX + 2) == 'D') &&
                        (code.charAt(INDEX + 3) == '_') &&
                        (code.charAt(INDEX + 4) == 'I') &&
                        (code.charAt(INDEX + 5) == 'F') )
            {
                INDEX += 5;
                if_stack.pop();
                if_position_stack.pop();
                continue;
            } else if ( (code.charAt(INDEX)     == 'E') &&
                        (code.charAt(INDEX + 1) == 'L') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'E') )
            {
                if(if_stack.peek()) {
                    INDEX = container.getEndIf(if_position_stack.pop()) + 5;
                    continue;
                } else {
                        INDEX += 3;
                        continue;
                }
            } else if ( (code.charAt(INDEX)     == 'E') &&
                        (code.charAt(INDEX + 1) == 'L') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'I') &&
                        (code.charAt(INDEX + 4) == 'F') )
            {
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
            } else if ( (code.charAt(INDEX)      == 'V') &&
                        (code.charAt(INDEX + 1)  == 'A') &&
                        (code.charAt(INDEX + 2)  == 'R') )
            {
                INDEX = container.getEndVar(INDEX) + 6;
                continue;
            } else if ( (code.charAt(INDEX)     == 'F') &&
                        (code.charAt(INDEX + 1) == 'O') &&
                        (code.charAt(INDEX + 2) == 'R') )
            {
                loop_for_postition_stack.push(INDEX);
            } else if ( (code.charAt(INDEX)     == 'W') &&
                        (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'L') &&
                        (code.charAt(INDEX + 4) == 'E') )
            {
                loop_while_position_stack.push(INDEX);
                int do_position = get_do(INDEX, code);
                String condition = code.substring(INDEX + 5, do_position - 1);
                loop_do_position_stack.push(do_position);
                loop_condition_stack.push(condition);
                if((Boolean)engine.eval(convert_condition(container.replaceVars(loop_condition_stack.peek(), context_stack.peek())))) {
                    INDEX = loop_do_position_stack.peek() + 1;
                    continue;
                } else {
                    INDEX = container.getEndWhile(loop_while_position_stack.peek() + 1) + 8;
                    loop_while_position_stack.pop();
                    loop_condition_stack.pop();
                    loop_do_position_stack.pop();
                    continue;
                }

            } else if ( (code.charAt(INDEX)     == 'E') &&
                        (code.charAt(INDEX + 1) == 'N') &&
                        (code.charAt(INDEX + 2) == 'D') &&
                        (code.charAt(INDEX + 3) == '_') &&
                        (code.charAt(INDEX + 4) == 'W') &&
                        (code.charAt(INDEX + 5) == 'H') &&
                        (code.charAt(INDEX + 6) == 'I') &&
                        (code.charAt(INDEX + 7) == 'L') &&
                        (code.charAt(INDEX + 8) == 'E') )
            {
                INDEX += 8;
                if((Boolean)engine.eval(convert_condition(container.replaceVars(loop_condition_stack.peek(), context_stack.peek())))) {
                    INDEX = loop_do_position_stack.peek() + 1;
                    continue;
                } else {
                    loop_while_position_stack.pop();
                    loop_condition_stack.pop();
                    loop_do_position_stack.pop();
                    continue;
                }
            } else if ( (code.charAt(INDEX)     == 'P') &&
                        (code.charAt(INDEX + 1) == 'R') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'N') &&
                        (code.charAt(INDEX + 4) == 'T') )
            {
                INDEX += 6;
                String print = "";
                for( ;; INDEX++ ) {
                    if ( (code.charAt(INDEX)     == ')') &&
                         (code.charAt(INDEX + 1) == ';') )
                    {
                        INDEX += 1;
                        System.out.println("#####PRINT#######");
                        System.out.println(container.replaceVars(print, context_stack.peek()));
                        break;
                    } else {
                        print += code.charAt(INDEX);
                    }
                }

            } else if ( (code.charAt(INDEX)      == 'E') &&
                        (code.charAt(INDEX + 1)  == 'N') &&
                        (code.charAt(INDEX + 2)  == 'D') &&
                        (code.charAt(INDEX + 3)  == '_') && 
                        (code.charAt(INDEX + 4)  == 'P') &&
                        (code.charAt(INDEX + 5)  == 'R') && 
                        (code.charAt(INDEX + 6)  == 'O') &&
                        (code.charAt(INDEX + 7)  == 'G') &&
                        (code.charAt(INDEX + 8)  == 'R') &&
                        (code.charAt(INDEX + 9)  == 'A') &&
                        (code.charAt(INDEX + 10) == 'M') )
            {
                break;
            }
        } /* end main for loop   */
    }

    public static int get_next_keyword(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
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
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = get_end_if(INDEX, code) + 5;
                continue;
            }
        }
    }

    public static int get_to(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                return INDEX;
            }
        }
    }
    public static int get_by(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'B') &&
                 (code.charAt(INDEX + 1) == 'Y') )
            {
                return INDEX;
            }
        }
    }
    public static int get_do(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                return INDEX;
            }
        }
    }

    public static int get_then(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'H') &&
                 (code.charAt(INDEX + 2) == 'E') &&
                 (code.charAt(INDEX + 3) == 'N') )
            {
                return INDEX;
            }
        }
    }

    public static int get_end_if(int INDEX, StringBuilder code) {
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'E') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'D') &&
                  (code.charAt(INDEX + 3) == '_') &&
                  (code.charAt(INDEX + 4) == 'I') &&
                  (code.charAt(INDEX + 5) == 'F') )
            {
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = get_end_if(INDEX, code) + 5;
                continue;
            }
        }
    }


    public static void engine_warmup(ScriptEngine engine) throws Exception {

        int total_evaluations = 2000;
        double avg = 0.0;

        System.out.print("starting engine warmup...\n");
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
                    // log verbosity lvl 2;
                    System.out.print("100 evaluations done, avg time: (" + avg + "ms avg)\n");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        System.out.print("100 evaluations done, avg time: (" + avg / 100 + "ms avg)\n");
        System.out.print("engine warmup finished, total evaluations: "
                                    + total_evaluations
                                    + ", total time: "
                                    + (System.currentTimeMillis() - start)
                                    + "ms\n");
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
     *
     * @param code will be the full condition as a string
     * @return string with the converted condition
     */
    public static String convert_condition(String code) {

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
