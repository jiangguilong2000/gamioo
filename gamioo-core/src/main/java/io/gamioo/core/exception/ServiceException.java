package io.gamioo.core.exception;

import io.gamioo.core.util.StringUtils;

/**
 * 框架异常类
 *
 * @author Allen Jiang
 * @since 1.0
 */
public class ServiceException extends RuntimeException {

	/**
     *
     */
	private static final long serialVersionUID = -3287463281746412649L;
	//private int errorCode;

	public ServiceException() {
		super();
	}
	public ServiceException(int code) {
		super(String.valueOf(code));
	}
	/**
	 * 
	 * @param message  异常信息
	 */
	public ServiceException(Throwable cause,String message,Object... params){
		super(StringUtils.format(message, params),cause);
	}

	public ServiceException(String message) {
		super(message);
	}
	public ServiceException(String message,Object... params){
		super(StringUtils.format(message, params));
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
