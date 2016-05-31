package sensores;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

public class Camara {

    // Carga de la libreria de OpenCV
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    private static VideoCapture camera;
    
    static {
        camera = new VideoCapture(0);
       
        camera.open(0);
    }
            
    /**
     * True-> guardar las capturas para debug en el sistema de archivos
     */
    private static final boolean __saveCaptures = true;
    private static boolean found = false;
    private static boolean goal_reached = false;
    private static double goal_position;

    /**
     * Distancia del centro a la que se considera que el objeto se encuentra de frente
     */
    private static final double CENTER_THRESHOLD = 150;
    private static final double CENTER = 320;

    /**
     * Tamaño del objeto (en píxeles) a partir del cual se considera que esta suficientemente cerca como para considerarse
     * que se ha alcanzado el objetivo.
     */
    private static final double GOAL_SIZE = 10000;

    public static void main(String... args) {
        Mat img = Highgui.imread("matlab/images/test2.jpg", Highgui.CV_LOAD_IMAGE_UNCHANGED);

        analize(img);
    }

    /**
     * Toma una captura y la analiza, actualizando el estado de la clase Camara.
     */
    public static void captureAndAnalize() {
        Mat img = capture();
        found = false;
        analize(img);
    }

    /**
     * Captura una imagen
     * @return La imagen
     */
    private static Mat capture() {
        Mat frame = new Mat();
        
        if (!camera.isOpened()) {
            System.out.println("Error al abrir la camara");
        } else {
            camera.read(frame);
            //long time = System.currentTimeMillis();
            //Highgui.imwrite("nuevaFoto2"+time+".jpg", frame);
            //camera.release();
        }
        return frame;
    }

    /**
     * Parámetros obtenidos mendiante los scripts de Matlab para reconocimiento del objeto
     */
    private static final double Rc = 198.6982;
    private static final double Gc = 178.8183;
    private static final double Bc = 4.8619;
    private static final double RADIO = 81.8675;
    private static final double MIN_AREA = 105;

    /**
     * Analiza una imagen y actualiza el estado de las variables de la clase para representar el analisis.
     * @param img
     */
    private static void analize(Mat img) {

        List<MatOfPoint> contornos = new ArrayList<>();
        Mat hierarchy = new Mat();

        // Separamos en componentes
        List<Mat> channels = new ArrayList<>(3);
        Core.split(img, channels);
        Mat R = new Mat(),
            G = new Mat(),
            B = new Mat();

        channels.get(0).convertTo(B, CvType.CV_16U);
        channels.get(1).convertTo(G, CvType.CV_16U);
        channels.get(2).convertTo(R, CvType.CV_16U);

        Core.absdiff(R, Scalar.all(Rc), R);
        Core.absdiff(G, Scalar.all(Gc), G);
        Core.absdiff(B, Scalar.all(Bc), B);

        Mat candidates = new Mat();

        Core.add(R.mul(R), G.mul(G), candidates);
        Core.add(B.mul(B), candidates, candidates);

        // ponemos a 1 los pixels dentro de la esfera de color y a 0 los demas
        for (int i = 0; i < candidates.size().height; i++) {
            for (int j = 0; j < candidates.size().width; j++) {
                double[] val = candidates.get(i, j);

                val[0] = Math.sqrt(val[0]);

                val[0] = val[0] < RADIO ? 1 : 0;

                candidates.put(i, j, val);
            }
        }

        candidates.convertTo(candidates, CvType.CV_8UC1);
        // Buscamos contornos
        Imgproc.findContours(candidates, contornos, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);

        double bigger_found = 0;

        for (int i = 0; i < contornos.size(); i++) {

            Mat imgPro = Mat.zeros(img.size(), img.type());
            Imgproc.drawContours(imgPro, contornos, i, Scalar.all(1), -1);

            double area = Core.sumElems(imgPro).val[0];

            if (area >= MIN_AREA && area > bigger_found) {
                bigger_found = area;
                found = true;
                Moments moments = Imgproc.moments(contornos.get(i));

                Point centroid = new Point();

                centroid.x = moments.get_m10() / moments.get_m00();
                centroid.y = moments.get_m01() / moments.get_m00();

                goal_position = centroid.x;

                // Si el tamaño es suficiente hemos encontrado el objetivo
                if (area > GOAL_SIZE)
                    goal_reached = true;

                // Debug
                if (__saveCaptures) {
                    Core.multiply(imgPro, Scalar.all(255), imgPro);
                    Core.circle(imgPro, centroid, 3, new Scalar(0,0,255), 5);
                    float time = System.currentTimeMillis();
                    Highgui.imwrite("in_"+time+".jpg", img);
                    Highgui.imwrite("out_"+time+".jpg", imgPro);
                }

            }
        }
    }

    /**
     * Devuelve True si la última imagen analizada contenia el objeto.
     *
     */
    public static boolean isFound() {
        return found;
    }

    /**
     * Devuelve la posición relativa del objeto si estaba contenido en la última imagen analizada
     * @return -1 => A la izquierda
     *          0 => De frente
     *          1 => A la derecha
     */
    public static int getGoalPosition() {
        if (!found) return 0;
        else {
            if (goal_position < CENTER - CENTER_THRESHOLD)
                return -1;
            else if(goal_position > CENTER + CENTER_THRESHOLD)
                return 1;
            else return 0;
        }
    }

    /**
     * Indica si se ha alcanzado el objetivo
     *
     */
    public static boolean goalReached() {
        return goal_reached;
    }
}
