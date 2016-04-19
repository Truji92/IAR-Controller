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
    
    /**
     * 
     * Calcula la nueva direcion a tomar para llegar al objetivo
     * 
     * @param goal Posicion del objetivo (x, y)
     * @param robot Posicion del robot (x, y)
     * @return vector direcion (magnitud, x, y)
     */
    public double[] go2Goal(final int[] goal, final int[] robot)
    {
        final double distancia = Math.sqrt( Math.pow((goal[0] - robot[0]), 2) 
                                    + Math.pow((goal[1] - robot[1]), 2) );
        
        final double magnitud = 5; //es una constante
        final double x = magnitud * (goal[0] - robot[0] ) / distancia,
                y = magnitud * (goal[0] - robot[0] ) / distancia;
        
        return new double[] { magnitud, x, y};
    }
    
     /**
     * 
     * Calcula la nueva direcion a tomar para evitar el obstaculo
     * 
     * @param obstacle Posicion del obstaculo (x, y)
     * @param robot Posicion del robot (x, y)
     * @return vector direcion (magnitud, x, y)
     */
    public double[] avoidObstacle(final double[] obstacle, final double[] robot)
    {
        final double distancia = Math.sqrt(Math.pow((obstacle[0] - robot[0]), 2) 
                                    + Math.pow((obstacle[1] - robot[1]), 2) );
        
        if( distancia > MAX_DIST )
            return new double[]{0, 0, 0}; // El obstaculo no me preocupa
        else
        {
            final double magnitud = (MAX_DIST - distancia) / distancia,
                    x = magnitud * (obstacle[0] - robot[0] ) / distancia,
                    y = magnitud * (obstacle[0] - robot[0] ) / distancia;
            
            return new double[] { magnitud, x, y};
        }
    }
    
    /**
     * 
     * Calcula la nueva direcion a tomar
     * 
     * @param newDir Nueva direcion a tomar (x, y)
     * @return vector direcion (magnitud, x, y)
     */
    public double[] moveAhead(final double[] newDir)
    {        
        final double angulo = Math.sqrt( Math.pow(newDir[0], 2) +Math.pow(newDir[1], 2) );
        final double magnitud = 5,
                x = magnitud * newDir[0] / angulo,
                y = magnitud * newDir[1] / angulo;
        
        return new double[] { magnitud, x, y};
    }
    /**
     * Metodo para que el robot ande
     * 
     * @TODO medir debe devolver el valor de cada sensor
     * @TODO pasarle los parametros al motor para que cambie
     */
    public void run()
    {
        while(true)
        {
            final int[] distancias = sensores.medir();
            double[] direccion = new double[] { 0, 0, 0};
            for(int i = 0; i < distancias.length; i++)
            {
                double[] posObstaculo = {(double) distancias[i]*sensor_directions[i][0], 
                                        (double) distancias[i]*sensor_directions[i][0]};
                final double[] evitar = avoidObstacle(posObstaculo , new double[] {0, 0});
                direccion = combinar(direccion, evitar);
            }
        }
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
        
        
        // PUNTO 1 Evitar_Obstaculos 
       
        
    }
    /**
     * 
     * @param direccion1 
     * @param direccion2 
     * @return suma vectorial
     */
    private double[] combinar(double[] direccion1, double[] direccion2) {
        return new double[]{ (direccion1[0]+direccion2[0])/2, 
                            (direccion1[1]+direccion2[1]),
                            (direccion1[2]+direccion2[2]) };
        
    // MIRAR QUE HACER CON LA MAGNITUD. HE PUESTO AL MEDIA
    }
}
