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

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class TelnetUtils {
    private static final Logger logger = LogManager.getLogger(TelnetUtils.class);

    /**
     * 模拟Telnet 连接
     * @param ip 地址
     * @param port 端口
     *
     * 检测是否能连上 */
    public static boolean isConnected(int checkNum,String ip, int port) {
        boolean ret = false;
        for (int i = 0; i < checkNum; i++) {
            try {
                TelnetClient telnet = new TelnetClient();
                telnet.connect(ip, port);
                ret = telnet.isConnected();
                telnet.disconnect();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            if (ret) {
                return ret;
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return ret;
    }

    public static boolean isConnected(String ip, int port) {
        boolean ret = false;
            try {
                TelnetClient telnet = new TelnetClient();
                telnet.setConnectTimeout(5000);//连接超时时间
              //  TelnetClient telnetClient = new TelnetClient("vt200");  //指明Telnet终端类型，否则会返回来的数据中文会乱码
              //  telnet.setDefaultTimeout(5000);//打开端口的超时时间
                telnet.connect(ip, port);
                ret = telnet.isConnected();
                if(ret){
                    logger.info("该连接可以用 {}:{}",ip,port);
                    telnet.disconnect();
                }
            } catch (Exception e) {
               // logger.warn("该连接无法使用 {}:{}",ip,port);
            //    logger.error(e.getMessage(), e);
            }
            return ret;
    }
}
