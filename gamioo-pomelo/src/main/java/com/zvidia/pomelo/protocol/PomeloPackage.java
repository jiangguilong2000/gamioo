package com.zvidia.pomelo.protocol;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-4
 * Time: 上午9:48
 * To change this template use File | Settings | File Templates.
 */
public class PomeloPackage {
    public static final int PKG_HEAD_BYTES = 4;

    public static final int TYPE_HANDSHAKE = 1;
    public static final int TYPE_HANDSHAKE_ACK = 2;
    public static final int TYPE_HEARTBEAT = 3;
    public static final int TYPE_DATA = 4;
    public static final int TYPE_KICK = 5;

    public static byte[] strencode(String str) {
        byte[] byteArray = new byte[str.length() * 3];
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            int charCode = str.codePointAt(i);
            int[] codes = null;
            if (charCode <= 0x7f) {
                codes = new int[1];
                codes[0] = charCode;
            } else if (charCode <= 0x7ff) {
                codes = new int[2];
                codes[0] = 0xc0 | (charCode >> 6);
                codes[1] = 0x80 | (charCode & 0x3f);
            } else {
                codes = new int[3];
                codes[0] = 0xe0 | (charCode >> 12);
                codes[1] = 0x80 | ((charCode & 0xfc0) >> 6);
                codes[2] = 0x80 | (charCode & 0x3f);
            }
            for (int j = 0; j < codes.length; j++) {
                byteArray[offset] = (byte) codes[j];
                ++offset;
            }
        }
        byte[] _buffer = Arrays.copyOfRange(byteArray, 0, offset);
        return _buffer;
    }

    public static String strdecode(byte[] buffer) {
        int offset = 0;
        int charCode = 0;
        int end = buffer.length;
        List<Integer> array = new LinkedList<Integer>();
        while (offset < end) {
            byte b = buffer[offset];
            int bi = b & 0xff;
            if (bi < 128) {
                charCode = bi;
                offset += 1;
            } else if (bi < 224) {
                int t1 = buffer[offset + 1] & 0xff;
                charCode = ((bi & 0x3f) << 6) + (t1 & 0x3f);
                offset += 2;
            } else {
                int t1 = buffer[offset + 1] & 0xff;
                int t2 = buffer[offset + 2] & 0xff;
                charCode = ((bi & 0x0f) << 12) + ((t1 & 0x3f) << 6) + (t2 & 0x3f);
                offset += 3;
            }
            array.add(charCode);
        }
        int[] codes = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            codes[i] = array.get(i);
        }
        return new String(codes, 0, array.size());
    }

    /**
     * Package protocol encode.
     * <p/>
     * Pomelo package format:
     * +------+-------------+------------------+
     * | type | body length |       body       |
     * +------+-------------+------------------+
     * <p/>
     * Head: 4bytes
     * 0: package type,
     * 1 - handshake,
     * 2 - handshake ack,
     * 3 - heartbeat,
     * 4 - data
     * 5 - kick
     * 1 - 3: big-endian body length
     * Body: body length bytes
     *
     * @param {Number}    type   package type
     * @param {ByteArray} body   body content in bytes
     * @return {ByteArray}        new byte array that contains encode result
     */
    public static byte[] encode(int type, byte[] body) {
        int length = body != null ? body.length : 0;
        byte[] buffer = new byte[PKG_HEAD_BYTES + length];
        int index = 0;
        buffer[index++] = (byte) (type);
        buffer[index++] = (byte) ((length >> 16));
        buffer[index++] = (byte) ((length >> 8));
        buffer[index++] = (byte) (length);
        if (body != null) {
            for (int i = 0; i < body.length; i++) {
                buffer[PKG_HEAD_BYTES + i] = (byte) body[i];
            }
        }
        return buffer;
    }

    /**
     * Package protocol decode.
     * See encode for package format.
     *
     * @param {ByteArray} buffer byte array containing package content
     * @return {Object}           {type: package type, buffer: body byte array}
     */
    public static Package decode(byte[] buffer) {
        byte[] bytes = Arrays.copyOf(buffer, buffer.length);
        int type = bytes[0];
        int index = 1;
        byte v1 = bytes[index++];
        byte v2 = bytes[index++];
        byte v3 = bytes[index++];
        int length = (v1 << 16) | (v2 << 8) | (v3 >>> 0) & 0xff;
        byte[] body = length > 0 ? new byte[length] : new byte[0];
        for (int i = 0; i < body.length; i++) {
            body[i] = bytes[PKG_HEAD_BYTES + i];
        }
        return new Package(type, body);
    }

    public static class Package {
        int type;
        byte[] body;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public byte[] getBody() {
            return body;
        }

        public void setBody(byte[] body) {
            this.body = body;
        }

        public Package(int type, byte[] body) {
            this.type = type;
            this.body = body;
        }
    }

}
