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
        boolean isIn = false;
        if(container_long.containsKey(varName)){
            isIn = true;
        }
        return isIn;
    }
    
    public long getValue(String varName) throws Exception { 
        long value = 0;
        
        if(container_long.containsKey(varName)){
            for(Map.Entry<String, Long> item: container_long.entrySet()){ 
                if(item.getKey().equals(varName)){
                    value = item.getValue();
                } 
            }
            return value;
        } else {
            throw new Exception("Variable \"" + varName + "\" might not be initialised");
        }
    }
    
    
    
}
