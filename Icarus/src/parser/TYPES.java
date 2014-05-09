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


import Ninti.*;
import vault.*;
/**
 * This class only creates objects of different types to return them.
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 * @version 1.0
 */
public class
TYPES
{
	
	/* logger */
	private final LogWriter  log;
	private final String     mainkey  = "parser";
	private final String     subkey   = "TYPES";
	private final String     key      = mainkey+"-"+subkey;
	
	public
	TYPES(LogWriter log)
	{
		this.log = log;
	}

/**
 * getType only need a String representing a type, like DINT
 * it then creates a new Object of this type and returns it.
 * @param type typeString like DINT, SINT, TIME ...
 * @return Object of the right type
 * @throws Ninti.UnsignedException
 */
	public Object
	get_type(String type) throws UnsignedException
	{
		switch(type) {
		case "BOOL":
			return false;
		case "SINT":
			return (byte)0;
		case "INT":
			return (short)0;
		case "DINT":
			return 0;
		case "LINT":
			return (long) 0;
		case "USINT":
			return new USINT("0");
		case "UINT":
			return new UINT("0");
		case "UDINT":
			return new UDINT("0");
		case "ULINT":
			log.log(key, 4, "New ULINT with 0");
			return new ULINT("0");
		case "REAL":
			return (float) 0.0;
		case "LREAL":
			return 0.0;
		case "TIME":
		case "DATE":
		case "TIME_OF_DAY":
		case "TOD":
		case "DATE_AND_TIME":
		case "DT":
			return (int) (System.currentTimeMillis() / 1000L);
		case "STRING":
		case "WSTRING":
			return "";
		case "BYTE":
			return new Boolean[8];
		case "WORD":
			return new Boolean[16];
		case "DWORD":
			return new Boolean[32];
		case "LWORD":
			return new Boolean[64];
		}
		return new Object(); /* ToDo: new types, return values from functions, and so on */
	}

	/**
	 * getType only need a String representing a type, like DINT
	 * it then creates a new Object of this type and returns it.
	 * In this case it also fills the new Object with the given Value.
	 * @param type typeString like DINT, SINT, TIME ...
	 * @param value valueString like "2.3", "32323" ...
	 * @return Object of the right type with the new value
	 * @throws Ninti.UnsignedException
	 */
	public Object
	get_type(String type, String value) throws UnsignedException
	{
		switch(type) {
		case "BOOL":
			return Boolean.valueOf(value);
		case "SINT":
			return new Byte(value);
		case "INT":
			return new Short(value);
		case "DINT":
			return new Integer(value);
		case "LINT":
			return new Long(value);
		case "USINT":
			return new USINT(value);
		case "UINT":
			return new UINT(value);
		case "UDINT":
			return new UDINT(value);
		case "ULINT":
			log.log(key, 4, "New ULINT with "+value);
			return new ULINT(value);
		case "REAL":
			return new Float(value);
		case "LREAL":
			return new Double(value);
		case "TIME":
		case "DATE":
		case "TIME_OF_DAY":
		case "TOD":
		case "DATE_AND_TIME":
		case "DT":
			return new Integer(value);
		case "STRING":
		case "WSTRING":
			return value;
		case "BYTE":
			return new Boolean[8];
		case "WORD":
			return new Boolean[16];
		case "DWORD":
			return new Boolean[32];
		case "LWORD":
			return new Boolean[64];
		}
		return new Object(); /* ToDo: new types, return values from functions, an so on */
	}
}

