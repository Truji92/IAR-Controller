package main;

import giovynet.nativelink.SerialPort;
import sensores.SRF;
import serialPort.SerialPortController;

import java.util.List;


/**
 */
public class Main {

    public static void main(String[] args)throws Exception {
        // SerialPortController: es un objeto del tipo de puerto que vamos a utilizar
        SerialPort free = new SerialPort();

        // Nos permite obtener la lista de puertos disponibles
        List<String> portsList = free.getFreeSerialPort();
        System.out.println("Puertos Disponibles");

        // Para verifica los puertos creamos un for mediante el cual
        // ontendremos los puertos libres impresos en consola
        for (String String : portsList)
        {
            System.out.println(String );
        }

        // Ejemplo de uso la interfaz de envio recepcion
        SerialPortController serial = new SerialPortController();
        serial.send('a', 'b', new char[]{'1','2'});
//        while (true) {
//            // Si conectas COM3 a un hyperterminal, lo que escribas aparecera en la consola
//            System.out.println(serial.read());
//        }


        //test sensores
        SRF[] sensores = SRF.initializeSensors(serial);
        int[] medidas = new int[sensores.length];
        for (int i = 0; i < sensores.length; i++) {
            medidas[i] = sensores[i].medir(); //Acción bloqueante e.e
            System.out.println(medidas[i]);
        }
    }

}
