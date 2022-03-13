package com.zvidia.pomelo.protocol;

import com.zvidia.pomelo.exception.PomeloException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-4
 * Time: 上午9:58
 * To change this template use File | Settings | File Templates.
 */
public class PomeloMessage {

    public static final int MSG_FLAG_BYTES = 1;
    public static final int MSG_ROUTE_CODE_BYTES = 2;
    public static final int MSG_ID_MAX_BYTES = 5;
    public static final int MSG_ROUTE_LEN_BYTES = 1;

    public static final int MSG_ROUTE_CODE_MAX = 0xffff;

    public static final int MSG_COMPRESS_ROUTE_MASK = 0x1;
    public static final int MSG_TYPE_MASK = 0x7;

    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_NOTIFY = 1;
    public static final int TYPE_RESPONSE = 2;
    public static final int TYPE_PUSH = 3;


    public static final String MSG_ROUTE_KEY = "route";


    public static Message decode(byte[] buffer) {
        byte[] bytes = Arrays.copyOf(buffer, buffer.length);
        int bytesLen = bytes.length;
        int offset = 0;
        int id = 0;
        String route = null;

        // parse flag
        int flag = bytes[offset++] & 0xff;
        int compressRoute = flag & MSG_COMPRESS_ROUTE_MASK;
        int type = (flag >> 1) & MSG_TYPE_MASK;

        // parse id
        if (msgHasId(type)) {
            int m = bytes[offset] & 0xff;
            int i = 0;
            do {
                m = (bytes[offset] & 0xff);
                id = (int) (id + ((m & 0x7f) * Math.pow(2, (7 * i))));
                offset++;
                i++;
            } while (m >= 128);
        }

        // parse route
        if (msgHasRoute(type)) {
            if (compressRoute != 0) {
                int _route = ((bytes[offset++] & 0xff) << 8) | (bytes[offset++] & 0xff);
                route = _route + "";
            } else {
                int routeLen = bytes[offset++];
                if (routeLen > 0) {
                    byte[] _route = new byte[routeLen];
                    copyArray(_route, 0, bytes, offset, routeLen);
                    route = PomeloPackage.strdecode(_route);
                } else {
                    route = "";
                }
                offset += routeLen;
            }
        }

        // parse body
        int bodyLen = bytesLen - offset;
        byte[] body = new byte[bodyLen];

        copyArray(body, 0, bytes, offset, bodyLen);
        return new Message(id, type, compressRoute, route, body);
    }

    public static byte[] encode(int id, int type, int compressRoute, String route, byte[] msg) throws PomeloException {
        // caculate message max length
        int idBytes = msgHasId(type) ? caculateMsgIdBytes(id) : 0;
        int msgLen = MSG_FLAG_BYTES + idBytes;
        byte[] _route = null;
        if (msgHasRoute(type)) {
            if (compressRoute != 0) {
                _route = new byte[1];
                try {
                    _route[0] = (byte) Integer.parseInt(route);
                } catch (NumberFormatException e) {
                    throw new PomeloException("route format is not number", e);
                }
                msgLen += MSG_ROUTE_CODE_BYTES;
            } else {
                msgLen += MSG_ROUTE_LEN_BYTES;
                if (route != null) {
                    _route = PomeloPackage.strencode(route);
                    if (_route.length > 255) {
                        throw new PomeloException("route maxlength is overflow");
                    }
                    msgLen += _route.length;
                }
            }
        }

        if (msg != null) {
            msgLen += msg.length;
        }

        byte[] buffer = new byte[msgLen];
        int offset = 0;

        // add flag
        offset = encodeMsgFlag(type, compressRoute, buffer, offset);

        // add message id
        if (msgHasId(type)) {
            offset = encodeMsgId(id, buffer, offset);
        }

        // add route
        if (msgHasRoute(type)) {
            offset = encodeMsgRoute(compressRoute, _route, buffer, offset);
        }

        // add body
        if (msg != null) {
            offset = encodeMsgBody(msg, buffer, offset);
        }

        return buffer;
    }

    public static boolean msgHasId(int type) {
        return type == TYPE_REQUEST || type == TYPE_RESPONSE;
    }

    public static boolean msgHasRoute(int type) {
        return type == TYPE_REQUEST || type == TYPE_NOTIFY ||
                type == TYPE_PUSH;
    }

    public static int caculateMsgIdBytes(int id) {
        int len = 0;
        do {
            len += 1;
            id >>= 7;
        } while (id > 0);
        return len;
    }

    public static int encodeMsgFlag(int type, int compressRoute, byte[] buffer, int offset) throws PomeloException {
        if (type != TYPE_REQUEST && type != TYPE_NOTIFY &&
                type != TYPE_RESPONSE && type != TYPE_PUSH) {
            throw new PomeloException("unkonw message type: " + type);
        }
        buffer[offset] = (byte) ((type << 1) | (compressRoute != 0 ? 1 : 0));
        return offset + MSG_FLAG_BYTES;
    }

    public static int encodeMsgId(int id, byte[] buffer, int offset) {
        do {
            int tmp = id % 128;
            int next = (int) Math.floor(id / 128);
            if (next != 0) {
                tmp = tmp + 128;
            }
            buffer[offset++] = (byte) tmp;
            id = next;
        } while (id != 0);
        return offset;
    }


    public static int encodeMsgRoute(int compressRoute, byte[] route, byte[] buffer, int offset) throws PomeloException {
        if (compressRoute != 0) {
            if (route.length != 1) {
                throw new PomeloException("route size is overflow");
            }
            int _route = route[0];
            if (_route > MSG_ROUTE_CODE_MAX) {
                throw new PomeloException("route number is overflow");
            }

            buffer[offset++] = (byte) (_route >> 8);
            buffer[offset++] = (byte) _route;
        } else {
            if (route != null) {
                buffer[offset++] = (byte) route.length;
                copyArray(buffer, offset, route, 0, route.length);
                offset += route.length;
            } else {
                buffer[offset++] = 0;
            }
        }
        return offset;
    }

    public static int encodeMsgBody(byte[] msg, byte[] buffer, int offset) {
        copyArray(buffer, offset, msg, 0, msg.length);
        return offset + msg.length;
    }

    public static void copyArray(int[] dest, int doffset, int[] src, int soffset, int length) {
        for (int index = 0; index < length; index++) {
            dest[doffset++] = src[soffset++];
        }
    }

    public static void copyArray(byte[] dest, int doffset, byte[] src, int soffset, int length) {
        for (int index = 0; index < length; index++) {
            dest[doffset++] = src[soffset++];
        }
    }


    public static class Message {
        int id;
        int type;
        int compressRoute;
        String route;
        byte[] body;
        JSONObject bodyJson;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCompressRoute() {
            return compressRoute;
        }

        public void setCompressRoute(int compressRoute) {
            this.compressRoute = compressRoute;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }

        public byte[] getBody() {
            return body;
        }

        public void setBody(byte[] body) {
            this.body = body;
        }

        public JSONObject getBodyJson() {
            return bodyJson;
        }

        public void setBodyJson(JSONObject bodyJson) {
            this.bodyJson = bodyJson;
        }

        public Message(int id, int type, int compressRoute, String route, byte[] body) {
            this.id = id;
            this.type = type;
            this.compressRoute = compressRoute;
            this.route = route;
            this.body = body;
        }

        @Override
        public String toString() {
            return "{id:" + id + ",type:" + type + ",compressReoute:" + compressRoute + ",route:" + route + ",body:" + Arrays.toString(body) + ",body str:" + ((bodyJson != null) ? bodyJson.toString() : "") + "}";
        }
    }


}
