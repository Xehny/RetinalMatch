import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.List;

public class RetinalMatch {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: RetinalMatch <image1> <image2>");
            return;
        }

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            Mat src1 = Imgcodecs.imread(args[0]);
            Mat src2 = Imgcodecs.imread(args[1]);

            Mat out1 = convertImage(src1);
            Mat out2 = convertImage(src2);

            // we do some magic
            // https://theailearner.com/2019/08/13/comparing-histograms-using-opencv-python/
            //compareImages(out1, out2);

            Imgcodecs.imwrite("out.png", out1);
            Imgcodecs.imwrite("out2.png", out2);
        }
        catch(Exception ex) {
            System.err.println("Error: " + ex);
        }

    }

    public static void compareImages(Mat src1, Mat src2) {
        Mat img1HSV = new Mat();
        Mat img2HSV = new Mat();

        // Convert images to HSV
        Imgproc.cvtColor(src1, img1HSV, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src2, img2HSV, Imgproc.COLOR_BGR2GRAY);

        Mat histImg1 = new Mat();
        Mat histImg2 = new Mat();

        Mat hist = new MatOfInt(180, 256);
        MatOfInt histSize = new MatOfInt(0, 180, 0, 256);

        MatOfInt matOfInt = new MatOfInt(0, 1);

        List<Mat> l = new ArrayList<Mat>();
        l.add(hist);
        //Imgproc.calcHist(l, matOfInt, new Mat(), hist, histSize);

        boolean isSame = false;
        System.out.println(isSame ? 1 : 0);
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

        // Blur with noise
        Mat blurred = new Mat();
        Imgproc.medianBlur(cropped, blurred, 3);

        // Laplace
        Mat laplace = new Mat();
        Imgproc.Laplacian(blurred, laplace, CvType.CV_8U, 3);

        // denoise
        Photo.fastNlMeansDenoisingColored(laplace, laplace);

        // Contrast adjustments
        Mat contrast = new Mat();
        Core.multiply(laplace, new Scalar(3, 3, 3), contrast);

        //Greyscale
        Mat greyCropped = new Mat();
        Imgproc.cvtColor(contrast, greyCropped, Imgproc.COLOR_BGR2GRAY);

        // Binary color
        Mat binary = new Mat();
        Imgproc.threshold(greyCropped, binary, 40, 255, Imgproc.THRESH_BINARY);


        Mat dst = new Mat();
        Imgproc.Canny(binary, dst, 25, 95);

        return dst;
    }
}