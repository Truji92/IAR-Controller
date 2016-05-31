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
    public float read(){
        char medida = serial.readByte(direccion, rumbo);
        
        char alto = serial.readByte(direccion, (char)0x02);
        char bajo = serial.readByte(direccion, (char)0x03);
        
        float res = (int) (alto*256 + bajo) & 0xFF;
        
        return ((int)medida  & 0xFF) * 360.0f/255.0f;
        //return res / 10f ;
    }
}
