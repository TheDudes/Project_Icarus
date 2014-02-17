/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author linc
 */
public class Container_String {
    
    private Map<String, String> container = new HashMap<>();
    
    
    /**
     * 
     * @param key the key under which the dataset will be stored in the Hashmap
     * @param value the value that will be stored in the Hashmap
     */
    public void put(String key, String value){
        container.put(key, value);
    }
    
    
    /**
     * 
     * @param key the key from which the value in the Hashmap should be stored
     * @param value the value to be changed in the Hashmap
     */
    public void set(String key, String value){
        container.put(key, value);
    }
    
    
    /**
     * 
     * @param key the key that we want to search in the Hashmap
     * @return returns true if the key is found in the Hashmap
     */
    public boolean is_in(String key){
        return container.containsKey(key);
    }
    
    
    /**
     * 
     * @param key the value from which we want to get the value from
     * @return the value from the key we searched
     */
    public String get_value(String key){
        return container.get(key);
    }
    
}
