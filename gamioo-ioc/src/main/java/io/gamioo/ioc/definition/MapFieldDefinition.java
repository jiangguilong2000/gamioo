package io.gamioo.ioc.definition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Map类型的属性注入
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class MapFieldDefinition extends GenericFieldDefinition {


    public MapFieldDefinition(Field field) {
        super(field);
    }

    @Override
    public Class<?> getClazz() {
        Class<?> ret = (Class<?>) ((ParameterizedType) this.getField().getGenericType()).getActualTypeArguments()[1];
        return ret;
    }
}