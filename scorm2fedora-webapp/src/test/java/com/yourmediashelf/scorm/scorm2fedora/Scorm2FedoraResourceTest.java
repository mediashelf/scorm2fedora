/**
 * 
 */
package com.yourmediashelf.scorm.scorm2fedora;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URLDecoder;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import eu.medsea.mimeutil.MimeUtil2;

/**
 * Integration test for the Scorm2Fedora Web Service. This test requires a 
 * running instance of Fedora.
 * 
 * @author Edwin Shin
 * 
 */
public class Scorm2FedoraResourceTest extends JerseyTest {

	public Scorm2FedoraResourceTest() {
		super(new WebAppDescriptor.Builder(
				"com.yourmediashelf.scorm.scorm2fedora").contextPath(
				"scorm2fedora").initParam("username", "fedoraAdmin").initParam(
				"password", "fedoraAdmin").initParam("baseUrl",
				"http://localhost:8080/fedora").initParam("namespace", "scorm")
				.build());
	}

	@Test
	public void testDeposit() throws Exception {
		File pkg = new File("src/test/resources/21.edit.essay.zip");
		assertTrue(pkg.exists());

		MimeUtil2 mimeUtil = new MimeUtil2();
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		String mimeType = MimeUtil2.getMostSpecificMimeType(
				mimeUtil.getMimeTypes(pkg)).toString();
		assertEquals("application/zip", mimeType);

		Client client = Client.create();
		//client.addFilter(new LoggingFilter());
		WebResource wr = client.resource("http://localhost:8080/scorm2fedora");
		MultiPart multiPart = new FormDataMultiPart()
				.bodyPart(new FileDataBodyPart("file", pkg, MediaType
						.valueOf(mimeType)));

		ScormDeposit response = wr.type(MediaType.MULTIPART_FORM_DATA).accept(
				MediaType.APPLICATION_JSON).post(ScormDeposit.class, multiPart);
		// The location (e.g. http://localhost:8080/fedora/objects/scorm%3A5), 
		// once decoded, should contain the pid (e.g. scorm:5)
		assertTrue(URLDecoder.decode(response.location.toString(), "UTF-8").contains(response.pid));
	}
}
