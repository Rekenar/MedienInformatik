package at.aau.itec.emmt.jpeg.stud;

import at.aau.itec.emmt.jpeg.impl.Component;
import at.aau.itec.emmt.jpeg.impl.YUVImage;
import at.aau.itec.emmt.jpeg.spec.ColorSpaceConverterI;
import at.aau.itec.emmt.jpeg.spec.YUVImageI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

public class ColorSpaceConverter implements ColorSpaceConverterI {

    @Override
    public YUVImageI convertRGBToYUV(Image rgbImg) {
        int width;
        int height;
        int[][] y;
        int[][] u;
        int[][] v;
        BufferedImage bufferedImage = toBufferedImage(rgbImg);

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        y = new int[height][width];
        u = new int[height][width];
        v = new int[height][width];
        int[] rgb = new int[3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel  = bufferedImage.getRGB(i, j);
                Color color = new Color(pixel, true);
                rgb[0] = color.getRed();
                rgb[1] = color.getGreen();
                rgb[2] = color.getBlue();
                y[i][j] = (int)(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
                u[i][j] = (int)(128 - 0.1687 * rgb[0] - 0.3313 * rgb[1] + 0.5 * rgb[2]);
                v[i][j] = (int)(128 + 0.5 * rgb[0] - 0.4187 * rgb[1] - 0.0813 * rgb[2]);
            }
        }

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print(y[i][j] + " ");
            }
            System.out.println();
        }


        return new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u, YUVImageI.CB_COMP), new Component(v, YUVImageI.CR_COMP), 0);
    }
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bImage;
    }

}
