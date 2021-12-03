import javax.imageio.ImageIO;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.*;

public class PDlab2B {

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

    public static class TablicaCallable implements Callable{
        int width, height, offsetx, offsety, size, N;
        double xmin, xmax, ymin, ymax;
        Boolean[][] Tablica;

        public TablicaCallable(int w, int h, int s, int ox, int oy, double xmi, double xma, double ymi, double yma, int n, Boolean[][] T){
            this.width = w;
            this.height = h;
            this.offsetx = ox;
            this.offsety = oy;
            this.xmin = xmi;
            this.xmax = xma;
            this.ymin = ymi;
            this.ymax = yma;
            this.size = s;
            this.N = n;
            this.Tablica = T;
        }
        public Boolean call(){

            for(int x = this.offsetx; x < this.width; x++){
                double xtmp = xmin + (x*((xmax - xmin)/(height - 1)));
                for (int y = this.offsety; y < this.height; y++){
                    double ytmp = ymin + (y*((ymax - ymin)/(height - 1)));
                    if(Punkt(N, xtmp, ytmp)){
                        Tablica[x][y] = true;
                    }
                    else {
                        Tablica[x][y] = false;
                    }
                }
            }
            return true;
        }
    }

    public static Boolean[][] Tablica(int width, int height, int bok, double xmin, double xmax, double ymin, double ymax, int N, ArrayList<Long> averageTimeList) throws InterruptedException {
        Boolean[][] Tablica = new Boolean[width][height];
        ArrayList<Callable<Boolean>> callableList = new ArrayList<>();
        int chunks = width/bok;
        int[] offsetArray = new int[width/bok];
        int offset = bok;

        for(int i=0; i<chunks; i++){
            offsetArray[i] = offset*i;
        }

        for (int i = 0; i < chunks; i++){
            for (int j = 0; j < chunks; j++){
                callableList.add(new TablicaCallable((offset+offsetArray[i]), (offset+offsetArray[j]), height, offsetArray[i], offsetArray[j], xmin, xmax, ymin, ymax, N, Tablica));
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        long start = System.nanoTime();
        List<Future<Boolean>> futures = executor.invokeAll(callableList);
        long end = System.nanoTime();
        averageTimeList.add(end - start);
        executor.shutdown();

        return Tablica;
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

    public static ArrayList<Long> Czas(int ile, int bok[], int size) throws InterruptedException {
        ArrayList<Long> averageTimeList = new ArrayList<Long>();

        for(int i=0; i<6; i++){
            long timeSum = 0;
            ArrayList<Long> temporaryAverageTimeList = new ArrayList<Long>();
            for(int j = 0; j < ile; j++){
                Boolean[][] Tablica = Tablica(size, size, bok[i],-2.1, 0.6, -1.2, 1.2, 200, temporaryAverageTimeList);
                Mandelbrot(size, size, Tablica);
            }
            long sum = 0;
            for(Long d : temporaryAverageTimeList)
                sum += d;
            averageTimeList.add(sum/ile);
        }

        return averageTimeList;
    }

//    public static void Statystyka(int[] rozmiary, int bok[], double[] czasy) throws IOException {
//        FileWriter fw = new FileWriter("Mandelbrot-" + new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()) + ".txt");
//        for (int i = 0; i < rozmiary.length; i++) {
//            fw.write(rozmiary[i] + "\t" + czasy[i] + "\n");
//        }
//        fw.close();
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // wydajnosc średnia dla n generacji
        int n = 3;
        int[] rozmiary = {1024, 2048, 4096, 8192};
        int[] bok = {4, 8, 16, 32, 64, 128};
        ArrayList<ArrayList<Long>> czasy = new ArrayList<ArrayList<Long>>();

        for (int i : rozmiary) {
            czasy.add(Czas(n, bok, i));
        }

        FileWriter fw = new FileWriter("Mandelbrot-" + new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()) + ".txt");
        for (int i=0; i<rozmiary.length; i++) {
                //fw.write(rozmiary[i] + "\t" + bok[j] + "\n");
                fw.write(rozmiary[i] + "\n");
                fw.write(czasy.get(i) + "\n");
        }
        fw.close();
    }
}






























