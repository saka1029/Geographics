package com.github.saka1029.gis.height;

public enum HeightEnum {
    EMPTY(0),
    地表面(1),
    表層面(2),
    海水面(3),
    内水面(4),
    その他(5),
    データなし(6),
    データ無し(6),
    ;

    public static final int size = values().length;

    public final int value;

    private HeightEnum(int value) {
        this.value = value;
    }

    public int typeHeight(double height) {
        int cm = (int)(height * 100);
        return (value << 24) | (cm & 0xFFFFFF);
    }

    public static HeightEnum type(int typeHeight) {
        return values()[typeHeight >>> 24];
    }

    static int signExtend(int value, int bits) {
        int shift = 32 - bits;
        return value << shift >> shift;
    }

    public static double height(int typeHeight) {
        return signExtend(typeHeight & 0x00FFFFFF, 24) / 100.0;
    }
}
