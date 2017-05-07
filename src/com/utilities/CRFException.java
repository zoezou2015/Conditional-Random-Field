package com.utilities;

/**
 *  Exception for this project.
 * @author ZOE ZOU
 *
 */

public class CRFException extends RuntimeException{
	
	public CRFException(){
		super();
	}
	
	public CRFException(Throwable cause){
		super(cause);
	}
	
	public CRFException(String message){
		super(message);
	}
	
	public CRFException(String message, Throwable cause){
		super(message, cause);
	}
	
	public CRFException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
