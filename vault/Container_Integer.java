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
        int value = 0;
        if(container_integer.containsKey(varName)){
            for(Map.Entry<String, Integer> item: container_integer.entrySet()){ 
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
