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

package io.gamioo.ioc.factory.support;

import io.gamioo.ioc.annotation.CommandMapping;
import io.gamioo.ioc.annotation.RequestMapping;
import io.gamioo.ioc.definition.BeanDefinition;
import io.gamioo.ioc.definition.MethodDefinition;
import io.gamioo.ioc.stereotype.Controller;
import io.gamioo.ioc.wrapper.Command;
import io.gamioo.ioc.wrapper.MethodWrapper;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory {

    @Override
    protected void populateBean(Object instance, BeanDefinition beanDefinition) {
        super.populateBean(instance, beanDefinition);
        //控制器入口有特殊的分析
        Class<? extends Annotation> type = beanDefinition.getAnnotation().annotationType();
        // 控制器
        if (type == Controller.class) {
            //@MessageMapping
            List<MethodDefinition> methodList = beanDefinition.getMethodDefinitionList(CommandMapping.class);
            for (MethodDefinition e : methodList) {
                CommandMapping mapping = e.getAnnotation();
                MethodWrapper wrapper = e.getMethodWrapper(instance);
                Command command = new Command(wrapper, mapping);
                this.commandStore.put(command.getCode(), command);
            }
            //@RequestMapping
            methodList = beanDefinition.getMethodDefinitionList(RequestMapping.class);
            for (MethodDefinition e : methodList) {
                RequestMapping mapping = e.getAnnotation();
                //TODO ...
            }


        } else {

        }

    }
}

