package parser;

import java.io.*;
import java.util.*;

public class Parser {
    public static void main(String[] args) throws FileNotFoundException, IOException {
	StringBuilder tmp = MergeFiles.mergeAll(args);
	Analyser test = new Analyser(tmp);
	Matcher match = new Matcher(test, tmp);
	
    }
}
