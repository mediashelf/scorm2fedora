/**
 * 
 */
package com.yourmediashelf.crosswalk;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * An interface for performing metadata crosswalks.
 * 
 * @author Edwin Shin
 *
 */
public interface Crosswalk {
	public void translate(String inFormat, InputStream in, String outFormat, 
			OutputStream out) throws CrosswalkException;
	
	public boolean isSupported(String inFormat, String outFormat);
	
	public Map<String, Set<String>> getSupportedCrosswalks();
}
