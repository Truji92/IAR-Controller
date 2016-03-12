package actuadores;

import serialPort.SerialPortController;

/**
 * Controlador del Motor Oruga
 */
public class MotorOruga {

    private SerialPortController serial;
    static private char direccion = 0xB1;
    static private char modo = 0x00;
    static private char velocidad = 0x01;
    static private char giro = 0x02;


    public MotorOruga(SerialPortController serial) {
        this.serial = serial;
    }

    public void forward() {
        serial.send(direccion,modo,new char[]{0x02});
        if (serial.read() == 0x00) return;

        serial.send(direccion,velocidad,new char[]{0x8F});
        if (serial.read() == 0x00) return;

        serial.send(direccion,giro,new char[]{0x80});
        if (serial.read() == 0x00) return;
    }


}
