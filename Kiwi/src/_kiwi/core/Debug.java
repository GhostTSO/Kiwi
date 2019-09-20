package _kiwi.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class Debug {
	private static final HashMap<Integer, Logger>
		INDEX =      new HashMap<Integer, Logger>();
	private static int
		LEVEL;
	
	public static final int
		DEBUG  = 16,
		ERROR  = 32;
	public static final Logger
		out = setLogger(DEBUG , "DEBUG" , System.out, null),		
		err = setLogger(ERROR , "ERROR" , System.err, null);
	
	public static final void log(int level) {
		getLogger(level).log();
	}
	
	public static final void log(int level, Object msg) {
		getLogger(level).log(msg);
	}
	
	public static final void setLevel(int level) {
		LEVEL = level;
	}
	
	public static final Logger setLoggerName(int level, String name) {
		Logger logger = getLogger(level);
		logger.name = name;
		return logger;
	}
	
	public static final Logger setLoggerStream(int level, PrintStream stream) {
		Logger logger = getLogger(level);
		logger.stream = stream;
		return logger;
	}
	
	public static final Logger setLoggerWriter(int level, PrintWriter writer) {
		Logger logger = getLogger(level);
		logger.writer = writer;
		return logger;
	}
	
	public static final Logger setLogger(int level, String name, PrintStream stream, PrintWriter writer) {
		Logger logger = getLogger(level);
		logger.writer = writer;
		logger.stream = stream;
		logger.name = name;
		return logger;
	}
	
	public static final int getLevel() {
		return LEVEL;
	}
	
	public static final Logger getLogger(int level) {
		Logger logger = INDEX.get(level);
		if(logger == null) 
			INDEX.put(
					level,
					logger = new Logger(level)
					);
		return logger;
	}
	
	public static class Logger {
		private final int
			level;
		
		private String
			name;
		private PrintStream
			stream;
		private PrintWriter
			writer;
		
		public Logger(int level) {
			this.level = level;
		}
		
		public void log() {
			log("");
		}
		
		public void log(Object msg) {
			if(this.level >= Debug.LEVEL) {
				String[] tmp = msg.toString().split(System.lineSeparator());
				for(int i = 0; i < tmp.length; i ++) {
					tmp[i] = tmp[i].trim();
					if(stream != null) stream.println("[" + this.name + "] " + tmp[i]);
					if(writer != null) writer.println("[" + this.name + "] " + tmp[i]);
				}
			}
		}
	}
}
