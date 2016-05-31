package sensores;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Camara {

    public Camara()
    {

    }

    public static void main(String... args)
    {
//        Webcam wc = Webcam.getDefault();
//        if(wc!=null)
//        {
//            System.out.println(wc.getName());
//        }
//        wc.open();
//        ImageIO.write(wc.getImage(), "PNG", new File("prueba.png"));

        //Cerramos el puerto
        //p.close();


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        analizeImg();


//        Mat frame = new Mat();
//        VideoCapture camera = new VideoCapture(0);
//        try {
//            Thread.sleep(1000);
//        } catch (Exception ignored) {}
//
//        camera.open(0);
//        if (!camera.isOpened()) {
//            System.out.println("Error al abrir la camara");
//        } else {
//            camera.read(frame);
//            long time = System.currentTimeMillis();
//            Highgui.imwrite("nuevaFoto2"+time+".jpg", frame);
//            camera.release();
//
//        }
    }

    private static final double Rc = 198.6982;
    private static final double Gc = 178.8183;
    private static final double Bc = 4.8619;
    private static final double RADIO = 81.8675;
    private static final double MIN_AREA = 105;

    public static void analizeImg() {

        List<MatOfPoint> contornos = new ArrayList<>();
        Mat hierarchy = new Mat();

        Mat img = Highgui.imread("matlab/images/test5.jpg", Highgui.CV_LOAD_IMAGE_UNCHANGED);

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

        for (int i = 0; i < contornos.size(); i++) {

            Mat imgPro = Mat.zeros(img.size(), img.type());
            Imgproc.drawContours(imgPro, contornos, i, Scalar.all(1), -1);

            if (Core.sumElems(imgPro).val[0] >= MIN_AREA) {
                Moments moments = Imgproc.moments(contornos.get(i));

                Point centroid = new Point();

                centroid.x = moments.get_m10() / moments.get_m00();
                centroid.y = moments.get_m01() / moments.get_m00();

                ///IMG
                Core.multiply(imgPro, Scalar.all(255), imgPro);
                Core.circle(imgPro, centroid, 3, new Scalar(0,0,255), 5);
                Highgui.imwrite("out"+i+".jpg", imgPro);

            }
        }
    }
}
