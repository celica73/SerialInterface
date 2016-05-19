package com.scott.serial.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.scott.serial.control.SerialController;

public class SerialGUI extends JFrame {

	private SerialController serialController;

	private static final long serialVersionUID = 1L;
	private final JTabbedPane tabPane = new JTabbedPane();
	public static final String PANE1 = "Setup Port";
	public static final String PANE2 = "View Port Comms";
	public static final String PANE3 = "View Log Files";

	private final SetupPanel setup = new SetupPanel();
	private PortComms comms = new PortComms();
	private WatcherThread listener;

	public SerialGUI() {
		super("Serial Port Monitor");

		serialController = new SerialController();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(tabPane);
		tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		tabPane.add(PANE1, setup);
		tabPane.add(PANE2, comms);
		// tabPane.add(PANE3, logs);
		setSize(new Dimension(500, 400));
		setLocationRelativeTo(null);
		setVisible(true);

	}

	private class SetupPanel extends JPanel implements ActionListener {

		private static final long serialVersionUID = 1L;
		private JLabel portTitle = new JLabel("Choose Port");
		private JLabel parameters = new JLabel("Setup Port Parameters");
		private JLabel baud = new JLabel("Baud");
		private JLabel bits = new JLabel("Bits");
		private JLabel flow = new JLabel("Flow");
		private JLabel parity = new JLabel("Parity");
		private JButton connect = new JButton("Connect");
		private JButton refresh = new JButton("Refresh List");
		private JComboBox<String> portList = new JComboBox<String>();
		private JComboBox<String> baudList = new JComboBox<String>();
		private JComboBox<String> bitsList = new JComboBox<String>();
		private JComboBox<String> flowList = new JComboBox<String>();
		private JComboBox<String> parityList = new JComboBox<String>();
		private JTextArea messages = new JTextArea();

		public SetupPanel() {
			super(new GridLayout(7, 1));
			this.add(parameters);
			JPanel parameterList = new JPanel(new GridLayout(2, 4));
			parameterList.add(baud);
			parameterList.add(bits);
			parameterList.add(flow);
			parameterList.add(parity);
			parameterList.add(baudList);
			parameterList.add(bitsList);
			parameterList.add(flowList);
			parameterList.add(parityList);
			this.add(parameterList);
			this.add(portTitle);
			JPanel connection = new JPanel(new FlowLayout());
			connection.add(portList);
			connection.add(refresh);
			this.add(connection);
			this.add(connect);
			this.add(messages);
			updatePorts();
			updateSettings();
			refresh.addActionListener(this);
			connect.setEnabled(false);
			if (serialController != null && serialController.getStatus()) {
				connect.setEnabled(true);
				connect.setText("Connect");
			} else if (serialController != null) {
				connect.setEnabled(true);
				connect.setText("Disconnect");
			}
			connect.addActionListener(this);

		}

		public void updatePorts() {
			if (serialController != null) {
				for (String s : serialController.getPorts()) {
					portList.addItem(s);
				}
			}
		}

		public void updateSettings() {
			if (serialController != null) {
				for (String s : serialController.getBaudList()) {
					baudList.addItem(s);
				}
				baudList.setSelectedItem("115200");
				for (String s : serialController.getBitsList()) {
					bitsList.addItem(s);
				}
				bitsList.setSelectedItem("8");
				for (String s : serialController.getFlowList()) {
					flowList.addItem(s);
				}
				flowList.setSelectedItem("1");
				for (String s : serialController.getParityList()) {
					parityList.addItem(s);
				}
				flowList.setSelectedItem("0");
			}
		}

		public void initializeConnection() {
			String[] parameters = new String[5];
			parameters[0] = baudList.getSelectedItem().toString();
			parameters[1] = bitsList.getSelectedItem().toString();
			parameters[2] = flowList.getSelectedItem().toString();
			parameters[3] = parityList.getSelectedItem().toString();
			parameters[4] = portList.getSelectedItem().toString();
			serialController.initialize(parameters);
			listener = new WatcherThread();
			new Thread(listener).start();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == refresh) {
				updatePorts();
				updateSettings();
				connect.setEnabled(true);
			}
			if (e.getSource() == connect) {
				try {
					if (connect.getText().equals("Connect")) {
						initializeConnection();
						connect.setText("Disconnect");
						messages.setText("Connection Status: " + serialController.getStatus());
					} else {
						serialController.closeConnection();
						connect.setText("Connect");
						messages.setText("Connection Status: " + serialController.getStatus());
					}
				} catch (IllegalArgumentException error) {
					messages.setText(error.getMessage());
				}
			}
		}
	}

	private class PortComms extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		private JButton logger = new JButton("Start Log");
		private JTextField sendData = new JTextField();
		private JTextArea sentData = new JTextArea();
		private JTextArea receiveData = new JTextArea();
		private JScrollPane recieved = new JScrollPane(receiveData);
		private String logFile;

		public PortComms() {
			super(new BorderLayout());
			Border sendOutline = BorderFactory.createEmptyBorder(0, 0, 0, 0);
			Border sendTitle = new TitledBorder("Send");
			Border receiveOutline = BorderFactory.createEmptyBorder(0, 0, 0, 0);
			Border receiveTitle = new TitledBorder("Receive");
			JPanel sendPanel = new JPanel(new BorderLayout());
			sendPanel.setBorder(BorderFactory.createCompoundBorder(sendOutline, sendTitle));
			sendPanel.setPreferredSize(new Dimension(472, 100));
			sendPanel.add(sendData, BorderLayout.NORTH);
			sendPanel.add(sentData, BorderLayout.CENTER);
			add(sendPanel, BorderLayout.NORTH);
			recieved.setBorder(BorderFactory.createCompoundBorder(receiveOutline, receiveTitle));
			recieved.setPreferredSize(new Dimension(472, 100));
			add(recieved, BorderLayout.CENTER);
			JPanel loggerPanel = new JPanel();
			loggerPanel.add(logger);
			add(loggerPanel, BorderLayout.SOUTH);
			sendData.addActionListener(this);
			logger.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == logger) {
				if (logger.getText().equals("Start Log")) {
					logFile = getFileName();
					logger.setText("Stop Log");
					serialController.startLogging(logFile);
				} else {
					logger.setText("Start Log");
					serialController.stopLogging();
				}
			}
			if (e.getSource() == sendData) {
				String input = sendData.getText();
				input += "\n";
				if(input.equals("\n"))
					receiveData.append(input);
				serialController.writeData(input);
				sentData.setText(input + sentData.getText());
				sendData.setText("");
			}
		}

		/**
		 * Gets the filename of a movie file for the system.
		 * 
		 * @author Scott Johnson
		 */
		private String getFileName() {
			JFileChooser fc = new JFileChooser("./");
			fc.showSaveDialog(this);
			File logFile = fc.getSelectedFile();
			return logFile.getAbsolutePath();
		}
	}

	public static void main(String[] args) {
		new SerialGUI();
	}

	public class WatcherThread extends Thread {
		private String newText;

		public WatcherThread() {
		}

		@Override
		public void run() {
			while (serialController.getStatus()) {
				newText = serialController.readData();
				if (newText != null) {
					comms.receiveData.append(newText);
					JScrollBar vertical = comms.recieved.getVerticalScrollBar();
					vertical.setValue(vertical.getMaximum());
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
