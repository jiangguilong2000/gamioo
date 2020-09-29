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

package io.gamioo.robot;

import io.gamioo.core.util.FileUtils;
import io.gamioo.core.util.ThreadUtils;
import io.gamioo.robot.entity.Proxy;
import io.gamioo.robot.entity.Server;
import io.gamioo.robot.entity.Target;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class H5Robot {
    private static final Logger logger = LogManager.getLogger(H5Robot.class);


    public static void main(String[] args)  {
        H5Robot robot = new H5Robot();
        List<Target> targets = robot.getServerList(Target.class, "target.txt");
        List<Proxy> array = robot.getServerList(Proxy.class, "cell.txt");
        int id = 1;
        for (Target target : targets) {
            int size=array.size();
            if(array.size()>0){
                int max=(int)Math.ceil(target.getNumber()/size);
                for(int i=0;i<max;i++){
                    for (Proxy proxy : array) {
                        WebSocketClient client = new WebSocketClient(id++, proxy, target);
                        ThreadUtils.sleep(target.getInterval());
                        client.connect();
                    }
                }

            }else{
                for(int i=0;i<target.getNumber();i++){
                    WebSocketClient client = new WebSocketClient(id++, null, target);
                    ThreadUtils.sleep(target.getInterval());
                    client.connect();
                }
            }
        }


    //    logger.debug("");

    }

//    public <T extends Server> List<T> getServerList(Class<T> clazz, String path) {
//        List<T> list = new ArrayList<>();
//        File file = FileUtils.getFile(path);
//        try {
//            List<String> array = FileUtils.readLines(file, Charset.defaultCharset());
//            array.forEach(e -> {
//                try {
//                    T T = clazz.newInstance();
//                    T.parse(e);
//                    list.add(T);
//                } catch (IllegalAccessException | InstantiationException ex) {
//                    logger.error(ex.getMessage(), ex);
//                }
//            });
//        } catch (IOException e) {
//            logger.error(e.getMessage(), e);
//        }
//        return list;
//    }

    public <T extends Server> List<T> getServerList(Class<T> clazz, String path) {
        List<T> list = new ArrayList<>();
        File file = FileUtils.getFile(path);
        try {
            List<String> array = FileUtils.readLines(file, Charset.defaultCharset());
            array.forEach(e -> {
                    T T = JSON.parseObject(e, clazz);
                    T.parse();
                    list.add(T);
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

}
