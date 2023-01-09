package at.aau.emmt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PSNRCalculator {

    public static final double MAX = 255.0;

    public static final char DELIMITER = File.separatorChar;
    public static final String FRAMES_FOLDER = "frames";

    public static void main(String args[]) {
        String yuvFrames = "yuvFrames";
        String av1Frames = "av1";
        String vp9_600Frames = "vp9_600";
        String vp9_1200Frames = "vp9_1200";
        String h264_600Frames = "h264_600";
        String h264_1200Frames = "h264_1200";
        String h265_600Frames = "h265_600";
        String h265_1200Frames = "h265_1200";


        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + h264_600Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + h264_1200Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + h265_600Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + h265_1200Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + vp9_600Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + vp9_1200Frames);
        calculateAveragePSNRforVideoFrames(FRAMES_FOLDER + DELIMITER + yuvFrames,
                FRAMES_FOLDER + DELIMITER + av1Frames);
    }

    /**
     * @param original the raw frame of a video
     * @param decoded  the decoded frame of an encoded video
     * @return the PSNR value
     */
    public static double PSNR(BufferedImage original, BufferedImage decoded) {
        if(original.getWidth() != decoded.getWidth() || original.getHeight() != decoded.getHeight() ||
                original.getType() != decoded.getType()) {
            throw new IllegalArgumentException("Images must be the same size and type");
        }

        double mse = MSE(original, decoded);


        double psnr = (20 * Math.log10(MAX))-(10 * Math.log10(mse));
        return psnr;
    }

    /**
     * @param original the raw frame of a video
     * @param decoded  the decoded frame of an encoded video
     * @return mean squared error between two frames
     */
    public static double MSE(BufferedImage original, BufferedImage decoded) {
            if (original.getWidth() != decoded.getWidth() || original.getHeight() != decoded.getHeight()) {
                throw new IllegalArgumentException("Images must have the same dimensions");
            }

            double mse = 0.0;

            for (int y = 0; y < original.getHeight(); y++) {
                for (int x = 0; x < original.getWidth(); x++) {
                    int rgb1 = original.getRGB(x, y);
                    int rgb2 = decoded.getRGB(x, y);
                    int r1 = (rgb1 >> 16) & 0xff;
                    int g1 = (rgb1 >>  8) & 0xff;
                    int b1 = (rgb1      ) & 0xff;
                    int r2 = (rgb2 >> 16) & 0xff;
                    int g2 = (rgb2 >>  8) & 0xff;
                    int b2 = (rgb2      ) & 0xff;
                    Color color1 = new Color(r1, g1, b1);
                    Color color2 = new Color(r2, g2, b2);
                    int y1 = rgbToYuv(color1);
                    int y2 = rgbToYuv(color2);
                    mse += Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2) + Math.pow(y1 - y2, 2);
                }
            }

            mse /= (original.getWidth() * original.getHeight() * 4);

            return mse;
    }


    /**
     * @param color RGB color object
     * @return luminance channel of the YUV colorspace
     */
    public static int rgbToYuv(Color color) {
        return (int) Math.round(0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
    }

    private static void calculateAveragePSNRforVideoFrames(String raw, String decoded) {
        File raw_folder = new File(raw);
        File decoded_folder = new File(decoded);


        if (!raw_folder.isDirectory() || !decoded_folder.isDirectory()) {
            System.out.println(raw_folder.toURI() + " is not a folder.");
            System.exit(-1);
        }

        String[] raw_list = raw_folder.list();
        String[] decoded_list = decoded_folder.list();

        if (raw_list.length != decoded_list.length) {
            System.out.println("Folders do not have the same amount of images.");
            System.exit(-1);
        }

        File raw_file = null;
        File decoded_file = null;
        double avg_psnr = 0;
        System.out.println("Calculating PSNR for decoded");
        for (int i = 0; i < raw_list.length; i++) {
            System.out.print("\r" + ((int) ((i / (double) raw_list.length) * 100)) + "%");

            raw_file = new File(raw + DELIMITER + raw_list[i]);
            decoded_file = new File(decoded + DELIMITER + decoded_list[i]);

            if (raw_file.getName().equalsIgnoreCase(decoded_file.getName())) {
                BufferedImage originalImage = loadImage(raw_file.getAbsolutePath());
                BufferedImage decodedImage = loadImage(decoded_file.getAbsolutePath());
                avg_psnr += PSNR(originalImage, decodedImage);
            } else {
                System.out.println("Files didnt match...");
                System.exit(-1);
            }
        }
        System.out.println("\r[Calculation completed]");
        avg_psnr /= raw_list.length;
        System.out.println("Average PSNR for " + raw + " and " + decoded + " is: " + avg_psnr + "\n");
    }

    public static BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.out.println("Could not load Image:" + filename);
            System.exit(-1);
        }

        return image;
    }
}