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
 * vim: foldmethod=syntax:foldcolumn=5:
 */

package Icarus;

import vault.*;
import parser.*;
import linc.*;
import interpreter.*;

import java.net.*;

public class Main
{
    static String        log_key = "main";
    static String        config_path;
    static String        hostname;
    static String        code;

    static Config_Reader config;
    static LogWriter     logger;
    static InfoCollector container;
    static Interpreter   interpreter;

    static STFileFinder  stfinder;

    /**
     * main funcion which will start Icarus
     */
    public static void main(String[] args) throws Exception
    {
        print_startup_message();
        set_config_file_path();

        config      = new Config_Reader(config_path);
        logger      = new LogWriter(config);
        container   = new InfoCollector(config, logger);
        interpreter = new Interpreter(container, logger, config);

        double blub = System.currentTimeMillis();
        interpreter.lets_get_this_party_started();
        System.out.println((System.currentTimeMillis() - blub) + "ms.");

        logger.log(log_key, 0, "exiting Icarus.");
        logger.kill();
    }

    private static void print_startup_message()
    {
        System.out.println("Staring Icarus Structure Text Interpreter!");
        System.out.println("verion: 0.8 (Alpha!)");
    }

    private static void set_config_file_path()
    {
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            System.out.println("UnknownHostException: " + e);
            hostname = "";
            System.exit(1);
        }

        switch (hostname)
        {
            case "beelzebub":
                config_path = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/Icarus/superduper_config";
                break;
            case "d4ryus":
            case "cubie":
                config_path = "/home/d4ryus/coding/Project_Icarus/Icarus/d4ryus_config";
                break;
            case "alarmpi":
                config_path = "/home/vault/Project_Icarus/Icarus/example_config";
                break;
            case "vault":
                config_path = "/home/vault/programing/NetBeansProjects/Project_Icarus/Icarus/example_config";
                break;
            case "csb.local":
                config_path = "/home/ninti/NetbeansProjects/Project_Icarus/Icarus/example_config";
                break;
            case "link":
                config_path = "/home/linc/Documents/defaultConfig";
                break;
            /*
             case "yourhostname":
             config_path = "/path/to/your/config_file";
             break;
             */
            default:
                System.out.print("no case for hostname: " + hostname + "\n");
                config_path = "";
                System.exit(0);
        }
    }
}
