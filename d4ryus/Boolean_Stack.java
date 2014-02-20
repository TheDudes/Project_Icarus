import java.util.ArrayList;

/*
 * @author d4ryus
 */

public class Boolean_Stack {

    /**
     * ArrayList containing the Elements of the Stack, basically the heart of the class,
     * contains Strings
     */
    private ArrayList<Boolean> container = new ArrayList<Boolean>();

    /*
     * stores the element in the Stack/Array
     * @param toStore the element to be stored in the Stack/Array
     */
    public void push(Boolean toStore){
        container.add(0, toStore);
    }

    public Boolean get_first(){
        return (container.get(0));
    }

    public void pop(){
        container.remove(0);
    }
}
