package parser;

import java.util.*;

public class TYPES {
    public static Object getType(String type) {
	switch(type) {
	case "BOOL":
	    return new Boolean(false);
	case "SINT":
	    return new Byte((byte)0);
	case "INT":
	    return new Short((short)0);
	case "DINT":
	    return new Integer(0);
	case "LINT":
	    return new Long(0);
	case "USINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UDINT": //throw some Exception, because no unsingend type in java
	    break;
	case "ULINT": //throw some Exception, because no unsingend type in java
	    break;
	case "REAL":
	    return new Float(0.0);
	case "LREAL":
	    return new Double(0.0);
	case "TIME":
	case "DATE":
	case "TIME_OF_DAY":
	case "TOD":
	case "DATE_AND_TIME":
	case "DT":
	    return new Integer((int) (System.currentTimeMillis() / 1000L));
	case "STRING":
	case "WSTRING":
	    return new String("");
        case "BYTE":
	    return new Boolean[8];
	case "WORD":
	    return new Boolean[16];
	case "DWORD":
	    return new Boolean[32];
	case "LWORD":
	    return new Boolean[64];
	}
	return new Object(); // ToDo: new types, return values from functions, an so on
    }

    public static Object getType(String type, String value) {
	switch(type) {
	case "BOOL":
	    return new Boolean(value);
	case "SINT":
	    return new Byte(value);
	case "INT":
	    return new Short(value);
	case "DINT":
	    return new Integer(value);
	case "LINT":
	    return new Long(value);
	case "USINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UDINT": //throw some Exception, because no unsingend type in java
	    break;
	case "ULINT": //throw some Exception, because no unsingend type in java
	    break;
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
	    return new String(value);
        case "BYTE":
	    return new Boolean[8];
	case "WORD":
	    return new Boolean[16];
	case "DWORD":
	    return new Boolean[32];
	case "LWORD":
	    return new Boolean[64];
	}
	return new Object(); // ToDo: new types, return values from functions, an so on
    }
}

