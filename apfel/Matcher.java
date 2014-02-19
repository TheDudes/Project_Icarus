package parser;

import java.util.*;

public class Matcher {
    private Analyser anal;
    private StringBuilder builder;
    List<ArrayList<Integer>> list; // list with all lists from Analyser
    HashMap<Integer,Integer> ifmatching; // map with if - end_if pairs
    HashMap<Integer,Integer> casematching; // map with case - end_case pairs
    Stack<Integer> stack; // a temp stack to find the pairs
    ArrayList<Integer> ifs; // list with all the ifs
    ArrayList<Integer> cases; // list with all the cases
    HashMap<Integer,String> ifendif; // map with if index and if keyword
    HashMap<Integer,String> caseendcase; // map with case index and case keyword

    public Matcher(Analyser anal, StringBuilder builder) {
	this.anal = anal;
	this.builder = builder;
	list = anal.giveMeAllTheLists();
	getherIfList();
	findIfEndIfPairs();
    }

    // if
    private void getherIfList() {
	ifs = list.get(6);
	ifendif = new HashMap<>();
	//String ifendif;
	for (Integer item : ifs) {
	    if (builder.substring(item, item+2).equals("IF")) {
		ifendif.put(item, "IF");
	    } else {
		ifendif.put(item, "END_IF");
	    }
	}
    }

    private void findIfEndIfPairs() {
	stack = new Stack<>();
	for (Integer item : ifendif.keySet()) {
	    if ( ifendif.get(item).equals("IF") ) {
		stack.push(item);
	    } else {
		ifmatching.put(stack.pop(), item);
	    } // will throw, if_not_closed_exception
	}
    }
    
    public int findEndIf(int a) {
	return (int)ifmatching.get(new Integer(a));
    }

    // case
    private void getherCaseList() {
	cases = list.get(7);
	caseendcase = new HashMap<>();
	//String ifendif;
	for (Integer item : ifs) {
	    if (builder.substring(item, item+2).equals("CASE")) {
		caseendcase.put(item, "CASE");
	    } else {
		caseendcase.put(item, "END_CASE");
	    }
	}
    }

    private void findCaseEndCasePairs() {
	stack = new Stack<>();
	for (Integer item : caseendcase.keySet()) {
	    if ( caseendcase.get(item).equals("CASE") ) {
		stack.push(item);
	    } else {
		casematching.put(stack.pop(), item);
	    } // will throw, case_not_closed_exception
	}
    }
    
    public int findEndCase(int a) {
	return (int)casematching.get(new Integer(a));
    }
}
