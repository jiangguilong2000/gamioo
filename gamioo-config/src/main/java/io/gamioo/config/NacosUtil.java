package io.gamioo.config;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.shaded.com.google.protobuf.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.Executor;

public class NacosUtil {
    private static final Logger logger = LogManager.getLogger(NacosUtil.class);

    public static void main(String[] args) throws ServiceException {
        NacosUtil util=new NacosUtil();
        util.initNacos();
    }
    public void initNacos() throws ServiceException {
        logger.info("[nacos]:nacosbutton equals 1L");
        ServerConfigManager serverConfigManager = new ServerConfigManager();
        serverConfigManager.load("game","game-config.yml");
        ServerConfig config=serverConfigManager.getServerConfig();
        //指定连接到的服务器地址，可以是本地也可以是其他服务器
        String serverAddr = "1.15.9.113";
        //自己指定的组
        String group = "game";

        // 示例
        try {
            //自己指定的dataId
            String dataId = "10003";

            //从nacos中获取的开关服务
            ConfigService configService = this.getConfigService(serverAddr);

            //本方法启动的时候获取内容
            String content = configService.getConfig(dataId, group, 5000);
            logger.info("content:{}", content);
            //获取服务器状态
            String status = configService.getServerStatus();
            logger.debug("status={}", status);

            configService.publishConfig(dataId, group, config.getContent(), ConfigType.YAML.getType());



            //监听器，一旦nacos中相应值改变，则进行相应开关状态改变
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    logger.info("[Listener]:{}", configInfo);


                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });
        } catch (NacosException e) {
            logger.error("获取功能开关配置失败:" + e, e);
        }


    }

    /**
     * 根据地址获取ConfigService
     *
     * @param serverAddr 地址
     * @return 返回获取到的ConfigService
     * @throws NacosException 抛出异常
     */
    private ConfigService getConfigService(String serverAddr) throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        return NacosFactory.createConfigService(properties);
    }

}
