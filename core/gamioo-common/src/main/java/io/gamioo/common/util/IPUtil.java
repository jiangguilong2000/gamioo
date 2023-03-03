package io.gamioo.common.util;


import io.gamioo.common.exception.ServiceException;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPUtil {
	private static final Logger logger = LogManager.getLogger(IPUtil.class);

	// SiteLocalAddress=false,LoopbackAddress=false,address.getHostAddress()=115.29.176.102
	// 广域网IP
	// SiteLocalAddress=true,LoopbackAddress=false,address.getHostAddress()=10.161.175.91
	// 局域网IP
	// SiteLocalAddress=false,LoopbackAddress=true,address.getHostAddress()=127.0.0.1
	// 回环IP
	// 获取局域网IP
	public static String getIP() throws ServiceException {
		String ret = "";
		try {
			if (SystemUtils.IS_OS_LINUX) {
				Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
				while (en.hasMoreElements()) {
					NetworkInterface ni = en.nextElement();
					Enumeration<InetAddress> enIp = ni.getInetAddresses();
					while (enIp.hasMoreElements()) {
						InetAddress inet = enIp.nextElement();
						if (inet.isSiteLocalAddress() && !inet.isLoopbackAddress() && (inet instanceof Inet4Address)) {
							ret = inet.getHostAddress().toString();
							// logger.info("SiteLocalAddress={},host={}",inet.isSiteLocalAddress(),
							// inet.getHostAddress().toString());
						}
					}
				}
			} else {
				ret = InetAddress.getLocalHost().getHostAddress();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (StringUtils.isEmpty(ret)) {
			throw new ServiceException("获取本地IP失败");
		}
		return ret;
	}

	// 将127.0.0.1形式的IP地址转换成十进制整数，这里没有进行任何错误处理
	// 将IP地址转换为主机字节序
	public static long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	// 将十进制整数形式转换成127.0.0.1形式的ip地址
	// 主机字节序转换为将IP地址
	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

	/** 判断是否为内网IP */
	public static boolean isInner(String ip) {
		String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";// 正则表达式=。
																																															// =、懒得做文字处理了、
		Pattern p = Pattern.compile(reg);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

	public static String getRemortIP(Channel channel) {
		String ip = null;
		InetSocketAddress remoteAddr = (InetSocketAddress) channel.remoteAddress();
		if (remoteAddr != null) {
			ip = remoteAddr.getAddress().getHostAddress();
		}
		return ip;
	}
	/**
	 * 兼容反向代理后的客户端真实地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {
		String ret = request.getHeader("x-forwarded-for");
		if (ret== null) {
			return request.getRemoteAddr();
		}
		
		// 处理多IP绑定的问题 "219.75.62.162, 10.168.82.130, 220.255.1.138"
		// 10.125.117.199, 119.233.254.49
		if (ret.indexOf(",") >= 0) {
			ret = StringUtils.trim(StringUtils.substringAfterLast(ret, ","));
		}
		return ret;
	}
	
	public static String getRemoteIP(FullHttpRequest httpRequest) {
	String ip=null;
		try {
			ip = httpRequest.headers().get("X-Real_IP");
			if (StringUtils.isEmpty(ip)) {
				ip = httpRequest.headers().get("x-forwarded-for");
				// 处理多IP绑定的问题 "219.75.62.162, 10.168.82.130, 220.255.1.138"
				// 10.125.117.199, 119.233.254.49
				if (StringUtils.indexOf(ip, ",")>= 0) {
					ip = StringUtils.trim(StringUtils.substringAfterLast(ip, ","));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ip;
	}
	
	

	public static void main(String[] args) {
		String ipStr = "127.0.0.1";
		long longIp = IPUtil.ipToLong(ipStr);
		System.out.println("整数形式为：" + longIp);
		System.out.println("整数" + longIp + "转化成字符串IP地址：" + IPUtil.longToIP(longIp));
		// ip地址转化成二进制形式输出
		System.out.println("二进制形式为：" + Long.toBinaryString(longIp));
	}

}
