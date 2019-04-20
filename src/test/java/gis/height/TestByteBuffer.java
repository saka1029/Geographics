package gis.height;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.junit.Test;

public class TestByteBuffer {

    byte[] write(float a, float b, float c, ByteOrder o) {
        ByteBuffer bb = ByteBuffer.allocate(Float.BYTES * 3);
        bb.order(o);
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(a).put(b).put(c);
        fb.flip();
        return bb.array();
    }

    @Test
    public void testByteBuffer() {
        float a = 1.0F, b = 2.0F, c = 3.0F;
        System.out.println(Arrays.toString(write(a, b, c, ByteOrder.LITTLE_ENDIAN)));
        System.out.println(Arrays.toString(write(a, b, c, ByteOrder.BIG_ENDIAN)));
    }

}
