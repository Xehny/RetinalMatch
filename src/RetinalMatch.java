import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
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

    public static Mat convertImage(Mat src) {
        // Resize.
        Mat resized = new Mat();
        Size size = new Size(src.width() / 4, src.height() / 4);
        Imgproc.resize(src, resized, size);

        Mat grey = new Mat();
        Imgproc.cvtColor(resized, grey, Imgproc.COLOR_RGB2GRAY);
        Mat bw = new Mat();
        Imgproc.threshold(grey, bw, 16, 255, Imgproc.THRESH_BINARY);

        int left = bw.width() / 2;
        int right = bw.width() / 2;
        int top = bw.height() / 2;
        int bottom = bw.height() / 2;

        // Find furtherest left and right pixels which are not black
        for(int row = 0; row < bw.height(); row++) {
            for (int col = 0; col < bw.width(); col++) {
                double[] pixel = bw.get(row, col);

                // Check if pixel is not black.
                if (pixel[0] > 0) {
                    if (col < left) {
                        left = col;
                    }
                    if (col > right) {
                        right = col;
                    }
                    if (row < top) {
                        top = row;
                    }
                    if (row > bottom) {
                        bottom = row;
                    }
                }
            }
        }

        // Crop the image.
        Rect roi = new Rect(left, top, right - left + 1, bottom - top + 1);
        Mat cropped = new Mat(resized, roi);

        Mat dst = cropped;
        return dst;
    }
}