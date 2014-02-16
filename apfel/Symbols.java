package parser;

import java.util.*;

public class Symbols {
    public Map<Integer, Object> valuetab = new HashMap<>();
    public Map<Integer, String> lookuptab = new HashMap<>();
    public Map<Integer, String> nametab = new HashMap<>();
    private int id = 0;
    StringBuilder builder;
    
    private void replaceWithId(String name) {
	int itmp;
	boolean flag = true;
	while (flag = true) {
	    flag = false;
	    for (String item : LangDef.token) {
		for (String item2 : LangDef.token) {
		    //stmp = item+name+":";
		    itmp = builder.indexOf(item+name+item2);
		    if ( itmp != -1 ) {
			builder.delete(itmp+item.length(), name.length());
			builder.insert(itmp+item.length(), "~"+id+"~");
			flag = true;
		    }
		}
	    }
	}
    }

    private void setGlobals() {
	String start = "VAR_GLOBAL";
	String stop = "END_VAR";
	int indexstart = builder.indexOf(start);
	int indexstop = builder.indexOf(stop, indexstart);
	StringBuilder global = new StringBuilder(builder.substring(indexstart, indexstop));
	StringBuilder var;
	int tmp;
	int tmp2;
	int indexopstart = start.length();
	while ((indexopstart - indexstop) < 0) {
	    // System.out.println("Debug");
	    // System.out.println(global.toString());
	    tmp = global.indexOf(";", indexopstart);
	    //System.out.println(tmp);
	    var = new StringBuilder(global.substring(indexopstart, tmp));
	    //  System.out.println(var.toString());
	    indexstart = tmp+1;
	    // System.out.println(indexopstart);
	    tmp = var.indexOf(":=");
	    //System.out.println(tmp);
	    if ( tmp == -1 ) {
		tmp = var.indexOf(":");
		//   System.out.println(tmp);
		String name = var.substring(0, tmp);
		//   System.out.println(var.toString());
		String type = var.substring(tmp+1);
		valuetab.put(id, TYPES.getType(type));
		lookuptab.put(id, "GLOBAL");
		nametab.put(id, name);
		replaceWithId(name);
		id++;
	    } else {
		tmp = var.indexOf(":");
		tmp2 = var.indexOf(":=");
		//   System.out.println(tmp);
		//   System.out.println(tmp2);
		String name = var.substring(0, tmp);
		String type = var.substring(tmp+1,tmp2);
		String value = var.substring(tmp2+2);
		valuetab.put(id, TYPES.getType(type, value));
		lookuptab.put(id, "GLOBAL");
		nametab.put(id, name);
		replaceWithId(name);
		id++;
	    }
	}
	var = global.delete(indexstart, indexstop+stop.length());
	var = null;
    }

    private void setLocalsProgramm() {
	String start = "PROGRAM";
	String stop = "END_PROGRAM";
	String progname;
	int endpointer;
	int cursor;
	List<Integer> list = new ArrayList<>();
	for(int tmp = builder.indexOf(start); tmp != -1; tmp = builder.indexOf(start, tmp+start.length())) {
	    endpointer = builder.indexOf(stop, tmp);
	    for(String item : LangDef.token) {
		cursor = builder.indexOf(item, tmp);
		if ( cursor < endpointer ) {
		    endpointer = cursor;
		}
	    }
	    progname = builder.substring(tmp+start.length(), endpointer);
	    endpointer = builder.indexOf(stop, tmp);
	    String startvar = "VAR";
	    String stopvar = "END_VAR";
	    int indexstart = builder.indexOf(startvar);
	    int indexstop = builder.indexOf(stopvar, indexstart);
	    StringBuilder global = new StringBuilder(builder.substring(indexstart, indexstop));
	    StringBuilder var;
	    int tmp2;
	    int tmp3;
	    int indexopstart = startvar.length();
	    while ((indexopstart - indexstop) < 0) {
		// System.out.println("Debug");
		// System.out.println(global.toString());
		tmp2 = global.indexOf(";", indexopstart);
		//System.out.println(tmp);
		var = new StringBuilder(global.substring(indexopstart, tmp2));
		//  System.out.println(var.toString());
		indexstart = tmp2+1;
		// System.out.println(indexopstart);
		tmp2 = var.indexOf(":=");
		//System.out.println(tmp);
		if ( tmp2 == -1 ) {
		    tmp2 = var.indexOf(":");
		    //   System.out.println(tmp);
		    String name = var.substring(0, tmp2);
		    //   System.out.println(var.toString());
		    String type = var.substring(tmp2+1);
		    valuetab.put(id, TYPES.getType(type));
		    lookuptab.put(id, "GLOBAL");
		    nametab.put(id, name);
		    replaceWithId(name);
		    id++;
		} else {
		    tmp2 = var.indexOf(":");
		    tmp3 = var.indexOf(":=");
		    //   System.out.println(tmp);
		    //   System.out.println(tmp2);
		    String name = var.substring(0, tmp2);
		    String type = var.substring(tmp2+1,tmp3);
		    String value = var.substring(tmp3+2);
		    valuetab.put(id, TYPES.getType(type, value));
		    lookuptab.put(id, "GLOBAL");
		    nametab.put(id, name);
		    replaceWithId(name);
		    id++;
		}
	    }
	    var = global.delete(indexstart, indexstop+stopvar.length());
	    var = null;   
	}
    }

    public Symbols(StringBuilder builder) {
	this.builder = builder;
	setGlobals();
    }

    
}
