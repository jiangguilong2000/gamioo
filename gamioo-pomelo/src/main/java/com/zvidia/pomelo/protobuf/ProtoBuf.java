package com.zvidia.pomelo.protobuf;

import com.zvidia.pomelo.exception.PomeloException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-8
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
public class ProtoBuf {
    private Encoder encoder;

    private Decoder decoder;

    public ProtoBuf() {
    }

    public ProtoBuf(Encoder encoder, Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public void initProtos(JSONObject encodeProtos, JSONObject decodeProtos) {
        if (encoder == null) {
            encoder = new Encoder(encodeProtos);
        } else {
            encoder.setProtos(encodeProtos);
        }
        if (decoder == null) {
            decoder = new Decoder(decodeProtos);
        } else {
            decoder.setProtos(decodeProtos);
        }
    }

    public byte[] encode(String proto, String msg) throws PomeloException, JSONException {
        return this.encoder.encode(proto, msg);
    }

    public String encodeBase64(String proto, String msg) throws PomeloException, JSONException {
        byte[] bytes = this.encoder.encode(proto, msg);
        return new String(bytes);
    }

    public String decode(String proto, byte[] bytes) throws PomeloException, JSONException {
        return this.decoder.decode(proto, bytes);
    }

    public String decodeBase64(String proto, String str) throws PomeloException, JSONException {
        return this.decoder.decode(proto, str.getBytes());
    }
}
