package javaaplication5;

import java.util.HashMap;
import java.util.Map;

public class Container_Long {
    
    Map<String, Long> container_long = new HashMap<>();
    
    public void put(String varName, long varValue){
        container_long.put(varName, varValue);
    }
    public void set(String varName, long varValue){
        container_long.put(varName, varValue);
    }
    public boolean is_In(String varName){
        return container_long.containsKey(varName);
    }
    public long getValue(String varName) throws Exception { 
        if(container_long.containsKey(varName)){  
            return container_long.get(varName).intValue();
        } else {
            throw new Exception("Variable \"" + varName + "\" might not be initialised");
        }
    }
    
    
    
}
