import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.opencv.core.CvType.CV_8UC1;

//import net.sourceforge.tess4j.*;

public class Main {

    public static int HEIGHT = 480;
    public static int WIDTH = 640;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void printMenu() {
        System.out.println("+----------------------+");
        System.out.println("+ Pasirinkite funkcija +");
        System.out.println("+----------------------+");
        System.out.println("| 1. Iškirpti žodžius");
        System.out.println("| 2. Iškirpti raides");
        System.out.println("+----------------------+");
        System.out.println("| 0. Baigti darbą");
        System.out.println("+----------------------+");
    }

    public static void main(String[] args) throws IOException {
        String pathName;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Pasirinkite funkciją");
        int chosen;

        tryExtractLetters("laba.jpg");
        tryExtractLetters("keturi.jpg");
        tryExtractLetters("trecias.jpg");
        tryExtractLetters("atskirtas.jpg");
        /*while (true) {
            printMenu();
            try {
                chosen = Integer.valueOf(keyboard.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ivesta ne skaicius!");
                chosen = -1;
            }

            switch (chosen) {
                case 0:
                    System.exit(1);
                    break;
                case 1:
                    System.out.println("Failo pavadinimas: ");
                    pathName = keyboard.nextLine();
                    if (new File(pathName).exists()){
                        extractWords(pathName);
                    }
                    break;
                case 2:
                    System.out.println("Failo pavadinimas: ");
                    pathName = keyboard.nextLine();
                    if (new File(pathName).exists()){
                        extractLetters(pathName);
                    }
                    break;
                default:
                    System.out.println("Blogas pasirinkimas");
            }
        }*/


        /*extractWords("seg.jpg");
        extractWords("pvz.jpg");
        extractLetters("laba.jpg");
        extractLetters("antras.jpg");
        extractLetters("trecias.jpg");
        extractLetters("keturi.jpg");
        extractLetters("atskirtas.jpg");
        */


    }

