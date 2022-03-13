package com.zvidia.pomelo.utils;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-7
 * Time: 下午10:31
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

}
