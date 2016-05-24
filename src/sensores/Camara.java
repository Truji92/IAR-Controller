package sensores;

public class Camara {

    public Camara()
    {

    }

    public static void main(String... args)
    {
        Webcam wc = Webcam.getDefault();
        if(wc!=null)
        {
            System.out.println(wc.getName());
        }
        wc.open();
        ImageIO.write(wc.getImage(), "PNG", new File("prueba.png"));

        Cerramos el puerto
        p.close();


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(0);
        Thread.sleep(1000);

        camera.open(0);
        if (!camera.isOpened()) {
            System.out.println("Error al abrir la camara");
        } else {
            camera.read(frame);
            Highgui.imwrite("nuevaFoto2.jpg", frame);
            camera.release();

        }
    }
}
