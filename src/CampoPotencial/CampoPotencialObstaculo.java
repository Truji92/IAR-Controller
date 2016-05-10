package CampoPotencial;

import actuadores.MotorOruga;
import sensores.SRF;

/**
 * Created by Truji on 09/05/2016.
 */
public class CampoPotencialObstaculo {

    private SRF[] sensores;

    private MotorOruga motor;

    private static final float MAX_DIST = 40;

    private static final float sqrt2 = (float) Math.sqrt(2)/2;

    private static final float[][] sensor_directions = new float[][] {
            new float[] {1, 0},        // izquierda
            new float[] {sqrt2, -sqrt2},  // 45º izquierda
            new float[] {0, -1},         // Frontal
            new float[] {-sqrt2, sqrt2},   // 45º derecha
            new float[] {-1, 0}          // derecha
    };

    //Peso de cada sensor en el potencial final [0-1]
    private static final float[] pesos = new float[] {1,1,1,1,1};

    private static final float[] campo_constante = new float[] {0, 0.6f}; //Campo constante hacia delante para que el robot avance

    public CampoPotencialObstaculo(SRF[] sensores, MotorOruga motor) {
        this.sensores = sensores;
        this.motor = motor;
    }

    private static float[] calcularPotencial(int[] sensors) {

        float[][] vectores_potenciales = new float[sensors.length][];

        float[] campo_total = new float[] {0,0};

        for (int i = 0; i < sensors.length; i++) {
            float mag = calcularMagnitudRepulsion(sensors[i]);
            vectores_potenciales[i] = new float[] {
                    sensor_directions[i][0]*mag,
                    sensor_directions[i][1]*mag
            };

            campo_total[0] += vectores_potenciales[i][0] * pesos[i];
            campo_total[1] += vectores_potenciales[i][1] * pesos[i];
        }

        campo_total[0] += campo_constante[0];
        campo_total[1] += campo_constante[1];

        return campo_total;
    }

    private static float calcularMagnitudRepulsion(int distancia) {
        if(distancia > MAX_DIST) return 0;
        if(distancia <= 0) return 1;

        return (MAX_DIST - distancia) / MAX_DIST ;
    }

    public static void main(String[] args) {
        int[][] tests = new int [][] {
                new int[] {174,37,17,123,218}
//                new int[] {9,0,0,0,0},
//                new int[] {0,0,9,0,0},
//                new int[] {0,0,0,0,9}
        };

        for (int[] test: tests) {
            float[] result = calcularPotencial(test);
            System.out.println("====================");
            System.out.println("dir: " + result[0] + ", vel: " + result[1] + ")");
        }
    }
}