/**
 * Copyright (c) 2014, HAW-Landshut
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package parser;

/**
 * a simple stack for the primitive datatype int
 * this one is faster then the java Stack<> in some
 * cases ...
 * <p>
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class
intStack
{
	private final int   EMPTY = 0;
	
//	private int   count = 0; // not used
	private int   index = -1;
	
//	private int   stack_size;
	private int[] stack;
	private int   pop_value;
	private int   peek_value;

        /**
         * @param stack_size the size of the stack
         */
	public
	intStack(int stack_size)
	{
//		this.stack_size = stack_size;
		stack           = new int[stack_size];
		index = 0;
//		count = 0;
	}

        /**
         * peek will return the top element of the stack without removing it
         * <p>
         * @return returns the value of the top value without deleting it
         * @throws StackOverflowError
         */
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

        /**
         * push will push a element on the stack
         * <p>
         * @param new_top is the pushed value
         * @throws StackOverflowError
         */
	public void
	push(int new_top) throws StackOverflowError
	{
		try {
			stack[index] = new_top;
			index++;
//			count++;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new StackOverflowError(e+" Stackindex: "+index);
		}
	}

        /**
         * pop will return and delete the top value of the stack
         * <p>
         * @return returns the top values and deletes it from the stack
         * @throws StackOverflowError
         */
	public int
	pop() throws StackOverflowError
	{
		try {
			index--;
//			count--;
			
			pop_value    = stack[index];
			stack[index] = EMPTY;
			
			return pop_value;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new StackOverflowError(e+" Stackindex: "+index);
		}
	}

        /**
         * empty checks if the stack is empty or not
         * <p>
         * @return true if the stack is empty and false if not
         */
	public boolean
	empty()
	{
		if (index == -1)
			return true;
                return false;
	}
}
