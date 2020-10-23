package io.gamioo.ioc.definition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * 所有实现此接口或继承此类的
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ListFieldDefinition extends GenericFieldDefinition {


    public ListFieldDefinition(Field field) {
        super(field);
    }

    @Override
    public Class<?> getClazz() {
        Class<?> ret = (Class<?>) ((ParameterizedType) this.getField().getGenericType()).getActualTypeArguments()[0];
        return ret;
    }
}
