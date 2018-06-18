package com.github.saka1029.gis.height;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class HeightColor {

    public static final double MIN_HEIGHT = -9999D;
    public static final float MIN_COLOR = 270F/360F; // violet
    public static final double MAX_HEIGHT = 5000D;
    public static final float MAX_COLOR = 0F/360F; // red
	public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);
	public static final HeightColor HEIGHT_COLOR = new HeightColor()
		.set(0, 240/360F) // blue
		.set(20, 120F/360F) // green
		.set(40, 60F/360F) // orange
		.set(100, 10F/360F); // red

	private final NavigableMap<Double, Float> colors = new TreeMap<>();

	public HeightColor() {
	    colors.put(MIN_HEIGHT, MIN_COLOR);
	    colors.put(MAX_HEIGHT, MAX_COLOR);
	}

	public HeightColor set(double height, float hue) {
	    colors.put(height, hue);
		return this;
	}

	public double hue(double height) {
	    Entry<Double, Float> floor = colors.floorEntry(height);
	    Entry<Double, Float> ceil = colors.ceilingEntry(height);
	    double hue;
	    if (floor.getKey() == ceil.getKey())
	        hue = floor.getValue();
	    else {
            double slope = (ceil.getValue() - floor.getValue()) / (ceil.getKey() - floor.getKey());
            hue = (height - floor.getKey()) * slope + floor.getValue();
	    }
	    return hue;
	}

//	public BufferedImage chart() {
//		int max = HeightColor.MAX;
//		int width = 100;
//		int offset = 50;
//		BufferedImage bi = new BufferedImage(width, max, BufferedImage.TYPE_INT_ARGB);
//		Graphics g = bi.getGraphics();
//		try {
//			for (int y = 0, h = MAX - BASE - 1; y < max; ++y, --h) {
//				g.setColor(this.get(h));
//				g.fillRect(0, y, width, y);
//			}
//			g.setColor(Color.WHITE);
//			for (int y = 0, h = MAX - BASE - 1; y < max; ++y, --h) {
//				if (h % 20 == 0) {
//					g.drawLine(offset, y, offset + 10, y);
//					int yy = y + 5;
//					if (yy >= max) yy = y;
//					g.drawString(String.format("%5dm", h), offset + 10, yy);
//				}
//			}
//
//		} finally {
//			g.dispose();
//		}
//		return bi;
//	}
//
//	public BufferedImage graph() {
//		int max = HeightColor.MAX;
//		int width = 256;
//		int[] yy = new int[max];
//		int[] xr = new int[max];
//		int[] xg = new int[max];
//		int[] xb = new int[max];
//		for (int i = 0; i < max; ++i) {
//			int h = i - BASE;
//			Color c = this.get(h);
//			xr[i] = c.getRed();
//			xg[i] = c.getGreen();
//			xb[i] = c.getBlue();
//		}
//		for (int i = 0; i < max; ++i)
//			yy[i] = i;
//		BufferedImage bi = new BufferedImage(width, max, BufferedImage.TYPE_INT_ARGB);
//		Graphics g = bi.getGraphics();
//		try {
//			g.setColor(Color.WHITE);
//			g.fillRect(0, 0, width, max);
//			g.setColor(Color.RED);
//			g.drawPolyline(xr, yy, max);
//			g.setColor(Color.GREEN);
//			g.drawPolyline(xg, yy, max);
//			g.setColor(Color.BLUE);
//			g.drawPolyline(xb, yy, max);
//		} finally {
//			g.dispose();
//		}
//		return bi;
//	}
//
//	public static void write(BufferedImage img, File file) throws IOException {
//		ImageIO.write(img, "png", file);
//	}
//
//	public static void main(String[] args) throws IOException {
//		HeightColor hc = HEIGHT_COLOR;
//		File base = new File("D:/JPGIS/height5m");
//		HeightColor.write(hc.chart(), new File(base, "tokyo-height/image/HeightColor.png"));
//		HeightColor.write(hc.graph(), new File(base, "tokyo-height/image/HeightGraph.png"));
//	}

}
