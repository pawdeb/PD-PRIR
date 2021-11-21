import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class PDlab1 {

    public static boolean Punkt(int ile, double xpos, double ypos) {
        double x = 0;
        double y = 0;

        for(int i=0; i<ile; ++i) {
            double oldX = x;
            double oldY = y;
            x = oldX*oldX - oldY*oldY + xpos;
            y = 2*oldX*oldY + ypos;

            if(Math.sqrt(x*x + y*y) >= 2)
                return false;
        }
        return true;
    }

    public static BufferedImage Mandelbrot(int width, int height, double xmin, double xmax, double ymin, double ymax, int N) {
        byte[] bw = {(byte) 0xff, (byte) 0};
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, new IndexColorModel(1, 2, bw, bw, bw));

        for(int i=0; i<width; ++i) {
            for(int j=0; j<height; ++j) {
                double xpos = xmin + i*((xmax - xmin) / (width - 1));
                double ypos = ymin + j*((ymax - ymin) / (height - 1));
                if(Punkt(N, xpos, ypos))
                    image.setRGB(i, j, Color.BLACK.getRGB());
                else
                    image.setRGB(i, j, Color.WHITE.getRGB());
            }
        }
        return image;
    }

    public static BufferedImage getMandelbrot(int width, int height) {
        return Mandelbrot(width, height, -2.1, 0.6, -1.2, 1.2, 200);
    }

    public static long Czas(int ile, int width, int height, TimeUnit unit) {
        long[] czas = new long[ile];

        for(int i=0; i<ile; ++i) {
            long start = System.nanoTime();
            getMandelbrot(width, height);
            long end = System.nanoTime();
            czas[i] = end - start;
        }

        double srednia = 0;
        for(var i : czas)
            srednia += i;
        srednia /= ile;

        return unit.convert((long)srednia, TimeUnit.NANOSECONDS);
    }

    public static void Plot(BufferedImage image) throws IOException {
        File output = new File("Mandelbrot-" + new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss").format(new java.util.Date()) + ".png");
        ImageIO.write(image, "png", output);
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

        // ładny do wyplotowania
        int ostatni = rozmiary[rozmiary.length-1];
        Plot(getMandelbrot(ostatni, ostatni));
    }
}






























