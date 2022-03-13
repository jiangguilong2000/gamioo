package com.zvidia.pomelo.utils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-9
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class GzipUtils {
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
//        System.out.println("String length : " + str.length());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
//        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
    }

    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
//        System.out.println("Input String length : " + str.length());
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }
//        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
    }
}
