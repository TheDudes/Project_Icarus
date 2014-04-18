package vault;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.Timestamp;

public class LogWriterWorker extends Thread{

	String configPath = "/home/vault/testConfig";
	Config_Reader configReader = new Config_Reader(configPath);
	Map<String, FileOutputStream[]> fileStreams = new HashMap<>();
	FileOutputStream fos = null;
	PrintWriter write = null;
	Date date = new Date();
	Timestamp currentTimestamp = new Timestamp(date.getTime());
	String timeStamp;
	String logLine;	
	String[] key;
	String[] paths;
	String message;
	static int count = 1;

 	public LogWriterWorker(){
	
	}
	public static void main(String[] args) throws FileNotFoundException{
		LogWriterWorker logWriter = new LogWriterWorker();
		logWriter.getFileStreams();
	}	
	public void getFileStreams() throws FileNotFoundException{
		key = configReader.get_keys();
		for(int i = 0; i < key.length; i++){
			paths = configReader.get_path(key[i]);
			for(int j = 0; j < paths.length; j++){
				//fileStreams.put(key[i], new FileOutputStream(paths[j], true));
			}
		}
		
	/*	
		for(int i = 0; i < paths.length; i++){
			try{
				fos = new FileOutputStream(paths[i], true);
				write = new PrintWriter(
				 	 new BufferedWriter(
		    	    		  new OutputStreamWriter(fos, "UTF-8")));
			} catch(IOException e){} 
		}*/
		System.out.println(fileStreams.keySet());
		//System.out.println((String)fileStreams.values());
	}
	
	public void run(){
	}

	/* 
		timeStamp = "[" + currentTimestamp + "] ";
		logLine = timeStamp + "[" + key + "]" + ": " + message + "\n";	
		write.append(logLine);
		*/
}
