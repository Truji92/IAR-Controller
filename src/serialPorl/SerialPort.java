package serialPorl;

import giovynet.serial.Com;
import giovynet.serial.Parameters;

/**
 * Interfaz de comunicación con el puerto serie
 */
public class SerialPort {

    private Com conection;

    private final char comandoEscritura =  0x55;

    public SerialPort() {
        try {
            Parameters params = new Parameters();
            params.setPort("COM3");
            conection = new Com(params);
        } catch (Exception e) {
            System.out.println("Error al inicializar el puerto");
            e.printStackTrace();
        }
    }

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

}