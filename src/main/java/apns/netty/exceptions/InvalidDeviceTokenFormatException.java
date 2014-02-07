/*
 * InvalidDeviceTokenFormatException.java
 * 
 **********************************************************************

             Copyright (c) 2004 - 2014 netty-apns
             


 ***********************************************************************/
package apns.netty.exceptions;

/**
 * Thrown when a device token cannot be parsed (invalid format).
 * 
 * @author Sylvain Pedneault
 */
@SuppressWarnings("serial")
public class InvalidDeviceTokenFormatException extends Exception {

	/**
     * Instantiates a new invalid device token format exception.
     * @param message
     *            the message
     */
	public InvalidDeviceTokenFormatException(String message) {
		super(message);
	}


	/**
     * Instantiates a new invalid device token format exception.
     * @param token
     *            the token
     * @param problem
     *            the problem
     */
	public InvalidDeviceTokenFormatException(String token, String problem) {
		super(String.format("Device token cannot be parsed, most likely because it contains invalid hexadecimal characters: %s in %s", problem, token));
	}

}
