package io.gamioo.common.util;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author Allen Jiang
 */
public class JVMUtil {

	public static String getStartArgs() {
		String ret = "";
		List<String> list = ManagementFactory.getRuntimeMXBean().getInputArguments();
		for (String e : list) {
			ret += e + " ";
		}
		return ret;
	}
}
