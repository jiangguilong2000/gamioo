package io.gamioo.nav;

import com.github.silencesu.Easy3dNav.Easy3dNav;
import io.gamioo.common.util.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@DisplayName("Recast native test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NavEngineTest {
    private static final Logger logger = LogManager.getLogger(NavEngineTest.class);
    private static NavEngine nav;

    private static Easy3dNav easyNav = new Easy3dNav();
    private static String navFilePath = "solo_navmesh.bin";
    private static float[] extents = {1.f, 1.f, 1.f};

    //    private static float[] recastExtends = {0.0f, 1.0f, 0.0f};
    public static int id;

    float[] src = new float[]{-112.082f, 0.359222f, 55.5665f};
    float[] end = new float[]{-49.0154f, 0.0922241f, 104.259f};


    float[] target = new float[]{-86.6f, 1.8f, 53.6f};

    @BeforeAll
    public static void beforeAll() throws IOException {
        nav = new NavEngine();
        id = 1;
        nav.init(id, navFilePath);
        //初始化寻路对象
        easyNav = new Easy3dNav();
        //默认为true，可以忽略
        easyNav.setUseU3dData(false);
        //默认为false，查看需要设置为true
        easyNav.setPrintMeshInfo(false);
        easyNav.setExtents(extents);
        File file = FileUtils.getFile(navFilePath);
        assert file != null;
        easyNav.init(file.getAbsolutePath());
    }

    @AfterAll
    public static void afterAll() {
        nav.release(id);
        nav.releaseAll();
    }

    @DisplayName("C++寻路")
    @Test
    @Order(2)
    public void nativeFind() throws Exception {
        List<float[]> array = nav.find(src, end);
        logger.debug("c++ find point {} array={} ", array.size(), array);
    }

    @DisplayName("Java寻路")
    @Test
    @Order(3)
    public void javaFind() throws Exception {
        List<float[]> array = easyNav.find(src, end);
        logger.debug("java find point {} array={} ", array.size(), array);
    }


    @DisplayName("C++射线")
    @Test
    @Order(4)
    public void nativeRaycast() {
        float[] point = nav.raycast(src, end);
        logger.debug("c++ raycast point={}", point);
    }

    @DisplayName("java射线")
    @Test
    @Order(5)
    public void javaRaycast() {
        float[] point = easyNav.raycast(src, end);
        logger.debug("java raycast point={}", point);
    }

    @DisplayName("C++寻找最近的可通点")
    @Test
    @Order(6)
    public void nativeFindNearest() {
        float[] point = nav.findNearest(target);
        logger.debug("c++ findNearest point={}", point);
    }

    @DisplayName("java寻找最近的可通点")
    @Test
    @Order(7)
    public void javaFindNearest() {
        float[] point = easyNav.findNearest(target);
        logger.debug("java findNearest point={}", point);
    }

}
