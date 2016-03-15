package sensores;

import serialPort.SerialPortController;

/**
 * Controlador de los sensores de distancia SRF02
 */
public class SRF {

    /**
     * Direcciones de los sensores
     * @TODO No se si son las correctas
     */
    public static char[] SENSORS = new char[]{0xE0,0xE2,0xE4,0xE6,0xE8};

    /**
     * Devuelve un Array con los 5 sensores inicializados
     *
     * @param serial Conexión serial para comunicarse con los sensores
     * @return Array con los cinco sensores
     */
    public static SRF[] initializeSensors(SerialPortController serial) {
        SRF[] sensores = new SRF[SRF.SENSORS.length];
        for (int i = 0; i < SRF.SENSORS.length; i++)
            sensores[i] = new SRF(serial, SENSORS[i]);

        return sensores;
    }

    /**
     * Conexión con el puerto Serial
     */
    private SerialPortController serialPort;

    /**
     * Dirección del sensor
     */
    private char direccion;

    /**
     * Comando para inicial una medida en cm
     */
    private char medirEncm = 0x51;

    /**
     *
     * @param serialPort Conexión con el puerto serie
     * @param direccion Dirección del sensor al que conectar
     */
    public SRF(SerialPortController serialPort, char direccion) {
        this.serialPort = serialPort;
        this.direccion = direccion;
    }

    /**
     * CUIDADO: LLamada bloqueante a nivel de hilo.
     *
     * @return Medida en cm
     */
    public int medir() {
        serialPort.send(direccion, (char)0x00, new char[]{medirEncm});

        try{
            Thread.sleep(750);
        } catch (InterruptedException e) {
            System.out.println("Error esperando a la medida");
        }

        char bitAlto = serialPort.readByte((char) (direccion + 1), (char) 0x02);
        char bitBajo = serialPort.readByte((char) (direccion + 1), (char) 0x03);

        return (bitAlto << 8) + bitBajo;
    }
}
