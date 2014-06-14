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
 */
package config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import logger.*;
import java.lang.ClassCastException;

/**
 *
 * @author linc
 */
public class Config_Reader {

    //stores the path to the config file
    private String filePath = "";
    private String logKey = " [Config_Reader]: ";

    /*
    used for storing the String we get from the read() Method
    is initialised in the constructor and used in the interpret_line function
    */
    private String workWithThat = "";

    /*
    LogWriter so we can log our stuff, the boolean is a flag that tells
    us if the LogWriter has been initialised since that is not the case at
    the beginning
    */
    private Logger logWriter;
    private boolean logWriterInit;

    //the map for the variables and stuff we get from the config file
    private Map<String, Object> container = new HashMap<>();

    /*
    we need this one to get all the Structured Text files (or the paths to them)
    from the directory the path variable in the config file points to, aka the
    directory the ST files are supposed to be in
    the StringArray is for the storage of the Array we get from the STFileFinder,
    it has the paths to all the ST files in the directory stored in it
    */
    private STFileFinder findEmFiles;
    private String stFiles[];


    /**
     *
     * @param path the file path of the config file, must be given when the Config_Reader is created
     * @throws java.io.IOException in case no there is no config found under the path provided
     */
    public Config_Reader(String path) throws IOException {
        logWriterInit = false;
        filePath = path;
        File checkFile = new File(filePath);
        if(checkFile.exists()){
            try{
                workWithThat = read();
                read_config();
            }
            catch(IOException e){
                System.err.println("could not access the config file");
                throw e;
            }
        }
        else{
            create_example_config(filePath);
            set_default_values();
        }
    }

    /**
     *
     * @param logger an object of the type LogWriter, is initialised here since it needs the ConfigReader first
     * will set the flag that the LogWriter is now initialised and the functions can send logging calls
     */
    public void set_Logger(Logger logger){
        logWriter = logger;
        logWriterInit = true;
        logWriter.log(0, logKey, "----------------------------------------\n");
        logWriter.log(0, logKey, "  Values specified in the config file:\n");
        logWriter.log(0, logKey, "----------------------------------------\n");
        for(Map.Entry<String, Object> e : container.entrySet()){
            String formattedValues = String.format("%6s%-20s%8s", "Key: ", e.getKey(), "Value: ");
            logWriter.log(0, logKey, formattedValues , e.getValue().toString(), "\n");
        }
        logWriter.log(0, logKey, "----------------------------------------\n");
    }

