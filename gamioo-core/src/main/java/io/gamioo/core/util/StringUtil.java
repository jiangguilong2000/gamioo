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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class StringUtil {
	private static final char DELIM_START = '{';
	private static final char DELIM_STOP = '}';
	private static final int GB = 1024 * 1024 * 1024;// 定义GB的计算常量
	private static final int MB = 1024 * 1024;// 定义MB的计算常量
	private static final int KB = 1024;// 定义KB的计算常量

	public static boolean isEmpty(String... strs) {
		for (String str : strs) {
			if (StringUtils.isEmpty(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Replace placeholders in the given messagePattern with arguments.
	 *
	 * @param messagePattern the message pattern containing placeholders.
	 * @param arguments the arguments to be used to replace placeholders.
	 * @return the formatted message.
	 */
	public static String format(String messagePattern, Object... arguments) {
		if (messagePattern == null || arguments == null || arguments.length == 0) {
			return messagePattern;
		}
		final StringBuilder result = new StringBuilder();
		int currentArgument = 0;
		for (int i = 0; i < messagePattern.length(); i++) {
			final char curChar = messagePattern.charAt(i);
			if (curChar == DELIM_START && i < messagePattern.length() - 1 && messagePattern.charAt(i + 1) == DELIM_STOP) {
				if (currentArgument < arguments.length) {
					result.append(arguments[currentArgument]);
				} else {
					result.append(DELIM_START).append(DELIM_STOP);
				}
				currentArgument++;
				i++;
			} else {
				result.append(curChar);
			}
		}
		return result.toString();
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
		if(StringUtils.isEmpty(content)){
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
		if(StringUtils.isEmpty(arg)){
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
		if(StringUtil.isEmpty(str)){
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
		if(StringUtil.isEmpty(str)){
			return 0;
		}
		HashSet<Character> set=new HashSet<>();
		for(char b:str.toCharArray()){
			set.add(b);
		}
		return set.size();
	}

	public static void main(String[] args) {
		System.out.println(StringUtil.stringToAscii("tencent-01"));
		System.out.println(StringUtil.stringToAscii("tencent-02"));
		System.out.println(StringUtil.stringToAscii("37-02"));
		System.out.println(StringUtil.stringToAscii("37-03"));
		
		System.out.println(StringUtil.byte2GBMBKB(70920));
		System.out.println(StringUtil.byte2GBMBKB(2555904));
		System.out.println(StringUtil.byte2GBMBKB(1459617792));
		System.out.println(StringUtil.byte2GBMBKB(89278480));
	}
}
