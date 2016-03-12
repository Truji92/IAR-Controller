package main;

import giovynet.nativelink.SerialPort;
import java.util.List;


/**
 */
public class Main {

    public static void main(String[] args)throws Exception {
        // SerialPort: es un objeto del tipo de puerto que vamos a utilizar
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
    }

}
