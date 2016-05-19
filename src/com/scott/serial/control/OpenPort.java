package com.scott.serial.control;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Supplies configuration options and opens the serial port. Runs as a singleton instance so only one
 * port can be opened at a time, and the port won't be busy if "re-opened."
 * @author Scott Johnson
 */
public class OpenPort {

	private static OpenPort serialPort;
	
	public static final int[] BAUD_RATES = { SerialPort.BAUDRATE_110, SerialPort.BAUDRATE_300, SerialPort.BAUDRATE_600,
			SerialPort.BAUDRATE_1200, SerialPort.BAUDRATE_4800, SerialPort.BAUDRATE_9600, SerialPort.BAUDRATE_14400,
			SerialPort.BAUDRATE_19200, SerialPort.BAUDRATE_38400, SerialPort.BAUDRATE_57600,
			SerialPort.BAUDRATE_115200 };
	public static final int[] DATA_SIZE = { SerialPort.DATABITS_5, SerialPort.DATABITS_6, SerialPort.DATABITS_7,
			SerialPort.DATABITS_8 };
	public static final int[] STOP_BITS = { SerialPort.STOPBITS_1, SerialPort.STOPBITS_2, SerialPort.STOPBITS_1_5 };
	public static final int[] PARITY = { SerialPort.PARITY_NONE, SerialPort.PARITY_ODD, SerialPort.PARITY_EVEN,
			SerialPort.PARITY_MARK, SerialPort.PARITY_SPACE };

	private SerialPort activePort;
	private int baud;
	private int dataBits;
	private int stopBits;
	private int parity;
	private String port;
	
	private OpenPort(){
	}
	
	public static OpenPort getInstance(){
		if (serialPort == null) {
			serialPort = new OpenPort();
		}
		return serialPort;
	}
	
	public void startConnection(String name, int baud, int dataBits, int stopBits, int parity){
		this.port = name;
		this.activePort = new SerialPort(port);
		this.baud = baud;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		
		try {
			activePort.openPort();
			activePort.setParams(this.baud, this.dataBits, this.stopBits, this.parity); // Set params
		} catch (SerialPortException e) {
			throw new IllegalArgumentException("Error opening serial port: " + e.getExceptionType());
		}// Open port
	}
	
	public String[][] getParameterList(){
		String[][] listing = new String[4][];
		listing[0] = new String[BAUD_RATES.length];
		listing[1] = new String[DATA_SIZE.length];
		listing[2] = new String[STOP_BITS.length];
		listing[3] = new String[PARITY.length];
		for (int i = 0; i < BAUD_RATES.length; i++) {
			listing[0][i] = "" + BAUD_RATES[i]; 			
		}
		for (int i = 0; i < DATA_SIZE.length; i++) {
			listing[1][i] = "" + DATA_SIZE[i]; 			
		}
		for (int i = 0; i < STOP_BITS.length; i++) {
			listing[2][i] = "" + STOP_BITS[i]; 			
		}
		for (int i = 0; i < PARITY.length; i++) {
			listing[3][i] = "" + PARITY[i]; 			
		}
		return listing;
	}

	public void closeConnection() {
		try {
			activePort.closePort();
		} catch (SerialPortException e) {
			throw new IllegalArgumentException("Error closing serial port: " + e.getExceptionType());
		}
	}
	
	public SerialPort getActivePort(){
		return activePort;
	}
	
	public boolean portStatus(){
		return activePort != null && activePort.isOpened();
	}
}
