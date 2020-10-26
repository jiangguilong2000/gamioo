package io.gamioo.ioc.definition;

import com.alibaba.fastjson.JSONObject;
import io.gamioo.core.util.JSONUtils;
import io.gamioo.core.util.StringUtils;
import io.gamioo.ioc.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置文件解析定义
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ConfigurationBeanDefinition implements BeanDefinition{
    protected final Class<?> beanClass;
    protected final Configuration annotation;

    protected Map<Class<? extends Annotation>, List<MethodDefinition>> methodStore = new HashMap<>();
    
    public ConfigurationBeanDefinition(Class<?> clazz, Annotation annotation) {
        this.beanClass = clazz;
        this.annotation = (Configuration)annotation;
    }

    /**每次都可以从磁盘加载*/
    @Override
    public Object newInstance() {
        String fileName=this.annotation.value();
        JSONObject object= JSONUtils.loadFromXMLFile(fileName);
        return object.toJavaObject(this.beanClass);
    }
    
    @Override
    public List<FieldDefinition> getAutowiredFieldDefinition() {
        return new ArrayList<>();
    }




    /**
     * 解析方法
     */
    @Override
    public void analysisMethodList() {

    }

    /**
     * 解析字段
     */
    @Override
    public void analysisFieldList() {
    }
//
//    /**
//     * 注入
//     */
//    @Override
//    public void inject() {
//
//    }

    @Override
    public MethodDefinition getInitMethodDefinition() {
    	  MethodDefinition ret = null;
          List<MethodDefinition> list = methodStore.get(PostConstruct.class);
          if (list != null && list.size() > 0) {
              ret = list.get(0);
          }
          return ret;
    }

    @Override
    public Class<?> getClazz() {
        return this.beanClass;
    }

    @Override
    public String getName() {
        return StringUtils.uncapitalized(this.beanClass.getSimpleName());
    }

    /**
     * 获取注解
     */
    @Override
    public Annotation[] getAnnotationList() {
        return new Annotation[]{this.annotation};
    }

    /**
     * 获取此方法的访问入口所对应的Index.
     *
     * @return 访问入口所对应的Index.
     */
    @Override
    public int getIndex() {
        return 0;
    }
}
