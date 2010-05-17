/**
 * 
 */
package com.yourmediashelf.scorm2fedora;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.yourmediashelf.scorm.scorm2fedora.Scorm2Fedora;


/**
 * @author Edwin Shin
 *
 */
public class Scorm2FedoraTest {
	private Scorm2Fedora s2f;
	
	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		props.put("username", "fedoraAdmin");
		props.put("password", "fedoraAdmin");
		props.put("baseUrl", "http://localhost:8080/fedora");
		s2f = new Scorm2Fedora(props);
	}

	@Test
	public void testDeposit() throws Exception {
		File pkg = new File("src/test/resources/21.edit.essay.zip");
		assertTrue(pkg.exists());
		InputStream in = new FileInputStream(pkg);
		s2f.deposit(in, null);
		in.close();
	}
}
