package actuadores;

import excepciones.SensorException;
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
        serial.send(direccion,modo,new char[]{0x00});
        waitConfirmation();
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

    public void stop() {
        motorAction((byte) 0x80, (byte) 0x80);
    }

    public void motorAction(byte rueda1, byte rueda2) {
        serial.send(direccion,velocidad,new char[]{(char) rueda1});
        waitConfirmation();

        serial.send(direccion,giro,new char[]{(char) rueda2});
        waitConfirmation();
    }

    private void waitConfirmation() {
        try {
           if (serial.read() == 0x00) throw new SensorException;
        } catch (SensorException e) {
            System.out.println("Fallo al escribir/leer un sensor: " + e.getStackTrace());
        }
    }
}
