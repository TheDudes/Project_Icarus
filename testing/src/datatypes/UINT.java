/**
 * Copyright (c) 2014, Jonas Huber <Jonas_Huber2@gmx.de>
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
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
package datatypes;

/**
 * 
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public class UINT extends SubCheckConvert {

    private final String MAX_VALUE = "65535";
    // Variable who holds the Max Value of the UINT                        
    private String MaxForSub = MAX_VALUE;
    // Variable to hold the String for subtracts
    private StringBuilder value;
    // Variable to hold the Value of the Unsigned Integer

    /**
     * Constructor
     *
     * @param aValue value of the integer given in strings
     * @throws UnsignedException
     */
    public UINT(String aValue) throws UnsignedException {

        StringBuilder temp = new StringBuilder(aValue);
        while (check(temp, MAX_VALUE)) {
            temp = sub(temp, MaxForSub);

        }
        value = temp;

    }

    /**
     * GETTER
     *
     * @return value of the unsigned integer
     */
    @Override
    public String toString() {
        return value.toString();
    }

}
