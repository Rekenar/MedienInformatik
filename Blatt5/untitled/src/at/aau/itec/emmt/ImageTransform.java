package at.aau.itec.emmt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageTransform {

    public static void main(String[] args) throws IOException {


        int transformationLevel = 4;

        double[][] matrix = readGreyScaleImage(new File("transformed_lena.png"));
        matrix = shiftHPValues(matrix, matrix.length >> transformationLevel, -128);
        matrix = reconstruct2D(matrix, transformationLevel);
        storeGreyScaleImage(new File("reconstructed.png"), matrix);
    }

    public static double[][] shiftHPValues(double[][] matrix, int startIndex, double shiftValue) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (i > startIndex || j > startIndex) {
                    matrix[i][j] += shiftValue;
                }
            }
        }
        return matrix;
    }

    public static double[][] readGreyScaleImage(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        int x = image.getWidth();
        int y = image.getHeight();
        double[][] imageData = new double[x][y];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color px = new Color(image.getRGB(i, j));
                imageData[i][j] = px.getRed();
            }
        }
        return imageData;
    }

    public static void storeGreyScaleImage(File destination, double[][] imageData) throws IOException {
        int x = imageData.length;
        int y = imageData[0].length;
        BufferedImage image = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int value = Math.min(255, Math.max(0, (int) imageData[i][j]));
                Color px = new Color(value, value, value);
                image.setRGB(i, j, px.getRGB());
            }
        }
        ImageIO.write(image, "PNG", destination);
    }

    public static double[][] reconstruct2D(double[][] input, int level) {
        for (int r = level; r > 0; r--) {
            int bound = input.length / (int)(Math.pow(2, r - 1));
            // Do the rows
            for (int i = 0; i < bound; i++) {
                input[i] = discreteHaarWaveletReconstruct(input[i], r);
            }
            // Do the columns
            for (int i = 0; i < bound; i++) {
                setColumn(input, i, discreteHaarWaveletReconstruct(getColumn(input, i), r));
            }
        }

        return input;
    }

    public static double[] discreteHaarWaveletReconstruct(double[] input, int level) {
        int bound = input.length / (int)(Math.pow(2, level - 1));
        int half = bound / 2;

        double[] result = new double[input.length];
        for (int i = 0; i < half; i++) {
            result[2 * i] = input[i] + input[half + i];
            result[2 * i + 1] = input[i] - input[half + i];
        }

        for (int i = bound; i < input.length; i++) {
            result[i] = input[i];
        }

        return result;
    }

    public static double[] getColumn(double[][] matrix, int colNo) {

        double[] col = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            col[i] = matrix[i][colNo];
        }

        return col;
    }

    public static double[][] setColumn(double[][] matrix, int colNo, double[] col) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][colNo] = col[i];
        }

        return matrix;
    }
}
