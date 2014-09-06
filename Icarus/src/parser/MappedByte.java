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
 * This class is only for the communication with the IOInterface
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class
MappedByte
{
        private String    byte_address;
        private boolean[] bits;
        private byte      dbyte;
        private char      abilities;

        /**
         * @param byte_address the byte address as string 0-65536
         */
        public
        MappedByte(String byte_address)
        {
                this.byte_address = byte_address;
                bits = new boolean[8];
        }

        /**
         * set_bit will set the bit in this byte
         * @param bit the bit which should be set
         * @param value the value this bit should have
         */
        public void
        set_bit(String bit, boolean value)
        {
                int bit_num = Integer.parseInt(bit);
                bits[bit_num] = value;
//                byte tmp = 0b0000_0000;

                switch(bit_num){
                case 7:
                        if (bits[0]){
                                dbyte = (byte)(dbyte & 0b1000_0000);
                        }
                        break;
                case 6:
                        if (bits[1]){
                                dbyte = (byte)(dbyte & 0b0100_0000);
                        }
                        break;
                case 5:
                        if (bits[2]){
                                dbyte = (byte)(dbyte & 0b0010_0000);
                        }
                        break;
                case 4:
                        if (bits[3]){
                                dbyte = (byte)(dbyte & 0b0001_0000);
                        }
                        break;
                case 3:
                        if (bits[4]){
                                dbyte = (byte)(dbyte & 0b0000_1000);
                        }
                        break;
                case 2:
                        if (bits[5]){
                                dbyte = (byte)(dbyte & 0b0000_0100);
                        }
                        break;
                case 1:
                        if (bits[6]){
                                dbyte = (byte)(dbyte & 0b0000_0010);
                        }
                        break;
                case 0:
                        if (bits[7]){
                                dbyte = (byte)(dbyte & 0b0000_0001);
                        }
                        break;
                }
        }

        /**
         * set_byte will just set the Mapped byte to the given byte
         * @param new_value the new value this byte should have
         */
        public void
        set_byte(byte new_value)
        {
                dbyte = new_value;
        }

        /**
         * get_byte will return the byte of this mappedbyte
         * @return the byte of this mappedbyte
         */
        public byte
        get_byte()
        {
                return dbyte;
        }

        /**
         * get_address will return the byte_address of this byte
         * @return byte address as String
         */
        public String
        get_address()
        {
                return byte_address;
        }

        /**
         * set_abilities will set the abilities, I or Q
         * @param abilities the abilities as char
         */
        public void
        set_abilities(char abilities)
        {
                this.abilities = abilities;
        }

        /**
         * get_abilities will return the abilities
         * @return the abilities as char
         */
        public char
        get_abilities()
        {
                return abilities;
        }

        /**
         * toString returns the String representation of this byte
         * @return the string representation of this byte
         */
        @Override
        public String
        toString()
        {
            return new Byte(dbyte).toString();
        }
}
