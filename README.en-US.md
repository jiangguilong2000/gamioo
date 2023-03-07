<p align="center">
  <img src="https://img-blog.csdnimg.cn/0a3678d0638342039887166f68c8d995.png" width="100">
</p>
<p align="center">
	<strong>Game , so easy.</strong>
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
	<a target="_blank" href="https://justauth.wiki" title="参考文档">
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

`gamioo`, as you see, It is a Game server framework, based on this framework, you can quickly implement a highly available, easy to maintain, stable and high-performance game server.

Source Code：[gitee](https://gitee.com/yadong.zhang/JustAuth) | [github](https://github.com/zhangyd-c/JustAuth)    
Docs：[Reference Doc](https://justauth.wiki)

## Features

1. **Multiple platform**: Has integrated more than a dozen third-party platforms.([plan](https://gitee.com/yadong.zhang/JustAuth/issues/IUGRK))
2. **Minimalist**: The minimalist design is very simple to use.

## Quick start

### Add maven dependency

- Add JustAuth dependency

These artifacts are available from Maven Central:

```xml

<dependency>
    <groupId>me.zhyd.oauth</groupId>
    <artifactId>JustAuth</artifactId>
    <version>{latest-version}</version>
</dependency>
```

> **latest-version** ：
> - CURRENT: ![](https://img.shields.io/github/v/release/justauth/JustAuth?style=flat-square)
> - SNAPSHOT: ![](https://img.shields.io/nexus/s/https/oss.sonatype.org/me.zhyd.oauth/JustAuth.svg?style=flat-square)

- Add http dependency（Only need one）

> If there is already in the project, please ignore it. In addition, you need to pay special attention. If the low version of the dependency has been introduced in the project, please exclude the low
> version of the dependency first, and then introduce the high version or the latest version of the dependency

- hutool-http

  ```xml
  <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-http</artifactId>
      <version>5.2.5</version>
  </dependency>
  ```

- httpclient

  ```xml
  <dependency>
  	<groupId>org.apache.httpcomponents</groupId>
    	<artifactId>httpclient</artifactId>
    	<version>4.5.12</version>
  </dependency>
  ```

- okhttp

  ```xml
  <dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.4.1</version>
  </dependency>
  ```

### Using JustAuth API

#### Simple

```java
// Create authorization request
AuthRequest authRequest=new AuthGiteeRequest(AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build());
// Generate authorization page url
        authRequest.authorize("state");
// Get token and userinfo
        authRequest.login(callback);
```

#### Builder 1. Use unchanging `AuthConfig`

```java
// Create authorization request
AuthRequest authRequest=AuthRequestBuilder.builder()
        .source("github")
        .authConfig(AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build())
        .build();
```

#### Builder 2. Use dynamic `AuthConfig`

```java
// Create authorization request
AuthRequest authRequest=AuthRequestBuilder.builder()
        .source("gitee")
        .authConfig((source)->{
        // Use source to dynamically get AuthConfig
        // Here you can flexibly take the configuration from sql or take the configuration from the configuration file
        return AuthConfig.builder()
        .clientId("clientId")
        .clientSecret("clientSecret")
        .redirectUri("redirectUri")
        .build();
        })
        .build();
```

#### Builder 3. Support custom platform

```java
AuthRequest authRequest=AuthRequestBuilder.builder()
        // Key point: configure the custom implementation of AuthSource
        .extendSource(AuthExtendSource.values())
        // Enum name in AuthExtendSource
        .source("other")
        // ... Do other things
        .build();
```

## Contributions

1. Fork this project to your repository
2. Clone the project after fork.
3. Modify the code (either to fix issue, or to add new features)
4. Commit and push code to a remote repository
5. Create a new PR (pull request), and select `dev` branch
6. Waiting for author to merge

I look forward to your joining us.

## Contributors

[contributors](https://justauth.wiki/contributors.html)

## Change Logs

[CHANGELOGS](https://justauth.wiki/update.html)

## Recommend

- `spring-boot-demo` In-depth study and actual combat of spring boot projects: [https://github.com/xkcoding/spring-boot-demo](https://github.com/xkcoding/spring-boot-demo)
- `mica` Efficient Development of scaffolding by Spring Cloud: [https://github.com/lets-mica/mica](https://github.com/lets-mica/mica)
- `pig` Cosmic strongest Micro Services Certified authorized scaffolding (essential for Architects): [https://gitee.com/log4j/pig](https://gitee.com/log4j/pig)
- `SpringBlade` Complete online solution (necessary for enterprise development): https://gitee.com/smallc/SpringBlade

## References

- [The OAuth 2.0 Authorization Framework](https://tools.ietf.org/html/rfc6749)
- [OAuth 2.0](https://oauth.net/2/)
