package com.zvidia.pomelo.websocket;

import com.zvidia.pomelo.protocol.PomeloMessage;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-11
 * Time: 上午12:02
 * To change this template use File | Settings | File Templates.
 */
public interface OnDataHandler {
    public void onData(PomeloMessage.Message message) throws JSONException;
}
