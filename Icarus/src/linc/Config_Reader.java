/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package linc;

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

/**
 *
 * @author linc
 */
public class Config_Reader {
    
    //stores the path to the config file
    private String filePath = "";
    
    //used for storing the String we get from the 
    private String workWithThat = "";
    
    //the maps for the variables and stuff we get from the config file
    public static Map<String, Object> container = new HashMap<>();
    public static Map<String, String> key_container = new HashMap<>();

    
    /**
     * 
     * @param path the path of the config file
     */
    public Config_Reader(String path) {
        filePath = path;
        File checkFile = new File(filePath);
        if(checkFile.exists()){
            try{
                workWithThat = read();
                read_config();
            }
            catch(IOException e){
                System.err.println("could not access the config file");       
            }
        }
        else{
            create_example_config(filePath);
        }
    }

    
    /**
     * 
     * @param filePath the path the config file is in
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    
    /**
     * 
     * @return StringArray with all the keys stored in the container
     */
    public String[] get_variables(){
        int i = 0;
        String[] toReturn = new String[container.size()];
        /* --warning-- map.entry is a raw type. References to generic
         * type Map<K,V>.Entry<K,V> should be parameterized */
        for(Map.Entry e : container.entrySet()){
            toReturn[i] = (String) e.getKey();
            i++;
        }
        return toReturn;
    }
    
    
    /**
     * just used for debugging, Object isn't a really good return value
     * @param key 
     * @return 
     */
    public Object get_val(String key){
        return container.get(key);
    }
    
    
    /**
     * 
     * @return StringArray with all the keys stored in the key_container
     */
    public String[] get_keys(){
        int i = 0;
        String[] toReturn = new String[key_container.size()];
        /* --warning-- map.entry is a raw type. References to generic 
         * type Map<K,V>.Entry<K,V> should be parameterized */
        for(Map.Entry e : key_container.entrySet()){
            toReturn[i] = (String) e.getKey();
            i++;
        }
        return toReturn;
    }
    
    
    /**
     * 
     * @param key the key from which we want to get the value from
     * @return the value stored under the key
     */
    public String get_path(String key){
        //removes the ; from the path Strings
        String prep = key_container.get(key).replace(";", "");
        //only one path allowed not so this is not needed any more
        //splits the String after each , thereby seperating the different paths from each other
        //String[] toReturn = prep.split(",");
        return prep;
    }
    
    
    /**
     * first casts the object in the Map to an Integer (we assume the one calling this method knows there is an Integer stored in there)
     * and we then get the int value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public int get_int(String key){
        Integer toReturn = (Integer)container.get(key);
        return toReturn;
    }
    
    
    /**
     * first casts the object in the Map to a Double (we assume the one calling this method knows there is a Double stored in there)
     * and we then get the double value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public double get_double(String key){
        Double toReturn = (Double)container.get(key);
        return toReturn;
    }
    
    
    /**
     * first casts the object in the Map to a Boolean (we assume the one calling this method knows there is a Boolean stored in there)
     * and we then get the boolean value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public boolean get_boolean(String key){
        Boolean toReturn = (Boolean)container.get(key);
        return toReturn;
    }
    
    
    /**
     * we cast the object in the Map to a String which we then return
     * @param key the key of the entry in which the data you want are stored in
     * @return value of given key
     */
    public String get_string(String key){
        return (String)container.get(key);
    }
    
    
    /**
     * first puts some default values in the container, then proceeds by splitting the String from the
     * config file after each newLine, the calls the interpret_line function which does the actual work
     * @throws IOException 
     */
    private void read_config() throws IOException{
        set_default_values();
        String[] lines = workWithThat.split("\n");
        for (String line : lines) {
            interpret_line(line);
        }
    }
    
