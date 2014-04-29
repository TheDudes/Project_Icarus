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
 * file: Icarus/src/Icarus/main.java
 * @author: d4ryus - https://github.com/d4ryus/
 * @version 0.2
 */

package Icarus;

import Ninti.*;
import vault.*;
import linc.*;
import parser.*;
import interpreter.*;

import java.io.*;
import java.net.*;
import java.util.Stack;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class main
{

    public static void main(String[] args) throws Exception 
    {

        String[] path = new String[2];
        /*
         * To make testing easyer for all of us, add your hostname to this logic
         */
        String hostname    = InetAddress.getLocalHost().getHostName();

        switch (hostname)
        {
            case "beelzebub":
                path[0] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/Icarus/test.st";
                path[1] = "/home/apfel/Documents/StudienProjekt/StudienProjekt/sp_2013_10/Project_Icarus/Icarus/logs/";
                break;
            case "d4ryus":
                path[0] = "/home/d4ryus/coding/Project_Icarus/Icarus/test.st";
                path[1] = "/home/d4ryus/coding/Project_Icarus/Icarus/logs/";
                break;
            /*
             case "yourhostname":
             path[0] = "/your/path/to/the/st/file";
             path[1] = "/your/path/to/the/logfolder/";
             break;
             */
            default:
                System.out.println("Hey stupid(faggot), take a look in the code!");
                System.exit(0);
        }
        System.out.print("your st file path: "  + path[0] + "\n");
        System.out.print("your log file path: " + path[1] + "\n");


        Log.init(path[1], 4);

        LogWriter log = Log.get_log();

        InfoCollector container     = new InfoCollector(path);
        Engine_Warmup warmup        = new Engine_Warmup(log);
        ScriptEngine  engine        = warmup.engine_warmup();
        Interpreter   interpreter   = new Interpreter(container, log, engine);

        String code = container.getAllTheCode().toString();
        interpreter.interpret(code, 0, code.length(), engine);
    }
}
