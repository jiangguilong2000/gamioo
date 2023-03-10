package io.gamioo.nav;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        float[] extents = {1.f, 1.f, 1.f};
        String navFilePath = "solo_navmesh.bin";
        //初始化寻路对象
        Easy3dNav easyNav = new Easy3dNav();
        //默认为true，可以忽略
        easyNav.setUseU3dData(false);
        //默认为false，查看需要设置为true
        easyNav.setPrintMeshInfo(false);
        easyNav.setExtents(extents);
//        File file = FileUtils.getFile(navFilePath);
//        assert file != null;
        easyNav.init(navFilePath);
    }
}
