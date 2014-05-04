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
 * file: Icarus/src/interpreter/Interpreter.java
 * vim: foldmethod=syntax:foldcolumn=5:
 */

package interpreter;

import vault.*;
import parser.*;

import javax.script.ScriptEngine;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class where the main interpret function is located.
 * <p>
 * @version 0.2
 */
public class Interpreter
{
    String log_key = "Interpreter";
    LogWriter       log;
    Keyword_Handler handler;

    /**
     * @param container used parser
     * @param log used LogWriter
     * @param engine Script engine to evaluate
     */
    public Interpreter(InfoCollector container, LogWriter log, ScriptEngine engine)
    {
        log.log(log_key, 4, "init Interpreter...");
        this.log       = log;

        handler = new Keyword_Handler(container, log, engine);
        log.log(log_key, 4, "init Interpreter done.");
    }


    /**
     * will interpret given code from given start till end INDEX.
     * @param code structure text code after parser
     * @param start INDEX where interpreter will start
     * @param end INDEX where interpreter will stop
     * @param engine ScriptEngine which will be used.
     * @throws exception Exception
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
                log.log(log_key, 3, "found Keyword PROGRAM,      INDEX = " + INDEX);
                INDEX = handler.found_PROGRAM(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'I') &&
                      (code.charAt(INDEX + 1) == 'F') )
            {
                log.log(log_key, 3, "found Keyword IF,           INDEX = " + INDEX);
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
                log.log(log_key, 3, "found Keyword END_IF,       INDEX = " + INDEX);
                INDEX = handler.found_END_IF(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'E') )
            {
                log.log(log_key, 3, "found Keyword ELSE,         INDEX = " + INDEX);
                INDEX = handler.found_ELSE(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'L') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'F') )
            {
                log.log(log_key, 3, "found Keyword ELSIF,        INDEX = " + INDEX);
                INDEX = handler.found_ELSIF(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'W') &&
                      (code.charAt(INDEX + 1) == 'H') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'L') &&
                      (code.charAt(INDEX + 4) == 'E') )
            {
                log.log(log_key, 3, "found Keyword WHILE,        INDEX = " + INDEX);
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
                log.log(log_key, 3, "found Keyword END_WHILE     INDEX = " + INDEX);
                INDEX = handler.found_END_WHILE(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'F') &&
                      (code.charAt(INDEX + 1) == 'O') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                log.log(log_key, 3, "found Keyword FOR,          INDEX = " + INDEX);
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
                log.log(log_key, 3, "found Keyword END_FOR,      INDEX = " + INDEX);
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
                log.log(log_key, 3, "found Keyword REPEAT,       INDEX = " + INDEX);
                INDEX = handler.found_REPEAT(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'U') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'T') &&
                      (code.charAt(INDEX + 3) == 'I') &&
                      (code.charAt(INDEX + 4) == 'L') )
            {
                log.log(log_key, 3, "found Keyword UNTIL,        INDEX = " + INDEX);
                INDEX = handler.found_UNTIL(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'B') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'E') &&
                      (code.charAt(INDEX + 3) == 'A') &&
                      (code.charAt(INDEX + 4) == 'K') )
            {
                log.log(log_key, 3, "found Keyword BREAK,        INDEX = " + INDEX);
                INDEX = handler.found_BREAK(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'P') &&
                      (code.charAt(INDEX + 1) == 'R') &&
                      (code.charAt(INDEX + 2) == 'I') &&
                      (code.charAt(INDEX + 3) == 'N') &&
                      (code.charAt(INDEX + 4) == 'T') )
            {
                log.log(log_key, 3, "found Keyword PRINT,        INDEX = " + INDEX);
                INDEX = handler.found_PRINT(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'V') &&
                      (code.charAt(INDEX + 1) == 'A') &&
                      (code.charAt(INDEX + 2) == 'R') )
            {
                log.log(log_key, 3, "found Keyword VAR,          INDEX = " + INDEX);
                INDEX = handler.found_VAR(INDEX, code);
                continue;
            }
            else if ( (code.charAt(INDEX)     == 'C') &&
                      (code.charAt(INDEX + 1) == 'A') &&
                      (code.charAt(INDEX + 2) == 'S') &&
                      (code.charAt(INDEX + 3) == 'E') )
            {
                log.log(log_key, 3, "found Keyword CASE,         INDEX = " + INDEX);
                handler.found_CASE(INDEX, code);
                break;
            }
            else if ( (code.charAt(INDEX)     == 'F') &&
                      (code.charAt(INDEX + 1) == 'U') &&
                      (code.charAt(INDEX + 2) == 'N') &&
                      (code.charAt(INDEX + 3) == 'C') &&
                      (code.charAt(INDEX + 4) == 'T') &&
                      (code.charAt(INDEX + 5) == 'I') &&
                      (code.charAt(INDEX + 6) == 'O') &&
                      (code.charAt(INDEX + 7) == 'N') )
            {
                log.log(log_key, 3, "found Keyword FUNCTION,     INDEX = " + INDEX);
                handler.found_FUNCTION(INDEX, code);
                break;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'F') &&
                      (code.charAt(INDEX + 5) == 'U') &&
                      (code.charAt(INDEX + 6) == 'N') &&
                      (code.charAt(INDEX + 7) == 'C') &&
                      (code.charAt(INDEX + 8) == 'T') &&
                      (code.charAt(INDEX + 9) == 'I') &&
                      (code.charAt(INDEX + 10)== 'O') &&
                      (code.charAt(INDEX + 11)== 'N') )
            {
                log.log(log_key, 3, "found Keyword END_FUNCTION, INDEX = " + INDEX);
                handler.found_END_FUNCTION(INDEX, code);
                break;
            }
            else if ( (code.charAt(INDEX)     == 'E') &&
                      (code.charAt(INDEX + 1) == 'N') &&
                      (code.charAt(INDEX + 2) == 'D') &&
                      (code.charAt(INDEX + 3) == '_') &&
                      (code.charAt(INDEX + 4) == 'C') &&
                      (code.charAt(INDEX + 5) == 'A') &&
                      (code.charAt(INDEX + 6) == 'S') &&
                      (code.charAt(INDEX + 7) == 'E') )
            {
                log.log(log_key, 3, "found Keyword END_CASE,     INDEX = " + INDEX);
                handler.found_END_CASE(INDEX, code);
                break;
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
                log.log(log_key, 3, "found Keyword END_PROGRAM,  INDEX = " + INDEX);
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
    /* thats some nice code right there.
    public void interpret_case (String code, int start, int end, ScriptEngine engine) throws Exception
    {
        log.log(log_key, 4, "started with start: " + start + ", end: " + end);
        for(int INDEX = start; INDEX < end; INDEX++)
        {
            switch( code.charAt(INDEX) )
            {
                case 'B':
                    if( (code.charAt(INDEX + 1) == 'R') &&
                        (code.charAt(INDEX + 2) == 'E') &&
                        (code.charAt(INDEX + 3) == 'A') &&
                        (code.charAt(INDEX + 4) == 'K') )
                    {
                        INDEX = handler.found_BREAK(INDEX, code);
                        continue;
                    }
                case 'C':
                    if( (code.charAt(INDEX + 1) == 'A') &&
                        (code.charAt(INDEX + 2) == 'S') &&
                        (code.charAt(INDEX + 3) == 'E') )
                    {
                        INDEX = handler.found_CASE(INDEX, code);
                        continue;
                    }
                case 'F':
                    if( (code.charAt(INDEX + 1) == 'O') &&
                        (code.charAt(INDEX + 2) == 'R') )
                    {
                        INDEX = handler.found_FOR(INDEX, code);
                        continue;
                    }
                    else if( (code.charAt(INDEX + 1) == 'U') &&
                             (code.charAt(INDEX + 2) == 'N') &&
                             (code.charAt(INDEX + 3) == 'C') &&
                             (code.charAt(INDEX + 4) == 'T') &&
                             (code.charAt(INDEX + 5) == 'I') &&
                             (code.charAt(INDEX + 6) == 'O') &&
                             (code.charAt(INDEX + 7) == 'N') )
                    {
                        INDEX = handler.found_FUNCTION(INDEX, code);
                        continue;
                    }
                case 'I':
                    if( (code.charAt(INDEX + 1) == 'F') )
                    {
                        INDEX = handler.found_IF(INDEX, code);
                        continue;
                    }
                case 'P':
                    if( (code.charAt(INDEX + 1) == 'R') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'N') &&
                        (code.charAt(INDEX + 4) == 'T') )
                    {
                        INDEX = handler.found_PRINT(INDEX, code);
                        continue;
                    }
                case 'R':
                    if( (code.charAt(INDEX + 1) == 'E') &&
                        (code.charAt(INDEX + 2) == 'P') &&
                        (code.charAt(INDEX + 3) == 'E') &&
                        (code.charAt(INDEX + 4) == 'A') &&
                        (code.charAt(INDEX + 5) == 'T') )
                    {
                        INDEX = handler.found_REPEAT(INDEX, code);
                        continue;
                    }
                case 'U':
                    if( (code.charAt(INDEX + 1) == 'N') &&
                        (code.charAt(INDEX + 2) == 'T') &&
                        (code.charAt(INDEX + 3) == 'I') &&
                        (code.charAt(INDEX + 4) == 'L') )
                    {
                        INDEX = handler.found_UNTIL(INDEX, code);
                        continue;
                    }
                case 'V':
                    if( (code.charAt(INDEX + 1) == 'A') &&
                        (code.charAt(INDEX + 2) == 'R') )
                    {
                        INDEX = handler.found_VAR(INDEX, code);
                        continue;
                    }
                case 'W':
                    if( (code.charAt(INDEX + 1) == 'H') &&
                        (code.charAt(INDEX + 2) == 'I') &&
                        (code.charAt(INDEX + 3) == 'L') &&
                        (code.charAt(INDEX + 4) == 'E') )
                    {
                        INDEX = handler.found_WHILE(INDEX, code);
                        continue;
                    }
                case 'E':
                    switch (code.charAt(INDEX + 1))
                    {
                        case 'L':
                            if( (code.charAt(INDEX + 2) == 'S') &&
                                (code.charAt(INDEX + 3) == 'E') )
                            {
                                if( (code.charAt(INDEX + 4) == 'I') &&
                                    (code.charAt(INDEX + 5) == 'F') )
                                {
                                    handler.found_ELSIF(INDEX, code);
                                    continue;
                                }
                                INDEX = handler.found_ELSE(INDEX, code);
                                continue;
                            }
                        case 'N':
                            if( (code.charAt(INDEX + 2) == 'D') &&
                                (code.charAt(INDEX + 3) == '_') )
                            {
                                switch (code.charAt(INDEX + 4))
                                {
                                    case 'C':
                                        if( (code.charAt(INDEX + 5) == 'A') &&
                                            (code.charAt(INDEX + 6) == 'S') &&
                                            (code.charAt(INDEX + 7) == 'E') )
                                        {
                                            handler.found_END_CASE(INDEX, code);
                                            continue;
                                        }
                                    case 'F':
                                        if( (code.charAt(INDEX + 5) == 'O') &&
                                            (code.charAt(INDEX + 6) == 'R') )
                                         {
                                             handler.found_FOR(INDEX, code);
                                             continue;
                                         }
                                         else if( (code.charAt(INDEX + 5) == 'U') &&
                                                  (code.charAt(INDEX + 6) == 'N') &&
                                                  (code.charAt(INDEX + 7) == 'C') &&
                                                  (code.charAt(INDEX + 8) == 'T') &&
                                                  (code.charAt(INDEX + 9) == 'I') &&
                                                  (code.charAt(INDEX + 10)== 'O') &&
                                                  (code.charAt(INDEX + 11)== 'N') )
                                         {
                                            handler.found_END_FUNCTION(INDEX, code);
                                            continue;
                                         }
                                    case 'I':
                                        if( (code.charAt(INDEX + 5) == 'F') )
                                        {
                                            handler.found_END_IF(INDEX, code);
                                            continue;
                                        }
                                    case 'P':
                                        if( (code.charAt(INDEX + 5)  == 'R') &&
                                            (code.charAt(INDEX + 6)  == 'O') &&
                                            (code.charAt(INDEX + 7)  == 'G') &&
                                            (code.charAt(INDEX + 8)  == 'R') &&
                                            (code.charAt(INDEX + 9)  == 'A') &&
                                            (code.charAt(INDEX + 10) == 'M') )
                                        {
                                            handler.found_END_PROGRAM(INDEX, code);
                                            continue;
                                        }
                                    case 'W':
                                        if( (code.charAt(INDEX + 5)  == 'H') &&
                                            (code.charAt(INDEX + 6)  == 'I') &&
                                            (code.charAt(INDEX + 7)  == 'L') &&
                                            (code.charAt(INDEX + 8)  == 'E') )
                                            {
                                                handler.found_END_WHILE(INDEX, code);
                                                continue;
                                            }
                                }
                            }
                    }
                default:
            }
        }
    }
    */
}
