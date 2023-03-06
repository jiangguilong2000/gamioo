package io.gamioo.nav.util;


import org.apache.commons.lang3.SystemUtils;

import java.io.*;

/**
 * 用于加载native dll的工具类
 *
 */
public class NativeUtils {
    public static void loadLibrary(String name) throws IOException {
        String suffix = "";
        //TODO 暂时只为两种系统服务
        if (SystemUtils.IS_OS_LINUX) {
            suffix += ".so";
        } else {
            suffix += ".dll";
        }
        try (InputStream inputStream = FileUtils.getInputStream(name + suffix); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                out.write(buffer, 0, n);
            }
            File file = File.createTempFile(name, suffix);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(out.toByteArray());
            }
            System.load(file.getAbsolutePath());
        }
    }

}
