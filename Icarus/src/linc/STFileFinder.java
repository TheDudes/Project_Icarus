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
 */
package linc;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import vault.LogWriter;

/**
 *
 * @author linc
 */
public class STFileFinder {
	
	// private Config_Reader cfReader; /* delete me ;) */
    private String fileDirectory;
    private String files[];
    private File directory;
    private ArrayList<String> stFiles;
    private LogWriter logWriter;
    
    
    /**
     * 
     * @param cfReader cfReader to be able to call the function to get the fileDirectory we should search
     * @param logWriter logWriter so we can spam the shit out of people
     */
    public STFileFinder(Config_Reader cfReader, LogWriter logWriter){
	    // this.cfReader = cfReader; /* delete me ;) */
        this.logWriter = logWriter;
        fileDirectory = cfReader.get_path("path");
	/* 
	   NullPointerException when path is not set correctly in
	   config file you should catch this error and print a nice output
	*/
	directory = new File(fileDirectory);
        files = directory.list();
        this.stFiles = new ArrayList<>(files.length);
        getSTFiles();
        logWriter.log("STFileFinder", 4, "leaving constructor");
    }
    
    
    /**
     * iterates through the inital Array and selects the filenames that end
     * on .st i.e. are Structured Text Files. Those will then be added to the
     * ArrayList.
     * If the directory name does not end on a / the function will append that one aswell
     */
    private void getSTFiles(){
        logWriter.log("STFileFinder", 4, "jumping in getSTFiles");
        if(!fileDirectory.endsWith("/")){
            fileDirectory += "/";
        }
        for(int i = 0; i < files.length; i++) {
            if(Pattern.matches(".*?\\.st", files[i])) {
                stFiles.add(fileDirectory + files[i]);
            }
        }
        logWriter.log("STFileFinder", 4, "leaving getSTFiles");
    }
    
    
    /**
     * creates a new String Array with the size of the ArrayList, then puts all
     * the Elements in the ArrayList in the String Array
     * @return Array with all the paths of the Structured Text files found in the provided (in the constructor) directory
     */
    public String[] get_file_names(){
        logWriter.log("STFileFinder", 4, "jumping in get_file_names");
        int size = stFiles.size();
        String returnArray[] = new String[size];
        logWriter.log("STFileFinder", 4, "leaving get_file_names");
        return stFiles.toArray(returnArray);
    }
    
    
    /**
     * 
     * @param fileDir the new directory we shall search
     */
    public void set_file_directory(String fileDir){
        logWriter.log("STFileFinder", 4, "jumping in set_file_directory");
        directory = new File(fileDir);
        files = directory.list();
        getSTFiles();
        logWriter.log("STFileFinder", 4, "leaving set_file_directory");
    }
    
}
