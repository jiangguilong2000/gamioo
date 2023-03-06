[![build](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml/badge.svg)](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/jiangguilong2000/gamioo-navigation/branch/main/graph/badge.svg?token=QBSoQmUNnn)](https://codecov.io/gh/jiangguilong2000/gamioo-navigation)
[![GitHub release](https://img.shields.io/github/release/jiangguilong2000/gamioo-navigation.svg)](https://github.com/jiangguilong2000/gamioo-navigation/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/jiangguilong2000/gamioo-navigation.svg?style=flat-square)](https://github.com/jiangguilong2000/gamioo-navigation/commits)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![license](https://img.shields.io/badge/license-MulanPSL-blue)](http://license.coscl.org.cn/MulanPSL)



# 简介

📌 研究寻路导航相关

* 寻路算法
    * NavMesh
    * A<sup>*</sup>

<p align="center">
  <img src="https://img-blog.csdnimg.cn/c4f2795ea1974d57a90a987aa5bea463.png" width="800">
</p>

```java
class Main {
    public static void main(String[] args) {
      NavEngine nav = new NavEngine();
        nav.init(1, "solo_navmesh.bin");
        float[] src = new float[]{-49.7f, 0.2f, 135.5f};
        float[] end = new float[]{-106.3f, 0.5f, 77.2f};
        List<float[]> array = nav.find(src, end);
    }
}

```

#### 📄寻路结果如下:

```

[[-49.7, 0.4, 135.5], [-66.79999, 1.0, 135.6], [-69.2, 0.8, 134.70001], [-70.7, 0.6, 132.6]
, [-73.99999, 0.2, 127.8], [-106.3, 0.78799236, 77.2]]

```

#### 📄 性能测试结果如下：

```bash
Benchmark                              Mode  Cnt        Score        Error  Units
NavEngineBenchMark.javaFind           thrpt   10    23907.038 ±   3651.774  ops/s
NavEngineBenchMark.javaFindNearest    thrpt   10   308210.438 ± 169221.381  ops/s
NavEngineBenchMark.javaRaycast        thrpt   10   268622.498 ±  45903.743  ops/s
NavEngineBenchMark.nativeFind         thrpt   10    55728.163 ±  12648.708  ops/s
NavEngineBenchMark.nativeFindNearest  thrpt   10  1016350.479 ± 201328.309  ops/s
NavEngineBenchMark.nativeRaycast      thrpt   10   692069.731 ± 237797.318  ops/s
```

在Windows下(4核8线程 Intel Core i7),很明显，

- 寻路API,性能达到了原先的206%;
- 光线照反射API，性能达到了原先的224.5%;
- 寻找最近可通点API,性能达到了原先的276%
-

### 依赖&参考

https://github.com/SilenceSu/Easy3dNav

## TODO list

* 寻路算法
    * A<sup>*</sup>