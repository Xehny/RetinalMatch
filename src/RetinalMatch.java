import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class RetinalMatch {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: RetinalMatch <image1> <image2>");
            return;
        }

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            Mat src1 = Imgcodecs.imread(args[0]);
            //Mat src2 = Imgcodecs.imread(args[1]);

            Mat out1 = convertImage(src1);
            //Mat out2 = convertImage(src2);

            Imgcodecs.imwrite("out.png", out1);
        }
        catch(Exception ex) {
            System.err.println("Error: " + ex);
        }

    }
}