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
        InfoCollector infR = new InfoCollector(path);
        System.out.println(infR.getAllTheCode());
    }

    public static void interpret (String string, int start, int end) throws Exception { 

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        /* here apfel's container later */
        // container Container = new Container();
        Stack<Boolean> stack = new Stack<>();

        StringBuilder code = new StringBuilder(string);

        for(int INDEX = start; INDEX < end; INDEX++) {

            /* if newline '§', ignore it and continue with next */
            if ( code.charAt(INDEX) == '§' )
            {
                continue;
            /*
             * when an 'IF' is found, cut the condition, evaluate it, push the solution
             * to the stack and jump ahead to the 'THEN', or 'ELSE' keyword.
             */
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
                if(stack.pop()) {
                    /* jump over ELSE block */
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
                if(stack.pop()) {
                    /* jump over ELSIF block */
                } else {
                    INDEX += 5;
                    StringBuilder condidtion = new StringBuilder("");
                    for(;;INDEX++) {
                        if ( (code.charAt(INDEX)     == 'T') &&
                             (code.charAt(INDEX + 1) == 'H') &&
                             (code.charAt(INDEX + 2) == 'E') &&
                             (code.charAt(INDEX + 3) == 'N') )
                        {
                            /* stack.pop? or not? not sure  see site 128 */
                            stack.pop();
                            stack.push((Boolean)engine.eval(convert_condition(container.replace(condition.toString()))));
                            /*
                             * check if condition is true, if yes, break all and go back
                             * to outter loop, if not search for the next occurence of 
                             * ELSE or END_IF and ignore everything in between
                             */
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
                if(stack.pop()) {
                    /*
                     * if the last condition was true, just dont to anything and continue
                     * evaluating the code
                     */
                } else {
                    /*
                     * if the last condition wasn't true, we have to jump to the next
                     * occurence of ELSE or END_IF
                     */
                }
            /* if 'VAR' is found, ignore code until 'END_VAR' */
            } else if ( (code.charAt(INDEX)      == 'V') &&
                        (code.charAt(INDEX + 1)  == 'A') &&
                        (code.charAt(INDEX + 2)  == 'R') )
            {
                INDEX += 3;
                for ( ;; INDEX++ ) {
                    if ( (code.charAt(INDEX)      == 'E') &&
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
                }

            /*
             * this will be replaced with print function to the Logwriter
             * hopefully that logwriter will be done soon.
             */
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
