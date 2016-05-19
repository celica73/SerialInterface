package com.scott.serial.control;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Sends data to the serial port.
 * 
 * @author Scott Johnson
 */
public class WriteData {
	private SerialPort serialPort;
	
	public WriteData(SerialPort serialPort){
		this.serialPort = serialPort;
	}

	public void write(String data){
		try {
			serialPort.writeString(data); // Write data to port
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
}