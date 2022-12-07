import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Dithering {
	/**
	 * palette will be used only for exercise 4.2
	 */
	public static final RGBPixel[] palette = new RGBPixel[]
			{
					new RGBPixel(0, 0, 0),
					new RGBPixel(0, 0, 255),
					new RGBPixel(0, 255, 0),
					new RGBPixel(0, 255, 255),
					new RGBPixel(255, 0, 0),
					new RGBPixel(255, 0, 255),
					new RGBPixel(255, 255, 0),
					new RGBPixel(255, 255, 255)
			};

	public static void main(String args[]) throws IOException {
		BufferedImage image = readImg("lena_512x512.png"); // TODO edit to your needs

		BufferedImage image_bw = deepCopy(image);
		BufferedImage image_color = deepCopy(image);

		image_bw = bw_dither(image_bw);
		image_color = color_dither(image_color);

		writeImg(image_bw, "png", "dither_bw.png"); // TODO edit to your needs
		writeImg(image_color, "png", "dither_color.png"); // TODO edit to your needs
	}

	/**
	 * @param image the input image
	 * @return the dithered black and white image
	 */
	public static BufferedImage bw_dither(BufferedImage image) {
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				RGBPixel oldPixel = new RGBPixel(image.getRGB(i, j));
				RGBPixel newPixel = closestColorBW(oldPixel);
				image.setRGB(i, j, newPixel.toRGB());
				RGBPixel quantError = oldPixel.sub(newPixel);
				if (j < image.getWidth() - 1) {
					image.setRGB(i, j + 1, (new RGBPixel(image.getRGB(i, j + 1)).add(quantError.mul(7 / 16F))).toRGB()); //wenn des funktioniert friss i an besn
				}
				if (i < image.getHeight() - 1) {
					image.setRGB(i + 1, j, (new RGBPixel(image.getRGB(i + 1, j)).add(quantError.mul(5 / 16F))).toRGB());
				}
				if (i < image.getHeight() - 1 && j > 0) {
					image.setRGB(i + 1, j - 1, (new RGBPixel(image.getRGB(i + 1, j - 1)).add(quantError.mul(3 / 16F))).toRGB());
				}
				if (i < image.getHeight() - 1 && j < image.getWidth() - 1) {
					image.setRGB(i + 1, j + 1, (new RGBPixel(image.getRGB(i + 1, j + 1)).add(quantError.mul(1 / 16F))).toRGB());

				}
			}
		}
		return image;
	}

	/**
	 * @param image the input image
	 * @return the dithered 8bit color image using the static palette
	 */
	public static BufferedImage color_dither(BufferedImage image) {
		//TODO IMPLEMENT FOR EXCERCISE 4.2
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				RGBPixel oldPixel = new RGBPixel(image.getRGB(i, j));
				RGBPixel newPixel = closestColor(oldPixel, palette);
				image.setRGB(i, j, newPixel.toRGB());
				RGBPixel quantError = oldPixel.sub(newPixel);
				if (j < image.getWidth() - 1) {
					image.setRGB(i, j + 1, (new RGBPixel(image.getRGB(i, j + 1)).add(quantError.mul(7 / 16F))).toRGB()); //wenn des funktioniert friss i an besn
				}
				if (i < image.getHeight() - 1) {
					image.setRGB(i + 1, j, (new RGBPixel(image.getRGB(i + 1, j)).add(quantError.mul(5 / 16F))).toRGB());
				}
				if (i < image.getHeight() - 1 && j > 0) {
					image.setRGB(i + 1, j - 1, (new RGBPixel(image.getRGB(i + 1, j - 1)).add(quantError.mul(3 / 16F))).toRGB());
				}
				if (i < image.getHeight() - 1 && j < image.getWidth() - 1) {
					image.setRGB(i + 1, j + 1, (new RGBPixel(image.getRGB(i + 1, j + 1)).add(quantError.mul(1 / 16F))).toRGB());

				}
			}
		}
		return image;
	}

	/**
	 * @param color input color
	 * @return the closest outputcolor. (Can only be black or white!)
	 */
	public static RGBPixel closestColorBW(RGBPixel color) {
		double y = yColor(color.r, color.g, color.b);
		if (y <= 128) {
			return new RGBPixel(0, 0, 0);
		} else {
			return new RGBPixel(255, 255, 255);
		}
	}

	private static double yColor(int r, int g, int b) {
		double y = (0.299 * r) + (0.587 * g) + (0.114 * b);
		return y;
	}

	/**
	 * @param c       the input color
	 * @param palette the palette to use
	 * @return the closest color of the palette compared to c
	 */
	public static RGBPixel closestColor(RGBPixel c, RGBPixel[] palette) {
		int diff = 100000;
		int pointer = 0;
		for (int i = 0; i < palette.length; i++) {
			if(c.diff(palette[i]) < diff){
				diff = c.diff(palette[i]);
				pointer = i;
			}
		}
		return palette[pointer];
	}

	/**
	 * The Class RGBPixel is a helper class to ease the calculation with colors.
	 */
	static class RGBPixel {
		int r, g, b;

		public RGBPixel(int c) {
			Color color = new Color(c);
			this.r = color.getRed();
			this.g = color.getGreen();
			this.b = color.getBlue();
		}

		public RGBPixel(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public RGBPixel add(RGBPixel o) {
			return new RGBPixel((o.r + r), (o.g + g), (o.b + b));
		}

		public RGBPixel sub(RGBPixel o) {
			return new RGBPixel((r - o.r), (g - o.g), (b - o.b));
		}

		public RGBPixel mul(double d) {
			return new RGBPixel((int) (d * r), (int) (d * g), (int) (d * b));
		}

		public int toRGB() {
			return toColor().getRGB();
		}

		public Color toColor() {
			return new Color(clamp(r), clamp(g), clamp(b));
		}

		public int clamp(int c) {
			return Math.max(0, Math.min(255, c));
		}

		public int diff(RGBPixel o) {
			int diffR = (int) Math.pow((o.r - r), 2);
			int diffG = (int) Math.pow((o.g - g), 2);
			int diffB = (int) Math.pow((o.b - b), 2);

			return diffR + diffG + diffB;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof RGBPixel)) {
				return false;
			}
			return this.r == ((RGBPixel) obj).r && this.g == ((RGBPixel) obj).g && this.b == ((RGBPixel) obj).b;
		}
	}

	private static BufferedImage readImg(String filePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			System.out.println("Could not load Image\n");
			System.exit(-1);
		}
		return image;
	}

	private static void writeImg(BufferedImage image, String format, String path) {
		try {
			ImageIO.write(image, format, new File(path));
		} catch (IOException e) {
			System.out.println("Could not save Image\n");
			System.exit(-1);
		}
	}

	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}