package main;

import CampoPotencial.*;
import actuadores.MotorOruga;
import giovynet.nativelink.SerialPort;
import sensores.Brujula;
import sensores.SRF;
import serialPort.SerialPortController;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;


/**
 */
public class Main {

    public static void main(String[] args)throws Exception {


        // Ejemplo de uso la interfaz de envio recepcion
        SerialPortController serial = new SerialPortController();
//        serial.send('a', 'b', new char[]{'1','2'});
////        while (true) {
////            // Si conectas COM3 a un hyperterminal, lo que escribas aparecera en la consola
////            System.out.println(serial.read());
////        }
//
//
//        //test sensores
        SRF[] sensores = SRF.initializeSensors(serial);
        short[] medidas = new short[sensores.length];
//        for (int i = 0; i < sensores.length; i++) {
//            medidas[i] = sensores[i].medir(); //Acción bloqueante e.e
//            System.out.println(medidas[i]);
//        }

        /*while(true){
            int mdid = sensores[3].medir();
            System.out.println(mdid);
        }*/
        
        //serial.send((char)0xC1, (char) 0x22, new char[]{0x20});
        //serial.send((char)0xC1, (char) 0x22, new char[]{0x2A});
        //serial.send((char)0xC1, (char) 0x22, new char[]{0x60});
        
        //Brujula brujula = new Brujula(serial);
        MotorOruga ruedas = new MotorOruga(serial);
        
       
        CampoPotencialObstaculo campo = new CampoPotencialObstaculo(sensores, ruedas);
        //campo.run();
        ruedas.stop();
       /*Scanner teclado = new Scanner(System.in);
        boolean seguir = true;


        while (seguir) {

            System.out.println("GIRANDO SENTIDO HORARIO");
            ruedas.spinClockwise();
            Thread.sleep(3000);

            System.out.println("GIRANDO SENTIDO ANTIHORARIO");
            ruedas.spinCounterClockwise();
            Thread.sleep(3000);

            System.out.println("AVANZANDO");
            ruedas.goForward();
            Thread.sleep(3000);

            System.out.println("MARCHA ATRAS");
            ruedas.goBackward();
            Thread.sleep(3000);

            ruedas.stop();

            System.out.println("INTRODUCE ALGO PARA REINICIAR EL TEST (o introduce stop para detener)");

            String next = teclado.next();
            System.out.println(next);
            if (next.compareTo("stop") == 0) seguir = false;
            teclado.reset();
        }*/
    }

}
