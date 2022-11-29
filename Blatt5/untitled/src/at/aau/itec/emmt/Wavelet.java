package at.aau.itec.emmt;

import java.io.IOException;
import java.util.Arrays;

public class Wavelet {
    //analysis filter
    static final double[] a0 = {1 / Math.sqrt(2), 1 / Math.sqrt(2)};    // low-pass
    static final double[] a1 = {1 / Math.sqrt(2), -1 / Math.sqrt(2)};    // high-pass

    //synthesis filter
    static final double[] s0 = a0;    // low-pass
    static final double[] s1 = a1;    // high-pass

    public static void main(String args[]) throws IOException {
        double[] data = {10, 13, 25, 26, 29, 27, 9, 15};

        double[][] matrix = {
                {10, 14, 7, 5},
                {16, 12, 13, 19},
                {14, 12, 1, 5},
                {8, 2, 3, 3}
        };

        System.out.println("Input:\n" + arrayToString(data) + "\n");

        double[] transformed = discreteHaarWaveletTransform(data, 0);
        System.out.println("Transformed Level 0:\n" + arrayToString(transformed) + "\n");

        double[] reconstructed = discreteHaarWaveletReconstruct(transformed, 1);
        System.out.println("Reconstructed from Level 1:\n" + arrayToString(reconstructed) + "\n");

        data = transform1D(data);
        System.out.println("Transformed Level N:\n" + arrayToString(data) + "\n");

        data = reconstruct1D(data);
        System.out.println("Reconstructed from Level N:\n" + arrayToString(data) + "\n");

        System.out.println("Input Matrix:\n" + matrixToString(matrix));

        matrix = transform2D(matrix);
        System.out.println("Transformed Matrix:\n" + matrixToString(matrix));

        matrix = reconstruct2D(matrix);
        System.out.println("Reconstructed Matrix:\n" + matrixToString(matrix));
    }


    public static double[] discreteHaarWaveletTransform(double[] input, int level) {

        int bound = input.length / (int)(Math.pow(2, level));
        int half = bound / 2;

        double[] result = new double[input.length];

        for (int i = 0; i < half; i++) {
            result[i] = input[2 * i] * a0[0] + input[2 * i + 1] * a0[1];
            result[half + i] = input[2 * i] * a1[0] + input[2 * i + 1] * a1[1];
        }

        for (int i = bound; i < result.length; i++) {
            result[i] = input[i];
        }
        return result;
    }

    public static double[] discreteHaarWaveletReconstruct(double[] input, int level) {

        int bound = input.length / (int)(Math.pow(2, level - 1));
        int half = bound / 2;

        double[] result = new double[input.length];
        for (int i = 0; i < half; i++) {
            result[2 * i] = input[i] * a0[0] + input[half + i] * a0[1];
            result[2 * i + 1] = input[i] * a1[0] + input[half + i] * a1[1];
        }
        for (int i = bound; i < input.length; i++) {
            result[i] = input[i];
        }

        return result;
    }

    public static double[] transform1D(double[] input) {
        int runs = getMaxLevel(input);

        for (int i = 0; i < runs; i++) {
            input = discreteHaarWaveletTransform(input, i);
        }

        return input;
    }

    public static double[] reconstruct1D(double[] input) {
        int runs = getMaxLevel(input);

        for (int i = runs; i > 0; i--) {
            input = discreteHaarWaveletReconstruct(input, i);
        }

        return input;
    }

    public static double[][] reconstruct2D(double[][] input) {
        int runs = getMaxLevel(input[0]);

        for (int r = runs; r > 0; r--) {

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

    public static double[][] transform2D(double[][] input) {
        int runs = getMaxLevel(input[0]);

        for (int r = 0; r < runs; r++) {

            int bound = input.length / (int)(Math.pow(2, r));

            // Do the rows
            for (int i = 0; i < bound; i++) {
                input[i] = discreteHaarWaveletTransform(input[i], r);
            }

            // Do the columns
            for (int i = 0; i < bound; i++) {
                setColumn(input, i, discreteHaarWaveletTransform(getColumn(input, i), r));
            }
        }

        return input;
    }

    /**
     * Extracts column with given column number from matrix
     *
     * @param matrix
     * @param colNo  Column no. to export, index starts with 0
     * @return Column as double array
     */
    public static double[] getColumn(double[][] matrix, int colNo) {

        double[] col = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            col[i] = matrix[i][colNo];
        }

        return col;
    }

    /**
     * Replaces the given column in the matrix
     *
     * @param matrix
     * @param colNo  Column no. to export, index starts with 0
     * @param col    New column values as array
     * @return Matrix with replaced column
     */
    public static double[][] setColumn(double[][] matrix, int colNo, double[] col) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][colNo] = col[i];
        }

        return matrix;
    }

    public static String matrixToString(double[][] matrix) {
        String s = "";

        for (int rows = 0; rows < matrix.length; rows++)
            s += arrayToString(matrix[rows]) + "\n";

        return s;
    }

    public static String arrayToString(double[] array) {
        int[] a = new int[array.length];
        for (int i = 0; i < a.length; i++)
            a[i] = (int) Math.round(array[i]);

        return Arrays.toString(a);
    }

    /**
     * Calculates the maximum number of transformation levels.
     *
     * @param input
     * @return
     */
    public static int getMaxLevel(double[] input) {
        return (int) Math.round((Math.log(input.length) / Math.log(2)));
    }
}