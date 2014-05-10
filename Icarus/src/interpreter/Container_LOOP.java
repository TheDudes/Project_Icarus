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
 * vim: foldmethod=syntax:foldcolumn=1:
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
    /** INDEX of Loop Starting Point */
    public int INDEX;
    /** INDEX of Loop jump in Point, DO for example  */
    public int do_index;
    /** INDEX of Loop END_ Keyword Point, END_WHILE for example  */
    public int end_index;

    /** only used by WHILE loop, holds the condtition String */
    public String condition;

    /** only used by FOR loop, holds the limit */
    public int limit;
    /** only used by FOR loop, holds the current count */
    public int count;
    /** only used by FOR loop, holds the by value */
    public int by;
    /** only used by FOR loop, if true a variable name was given */
    public boolean name_given;
    /** only used by FOR loop, if flag true this holds the name */
    public String name;
}
