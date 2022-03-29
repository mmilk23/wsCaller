package com.milklabs.wscall;

public class WebServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebServiceException(Exception e) {
		super(e);
	}

	public WebServiceException() {
		super();
	}

	public WebServiceException(String message) {
		super(message);
	}

	public WebServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebServiceException(Throwable cause) {
		super(cause);
	}
}
