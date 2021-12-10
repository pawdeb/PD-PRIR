import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PDlab3A {

    public static BufferedImage Gauss (BufferedImage obrazek) {
        float[] matrix = {
                1/16f, 1/8f, 1/16f,
                1/8f, 1/4f, 1/8f,
                1/16f, 1/8f, 1/16f,
        };

        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
        return op.filter(obrazek, null);
    }


    public static void Obrazki (String adres, boolean czyGauss) throws IOException {
        Document site = Jsoup.connect(adres).get();
        Elements elements = site.select("[href$=.png]");
//        int iterator = 0;

        for(var element : elements) {
            URL imageURL = new URL(element.attr("abs:href"));
//            iterator++;

            if(czyGauss) {
                BufferedImage image = ImageIO.read(imageURL);
                image = Gauss(image);
                ImageIO.write(image, "png", new File("pobrane/A/" + "b_" + element.text()));
            }

            else
                ImageIO.write(ImageIO.read(imageURL), "png", new File("pobrane/A/" + element.text()));
        }
    }


    public static void main(String[] args) throws IOException {
        long t1 = System.nanoTime();
        Obrazki("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/", false);
        long t2 = System.nanoTime();
        long bezGaussa = (t2 - t1) / 1000000;
        System.out.println("Czas bez Gaussa " + bezGaussa + " ms");

        long t3 = System.nanoTime();
        Obrazki("http://www.if.pw.edu.pl/~mrow/dyd/wdprir/", true);
        long t4 = System.nanoTime();
        long zGaussem = (t4 - t3) / 1000000;
        System.out.println("Czas z Gaussem " + zGaussem + " ms");
    }
}
