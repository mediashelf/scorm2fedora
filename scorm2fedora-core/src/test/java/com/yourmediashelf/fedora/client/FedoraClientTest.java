package com.yourmediashelf.fedora.client;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * This is an integration test which requires a running Fedora server.
 *
 * @author Edwin Shin
 *
 */
public class FedoraClientTest {
	private static FedoraCredentials credentials;
	private FedoraClient client;
	//private static String testPid = "test-rest:1";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String baseUrl = System.getProperty("fedora.test.baseUrl");
		String username = System.getProperty("fedora.test.username");
		String password = System.getProperty("fedora.test.password");
		credentials = new FedoraCredentials(new URL(baseUrl), username, password);
	}

	@Before
	public void setUp() throws Exception {
		client = new FedoraClient(credentials);
	}

	@Test
	public void testGetMimeType() throws Exception {
	    File f = new File("src/test/resources/foo.xml");
	    assertEquals("text/xml", client.getMimeType(f));
	}
}
