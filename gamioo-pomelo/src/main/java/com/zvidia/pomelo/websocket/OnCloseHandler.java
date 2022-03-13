package com.zvidia.pomelo.websocket;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-12
 * Time: 下午11:02
 * To change this template use File | Settings | File Templates.
 */
public interface OnCloseHandler {
    public void onClose(int code, String msg, boolean remote);
}
