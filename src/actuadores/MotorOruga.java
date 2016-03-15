package actuadores;

import excepciones.SensorException;
import serialPort.SerialPortController;

/**
 * Controlador del Motor Oruga
 */
public class MotorOruga {

    private SerialPortController serial;
    static private char direccion = 0xB0;
    static private char modo = 0x00;
    static private char velocidad = 0x01;
    static private char giro = 0x02;


    public MotorOruga(SerialPortController serial) {
        this.serial = serial;
        serial.send(direccion,modo,new char[]{0x00});
        waitConfirmation();
    }

    public void goForward() {
        motorAction((char) 0xFF, (char) 0xFF);
    }

    public void goBackward() {
        motorAction((char) 0x00, (char) 0x00);
    }

    public void spinClockwise() {
        motorAction((char) 0xFF, (char) 0x00);
    }

    public void spinCounterClockwise() {
        motorAction((char) 0x00, (char) 0xFF);
    }

    public void stop() {
        motorAction((char) 0x80, (char) 0x80);
    }

    public void motorAction(char rueda1, char rueda2) {
        //serial.send(direccion,modo,new char[]{(char) 0x00, (char)128, (char) 128});
        serial.send(direccion, velocidad, new char[]{rueda1});
        //waitConfirmation();

        serial.send(direccion,giro,new char[]{rueda2});
        //waitConfirmation();
    }

    private void waitConfirmation() {
        try {
           if (serial.read() == 0x00) throw new SensorException();
        } catch (SensorException e) {
            System.out.println("Fallo al escribir/leer un sensor: " + e.getStackTrace());
        }
    }
}
