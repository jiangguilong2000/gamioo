[![build](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml/badge.svg)](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/jiangguilong2000/gamioo-navigation/branch/main/graph/badge.svg?token=QBSoQmUNnn)](https://codecov.io/gh/jiangguilong2000/gamioo-navigation)
[![GitHub release](https://img.shields.io/github/release/jiangguilong2000/gamioo-navigation.svg)](https://github.com/jiangguilong2000/gamioo-navigation/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/jiangguilong2000/gamioo-navigation.svg?style=flat-square)](https://github.com/jiangguilong2000/gamioo-navigation/commits)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![license](https://img.shields.io/badge/license-MulanPSL-blue)](http://license.coscl.org.cn/MulanPSL)

# 简介

📌 压缩相关

* 压缩算法
    * zstandard
    * zlib

#### 📄 性能测试结果如下：

```bash
Benchmark                               Mode  Cnt          Score          Error  Units
CompressBenchMark.zlibCompress         thrpt   10      88715.804 ±     7722.750  ops/s
CompressBenchMark.zlibDecompress       thrpt   10  469560721.886 ± 12731671.970  ops/s
CompressBenchMark.zstandardCompress    thrpt   10     192338.673 ±    19923.725  ops/s
CompressBenchMark.zstandardDecompress  thrpt   10  477850633.433 ±  3949541.241  ops/s
```

在Windows下(4核8线程 Intel Core i7),很明显，

- 压缩API,zstandard比zlib性能达到了 216.8%;
- 解压缩API,zstandard比zlib性能达到了101.8%;

### 依赖&参考

## TODO list

