package com.github.saka1029.gis.height;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.github.saka1029.gis.common.Logging;

public class Zoomer {

	private static final Logger logger = Logging.logger(Zoomer.class);
	private static final String EXT = "png";
	private static final int MAX = 256;

//	private File inDir;
//	private File outDir;
//	/** ターゲットのズームレベル */
//	private int z;

	static String name(int x, int y, int z) {
		return String.format("%d-%d-%d.%s", x, y, z, EXT);
	}

	/**
	 * java.awt.ImageをBufferedImageに変換します。
	 */
	private static BufferedImage convert(Image image) {
		BufferedImage bimg = new BufferedImage(
			image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimg;
	}

	static void zoomOut(File inDir, File outDir, String name, int x, int y, int z0, int r) throws IOException {
		int size = MAX * r;
		BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		try (Closeable c = () -> g.dispose()) {
			g.setColor(HeightColor.TRANSPARENT_COLOR);
			g.drawRect(0, 0, size, size);
			for (int i = 0; i < r; ++i)
				for (int j = 0; j < r; ++j) {
					String div = name(x * r + i, y * r + j, z0);
					File df = new File(inDir, div);
					if (!df.exists()) continue;
					BufferedImage im = ImageIO.read(df);
					g.drawImage(im, i * MAX, j * MAX, null);
				}
			ImageFilter filter = new AreaAveragingScaleFilter(MAX, MAX);
			ImageProducer p = new FilteredImageSource(bi.getSource(), filter);
			Image dstImage = Toolkit.getDefaultToolkit().createImage(p);
			BufferedImage newImage = convert(dstImage);
			logger.info(name);
			ImageIO.write(newImage, EXT, new File(outDir, name));
		}
	}

	static void zoomIn(File outDir, int z, File file, int x, int y, int r) throws IOException {
		int ssize = MAX / r;	// 元のイメージから切り出すサイズ
		BufferedImage iimg = ImageIO.read(file);
		for (int i = 0; i < r; ++i)
			for (int j = 0; j < r; ++j) {
				BufferedImage oimg = new BufferedImage(MAX, MAX, BufferedImage.TYPE_INT_ARGB);
				Graphics g = oimg.getGraphics();
				try (Closeable c = () -> g.dispose()){
					g.drawImage(iimg,
						0, 0, MAX, MAX,
						i * ssize, j * ssize, (i + 1) * ssize, (j + 1) * ssize, null);
				}
				String name = name(x + i, y + j, z);
                logger.info(name);
				ImageIO.write(oimg, EXT, new File(outDir, name));
			}
	}

	static void zoom(File inDir, File outDir, File file, int z) throws IOException {
		String org = file.getName();
		String[] f = org.split("[-\\.]");
		int x0 = Integer.parseInt(f[0]);
		int y0 = Integer.parseInt(f[1]);
		int z0 = Integer.parseInt(f[2]);
		if (z == z0)
			return;
		else if (z < z0) {
			int r = 1 << (z0 - z);
			int x = x0 / r;
			int y = y0 / r;
			String name = name(x, y, z);
//			if (new File(outDir, name).exists()) return;
			logger.fine(file.getName() + "->" + x + "," + y + "," + r);
//			info(logger, name);
			zoomOut(inDir, outDir, name, x, y, z0, r);
		} else {
			int r = 1 << (z - z0);
			int x = x0 * r;
			int y = y0 * r;
			logger.fine(x + "," + y + "," + r);
			zoomIn(outDir, z, file, x, y, r);
		}
	}

	public static void zoom(File inDir, File outDir, int z) throws IOException {
		if (outDir.equals(inDir)) return;
		if (!outDir.exists()) outDir.mkdirs();
//		this.inDir = inDir;
//		this.outDir = outDir;
//		this.z = z;
		for (File e : inDir.listFiles(f -> f.getName().matches("(?i)\\d+-\\d+-\\d+\\.png")))
			zoom(inDir, outDir, e, z);
	}

//	public static void main(String[] args) throws IOException {
//		File base = new File("D:/JPGIS/height5m/tokyo-height/image/theme1");
//		File inDir = new File(base, "15");
//		for (int z = 16; z <= 16; ++z)
//			new Zoomer().run(inDir, new File(base, Integer.toString(z)), z);
//		for (int z = 14; z >= 10; --z)
//			new Zoomer().run(inDir, new File(base, Integer.toString(z)), z);
//	}
}
