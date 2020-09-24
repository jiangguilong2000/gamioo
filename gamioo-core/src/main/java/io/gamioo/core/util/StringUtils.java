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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class StringUtils {

    /**
     * 空字符串 {@code ""}.
     */
    public static final String EMPTY = "";
    /**
     * 一个空格字符串 {@code " "}
     */
    public static final String SPACE = " ";
    /**
     * 一个换行字符串 {@code "\n"}
     */
    public static final String LF = "\n";
    /**
     * 一个回车字符串 {@code "\r"}
     */
    public static final String CR = "\r";
    /**
     * 一个英文逗号字符串 {@code ","}
     */
    public static final String COMMA = ",";
    /**
     * 一个英文连字符字符串 {@code "-"}
     */
    public static final String HYPHEN = "-";

    /**
     * 一个英文左括号字符串 {@code "("}
     */
    public static final String LPAREN = "(";
    /**
     * 一个英文右括号字符串 {@code ")"}
     */
    public static final String RPAREN = ")";
    /**
     * 一个英文左大括号字符串 "{"
     */
    public static final String LBRACE = "{";
    /**
     * 一个英文右大括号字符串 "}"
     */
    public static final String RBRACE = "}";
    /**
     * 一个英文左中括号字符串 {@code "["}
     */
    public static final String LBRACKET = "[";
    /**
     * 一个英文右中括号字符串 {@code "]"}
     */
    public static final String RBRACKET = "]";
    /**
     * 一个英文冒号字符串 {@code ":"}
     */
    public static final String COLON = ":";
    /**
     * 一个英文星号字符串 {@code "*"}
     */
    public static final String ASTERISK = "*";
    /**
     * 一个空字符串数组.
     */
    public static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * 检测字符串是否为 null或"".
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("test")     = false
     * StringUtils.isEmpty("  test  ") = false
     * </pre>
     *
     * @param text 被检测字符串
     * @return 如果字符为null或""则返回true,否则返回false.
     */
    public static boolean isEmpty(final String text) {
        return text == null || text.length() == 0;
    }

    /**
     * 检测字符串是否不为 null且不为"".
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("test")    = true
     * StringUtils.isNotEmpty("  test ") = true
     * </pre>
     *
     * @param text 被检测字符串
     * @return 如果字符不为 null且不为""则返回true,否则返回false.
     */
    public static boolean isNotEmpty(final String text) {
        return !isEmpty(text);
    }

    /**
     * 检测字符串是否为空，null或Java空白字符。
     * <p>
     * Java空白字符的定义请参考{@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param text 被检测字符串
     * @return 如果字符串为空，null或Java空白字符则返回true,否则返回false.
     */
    public static boolean isBlank(final String text) {
        if (text == null) {
            return true;
        }
        // 只要有一个字符不为空白，那就肯定不是空白
        for (int i = 0, len = text.length(); i < len; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测字符串是否不为空，null或Java空白字符。
     * <p>
     * Java空白字符的定义请参考{@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param text 被检测字符串
     * @return 如果字符串不为空，null或Java空白字符则返回true,否则返回false.
     */
    public static boolean isNotBlank(final String text) {
        return !isBlank(text);
    }

    /**
     * 检测一个字符串长度.
     * <p>
     * 字符串有可能包含中文等其他文字，中文应该算2个长度.
     *
     * @param text 被检测字符串
     * @return 字符串长度
     */
    public static int length(final String text) {
        if (text == null) {
            return 0;
        }

        int sum = 0;
        for (int i = 0, len = text.length(); i < len; i++) {
            sum += text.charAt(i) > 127 ? 2 : 1;
        }
        return sum;
    }

    /**
     * <p>
     * Splits the provided text into an array, separators specified. This is an alternative to using StringTokenizer.
     * </p>
     *
     * <p>
     * The separator is not included in the returned String array. Adjacent separators are treated as one separator. For more control over the split use the StrTokenizer class.
     * </p>
     *
     * <p>
     * A {@code null} input String returns {@code null}. A {@code null} separatorChars splits on whitespace.
     * </p>
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * Performs the logic for the {@code split} and {@code splitPreserveAllTokens} methods that return a maximum array length.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the array. A zero or negative value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are treated as empty token separators; if {@code false}, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        final boolean flag = preserveAllTokens && lastMatch;
        if (match || flag) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[0]);
    }

    /**
     * 将一个字符串由驼峰式命名变成分割符分隔单词
     *
     * <pre>
     *  lowerWord("helloWorld", '_') =&gt; "hello_world"
     * </pre>
     *
     * @param cs 字符串
     * @param c  分隔符
     * @return 转换后字符串
     */
    public static String lowerWord(CharSequence cs, char c) {
        int len = cs.length();
        StringBuilder sb = new StringBuilder(len + 5);
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append(c);
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * 计算一个Long类型的数字的文本长度.
     * <p>
     * 包含负数计算
     *
     * @param v Long类型的数字
     * @return 数字的文本长度
     */
    public static int asciiSizeInBytes(long v) {
        if (v == 0) {
            return 1;
        }
        if (v == Long.MIN_VALUE) {
            return 20;
        }
        boolean negative = false;
        if (v < 0) {
            v = -v;
            negative = true;
        }
        int width = v < 100000000L
                //
                ? v < 10000L
                //
                ? v < 100L ? v < 10L ? 1 : 2 : v < 1000L ? 3 : 4
                //
                : v < 1000000L ? v < 100000L ? 5 : 6 : v < 10000000L ? 7 : 8
                //
                : v < 1000000000000L
                //
                ? v < 10000000000L ? v < 1000000000L ? 9 : 10 : v < 100000000000L ? 11 : 12
                //
                : v < 1000000000000000L
                //
                ? v < 10000000000000L ? 13 : v < 10000000000000L ? 14 : 15
                //
                : v < 100000000000000000L
                //
                ? v < 10000000000000000L ? 16 : 17
                //
                : v < 1000000000000000000L ? 18 : 19;
        //
        return negative ? width + 1 : width;
    }

    /**
     * 拼接字符串.
     *
     * @param strings 需要拼接的字串
     * @return 拼接后的字符串
     */
    public static String join(String... strings) {
        int len = 0;
        for (String str : strings) {
            len += str.length();
        }
        final StringBuilder result = new StringBuilder(len);
        for (String str : strings) {
            result.append(str);
        }
        return result.toString();
    }

    /**
     * 拼接路径字符串.
     *
     * @param paths 需要拼接的路径字串
     * @return 拼接后的路径字符串
     */
    public static String pathJoin(String... paths) {
        int len = 0;
        for (String str : paths) {
            len += str.length() + 1;
        }
        final StringBuilder sb = new StringBuilder(len);
        for (String str : paths) {
            sb.append(str);
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '/') {
                sb.append('/');
            }
        }
        return sb.toString();
    }

    /**
     * 拼接字符串.
     * <p>
     * 在比较长或多的情况计算长度比StringJoiner性能好.<br>
     *
     * <pre>
     *     StringJoiner result = new StringJoiner(delimiter, prefix, suffix);
     *     for (String str : strings) {
     *         result.add(str);
     *    }
     *     return result.toString();
     *
     *     Stream.of(strings).collect(Collectors.joining(delimiter, prefix, suffix))
     * </pre>
     *
     * @param delimiter 分隔符
     * @param prefix    前缀
     * @param suffix    后缀
     * @param strings   需要拼接的字串
     * @return 拼接后的字符串
     */
    public static String build(String delimiter, String prefix, String suffix, String... strings) {
        int len = prefix.length() + suffix.length() + (strings.length - 1) * delimiter.length();
        for (String str : strings) {
            len += str.length();
        }
        StringBuilder result = new StringBuilder(len);
        result.append(prefix);
        for (String str : strings) {
            result.append(str).append(delimiter);
        }
        if (result.length() > prefix.length()) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append(suffix);
        return result.toString();
    }

    /**
     * 编码字符串，编码为UTF-8
     *
     * @param str 字符串
     * @return 编码后的字节数组
     */
    public static byte[] utf8Bytes(CharSequence str) {
        return bytes(str, CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 编码字符串<br>
     * 使用系统默认编码
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str) {
        return bytes(str, null);
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则编码的结果取决于平台
     * @return 编码后的字节数组
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return ByteArrayUtils.EMPTY_BYTE_ARRAY;
        }
        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 常规的格式化一个带有占位符的字符串.
     * <p>
     * 区别于JDK的{@link MessageFormat#format(String, Object...)}，这个方法只是简单的按位填充
     *
     * @param messagePattern       带有占位符的字符串
     * @param arguments 参数列表
     * @return 格式化以后的字符串
     */
    public static String format(String messagePattern, Object... arguments) {
        if (arguments.length == 0) {
            return messagePattern;
        }
        // FIXME 这个方法基本用于日志，邮件内容拼接，可以参考日志中使用的方案做一个缓存策略
        for (int i = 0, len = arguments.length; i < len; i++) {
            messagePattern = messagePattern.replace(join("{", String.valueOf(i), "}"), String.valueOf(arguments[i]));
        }
        return messagePattern;
    }

    /**
     * 从输入流中读出所有文本.
     *
     * @param inputStream 输入流
     * @return 返回流中的文本
     * @throws IOException If an I/O error occurs
     */
    public static String readString(InputStream inputStream) throws IOException {
        return readString(inputStream, CharsetUtils.CHARSET_UTF_8);
    }

    /**
     * 从输入流中读出所有文本.
     *
     * @param inputStream 输入流
     * @param charset     文本的编码方式
     * @return 返回流中的文本
     * @throws IOException If an I/O error occurs
     */
    public static String readString(InputStream inputStream, Charset charset) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(inputStream, charset)) {
            return readString(isr);
        }
    }

    /**
     * 读出所有文本。
     * <p>
     * 这里没有选择BufferedReader就是不想一行一行的读，浪费字符串拼接性能 <br>
     * 正常用于读HTTP的响应，配置文件内容，小文件等情况
     *
     * @param reader 抽象的文本流
     * @return 返回流中所有文本
     * @throws IOException If an I/O error occurs
     */
    public static String readString(Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder(1024);
        // 申明一次读取缓冲区
        final char[] array = new char[256];
        // 这里并没有使用while(true),如果一个文本超过100W，还是放弃后面的算了
        while (sb.length() < MathUtils.MILLION) {
            int n = reader.read(array);
            // 读结束了，就GG了
            if (n < 0) {
                break;
            }
            sb.append(array, 0, n);
        }
        return sb.toString();
    }

    /**
     * 从左边使用空格(' ')补齐指定位位数的字符串.
     * <pre>
     * StringUtils.leftPad(null, *)   = null
     * StringUtils.leftPad("", 3)     = "   "
     * StringUtils.leftPad("bat", 3)  = "bat"
     * StringUtils.leftPad("bat", 5)  = "  bat"
     * StringUtils.leftPad("bat", 1)  = "bat"
     * StringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  字符串
     * @param size 补齐后的位数
     * @return 返回补齐后的字符串{@code null}
     */
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * 从左边使用指定字符补齐指定位位数的字符串.
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'b')     = "bbb"
     * StringUtils.leftPad("bat", 3, 'b')  = "bat"
     * StringUtils.leftPad("bat", 5, ' ')  = "  bat"
     * StringUtils.leftPad("bat", 1, 'b')  = "bat"
     * StringUtils.leftPad("bat", -1, 'b') = "bat"
     * </pre>
     *
     * @param str     字符串
     * @param size    补齐后的位数
     * @param padChar 补齐字符
     * @return 返回补齐后的字符串{@code null}
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }

        // 不需要补位，那就返回原来的字符串
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }

        // 创建返回数组，前面填充指定字符，后面复制原来的字符串
        final char[] array = new char[size];
        Arrays.fill(array, 0, pads, padChar);
        System.arraycopy(str.toCharArray(), 0, array, pads, str.length());
        return new String(array);
    }

    private static final String FOLDER_SEPARATOR = "/";
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final int GB = 1024 * 1024 * 1024;// 定义GB的计算常量
    private static final int MB = 1024 * 1024;// 定义MB的计算常量
    private static final int KB = 1024;// 定义KB的计算常量

    public static boolean isEmpty(String... array) {
        for (String str : array) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
                return true;
            }
        }
        return false;
    }



    public static String format2Percent(double value) {
        String p = String.valueOf(value * 100D);
        int ix = p.indexOf(".") + 1;
        String percent = p.substring(0, ix) + p.substring(ix, ix + 1);
        return percent + "%";
    }

    public static String byte2GBMBKB(long value) {
        if (value / GB >= 1) // 如果当前Byte的值大于等于1GB
            return String.format("%.2f", value / (float) GB) + " GB";// 将其转换成GB
        else if (value / MB >= 1) // 如果当前Byte的值大于等于1MB
            return String.format("%.2f", value / (float) MB) + " MB";// 将其转换成MB
        else if (value / KB >= 1) // 如果当前Byte的值大于等于1KB
            return String.format("%.2f", value / (float) KB) + " KB";// 将其转换成KB
        else
            return String.valueOf(value) + " Byte";// 显示Byte值
    }

    public static String stringToAscii(String value) {
        StringBuffer ret = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                ret.append((int) chars[i]);
            } else {
                ret.append((int) chars[i]);
            }
        }
        return ret.toString();
    }

    /**
     * 解析字符串中所有数字，注意：这里只是把数字字符串切割出来了，转换成数字时注意边界
     * @param content 需要解析的字符串
     * @return 所有数字字符串集合
     */
    public static ArrayList<String> incisionNumber(String content){
        ArrayList<String> ret=new ArrayList<>();
        if(org.apache.commons.lang3.StringUtils.isEmpty(content)){
            return ret;
        }
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(content);
        while (m.find()) {
            ret.add(m.group());
        }
        return ret;
    }

    /**
     * 判断字符串是否是同一个字符。kkk全是k;22222全是2
     * @param arg  需要解析的字符串
     * @return 返回判断结果
     */
    public static boolean isSameChar(String arg){
        boolean flag = true;
        if(org.apache.commons.lang3.StringUtils.isEmpty(arg)){
            return flag;
        }
        char str = arg.charAt(0);
        for (int i = 1; i < arg.length(); i++) {
            if (str != arg.charAt(i)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断一个数字是否是连续的数字，例如，12345和54321就是连续的
     * @param str 要判断的数字字符串
     * @return 返回结果
     */
    public static boolean strNumberIsContinue(String str){
        if(isEmpty(str)){
            return false;
        }
        char[] chars= str.toCharArray();
        char c= chars[0];
        for(int i=1;i<chars.length;i++){
            int diff= chars[i]-c;
            if(diff!=1&&diff!=-1){
                return false;
            }
            c=chars[i];
        }
        return true;
    }

    /**
     * 获取字符串中去重后剩下多少个字符
     * @param str  需要解析的字符串
     * @return 返回去重后的字符串
     */
    public static int getStringCharCount(String str){
        if(isEmpty(str)){
            return 0;
        }
        HashSet<Character> set=new HashSet<>();
        for(char b:str.toCharArray()){
            set.add(b);
        }
        return set.size();
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    /**
     * Apply the given relative path to the given path,
     * assuming standard Java folder separation (i.e. "/" separators).
     * @param path the path to start from (usually a full file path)
     * @param relativePath the relative path to apply
     * (relative to the full file path above)
     * @return the full file path that results from applying the relative path
     */
    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        }
        else {
            return relativePath;
        }
    }

    public static String uncapitalized(String name)  {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        // Step-wise comparison
        final int length = cs1.length();
        for (int i = 0; i < length; i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}