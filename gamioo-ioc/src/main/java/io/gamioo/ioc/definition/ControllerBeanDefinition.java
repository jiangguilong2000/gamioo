package io.gamioo.ioc.definition;

import io.gamioo.ioc.annotation.CommandMapping;
import io.gamioo.ioc.annotation.RequestMapping;
import io.gamioo.ioc.stereotype.Controller;
import io.gamioo.ioc.wrapper.Command;
import io.gamioo.ioc.wrapper.MethodWrapper;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 控制器入口的定义
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ControllerBeanDefinition extends GenericBeanDefinition{
    /**
     * 控制器的消息处理容器
     */
    protected final static  Map<Integer, Command> commandStore = new ConcurrentHashMap<>(1024);
    public ControllerBeanDefinition(Class<?> clazz, Annotation annotation) {
        super(clazz, annotation);
    }


    /**
     * 解析实体对象
     *
     * @param instance
     */
    @Override
    public void analysisBean(Object instance) {
        //控制器入口有特殊的分析
//        Class<? extends Annotation> type =this.getAnnotationType();
//        // 控制器
//        //TODO ... 后续可以把这些解析放到 gamioo-game 中，开发者可以自定义注释,如何解析注释以及如何使用注释
//        if (type == Controller.class) {
            //@MessageMapping
            List<MethodDefinition> methodList = this.getMethodDefinitionList(CommandMapping.class);
            for (MethodDefinition e : methodList) {
                CommandMapping mapping = e.getAnnotation();
                MethodWrapper wrapper = e.getMethodWrapper(instance);
                Command command = new Command(wrapper, mapping);
                this.commandStore.put(command.getCode(), command);
            }
            //@RequestMapping
            methodList = this.getMethodDefinitionList(RequestMapping.class);
            for (MethodDefinition e : methodList) {
                RequestMapping mapping = e.getAnnotation();
                //TODO ...
            }

//
//        } else {
//
//        }
    }

    /**根据指令调用*/
    public static Command getCommand(int code) {
        return commandStore.get(code);
    }

    /**指令列表*/
    public static Collection<Command> getCommandList(){
        return commandStore.values();
    }
}
