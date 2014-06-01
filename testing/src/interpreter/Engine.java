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
 * file: Icarus/src/interpreter/Engine_Warmup.java
 * vim: foldmethod=syntax:
 */

package interpreter;

import logger.*;
import linc.*;

import javax.script.*;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class which is used to warmup the JavaScript engine, the engine
 * is used to evaluate conditions.
 * <p>
 * @version 0.8
 */
public class Engine
{
    final private ScriptEngine engine;
    final private Logger       log;
    final private String       log_key = " [engine]: ";

    /**
     * @param log Logger Object
     * @param config Config_Reader Object
     */
    public Engine(Logger log, Config_Reader config)
    {
        this.log = log;
        log.log(2, log_key, "init Engine...\n");
        ScriptEngineManager factory = new ScriptEngineManager();
        engine                      = factory.getEngineByName("JavaScript");
        if(config.get_boolean("Engine_Warmup"))
            warmup();
        log.log(2, log_key, "init Engine done.\n");
    }

    /**
     * used to warmup the Java ScriptEngine.
     * @return ScriptEngine after warmup
     */
    public ScriptEngine warmup()
    {

        int total_evaluations = 10000;
        double avg = 0.0;

        log.log(0, log_key, "starting engine warmup...\n");
        long start = System.currentTimeMillis();

        for(int i = 0; i < total_evaluations; i++)
        {

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

            eval(test0, false);
            eval(test1, false);
            eval(test2, false);
            eval(test3, false);
            eval(test4, false);
            eval(test5, false);
            eval(test6, false);
            eval(test7, false);
            eval(test8, false);
            eval(test9, false);

            now = System.currentTimeMillis() - now;

            avg += now;
            if (i % 100 == 0 && i != 0)
            {
                avg = avg / 100;
                log.log(0, i, total_evaluations, 1, 40, log_key, "engine warmup...");
                log.log(2, log_key, "100 evaluations done, avg time: (", new Double(avg).toString(), "ms avg)\n");
            }
        }
        log.log(2, log_key, "100 evaluations done, avg time: (", new Double(avg / 100).toString(), "ms avg)\n");
        log.log(0, log_key, "engine warmup finished, total evaluations:",
                                    new Double(total_evaluations).toString(),
                                    ", total time: ",
                                    new Double(System.currentTimeMillis() - start).toString(),
                                    "ms\n");
        return engine;
    }

    /**
     * used to evaluate conditions.
     * @param condition String with the condition
     * @param convert true if condition should be converted
     *                from structure style to java style
     * @return Object which holds the evaluated value
     */
    public Object eval(String condition, boolean convert)
    {
        log.log(4, log_key, "call_engine_eval with: ", condition, "\n");
        if (convert)
            condition = convert_condition(condition);
        Object obj = new Object();
        try
        {
            obj = engine.eval(condition);
        }
        catch(ScriptException e)
        {
            log.log(0, log_key, "could not evalue condition:", condition, "\n");
            log.kill();
            System.exit(1);
        }
        catch(NullPointerException e)
        {
            log.log(0, log_key, "could not evalue condition:", condition, "\n");
            log.kill();
            System.exit(1);
        }
        log.log(4,  log_key, "return_engine_eval with: ", condition, "\n");
        return obj;
    }

    /**
     * convert given string so that the Java Script Engine can Interpret it,
     * will replace following matches:
     * |FROM     | TO            |
     * +---------+---------------+
     * |'='      | ' == '        |
     * |'TRUE'   | ' true '      |
     * |'FALSE'  | ' false '     |
     * |'XOR'    | ' ^ '         |
     * |'AND'    | ' && '        |
     * |'NOT'    | ' !'          |
     * |'&'      | ' && '        |
     * |'<>'     | ' != '        |
     * |'MOD'    | ' % '         |
     * |'OR'     | ' || '        |
     * |'SIN()'  | 'Math.sin()'  |
     * |'COS()'  | 'Math.cos()'  |
     * |'TAN()'  | 'Math.tan()'  |
     * |'ASIN()' | 'Math.asin()' |
     * |'ACOS()' | 'Math.acos()' |
     * |'ATAN()' | 'Math.atan()' |
     * |'LOG()'  | 'Math.log10()'|
     * |'EXP()'  | 'Math.exp()'  |
     * |'LN()'   | 'Math.log()'  |
     * |'SQRT()' | 'Math.sqrt()' |
     * +---------+---------------+
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
