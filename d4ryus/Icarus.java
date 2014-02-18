/*
 * Pre Alpha Icarus Interpreter Version 0.1
 *
 * current status:
 *	- able to preprozess file (removes comments and stuff, no one needs that, right?)
 *	- able to interpret simple VAR blocks (dynamic allocation with HashMaps, l33th4XX!)
 *	- able to Print out stuff (yeah yeah i know, Structured Text has no PRINT function,
 *							   and thats one of the many reasons why this Interpreter 
 *							   is fucking awesome! why? cause it can interpret more than
 *							   it should or could!)
 *	- able to interpret IF conditions (pretty damn great, isnt it?)
 *	
 * todo:
 *	- changing of values
 *	- WHILE/FOR/CASE implementation
 *	- Timer implementation
 *	- clean up code
 *	- optimizing code
 *
 * 
 * this is the Main file, containing the main function to start the Interpreter.
 *
 * to get this to work you have to change the path to the structured text by hand for
 * now, simple search for the string which looks like "/home/d4ryus/..." and replace
 * it with ur own path.
 *
 * created by d4ryus, using Vim
 */
import java.io.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

class Icarus {

	/* at first create a container to hold the VAR values */
	public static Container container = new Container();
	/*
	 * convert given string so that the Java Script Engine can Interpret it,
	 * will replace following matches:
	 * 
	 * FROM		| TO
	 * ---------+------------
	 * '='		| ' == '
	 * 'TRUE'	| ' true '
	 * 'FALSE'	| ' false '
	 * 'XOR'	| ' ^ '
	 * 'AND'	| ' && '
	 * 'NOT'	| ' !'
	 * '&'		| ' && '
	 * '<>'		| ' != '
	 * 'MOD'	| ' % '
	 * 'OR'		| ' || '
	 * ---------+------------
	 */ 
    public static String convert_condition(String code) {
        String final_condition = "";
        
        int spot = 0;
        
    
        for( ; spot < code.length(); spot++ ) {
            if ( (code.charAt(spot) == '=') )
            {
                final_condition += " == ";
                continue;
            }
            else if ( (code.charAt(spot)     == 'T') &&
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

    /* 
     * preprozesses given string, and converts:
	 * FROM		| TO
	 * ---------+-------
	 * ' '		| ''	removing spaces
	 * '(* - *)'| ''	removing comments
	 * '\n'		| '§'	replacing newlines with '§'
	 * '\t'		| ''	removing tabs
     */
    public static String string_preprozess(String code) {

        String final_code = "";

        for(int spot = 0 ; spot < code.length() ; spot++) {

            if( (code.charAt(spot) == ' ') ) 
            {     
                continue;
            } else if( (code.charAt(spot)     == '(') && 
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
            } else if (code.charAt(spot) == '\n') 
            {
                final_code += '§';
                continue;
            } else if (code.charAt(spot) == '\t')
			{
				continue;
			}
            final_code += code.charAt(spot);
        }
        return final_code;
    }

    /* 
     * will read in given file, and return a string with the content 
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
        String file = "/home/d4ryus/Coding/Project_Icarus/d4ryus/test.st";
        String blub = readFile(file);
System.out.println("readed in:-----------------------------------------------------------");
System.out.println(blub + "\n------------------------------------------------------------");
        String code = string_preprozess(blub);
System.out.println("preprozessed to:----------------------------------------------------");
System.out.println(code + "\n-----------------------------------------------------------");

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
               
        /*
         * String code = "IF(3+3>5)§THEN§blub§END_IF§END_PROGRAM"; 
         */
        
        int INDEX = 0;
        

        for( ;INDEX < code.length(); INDEX++) {

            if ( code.charAt(INDEX) == '§' ) 
            {
                continue;
            } else if ( (code.charAt(INDEX)     == 'I') &&
                        (code.charAt(INDEX + 1) == 'F') )
            {
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
                        if((Boolean)engine.eval(convert_condition(container.replace(condition)))) 
						{
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
                    /* if no match is found concat the chars to the condition String */
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
                    /*
                     * here Code for the VAR BLOCK;
                     */
					var_line += code.charAt(INDEX);
				
                }
            
            } else if ( (code.charAt(INDEX)     == 'P') &&
                        (code.charAt(INDEX + 1) == 'R') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'N') && 
                        (code.charAt(INDEX + 4) == 'T') ) 
            {
                INDEX += 6;
                /*
                 * here Code if WHILE BLOCK is found
                 */
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
