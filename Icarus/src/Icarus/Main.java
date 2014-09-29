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
 * file: /src/Icarus/Main.java
 * vim: foldmethod=syntax:
 */

package Icarus;

import logger.*;
import parser.*;
import config.*;
import interpreter.*;
import IOInterface.*;

import java.util.Properties;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * Main, here is where the magic happens.
 * <p>
 * @version 0.85
 */
public class Main
{
    final private static String log_key = " [main]: ";

          private static Config_Reader   config      = null;
          private static Logger          log         = null;
          private static ParserContainer container   = null;
          private static Interpreter     interpreter = null;
          private static IO_Manager      io          = null;
          private static Properties      propertie;
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
        log.

        /* Set System Property to make it easy to ignore logs */
            propertie = new Properties(System.getProperties());
            propertie.setProperty("loglevel", Integer.toHexString(config.get_int("verbosity_level", 0, 4)));
            System.setProperties(propertie);
        container   = new ParserContainer(config, log);
        io          = new IO_Manager(log, config, container);
        interpreter = new Interpreter(container, log, config);

        log.log(0, log_key, "starting Icarus.\n");

        String code = container.get_all_the_code().toString();

        for(int i = 0; i < 1000; i++ )
            interpreter.interpret(code, 0, code.length());

        log.log(0, log_key, "exiting Icarus.\n");
        exit();
    }

    private static void print_startup_message()
    {
        System.out.println("Staring Icarus Structure Text Interpreter!");
        System.out.println("version: 0.85 (Alpha! but pretty close to beta now)");
    }

    /**
     * exit function to exit Icarus and kill all threads
     */
    public static void exit()
    {
        if(io != null)
        {
            log.log(0, log_key, "killing io.\n");
            io.kill();
        }
        if(log != null)
        {
            log.log(0, log_key, "killing log.\n");
            log.kill();
        }
        System.out.print("exiting this awesome interpreter\n");
        System.exit(1);
    }
}
