package parser;

public class
intStack
{
	private final int   EMPTY = 0;
	
	private int   count = 0;
	private int   index = -1;
	
	private int   stack_size;
	private int[] stack;
	private int   pop_value;
	private int   peek_value;
	
	public
	intStack(int stack_size)
	{
		this.stack_size = stack_size;
		stack           = new int[stack_size];
		index = 0;
		count = 0;
	}
	
	public int
	peek() throws StackOverflowError
	{
		try {
		peek_value = stack[index -1];
		
		return peek_value;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new StackOverflowError(e+" Stackindex: "+index);
		}
	}
	
	public void
	push(int new_top) throws StackOverflowError
	{
		try {
			stack[index] = new_top;
			index++;
			count++;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new StackOverflowError(e+" Stackindex: "+index);
		}
	}
	
	public int
	pop()  throws StackOverflowError
	{
		try {
			index--;
			count--;
			
			pop_value    = stack[index];
			stack[index] = EMPTY;
			
			return pop_value;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new StackOverflowError(e+" Stackindex: "+index);
		}
	}
	
	public boolean
	empty()
	{
		if (index == -1)
			return true;
		else
			return false;
	}
}
