import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class PDlab2A {

    public static boolean Punkt(int ile, double xpos, double ypos) {
        double x = 0;
        double y = 0;

        for(int i=0; i<ile; i++) {
            double oldX = x;
            double oldY = y;
            x = oldX*oldX - oldY*oldY + xpos;
            y = 2*oldX*oldY + ypos;

            if(Math.sqrt(x*x + y*y) >= 2)
                return false;
        }
        return true;
    }

    public static class TablicaRunnable implements Runnable{
        int width, height, offset, N;
        double xmin, xmax, ymin, ymax;
        Boolean[][] Tablica;

        public TablicaRunnable(int w, int h, int o, double xmi, double xma, double ymi, double yma, int n, Boolean[][] T){
            this.width = w;
            this.height = h;
            this.offset = o;
            this.xmin = xmi;
            this.xmax = xma;
            this.ymin = ymi;
            this.ymax = yma;
            this.N = n;
            this.Tablica = T;
        }
        public void run(){

            for(int x = this.offset; x < this.width; x++){
                double xtmp = xmin + (x*((xmax - xmin)/(height - 1)));
                for (int y = 0; y < height; y++){
                    double ytmp = ymin + (y*((ymax - ymin)/(height - 1)));
                    if(Punkt(N, xtmp, ytmp)){
                        Tablica[x][y] = true;
                    }
                    else {
                        Tablica[x][y] = false;
                    }
                }
            }
        }
    }

    public static Boolean[][] Tablica(int width, int height, double xmin, double xmax, double ymin, double ymax, int N){
        Boolean[][] validityArray = new Boolean[width][height];
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        int[] offsetArray = new int[numberOfThreads];
        int basicOffset = width/numberOfThreads;

        for(int i=0; i<numberOfThreads; ++i){
            offsetArray[i] = basicOffset*i;
        }

        Runnable runnable0 = new TablicaRunnable((basicOffset+offsetArray[0]), height, offsetArray[0], xmin, xmax, ymin, ymax, N, validityArray);
        Thread thread0 = new Thread(runnable0);
        thread0.start();

        Runnable runnable1 = new TablicaRunnable((basicOffset+offsetArray[1]), height, offsetArray[1], xmin, xmax, ymin, ymax, N, validityArray);
        Thread thread1 = new Thread(runnable1);
        thread1.start();

        Runnable runnable2 = new TablicaRunnable((basicOffset+offsetArray[2]), height, offsetArray[2], xmin, xmax, ymin, ymax, N, validityArray);
        Thread thread2 = new Thread(runnable2);
        thread2.start();

        Runnable runnable3 = new TablicaRunnable((basicOffset+offsetArray[3]), height, offsetArray[3], xmin, xmax, ymin, ymax, N, validityArray);
        Thread thread3 = new Thread(runnable3);
        thread3.start();

        try {
            thread0.join();
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  validityArray;
    }

    public static BufferedImage Mandelbrot(int width, int height, Boolean [][] Tablica) {
        byte[] bw = {(byte) 0xff, (byte) 0};
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, new IndexColorModel(1, 2, bw, bw, bw));

        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                if(Tablica[i][j])
                    image.setRGB(i, j, Color.BLACK.getRGB());
                else
                    image.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        return image;
    }

    public static long Czas(int ile, int width, int height, TimeUnit unit) {
        long[] czas = new long[ile];

        for(int i=0; i<ile; i++) {
            long start = System.nanoTime();
            Boolean[][] Tablica = Tablica(width, height, -2.1, 0.6, -1.2, 1.2, 200);
            Mandelbrot(width, height, Tablica);
            long end = System.nanoTime();
            czas[i] = end - start;
        }

        double srednia = 0;
        for(var i : czas)
            srednia += i;
        srednia /= ile;

        return unit.convert((long)srednia, TimeUnit.NANOSECONDS);
    }

    public static void Statystyka(int[] rozmiary, double[] czasy) throws IOException {
        FileWriter fw = new FileWriter("Mandelbrot-" + new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()) + ".txt");
        for (int i = 0; i < rozmiary.length; i++) {
            fw.write(rozmiary[i] + "\t" + czasy[i] + "\n");
        }
        fw.close();
    }

    public static void main(String[] args) throws IOException {
        // wydajnosc średnia dla n generacji
        int n = 3;
        int[] rozmiary = {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
        double[] czasy = {32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};

        int iterator = 0;
        for (int i : rozmiary) {
            double czas = Czas(n, i, i, TimeUnit.MILLISECONDS);
            System.out.println("Sredni czas generowania jednego Mandelbrota o boku " + i + " to " + czas + " ms");
            czasy[iterator] = czas;
            iterator++;
        }
        Statystyka(rozmiary,czasy);
    }
}






























