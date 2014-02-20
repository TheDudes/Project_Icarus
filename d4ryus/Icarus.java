/**
 * this is the Main file, containing the main function to start the Interpreter.
 *
 * current status:
 *  - able to preprozess file (removes comments and stuff, no one needs that, right?)
 *  - able to interpret simple VAR blocks (dynamic allocation with HashMaps, l33th4XX!)
 *  - able to Print out stuff (yeah yeah i know, Structured Text has no PRINT function,
 *                             and thats one of the many reasons why this Interpreter 
 *                             is fucking awesome! why? cause it can interpret more than
 *                             it should or could!)
 *  - able to interpret IF conditions (pretty damn great, isnt it?)
 *
 *
 * to get this to work you have to change the path to the structured text by hand for
 * now, simple search for the string which looks like "/home/d4ryus/..." and replace
 * it with ur own path.
 *
 * @autor d4ryus <w.wackerbauer@yahoo.de>
 * @version 0.1
 */
import java.io.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

class Icarus {

    /* at first create a container to hold the VAR values */
    public static Container container = new Container();
    private static Boolean_Stack stack = new Boolean_Stack();

    /**
     * convert given string so that the Java Script Engine can Interpret it,
     * will replace following matches: 
     * FROM     | TO
     * ---------+------------
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
     * ---------+------------
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
            }
            final_condition += code.charAt(spot);
        }
        return final_condition;
    }

    public static void main(String [] args) throws Exception {
        String file = "/home/d4ryus/Coding/Project_Icarus/d4ryus/test.st";
        String blub = readFile(file);
System.out.println("readed in:-----------------------------------------------------------");
System.out.println(blub + "\n------------------------------------------------------------");
        String code = string_preprozess(blub);
System.out.println("preprozessed to:----------------------------------------------------");
System.out.println(code + "\n-----------------------------------------------------------");

        interpret(code);
    }

    /**
     * main function to interpret given string
     *
     * @param string after preprozession
     */
    public static void interpret (String string) throws Exception { 

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        int INDEX = 0;
        StringBuilder code = new StringBuilder(string);

        for( ;INDEX < code.length(); INDEX++) {

            if ( code.charAt(INDEX) == '§' )
            {
                continue;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX += 2;
                StringBuilder condition = new StringBuilder("");
                for(;;INDEX++) {
                    if ( (code.charAt(INDEX)     == 'T') &&
                         (code.charAt(INDEX + 1) == 'H') &&
                         (code.charAt(INDEX + 2) == 'E') &&
                         (code.charAt(INDEX + 3) == 'N') )
                    {
                        /*
                         * cut function here, then recursive call of interpret function
                         */
                        stack.push((Boolean)engine.eval(convert_condition(container.replace(condition.toString()))));
                    } else if (code.charAt(INDEX) == '§') {
                        continue;
                    }
                    condition.append(code.charAt(INDEX));
                }
            } else if ( (code.charAt(INDEX) ==     'E') &&
                        (code.charAt(INDEX + 1) == 'L') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'E') )
            {
                if(stack.get_first()) {
                    stack.pop();
                    return;
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
                if(stack.get_first()) {
                    stack.pop();
                    return;
                } else {
                    INDEX += 5;
                    for(;;INDEX++) {
                        if ( (code.charAt(INDEX)     == 'T') &&
                             (code.charAt(INDEX + 1) == 'H') &&
                             (code.charAt(INDEX + 2) == 'E') &&
                             (code.charAt(INDEX + 3) == 'N') )
                        {
                            stack.pop();
                            stack.push((Boolean)engine.eval(convert_condition(container.replace(condition.toString()))));
                            break;
                        } else if (code.charAt(INDEX) == '§') {
                            continue;
                        }
                        condition.append(code.charAt(INDEX));
                    }
                }
        } else if ( (code.charAt(INDEX) ==     'T') &&
                        (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'E') &&
                        (code.charAt(INDEX + 3) == 'N') )
            {
                if(stack.get_first()) {

            }
            } else if ( (code.charAt(INDEX)      == 'V') &&
                        (code.charAt(INDEX + 1)  == 'A') &&
                        (code.charAt(INDEX + 2)  == 'R') )
            {
                INDEX += 3;
                String var_line = "";
                Boolean first = true;
                for ( ;; INDEX++ ) {
                    if(code.charAt(INDEX) == '§')
                    {
                        if(first) {
                            first = false;
                            continue;
                        } else {
                            container.add(var_line);
                            var_line = "";
                            continue;
                        }
                    } else if ( (code.charAt(INDEX)      == 'E') &&
                                (code.charAt(INDEX + 1)  == 'N') &&
                                (code.charAt(INDEX + 2)  == 'D') &&
                                (code.charAt(INDEX + 3)  == '_') &&
                                (code.charAt(INDEX + 4)  == 'V') &&
                                (code.charAt(INDEX + 5)  == 'A') &&
                                (code.charAt(INDEX + 6)  == 'R') )
                    {
                        INDEX += 7;
                        break;
                    }
                    var_line += code.charAt(INDEX);
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
                        System.out.println(container.replace(print));
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
}
