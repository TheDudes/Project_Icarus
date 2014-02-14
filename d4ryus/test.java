/* 
 * file to test out things
 * created by d4ryus, using Vim
 * todo:
 * - Hashmap
 * - Linked List
 * - & -> &&
 */
import java.io.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

class test {

    /* 
     * will reconfigure given string, for example it will remove spaces, or
     * replace newlines with '§'
     */
    public static String string_preprozess(String code) {
        String final_code = "";

        for(int spot = 0 ; spot < code.length() ; spot++) {

            if( (code.charAt(spot) == ' ') ) continue;

            else if( (code.charAt(spot)     == '(') && 
                     (code.charAt(spot + 1) == '*') )
            {
                spot++;
                while(true) {
                    if( (code.charAt(spot)     == '*') &&
                        (code.charAt(spot + 1) == ')') ) 
                    {
                        spot++;
                        break;
                    } else {
                        spot++;
                    }
                }
            continue;
            } else if(  (code.charAt(spot)      == 'n') &&
                        (code.charAt(spot + 1 ) == 'u') &&
                        (code.charAt(spot + 2 ) == 'l') && 
                        (code.charAt(spot + 3 ) == 'l') )
            {
                spot += 3;
                final_code += '§';
                continue;
            } else if (code.charAt(spot) == '\n') 
            {
                final_code += '§';
                continue;
            }
            final_code += code.charAt(spot);
        }
        return final_code;
    }

    /* 
     * will read given file, and return a string with the content 
     */
    private static String readFile(String file) throws IOException { 
        BufferedReader reader = new BufferedReader( new FileReader (file)); 
        String line = null; 
        StringBuilder stringBuilder = new StringBuilder(); 
        String ls = System.getProperty("line.separator"); 

        while((line = reader.readLine() ) != null )  
        { 
            stringBuilder.append(line); 
            stringBuilder.append(ls); 
        }    
        return stringBuilder.toString(); 
    } 


    public static void main(String [] args) throws Exception {
        String file = "/home/d4ryus/Coding/praktikum_code/studienprojekt/d4ryus/test.st";
        String blub = readFile(file); 
        System.out.println(blub);
        String final_code = string_preprozess(blub);
        System.out.println(final_code);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
               
        String code = "IF(3+3>5)§THEN§blub§END_IF§END_PROGRAM";
        
        int INDEX = 0;
        

        for( ;INDEX < code.length(); INDEX++) {

            if ( code.charAt(INDEX) == '§' ) 
            {
                continue;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
                /* if an IF is found, jump over it with INDEX += 2 */
                INDEX += 2; 
                String condition = "";
                /* for loop to figure out the IF condidition */
                for(;;INDEX++){
                    if( (code.charAt(INDEX)     == 'T') &&
                        (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'E') &&
                        (code.charAt(INDEX + 3) == 'N') )
                    {
                        /* 
                         * if the end of the condition is reached, set cursor
                         * after the THEN statement 
                         */
                        INDEX += 3;
                        if( (Boolean)engine.eval(condition) ) {
                            /*
                             * if condition is true, continue execution
                             * after the THEN statement.
                             */
                            break;
                        } else {
                            /*
                             * if condition is false, jump over the THEN
                             * block, and continue execute after the END_IF
                             */
                            for(;;INDEX++) {
                            if ( (code.charAt(INDEX)     == 'E') &&
                                 (code.charAt(INDEX + 1) == 'N') &&
                                 (code.charAt(INDEX + 2) == 'D') &&
                                 (code.charAt(INDEX + 3) == '_') && 
                                 (code.charAt(INDEX + 4) == 'I') &&
                                 (code.charAt(INDEX + 5) == 'F') ) 
                                {
                                    INDEX += 5;
                                    break;   
                                }
                            }
                            break;
                        }
                    /* concat the chars to an String */
                    } else if (code.charAt(INDEX) != '§' ) {
                        condition += code.charAt(INDEX);
                    }   
                } /* end for loop for IF */  
            } else if ( (code.charAt(INDEX)     == 'E') &&
                        (code.charAt(INDEX + 1) == 'N') &&
                        (code.charAt(INDEX + 2) == 'D') &&
                        (code.charAt(INDEX + 3) == '_') && 
                        (code.charAt(INDEX + 4) == 'I') &&
                        (code.charAt(INDEX + 5) == 'F') )
            { 
               INDEX += 5;
               continue;
            } else if ( (code.charAt(INDEX)      == 'V') &&
                        (code.charAt(INDEX + 1)  == 'A') &&
                        (code.charAt(INDEX + 2)  == 'R') )
            {  
                INDEX += 3;    
                for ( ;; INDEX++ ) {
                    if(code.charAt(INDEX) == '§') 
                    {
                        continue;
                    } else_if( (code.charAt(INDEX)      == 'E') && 
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
            
            } else if ( (code.charAt(INDEX)     == 'W') &&
                        (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'L') && 
                        (code.charAt(INDEX + 4) == 'E') ) 
            {
                INDEX += 5;
                
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
