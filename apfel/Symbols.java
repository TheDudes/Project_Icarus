package parser;

import java.util.*;

/*
 * This class detects all Symbols in the code und throws them in some hashmaps
 * the valuetab holds all values of the variables
 * the lookuptab holds the information where the variable belongs to
 * the nametab holds all the variable names 
 * all three tables a referenzed by integer IDs
 */


public class Symbols {
    private LangDef ld = new LangDef();
    public Map<Integer, Object> valuetab = new HashMap<>();
    public Map<Integer, String> lookuptab = new HashMap<>();
    public Map<Integer, String> nametab = new HashMap<>();
    private int id = 0;
    StringBuilder builder;
    
    private void replaceWithId(String name) {
	int itmp;
	int from;
	boolean flag = true;
	String namer = "~"+id+"~";
	while (flag == true) {
	    flag = false;
	    for (int i = 0; i < ld.token.length; i++) {
		for (int j = 0; j < ld.token.length; j++) {
		    itmp = builder.indexOf(ld.token[i]+name+ld.token[j]);
		    if ( itmp != -1 ) {
			//System.out.println(builder.toString());
			//System.out.println(ld.token[i]);
			//System.out.println(itmp+ld.token.length + " and " + name.length());
			from = itmp+ld.token[i].length();
			builder.delete(from, from+name.length());
			//System.out.println(sb.toString());
			System.out.println(id);
			builder.insert(from, namer);
			System.out.println(id);
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
	    tmp = global.indexOf(";", indexopstart);
	    var = new StringBuilder(global.substring(indexopstart, tmp));
	    indexstart = tmp+1;
	    tmp = var.indexOf(":=");
	    if ( tmp == -1 ) {
		tmp = var.indexOf(":");
		String name = var.substring(0, tmp);
		String type = var.substring(tmp+1);
		valuetab.put(id, TYPES.getType(type));
		lookuptab.put(id, "GLOBAL");
		nametab.put(id, name);
		replaceWithId(name);
		System.out.println(id);
		id++;
		System.out.println(id);
	    } else {
		tmp = var.indexOf(":");
		tmp2 = var.indexOf(":=");
		String name = var.substring(0, tmp);
		String type = var.substring(tmp+1,tmp2);
		String value = var.substring(tmp2+2);
		valuetab.put(id, TYPES.getType(type, value));
		lookuptab.put(id, "GLOBAL");
		nametab.put(id, name);
		replaceWithId(name);
		System.out.println(id);
		id++;
		System.out.println(id);
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
	    for(String item : ld.token) {
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
		tmp2 = global.indexOf(";", indexopstart);
		var = new StringBuilder(global.substring(indexopstart, tmp2));
		indexstart = tmp2+1;
		tmp2 = var.indexOf(":=");
		if ( tmp2 == -1 ) {
		    tmp2 = var.indexOf(":");
		    String name = var.substring(0, tmp2);
		    String type = var.substring(tmp2+1);
		    valuetab.put(id, TYPES.getType(type));
		    lookuptab.put(id, "GLOBAL");
		    nametab.put(id, name);
		    replaceWithId(name);
		    id++;
		} else {
		    tmp2 = var.indexOf(":");
		    tmp3 = var.indexOf(":=");
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

    private void setVar() {
	String start = "VAR";
	String stop = "END_VAR";
	String varend = ";";
	StringBuilder local;
	int varcursor;
	int seperator;
	for(int tmp = builder.indexOf(start); tmp != -1; tmp = builder.indexOf(start, tmp+start.length())) {
	    local = new StringBuilder(builder.substring(tmp+start.length(), builder.indexOf(stop)));
	    varcursor = tmp+start.length();
	    for(int tmp2 = builder.indexOf(varend); tmp2 != -1; builder.indexOf(varend, tmp2+1)) {
		String var = builder.substring(varcursor, tmp2);
		String[] collon = var.split(":");
		
	    }
	}
    }

    public Symbols(StringBuilder builder) {
	this.builder = builder;
	setGlobals();
    }
}
