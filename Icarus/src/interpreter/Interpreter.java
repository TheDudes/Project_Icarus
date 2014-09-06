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
 * file: /src/interpreter/Interpreter.java
 * vim: foldmethod=syntax:
 */

package interpreter;

import logger.*;
import parser.*;
import config.*;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class where the main interpret function is located.
 * <p>
 * @version 0.85
 */
public class Interpreter
{
    final private String log_key = " [Interpreter]: ";
    final private Logger log;
    final private Keyword_Handler handler;

    /**
     * @param container used parser
     * @param log used LogWriter
     * @param config Config_Reader object which will be passed to Engine
     */
    public Interpreter(ParserContainer container, Logger log, Config_Reader config)
    {
        log.log(2, log_key, "init Interpreter...\n");

        this.log       = log;
        handler        = new Keyword_Handler(container, log, config, this);

        log.log(2, log_key, "init Interpreter done.\n");
    }

    /**
     * will interpret given code from given start till end INDEX.
     * @param code structure text code after parser
     * @param start INDEX where interpreter will start
     * @param end INDEX where interpreter will stop
     * @throws Exception --fixme--
     */
    public void interpret (String code, int start, int end) throws Exception
    {
        log.log(3, log_key, "call interpret: ", new Integer(start).toString(),  ", end: ", new Integer(end).toString(), "\n");
        int INDEX = start;
        char[] codeArray = code.toCharArray();
        for(; INDEX < end; INDEX++)
        {
            log.log(4, log_key, "for_loop_top, INDEX = ", new Integer(INDEX).toString(), "\n");

            if (      (codeArray[INDEX]     == 'I') &&
                      (codeArray[INDEX + 1] == 'F') )
            {
                log.log(3, log_key, "found Keyword IF,           INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_IF(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'P') &&
                      (codeArray[INDEX + 1] == 'R') &&
                      (codeArray[INDEX + 2] == 'I') &&
                      (codeArray[INDEX + 3] == 'N') &&
                      (codeArray[INDEX + 4] == 'T') )
            {
                log.log(3, log_key, "found Keyword PRINT,        INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_PRINT(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'E') &&
                      (codeArray[INDEX + 1] == 'N') &&
                      (codeArray[INDEX + 2] == 'D') &&
                      (codeArray[INDEX + 3] == '_') )
            {
                if (      (codeArray[INDEX + 4] == 'I') &&
                          (codeArray[INDEX + 5] == 'F') )
                {
                    log.log(3, log_key, "found Keyword END_IF,       INDEX = ",
                                            new Integer(INDEX).toString(), "\n");
                    INDEX = handler.found_END_IF(INDEX, code);
                    continue;
                }
                else if ( (codeArray[INDEX + 4] == 'W') &&
                          (codeArray[INDEX + 5] == 'H') &&
                          (codeArray[INDEX + 6] == 'I') &&
                          (codeArray[INDEX + 7] == 'L') &&
                          (codeArray[INDEX + 8] == 'E') )
                {
                    log.log(3, log_key, "found Keyword END_WHILE     INDEX = ",
                                            new Integer(INDEX).toString(),
                                            "\n");
                    INDEX = handler.found_END_WHILE(INDEX, code);
                    continue;
                }
                else if ( (codeArray[INDEX + 4] == 'F') &&
                          (codeArray[INDEX + 5] == 'O') &&
                          (codeArray[INDEX + 6] == 'R') )
                {
                    log.log(3, log_key, "found Keyword END_FOR,      INDEX = ",
                                            new Integer(INDEX).toString(), "\n");
                    INDEX = handler.found_END_FOR(INDEX, code);
                    continue;
                }
                else if ( (codeArray[INDEX + 4] == 'F') &&
                          (codeArray[INDEX + 5] == 'U') &&
                          (codeArray[INDEX + 6] == 'N') &&
                          (codeArray[INDEX + 7] == 'C') &&
                          (codeArray[INDEX + 8] == 'T') &&
                          (codeArray[INDEX + 9] == 'I') &&
                          (codeArray[INDEX + 10]== 'O') &&
                          (codeArray[INDEX + 11]== 'N') )
                {
                    log.log(3, log_key, "found Keyword END_FUNCTION, INDEX = ",
                                            new Integer(INDEX).toString(), "\n");
                    handler.found_END_FUNCTION(INDEX, code);
                    break;
                }
                else if ( (codeArray[INDEX + 4] == 'P') &&
                          (codeArray[INDEX + 5] == 'R') &&
                          (codeArray[INDEX + 6] == 'O') &&
                          (codeArray[INDEX + 7] == 'G') &&
                          (codeArray[INDEX + 8] == 'R') &&
                          (codeArray[INDEX + 9] == 'A') &&
                          (codeArray[INDEX + 10]== 'M') )
                {
                    log.log(3, log_key, "found Keyword END_PROGRAM,  INDEX = ",
                                            new Integer(INDEX).toString(), "\n");
                    INDEX = handler.found_END_PROGRAM(INDEX, code);
                    break;
                }
            }
            else if ( (codeArray[INDEX]     == 'E') &&
                      (codeArray[INDEX + 1] == 'L') &&
                      (codeArray[INDEX + 2] == 'S') &&
                      (codeArray[INDEX + 3] == 'E') )
            {
                log.log(3, log_key, "found Keyword ELSE,         INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_ELSE(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'E') &&
                      (codeArray[INDEX + 1] == 'L') &&
                      (codeArray[INDEX + 2] == 'S') &&
                      (codeArray[INDEX + 3] == 'I') &&
                      (codeArray[INDEX + 4] == 'F') )
            {
                log.log(3, log_key, "found Keyword ELSIF,        INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_ELSIF(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'C') &&
                      (codeArray[INDEX + 1] == 'A') &&
                      (codeArray[INDEX + 2] == 'S') &&
                      (codeArray[INDEX + 3] == 'E') )
            {
                log.log(3, log_key, "found Keyword CASE,         INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_CASE(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'P') &&
                      (codeArray[INDEX + 1] == 'R') &&
                      (codeArray[INDEX + 2] == 'O') &&
                      (codeArray[INDEX + 3] == 'G') &&
                      (codeArray[INDEX + 4] == 'R') &&
                      (codeArray[INDEX + 5] == 'A') &&
                      (codeArray[INDEX + 6] == 'M') )
            {
                log.log(3, log_key, "found Keyword PROGRAM,      INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_PROGRAM(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'V') &&
                      (codeArray[INDEX + 1] == 'A') &&
                      (codeArray[INDEX + 2] == 'R') )
            {
                log.log(3, log_key, "found Keyword VAR,          INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_VAR(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'W') &&
                      (codeArray[INDEX + 1] == 'H') &&
                      (codeArray[INDEX + 2] == 'I') &&
                      (codeArray[INDEX + 3] == 'L') &&
                      (codeArray[INDEX + 4] == 'E') )
            {
                log.log(3, log_key, "found Keyword WHILE,        INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_WHILE(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'F') &&
                      (codeArray[INDEX + 1] == 'O') &&
                      (codeArray[INDEX + 2] == 'R') )
            {
                log.log(3, log_key, "found Keyword FOR,          INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_FOR(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'R') &&
                      (codeArray[INDEX + 1] == 'E') &&
                      (codeArray[INDEX + 2] == 'P') &&
                      (codeArray[INDEX + 3] == 'E') &&
                      (codeArray[INDEX + 4] == 'A') &&
                      (codeArray[INDEX + 5] == 'T') )
            {
                log.log(3, log_key, "found Keyword REPEAT,       INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_REPEAT(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'U') &&
                      (codeArray[INDEX + 1] == 'N') &&
                      (codeArray[INDEX + 2] == 'T') &&
                      (codeArray[INDEX + 3] == 'I') &&
                      (codeArray[INDEX + 4] == 'L') )
            {
                log.log(3, log_key, "found Keyword UNTIL,        INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_UNTIL(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'B') &&
                      (codeArray[INDEX + 1] == 'R') &&
                      (codeArray[INDEX + 2] == 'E') &&
                      (codeArray[INDEX + 3] == 'A') &&
                      (codeArray[INDEX + 4] == 'K') )
            {
                log.log(3, log_key, "found Keyword BREAK,        INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_BREAK(INDEX, code);
                continue;
            }
            else if ( (codeArray[INDEX]     == 'F') &&
                      (codeArray[INDEX + 1] == 'U') &&
                      (codeArray[INDEX + 2] == 'N') &&
                      (codeArray[INDEX + 3] == 'C') &&
                      (codeArray[INDEX + 4] == 'T') &&
                      (codeArray[INDEX + 5] == 'I') &&
                      (codeArray[INDEX + 6] == 'O') &&
                      (codeArray[INDEX + 7] == 'N') )
            {
                log.log(3, log_key, "found Keyword FUNCTION,     INDEX = ",
                                        new Integer(INDEX).toString(), "\n");
                INDEX = handler.found_FUNCTION(INDEX, code);
                continue;
            }
            else /* if no match is found */
            {
                INDEX = handler.found_nothing(INDEX, code);
                continue;
            }
        }
        log.log(3, log_key, "returning from interpret,   INDEX = ",
                                new Integer(INDEX).toString(), "\n");
    }
}
