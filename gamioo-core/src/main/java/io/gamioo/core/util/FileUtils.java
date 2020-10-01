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

package io.gamioo.core.util;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

/**
 * 文件操作工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class FileUtils {
    /**
     * 可读大小的单位
     */
    private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "EB"};

    /**
     * 加载类路径下指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static Optional<String> getFileText(String fileName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            return Optional.ofNullable(StringUtils.readString(is));
        } catch (Exception e) {
        }
        // 文件不存在等其他情况返回null
        return Optional.empty();
    }

    /**
     * 读取指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException 文件未找到会抛出此异常
     */
    public static String readFileText(String fileName) throws FileNotFoundException, IOException {
        try (FileReader reader = new FileReader(fileName)) {
            return StringUtils.readString(reader);
        }
    }

    /**
     * 读取指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static File getFile(String fileName) {
            URL url =FileUtils.class.getClassLoader().getResource(fileName);
            // 通过url获取File的绝对路径
            return new File(url.getFile());
    }


    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建，默认是覆盖原文件
     *
     * @param fileName 文件名称
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, String content) throws IOException {
        writeFileText(fileName, false, content);
    }

    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建
     *
     * @param fileName 文件名称
     * @param append   是否追加写入
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, boolean append, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName, append); OutputStreamWriter osw = new OutputStreamWriter(fos, CharsetUtils.CHARSET_UTF_8);) {
            osw.write(content);
            osw.flush();
        }
    }

    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     *
     * @param size Long类型大小
     * @return 大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups];
    }

    /**
     * 创建指定文件,目录不存在则自动创建
     * <p>
     * 如果目标文件不存在并且创建成功，则为true；如果目标文件存在，则为false
     *
     * @param file 文件对象
     * @return 如果目标文件不存在并且创建成功，则为true；如果目标文件存在，则为false
     * @throws IOException IO异常
     */
    public static boolean createNewFile(File file) throws IOException {
        // 目标文件存在，直接返回false.
        if (file.exists()) {
            return false;
        }

        // 如果目录不存在则创建此父目录
        final File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 目录有了，直接调用JDK的创建新文件命令
        return file.createNewFile();
    }

    /**
     * Reads the contents of a file line by line to a List of Strings.
     * The file is always closed.
     *
     * @param file     the file to read, must not be {@code null}
     * @param charset the charset to use, {@code null} means platform default
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtils.readLines(in, Charsets.toCharset(charset));
        }
    }
    /**
     * Opens a {@link FileInputStream} for the specified file, providing better
     * error messages than simply calling <code>new FileInputStream(file)</code>.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * </p>
     * <p>
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     * </p>
     *
     * @param file the file to open for input, must not be {@code null}
     * @return a new {@link FileInputStream} for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if the file object is a directory
     * @throws IOException           if the file cannot be read
     * @since 1.3
     */
    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

}