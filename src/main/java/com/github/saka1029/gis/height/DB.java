package com.github.saka1029.gis.height;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 高度のデータベースです。
 *
 */
public class DB implements Closeable {

    public static final int ENTRY_SIZE = GoogleMaps.IMAGE_SIZE;

    static FileFilter fileFilter(int z) {
        return f -> f.getName().matches("\\d+-\\d+-" + z + ".bin");
    }

    /**
     * データベースのディレクトリです。
     * このディレクトリの下にあるファイル"pp-qq-z.bin"がエントリです。
     * */
    final File baseDir;
    /** GoogleMapsのズームレベルです。 */
    final int z;
    /** 全てのEntryのキーです。 */
    final Set<Long> keys;
    /** キャッシュです。 */
    final Map<Long, Rec> cache;

    public DB(File baseDir, int z, int cacheSize) {
        if (cacheSize <= 0)
            throw new IllegalArgumentException("cacheSize");
        this.baseDir = baseDir;
        if (!baseDir.exists()) baseDir.mkdirs();
        this.z = z;
        this.cache = new LinkedHashMap<Long, Rec>() {
            /**
             * キャッシュサイズを超えた時、最も過去にアクセスされたEntryを
             * ディスクに書き込んで、キャッシュから除外します。
             */
            protected boolean removeEldestEntry(Map.Entry<Long, Rec> eldest) {
                if (size() <= cacheSize) return false;
                Rec entry = eldest.getValue();
                try {
                    entry.close();// 削除エントリをディスクに保存します。
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        };
        // ディスクに存在する全てのEntryのキーだけをキャッシュに格納します。
        this.keys = new HashSet<>();
        for (File file : baseDir.listFiles(fileFilter(z)))
            this.keys.add(key(file));
    }

    /** 参照可能なkeyの一覧を返します。 */
    public Iterable<Long> keys() {
        return keys;
    }

    /** ppとqqからkeyを求めます。 */
    public long key(long pp, long qq) { return ((long)pp) << 32 | qq; }
    /** keyからppを求めます。 */
    public int pp(long key) { return (int)(key >> 32); }
    /** keyからqqを求めます。 */
    public int qq(long key) { return (int)key & 0xFFFFFFFF; }

    /** Fileをkeyに変換します。*/
    public long key(File file) {
        String[] nums = file.getName().split("-");
        return key(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
    }

    /** keyをFileに変換します。 */
    public File file(long key) {
        return new File(baseDir, String.format("%d-%d-%d.bin", pp(key), qq(key), z));
    }

    /** 経度をGoogleMapsの経度座標値(ppp)に変換します。 */
    public long ppp(double lon) { return GoogleMaps.p(lon, z); }
    /** 緯度をGoogleMapsの緯度座標値(qqq)に変換します。 */
    public long qqq(double lat) { return GoogleMaps.q(lat, z); }

    public int get(long ppp, long qqq) {
        long pp = ppp / ENTRY_SIZE;
        long qq = qqq / ENTRY_SIZE;
        long key = key(pp, qq);
        if (!keys.contains(key)) return 0;
        Rec entry = cache.computeIfAbsent(key, k -> new Rec(file(k), true));
        int p = (int)ppp % ENTRY_SIZE;
        int q = (int)qqq % ENTRY_SIZE;
        return entry.get(p, q);
    }

    /**
     * 緯度経度から高さを返します。
     *
     * @param lon 経度を指定します。
     * @param lat 緯度を指定します。
     * @return 高さを返します。返された値をhとすると
     *         HeightEnum.type(h)で高さの種類が求められます。
     *         HeightEnum.height(h)で高さ(double)が求められます。
     *         データーベースに該当する値が存在しない場合は0を返します。
     */
    public int get(double lon, double lat) {
        return get(ppp(lon), qqq(lat));
    }

    public void put(long ppp, long qqq, int value) {
        long pp = ppp / ENTRY_SIZE;
        long qq = qqq / ENTRY_SIZE;
        long key = key(pp, qq);
        File file = file(key);
        Rec entry;
        if (keys.contains(key))
            entry = cache.computeIfAbsent(key, k -> new Rec(file, true));
        else {
            cache.put(key, entry = new Rec(file, false));
            keys.add(key);
        }
        int p = (int)ppp % ENTRY_SIZE;
        int q = (int)qqq % ENTRY_SIZE;
        entry.set(p, q, value);
    }

    @Override
    public String toString() {
        return "DB(" + baseDir + " z=" + z + ")";
    }

    /** DBをクローズします。ディスクに反映されていないキャッシュのエントリがあれば書き込みます。 */
    @Override
    public void close() throws IOException {
        for (Rec e : cache.values())
            if (e != null)
                e.close();
    }
}

class Rec implements Closeable {

    final File file;
    final ByteBuffer buffer;
    final IntBuffer points;
    boolean dirty;

    public Rec(File file, boolean read) {
        this.file = file;
        this.buffer = ByteBuffer.allocateDirect(DB.ENTRY_SIZE * DB.ENTRY_SIZE * Integer.BYTES);
        this.points = buffer.asIntBuffer();
        if (!read) return;
        try (FileChannel channel = FileChannel.open(file.toPath(),
            StandardOpenOption.READ)) {
            buffer.clear();
            while (buffer.position() < buffer.limit())
                channel.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.dirty = false;
    }

    static int index(int p, int q) {
        return p * DB.ENTRY_SIZE + q;
    }

    int get(int p, int q) {
        return points.get(index(p, q));
    }

    void set(int p, int q, int value) {
        points.put(index(p, q), value);
        dirty = true;
    }

    @Override
    public String toString() {
        return "Entry(" + file.getName() + ")";
    }

    @Override
    public void close() throws IOException {
        if (!dirty) return;
        try (FileChannel channel = FileChannel.open(file.toPath(),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            buffer.clear();
            channel.write(buffer);
        }
        dirty = false;
    }
}