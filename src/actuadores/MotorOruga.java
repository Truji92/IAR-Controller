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


    /**
     * Inicializa por defecto con el modo 1 del controlador para las ruedas
     *
     * @param serial Conexión mediante el puerto serie con el robot
     */
    public MotorOruga(SerialPortController serial) {
        this.serial = serial;
        serial.send(direccion,modo,new char[]{0x00});
        waitConfirmation();
    }

    /**
     * Avanzar en línea recta
     */
    public void goForward() {
        motorAction((char) 0x00, (char) 0xFF);
    }

    /**
     * Retroceder en línea recta
     */
    public void goBackward() {
        motorAction((char) 0xFF, (char) 0x00);
    }

    /**
     * Girar en el sentido de las agujas del reloj
     */
    public void spinClockwise() {
        motorAction((char) 0x00, (char) 0x00);
    }

    /**
     * Girar en el sentido contrario a las agujas del reloj
     */
    public void spinCounterClockwise() {
        motorAction((char) 0xFF, (char) 0xFF);
    }

    /**
     * Para ambos motores
     */
    public void stop() {
        motorAction((char) 0x80, (char) 0x80);
    }

    /**
     * Conecta con los motores para modificar su velocidad de giro estableciendo la velocidad de cada motor,
     * siendo 0 y 255 velocidad máxima en sentido opuesto respectivamente
     *
     * @param rueda1 Establece la velocidad de la rueda izquierda
     * @param rueda2 Establece la velocidad de la rueda derecha
     */
    public void motorAction(char rueda1, char rueda2) {
        //serial.send(direccion,modo,new char[]{(char) 0x00, (char)128, (char) 128});
        serial.send(direccion, velocidad, new char[]{rueda1});
        //waitConfirmation();

        serial.send(direccion,giro,new char[]{rueda2});
        //waitConfirmation();
    }

    /**
     * Lee un byte desde el puerto serie para confirmar que se realizó correctamnte la acción previa
     */
    private void waitConfirmation() {
        try {
           if (serial.read() == 0x00) throw new SensorException();
        } catch (SensorException e) {
            System.out.println("Fallo al escribir/leer un sensor: " + e.getStackTrace());
        }
    }
}
