package at.aau.itec.emmt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.dgc.VMID;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * @author Philipp Moll
 */
public class MotionVector {

    private static final String IMAGE_1 = "bunny_1.png";
//    private static final String IMAGE_1 = "geo_1.png";
    private static final String IMAGE_2 = "bunny_2.png";
//    private static final String IMAGE_2 = "geo_2.png";
    private static final String OUTPUT = "bunny_out.png";
//    private static final String OUTPUT = "geo_out.png";

    public static final int BLOCKSIZE = 8;
    public static final int SEARCH_WINDOW = 32;

    public static void main(String args[]) {
        int[][] rFrame = getLumincanceChannel(IMAGE_1);
        int[][] tFrame = getLumincanceChannel(IMAGE_2);

        MotionVector vector = searchMotionVector(rFrame, tFrame, BLOCKSIZE, SEARCH_WINDOW, 256, 256);

        if (vector != null)
            System.out.println(vector.toString());
        else
            System.out.println("Vector is null!");

        // TODO: Execute the following code if your result matches "MV(-5, -3)"
         searchAndVisualizeMotionVector(rFrame, tFrame);
    }

    /////////////////////////////////////START SECTION FOR STUDENT IMPLEMENTATION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


    /**
     * Searches the motion vector for a given Macroblock (rowIndex, colIndex) in the current frame (tFrame) and given
     * reference frame.
     *
     * @param rFrame       the reference frame of a video (actually just 1 channel, e.g. Y')
     * @param tFrame       the target frame of a video	(actually just 1 channel, e.g. Y')
     * @param blocksize    the blocksize of the macroblocks (should be 8x8 or 16x16)
     * @param searchWindow the searchwindow size = (2*p+1) for the algorithm (the bigger the better the result, but the worse the performance)
     * @param rowIndex     the y-coordinate of first (top,left) element of the macroblock in the tFrame
     * @param colIndex     the x-coordinate of the first (top,left) element of the macroblock in the tFrame
     * @return the motion vector
     */
    private static MotionVector searchMotionVector(int[][] rFrame, int[][] tFrame, int blocksize, int searchWindow, int rowIndex, int colIndex) {
        double min_MAD = Double.MAX_VALUE;
        double cur_MAD;

        int u = 0;
        int v = 0;


        //TODO IMPLEMENT
        //1. check if the specified macroblock does not exceed the frame border. if so return null.
        if(rFrame.length <= blocksize + rowIndex || rFrame[0].length <= blocksize + colIndex) return null;

        //3. sequential search algorithm on rFrame to determine the motion vector
        //use MAD function as difference measure.

        for(int i = -1*searchWindow/2; i<searchWindow/2; i++) {
            for (int j = -1*searchWindow/2-1; j<searchWindow/2; j++) {
                cur_MAD = MAD(tFrame, rFrame, i, j, rowIndex, colIndex);
                if (cur_MAD < min_MAD) {
                    min_MAD = cur_MAD;
                    u = i;
                    v = j;
                }
            }
        }
        MotionVector mv = new MotionVector(u, v);

        return mv;
    }

    /**
     * Calculates the mean absolute distance of a given Macroblock to a position in the given reference frame.
     *
     * @param tBlock   the target macroblock for which you search the MV
     * @param rFrame   the reference frame
     * @param i        the search offset of the x coordinate (current window movement)
     * @param j        the search offset of the y coordinate (current window movement)
     * @param rowIndex the y-coordinate of first (top,left) element of the macroblock in the tFrame
     * @param colIndex the x-coordinate of the first (top,left) element of the macroblock in the tFrame
     * @return the MDA value
     */
    private static double MAD(int[][] tBlock, int[][] rFrame, int i, int j, int rowIndex, int colIndex) {
        //TODO IMPLEMENT

        if(j+rowIndex < 0) return Double.MAX_VALUE;
        if(i+colIndex < 0) return Double.MAX_VALUE;
        double cur_MAD = 0.0;
        for(int k = 0; k<BLOCKSIZE-1; k++){
            for(int l = 0; l<BLOCKSIZE-1;l++){
                cur_MAD += (1/Math.pow(BLOCKSIZE,2)) * (Math.abs(tBlock[rowIndex+k][colIndex+l]-rFrame[rowIndex+k+j][colIndex+l+i]));
            }
        }
        return cur_MAD;
    }



