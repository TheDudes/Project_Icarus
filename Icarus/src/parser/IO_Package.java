

/*
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
	/** device id or name for the configured device */
	public  String   device_id;
	/** pin id per device */
	public  byte     pin_id;
	/** value of the pin */
	public  byte     value;
	/**
	 * abilities can only have the values 0, 1, 2, 3
	 * <p>
	 * 0 = Undefined;
	 * 1 = read/input;
	 * 2 = write/output;
	 * 3 = read-write/input-output;
	 * >3 = Undefined;
	 */
	public  byte     abilities;
	/** if true, then register for polling */
	public  boolean  to_poll;
	
	/**
	 * io_package need the device_id, the pin_id and the bool value for the PIN
	 * 
	 * @param device_id  the device_id as int
	 * @param pin_id     the pin_id as int
	 * @param value      the value as boolean
	 * @param abilities  the abilities parameter
	 */
	public
	IO_Package (String device_id, byte pin_id, byte value, byte abilities, boolean to_poll)
	{
		this.device_id  = device_id;
		this.pin_id     = pin_id;
		this.value      = value;
		this.abilities  = abilities;
		this.to_poll    = to_poll;
	}
}










