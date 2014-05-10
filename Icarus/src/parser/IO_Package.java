


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
	/**
	 * The variables this class holds
	 */
	public  int      device_id;
	public  int      pin_id;
	public  boolean  value;

	/**
	 * io_package need the device_id, the pin_id and the bool value for the PIN
	 * 
	 * @param device_id  the device_id as int
	 * @param pin_id     the pin_id as int
	 * @param value      the value as boolean
	 */
	public
	IO_Package (int device_id, int pin_id, boolean value)
	{
		this.device_id  = device_id;
		this.pin_id     = pin_id;
		this.value      = value;
	}
}










