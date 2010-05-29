/**
 *
 */

package com.yourmediashelf.scorm.scorm2fedora;

import static com.yourmediashelf.fedora.client.FedoraClient.getDatastream;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;

import eu.medsea.mimeutil.MimeUtil2;

/**
 * Integration test for the Scorm2Fedora Web Service. This test requires a
 * running instance of Fedora.
 *
 * @author Edwin Shin
 */
public class Scorm2FedoraResourceTest
        extends JerseyTest {

    private FedoraClient fedora;
    private static String scormdsid = "SCORM";

    public Scorm2FedoraResourceTest() {
        super(new WebAppDescriptor.Builder("com.yourmediashelf.scorm.scorm2fedora")
                .contextPath("scorm2fedora")
                .initParam("username",
                           System.getProperty("fedora.test.username"))
                .initParam("password",
                           System.getProperty("fedora.test.password"))
                .initParam("baseUrl", System.getProperty("fedora.test.baseUrl"))
                .initParam("namespace", "scorm-test")
                .initParam("scorm.dsid", scormdsid).build());
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String baseUrl = System.getProperty("fedora.test.baseUrl");
        String username = System.getProperty("fedora.test.username");
        String password = System.getProperty("fedora.test.password");
        fedora =
                new FedoraClient(new FedoraCredentials(new URL(baseUrl),
                                                       username,
                                                       password));
    }

    @Test
    public void testDeposit() throws Exception {
        File pkg = new File("src/test/resources/21.edit.essay.zip");
        assertTrue(pkg.exists());

        MimeUtil2 mimeUtil = new MimeUtil2();
        mimeUtil
                .registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        String mimeType =
                MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(pkg))
                        .toString();
        assertEquals("application/zip", mimeType);

        //client().addFilter(new LoggingFilter());
        MultiPart multiPart =
                new FormDataMultiPart()
                        .bodyPart(new FileDataBodyPart("file", pkg, MediaType
                                .valueOf(mimeType)));

        ScormDeposit response =
                resource().type(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .post(ScormDeposit.class, multiPart);
        // The location (e.g. http://localhost:8080/fedora/objects/scorm%3A5),
        // once decoded, should contain the pid (e.g. scorm:5)
        assertTrue(URLDecoder.decode(response.location.toString(), "UTF-8")
                .contains(response.pid));

        // sanity check: make sure the datastream for the uploaded file exists
        // and that the datastream label matches the filename
        FedoraResponse getDS = getDatastream(response.pid, scormdsid)
                                    .format("xml").execute(fedora);
        assertEquals(200, getDS.getStatus());
        String dsProfile = getDS.getEntity(String.class);
        assertXpathEvaluatesTo(pkg.getName(),
                               String.format("/datastreamProfile[@pid='%s']/dsLabel", response.pid),
                               dsProfile);
    }
}
