/**
 * 
 */
package com.yourmediashelf.scorm.scorm2fedora;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edwin Shin
 *
 */
@XmlRootElement
public class ScormDeposit {
	public String pid;
	public URI location;
	
	// JAXB requires this
	public ScormDeposit() {}
	
	public ScormDeposit(String pid, URI location) {
		this.pid = pid;
		this.location = location;
	}
}
