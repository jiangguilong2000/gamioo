package com.zvidia.pomelo.protobuf;

import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-7
 * Time: 下午10:27
 * To change this template use File | Settings | File Templates.
 */
public class Decoder {
    private JSONObject protos;

    private int offset;

    private ByteBuffer buffer;

    public Decoder() {
    }

    public Decoder(JSONObject protos) {
        this.protos = protos;
    }

    public JSONObject getProtos() {
        return protos;
    }

    public void setProtos(JSONObject protos) {
        this.protos = protos;
    }

    public String decode(String proto, byte[] bytes) throws PomeloException, JSONException {
        if (StringUtils.isEmpty(proto) || bytes == null) {
            throw new PomeloException("Route or msg can not be null, proto : " + proto);
        }
        this.buffer = ByteBuffer.wrap(bytes);
        this.offset = 0;
        if (protos != null) {
            JSONObject _proto = protos.getJSONObject(proto);
            JSONObject jsonObject = decodeMsg(new JSONObject(), _proto, bytes.length);
            return jsonObject.toString();
        }
        return null;
    }

    private JSONObject decodeMsg(JSONObject msg, JSONObject proto, int length) throws JSONException {
        while (offset < length) {
            JSONObject head = getByteHead();
            JSONObject tags = proto.getJSONObject(ProtoBufParser.TAGS_KEY);
            int tag = head.getInt(ProtoBufParser.TAG_KEY);
            int type = head.getInt(ProtoBufParser.TYPE_KEY);
            String name = tags.getString(tag + "");
            JSONObject _proto = proto.getJSONObject(name);
            String option = _proto.getString(ProtoBufParser.OPTION_KEY);
            String _type = _proto.getString(ProtoBufParser.TYPE_KEY);
            MessageOption _option = MessageOption.valueOf(option);
            switch (_option) {
                case optional:
                case required: {
                    Object obj = decodeProp(_type, proto);
                    msg.put(name, obj);
                    break;
                }
                case repeated: {
                    if (msg.isNull(name)) {
                        msg.put(name, new JSONArray());
                    }
                    JSONArray array = msg.getJSONArray(name);
                    decodeArray(array, _type, proto);
                    break;
                }
            }
        }
        return msg;
    }

    private boolean isFinsh() throws JSONException {
        JSONObject tags = protos.getJSONObject(ProtoBufParser.TAGS_KEY);
        JSONObject head = peekHead();
        int tag = head.getInt(ProtoBufParser.TAG_KEY);
        return tags.isNull(tag + "");
    }

    private JSONObject getByteHead() throws JSONException {
        byte bytes = getByte();
        int tag = bytes & 0xff;
        JSONObject obj = new JSONObject();
        obj.put(ProtoBufParser.TYPE_KEY, tag & 0x7);
        obj.put(ProtoBufParser.TAG_KEY, tag >> 3);
        return obj;
    }

    private JSONObject getBytesHead() throws JSONException {
        byte[] bytes = getBytes(false);
        int tag = Codec.decodeUInt32(bytes);
        JSONObject obj = new JSONObject();
        obj.put(ProtoBufParser.TYPE_KEY, tag & 0x7);
        obj.put(ProtoBufParser.TAG_KEY, tag >> 3);
        return obj;
    }

    private JSONObject peekHead() throws JSONException {
        byte[] bytes = peekBytes();
        int tag = (int) Codec.decodeUInt32(bytes);
        JSONObject obj = new JSONObject();
        obj.put(ProtoBufParser.TYPE_KEY, tag & 0x7);
        obj.put(ProtoBufParser.TAG_KEY, tag >> 3);
        return obj;
    }

    private Object decodeProp(String type, JSONObject proto) throws JSONException {
        WireType _type = WireType.valueOfType("_" + type);
        switch (_type) {
            case _uInt32: {
                return getByte() & 0xff;
            }
            case _int32:
            case _sInt32: {
                return getByte() & 0xff;
            }
            case _float: {
                float aFloat = buffer.getFloat(offset);
                offset += 4;
                return aFloat;
            }
            case _double: {
                double aDouble = buffer.getDouble(offset);
                offset += 8;
                return aDouble;
            }
            case _string: {
                //int length = Codec.decodeUInt32(getBytes(false));
                int length = getByte() & 0xff;
                byte[] _bytes = new byte[length];
                buffer.get(_bytes, 0, length);
                offset += length;
                return new String(_bytes, ProtoBufParser.DEFAULT_CHARSET);
            }
            default: {
                boolean aNull = proto.isNull(ProtoBufParser.MESSAGES_KEY);
                JSONObject messages = aNull ? new JSONObject() : proto.getJSONObject(ProtoBufParser.MESSAGES_KEY);
                if (!messages.isNull(type)) {
                    JSONObject _proto = messages.getJSONObject(type);
                    //int length = (int) Codec.decodeUInt32(getBytes(false));
                    int length = getByte() & 0xff;
                    JSONObject msg = new JSONObject();
                    decodeMsg(msg, _proto, offset + length);
                    return msg;
                }
                break;
            }
        }
        return null;
    }

    private void decodeArray(JSONArray array, String type, JSONObject proto) throws JSONException {
        WireType _type = WireType.valueOfType(type);
        if (_type != WireType._string && _type != WireType._message) {
            //simple type
            //int length = (int) Codec.decodeUInt32(getBytes(false));
            int length = getByte() & 0xff;
            for (int i = 0; i < length; i++) {
                Object obj = decodeProp(type, proto);
                array.put(obj);
            }
        } else {
            Object obj = decodeProp(type, proto);
            array.put(obj);
        }
    }

    private byte getByte() {
        byte b = buffer.get();
        offset++;
        return b;
    }

    private byte[] getBytes(boolean flag) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        int pos = offset;
        flag = flag || false;
        int b = buffer.getInt(pos);
        buf.putInt(b);
        pos += 4;
        if (!flag) {
            offset = pos;
            buffer.position(pos);
        }
        buf.flip();
        return buf.array();
    }

    private byte[] peekBytes() {
        return getBytes(true);
    }
}
