/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        try{
        workWithThat = read();
        read_config();
        }
        catch(IOException e){
            System.err.println("could not access the config file");
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
    public String[] get_path(String key){
        //removes the ; from the path Strings
        String prep = key_container.get(key).replace(";", "");
        //splits the String after each , thereby seperating the different paths from each other
        String[] toReturn = prep.split(",");
        return toReturn;
    }
    
    
    /**
     * first casts the object in the Map to an Integer (we assume the one calling this method knows there is an Integer stored in there)
     * and we then get the int value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return
     */
    public int get_int(String key){
        Integer toReturn = (Integer)container.get(key);
        return toReturn.intValue();
    }
    
    
    /**
     * first casts the object in the Map to a Double (we assume the one calling this method knows there is a Double stored in there)
     * and we then get the double value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return 
     */
    public double get_double(String key){
        Double toReturn = (Double)container.get(key);
        return toReturn.doubleValue();
    }
    
    
    /**
     * first casts the object in the Map to a Boolean (we assume the one calling this method knows there is a Boolean stored in there)
     * and we then get the boolean value of it
     * @param key the key of the entry in which the data you want are stored in
     * @return 
     */
    public boolean get_boolean(String key){
        Boolean toReturn = (Boolean)container.get(key);
        return toReturn.booleanValue();
    }
    
    
    /**
     * we cast the object in the Map to a String which we then return
     * @param key the key of the entry in which the data you want are stored in
     * @return 
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
        //default values
        container.put("takt_frequency", 100);
        container.put("verbosity_level", 1);
        
        String[] lines = workWithThat.split("\n");
        for (String line : lines) {
            interpret_line(line);
        }
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
     * 
     * @return the content of the file without comments, spaces and stuff as a String
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private String read() throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader (filePath));
        String toReturn = "";
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
                    toReturn += '/' + sign;
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
        return toReturn;
    }   
}
