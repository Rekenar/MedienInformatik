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
        y = new int[width][height];
        u = new int[width][height];
        v = new int[width][height];
        int[] rgb = new int[3];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color = bufferedImage.getRGB(i, j);
                rgb[0] = (color & 0xff0000) >> 16;
                rgb[1] = (color & 0xff00) >> 8;
                rgb[2] = (color & 0xff);
                y[i][j] = (int)(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
                u[i][j] = (int)(128 - 0.1687 * rgb[0] - 0.3313 * rgb[1] + 0.5 * rgb[2]);
                v[i][j] = (int)(128 - 0.5 * rgb[0] - 0.4187 * rgb[1] - 0.0813 * rgb[2]);
            }
        }
        YUVImageI yuvImageI = new YUVImage(new Component(y, YUVImageI.Y_COMP), new Component(u, YUVImageI.CB_COMP), new Component(v, YUVImageI.CR_COMP), 0);


        return yuvImageI;
    }
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

}
