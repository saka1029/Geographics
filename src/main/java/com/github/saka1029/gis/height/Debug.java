package com.github.saka1029.gis.height;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.github.saka1029.gis.common.Logging;

public class Debug {

    static Logger logger = Logging.logger(Debug.class);

    static void draw(BufferedImage image, int i, int j,
        HeightEnum type, double height, double min, double max) {
        double range = max - min;
        Color color;
        switch (type) {
        case EMPTY:
            color = Color.PINK;
            break;
        case 地表面:
            int b = (int) ((height - min) / range * 255);
            color = new Color(b, b, b);
            break;
        case 表層面:
            color = Color.GREEN;
            break;
        case 海水面:
            color = Color.BLUE;
            break;
        case 内水面:
            color = Color.MAGENTA;
            break;
        case その他:
            color = Color.RED;
            break;
        case データなし:
        case データ無し:
        default:
            color = Color.YELLOW;
            break;
        }
        image.setRGB(i, j, color.getRGB());
    }

    static void draw(DB db, long key, BufferedImage image) {
        long ppp = db.pp(key) * DB.ENTRY_SIZE;
        long qqq = db.qq(key) * DB.ENTRY_SIZE;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < DB.ENTRY_SIZE; ++i) {
            for (int j = 0; j < DB.ENTRY_SIZE; ++j) {
                int typeHeight = db.get(ppp + i, qqq + j);
                HeightEnum type = HeightEnum.type(typeHeight);
                if (type == HeightEnum.地表面) {
                    double height = HeightEnum.height(typeHeight);
                    if (height < min)
                        min = height;
                    if (height > max)
                        max = height;
                }
            }
        }
        for (int i = 0; i < DB.ENTRY_SIZE; ++i)
            for (int j = 0; j < DB.ENTRY_SIZE; ++j) {
                int typeHeight = db.get(ppp + i, qqq + j);
                HeightEnum type = HeightEnum.type(typeHeight);
                double height = HeightEnum.height(typeHeight);
                draw(image, i, j, type, height, min, max);
            }
    }

    static void write(DB db, long key, File outFile) throws IOException {
        logger.info(outFile.getName());
        BufferedImage image= new BufferedImage(
            DB.ENTRY_SIZE, DB.ENTRY_SIZE, BufferedImage.TYPE_INT_ARGB);
        draw(db, key, image);
        ImageIO.write(image, "png", outFile);
    }

    static void renderEmptyImage(File outFile) throws IOException {
        BufferedImage image= new BufferedImage(
            DB.ENTRY_SIZE, DB.ENTRY_SIZE, BufferedImage.TYPE_INT_ARGB);
        int rgb = HeightColor.TRANSPARENT_COLOR.getRGB();
        for (int x = 0; x < DB.ENTRY_SIZE; ++x)
            for (int y = 0; y < DB.ENTRY_SIZE; ++y)
                image.setRGB(x, y, rgb);
        ImageIO.write(image, "png", outFile);
    }

    public static void renderHtml(DB db, File outDir) throws IOException {
        logger.info("start");
        long minpp = Long.MAX_VALUE, maxpp = Long.MIN_VALUE;
        long minqq = Long.MAX_VALUE, maxqq = Long.MIN_VALUE;
        int count = 0;
        for (long key : db.keys()) {
            long pp = db.pp(key);
            long qq = db.qq(key);
            minpp = Math.min(minpp, pp);
            maxpp = Math.max(maxpp, pp);
            minqq = Math.min(minqq, qq);
            maxqq = Math.max(maxqq, qq);
            ++count;
        }
        if (minpp == Long.MAX_VALUE)
            throw new IllegalStateException("db empty");
        renderEmptyImage(new File(outDir, "empty.png"));
        boolean[][] exists = new boolean[(int)(maxqq - minqq + 1)][(int)(maxpp - minpp + 1)];
        logger.info("image count = " + count);
        logger.info("table size = (" + exists.length + ", " + exists[0].length + ")");
        for (long key : db.keys()) {
            long pp = db.pp(key);
            long qq = db.qq(key);
            exists[(int)(qq - minqq)][(int)(pp - minpp)] = true;
        }
        try (Writer fw = new FileWriter(new File(outDir, "index.html"));
            PrintWriter out = new PrintWriter(fw)) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<style>");
            out.println("img { vertical-align:top; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
            for (long i = minqq; i <= maxqq; ++i) {
                out.println("<tr>");
                for (long j = minpp; j < maxpp; ++j) {
                    if (exists[(int)(i - minqq)][(int)(j - minpp)])
                        out.printf("<td><img src=\"%s\"></td>%n",
                            db.file(db.key(j, i)).getName().replaceFirst("\\.bin$", ".png"));
                    else
                        out.printf("<td><img src=\"empty.png\"></td>%n");
                }
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        }
        logger.info("end");
    }

    public static void dump(DB db, File outDir) throws IOException {
        logger.info("start db = " + db.baseDir + " outDir = " + outDir);
        if (!outDir.exists())
            outDir.mkdirs();
        for (long key : db.keys()) {
            String fileName = db.file(key).getName().replaceFirst("\\.bin$", ".png");
            write(db, key, new File(outDir, fileName));
        }
        logger.info("end");
    }
}