    private static void tryExtractLetters(String pathName) {
        Mat image = Imgcodecs.imread(pathName, Imgproc.COLOR_BGR2GRAY);
        Mat imageHSV = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageA = new Mat(image.size(), CvType.CV_32F);
        Mat kernel = Mat.ones(1, 2, CV_8UC1);

        Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imageHSV, imageHSV, 125, 255, Imgproc.THRESH_OTSU);

        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(1, 1), 0);

        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 33, 5);

        Mat blackWhiteImage = image.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageA.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> findRects = new ArrayList<>();
        List<Point> taskai = new ArrayList<>();

        for (int i = 0; i < contours.size() - 1; i++) {
            //System.out.println("AREA: " + Imgproc.contourArea(contours.get(i)));

            if (Imgproc.contourArea(contours.get(i)) > 30) {
                Rect rect = Imgproc.boundingRect(contours.get(i));

                //.out.println("aukstis: " + rect.height);
                if (rect.height > 19) {
                    List<Rect> temp = new ArrayList<>(findRects);
                    System.out.println("TL: " + rect.tl().toString());
                    System.out.println("BR: " + rect.br().toString());
                    System.out.println("X: " + rect.width);
                    System.out.println("Y: " + rect.height);
                    taskai.add(rect.tl());
                    taskai.add(rect.br());
                    if (!(temp.stream().anyMatch(r -> r.contains(new Point(rect.x, rect.y))))) {
                        findRects.add(rect);

                    }
                }
            }
        }

        for (Point aTaskai : taskai) {
            System.out.println("Eina per " + aTaskai);
            for (int k = 0; k < imageBlurr.rows(); k++) {
                imageBlurr.put(k, (int) aTaskai.x, 0);
                imageBlurr.put(k, (int) aTaskai.x + 1, 0);
            }
        }

        Imgcodecs.imwrite("testasdasdasd" + pathName + ".jpg", imageBlurr);


        System.out.println(pathName + " Segmentu: " + findRects.size());
        for (int i = 0; i < findRects.size(); ++i) {
            Rect rect = findRects.get(i);
            Mat image_output = blackWhiteImage.submat(rect);
            Imgcodecs.imwrite("letters/" + pathName + "_" + i + ".jpg", image_output);

            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 2);

        }
        Imgcodecs.imwrite("test" + pathName + ".jpg", image);
    }

    private static void extractWords(String pathName) {
        Mat image = Imgcodecs.imread(pathName, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.resize(image, image, new Size(WIDTH, HEIGHT));
        Mat imageHSV = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageA = new Mat(image.size(), CvType.CV_32F);
        Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(105, 17), 9);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 127, 4);

        Mat blackWhiteImage = image.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(imageA, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(imageBlurr, contours, 1, new Scalar(0, 0, 255));

        List<Rect> findRects = new ArrayList<>();

        for (int i = 0; i >= 0; ) {
            double[] contourInfo = hierarchy.get(0, i);

            //**[Next, Previous, First_Child, Parent]**
            if (Imgproc.contourArea(contours.get(i)) > 30) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                //.out.println("aukstis: " + rect.height);
                if (rect.height > 19) {
                    List<Rect> temp = new ArrayList<>(findRects);

                    if ((contourInfo[2] == -1)) {
                        findRects.add(rect);

                    }
                }

            }
            i = (int) contourInfo[0]; // this gives next sibling
        }

        findRects.remove(findRects.get(findRects.size() - 1));
        System.out.println(pathName + " Segmentu: " + findRects.size());
        for (int i = 0; i < findRects.size(); ++i) {
            Rect rect = findRects.get(i);
            Mat image_output = blackWhiteImage.submat(rect);
            Imgcodecs.imwrite("words/" + pathName + "_" + i + ".jpg", image_output);

            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 1);

        }

        Imgcodecs.imwrite("test" + pathName + ".jpg", image);
    }

    private static void extractLetters(String pathName) {
        Mat image = Imgcodecs.imread(pathName, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.resize(image, image, new Size(WIDTH, HEIGHT));
        Mat imageHSV = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageA = new Mat(image.size(), CvType.CV_32F);
        Mat kernel = Mat.ones(1, 2, CV_8UC1);


        Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imageHSV, imageHSV, 125, 255, Imgproc.THRESH_OTSU);

        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(1, 1), 0);

        //Imgproc.dilate(imageBlurr, imageBlurr, kernel);
        //Imgproc.Canny(imageBlurr, imageBlurr, 0, 225, 3, true);
        //Imgcodecs.imwrite("1-gz-dilate.jpg", imageBlurr);
        // Imgproc.morphologyEx(imageBlurr, imageBlurr, Imgproc.MORPH_ELLIPSE, kernel);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 33, 5);
        // Imgcodecs.imwrite("2-adaptiveThreshold.jpg", imageA);


        //Imgcodecs.imwrite("magija.jpg", imageA);
        //Imgproc.dilate(imageA, imageA, kernel);
        // kernel = Mat.ones(5, 1, CvType.CV_8UC1);
        // Imgproc.erode(imageA, imageA, kernel);
        // Imgcodecs.imwrite("4. erode.jpg", imageA);
        // Imgproc.dilate(imageA, imageA, kernel, new Point(0,0), 100);
        //Imgproc.morphologyEx(imageA, imageA, Imgproc.MORPH_DILATE, kernel);
        // Imgproc.Laplacian(imageA, imageA, CvType.CV_8UC4);


        //Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 4);

        Mat blackWhiteImage = image.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageA.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //Imgproc.drawContours(imageBlurr, contours, 1, new Scalar(3, 3, 255));

        //combineLine(contours);

        List<Rect> findRects = new ArrayList<>();

        for (int i = 0; i < contours.size() - 1; i++) {
            //System.out.println("AREA: " + Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 30) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                //.out.println("aukstis: " + rect.height);
                if (rect.height > 19) {
                    List<Rect> temp = new ArrayList<>(findRects);

                    if (!(temp.stream().anyMatch(r -> r.contains(new Point(rect.x, rect.y))))) {
                        findRects.add(rect);
                    }
                }
            }
        }
        System.out.println(pathName + " Segmentu: " + findRects.size());
        for (int i = 0; i < findRects.size(); ++i) {
            Rect rect = findRects.get(i);
            Mat image_output = blackWhiteImage.submat(rect);
            Imgcodecs.imwrite("letters/" + pathName + "_" + i + ".jpg", image_output);
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);
            //Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 2);

        }

        Imgcodecs.imwrite("test" + pathName + ".jpg", image);
    }

}
