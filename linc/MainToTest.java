
import test.Condition_Stack;
import test.Index_Stack;

/*
 * Copyright (c) 2014, linc
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

/**
 *
 * @author linc
 */
public class MainToTest {
    

    public static void main(String[] args){
        
        Index_Stack iStack = new Index_Stack();
        Condition_Stack cStack = new Condition_Stack();
        
        iStack.push(2);
        iStack.push(5);
        iStack.push(9);
        cStack.push("a == 3");
        cStack.push("b == 0");
        cStack.push("c == 5");
        
        System.out.println(iStack.getFirst());
        iStack.pop();
        System.out.println(iStack.getFirst());
        iStack.pop();
        System.out.println(iStack.getFirst());
        iStack.pop();
        System.out.println(cStack.getFirst());
        cStack.pop();
        System.out.println(cStack.getFirst());
        cStack.pop();
        System.out.println(cStack.getFirst());
        cStack.pop();
        System.out.println(cStack.getFirst());
        cStack.pop();
    }
    
}
