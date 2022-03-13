package com.zvidia.pomelo.exception;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-6
 * Time: 下午12:57
 * To change this template use File | Settings | File Templates.
 */
public class PomeloException extends Exception {

    public PomeloException() {
    }

    public PomeloException(String message) {
        super(message);
    }

    public PomeloException(String message, Throwable cause) {
        super(message, cause);
    }

    public PomeloException(Throwable cause) {
        super(cause);
    }
}
