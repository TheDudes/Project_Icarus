package parser;
/*
 * to work arround the lack of a union type ...
 */

public class VALUE {

    abstract class Val {
	public boolean BOOL;
	public byte SINT;
	public short INT;
	public int DINT;
	public long LINT;
	public float REAL;
	public double LREAL;
	public int DT;
	public String WSTRING;
	public boolean[] LWORD;

	public Val (boolean t) { this.BOOL = t;	}
	public Val (byte t) { this.SINT = t; }
	public Val (short t) { this.INT = t; }
	public Val (int t) { this.DINT = t; }
	public Val (long t) { this.LINT = t; }
	public Val (float t) { this.REAL = t; }
	public Val (double t) { this.LREAL = t; }
	public Val (String t) { this.WSTRING = t; }
	public Val (boolean[] t) { this.LWORD = t; }

	public void setValue(boolean t) { this.BOOL = t; }
	public void setValue(byte t) { this.SINT = t; }
	public void setValue(short t) { this.INT = t; }
	public void setValue(int t) { this.DINT = t; }
	public void setValue(long t) { this.LINT = t; }
	public void setValue(float t) { this.REAL = t; }
	public void setValue(double t) { this.LREAL = t; }
	public void setValue(String t) { this.WSTRING = t; }
	public void setValue(boolean[] t) { this.LWORD = t; }


	public abstract Val getVALUE();
	//public Object getValue() { return this.BOOL; }
	/*	public byte getValue() { return this.SINT; }
	public short getValue() { return this.INT; }
	public int getValue() { return this.DINT; }
	public long getValue() { return this.LINT; }
	public float getValue() { return this.REAL; }
	public double getValue() { return this.LREAL; }
	public String getValue() { return this.WSTRING; }
	public boolean[] getValue() { return this.LWORD; }*/

    }

    private class BOOL extends Val {
	public BOOL() { super(false); }
	public BOOL (boolean b) { super(b); }
	public void setValue(boolean b) { this.setValue(b); }
	public boolean getValue() { return this.BOOL; }
	public BOOL getVALUE() { return this; };
    }

    public class SINT extends Val {
	public SINT () { super((byte)0); }
	public SINT (byte b) { super(b); }
	public void setValue(byte b) { this.SINT = b; }
	public byte getValue() { return this.SINT; }
	@Override public SINT getVALUE() { return (SINT)this; };
    }

    private class INT extends Val {
	public INT () { super((short)0); }
	public INT (short b) { super(b); }
	public void setValue(short b) { this.INT = b; }
	public short getValue() { return this.INT; }
	public INT getVALUE() { return this; };
    }

    private class DINT extends Val {
	public DINT () { super(0); }
	public DINT (int b) { super(b); }
	public void setValue(int b) { this.DINT = b; }
	public int getValue() { return this.DINT; }
	public DINT getVALUE() { return this; };
    }

    private class LINT extends Val {
	public LINT () { super((long)0); }
	public LINT (long b) { super(b); }
	public void setValue(long b) { this.LINT = b; }
	public long getValue() { return this.LINT; }
	public LINT getVALUE() { return this; };
    }

    private class REAL extends Val {
	public REAL () { super(0.0f); }
	public REAL (float b) { super(b); }
	public void setValue(float b) { this.REAL = b; }
	public float getValue() { return this.REAL; }
	public REAL getVALUE() { return this; };
    }

    private class LREAL extends Val {
	public LREAL () { super(0.0); }
	public LREAL (double b) { super(b); }
	public void setValue(double b) { this.LREAL = b; }
	public double getValue() { return this.LREAL; }
	public LREAL getVALUE() { return this; };
    }

    private class DT extends Val {
	public DT () { super((int) (System.currentTimeMillis() / 1000L)); }
	public DT (int b) { super(b); }
	public void setValue(int b) { this.DT = b; }
	public int getValue() { return this.DT;	}
	public DT getVALUE() { return this; }
    }

    private class WSTRING extends Val {
	public WSTRING () { super(""); }
	public WSTRING (String b) { super(b); }
	public void setValue(String b) { this.WSTRING = b; }
	public String getValue() { return this.WSTRING; }
	public WSTRING getVALUE() { return this; }
    }

    private class LWORD extends Val {
	public LWORD () { super(new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false }); }
	public LWORD (boolean[] b) { super(b); }
	public void setValue(boolean[] b) { this.LWORD = b; }
	public boolean[] getValue() { return this.LWORD; }
	public LWORD getVALUE() { return this; }
    }

    public Val val;

    public VALUE(String type) {
	switch(type) {
	case "BOOL":
	    val = new BOOL(false);
	    break;
	case "SINT":
	    val = new SINT();
	    break;
	case "INT":
	    val = new INT();
	    break;
	case "DINT":
	    val = new DINT();
	    break;
	case "LINT":
	    val = new LINT();
	    break;
	case "USINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UINT": //throw some Exception, because no unsingend type in java
	    break;
	case "UDINT": //throw some Exception, because no unsingend type in java
	    break;
	case "ULINT": //throw some Exception, because no unsingend type in java
	    break;
	case "REAL":
	    val = new REAL();
	    break;
	case "LREAL":
	    val = new LREAL();
	    break;
	case "TIME":
	case "DATE":
	case "TIME_OF_DAY":
	case "TOD":
	case "DATE_AND_TIME":
	case "DT":
	    val = new DT((int) (System.currentTimeMillis() / 1000L));
	    break;
	case "STRING":
	case "WSTRING":
	    val = new WSTRING();
	    break;
        case "BYTE":
	case "WORD":
	case "DWORD":
	case "LWORD":
	    val = new LWORD();
	    break;
	default:
	    break;
	}

    }
}
