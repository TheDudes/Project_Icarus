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
IO_Package
{
	/** Memory address as a String */
	public  String   byte_address;
	/** pin id per device */
	public  String   pin_id;
	/** value of the pin */
	public  byte     value;
	/**
	 * abilities can only have the values I, W
	 * <p>
	 * I = read/input;
	 * W = write/output;
	 */
	public  char     abilities;
	/** if true, then register for polling */
	public  boolean  to_poll;
	
	/**
	 * io_package need the byte_address, the pin_id and the bool value for the PIN
	 * 
	 * @param byte_address  the byte_address as int
	 * @param pin_id     the pin_id as int
	 * @param value      the value as boolean
	 * @param abilities  the abilities parameter
	 * @param to_poll    true if this pin should be registered for polling
	 */
	public
	IO_Package (String byte_address, String pin_id, byte value, char abilities, boolean to_poll)
	{
		this.byte_address  = byte_address;
		this.pin_id     = pin_id;
		this.value      = value;
		this.abilities  = abilities;
		this.to_poll    = to_poll;
	}

        public void
        set_polling_true()
        {
                this.to_poll = true;
        }
}










