package actuadores;

import serialPort.SerialPort;

/**
 * Controlador del Motor Oruga
 */
public class MotorOruga {

    private SerialPort serial;
    static private char direccion = 0xB1;
    static private char modo = 0x00;
    static private char velocidad = 0x01;
    static private char giro = 0x02;


    public MotorOruga() {
        serial = new SerialPort();
    }

    public void forward() {
        serial.send(direccion,modo,new char[]{0x01});
        serial.send(direccion,velocidad,new char[]{0x8F});
        serial.send(direccion,giro,new char[]{0x80});
    }
}
