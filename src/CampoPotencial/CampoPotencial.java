package CampoPotencial;

import sensores.SRF;

/**
 *
 */
public class CampoPotencial {

    private SRF[] sensores;

    private static final int MAX_DIST = 100;

    private static final float sqrt2 = (float) Math.sqrt(2);

    private static final float[][] sensor_directions = new float[][] {
            new float[] {-1, 0},        // izquierda
            new float[] {-sqrt2, sqrt2},  // 45º izquierda
            new float[] {0, 1},         // Frontal
            new float[] {sqrt2, sqrt2},   // 45º derecha
            new float[] {1, 0}          // derecha
    };

    public CampoPotencial(SRF[] sensores) {
        this.sensores = sensores;
    }

    public float[] tick() {
        int [] distancias = new int[5];
        for (int i = 0; i < sensores.length; i++) {
            int medida = sensores[i].medir();
            distancias[i] = medida;
        }

        return calcWheelsSpeed(distancias);
    }

    private static float[] calcWheelsSpeed(int[] distancias) {

        float[][] normalized_directions =  new float[5][];
        float[] normalized_dist = new float[5];

        for (int i = 0; i < distancias.length; i++) {
            normalized_dist[i] = (distancias[i] <= MAX_DIST ? (float)distancias[i] : 0) / MAX_DIST;

            float[] direction_unitary = sensor_directions[i];

            normalized_directions[i] = new float[] {
                    direction_unitary[0] * normalized_dist[i],
                    direction_unitary[1] * normalized_dist[i]
            };

        }

        return reduce_vectors(normalized_directions);
    }

    private static float[] reduce_vectors(float[][] vectors) {
        float [] result = new float[] {0,0};

        for (float[] vector : vectors) {
            result[0] -= vector[0];
            result[1] -= vector[1];
        }

        return result;
    }

    public static void main(String[] args) {
        int[][] tests = new int [][] {
                new int[] {0,0,0,0,0}
//                new int[] {9,0,0,0,0},
//                new int[] {0,0,9,0,0},
//                new int[] {0,0,0,0,9}
        };

        for (int[] test: tests) {
            float[] result = calcWheelsSpeed(test);
            System.out.println("====================");
            System.out.println("dir: " + result[0] + ", vel: " + result[1] + ")");
        }
    }
}
