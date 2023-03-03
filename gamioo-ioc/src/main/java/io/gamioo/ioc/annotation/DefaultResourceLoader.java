/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.ioc.annotation;

import io.gamioo.core.exception.ServerBootstrapException;
import io.gamioo.core.util.ClassUtils;
import io.gamioo.ioc.io.FileClassResource;
import io.gamioo.ioc.io.JarClassResource;
import io.gamioo.ioc.io.Resource;
import io.gamioo.ioc.io.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源读取器
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class DefaultResourceLoader implements ResourceLoader {
    private static final Logger logger = LogManager.getLogger(DefaultResourceLoader.class);
    private static final String CLASS_SUFFIX = ".class";
    private static final String PACKAGE_INFO_CLASS = "package-info.class";
    /**
     * URL protocol for a file in the file system: "file"
     */
    private static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL protocol for an entry from a jar file: "jar"
     */
    private static final String URL_PROTOCOL_JAR = "jar";


    @Override
    public List<Resource> getResourceList(String basePackage) {
        List<Resource> ret = new ArrayList<>();
        // 处理一下包名到目录
        basePackage = basePackage.replace('.', '/').replace('\\', '/').concat("/");
        try {
            Enumeration<URL> list = ClassUtils.getDefaultClassLoader().getResources(basePackage);
            while (list.hasMoreElements()) {
                URL url = list.nextElement();
                switch (url.getProtocol()) {
                    // "file"
                    case URL_PROTOCOL_FILE: {
                        ret.addAll(this.doFindFileResources(basePackage, new File(url.getFile())));
                        break;
                    }
                    // "jar"
                    case URL_PROTOCOL_JAR: {
                        ret.addAll(this.doFindJarResources(url));
                        break;
                    }
                    default:
                        break;
                }
            }

            //TODO ...


        } catch (IOException e) {
            throw new ServerBootstrapException(e, "scan process failed,basePackages={}", basePackage);
        }

        return ret;
    }


    /**
     * 递归扫描目录文件.
     *
     * @param file 文件
     */
    private List<Resource> doFindFileResources(String basePackage, File file) {
        List<Resource> ret = new ArrayList<>();
        String path = file.getAbsolutePath();
        // 这个目录不存在，忽略
        if (!file.exists()) {
            logger.debug("path:{} does not exist", path);
            return ret;
        }
        // 这个目录不可以读，忽略
        if (!file.canRead()) {
            logger.warn("the directory {} can't be read", path);
            return ret;
        }
        ret = this.listFiles(basePackage, file);
        return ret;
    }


    /**
     * 查找到一个目录.
     *
     * @param file 目录
     */
    private List<Resource> listFiles(String basePackage, File file) {
        List<Resource> ret = new ArrayList<>();
        if (file.isDirectory()) {
            File[] array = file.listFiles();
            if (array != null) {
                for (File e : array) {
                    if (e.isFile()) {
                        String fileName = file.getName();
                        // 忽略 package-info.class
                        if (fileName.equals(PACKAGE_INFO_CLASS)) {
                            continue;
                        }
                        // 忽略非Class文件
                        if (fileName.endsWith(CLASS_SUFFIX)) {
                            continue;
                        }

                        Resource resource = getFile(basePackage, e);
                        ret.add(resource);
                    } else if (e.isDirectory()) {
                        List<Resource> list = listFiles(basePackage + e.getName() + "/", e);
                        ret.addAll(list);
                    }
                }
            }
        } else if (file.isFile()) {
            Resource resource = getFile(basePackage, file);
            ret.add(resource);
        }
        return ret;
    }

    /**
     * 查找到一个文件.
     *
     * @param file 文件
     * @return ResourceCallback
     */
    private Resource getFile(String basePackage, File file) {
        return new FileClassResource(basePackage, file);
    }


    private List<Resource> doFindJarResources(URL url) throws IOException {
        List<Resource> ret = new ArrayList<>();
        JarURLConnection jarCon = (JarURLConnection) url.openConnection();
        try (JarFile jarFile = jarCon.getJarFile()) {
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(CLASS_SUFFIX)) {
                    Resource resource = getJarFile(entry.getName());
                    ret.add(resource);
                }
            }
        }
        return ret;
    }

    /**
     * 查找到一个Jar文件.
     *
     * @param path Jar的资源路径
     */
    private Resource getJarFile(String path) {
        return new JarClassResource(path);
    }

}
