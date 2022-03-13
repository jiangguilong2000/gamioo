package com.zvidia.pomelo.protobuf;

import com.sun.org.apache.bcel.internal.classfile.Code;
import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-7
 * Time: 下午10:27
 * To change this template use File | Settings | File Templates.
 */
public class Encoder {

    private JSONObject protos;

    public Encoder() {
    }

    public Encoder(JSONObject protos) {
        this.protos = protos;
    }

    public JSONObject getProtos() {
        return protos;
    }

    public void setProtos(JSONObject protos) {
        this.protos = protos;
    }

    public byte[] encode(String proto, String msg) throws PomeloException, JSONException {
        if (StringUtils.isEmpty(proto) || StringUtils.isEmpty(msg)) {
            throw new PomeloException("Route or msg can not be null : " + msg + ", proto : " + proto);
        }
        if (this.protos.isNull(proto)) {
            throw new PomeloException("check proto failed! msg : " + msg + ", proto : " + proto);
        }
        //Get protos from protos map use the route as key
        JSONObject _proto = this.protos.getJSONObject(proto);
        JSONObject _msg = new JSONObject(msg);

        //check msg
        if (!checkMsg(_msg, _proto)) {
            throw new PomeloException("check msg failed! msg : " + msg + ", proto : " + proto);
        }

        CodedOutputStream output = new CodedOutputStream();
        if (_proto != null) {
            encodeMsg(output, _proto, _msg);
            byte[] res = output.toBytes();
            return res;
        }
        return null;
    }

    private boolean checkMsg(JSONObject msg, JSONObject proto) throws PomeloException, JSONException {
        if (msg == null || proto == null) {
            throw new PomeloException("check msg failed! msg : " + msg + ", proto : " + proto);
        }
        Iterator<String> names = proto.keys();
        while (names.hasNext()) {
            String name = names.next();
            JSONObject value = proto.getJSONObject(name);
            if (!value.isNull(ProtoBufParser.OPTION_KEY)) {
                String option = value.getString(ProtoBufParser.OPTION_KEY);
                String type = value.getString(ProtoBufParser.TYPE_KEY);
                boolean msgNull = value.isNull(ProtoBufParser.MESSAGES_KEY);
                JSONObject messages = msgNull ? new JSONObject() : value.getJSONObject(ProtoBufParser.MESSAGES_KEY);
                boolean tagNull = value.isNull(ProtoBufParser.TAGS_KEY);
                JSONObject tags = tagNull ? new JSONObject() : value.getJSONObject(ProtoBufParser.TAGS_KEY);
                MessageOption messageOption = MessageOption.valueOf(option);
                switch (messageOption) {
                    case required: {
                        if (msg.isNull(name)) {
                            //no property exist for required!
                            return false;
                        }
                    }
                    case optional: {
                        if (!msg.isNull(name)) {
                            if (!msg.isNull(name) && !messages.isNull(type) && !checkMsg(msg.getJSONObject(name), messages.getJSONObject(type))) {
                                //inner proto error!
                                return false;
                            }
                        }
                        break;
                    }
                    case repeated: {
                        if (!msg.isNull(name) && !messages.isNull(type)) {
                            JSONArray array = msg.getJSONArray(name);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                if (!checkMsg(obj, messages.getJSONObject(type))) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void encodeMsg(CodedOutputStream output, JSONObject proto, JSONObject msg) throws JSONException {
        Iterator<String> names = msg.keys();
        while (names.hasNext()) {
            String name = names.next();
            Object value = msg.get(name);
            JSONObject _proto = proto.getJSONObject(name);
            String option = _proto.getString(ProtoBufParser.OPTION_KEY);
            String type = _proto.getString(ProtoBufParser.TYPE_KEY);
            int tag = _proto.getInt(ProtoBufParser.TAG_KEY);
            MessageOption _option = MessageOption.valueOf(option);
            switch (_option) {
                case required:
                case optional: {
                    output.writeInt32NoTag(encodeIntTag(type, tag));
                    encodeProp(value, type, output, proto);
                    break;
                }
                case repeated: {
                    if (value != null) {
                        JSONArray array = (JSONArray) value;
                        if (array.length() > 0) {
                            encodeArray(array, _proto, output, proto);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void encodeArray(JSONArray array, JSONObject _proto, CodedOutputStream output, JSONObject proto) throws JSONException {
        String type = _proto.getString(ProtoBufParser.TYPE_KEY);
        int tag = _proto.getInt(ProtoBufParser.TAG_KEY);
        WireType _type = WireType.valueOfType(type);

        if (_type != WireType._string && _type != WireType._message) {
            //simple type
            output.writeRawByte(encodeIntTag(type, tag));
            output.writeInt32NoTag(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                encodeProp(obj, type, output, proto);
            }
        } else {
            //complex type
            for (int i = 0; i < array.length(); i++) {
                output.writeRawByte(encodeIntTag(type, tag));
                JSONObject obj = array.getJSONObject(i);
                encodeProp(obj, type, output, proto);
            }
        }
    }

    //所有涉及Codec.encode的都应该是variant length!!!
    //http://www.cnblogs.com/shitouer/archive/2013/04/12/google-protocol-buffers-encoding.html
    private void encodeProp(Object value, String type, CodedOutputStream output, JSONObject proto) throws JSONException {
        WireType _type = WireType.valueOfType("_" + type);
        switch (_type) {
            case _uInt32: {
                int _value = Integer.parseInt(value.toString());
                output.writeInt32NoTag(_value);
                break;
            }
            case _int32:
            case _sInt32: {
                int _value = Integer.parseInt(value.toString());
                output.writeSInt32NoTag(_value);
                break;
            }
            case _float: {
                float _value = Float.parseFloat(value.toString());
                output.writeFloatNoTag(_value);
                break;
            }
            case _double: {
                double _value = Double.parseDouble(value.toString());
                output.writeDoubleNoTag(_value);
                break;
            }
            case _string: {
                String _value = value.toString();
                byte[] bytes = _value.getBytes(ProtoBufParser.DEFAULT_CHARSET);
                output.writeInt32NoTag(bytes.length);
                output.writeRawBytes(bytes);
                break;
            }
            default: {
                boolean aNull = proto.isNull(ProtoBufParser.MESSAGES_KEY);
                JSONObject messages = aNull ? new JSONObject() : proto.getJSONObject(ProtoBufParser.MESSAGES_KEY);
                if (!messages.isNull(type)) {
                    CodedOutputStream output2 = new CodedOutputStream();
                    encodeMsg(output2, messages.getJSONObject(type), (JSONObject) value);
                    byte[] buffer2 = output2.toBytes();
                    output.writeInt32NoTag(buffer2.length);
                    output.writeRawBytes(buffer2);
                }
                break;
            }
        }
    }

    /*
    * int -> byte
    * */
    private int encodeIntTag(String type, int tag) {
        String _type = "_" + type;
        WireType __type = WireType.valueOfType(_type);
        int num = (tag << 3) | __type.getValue();
        return num;

    }
}
