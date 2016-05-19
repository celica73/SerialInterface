package com.scott.serial.control;

import java.io.UnsupportedEncodingException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Reads new data from the serial port. Generally will require an extra thread to keep updating.
 * 
 * @author Scott Johnson
 */
public class ReadData  {

	private static SerialPort serialPort;
	private static StringBuffer newData;

	public ReadData(SerialPort newPort) {
		serialPort = newPort;
		newData = new StringBuffer();
		int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;// Prepare mask
		try {
			serialPort.setEventsMask(mask); // Set mask
			serialPort.addEventListener(new SerialPortReader());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Gets any new data in the buffer, and returns it.
	 * @return String Buffer containing any new data.
	 */
	public StringBuffer getNewData(){
		return newData;
	}
	
	/**
	 * @return true if new data is present in the buffer
	 */
	public boolean hasData(){
		return newData.length() > 0;
	}
	
	/**
	 * Clears the buffer once any data has been retrieved.
	 * @param number of characters to remove from the buffer.
	 */
	public void clearBuffer(int length){
		newData.delete(0, length);
	}
	
	/**
	 * Serial Port reader implements the listener on the serial port and appends the new data buffer as needed.
	 * @author Scott Johnson
	 */
	static class SerialPortReader implements SerialPortEventListener {

		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR()) {// If data is available
				if (event.getEventValue() > 0) {// Check bytes count in the input buffer
					try {
						byte buffer[] = serialPort.readBytes();
						newData.append(new String(buffer,"UTF-8"));
					} catch (SerialPortException ex) {
						System.out.println(ex);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			//legacy code. Not needed here.
//				else if (event.isCTS()) {// If CTS line has changed state
//				if (event.getEventValue() == 1) {// If line is ON
//					System.out.println("CTS - ON");
//				} else {
//					System.out.println("CTS - OFF");
//				}
//			} else if (event.isDSR()) {/// If DSR line has changed state
//				if (event.getEventValue() == 1) {// If line is ON
//					System.out.println("DSR - ON");
//				} else {
//					System.out.println("DSR - OFF");
//				}
//			}
		}
	}
}
