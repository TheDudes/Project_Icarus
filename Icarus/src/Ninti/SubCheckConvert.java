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
package Ninti;

/**
 *
 * @author Jonas Huber <Jonas_Huber2@gmx.de>
 */
public abstract class SubCheckConvert {

    private final char MIN_VALUE = '-';// Variable to Check if it's not Negative
    private final int toChar = 48;     // Variable for int to Char conversion
    private int minuent, subtrahent;
// Variables for the converted chars for subtracting
    private char minuend, subtrahend;
// Variables to hold the tmp chars for subtracting
    private int a;
// Variable for subtracting 1 if ur minuend is lower than the subtrahend
    private int tmp;
    private char tmp1;

    /**
     * Function who checks the given String if it's more or less than the
     * MAX_VALUE
     *
     * @param aValue
     * @return the state of the Value(lower than max, or higher)
     * @throws UnsignedException
     */
    /* --fixme-- */
    /* Javadoc: Missing tag for parameter MAX_VALUE */
    public boolean check(StringBuilder aValue, String MAX_VALUE) throws UnsignedException {

        if (aValue.charAt(0) == MIN_VALUE) {
            throw new UnsignedException();                      //is there a -
        } else if (aValue.length() < MAX_VALUE.length()) {      //less letters?

            return false;

        } else if (aValue.toString().equals(MAX_VALUE)) {
            return false;
        } else if (aValue.length() == MAX_VALUE.length()) {     //same length?
            for (int i = 0; i < MAX_VALUE.length(); i++) {
//loop to check if the value at letter i at aValue is higher than Max_VALUE
                if (aValue.charAt(i) > MAX_VALUE.charAt(i)) {

                    return true;
                }
            }

            //value is less than MAX
        } else {

            return true;
        }

        return true;

    }

    /**
     * Function for subtracting the Strings It's awesome I mean, look at it!
     *
     * @param aValue
     * @return the one time subtracted aValue
     */
    /* --fixme-- */
    /* Javadoc: Missing tag for parameter MaxForSub */
    public StringBuilder sub(StringBuilder aValue, String MaxForSub) {

        while (aValue.length() > MaxForSub.length()) {
//fills up 0's if value got more numbers

            MaxForSub = "0" + MaxForSub;
        }

        for (int i = aValue.length(); i >= 1; i--) {

            if (MaxForSub.charAt(i - 1) > aValue.charAt(i - 1)) {

                tmp = i - 1;
                while (aValue.charAt(tmp - 1) == '0') {

                    aValue.setCharAt(tmp - 1, '9');
                    tmp--;

                    if (tmp == 0) {
                        break;
                    }
                    if (aValue.charAt(tmp - 1) != '0') {
                        tmp1 = aValue.charAt(tmp - 1);
                        aValue.setCharAt(tmp - 1, convertAndSubtrac1(tmp1));
                        minuend = aValue.charAt(i - 1);
                        subtrahend = MaxForSub.charAt(i - 1);
                        aValue.setCharAt(i - 1, convertAndSubtrac10(minuend, subtrahend));
                        break;

                    }
                }
            } else {

                minuend = aValue.charAt(i - 1);
                subtrahend = MaxForSub.charAt(i - 1);
                aValue.setCharAt(i - 1, convertAndSubtrac(minuend, subtrahend));

            }

        }

        if (aValue.charAt(0) == '0') {
            aValue.deleteCharAt(0);
        }
        aValue.setCharAt(aValue.length() - 1, convertAndSubtrac1(aValue.charAt(aValue.length() - 1)));
        return aValue;
    }

    /**
     * Function converts chars to int and adds 10 and subtract both params from
     * each other
     *
     * @param aChar
     * @param bChar
     * @return the subtracted char
     */
    public char convertAndSubtrac10(char aChar, char bChar) {

        minuent = (int) aChar;
        subtrahent = (int) bChar;
        minuent = (minuent + 10) - subtrahent;
        minuent = minuent + toChar;
        aChar = (char) minuent;
        return aChar;
    }

    /**
     * function subtracs both params from each other witouth adding 10
     *
     * @param aChar
     * @param bChar
     * @return subtracted number
     */
    public char convertAndSubtrac(char aChar, char bChar) {

        minuent = (int) aChar;
        subtrahent = (int) bChar;
        minuent = minuent - subtrahent;
        minuent = minuent + toChar;
        aChar = (char) minuent;
        return aChar;
    }

    /**
     *
     * @param aChar
     * @return a char 1 lower
     *
     */
    public char convertAndSubtrac1(char aChar) {

        a = (int) aChar;
        a = a - 1;
        aChar = (char) a;

        return aChar;
    }
}
