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
        
    public void add(String key, String value){
        container.put(key, value);
    }
    
    public void set(String key, String value){
        container.put(key, value);
    }
    
    public boolean is_in(String key){
        return container.containsKey(key);
    }
    
    public String get_value(String key){
        return container.get(key);
    }
    
}
