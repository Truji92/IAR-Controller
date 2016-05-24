package sensores;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import javax.imageio.ImageIO;
import java.io.File;

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
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(0);
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}

        camera.open(0);
        if (!camera.isOpened()) {
            System.out.println("Error al abrir la camara");
        } else {
            camera.read(frame);
            long time = System.currentTimeMillis();
            Highgui.imwrite("nuevaFoto2"+time+".jpg", frame);
            camera.release();

        }
    }
}
