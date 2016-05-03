/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoring.suhu;

/**
 *
 * @author je
 */

import gnu.io.*;
import java.awt.HeadlessException;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.text.NumberFormat;

public class KomunikasiSerial implements SerialPortEventListener {
    GUI window = null;
    private Enumeration ports = null;
    private final HashMap portMap = new HashMap();
    private CommPortIdentifier portIdentifier = null;
    private SerialPort serialPort = null;
    private InputStream serialInput = null;
    private boolean serialConnected = false;
    final static int TIMEOUT = 2000;
    String inputDataSerial = "";
    String statusSerialPort = "";
    int dataADC = 0;
    float suhu = 0;
    double suhuRuangan;

    public KomunikasiSerial(GUI window) {
        this.window = window;

    }

    /**
     * Cek PORT yang tersedia
     */
    public void cekSerialPort() {
        window.listPortComboBox.removeAllItems();
        ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {                
                window.listPortComboBox.addItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }

    final public boolean getConnected() {
        return serialConnected;
    }

    public void setConnected(boolean serialConnected) {
        this.serialConnected = serialConnected;
    }

    public void connect() {
        String selectedPort = (String) window.listPortComboBox.getSelectedItem();
        portIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
        CommPort commPort = null;
        try {
            commPort = portIdentifier.open("TigerControlPanel", TIMEOUT);
            serialPort = (SerialPort) commPort;
            setConnected(true);
            statusSerialPort = selectedPort + " opened successfully.";
            JOptionPane.showMessageDialog(null, statusSerialPort);
            window.statusLabel.setText("Serial Port Connected");
        } catch (PortInUseException e) {
            statusSerialPort = selectedPort + " is in use. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusSerialPort);
        } catch (HeadlessException e) {
            statusSerialPort = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusSerialPort);
        }
    }

    public void disconnect() {
        try {
            serialPort.removeEventListener();
            serialPort.close();
            serialInput.close();
            setConnected(false);
            window.serialInputTextField.setText("");
            window.suhuLabel.setText("Temperature = 0.00 Celcius");
            statusSerialPort = "PORT closed successfully";
            JOptionPane.showMessageDialog(null, statusSerialPort);
            window.statusLabel.setText("Serial Port Disconnect");
            inputDataSerial = "";
        } catch (IOException | HeadlessException e) {
            statusSerialPort = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusSerialPort);
        }
    }

    public boolean initIOStream() {
        boolean successful = false;
        try {
            serialInput = serialPort.getInputStream();
            successful = true;
            return successful;
        } catch (IOException e) {
            statusSerialPort = "I/O Streams failed to open. (" + e.toString() + ")";
            JOptionPane.showMessageDialog(null, statusSerialPort);
            return successful;
        }
    }

    public void initListener() {
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public double getSuhu() {
        return suhuRuangan;
    }

    public void setSuhu(double suhu) {
        suhuRuangan = suhu;
    }

    @Override
    public void serialEvent(SerialPortEvent evt) {
        char dataSerial = 0; // Untuk menampung input dari serial port 
        int dataDigital; // hasil parsing data integer

        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                dataSerial = (char) serialInput.read();
                if (dataSerial == '\n') {
                    System.out.println(inputDataSerial);
                    window.serialInputTextField.setText(inputDataSerial);
                    dataDigital = Integer.parseInt(inputDataSerial);
                    suhuRuangan = (double) dataDigital * 5000.0 / 10240.0;
                    NumberFormat n = NumberFormat.getInstance();
                    n.setMaximumFractionDigits(2);
                    window.suhuLabel.setText("Temperature = " + n.format(suhuRuangan) + " Celcius");
                    inputDataSerial = "";
                } else {
                    inputDataSerial += String.valueOf(dataSerial);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
        }
    } 
}
