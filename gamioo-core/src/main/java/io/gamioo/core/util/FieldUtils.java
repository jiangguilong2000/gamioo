package io.gamioo.core.util;

import io.gamioo.core.exception.NoPublicFieldException;
import io.gamioo.core.exception.ServerBootstrapException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 属性工具类.
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class FieldUtils {

    private static final int PREFIX_IS_METHOD_INDEX = 2;
    private static final int PREFIX_GET_METHOD_INDEX = 3;
    private static final int PREFIX_SET_METHOD_INDEX = 3;

    /**
     * 强制给一个属性{@link Field}写入值.
     *
     * @param target 目标对象
     * @param field  要写入的属性
     * @param value  要写入的值
     */
    public static void writeField(final Object target, final Field field, final Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(target, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ServerBootstrapException(target.getClass() + " 的 " + field.getName() + " 属性无法注入.", e);
        }
    }

    /**
     * 强制给一个指定名称的属性写入值.
     * <p>
     * 这个基本是留言给脚本调用的，方便修改一些配置错误而生的方法(自动找父类的属性)
     *
     * @param target    目标对象
     * @param fieldName 要写入的属性名称
     * @param value     要写入的值
     */
    public static void writeField(final Object target, final String fieldName, final Object value) {
        Field field = FieldUtils.getField(target.getClass(), fieldName);
        if (field == null) {
            throw new NoPublicFieldException("Class={},field={} not found",target.getClass().getName(),fieldName);
        }
        FieldUtils.writeField(target, field, value);
    }

    /**
     * 强制读取一个属性{@link Field}的值.
     *
     * @param target 目标类对象，如果是静态方法，目标为null
     * @param field  对象的属性
     * @return 返回对象属性的值
     */
    public static Object readField(final Object target, final Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(target);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new ServerBootstrapException(e,"{} 的 {} 属性无法读取.",target.getClass(), field.getName());

        }
    }

    /**
     * 获取指定名称的属性.
     *
     * @param klass     指定类
     * @param fieldName 指定名称
     * @return 指定名称的属性
     */
    public static Field getField(final Class<?> klass, String fieldName) {
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            for (Field field : target.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定类的所有属性，包含父类的属性.
     * <p>
     * 包含私有属性，包含静态属性等等....
     *
     * @param klass 指定类
     * @return 指定类的属性集合.
     */
    public static List<Field> getFieldList(final Class<?> klass) {
        List<Field> result = new ArrayList<>();
        for (Class<?> target = klass; target != Object.class; target = target.getSuperclass()) {
            result.addAll(Arrays.asList(target.getDeclaredFields()));
        }
        return result;
    }

    /**
     * 生成Get方法名.
     * <p>
     *
     * @param field 属性
     * @return Get方法名
     */
    public static String genGetMethodName(Field field) {
        int len = field.getName().length();
        StringBuilder sb;
        if (field.getType() == boolean.class) {
            sb = new StringBuilder(len + 2);
            sb.append("is").append(field.getName());
            if (Character.isLowerCase(sb.charAt(PREFIX_IS_METHOD_INDEX))) {
                sb.setCharAt(PREFIX_IS_METHOD_INDEX, Character.toUpperCase(sb.charAt(PREFIX_IS_METHOD_INDEX)));
            }
        } else {
            sb = new StringBuilder(len + 3);
            sb.append("get").append(field.getName());
            if (Character.isLowerCase(sb.charAt(PREFIX_GET_METHOD_INDEX))) {
                sb.setCharAt(PREFIX_GET_METHOD_INDEX, Character.toUpperCase(sb.charAt(PREFIX_GET_METHOD_INDEX)));
            }
        }
        return sb.toString();
    }

    /**
     * 生成Set方法名.
     *
     * @param field 属性
     * @return Set方法名
     */
    public static String genSetMethodName(Field field) {
        int len = field.getName().length();
        StringBuilder sb = new StringBuilder(len + 3);
        sb.append("set").append(field.getName());
        if (Character.isLowerCase(sb.charAt(PREFIX_SET_METHOD_INDEX))) {
            sb.setCharAt(PREFIX_SET_METHOD_INDEX, Character.toUpperCase(sb.charAt(PREFIX_SET_METHOD_INDEX)));
        }
        return sb.toString();
    }

    /**
     * 利用反射，扫描出此类所有属性(包含父类中子类没有重写的属性)
     *
     * @param klass       指定类.
     * @param annotations 标识属性的注解
     * @return 返回此类所有属性.
     */
    public static Field[] scanAllField(final Class<?> klass, List<Class<? extends Annotation>> annotations) {
        // 为了返回是有序的添加过程，这里使用LinkedHashMap
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        scanField(klass, fieldMap, annotations);
        return fieldMap.values().toArray(new Field[0]);
    }

    /**
     * 递归的方式拉取属性，这样父类的属性就在上面了...
     *
     * @param klass       类
     * @param fieldMap    所有属性集合
     * @param annotations 标识属性的注解
     */
    private static void scanField(final Class<?> klass, Map<String, Field> fieldMap, List<Class<? extends Annotation>> annotations) {
        Class<?> superClass = klass.getSuperclass();
        if (!Object.class.equals(superClass)) {
            scanField(superClass, fieldMap, annotations);
        }
        // 属性判定
        for (Field f : klass.getDeclaredFields()) {
            // Static和Final的不要
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers())) {
                continue;
            }
            // 子类已重写或内部类中的不要
            if (fieldMap.containsKey(f.getName()) || f.getName().startsWith("this$")) {
                continue;
            }
            // 没有指定的注解不要
            for (Annotation a : f.getAnnotations()) {
                if (annotations.contains(a.annotationType())) {
                    fieldMap.put(f.getName(), f);
                    break;
                }
            }
        }
    }


    /**
     * 获取Map类型的属性Key的Class对象.
     *
     * @param field Map类型的属性
     * @return Key的Class对象
     */
    public static Class<?> getMapFieldKeyClass(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            // Key是第0位
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return Object.class;
    }
}