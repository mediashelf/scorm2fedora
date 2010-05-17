/**
 * 
 */
package com.yourmediashelf.crosswalk;

/**
 * @author Edwin Shin
 *
 */
public class CrosswalkException extends Exception {
	private static final long serialVersionUID = 1L;

	public CrosswalkException(String message) {
        super(message, null);
    }

    public CrosswalkException(String message, Throwable cause) {
        super(message, cause);
    }
}
