import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class OpencvTest {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        double min = 0;
        List<Double> sumList = new ArrayList<>();

        // String path = args[0];
        String path = "laba.jpg";

        Mat orgMat = Imgcodecs.imread(path, 0);
        Imgproc.pyrDown(orgMat, orgMat);
        Mat newMat = new Mat();
        // Imgproc.adaptiveThreshold(orgMat, newMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 5);
        Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 3)); // Čia ryškinam linijas, galima žaisti su kernelio dydžiu Size()
        Imgproc.morphologyEx(orgMat, newMat, Imgproc.MORPH_GRADIENT, morphKernel);
        Imgproc.threshold(newMat, newMat, 0.0, 255.0, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        // Highgui.imwrite("result.jpg", newMat); 
        Mat newMatCopy = newMat;
        Mat resultMat = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();

        // Ieškom kuriuose column mažiausiai juodų pixelių
        int buff[] = new int[(int) (newMat.total() * newMat.channels())];
        for (int j = 0; j < newMat.cols(); j++) {
            double columnSum = 0;
            for (int i = 0; i < newMat.rows(); i++) {
                double value = newMat.get(i, j)[0];
                // System.out.println(String.valueOf(value));
                columnSum += value;
            }
            // System.out.println(columnSum);
            sumList.add(columnSum);
            if (min > columnSum || min == 0) min = columnSum;
        }

        // Columnus kuriuose mažiausiai juodų pixelių pašalinam (t.y. atskiriam 'raides') 
        int count = 0;
        Double secondLowest = getSecondLowest(sumList);
        for (int j = 0; j < newMat.cols(); j++) {
            double value = sumList.get(j);
           // if (secondLowest * 1.5 >= value) { // Didinant šitą parametrą (1.3) ieškos daugiau vietų kuriose skaidyti, mažinant - skaidys mažiau
                for (int k = 0; k < newMat.rows(); k++) {
                    newMat.put(k, j, 0);
                }
                count++;
            //}
        }
        // Highgui.imwrite("result.jpg", newMat);

        // morphKernel  = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 2));
        // Imgproc.morphologyEx(newMat, newMat, Imgproc.MORPH_CLOSE, morphKernel);
        Imgproc.findContours(newMat, contours, resultMat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            if (rect.height > 15 && rect.width > 8) { // Praleidžiam mažus boundingboxus, kurie tikrai negali būti raidės
                Imgproc.rectangle(orgMat, rect.tl(), rect.br(), new Scalar(0, 255, 0), 1);
                // čia galima išsaugoti boundingboxą rect į failą, atlikinėti kitus veiksmus etc.
            }
        }

        Imgcodecs.imwrite("result_" + path, orgMat);
    }


    private static Double getSecondLowest(List<Double> searchList) {
        Double min = 0.0, secondMin = 0.0;
        for (Double value : searchList) {
            if (min > value || min == 0) min = value;
        }
        for (Double value : searchList) {
            if ((min < value && secondMin > value) || secondMin == 0) secondMin = value;
        }
        return secondMin;
    }

}