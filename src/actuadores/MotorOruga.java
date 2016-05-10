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

    private final int velocidadXMax = 64;
    private final int velocidadYMax = 64;


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
     *
     * @param vx
     * @param vy
     */
    public void setVelocity(float vx, float vy) {
        float motorL = 128 + velocidadXMax * vx;
        float motorR = 128 + velocidadXMax * vx;
        if(vy > 0) {
            motorR -= velocidadYMax * vy * (vx/Math.abs(vx));
        } else if (vy < 0) {
            motorL -= velocidadYMax * vy * (vx/Math.abs(vx));
        }

        motorL = 255 - Math.abs(motorL);
        motorAction((char) motorL, (char) motorR);
    }

    public void setVelocity2(float avance, float giro) {
        float diffVel = 255 * giro;
        float motorL = 128 + diffVel/2;
        float motorR = 128 - diffVel/2;

        Velocity v = increaseLinealVelocity(new Velocity(motorL, motorR), avance);
        motorAction((char) motorL, (char) motorR);
    }

    private Velocity increaseLinealVelocity(Velocity v, float dv) {
        v.vx += dv;
        v.vy += dv;

        float overflow;
        if (v.vx > 255) {
            overflow = v.vx - 255;
            v.vx = 255;
            v.vy -= overflow;
        }
        if (v.vx < 0) {
            overflow = Math.abs(v.vx);
            v.vx = 0;
            v.vy += overflow;
        }
        if (v.vy > 255) {
            overflow = v.vy - 255;
            v.vy = 255;
            v.vx -= overflow;
        }
        if (v.vy < 0) {
            overflow = Math.abs(v.vy);
            v.vy = 0;
            v.vx += overflow;
        }
        return v;
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
        waitConfirmation();

        serial.send(direccion,giro,new char[]{rueda2});
        waitConfirmation();
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

class Velocity {
    public float vx;
    public float vy;

    public Velocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    };
}
