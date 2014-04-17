package Icarus;

import java.util.ArrayList;

/**
 * Boolean_Stack class, simple Class which works like a Stack, you can push/pop Boolean
 * values.
 * @author d4ryus <w.wackerbauer@yahoo.de>
 */

public class Boolean_Stack {

    /**
     * ArrayList containing the Elements of the Stack, basically the heart of the
     * class, contains Strings
     */
    private ArrayList<Boolean> container = new ArrayList<Boolean>();

    /**
     * stores the element in the Stack/Array
     * @param toStore Boolean element which will pushed to the Stack
     */
    public void push(Boolean toStore){
        container.add(0, toStore);
    }

    /**
     * @return last pushed boolean value from stack
     * this will NOT remove the first element.
     */
    public Boolean get_first(){
        return (container.get(0));
    }

    /**
     * removes the last pushed boolean value from stack
     */
    public void pop(){
        container.remove(0);
    }
}
