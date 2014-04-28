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
 * @author: d4ryus - https://github.com/d4ryus/
 * @version 0.2
 */

package interpreter;

import vault.*;

import java.io.*;
import java.net.*;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * used to warmup the Java ScriptEngine.
 * @param engine the engine which will be used
 */
public class Engine_Warmup
{

    ScriptEngine engine;
    LogWriter log;

    public Engine_Warmup(LogWriter log)
    {
        this.log = log;
        ScriptEngineManager factory = new ScriptEngineManager();
        engine                      = factory.getEngineByName("JavaScript");
    }


    /**
     * used to warmup the Java ScriptEngine.
     */
    public ScriptEngine engine_warmup() throws Exception
    {

        int total_evaluations = 2000;
        double avg = 0.0;

        log.log("engine", 1,"starting engine warmup...");
        long start = System.currentTimeMillis();

        for(int i = 0; i < total_evaluations; i++) {

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

            boolean result0 = (boolean)engine.eval(test0);
            boolean result1 = (boolean)engine.eval(test1);
            boolean result2 = (boolean)engine.eval(test2);
            double  result3 = (double)engine.eval(test3);
            double  result4 = (double)engine.eval(test4);
            double  result5 = (double)engine.eval(test5);
            boolean result6 = (boolean)engine.eval(test6);
            boolean result7 = (boolean)engine.eval(test7);
            boolean result8 = (boolean)engine.eval(test8);
            boolean result9 = (boolean)engine.eval(test9);

            now = System.currentTimeMillis() - now;

            avg += now;
            if (i % 100 == 0 && i != 0) {
                avg = avg / 100;
                try {
                    log.log("engine", 2,"100 evaluations done, avg time: (" + avg + "ms avg)");
                } catch (Exception e) {
                }
            }
        }
        log.log("engine", 2, "100 evaluations done, avg time: (" + avg / 100 + "ms avg)");
        log.log("engine", 1, "engine warmup finished, total evaluations: "
                                    + total_evaluations
                                    + ", total time: "
                                    + (System.currentTimeMillis() - start)
                                    + "ms");
        return engine;
    }
}
