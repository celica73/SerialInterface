/**
 * @author Scott Johnson
 * @version 1.0
 * 
 * Simple class for listing the available serial ports on the system.
 */
package com.scott.serial.control;

import java.util.Arrays;

import jssc.SerialPortList;

public class ListPorts {
	private String[] portNames;
	
	public ListPorts(){
		portNames = SerialPortList.getPortNames();
	}
    
	public String[] getPortName(){
		return portNames;
	}
	
	@Override
	public String toString(){
		return Arrays.toString(portNames);
	}
}