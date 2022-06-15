import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class RetinalMatch {
    public static void main(String[] args) {
        System.out.println(Core.VERSION);
        Mat src = Imgcodecs.imread("img/IM000001_1.JPG");
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2BGR);
        Imgcodecs.imwrite("out.png");
    }
}