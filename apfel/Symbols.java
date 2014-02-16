package parser;

import java.util.*;

public class Symbols {
    public Map<String, Object> globals = new HashMap<>();
    public Map<String, String> lookup = new HashMap<>();
    public Map<String, Map<String, Map<String, Object>>> locals = new HashMap<>();
	//     function    var|in|out  varname  varvalue
    StringBuilder builder;
    

    private void setGlobals() {
	String start = "VAR_GLOBAL";
	String stop = "END_VAR";
	int indexstart = builder.indexOf(start);
	int indexstop = builder.indexOf(stop, indexstart);
	StringBuilder global = new StringBuilder(builder.substring(indexstart, indexstop));
	StringBuilder var;
	int tmp;
	int tmp2;
	indexopstart = start.length();
	while ((indexopstart - indexstop) < 0) {
	       System.out.println("Debug");
	       System.out.println(global.toString());
	    tmp = global.indexOf(";", indexopstart);
	    //System.out.println(tmp);
	    var = new StringBuilder(global.substring(indexopstart, tmp));
	       System.out.println(var.toString());
	    indexstart = tmp+1;
	       System.out.println(indexopstart);
	    tmp = var.indexOf(":=");
	    //System.out.println(tmp);
	    if ( tmp == -1 ) {
		tmp = var.indexOf(":");
		//   System.out.println(tmp);
		String name = var.substring(0, tmp);
		//   System.out.println(var.toString());
		String type = var.substring(tmp+1);
		globals.put(name, TYPES.getType(type));
		lookup.put(name, "GLOBAL");
	    } else {
		tmp = var.indexOf(":");
		tmp2 = var.indexOf(":=");
		//   System.out.println(tmp);
		//   System.out.println(tmp2);
		String name = var.substring(0, tmp);
		String type = var.substring(tmp+1,tmp2);
		String value = var.substring(tmp2+2);
		globals.put(name, TYPES.getType(type, value));
		lookup.put(name, "GLOBAL");
	    }
	}
	var = global.delete(indexstart, indexstop+stop.length());
    }

    public Symbols(StringBuilder builder) {
	this.builder = builder;
	setGlobals();
    }

    
}
