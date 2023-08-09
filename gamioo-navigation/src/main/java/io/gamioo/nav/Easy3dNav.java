package io.gamioo.nav;

import com.github.silencesu.Easy3dNav.EasyNavFunc;
import com.github.silencesu.Easy3dNav.Vector3f;
import com.github.silencesu.Easy3dNav.detour.*;
import com.github.silencesu.Easy3dNav.detour.io.MeshSetReader;
import com.github.silencesu.Easy3dNav.detour.io.MeshSetReaderU3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 3d游戏服务端寻路组件
 *
 * @author Allen Jiang
 */
public class Easy3dNav implements EasyNavFunc {

    private static Logger LOGGER = LoggerFactory.getLogger(com.github.silencesu.Easy3dNav.Easy3dNav.class);


    /**
     * 是否使用U3d插件CritterAI导出的格式
     */
    private boolean useU3dData = true;
    /**
     * 是否打印地图信息
     */
    private boolean printMeshInfo = false;

    private NavMeshQuery query;
    private QueryFilter filter;

    private float[] extents = {2.f, 2.f, 2.f};


    public Easy3dNav() {

    }

    /**
     * 修改配置 构造
     *
     * @param useU3dData    是否使用critterAi数据
     * @param printMeshInfo 是否打印地图西信息
     */
    public Easy3dNav(boolean useU3dData, boolean printMeshInfo) {
        this.useU3dData = useU3dData;
        this.printMeshInfo = printMeshInfo;
    }

    //navmesh 文件路径
    public Easy3dNav(String filePath) throws IOException {
        init(filePath);
    }


    /**
     * 初始化寻路需要参数
     *
     * @param filePath
     */
    public void init(String filePath) throws IOException {

        NavMesh mesh = loadNavMesh(filePath);

        query = new NavMeshQuery(mesh);
        filter = new QueryFilter();

        if (printMeshInfo) {
            printMeshInfo(mesh);
        }

    }

