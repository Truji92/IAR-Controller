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

    public void goForward() {
        motorAction((byte) 0xFF, (byte) 0xFF);
    }

    public void goBackward() {
        motorAction((byte) 0x00, (byte) 0x00);
    }

    public void spinClockwise() {
        motorAction((byte) 0xFF, (byte) 0x00);
    }

    public void spinCounterClockwise() {
        motorAction((byte) 0x00, (byte) 0xFF);
    }

    public void motorAction(byte rueda1, byte rueda2) {
         serial.send(direccion,modo,new char[]{0x00});
        if (serial.read() == 0x00) return;

        serial.send(direccion,velocidad,new char[]{(char) rueda1});
        if (serial.read() == 0x00) return;

        serial.send(direccion,giro,new char[]{(char) rueda2});
        if (serial.read() == 0x00) return;
    }




}
