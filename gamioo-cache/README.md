[![build](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml/badge.svg)](https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/jiangguilong2000/gamioo-navigation/branch/main/graph/badge.svg?token=QBSoQmUNnn)](https://codecov.io/gh/jiangguilong2000/gamioo-navigation)
[![GitHub release](https://img.shields.io/github/release/jiangguilong2000/gamioo-navigation.svg)](https://github.com/jiangguilong2000/gamioo-navigation/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/jiangguilong2000/gamioo-navigation.svg?style=flat-square)](https://github.com/jiangguilong2000/gamioo-navigation/commits)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![license](https://img.shields.io/badge/license-MulanPSL-blue)](http://license.coscl.org.cn/MulanPSL)

# 简介

📌 压缩相关

* 压缩算法
    * caffeine
    * guava

* 如何使用

```bash
implementation group: 'io.gamioo', name: 'gamioo-cache', version: '0.2.11'
```

#### 📄 性能测试结果如下：

```log
Benchmark                    Mode  Cnt         Score         Error  Units
CacheBenchMark.caffeineGet  thrpt   10  33895330.174 ± 2404744.750  ops/s
CacheBenchMark.caffeinePut  thrpt   10    754213.850 ±  930595.062  ops/s
CacheBenchMark.guavaGet     thrpt   10  26574517.316 ±  958336.930  ops/s
CacheBenchMark.guavaPut     thrpt   10    411722.523 ±  531660.440  ops/s
```

在Windows下(4核8线程 Intel Core i7),很明显，

- 存入API,caffeine 比 guava 性能达到了 183.2%;
- 获取API,caffeine 比 guava 性能达到了 127.6%;

### 依赖&参考

dependncy :
jdk:

```bash
OpenJDK Runtime Environment (Tencent Kona 8.0.12) (build 1.8.0_352-b1)
OpenJDK 64-Bit Server VM (Tencent Kona 8.0.12) (build 25.352-b1, mixed mode, sharing)
```

## TODO list

