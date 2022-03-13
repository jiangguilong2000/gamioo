package io.gamioo.pomelo;

/**
 * 框架异常类，此类被框架捕获，由框架决定处理。
 * <p>
 * v2.0: 框架处理基本类
 *
 * @author Allen Jiang
 * @since 2.0
 */
public class ServiceException extends RuntimeException {
    static final long serialVersionUID = 1L;


    private final int code;

    public ServiceException(int code) {
        super();
        this.code = code;
    }

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ServiceException(int code, String msg, Throwable cause) {
        super(msg, cause, true, false);
        assert !(cause instanceof ServiceException) : "不能反复抛出ServiceException及其子类";
        this.code = code;
    }

    public ServiceException(int code, Throwable cause) {
        super(cause);
        assert !(cause instanceof ServiceException) : "不能反复抛出ServiceException及其子类";
        this.code = code;
    }


    final public int getCodeId() {
        return this.code;
    }


    public String getRawMessage() {
        return super.getMessage();
    }


}
