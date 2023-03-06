package io.gamioo.nav;

import io.gamioo.nav.util.FileUtils;
import io.gamioo.nav.util.NativeUtils;
import io.gamioo.nav.vector.Vector3f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 3D NavMesh 寻路导航
 *
 * @author Allen Jiang
 */
public class NavEngine implements INav {
    private static final Logger logger = LogManager.getLogger(NavEngine.class);
    private int id;
    private float[] extents = {1.f, 1.f, 1.f};


    static {
        try {
            NativeUtils.loadLibrary("recast");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }

    }


    public void init(int id, String filePath) throws IOException {
        File file = FileUtils.getFile(filePath);
        byte[] content = FileUtils.readFileToByteArray(file);
        this.id = this.load(id, content, content.length);
        if (this.id < 0) {
            throw new IOException("load map failed,mapId" + id);
        }
    }


    /**
     * 加载地图
     *
     * @param navmeshId 寻路数据地图ID
     * @param content   地图文件的路径例
     * @param length    数据长度
     * @return navmeshId, 为0或负数表示加载失败，为正数表示加载成功，后续寻路时传入此id为参数
     */
    public native int load(int navmeshId, byte[] content, int length);

    /**
     * 寻路
     *
     * @param navmeshId 寻路数据地图ID
     * @param startX    起始点X
     * @param startY    起始点Y
     * @param startZ    起始点Z
     * @param endX      结束点X
     * @param endY      结束点Y
     * @param endZ      结束点Z
     * @return 返回路径点列表，注意，查找不到时，会返回空
     */
    public native float[] find(int navmeshId, float startX, float startY, float startZ, float endX, float endY, float endZ);

    /**
     * 找到目标点最近的静态可达点
     *
     * @param navmeshId 寻路数据地图ID
     * @param pointX    参考点X
     * @param pointY    参考点Y
     * @param pointZ    参考点Z
     * @return 如果目标点可达，返回目标点即可， 如果搜索范围内没有，返回空
     */
    public native float[] findNearest(int navmeshId, float pointX, float pointY, float pointZ);


    /**
     * 光线照射发，寻找可以支线通过的hit点，如果可通过则返回hit
     *
     * @param navmeshId 寻路数据地图ID
     * @param startX    起始点X
     * @param startY    起始点Y
     * @param startZ    起始点Z
     * @param endX      结束点X
     * @param endY      结束点Y
     * @param endZ      结束点Z
     * @return 返回射线射过去遇到的第一个阻挡点，如果到终点都没有阻挡，返回终点
     */
    public native float[] raycast(int navmeshId, float startX, float startY, float startZ, float endX, float endY, float endZ);


    /**
     * 释放加载的地图数据
     *
     * @param navmeshId 寻路数据地图ID
     */
    public native void release(int navmeshId);


    /**
     * 释放加载的所有地图数据
     */
    public native void releaseAll();


    @Override
    public List<float[]> find(float[] start, float[] end) {
        List<float[]> ret = new ArrayList<>();
        float[] array = this.find(this.id, start[0], start[1], start[2], end[0], end[1], end[2]);
        if (array == null) {
            return ret;
        }
        int length = array.length / 3;
        for (int i = 0; i < length; i++) {
            float[] point = new float[3];
            int index = i * 3;
            point[0] = array[index];
            point[1] = array[index + 1];
            point[2] = array[index + 2];
            ret.add(point);
        }
        return ret;
    }

    @Override
    public List<Vector3f> find(Vector3f start, Vector3f end) {
        List<float[]> paths = find(vector2Point(start), vector2Point(end));
        return paths.stream().map(p -> new Vector3f(p[0], p[1], p[2])).collect(Collectors.toList());
    }


    @Override
    public float[] raycast(float[] start, float[] end) {
        return this.raycast(id, start[0], start[1], start[2], end[0], end[1], end[2]);
    }

    @Override
    public Vector3f raycast(Vector3f start, Vector3f end) {
        Vector3f ret = null;
        float[] point = this.raycast(vector2Point(start), vector2Point(end));
        if (point != null) {
            ret = point2Vector(point);
        }
        return ret;
    }


    @Override
    public float[] findNearest(float[] point) {
        return this.findNearest(id, point[0], point[1], point[2]);
    }

    @Override
    public Vector3f findNearest(Vector3f point) {
        Vector3f ret = null;
        float[] p = this.findNearest(vector2Point(point));
        if (p != null) {
            ret = point2Vector(p);
        }
        return ret;
    }
}
