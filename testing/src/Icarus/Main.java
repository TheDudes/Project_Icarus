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
 * file: Icarus/src/Icarus/main.java
 * @author d4ryus - https://github.com/d4ryus/
 * @version 0.8
 * vim: foldmethod=syntax:
 */

package Icarus;

import java.util.Properties;

import parser.*;

import config.*;

import interpreter.*;

import logger.*;
import IOInterface.*;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * Main, here is where the magic happens.
 * <p>
 * @version 0.8
 */
public class Main
{
    final static String log_key = " [main]: ";
          static String config_path;
          static String hostname;
          static String code;

          static Config_Reader config;
          static Logger        log;
          static ParserContainer container;
          static Interpreter   interpreter;
          static SynchronousIO io;

        static Properties propertie;
    /**
     * main funcion which will start Icarus
     * @param args not used yet
     * @throws Exception --fixme--
     */
    public static void main(String... args) throws Exception
    {
        print_startup_message();

        config      = new Config_Reader("./icarus.conf");
        log         = new Logger(config);
        /* Set System Property to make it easy to ignore logs */
            propertie = new Properties(System.getProperties());
            propertie.setProperty("loglevel", Integer.toHexString(config.get_int("verbosity_level", 0, 4)));
            System.setProperties(propertie);
        container   = new ParserContainer(config, log);
        interpreter = new Interpreter(container, log, config);
        io          = new SynchronousIO(log, config, container);

        
            
        
        log.log(0, log_key, "starting Icarus.\n");
        double blub = 0.0;
        for(int i = 0; i < 10; i++ )
        {
            blub = System.currentTimeMillis();

            code = container.get_all_the_code().toString();
            interpreter.interpret(code, 0, code.length());

            log.log(0, log_key, new Double(System.currentTimeMillis() - blub).toString(), "ms.\n");
        }

        log.log(0, log_key, "exiting Icarus.\n");
        log.kill();
    }

    private static void print_startup_message()
    {
        System.out.println("Staring Icarus Structure Text Interpreter!");
        System.out.println("verion: 0.8 (Alpha!)");
    }
}
