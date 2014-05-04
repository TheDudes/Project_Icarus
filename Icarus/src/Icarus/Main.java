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
 * @version 0.2
 * vim: foldmethod=syntax:foldcolumn=5:
 */

package Icarus;

import vault.*;
import parser.*;
import linc.*;
import interpreter.*;

import java.net.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Main
{
    public static void main(String[] args) throws Exception
    {

        String  log_key = "main";
        String path;
        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname = InetAddress.getLocalHost().getHostName();

        switch (hostname)
        {
            case "beelzebub":
                path = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/Icarus/superduper_config";
                break;
            case "d4ryus":
            case "cubie":
                path = "/home/d4ryus/coding/Project_Icarus/Icarus/d4ryus_config";
                break;
            case "alarmpi":
                path = "/home/vault/Project_Icarus/Icarus/example_config";
                break;
            case "vault":
                path = "/home/vault/programing/NetBeansProjects/Project_Icarus/Icarus/example_config";
                break;
            case "csb.local":
                path = "/home/ninti/NetbeansProjects/Project_Icarus/Icarus/example_config";
                break;
            case "link":
                path = "/home/linc/Documents/defaultConfig";
                break;
            /*
             case "yourhostname":
             path[0] = "/your/path/to/the/st/file";
             path[1] = "/your/path/to/the/logfolder/";
             break;
             */
            default:
                System.out.print("no case for hostname: " + hostname + "\n");
                path = "";
                System.exit(0);
        }


        /* init */
        Config_Reader config   = new Config_Reader(path);
        LogWriter     logger   = new LogWriter(config);
        logger.log(log_key, 0, "hostname:         " + hostname);
        logger.log(log_key, 0, "config file path: " + path);
        logger.log(log_key, 0, "st file path:     " + config.get_path("path"));
        config.setLogWriter(logger);

        STFileFinder  stfinder = new STFileFinder(config, logger);

        InfoCollector container = new InfoCollector(stfinder.get_file_names(), logger);
        ScriptEngine  engine;

        if(config.get_boolean("Engine_Warmup"))
            engine               = new Engine_Warmup(logger).engine_warmup();
        else
            engine = new ScriptEngineManager().getEngineByName("JavaScript");

        Interpreter interpreter = new Interpreter(container, logger, engine);
        String      code        = container.get_all_the_code().toString();
        interpreter.interpret(code, 0, code.length(), engine);

        logger.kill();
    }
}
