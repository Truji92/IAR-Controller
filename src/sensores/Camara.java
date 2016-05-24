package sensores;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.io.File;
import java.rmi.server.ExportException;

/**
 * Created by alejandro on 24/05/16.
 */
public class Camara {

    public static void main(String[] args) {
        Webcam webcam = Webcam.getDefault();

        System.out.println("asdasd");


//        webcam.open();
//        try {
//            ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
//        } catch (Exception ignored) {}
    }
}