    /**
     *
     * @param filePath the path the config file is in
     */
    public void setConfigFilePath(String filePath) {
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in setConfigFilePath and setting path to" , filePath, "\n");
        }
        this.filePath = filePath;
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving setFilePath\n");
        }
    }


    /**
     *
     * @return StringArray with all the keys stored in the container
     */
    public String[] get_variables(){
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_variables\n");
        }
        int i = 0;
        String[] toReturn = new String[container.size()];
        for(Map.Entry<String, Object> e : container.entrySet()){
            toReturn[i] = (String) e.getKey();
            i++;
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_variables\n");
        }
        return toReturn;
    }


    /**
     * just used for debugging, Object isn't a really good return value
     * @param key
     * @return the Object stored under the key in the variable container
     */
    public Object get_val(String key){
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_val with key" , key, "\n");
        }
        if(container.containsKey(key)){
            if(logWriterInit){
                logWriter.log(4, logKey, "leaving get_val\n");
            }
            return container.get(key);
        }
        if(logWriterInit){
            logWriter.log(0, logKey, "in get_val could not find a match for " , key , ", exiting\n");
            System.exit(0);
        }
        else{
            System.err.println("in function get_val could not find a match for " + key + ", exiting");
            System.exit(0);
        }
        return new Integer(-1);
    }


    /**
     *
     * @param key the key from which we want to get the value from
     * @return the value stored under the key
     */
    public String get_path(String key) {
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_path with key" , key, "\n");
        }
        String prep;
        //removes the ; from the path Strings
        if(container.containsKey(key)){
            prep = (String)container.get(key);
        }
        else{
            prep = "";
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_path could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_path could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        //only one path allowed not so this is not needed any more
        //splits the String after each , thereby seperating the different paths from each other
        //String[] toReturn = prep.split(",");
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_path\n");
        }
        return prep;
    }


    /**
     * first casts the object in the Map to an Integer (we assume the one calling this method knows there is an Integer stored in there)
     * and we then get the int value of it
     * @deprecated
     */
    public int get_int(String key) throws ClassCastException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_int with key" , key, "\n");
        }
        int toReturn;
        if(container.containsKey(key)){
            toReturn = (int)container.get(key);
        }
        else{
            toReturn = -1;
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_int could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_int could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_int\n");
        }
        return toReturn;
    }

    /**
     * first casts the object in the Map to an Integer (we assume the one calling this method knows there is an Integer stored in there)
     * and we then get the int value of it
     * @param key the key of the entry in which the data you want are stored in
     * @param min min return value
     * @param max max return value
     * @return value of given key
     */
    public int get_int(String key, int min, int max)
    {
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_int with key" , key, "\n");
        }
        int toReturn;
        if(container.containsKey(key)){
            try
            {
                toReturn = (int)container.get(key);
                if (min > toReturn || toReturn > max)
                {
                    if(logWriterInit)
                    {
                        logWriter.log(0, logKey, "\n\n");
                        logWriter.log(0, logKey, "ERROR: value from config file is not valid.\n");
                        logWriter.log(0, logKey, "value: '", key, "' is not valid!\n");
                        logWriter.log(0, logKey, "DETAILED ERROR: ");
                        logWriter.log(0, logKey, "'", key, "' is set to: '", new Integer(toReturn).toString(), "'\n");
                        logWriter.log(0, logKey, "should be between '", new Integer(min).toString(),"' and '", new Integer(max).toString(), "'\n");
                        logWriter.kill();
                    }
                    else
                    {
                        System.out.print("\n\n");
                        System.out.print("ERROR: value from config file is not valid.\n");
                        System.out.print("value '" + key + "' is not valid!\n");
                        System.out.print("DETAILED ERROR: ");
                        System.out.print("'" + key + "' is set to: '" + toReturn + "'\n");
                        System.out.print("should be between '" + min + "' and '" + max + "'\n");
                    }
                    System.exit(0);
                    return -1;
                }
            }
            catch(ClassCastException e)
            {
                if(logWriterInit)
                {
                        logWriter.log(0, logKey, "\n\n");
                        logWriter.log(0, logKey, "ERROR: value from config file is not valid.\n");
                        logWriter.log(0, logKey, "value: '", key, "' is not a int!\n");
                        logWriter.log(0, logKey, "DETAILED ERROR: ");
                        logWriter.log(0, logKey, "'", key, "' is set to: '", get_string(key), "'\n");
                        logWriter.kill();
                }
                else
                {
                    System.out.print("\n\n");
                    System.out.print("ERROR: value from config file is not valid.\n");
                    System.out.print("value '" + key + "' is not a int!\n");
                    System.out.print("DETAILED ERROR: ");
                    System.out.print("'" + key + "' is set to: '" + get_string(key) + "'\n");
                }
                System.exit(0);
                return -1;
            }
        }
        else{
            toReturn = -1;
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_int could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_int could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_int\n");
        }
        return toReturn;
    }


    /**
     * first casts the object in the Map to a Double (we assume the one calling this method knows there is a Double stored in there)
     * and we then get the double value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public double get_double(String key) throws ClassCastException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_double with key" , key, "\n");
        }
        double toReturn;
        if(container.containsKey(key)){
            toReturn = (double)container.get(key);
        }
        else{
            toReturn = -1.0;
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_double could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_double could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_double\n");
        }
        return toReturn;
    }


    /**
     * first casts the object in the Map to a Boolean (we assume the one calling this method knows there is a Boolean stored in there)
     * and we then get the boolean value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public boolean get_boolean(String key) {
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_boolean with key" , key, "\n");
        }
        boolean toReturn;
        if(container.containsKey(key)){
            try
            {
                toReturn = (boolean)container.get(key);
            }
            catch(ClassCastException e)
            {
                if(logWriterInit)
                {
                        logWriter.log(0, logKey, "\n\n");
                        logWriter.log(0, logKey, "ERROR: value from config file is not valid.\n");
                        logWriter.log(0, logKey, "value: '", key, "' is not a boolean!\n");
                        logWriter.log(0, logKey, "DETAILED ERROR: ");
                        logWriter.log(0, logKey, "'", key, "' is set to: '", get_string(key), "'\n");
                        logWriter.kill();
                }
                else
                {
                        System.out.print("\n\n");
                        System.out.print("ERROR: value from config file is not valid.\n");
                        System.out.print("value: '" + key + "' is not a boolean!\n");
                        System.out.print("DETAILED ERROR: ");
                        System.out.print("'" + key + "' is set to: '" + get_string(key) + "'\n");
                }
                System.exit(0);
                return false;
            }
        }
        else{
            toReturn = false;
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_boolean could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_boolean could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_boolean\n");
        }
        return toReturn;
    }


    /**
     * we cast the object in the Map to a String which we then return
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public String get_string(String key) {
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_string with key" , key, "\n");
        }
        String prep = "";
        if(container.containsKey(key)){
            prep = (String)container.get(key);
        }
        else{
            if(logWriterInit){
                logWriter.log(0, logKey, "in get_string could not find a match for " , key , ", exiting\n");
                System.exit(0);
            }
            else{
                System.err.println("in function get_string could not find a match for " + key + ", exiting");
                System.exit(0);
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving get_String\n");
        }
        return prep;
    }


    /**
     * will create a STFileFinder that is used to get all the Structured Text files
     * from the directory specified in the path variable from the config file
     * @return a StringArray with the paths to all the ST files in the directory specified in the path variable in the config file
     */
    public String[] get_st_filepaths(){
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in get_st_filepaths\n");
            findEmFiles = new STFileFinder(this, logWriter);
            stFiles = findEmFiles.get_file_names();
            for(int i = 0; i < stFiles.length; i++){
                logWriter.log(0, logKey, "path to ST File " , new Integer(i).toString() , ".  " , stFiles[i], "\n");
            }
            logWriter.log(4, logKey, "leaving get_st_filepaths\n");
        }
        return stFiles;
    }


    /**
     * first puts some default values in the container, then proceeds by splitting the String from the
     * config file after each newLine, the calls the interpret_line function which does the actual work
     * @throws IOException
     */
    private void read_config() throws IOException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in read_config\n");
        }
        set_default_values();
        String[] lines = workWithThat.split("\n");
        for (String line : lines) {
            interpret_line(line);
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving read_config\n");
        }
    }

    /**
     * puts some default values in the container, the default value is
     * defined here
     * takt_frequency default is 100
     * verbosity_level default is 0
     */
    private void set_default_values(){
        container.put("takt_frequency", (Integer)100);
        container.put("verbosity_level", (Integer)0);
        container.put("silent", (Boolean)false);
        container.put("Engine_Warmup", (Boolean)true);
        container.put("sync_port", (Integer)5021);
        container.put("async_port", (Integer)5022);
        container.put("hostname", "localhost");
        container.put("path", "/home/linc/NetBeansProjects/Project_Icarus/Icarus/st_files");
    }


    /**
     * first checks if it is a key which has to be put in the key_container, if so removes the key_ part and stores it in the map
     * if it is no a key, checks the type of the value and then puts the value in the normal container
     * @param line the line we get from the read_config function, this is the String we will work with
     */
    public void interpret_line(String line){
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in interpret_line\n");
        }
        String[] split_line = line.split("=");
        if(split_line.length != 1){
            //is the value a integer?
            if(Pattern.matches("\\d*?;", split_line[1])){
                container.put(split_line[0], new Integer(split_line[1].replace(";", "")));
            }
            //is the value a double?
            else if(Pattern.matches("\\d*?\\.\\d*?;", split_line[1])){
                container.put(split_line[0], new Double(split_line[1].replace(";", "")));
            }
            //is the value a boolean?
            else if(Pattern.matches("(true;)|(false;)|(TRUE)|(FALSE)|(True)|(False)", split_line[1])){
                container.put(split_line[0], Boolean.valueOf(split_line[1].replace(";", "")));
            }
            //or is it a string?
            else{
                container.put(split_line[0], split_line[1].replace(";", ""));
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving interpret_line\n");
        }
    }


    /**
     * only checks if the System is Windows or Linux and then calls the
     * read Method for the fitting system
     * @return the String with the read in config file
     */
    private String read() throws IOException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in read\n");
        }
        String toReturn = "";
        if(Pattern.matches("Windows.*", System.getProperty("os.name"))){
            toReturn = read_Windows();
        }
        else{
            toReturn = read_Linux();
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving read\n");
        }
        return toReturn;
    }


    /**
     * This is the read Method for Linux since Linux uses only \n as a newLine indicator
     * @return the content of the file without comments, spaces and stuff as a String
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String read_Linux() throws FileNotFoundException, IOException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in read_Linux\n");
        }
        String toReturn;
        try (BufferedReader reader = new BufferedReader(new FileReader (filePath))) {
            toReturn = "";
            char sign = (char)reader.read();
            while(sign != (char)-1){
                //read over the comment line without attaching it to the string
                if(sign == '#'){
                    while(sign != '\n'){
                        sign = (char)reader.read();
                    }
                    continue;
                }
                //throws out everything after //, if there is only one / it appends it plus the next sign
                else if(sign == '/'){
                    sign = (char)reader.read();
                    if(sign == '/'){
                        while(sign != '\n'){
                            sign = (char)reader.read();
                        }
                    }
                    else{
                        toReturn += "/";
                    }
                    continue;
                }
                //remove spaces (they are not attached to the string, that's all)
                else if(sign == ' '){
                    sign = (char)reader.read();
                    continue;
                }
                //reads over tabs aswell, they are not needed
                else if(sign == '\t'){
                    sign = (char)reader.read();
                    continue;
                }
                //if there are several newLines after each other only one is attached
                else if(sign == '\n'){
                    toReturn += sign;
                    sign = (char)reader.read();
                    while(sign == '\n'){
                        sign = (char)reader.read();
                    }if(sign == '\n'){
                        sign = (char)reader.read();
                    }
                }
                //every other symbol is attached to the string
                else{
                    toReturn += sign;
                    sign = (char)reader.read();
                }
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving read_Linux\n");
        }
        return toReturn;
    }


    /**
     * This is the read Method for Windows as Windows uses \r\n as a newLine indicator
     * @return the content of the file without comments, spaces and stuff as a String
     * @throws FileNotFoundException
     * @throws IOException
     */
     private String read_Windows() throws FileNotFoundException, IOException{
         if(logWriterInit){
            logWriter.log(4, logKey, "jumping in read_Windows\n");
        }
        String toReturn;
        try (BufferedReader reader = new BufferedReader(new FileReader (filePath))) {
            toReturn = "";
            char sign = (char)reader.read();
            while(sign != (char)-1){
                //read over the comment line without attaching it to the string
                if(sign == '#'){
                    while(true){
                        if(sign == '\r'){
                            sign = (char)reader.read();
                            if(sign == '\n'){
                                break;
                            }
                        }
                        sign = (char)reader.read();
                    }
                    continue;
                }
                //throws out everything after //, if there is only one / it appends it plus the next sign
                else if(sign == '/'){
                    sign = (char)reader.read();
                    if(sign == '/'){
                        while(true){
                            if(sign == '\r'){
                                sign = (char)reader.read();
                                if(sign == '\n'){
                                    break;
                                }
                            }
                            sign = (char)reader.read();
                        }
                    }
                    else{
                        toReturn += "/";
                    }
                    continue;
                }
                //remove spaces (they are not attached to the string, that's all)
                else if(sign == ' '){
                    sign = (char)reader.read();
                    continue;
                }
                //reads over tabs aswell, they are not needed
                else if(sign == '\t'){
                    sign = (char)reader.read();
                    continue;
                }
                //if there are several newLines after each other only one is attached
                else if(sign == '\r'){
                    toReturn += sign;
                    sign = (char)reader.read();
                    if(sign == '\n'){
                        toReturn += sign;
                        sign = (char)reader.read();
                        while(sign == '\r'){
                            sign = (char)reader.read();
                            if(sign == '\n'){
                                sign = (char)reader.read();
                            }
                            else{
                                break;
                            }
                        }
                    }
                }
                //every other symbol is attached to the string
                else{
                    toReturn += sign;
                    sign = (char)reader.read();
                }
            }
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving read_Windows\n");
        }
        return toReturn;
    }


    /**
     * will created an example config file at the specified path
     * @param path the path at which the example config will be created
     */
    private void create_example_config(String path) throws IOException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in create_example_config(String) with path" , path, "\n");
        }
        String hugeAssExampleString = "######################################################################\n" +
            "#####################################################################\n" +
            "## Default Config File for the Config_Reader                       ##\n" +
            "## Class of the Icarus Project                                     ##\n" +
            "## Config_Reader by:   Michael Kaspera/linc                        ##\n" +
            "## Icarus Project by:                                              ##\n" +
            "## currently only supports the linux newline                       ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "## The Config_Reader will throw out all unneeded symbols such as:  ##\n" +
            "## -space                                                          ##\n" +
            "## -several newlines after each other without a sign between them  ##\n" +
            "## -tabs                                                           ##\n" +
            "##                                                                 ##\n" +
            "## Comment signs are:                                              ##\n" +
            "## -the # sign                                                     ##\n" +
            "## -the // signs                                                   ##\n" +
            "##     Comments will throw out everything following                ##\n" +
            "## them until a newLine symbol is read                             ##\n" +
            "##                                                                 ##\n" +
            "## Default Values are at the moment:                               ##\n" +
            "## takt_frequency = 100;                                           ##\n" +
            "## verbosity_level = 0;                                            ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                       ##################\n" +
            "                       ##  Variables   ##\n" +
            "                       ##################\n" +
            "\n" +
            "## verbosity_level 0: standard value\n" +
            "## verbosity_level 1: some additional informations\n" +
            "## verbosity_level 2: more exact informations\n" +
            "## verbosity_level 3: almost everything the program does is logged\n" +
            "# verbosity_level = 0;\n" +
            "# takt_frequency = 100;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign \n" +
            "## the value 1 to it - the testvar will be a Integer Object\n" +
            "#testvar = 1;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value 1.5 to it - the testvar will be a Double Object\n" +
            "#testvar2 = 1.5;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value true to it - the testvar will be a Boolean Object\n" +
            "#testvar3 = true;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value asdf to it - the testvar will be a String\n" +
            "#testvar4 = asdf;\n" +
            "\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "## Setting the path for where which log will be written to         ##\n" +
            "## The key for telling the program where to write the logs is      ##\n" +
            "## quite simple: You just write the keyword = directory            ##\n" +
            "## The keyword for the logs is: LogWriter (see example below)      ##\n" +
            "##                                                                 ##\n" +
            "## The following keywords exist:                                   ##\n" +
            "## LogWriter         tells the LogWriter where to put his logs     ##\n" +
            "## path              this is the path to the ST File Directory     ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                   ##############################\n" +
            "                   ##  logs and paths to them  ##\n" +
            "                   ##############################\n" +
            "                       \n" +
            "## will assign the path /home/linc/Documents/ParseLog to the Parser \n" +
            "## output\n" +
            "#parser = /home/linc/Documents/ParseLog;\n" +
            "\n" +
            "## will assign the path /home/linc/Icarus/Logs/LogAll to the output \n" +
            "## of the Syntax checker\n" +
            "#syntax_checker = /home/linc/Icarus/Logs/LogAll; \n" +
            "\n " +
            "## set the path for the LogWriter: \n" +
            "LogWriter = /tmp" +
            "\n" +
            "## set the path to the Structured Text Files Directory:" +
            "path = /home/linc/NetBeansProjects/Project_Icarus/Icarus/st_files";
        try(
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        ){
            writer.write(hugeAssExampleString);
            writer.flush();
        }
        catch(IOException e){
            System.err.println("could not create example_config");
            throw e;
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving create_example_config(String)\n");
        }
    }


    /**
     * @throws IOException
     */
    public void create_example_config() throws IOException{
        if(logWriterInit){
            logWriter.log(4, logKey, "jumping in create_example_config\n");
        }
        String hugeAssExampleString = "######################################################################\n" +
            "#####################################################################\n" +
            "## Default Config File for the Config_Reader                       ##\n" +
            "## Class of the Icarus Project                                     ##\n" +
            "## Config_Reader by:   Michael Kaspera/linc                        ##\n" +
            "## Icarus Project by:                                              ##\n" +
            "## currently only supports the linux newline                       ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "## The Config_Reader will throw out all unneeded symbols such as:  ##\n" +
            "## -space                                                          ##\n" +
            "## -several newlines after each other without a sign between them  ##\n" +
            "## -tabs                                                           ##\n" +
            "##                                                                 ##\n" +
            "## Comment signs are:                                              ##\n" +
            "## -the # sign                                                     ##\n" +
            "## -the // signs                                                   ##\n" +
            "##     Comments will throw out everything following                ##\n" +
            "## them until a newLine symbol is read                             ##\n" +
            "##                                                                 ##\n" +
            "## Default Values are at the moment:                               ##\n" +
            "## takt_frequency = 100;                                           ##\n" +
            "## verbosity_level = 0;                                            ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                       ##################\n" +
            "                       ##  Variables   ##\n" +
            "                       ##################\n" +
            "\n" +
            "## verbosity_level 0: standard value\n" +
            "## verbosity_level 1: some additional informations\n" +
            "## verbosity_level 2: more exact informations\n" +
            "## verbosity_level 3: almost everything the program does is logged\n" +
            "# verbosity_level = 0;\n" +
            "# takt_frequency = 100;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign \n" +
            "## the value 1 to it - the testvar will be a Integer Object\n" +
            "#testvar = 1;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value 1.5 to it - the testvar will be a Double Object\n" +
            "#testvar2 = 1.5;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value true to it - the testvar will be a Boolean Object\n" +
            "#testvar3 = true;\n" +
            "\n" +
            "## will add the variable testvar in the container map and assign\n" +
            "## the value asdf to it - the testvar will be a String\n" +
            "#testvar4 = asdf;\n" +
            "\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "## Setting the path for where which log will be written to         ##\n" +
            "## The key for telling the program where to write the logs is      ##\n" +
            "## quite simple: You just write the keyword = directory            ##\n" +
            "## The keyword for the logs is: LogWriter (see example below)      ##\n" +
            "##                                                                 ##\n" +
            "## The following keywords exist:                                   ##\n" +
            "## LogWriter         tells the LogWriter where to put his logs     ##\n" +
            "## path              this is the path to the ST File Directory     ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                   ##############################\n" +
            "                   ##  logs and paths to them  ##\n" +
            "                   ##############################\n" +
            "                       \n" +
            "## will assign the path /home/linc/Documents/ParseLog to the Parser \n" +
            "## output\n" +
            "#parser = /home/linc/Documents/ParseLog;\n" +
            "\n" +
            "## will assign the path /home/linc/Icarus/Logs/LogAll to the output \n" +
            "## of the Syntax checker\n" +
            "#syntax_checker = /home/linc/Icarus/Logs/LogAll; \n" +
            "\n " +
            "## set the path for the LogWriter: \n" +
            "LogWriter = /home/linc/Documents/logs" +
            "\n" +
            "## set the path to the Structured Text Files Directory:" +
            "path = /home/linc/NetBeansProjects/Project_Icarus/Icarus/st_files";
        try(
            BufferedWriter writer = new BufferedWriter(new FileWriter("example_config"));
        ){
            writer.write(hugeAssExampleString);
            writer.flush();
        }
        catch(IOException e){
            System.err.println("could not create example_config");
            throw e;
        }
        if(logWriterInit){
            logWriter.log(4, logKey, "leaving create_example_config\n");
        }
    }
}
