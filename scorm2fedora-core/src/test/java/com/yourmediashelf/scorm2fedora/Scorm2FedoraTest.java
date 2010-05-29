/**
 *
 */
package com.yourmediashelf.scorm2fedora;

import static com.yourmediashelf.fedora.client.FedoraClient.getDatastream;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import com.yourmediashelf.scorm.scorm2fedora.Scorm2Fedora;
import com.yourmediashelf.scorm.scorm2fedora.ScormDeposit;


/**
 * Integration test. Requires a running instance of Fedora, with test
 * parameters set in the parent pom.xml.
 *
 * @author Edwin Shin
 *
 */
public class Scorm2FedoraTest {
	private Scorm2Fedora s2f;
	private FedoraClient fedora;
	private final String scormdsid = "SCORM";

	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		String baseUrl = System.getProperty("fedora.test.baseUrl");
        String username = System.getProperty("fedora.test.username");
        String password = System.getProperty("fedora.test.password");
        fedora = new FedoraClient(new FedoraCredentials(new URL(baseUrl), username, password));
		props.put("username", username);
		props.put("password", password);
		props.put("baseUrl", baseUrl);
		props.put("scorm.dsid", scormdsid);
		s2f = new Scorm2Fedora(props);
	}

	@Test
	public void testDeposit() throws Exception {
		File pkg = new File("src/test/resources/21.edit.essay.zip");
		assertTrue(pkg.exists());
		InputStream in = new FileInputStream(pkg);
		ScormDeposit result = s2f.deposit(in, pkg.getName());
		in.close();
		assertTrue(result.pid != null && !result.pid.isEmpty());
		assertTrue(result.location != null && !result.location.toString().isEmpty());

		// sanity check: make sure the datastream for the uploaded file exists
		// and that the datastream label matches the filename
		FedoraResponse response = getDatastream(result.pid, scormdsid)
		                            .format("xml").execute(fedora);
		assertEquals(200, response.getStatus());
        String dsProfile = response.getEntity(String.class);
        assertXpathEvaluatesTo(pkg.getName(),
                               String.format("/datastreamProfile[@pid='%s']/dsLabel", result.pid),
                               dsProfile);
	}
}
