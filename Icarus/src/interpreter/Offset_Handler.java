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
 *
 * file: Icarus/src/interpreter/Offset.java
 * vim: foldmethod=syntax:foldcolumn=5:
 */

package interpreter;

import logger.*;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class which holds all the offset related Functions, these are used
 * to get the index of the next Keyword, for example.
 * <p>
 * @version 0.8
 */
public class Offset_Handler
{
    final private String    log_key = " [Interpreter-Offset]: ";
    final private Logger log;

    /**
     * @param log used LogWriter
     */
    public Offset_Handler(Logger log)
    {
        log.log(2, log_key, "init Offset_Handler...\n");

        this.log       = log;

        log.log(2, log_key, "init Offset_Handler done.\n");
    }

    /**
     * will return the next keyword found, these are ELSEIF ELSE END_IF
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found keyword
     */
    public int get_next_keyword(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_next_keyword, INDEX = ", new Integer(INDEX).toString(), "\n");

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
                log.log(4, log_key, "return get_next_keyword, INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            }
            else if ( (code.charAt(INDEX)     == 'I') &&
                      (code.charAt(INDEX + 1) == 'F') )
            {
                INDEX = get_END_IF(INDEX, code) + 5;
                continue;
            }
        }
    }

    /**
     * will return next colon index (only used by found_FOR)
     * @param string containing colon
     * @return ińdex of colon
     */
    public int get_colon(String string)
    {
        log.log(4, log_key, "call   get_colon, string = ", string, "\n");
        for(int i = 0;;i++)
        {
            if (string.charAt(i) == ':')
            {
                log.log(4, log_key, "return get_colon, i = ", new Integer(i).toString(), "\n");
                return i;
            }
        }
    }

