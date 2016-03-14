package sensores;

import serialPort.SerialPortController;

/**
 * Controlador de la brújula CMPS09
 */
public class Brujula {
    private SerialPortController serial;
    static private char direccion = 0xC0;
    static private char rumbo = 0x01;

    public Brujula(SerialPortController serial) {
        this.serial = serial;
    }

    public void read(){
        rumbo = serial.read();
    }
}
