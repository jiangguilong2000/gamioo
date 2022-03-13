package com.zvidia.pomelo.protobuf;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-7
 * Time: 下午8:41
 * To change this template use File | Settings | File Templates.
 */
public enum WireType {
    _uInt32(0),
    _sInt32(0),
    _int32(0),
    _double(1), //double
    _string(2),
    _message(2),
    _float(5); //float

    int value;

    private WireType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WireType valueOfType(String name) {
        boolean simpleType = WireType.isSimpleType(name);
        return simpleType ? WireType.valueOf(name) : WireType._message;
    }

    public static boolean isSimpleType(String name) {
        if (WireType._uInt32.name().equals(name)
                || WireType._sInt32.name().equals(name)
                || WireType._int32.name().equals(name)
                || WireType._double.name().equals(name)
                || WireType._string.name().equals(name)
                || WireType._float.name().equals(name)) {
            return true;
        }
        return false;
    }
}
