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
 * file: Icarus/src/interpreter/Interpreter.java
 * @author: d4ryus - https://github.com/d4ryus/
 * @version 0.2
 */

package interpreter;

import vault.*;
import parser.*;

import java.io.*;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Interpreter
{
    String log_key = "Interpreter";
    LogWriter       log;
    Keyword_Handler handler;

    public Interpreter(InfoCollector container, LogWriter log, ScriptEngine engine)
    {
        log.log(log_key, 4, "init Interpreter...");
        this.log       = log;

        handler = new Keyword_Handler(container, log, engine);
        log.log(log_key, 4, "init Interpreter done.");
    }


    /**
     * will interpret given code from given start till end INDEX.
     * @param string structure text code after parser
     * @param start INDEX where interpreter will start
     * @param end INDEX where interpreter will stop
     * @param engine ScriptEngine which will be used.
     */
    public void interpret (String code, int start, int end, ScriptEngine engine) throws Exception 
    {

        log.log(log_key, 4, "started with start: " + start + ", end: " + end);

        for(int INDEX = start; INDEX < end; INDEX++)
        {

            log.log(log_key, 4, "for_loop_top, INDEX = " + INDEX);

            if (      (code.charAt(INDEX)     == 'P') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'O') &&
                      (code.charAt(INDEX + 3) == 'G') &&
                      (code.charAt(INDEX + 4) == 'R') &&
                      (code.charAt(INDEX + 5) == 'A') &&
                      (code.charAt(INDEX + 6) == 'M') )
            {
                INDEX = handler.found_PROGRAM(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'I') &&
                      (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = handler.found_IF(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'I') &&
                      (code.charAt(INDEX + 5) == 'F') )
            {
                INDEX = handler.found_END_IF(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'E') )
            {
                INDEX = handler.found_ELSE(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'F') )
            {
                INDEX = handler.found_ELSIF(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'W') &&
                      (code.charAt(INDEX + 1) == 'H') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'L') &&
                      (code.charAt(INDEX + 4) == 'E') )
            {
                INDEX = handler.found_WHILE(INDEX, code);
                continue;
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
                INDEX = handler.found_END_WHILE(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'F') &&
                      (code.charAt(INDEX + 1) == 'O') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                INDEX = handler.found_FOR(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'F') &&
                      (code.charAt(INDEX + 5) == 'O') &&
                      (code.charAt(INDEX + 6) == 'R') )
            {
                INDEX = handler.found_END_FOR(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'R') &&
                      (code.charAt(INDEX + 1) == 'E') &&
                      (code.charAt(INDEX + 2) == 'P') &&
                      (code.charAt(INDEX + 3) == 'E') &&
                      (code.charAt(INDEX + 4) == 'A') &&
                      (code.charAt(INDEX + 5) == 'T') )
            {
                INDEX = handler.found_REPEAT(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'U') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'T') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'L') )
            {
                INDEX = handler.found_UNTIL(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'B') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'E') &&
                      (code.charAt(INDEX + 3) == 'A') &&
                      (code.charAt(INDEX + 4) == 'K') )
            {
                INDEX = handler.found_BREAK(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'P') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'N') &&
                      (code.charAt(INDEX + 4) == 'T') )
            {
                INDEX = handler.found_PRINT(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'V') &&
                      (code.charAt(INDEX + 1) == 'A') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                INDEX = handler.found_VAR(INDEX, code);
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
                handler.found_END_PROGRAM(INDEX, code);
                break;
            }
            else /* if no match is found */
            {
                INDEX = handler.found_nothing(INDEX, code);
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
    private int get_next_keyword(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_next_keyword call, INDEX = " + INDEX);
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
                log.log(log_key, 4, "get_next_keyword return, INDEX = " + INDEX);
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
    private int get_var(int INDEX, StringBuilder code) 
    {
        log.log(log_key, 4, "get_var call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'V') &&
                 (code.charAt(INDEX + 1) == 'A') &&
                 (code.charAt(INDEX + 2) == 'R') )
            {
                log.log(log_key, 4, "get_var return, INDEX = " + INDEX);
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
    private int get_to(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_to call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(log_key, 4, "get_to return, INDEX = " + INDEX);
                return INDEX;
            }
        }
    }

    /**
     * will return next colon index
     * @param string containing colon
     * @return ińdex of colon
     */
    private int get_colon(String string)
    {
        log.log(log_key, 4, "get_colon call, string: " + string);
        for(int i = 0;;i++) {
            if (string.charAt(i) == ':')
            {
                log.log(log_key, 4, "get_colon return, i = " + i);
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
    private int get_by(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_by call, INDEX = " + INDEX);
        int count = 0;
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(log_key, 4, "get_by return (DO found): -1");
                return -1;
            }
            if ( (code.charAt(INDEX)     == 'B') &&
                 (code.charAt(INDEX + 1) == 'Y') )
            {
                log.log(log_key, 4, "get_by return, (BY found) INDEX = "
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
    private int get_semicolon(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_semicolon call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == ';') )
            {
                log.log(log_key, 4, "get_semicolon return, INDEX = " + INDEX);
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
    private int get_do(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_do call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(log_key, 4, "get_do return, INDEX = " + INDEX);
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
    private int get_then(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_then call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'H') &&
                 (code.charAt(INDEX + 2) == 'E') &&
                 (code.charAt(INDEX + 3) == 'N') )
            {
                log.log(log_key, 4, "get_then return, INDEX = " + INDEX);
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
    private int get_end_if(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_end_if call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'E') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'D') &&
                  (code.charAt(INDEX + 3) == '_') &&
                  (code.charAt(INDEX + 4) == 'I') &&
                  (code.charAt(INDEX + 5) == 'F') )
            {
                log.log(log_key, 4, "get_end_if return, INDEX = " + INDEX);
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
    private int get_until(int INDEX, StringBuilder code)
    {
        log.log(log_key, 4, "get_until call, INDEX = " + INDEX);
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'U') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'T') &&
                  (code.charAt(INDEX + 3) == 'I') &&
                  (code.charAt(INDEX + 4) == 'L') )
            {
                log.log(log_key, 4, "get_until return, INDEX = " + INDEX);
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
    private String convert_condition(String code)
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
