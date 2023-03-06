package io.gamioo.common.exception;

/**
 * 在找不到指定Public属性时抛出.
 *
 * @author 小流氓[176543888@qq.com]
 * @since 3.3.6
 */
public class NoPublicFieldException extends ServiceException {


    public NoPublicFieldException(String message,Object... params) {
        super(message, params);
    }

    public NoPublicFieldException(String message, Throwable cause) {
        super(message, cause);
    }


    public NoPublicFieldException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
