package de.mobilcom.dpvo.common;

/**
 * The class <code>CICDException</code> is an exception class and is used for error 
 * handling in the package. 
 */
public class DPVOException extends Exception {

	/**
	 * Serial Version
	 */
	private static final long serialVersionUID = 6648016100073749301L;

	
	public DPVOException(String message) {
		super(message);
	}
	
	public DPVOException(Throwable ex) {
		super(ex);
	}
}
