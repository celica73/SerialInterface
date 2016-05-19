package com.scott.serial.control;

import static org.junit.Assert.*;

import org.junit.Test;

public class ListPortsTest {


	@Test
	public void testListPortName() {
		ListPorts list = new ListPorts();
		assertEquals("[/dev/tty.usbserial]",list.toString());
	}

}
