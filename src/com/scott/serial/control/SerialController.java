package com.scott.serial.control;

/**
 * Controller class for the serial port monitor.
 * @author Scott Johnson
 */
public class SerialController {

	private ListPorts portList;
	private OpenPort openPort;
	private ReadData reader;
	private WriteData writer;
	private WriteLog log;
	private boolean logging;

	/**
	 * Opens a new port.
	 */
	public SerialController() {
		portList = new ListPorts();
		openPort = OpenPort.getInstance();
		logging = false;
	}

	public String[] getPorts() {
		return portList.getPortName();
	}

	public String[] getBaudList() {
		return openPort.getParameterList()[0];
	}

	public String[] getBitsList() {
		return openPort.getParameterList()[1];
	}

	public String[] getFlowList() {
		return openPort.getParameterList()[2];
	}

	public String[] getParityList() {
		return openPort.getParameterList()[3];
	}

	/**
	 * Writes data to the log file.
	 * @param data string to be written.
	 */
	public void writeData(String data){
		if (getStatus())
			writer.write(data);
		if(logging)
			log.write(data + "\n");
	}
	/**
	 * Reads new data from the serial port and clears the buffer as needed.
	 * @return any new data
	 */
	public String readData(){
		if (getStatus() && reader.hasData()){
			String output = reader.getNewData().toString();
			if(logging)
				log.write(output);
			reader.clearBuffer(output.length());
			return output;
		}
		return null;
	}
	/**
	 * Initialize the new serial port with user supplied parameters.
	 * @param parameters
	 */
	public void initialize(String[] parameters) {
		openPort.startConnection(parameters[4], Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]),
				Integer.parseInt(parameters[2]), Integer.parseInt(parameters[3]));
		if (getStatus()) {
			reader = new ReadData(openPort.getActivePort());
			writer = new WriteData(openPort.getActivePort());
		}
	}

	public void closeConnection() {
		openPort.closeConnection();
	}

	public boolean getStatus() {
		return 	openPort != null && openPort.portStatus();
	}
	/**
	 * Starts logging to the specified log file.
	 * @param filename
	 */
	public void startLogging(String filename){
		log = new WriteLog(filename);
		logging = log.getLogOpen();
	}
	
	public void stopLogging(){
		logging = false;
		log.stopLog();
	}
}
