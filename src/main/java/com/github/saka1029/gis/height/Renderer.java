package com.github.saka1029.gis.height;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.github.saka1029.gis.common.Logging;

public class Renderer {

    static Logger logger = Logging.logger(Renderer.class);

    /** メッシュサイズ(5m) */
    static final double CELL_SIZE = 1;
    /** 傾斜時の水平距離 */
    static final double K = CELL_SIZE * Math.sqrt(2D) / 2D;
    /** 傾斜度計算時の参照セル範囲（対象セル±SHADING_RANGEの範囲で計算） */
    static final int SHADING_RANGE = 2;
    static final double SIN45 = Math.sin(Math.PI / 4.0);

    static double slope(DB db, long pp, long qq, int x, int y) {
        double center = HeightEnum.height(db.get(pp + x, qq + y));
        double height = 0;
        double distance = 0;
        for (int i = -SHADING_RANGE; i <= SHADING_RANGE; ++i)
            for (int j = -SHADING_RANGE; j <= SHADING_RANGE; ++j) {
                int ij = i + j;
                if (ij == 0)
                    continue;
                int xi = x + i;
                int yj = y + j;
                int typeHeight = db.get(pp + xi, qq + yj);
                HeightEnum t = HeightEnum.type(typeHeight);
                double h = HeightEnum.height(typeHeight);
                switch (t) {
                case 海水面:
                case 内水面:
                    break;
                case 地表面:
                case 表層面:
                case その他:
                    height += ij < 0 ? center - h : h - center;
                    distance += Math.abs(ij) * K;
                    break;
                }
            }
        return Math.atan(height / distance);
    }

    static void draw(DB db, long key, Graphics g) {
        long pp = db.pp(key) * DB.ENTRY_SIZE;
        long qq = db.qq(key) * DB.ENTRY_SIZE;
        for (int x = 0; x < DB.ENTRY_SIZE; ++x)
            for (int y = 0; y < DB.ENTRY_SIZE; ++y) {
                int typeHeight = db.get(pp + x, qq + y);
                HeightEnum type = HeightEnum.type(typeHeight);
                double height = HeightEnum.height(typeHeight);
                switch (type) {
                case EMPTY:
                case データなし:
                case データ無し:
                case 内水面:
//                    height = -50;
                    /* fall through */
                case 海水面:
                    break;
                case 地表面:
                case 表層面:
                case その他:
                    double slope = slope(db, pp, qq, x, y);
                    double shading = Math.max(0.0, Math.sin(slope * 2D + SIN45));
                    Color cc = Color.getHSBColor((float)HeightColor.HEIGHT_COLOR.hue(height), 1f, (float)shading);
//                    Color c = HeightColor.HEIGHT_COLOR.get(height);
//                    float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
//                    Color cc = Color.getHSBColor(hsv[0], hsv[1], (float)(hsv[2] * shading));
                    g.setColor(cc);
                    g.fillRect(x, y, 1, 1);
                    break;
                }
            }
    }

    static void write(DB db, long key, File outFile) throws IOException {
        BufferedImage image= new BufferedImage(
            DB.ENTRY_SIZE, DB.ENTRY_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.createGraphics();
        try (Closeable g = () -> graphics.dispose()) {
            draw(db, key, graphics);
        }
        ImageIO.write(image, "png", outFile);
    }

    public static void render(DB db, File outDir) throws IOException {
        logger.info("start");
        if (!outDir.exists())
            outDir.mkdirs();
        for (long key : db.keys()) {
            String fileName = db.file(key).getName().replaceFirst("\\.bin$", ".png");
            logger.info(fileName);
            write(db, key, new File(outDir, fileName));
        }
        logger.info("end");
    }
}