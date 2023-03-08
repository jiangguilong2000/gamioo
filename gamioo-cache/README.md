<p align="center">
  <img src="https://img-blog.csdnimg.cn/0a3678d0638342039887166f68c8d995.png" width="100">
</p>
<p align="center">
	<strong>Cache, so easy.</strong>
</p>
<p align="center">
	<a target="_blank" href="https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml">
		<img src="https://github.com/jiangguilong2000/gamioo-navigation/actions/workflows/gradle.yml/badge.svg" ></img>
	</a>
	<a target="_blank" href="https://codecov.io/gh/jiangguilong2000/gamioo-navigation">
		<img src="https://codecov.io/gh/jiangguilong2000/gamioo-navigation/branch/main/graph/badge.svg?token=QBSoQmUNnn" ></img>
	</a>
	<a target="_blank" href="https://github.com/jiangguilong2000/gamioo-navigation/releases">
		<img src="https://img.shields.io/github/release/jiangguilong2000/gamioo-navigation.svg" ></img>
	</a>
	<a target="_blank" href="https://github.com/jiangguilong2000/gamioo-navigation/commits">
		<img src="https://img.shields.io/github/last-commit/jiangguilong2000/gamioo-navigation.svg?style=flat-square" ></img>
	</a>
	<a target="_blank" href="https://gamioo.wiki" title="参考文档">
		<img src="https://img.shields.io/badge/Docs-latest-blueviolet.svg" ></img>
	</a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html" >
		<img src="https://img.shields.io/badge/JDK-1.8%2B-green.svg" ></img>
	</a>
	<a href="https://www.apache.org/licenses/LICENSE-2.0.html">
		<img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg" />
	</a>
	<a target="_blank" href='https://github.com/jiangguilong2000/gamioo'>
		<img src="https://img.shields.io/github/stars/jiangguilong2000/gamioo.svg?style=social" alt="github star"></img>
	</a>
</p>
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

