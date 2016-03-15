package serialPort;

import giovynet.serial.Baud;
import giovynet.serial.Com;
import giovynet.serial.Parameters;

/**
 * Interfaz de comunicación con el puerto serie
 */
public class SerialPortController {

    /**
     * Conexion con el puerto serie
     */
    private Com conection;

    private final char comandoEscritura =  0x55;

    /**
     * Inicializa por defecto a COM3
     */
    public SerialPortController() {
        try {
            Parameters params = new Parameters();
            params.setPort("COM3");
            params.setBaudRate(Baud._19200);
            params.setStopBits("2");
            conection = new Com(params);
        } catch (Exception e) {
            System.out.println("Error al inicializar el puerto");
            e.printStackTrace();
        }
    }

    /**
     * Envio de información a los dispositivos
     *
     * @param direccion Dirección del dispositivo al que enviar
     * @param registro Número de registro del dispositivo
     * @param comandos Comandos a enviar
     */
    public void send(char direccion, char registro, char[] comandos) {
        try {
            char[] msg = new char[comandos.length+4];
            msg[0] = comandoEscritura;
            msg[1] = direccion;
            msg[2] = registro;
            msg[3] = (char)comandos.length;

            System.arraycopy(comandos, 0, msg, 4, comandos.length);

            conection.sendArrayChar(msg);
        } catch (Exception e) {
            System.out.println("Error al enviar al puerto serie");
            e.printStackTrace();
        }
    }

    /**
     * Solicita al bus una lectura, los datos leidos deben obtenerse con el metodo read()
     *
     * @param direccion Dirección del dispositivo
     * @param registro Registro de inicio de la lectura
     * @param cantidadALeer Número de registros a leer
     */
    public void send(char direccion, char registro, int cantidadALeer) {
        try {
            conection.sendArrayChar(new char[]{comandoEscritura, direccion, registro, (char)cantidadALeer});
        } catch (Exception e) {
            System.out.println("Error al enviar al puerto serie");
        }
    }

    /**
     * Lee el contenido del bus
     *
     * @return Byte leido del bus
     */
    public char read() {
        char res = ' ';
        try {
            res = conection.receiveSingleChar();
        } catch (Exception e) {
            System.out.println("Error al revivir desde el puerto serie");
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Realiza una petición de lectura de un registro y devuelve el valor leido
     *
     * @param direccion Dirección del dispositivo
     * @param registro Número del registro a leer
     * @return Valor del registro
     */
    public char readByte(char direccion, char registro) {
        send(direccion, registro, 1);
        return read();
    }

}