    /**
     * will return next Semicolon index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found semicolon
     */
    public int get_semicolon(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_semicolon, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == ';') )
            {
                log.log(4, log_key, "return get_semicolon , INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            }
        }
    }

    /**
     * will return next comma index (only used by found_PRINT)
     * @param string containing colon
     * @return ińdex of colon
     */
    public int get_comma(String string)
    {
        log.log(4, log_key, "call   get_comma, string = ", string, "\n");
        for(int i = 0;;i++)
        {
            if (string.charAt(i) == ',')
            {
                log.log(4, log_key, "return get_comma, i = ", new Integer(i).toString(), "\n");
                return i;
            }
        }
    }

    /**
     * will return next OF index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found DO
     */
    public int get_OF(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_OF, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'O') &&
                 (code.charAt(INDEX + 1) == 'F') )
            {
                log.log(4, log_key, "return get_OF, INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            }
        }
    }

    /**
     * will return next VAR index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found VAR
     */
    public int get_VAR(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_VAR, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'V') &&
                 (code.charAt(INDEX + 1) == 'A') &&
                 (code.charAt(INDEX + 2) == 'R') )
            {
                log.log(4, log_key, "return get_VAR, INDEX = ", new Integer(INDEX).toString(), "\n");
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
    public int get_TO(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_TO, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(4, log_key, "return get_TO, INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            }
        }
    }

    /**
     * will return next BY index
     * @param INDEX starting point
     * @param code code from parser
     * @return INDEX of found BY, -1 if DO is found
     */
    public int get_BY(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_BY, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++)
        {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(4, log_key, "return get_BY (DO found): -1\n");
                return -1;
            }
            else if ( (code.charAt(INDEX)     == 'B') &&
                      (code.charAt(INDEX + 1) == 'Y') )
            {
                log.log(4, log_key, "return get_BY , (BY found) INDEX = ", new Integer(INDEX).toString(), "\n");
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
    public int get_DO(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_DO, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++)
        {
            if ( (code.charAt(INDEX)     == 'D') &&
                 (code.charAt(INDEX + 1) == 'O') )
            {
                log.log(4, log_key, "return get_DO, INDEX = ", new Integer(INDEX).toString(), "\n");
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
    public int get_THEN(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_THEN, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if ( (code.charAt(INDEX)     == 'T') &&
                 (code.charAt(INDEX + 1) == 'H') &&
                 (code.charAt(INDEX + 2) == 'E') &&
                 (code.charAt(INDEX + 3) == 'N') )
            {
                log.log(4, log_key, "return get_THEN, INDEX = ", new Integer(INDEX).toString(), "\n");
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
    public int get_END_IF(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_END_IF, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'E') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'D') &&
                  (code.charAt(INDEX + 3) == '_') &&
                  (code.charAt(INDEX + 4) == 'I') &&
                  (code.charAt(INDEX + 5) == 'F') )
            {
                log.log(4, log_key, "return get_END_IF, INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            }
            else if ( (code.charAt(INDEX)     == 'I') &&
                      (code.charAt(INDEX + 1) == 'F') )
            {
                log.log(4, log_key, "recursive call get_END_IF, INDEX = ", new Integer(INDEX).toString(), "\n");
                INDEX = get_END_IF(INDEX, code) + 5;
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
    public int get_UNTIL(int INDEX, String code)
    {
        log.log(4, log_key, "call   get_UNTIL, INDEX = ", new Integer(INDEX).toString(), "\n");
        for(;;INDEX++) {
            if  ( (code.charAt(INDEX)     == 'U') &&
                  (code.charAt(INDEX + 1) == 'N') &&
                  (code.charAt(INDEX + 2) == 'T') &&
                  (code.charAt(INDEX + 3) == 'I') &&
                  (code.charAt(INDEX + 4) == 'L') )
            {
                log.log(4, log_key, "return get_UNTIL, INDEX = ", new Integer(INDEX).toString(), "\n");
                return INDEX;
            } else if ( (code.charAt(INDEX)     == 'R') &&
                        (code.charAt(INDEX + 1) == 'E') &&
                        (code.charAt(INDEX + 2) == 'P') &&
                        (code.charAt(INDEX + 3) == 'E') &&
                        (code.charAt(INDEX + 4) == 'A') &&
                        (code.charAt(INDEX + 5) == 'T') )
            {
                log.log(4, log_key, "recursive call get_UNTIL, INDEX = ", new Integer(INDEX).toString(), "\n");
                INDEX = get_UNTIL(INDEX, code) + 5;
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
    public String convert_condition(String code)
    {
        log.log(4, log_key, "convert_condition call with: ", code, "\n");
        String final_condition = "";
        int spot = 0;

        for( ; spot < code.length(); spot++ )
        {
            if (        (code.charAt(spot)     == '=') )
            {
                final_condition += " == ";
                continue;
            }
            else if (   (code.charAt(spot)     == 'T') &&
                        (code.charAt(spot + 1) == 'R') &&
                        (code.charAt(spot + 2) == 'U') &&
                        (code.charAt(spot + 3) == 'E') )
            {
                final_condition += " true ";
                spot += 3;
                continue;
            }
            else if (   (code.charAt(spot)     == 'F') &&
                        (code.charAt(spot + 1) == 'A') &&
                        (code.charAt(spot + 2) == 'L') &&
                        (code.charAt(spot + 3) == 'S') &&
                        (code.charAt(spot + 4) == 'E') )
            {
                final_condition += " false ";
                spot += 4;
                continue;
            }
            else if (   (code.charAt(spot)     == 'X') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'R') )
            {
                final_condition += " ^ ";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'N') &&
                        (code.charAt(spot + 2) == 'D') )
            {
                final_condition += " && ";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'N') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'T') )
            {
                final_condition += " !";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == '&') )
            {
                final_condition += " && ";
                continue;
            }
            else if (   (code.charAt(spot)     == '<') &&
                        (code.charAt(spot + 1) == '>') )
            {
                final_condition += " != ";
                spot += 1;
                continue;
            }
            else if (   (code.charAt(spot)     == 'M') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'D') )
            {
                final_condition += " % ";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'O') &&
                        (code.charAt(spot + 1) == 'R') )
            {
                final_condition += " || ";
                spot += 1;
                continue;
            }
            else if (   (code.charAt(spot)     == 'S') &&
                        (code.charAt(spot + 1) == 'I') &&
                        (code.charAt(spot + 2) == 'N') )
            {
                final_condition += " Math.sin";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'T') &&
                        (code.charAt(spot + 1) == 'A') &&
                        (code.charAt(spot + 2) == 'N') )
            {
                final_condition += " Math.tan";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'S') &&
                        (code.charAt(spot + 2) == 'I') &&
                        (code.charAt(spot + 3) == 'N') )
            {
                final_condition += " Math.asin";
                spot += 3;
                continue;
            }
            else if (   (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'C') &&
                        (code.charAt(spot + 2) == 'O') &&
                        (code.charAt(spot + 3) == 'S') )
            {
                final_condition += " Math.acos";
                spot += 3;
                continue;
            }
            else if (   (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'T') &&
                        (code.charAt(spot + 2) == 'A') &&
                        (code.charAt(spot + 3) == 'N') )
            {
                final_condition += " Math.atan";
                spot += 3;
                continue;
            }
            else if (   (code.charAt(spot)     == 'L') &&
                        (code.charAt(spot + 1) == 'O') &&
                        (code.charAt(spot + 2) == 'G') )
            {
                final_condition += " Math.log10";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'E') &&
                        (code.charAt(spot + 1) == 'X') &&
                        (code.charAt(spot + 2) == 'P') )
            {
                final_condition += " Math.exp";
                spot += 2;
                continue;
            }
            else if (   (code.charAt(spot)     == 'L') &&
                        (code.charAt(spot + 1) == 'N') )
            {
                final_condition += " Math.log";
                spot += 1;
                continue;
            }
            else if (   (code.charAt(spot)     == 'S') &&
                        (code.charAt(spot + 1) == 'Q') &&
                        (code.charAt(spot + 2) == 'R') &&
                        (code.charAt(spot + 3) == 'T') )
            {
                final_condition += " Math.sqrt";
                spot += 3;
                continue;
            }
            else if (   (code.charAt(spot)     == 'A') &&
                        (code.charAt(spot + 1) == 'B') &&
                        (code.charAt(spot + 2) == 'S') )
            {
                final_condition += " Math.abs";
                spot += 2;
                continue;
            }
            final_condition += code.charAt(spot);
        }
        log.log(4, log_key, "convert_condition return with: ", final_condition, "\n");
        return final_condition;
    }
}
