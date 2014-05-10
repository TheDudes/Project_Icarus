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
 * vim: foldmethod=syntax:foldcolumn=4:
 */

package interpreter;

import vault.*;
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
    final private LogWriter    log;
    final private String       log_key = "engine";

    /**
     * @param log LogWriter Object
     * @param config Config_Reader Object
     */
    public Engine(LogWriter log, Config_Reader config)
    {
        this.log = log;
        log.log(log_key, 4, "init Engine...");
        ScriptEngineManager factory = new ScriptEngineManager();
        engine                      = factory.getEngineByName("JavaScript");
        if(config.get_boolean("Engine_Warmup"))
            warmup();
        log.log(log_key, 4, "init Engine done.");
    }

    /**
     * used to warmup the Java ScriptEngine.
     * @return ScriptEngine after warmup
     */
    public ScriptEngine warmup()
    {

        int total_evaluations = 2000;
        double avg = 0.0;

        log.log("engine", 0,"starting engine warmup...");
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

            eval(test0);
            eval(test1);
            eval(test2);
            eval(test3);
            eval(test4);
            eval(test5);
            eval(test6);
            eval(test7);
            eval(test8);
            eval(test9);

            now = System.currentTimeMillis() - now;

            avg += now;
            if (i % 100 == 0 && i != 0)
            {
                avg = avg / 100;
                log.log("engine", 2,"100 evaluations done, avg time: (" + avg + "ms avg)");
            }
        }
        log.log("engine", 2, "100 evaluations done, avg time: (" + avg / 100 + "ms avg)");
        log.log("engine", 0, "engine warmup finished, total evaluations: "
                                    + total_evaluations
                                    + ", total time: "
                                    + (System.currentTimeMillis() - start)
                                    + "ms");
        return engine;
    }

    /**
     * used to evaluate conditions.
     * @param condition String with the condition
     * @return Object which holds the evaluated value
     */
    public Object eval(String condition)
    {
        log.log(log_key, 4, "call_engine_eval with: " + condition);
        Object obj = new Object();
        try
        {
            obj = engine.eval(condition);
        }
        catch(ScriptException e)
        {
            log.log(log_key, 0, "could not evalue condition:" + condition);
            log.kill();
            System.exit(1);
        }
        catch(NullPointerException e)
        {
            log.log(log_key, 0, "could not evalue condition:" + condition);
            log.kill();
            System.exit(1);
        }
        log.log(log_key, 4, "return_engine_eval with: " + condition);
        return obj;
    }
}