    /////////////////////////////////////END SECTION FOR STUDENT IMPLEMENTATION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private int u, v;
    private int blockX, blockY;

    /**
     * creates a new motion vector
     *
     * @param u vertical change
     * @param v horizontal change
     */
    public MotionVector(int u, int v) {
        this.u = u;
        this.v = v;
    }

    /**
     * updates the motion vector
     *
     * @param u horizontal change
     * @param v vertical change
     */
    public void setNewValues(int u, int v) {
        this.u = u;
        this.v = v;
    }

    /**
     * converts the MV to a String.
     */
    public String toString() {
        return new String("MV(" + u + "," + v + ")");
    }

    /**
     * loads an image file from the filesystem. returns the luminanceChannel of the image as int[][].
     *
     * @param file the image to read.
     * @return
     */
    private static int[][] getLumincanceChannel(String file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Could not load Image: " + file);
            System.exit(-1);
        }

        int h = image.getHeight();
        int w = image.getWidth();
        int pixel = 0;

        int[][] data = new int[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                pixel = image.getRGB(j, i);
                data[i][j] = (int) Math.round(0.299 * ((pixel >> 16) & 0xFF) + 0.587 * ((pixel >> 8) & 0xFF) + 0.114 * (pixel & 0xFF));
            }
        }

        return data;
    }

    private static void searchAndVisualizeMotionVector(int[][] rFrame, int[][] tFrame) {
        int blocksize;
        blocksize = 32;
        List<MotionVector> motionVectorList = new ArrayList<>();
        for (int i = 0; i < rFrame.length; i += blocksize) {
            for (int z = 0; z < rFrame[0].length; z += blocksize) {
                MotionVector mv = searchMotionVector(rFrame, tFrame, blocksize, 64, i, z);
                if (mv != null && (mv.v > 0 || mv.u > 0)) {
                    mv.blockY = i + blocksize / 2;
                    mv.blockX = z + blocksize / 2;
                    motionVectorList.add(mv);
                }
            }
        }
        visualizeVectors(IMAGE_2, motionVectorList);
    }

    private static void visualizeVectors(String file, List<MotionVector> motionVectorList) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Could not load Image: " + file);
            System.exit(-1);
        }

        int h = image.getHeight();
        int w = image.getWidth();
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        for (MotionVector vector : motionVectorList) {
            drawArrow(g2d, vector.blockX, vector.blockY, vector.blockX + vector.u, vector.blockY + vector.v);
        }
        g2d.dispose();
        try {
            ImageIO.write(image, "png", new File(OUTPUT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawArrow(Graphics2D g2d, int tipX, int tipY, int tailX, int tailY) {

        int arrowLength = 7; //can be adjusted
        int dx = tipX - tailX;
        int dy = tipY - tailY;

        double theta = Math.atan2(dy, dx);

        double rad = Math.toRadians(35); //35 angle, can be adjusted
        double x = tipX - arrowLength * Math.cos(theta + rad);
        double y = tipY - arrowLength * Math.sin(theta + rad);

        double phi2 = Math.toRadians(-35);//-35 angle, can be adjusted
        double x2 = tipX - arrowLength * Math.cos(theta + phi2);
        double y2 = tipY - arrowLength * Math.sin(theta + phi2);

        int[] arrowYs = new int[3];
        arrowYs[0] = tipY;
        arrowYs[1] = (int) y;
        arrowYs[2] = (int) y2;

        int[] arrowXs = new int[3];
        arrowXs[0] = tipX;
        arrowXs[1] = (int) x;
        arrowXs[2] = (int) x2;

        g2d.drawLine(tipX, tipY, tailX, tailY);
        g2d.fillPolygon(arrowXs, arrowYs, 3);
    }

}