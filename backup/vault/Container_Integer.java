package javaaplication5;

import java.util.HashMap;
import java.util.Map;

public class Container_Integer {
    
    Map<String, Integer> container_integer = new HashMap<>();

    public void put(String varName, int varValue){
        container_integer.put(varName, varValue);
    }
    public void set(String varName, int varValue){
        container_integer.put(varName, varValue);
    }
    public boolean is_In(String varName){
        return container_integer.containsKey(varName);
    }
    public int getValue(String varName) throws Exception { 
        if(container_integer.containsKey(varName)){  
            return container_integer.get(varName).intValue();
        } else {
            throw new Exception("Variable \"" + varName + "\" might not be initialised");
        }
    }

}