    /**
     * puts some default values in the container, the default value is
     * defined here
     * takt_frequency default is 100
     * verbosity_level default is 0
     */
    private void set_default_values(){
        container.put("takt_frequency", 100);
        container.put("verbosity_level", 0);        
    }
    
    
    /**
     * first checks if it is a key which has to be put in the key_container, if so removes the key_ part and stores it in the map
     * if it is no a key, checks the type of the value and then puts the value in the normal container
     * @param line the line we get from the read_config function, this is the String we will work with
     */
    public void interpret_line(String line){
        //does the key have the key attribute? (the key is used for assigning files to write their log in for the parser and other stuff)
        if(Pattern.matches("key_.*?=.*?;", line)){
            String[] split_line = line.split("=");
            split_line[0] = split_line[0].substring(4);
            key_container.put(split_line[0], split_line[1]);
        }
        else{
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
                else if(Pattern.matches("(true;)|(false;)", split_line[1])){
                    container.put(split_line[0], Boolean.valueOf(split_line[1].replace(";", "")));
                }
                //or is it a string?
                else{
                    container.put(split_line[0], split_line[1]);
                }
            }
        }
    }
    
    
    /**
     * only checks if the System is Windows or Linux and then calls the
     * read Method for the fitting system
     * @return the String with the read in config file
     */
    private String read() throws IOException{
        String toReturn = "";
        if(Pattern.matches("Windows.*", System.getProperty("os.name"))){
            toReturn = read_Windows();
        }
        else{
            toReturn = read_Linux();
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
                }
                //remove spaces (they are not attached to the string, that's all)
                else if(sign == ' '){
                    sign = (char)reader.read();
                }
                //reads over tabs aswell, they are not needed
                else if(sign == '\t'){
                    sign = (char)reader.read();
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
        return toReturn;
    }
    
    
    /**
     * This is the read Method for Windows as Windows uses \r\n as a newLine indicator
     * @return the content of the file without comments, spaces and stuff as a String
     * @throws FileNotFoundException
     * @throws IOException 
     */
     private String read_Windows() throws FileNotFoundException, IOException{

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
                }
                //remove spaces (they are not attached to the string, that's all)
                else if(sign == ' '){
                    sign = (char)reader.read();
                }
                //reads over tabs aswell, they are not needed
                else if(sign == '\t'){
                    sign = (char)reader.read();
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
        return toReturn;
    }
    
    
    /**
     * will created an example config file at the specified path
     * @param path the path at which the example config will be created
     */
    private void create_example_config(String path){
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
            "## The key for telling the porgramm that there                     ##\n" +
            "## will now be a path for an output is:                            ##\n" +
            "## key_                                                            ##\n" +
            "## there can be several paths assigned to the output of an object, ##\n" +
            "## each path is simply seperated by a ,                            ##\n" +
            "## This means that key_Parser will signal the Config_Reader that   ##\n" +
            "## the paths to where the output from the Parser shall be written  ##\n" +
            "## to will follow now (see example below)                          ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                   ##############################\n" +
            "                   ##  logs and paths to them  ##\n" +
            "                   ##############################\n" +
            "                       \n" +
            "## will assign the path /home/linc/Documents/ParseLog to the Parser \n" +
            "## output\n" +
            "#key_parser = /home/linc/Documents/ParseLog;\n" +
            "\n" +
            "## will assign the path /home/linc/Icarus/Logs/LogAll as well as\n" +
            "## the path /home/linc/Icarus/Logs/SyntaxCheck to the output \n" +
            "## of the Syntax checker\n" +
            "#key_syntax_checker = /home/linc/Icarus/Logs/LogAll, /home/linc/Icarus/Logs/SyntaxCheck;";
        try(
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        ){
            writer.write(hugeAssExampleString);
            writer.flush();
        }   
        catch(IOException e){
            System.err.println("could not create example_config");
        }
    }
    
    
    /**
     * 
     */
    public void create_example_config(){
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
            "## The key for telling the porgramm that there                     ##\n" +
            "## will now be a path for an output is:                            ##\n" +
            "## key_                                                            ##\n" +
            "## there can be several paths assigned to the output of an object, ##\n" +
            "## each path is simply seperated by a ,                            ##\n" +
            "## This means that key_Parser will signal the Config_Reader that   ##\n" +
            "## the paths to where the output from the Parser shall be written  ##\n" +
            "## to will follow now (see example below)                          ##\n" +
            "#####################################################################\n" +
            "#####################################################################\n" +
            "\n" +
            "                   ##############################\n" +
            "                   ##  logs and paths to them  ##\n" +
            "                   ##############################\n" +
            "                       \n" +
            "## will assign the path /home/linc/Documents/ParseLog to the Parser \n" +
            "## output\n" +
            "#key_parser = /home/linc/Documents/ParseLog;\n" +
            "\n" +
            "## will assign the path /home/linc/Icarus/Logs/LogAll as well as\n" +
            "## the path /home/linc/Icarus/Logs/SyntaxCheck to the output \n" +
            "## of the Syntax checker\n" +
            "#key_syntax_checker = /home/linc/Icarus/Logs/LogAll, /home/linc/Icarus/Logs/SyntaxCheck;";
        try(
            BufferedWriter writer = new BufferedWriter(new FileWriter("example_config"));
        ){
            writer.write(hugeAssExampleString);
            writer.flush();
        }
        catch(IOException e){
            System.err.println("could not create example_config");
        }
    }
}