    private NavMesh loadNavMesh(String meshFile) throws IOException {
        InputStream inputStream = null;
        NavMesh mesh;
        try {
            //获取文件流
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(meshFile);
            if (useU3dData) {
                MeshSetReaderU3d reader = new MeshSetReaderU3d();
                mesh = reader.read32Bit(inputStream, 6);
            } else {
                MeshSetReader reader = new MeshSetReader();
                mesh = reader.read32Bit(inputStream, 6);
            }

            query = new NavMeshQuery(mesh);
            filter = new QueryFilter();

        } finally {
            //使用完，关闭流
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return mesh;
    }

    /**
     * 输出mesh信息
     *
     * @param mesh meshd对象
     */
    private void printMeshInfo(NavMesh mesh) {
        //输出地图基本信息
        int tileCount = 0;
        int nodeCount = 0;
        int polyCount = 0;
        int vertCount = 0;
        int triCount = 0;
        int triVertCount = 0;
        int dataSize = 0;
        for (int i = 0; i < mesh.getMaxTiles(); i++) {

            MeshTile tile = mesh.getTile(i);
            if (tile == null) {
                continue;
            }
            tileCount++;
            nodeCount += tile.data.header.bvNodeCount;
            polyCount += tile.data.header.polyCount;
            vertCount += tile.data.header.vertCount;
            triCount += tile.data.header.detailTriCount;
            triVertCount += tile.data.header.detailVertCount;


            System.out.printf("%f %f %f\n", tile.data.verts[0], tile.data.verts[1], tile.data.verts[2]);
            for (int m = 0; m < tile.data.header.detailVertCount; m++) {
                System.out.printf("%f %f %f\n", tile.data.detailVerts[m * 3], tile.data.detailVerts[m * 3 + 1], tile.data.detailVerts[m * 3 + 2]);
            }
        }

        System.out.printf("\t==> tiles loaded: %d\n", tileCount);
        System.out.printf("\t==> BVTree nodes: %d\n", nodeCount);
        System.out.printf("\t==> %d polygons (%d vertices)\n", polyCount, vertCount);

        System.out.printf("\t==> %d triangles (%d vertices)\n", triCount, triVertCount);
    }

    /**
     * 设置默认搜索范围
     *
     * @param extents 搜索范围
     */
    public void setExtents(float[] extents) {
        this.extents = extents;
    }

    /**
     * 设置是否使用critterAI导出的数据
     *
     * @param useU3dData
     *
     */
    public void setUseU3dData(boolean useU3dData) {
        this.useU3dData = useU3dData;
    }

    public void setPrintMeshInfo(boolean printMeshInfo) {
        this.printMeshInfo = printMeshInfo;
    }

    @Override
    public List<float[]> find(float[] start, float[] end, float[] extents) {


        List<float[]> pathRet = new ArrayList<>();

        //获取开始点，附近的点
        FindNearestPolyResult startResult = query.findNearestPoly(start, extents, filter);
        //获取结束点，附近的点
        FindNearestPolyResult endResult = query.findNearestPoly(end, extents, filter);

        //寻找开始点和结束点附近的多边形
        if (startResult.getNearestRef() == 0 || endResult.getNearestRef() == 0) {
            LOGGER.info("start or end point not found poly");
            return Collections.emptyList();
        }

        //获取路径ids
        FindPathResult path = query.findPath(startResult.getNearestRef(), endResult.getNearestRef(), startResult.getNearestPos(), endResult.getNearestPos(), filter);

        if (path.getStatus() != Status.SUCCSESS) {
            LOGGER.info("nav path  status is  {}", path.getStatus());
            return Collections.emptyList();
        }

        //路径平滑
        List<StraightPathItem> straightPath = query.findStraightPath(startResult.getNearestPos(), endResult.getNearestPos(), path.getRefs(), Integer.MAX_VALUE, 0);
        for (StraightPathItem straightPathItem : straightPath) {
            float[] p = new float[3];
            System.arraycopy(straightPathItem.getPos(), 0, p, 0, straightPathItem.getPos().length);
            pathRet.add(p);
        }
        return pathRet;
    }

    @Override
    public List<Vector3f> find(Vector3f start, Vector3f end, Vector3f extents) {
        List<float[]> paths = find(v3fToFArr(start), v3fToFArr(end), v3fToFArr(extents));
        return paths.stream().map(p -> new Vector3f(p[0], p[1], p[2])).collect(Collectors.toList());
    }


    @Override
    public List<float[]> find(float[] start, float[] end) {
        return find(start, end, extents);
    }

    @Override
    public List<Vector3f> find(Vector3f start, Vector3f end) {
        return find(start, end, FArrTov3f(extents));
    }


    @Override
    public float[] raycast(float[] start, float[] end, float[] extents) {
        FindNearestPolyResult startResult = query.findNearestPoly(start, extents, filter);
        if (startResult.getNearestRef() == 0) {
            throw new IllegalArgumentException("not find nearestPoly");
        }
        float[] hitPoint = null;

        RaycastHit hitReasult = query.raycast(startResult.getNearestRef(), startResult.getNearestPos(), end, filter, 0, 0);
        if (hitReasult.t > 1) {
            return end;
        } else {
            hitPoint = new float[3];
            hitPoint[0] = start[0] + (end[0] - start[0]) * hitReasult.t;
            hitPoint[1] = start[1] + (end[1] - start[1]) * hitReasult.t;
            hitPoint[2] = start[2] + (end[2] - start[2]) * hitReasult.t;
        }
        return hitPoint;
    }

    @Override
    public Vector3f raycast(Vector3f start, Vector3f end, Vector3f extents) {
        float[] point = raycast(v3fToFArr(start), v3fToFArr(end), v3fToFArr(extents));
        return FArrTov3f(point);
    }

    @Override
    public float[] raycast(float[] start, float[] end) {
        return raycast(start, end, new float[]{0.f, 2.f, 0.f});
    }

    @Override
    public Vector3f raycast(Vector3f start, Vector3f end) {
        return raycast(start, end, FArrTov3f(extents));
    }


    @Override
    public float[] findNearest(float[] point) {
        FindNearestPolyResult result = query.findNearestPoly(point, extents, filter);
        return result.getNearestPos();
    }

    @Override
    public Vector3f findNearest(Vector3f point) {
        float[] p = findNearest(v3fToFArr(point));
        return FArrTov3f(p);
    }


    @Override
    public float[] findNearest(float[] point, float[] extents) {
        FindNearestPolyResult result = query.findNearestPoly(point, extents, filter);
        return result.getNearestPos();
    }

    @Override
    public Vector3f findNearest(Vector3f point, Vector3f extents) {
        float[] p = findNearest(v3fToFArr(point), v3fToFArr(extents));
        return FArrTov3f(p);

    }

    private static float[] v3fToFArr(Vector3f vector3f) {
        float[] arr = new float[3];
        arr[0] = vector3f.getX();
        arr[1] = vector3f.getY();
        arr[2] = vector3f.getZ();
        return arr;
    }

    private static Vector3f FArrTov3f(float[] point) {
        return new Vector3f(point[0], point[1], point[2]);
    }


}

