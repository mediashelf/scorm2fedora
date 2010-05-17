package com.yourmediashelf.fedora.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

/**
 * This is an integration test which requires a running Fedora server.
 * 
 * @author Edwin Shin
 *
 */
public class FedoraClientTest {
	private static FedoraCredentials credentials;
	private FedoraClient client;
	private static String testPid = "test-rest:1";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		credentials = new FedoraCredentials(new URL(
				"http://localhost:8080/fedora"), "fedoraAdmin", "fedoraAdmin");
	}
	
	@Before
	public void setUp() throws Exception {
		client = new FedoraClient(credentials);
	}

	@Test
	public void testIngest() throws Exception {
		ClientResponse response = null;
		URI location = null;
		Map<String, String> queryParams = new HashMap<String, String>();
		String pid = null;
		
		// empty object, no pid, no namespace
		response = client.ingest(null, null, null);
		assertEquals(201, response.getStatus());
		location = response.getLocation();
		pid = response.getEntity(String.class);
		client.purgeObject(pid, null);
		
		// empty object, with namespace, but no pid
		queryParams.put("namespace", "foospace");
		response = client.ingest(null, queryParams, null);
		assertEquals(201, response.getStatus());
		location = response.getLocation();
		assertTrue(location.toString().contains("/objects/foospace"));
		pid = response.getEntity(String.class);
		client.purgeObject(pid, null);
		
		// empty object, with pid
		response = client.ingest(testPid, null, null);
		assertEquals(201, response.getStatus());
		pid = response.getEntity(String.class);
		assertEquals(testPid, pid);
	}
	
	@Test
	public void testAddDatastream() throws Exception {
		ClientResponse response = null;
		Map<String, String> queryParams = null;
		queryParams = new HashMap<String, String>();
		queryParams.put("controlGroup", "X");
		queryParams.put("dsLabel", "someLabel");
		String dsContent = "<foo>bar</foo>";
		response = client.addDatastream(testPid, "DSBAR", queryParams, dsContent);
		assertEquals(201, response.getStatus());
		
		// file
		File f = new File("src/test/resources/21.edit.essay.zip");
		assertTrue(f.exists());
		queryParams = new HashMap<String, String>();
		queryParams.put("controlGroup", "M");
		queryParams.put("dsLabel", f.getName());
		response = client.addDatastream(testPid, "FOO", queryParams, f);
		assertEquals(201, response.getStatus());
	}
	
	@Test
	public void testModifyDatastream() throws Exception {
		ClientResponse response = null;
		Map<String, String> queryParams = null;
		String dsContent = "<bar>baz</bar>";
		response = client.modifyDatastream(testPid, "DSBAR", queryParams, dsContent);
		assertEquals(201, response.getStatus());
	}
	
	@Test
	public void testPurgeObject() throws Exception {
		ClientResponse response = client.purgeObject(testPid, null);
		assertEquals(204, response.getStatus());
	}
}
