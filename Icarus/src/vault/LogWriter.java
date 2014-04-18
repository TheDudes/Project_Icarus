package vault;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.Timestamp;

public class LogWriter{
	
	ArrayList<LogWriterWorker> workers = new ArrayList<>();
	String configPath = "/home/vault/testConfig";
	Config_Reader configReader = new Config_Reader(configPath);
	FileOutputStream fos = null;
	PrintWriter write = null;
	Date date = new Date();
	Timestamp currentTimestamp = new Timestamp(date.getTime());
	String timeStamp;
	String logLine;	
	String[] path;
	String[] keys;

	public static void main(String[] args) throws IOException{
		LogWriter logwrite = new LogWriter();
	}
	
	public void log(String key, String message)throws IOException{
		startWorker(key, message);
	}

	public void startWorker(String key, String message){
		path = configReader.get_path(key);		
		for(int i = 0; i < path.length; i++){
			//workers.add(new LogWriterWorker(key, path, message));
		}	
	}
}	
