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
    }

    public static void interpret (String string, int start, int end) throws Exception { 

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        /* stacks */
        Stack<Boolean> stack         = new Stack(); // last if condition
        Stack<Integer> if_stack      = new Stack(); // last if position
        Stack<String>  context_stack = new Stack(); // current context

        StringBuilder code = new StringBuilder(string);

        for(int INDEX = start; INDEX < end; INDEX++) {

            if ( (code.charAt(INDEX)     == 'P') &&
                        (code.charAt(INDEX + 1) == 'R') &&
                        (code.charAt(INDEX + 2) == 'O') &&
                        (code.charAt(INDEX + 3) == 'G') &&
                        (code.charAt(INDEX + 4) == 'A') &&
                        (code.charAt(INDEX + 5) == 'M') )
            {
                String context = "";
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
                if_stack.push(INDEX);
                INDEX += 2;
                StringBuilder condition = new StringBuilder("");
                for(;;INDEX++) {
                    if ( (code.charAt(INDEX)     == 'T') &&
                         (code.charAt(INDEX + 1) == 'H') &&
                         (code.charAt(INDEX + 2) == 'E') &&
                         (code.charAt(INDEX + 3) == 'N') )
                    {
                        stack.push((Boolean)engine.eval(convert_condition(container.replaceVars(condition.toString(), context_stack.peek()))));
                        if(stack.peek()) {
                            break;
                        } else {
                            INDEX = get_next_keyword(INDEX, code);
                            break;
                        }
                    }
                    condition.append(code.charAt(INDEX));
                }
            } else if ( (code.charAt(INDEX) ==     'E') &&
                        (code.charAt(INDEX + 1) == 'N') &&
                        (code.charAt(INDEX + 2) == 'D') &&
                        (code.charAt(INDEX + 3) == '_') &&
                        (code.charAt(INDEX + 4) == 'I') &&
                        (code.charAt(INDEX + 5) == 'F') )
            {
                INDEX += 5;
                stack.pop();
                if_stack.pop();
            } else if ( (code.charAt(INDEX) ==     'E') &&
                        (code.charAt(INDEX + 1) == 'L') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'E') )
            {
                if(stack.peek()) {
                    int jump = container.getEndIf(if_stack.pop());
                    if_stack.pop();
                    stack.pop();
                    INDEX = jump + 5;
                    continue;
                } else {
                        INDEX += 3;
                        continue;
                }
            } else if ( (code.charAt(INDEX) ==     'E') &&
                        (code.charAt(INDEX + 1) == 'L') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'I') &&
                        (code.charAt(INDEX + 4) == 'F') )
            {
                if(stack.peek()) {
                    INDEX = get_next_keyword(INDEX, code);
                    continue;
                } else {
                    INDEX += 5;
                    StringBuilder condition = new StringBuilder("");
                    for(;;INDEX++) {
                        if ( (code.charAt(INDEX)     == 'T') &&
                             (code.charAt(INDEX + 1) == 'H') &&
                             (code.charAt(INDEX + 2) == 'E') &&
                             (code.charAt(INDEX + 3) == 'N') )
                        {
                            stack.pop();
                            Boolean bool_condition = (Boolean)engine.eval(convert_condition(container.replaceVars(condition.toString(), context_stack.peek())));
                            stack.push(bool_condition);
                            if(bool_condition) {
                                break;
                            } else {
                                INDEX = get_next_keyword(INDEX, code);
                                break;
                            }
                        }
                        condition.append(code.charAt(INDEX));
                    }
                }
            } else if ( (code.charAt(INDEX) ==     'T') &&
                        (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'E') &&
                        (code.charAt(INDEX + 3) == 'N') )
            {
                if(stack.peek()) {
                    INDEX += 3;
                    continue;
                } else {
                    INDEX = get_next_keyword(INDEX, code);
                    continue;
                }
            } else if ( (code.charAt(INDEX)      == 'V') &&
                        (code.charAt(INDEX + 1)  == 'A') &&
                        (code.charAt(INDEX + 2)  == 'R') )
            {
                INDEX = container.getEndVar(INDEX) + 6;
                continue;
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
        }/* end main for loop   */
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
                INDEX -= 1;
                break;
            }
        }
        return INDEX;
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
