package com.github.saka1029.gis.height;

import java.io.IOException;
import java.util.logging.Logger;

import com.github.saka1029.gis.common.Logging;

public class Interpolator {

    static Logger logger = Logging.logger(Interpolator.class);

    /**
     *
     * @param db 高さのデータベースです。
     * @param ppp GoogleMapsのピクセル経度座標値です。
     * @param qqq GoogleMapsのピクセル緯度座標値です。
     * @param counter 補間領域のHeightEnumごとの点の数にウェイトを掛けたものです。
     * @param heights 補間領域のHeightEnumごとの点の高さにウェイトを掛けたものです。
     * @return 補間した結果のHeightEnumと高さを返します。
     */
    static int interpolate(DB db, long ppp, long qqq, double[] counter, double[] heights) {
        double maxValue = 0;
        int maxIndex = 0;
        for (int i = 0; i < counter.length - 1; ++i)
            if (counter[i] >= maxValue) {
                maxIndex = i;
                maxValue = counter[i];
            }
        HeightEnum type = HeightEnum.values()[maxIndex];
        return type.typeHeight(heights[maxIndex] / maxValue);
    }

    /**
     * 補間する近傍領域のサイズです。
     * 縦(NEAR * 2 + 1) × 横(NEAR * 2 + 1)が近傍領域となります。
     */
    static int NEAR = 1;
//    static int NEAR = 2;
//    static int NEAR = 3;

    /**
     * 補間する近傍領域の中で有効なデータの数がこの数以上の場合のみ補間を行います。
     * この数未満の場合は補間しません。
     */
    static int MIN_COUNT = (2 * NEAR + 1) * (2 * NEAR + 1) / 2;

    static void interpolate(DB db, long ppp, long qqq) {
        if (db.get(ppp, qqq) != 0) return;  // データがない点だけを対象とします。
        double[] counter = new double[HeightEnum.size];
        double[] heights = new double[HeightEnum.size];
        int count = 0;
        for (int m = -NEAR; m <= NEAR; ++m)
            for (int n = -NEAR; n <= NEAR; ++n) {
                int typeHeight = db.get(ppp + m, qqq + n);
                if (typeHeight == 0) continue;  // データがある点だけを補間対象とします。
                ++count;
                HeightEnum type = HeightEnum.type(typeHeight);
                double height = HeightEnum.height(typeHeight);
                double weight = 1D / Math.hypot(m, n);
                counter[type.value] += weight;
                heights[type.value] += height * weight;
            }
        if (count >= MIN_COUNT)
            db.put(ppp, qqq, interpolate(db, ppp, qqq, counter, heights));
    }

    static void interpolate(DB db, long key, int refSize) {
        logger.info(db.file(key).getName());
        long ppp = db.pp(key) * DB.ENTRY_SIZE;
        long qqq = db.qq(key) * DB.ENTRY_SIZE;
        for (int i = 0; i < DB.ENTRY_SIZE; ++i)
            for (int j = 0; j < DB.ENTRY_SIZE; ++j)
                interpolate(db, ppp + i, qqq + j);
    }

    public static void interpolate(DB database) throws IOException {
        logger.info("start");
        for (long key : database.keys())
            interpolate(database, key, 1);
        logger.info("end");
    }
}
