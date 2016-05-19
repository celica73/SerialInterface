package com.scott.serial.control;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Opens a log file and writes any serial port communications to the log file.
 * @author Scott Johnson
 *
 */
public class WriteLog {
	private String filename;
	private Logger logger;
	private boolean logOpen;

	public WriteLog(String filename) {
		this.filename = filename;
		logOpen = false;
		openLog();
	}

	/**
	 * Start logging.
	 * @return true if the file is opened.
	 */
	public boolean openLog() {
		logger = Logger.getLogger("MyLog");
		FileHandler fh;
		try {

			// This block configures the logger with handler and formatter
			fh = new FileHandler(filename, true);
			logger.addHandler(fh);
			// logger.setLevel(Level.ALL);
			logger.setUseParentHandlers(false); //do not log to console
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			logger.info("My first log");

		} catch (SecurityException e) {
			e.printStackTrace();
			return logOpen;
		} catch (IOException e) {
			e.printStackTrace();
			return logOpen;
		}
		logOpen = true;
		return logOpen;
	}
	
	public boolean getLogOpen() {
		return logOpen;
	}

	public void write(String logInfo) {
		logger.info(logInfo);	
	}
	
	public void stopLog(){
		LogManager.getLogManager().reset();
	}
}
