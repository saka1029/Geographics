package com.github.saka1029.gis.height;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.github.saka1029.gis.common.Logging;

public class Renderer {

    static Logger logger = Logging.logger(Renderer.class);

    /** メッシュサイズ(5m) */
    static final double CELL_SIZE = 1.0;
    /** 傾斜時の水平距離 */
    static final double K = CELL_SIZE * Math.sqrt(2D) / 2D;
    /** 傾斜度計算時の参照セル範囲（対象セル±SHADING_RANGEの範囲で計算） */
    static final int SHADING_RANGE = 2;
    /** 光源の角度 */
    static final double 入射角 = 40.0 * Math.PI / 180.0;

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
                case 地表面:
                case 表層面:
                case その他:
                    height += ij < 0 ? center - h : h - center;
                    distance += Math.abs(ij) * K;
                    break;
                default:
                    break;
                }
            }
        return Math.atan(height / distance);
    }

    static void draw(DB db, long key, BufferedImage image) {
        long pp = db.pp(key) * DB.ENTRY_SIZE;
        long qq = db.qq(key) * DB.ENTRY_SIZE;
        for (int x = 0; x < DB.ENTRY_SIZE; ++x)
            for (int y = 0; y < DB.ENTRY_SIZE; ++y) {
                int typeHeight = db.get(pp + x, qq + y);
                HeightEnum type = HeightEnum.type(typeHeight);
                double height = HeightEnum.height(typeHeight);
                switch (type) {
                case 地表面:
                case 表層面:
                case その他:
                    double slope = slope(db, pp, qq, x, y);
                    double deg = 1.2 * slope + 入射角;
//                    double deg = 2.0 * slope + 入射角;
//                    double deg = - slope * 2.0 - 入射角 + Math.PI / 2.0;
                    double shading = Math.max(0.0, Math.sin(deg));
//                    if (slope < 0 && shading == 0)
//                        logger.finest("slope=" + slope + " deg=" + deg);
                    Color cc = Color.getHSBColor((float)HeightColor.HEIGHT_COLOR.hue(height), 1f, (float)shading);
                    image.setRGB(x, y, cc.getRGB());
                    break;
                }
            }
    }

    static void write(DB db, long key, File outFile) throws IOException {
        BufferedImage image= new BufferedImage(
            DB.ENTRY_SIZE, DB.ENTRY_SIZE, BufferedImage.TYPE_INT_ARGB);
        draw(db, key, image);
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
