package javaaplication5;

import java.util.HashMap;
import java.util.Map;

public class Container_Integer {
    
    Map<String, Integer> integer = new HashMap<>();
    
    
    //Adds a new Variable to the Map//
    public void put(String varName, int varValue){
        integer.put(varName, varValue);
    }
    //Changes the Value of the Variable//
    public void set(String varName, int varValue){
        integer.put(varName, varValue);
    }
    //Gives back the value of the variable// 
    //Throws Exception if variable is not in Map//
    public int getValue(String varName) throws Exception { 
        int value = 0;
        
        if(integer.containsKey(varName)){
            for(Map.Entry<String, Integer> item: integer.entrySet()){ 
                if(item.getKey().equals(varName)){
                    value = item.getValue();
                } 
            }
            return value;
        } else {
            throw new Exception("Variable \"" + varName + "\" might not be initialised");
        }
    }
    //Checks wether variable is in Map - true ->> 1 - false ->> 0//
    public boolean is_In(String varName){
        boolean in = false;
        if(integer.containsKey(varName)){
            in = true;
        }
        return in;
    }

}
