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
Benchmark                               Mode  Cnt           Score          Error  Units
CompressBenchMark.zlibCompress         thrpt   10       42671.817 ±     2112.154  ops/s
CompressBenchMark.zlibDecompress       thrpt   10  2366646909.611 ± 43144539.607  ops/s
CompressBenchMark.zstandardCompress    thrpt   10      126078.294 ±    10863.591  ops/s
CompressBenchMark.zstandardDecompress  thrpt   10  2133946821.515 ± 96154271.597  ops/s
```

在Windows下(4核8线程 Intel Core i7),很明显，

- 压缩API,zstandard比zlib性能达到了 216.8%;
- 解压缩API,zstandard比zlib性能达到了101.8%;

### 依赖&参考

dependncy :
jdk:

```bash
OpenJDK Runtime Environment (Tencent Kona 8.0.12) (build 1.8.0_352-b1)
OpenJDK 64-Bit Server VM (Tencent Kona 8.0.12) (build 25.352-b1, mixed mode, sharing)
```

lib:

```bash
group: 'com.github.luben', name: 'zstd-jni', version: '1.5.4-2'
```

压缩文本的样本(1038 bytes)如下:
<details>
<summary>展开查看</summary>
<pre><code>
{code {
  flag: 1
  id: 1
}
tableId: 936940
ownerId: 143566
createId: 143566
roomTemplateId: 4
configTemplateId: 1101
entryDTO {
  key: 1
  value: 3
}
entryDTO {
  key: 201
  value: 0
}
entryDTO {
  key: 202
  value: 0
}
entryDTO {
  key: 204
  value: 1
}
entryDTO {
  key: 4
  value: 6
}
entryDTO {
  key: 203
  value: 0
}
playerDTO {
  playerDTO {
    id: 143566
    name: "King\345\274\272"
    gender: 1
    icon: "http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLLMzUbUh9ic7fQlhibCCLnibAIAP838Xge2cmFcStdEaWLL4UdLrgzhZsxrcsYxgJLsDR39vPsfjLibw/132"
    city: "\345\256\201\346\263\242\345\270\202"
    ip: "39.188.248.167"
    longitude: "0.0"
    latitude: "0.0"
    position: 0
    ready: false
    online: true
    totalPoint: 0.0
    lastEnterTime: 1595855143603
    win: 0
    lose: 0
    type: 1
  }
  sitDownPosition: 0
  remain: 0
  score: 0
  daoNum: 0.0
  totalDaoNum: 0.0
  rank: 0
}
clubId: 0
status: 0
sitDownPosition: 0
kingBormPokerDTO {
  id: 143566
}
}
</code></pre>
</details>

## TODO list

