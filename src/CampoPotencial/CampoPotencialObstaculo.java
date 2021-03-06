package CampoPotencial;

import actuadores.MotorOruga;
import sensores.Camara;
import sensores.SRF;

/**
 * Created by Truji on 09/05/2016.
 */
public class CampoPotencialObstaculo {

    private SRF[] sensores;

    private MotorOruga motor;

    private static final float MAX_DIST = 20;

    private static final int TIEMPO_BARRIDO = 5;

    private static final int TIEMPO_POR_BARRIDO = 5;

    private static final int NUMERO_GIROS_CAMARA = 450*8/250;

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

    private static final float[] campo_constante = new float[] {0, 0.8f}; //Campo constante hacia delante para que el robot avance

    private static int[] distancias = new int[5];

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
        
        campo_total = normalize(campo_total);
        
        campo_total[0] += campo_constante[0];
        campo_total[1] += campo_constante[1];
        
        campo_total = normalize(campo_total);
        
        return campo_total;
    }
    
    private static float[] normalize(float[] array) {
        float modulo = (float)Math.sqrt(array[0]*array[0] + array[1]*array[1]);
        float[] out = array;
        if(modulo != 0) {
            out[0] /= modulo;
            out[1] /= modulo;
        }
        
        return out;
    }

    private static float calcularMagnitudRepulsion(int distancia) {
        if(distancia > MAX_DIST) return 0;
        if(distancia <= 0) return 1;

        return (MAX_DIST - distancia) / MAX_DIST ;
    }

    public void tickMedidas() {

        for(int i = 0; i<sensores.length; i++) {
            distancias[i] = sensores[i].medir();
            if(i == 1 && distancias[i] > 5) {
                distancias[i]-=5;
            }
            System.out.println("SENSOR "+i+": "+distancias[i]);
        }

    }
    
    public void tickFrontal() {
        for(int i = 1; i<sensores.length-1; i++) {
            distancias[i] = sensores[i].medir();
            if(i == 1 && distancias[i] > 5) {
                distancias[i]-=5;
            }
            System.out.println("SENSOR "+i+": "+distancias[i]);
        }
        Camara.captureAndAnalize(true);
    }

    public void barrido() {
        int i = 0;

        System.out.println("#### START BARRIDO ####");
        while (!Camara.isFound() && i < NUMERO_GIROS_CAMARA) {
            try {
                Thread.sleep(500);
            } catch (Exception ignored){}
            
            Camara.captureAndAnalize(true);

            System.out.println("Giro "+i+": "+Camara.isFound());

            if (!Camara.isFound()) {
                motor.turnLeft(250);
                i++;
            }
        }
        System.out.println("#### FIN BARRIDO #### i: " +i);


//        for(int i = 0; i < NUMERO_GIROS_CAMARA ; i++){
//            Camara.captureAndAnalize();
//            try {
//                Thread.sleep(1000);
//            } catch(Exception ig) {}
//            if (Camara.isFound()) break;
//            else motor.turnLeft(250);
//        }
    }

    public static void main(String[] args) {
        int[][] tests = new int [][] {
                new int[] {100,50,13,78,150}
//                new int[] {9,0,0,0,0},
//                new int[] {0,0,9,0,0},
//                new int[] {0,0,0,0,9}
        };

        for (int[] test: tests) {
            float[] result = calcularPotencial(test);
            System.out.println("====================");
            System.out.println("dir: " + result[0] + ", vel: " + result[1] + "");
        }
    }

    public void run()
    {
        int muestreos = 0;

        int num_muestreos = TIEMPO_BARRIDO * TIEMPO_POR_BARRIDO;
        
        if(!Camara.isFound())
            barrido();

        boolean loop = true;
        
        while(loop){
            if (!Camara.goalReached()) {
                muestreos++;
                //System.out.println(muestreos);
                               
                tickFrontal();
                
                if(!Camara.isFound() && muestreos%num_muestreos == 0) barrido();
                
                boolean shouldTurn = distancias[1] < 18 || distancias[2] < 30 || distancias[3] < 18;

                if (shouldTurn) {
                    motor.stop();
                    tickMedidas();

                    float[] action = calcularPotencial(distancias);

                    System.out.println("Action: v-> " + action[1] + " giro -> " + action[0]);

                    long minTurnTime = 200;

                    long maxTurnTime = 500;

                    long turnTime = (long) (minTurnTime + maxTurnTime * Math.abs(action[0]));

                    if (action[0] < 0)
                        motor.turnLeft(turnTime);
                    else
                        motor.turnRight(turnTime);
                } else {
                    if (Camara.isFound()) {
                        int direccion_giro = Camara.getGoalPosition();
                        long turnTime = 100;
                        if (direccion_giro > 0) {
                            motor.turnRight(turnTime);
                        } else if (direccion_giro < 0) {
                            motor.turnLeft(turnTime);
                        } else {
                            motor.avanzar(1f);
                        }
                    } else {
                        motor.avanzar(1f);
                    }
                }
            } else {
                motor.spinClockwise();
                try{
                    Thread.sleep(5000);
                    motor.stop();
                    loop=false;
                } catch (Exception e) {}
            }
        }
    }
}

