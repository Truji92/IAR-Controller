package sensores;

import serialPort.SerialPortController;

/**
 * Controlador de la brújula CMPS09
 */
public class Brujula {
    private SerialPortController serial;
    static private final char direccion = 0xC1;
    static private final char rumbo = 0x01;

    public Brujula(SerialPortController serial) {
        this.serial = serial;
    }

    /**
     * Devuelve la medida en grados (0 - 360)
     *
     * @return Grados medidos por la brujula
     */
    public int read(){
        char medida = serial.readByte(direccion, rumbo);
        return (int)medida*360/255;
    }
}
