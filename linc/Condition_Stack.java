/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.ArrayList;

/**
 *
 * @author linc
 */
public class Condition_Stack {
    
    //ArrayList containing the Elements of the Stack, basically the heart of the class, contains Strings
    private ArrayList<String> container = new ArrayList();
    
    
    /**
     * stores the element in the Stack/Array
     * @param toStore the element to be stored in the Stack/Array
     */
    public void push(String toStore){
        container.add(0, toStore);
    }
    
    
    /**
     * gets the first Element in the Array, if the Array is empty returns the String -1 and prints an error message
     * @return the first Element in the Array/Stack
     */
    public String getFirst(){
        if(container.isEmpty()){
            System.err.println("No more Elements stored in the Condition Stack!");
            return "-1";
        }
        else{
            return (container.get(0));
        }
    }
    
    
    /**
     * deletes the first element in the Stack/Array, if it is empty prints an error message
     */
    public void pop(){
        if(container.isEmpty()){
            System.err.println("No more Elements stored in here!");
        }
        else{
            container.remove(0);
        }
    }
}
