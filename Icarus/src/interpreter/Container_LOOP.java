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
 *
 * file: Icarus/src/interpreter/Container_LOOP.java
 * vim: foldmethod=syntax:foldcolumn=5:
 */
package interpreter;

/**
 * @author d4ryus - https://github.com/d4ryus/
 * <p>
 * class/struct to hold Loop values.
 * <p>
 * @version 0.8
 */
public class Container_LOOP
{

    /* WHILE, REPEAT and FOR values */
    /* loop type 0 = WHILE, 1 = FOR, 2 = REPEAT */
    public int type;                // loop type
    public int INDEX;               // loop position
    public int do_index;            // loop entry point
    public int end_index;           // loop end point

    /* WHILE value */
    public String condition;        // loop condition

    /* FOR values */
    public int limit;               // max FOR count
    public int count;               // FOR count
    public int by;                  // FOR + value
    public Boolean name_given;      // name given or not
    public String name;             // value name
}
