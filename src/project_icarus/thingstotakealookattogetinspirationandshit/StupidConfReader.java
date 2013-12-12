/*
 * Copyright (c) 2013, Simon Mages <mages.simon@googlemail.com>
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
 */

package project_icarus.thingstotakealookattogetinspirationandshit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * StupidConfReader is a very Simple configfile parser, it will find values
 * to the corresponding Strings
 * 
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 0.5
 */
public class StupidConfReader {
    private String []foundvars;
    private String pathtofile;
    private String nameoffile;
    private List<String> lines;
    
    /**
     * The StupidConfReader constructor needs the path to a file and the filename.
     * The whole config file will be saved in a list, line by line.
     * 
     * the configfile should look like this:
     *      VARIABLE=VALUE
     * 
     * Without spaces between the = and the Surrounding Characters, the = is the
     * delimiter, so please don't use it as a value or variable.
     * 
     * @param pathtofile The path to the config file
     * @param nameoffile The name of the config file
     */
    public StupidConfReader(String pathtofile, String nameoffile) {
        this.pathtofile = pathtofile;
        this.nameoffile = nameoffile;
        try {
            if ("Windows".equals(System.getProperties().getProperty("os.name"))) {
                lines = Files.readAllLines(Paths.get(this.pathtofile+"\\"+this.nameoffile), Charset.defaultCharset());
            } else {
                lines = Files.readAllLines(Paths.get(this.pathtofile+"/"+this.nameoffile), Charset.defaultCharset());
            }
        } catch (IOException e) {
            System.out.println("Can't find configfile: "+pathtofile+"/"+nameoffile);
        }
    }
    
    /**
     * getVar loops through all the elements of the list
     * and if var is found it will return the value of var
     * 
     * @param var   Search string, to find the value
     * @return      return the value of var
     */
    public String getVar (String var) {
        //this.foundvars = lines.toArray(this.foundvars);
        String []splitresult;
        String result = null;
        for (int i = 0; i < lines.size(); i++)
        {
            splitresult = lines.get(i).split("=");
            if (var.equals(splitresult[0])) {
                result = splitresult[1];
                i = lines.size();
            }
        }
        return result;
    }   
}



