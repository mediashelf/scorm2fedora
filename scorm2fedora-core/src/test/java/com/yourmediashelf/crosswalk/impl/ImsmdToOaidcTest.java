
package com.yourmediashelf.crosswalk.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;

import com.yourmediashelf.crosswalk.Crosswalk;

/**
 * Test the IMS Metadata to OAI-DC conversion.
 *
 * @see http://www.imsglobal.org/metadata/imsmdv1p2p1/imsmd_bestv1p2p1.html#1242547
 *
 * @author Edwin Shin
 * @version $Id$
 */

public class ImsmdToOaidcTest {

    private static String imsmd;
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("<imsmd:lom xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        sb.append(" xmlns:imsmd=\"http://www.imsglobal.org/xsd/imsmd_rootv1p2p1\"");
        sb.append(" xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imsmd_v1p2 http://www.imsglobal.org/xsd/imsmd_v1p2p4.xsd\">");
        sb.append("  <imsmd:general>");
        sb.append("    <imsmd:title>");
        sb.append("      <imsmd:langstring xml:lang=\"en-US\">IMS Content Packaging Sample - Full Metadata</imsmd:langstring>");
        sb.append("    </imsmd:title>");
        sb.append("    <imsmd:language>en-US</imsmd:language>");
        sb.append("    <imsmd:description>");
        sb.append("      <imsmd:langstring xml:lang=\"en-US\">A sample content packaging record</imsmd:langstring>");
        sb.append("      <imsmd:langstring xml:lang=\"fr\">Un programme...</imsmd:langstring>");
        sb.append("    </imsmd:description>");
        sb.append("    <imsmd:keyword>");
        sb.append("      <imsmd:langstring xml:lang=\"en\">content interchange</imsmd:langstring>");
        sb.append("    </imsmd:keyword>");
        sb.append("    <imsmd:keyword>");
        sb.append("      <imsmd:langstring xml:lang=\"fr\">contenu d'exchange</imsmd:langstring>");
        sb.append("    </imsmd:keyword>");
        sb.append("    <imsmd:coverage>");
        sb.append("      <imsmd:langstring xml:lang=\"en\">Sample code</imsmd:langstring>");
        sb.append("    </imsmd:coverage>");
        sb.append("  </imsmd:general>");
        sb.append("  <imsmd:lifecycle>");
        sb.append("    <imsmd:contribute>");
        sb.append("      <imsmd:role>");
        sb.append("        <imsmd:source>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">LOMv1.0</imsmd:langstring>");
        sb.append("        </imsmd:source>");
        sb.append("        <imsmd:value>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">Author</imsmd:langstring>");
        sb.append("        </imsmd:value>");
        sb.append("      </imsmd:role>");
        sb.append("      <imsmd:centity>");
        sb.append("        <imsmd:vcard>BEGIN:vCard FN:Chris Moffatt N:Moffatt END:vCard</imsmd:vcard>");
        sb.append("      </imsmd:centity>");
        sb.append("      <imsmd:date>");
        sb.append("        <imsmd:datetime>2000-01-01</imsmd:datetime>");
        sb.append("      </imsmd:date>");
        sb.append("    </imsmd:contribute>");
        sb.append("    <imsmd:contribute>");
        sb.append("      <imsmd:role>");
        sb.append("        <imsmd:source>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">LOMv1.0</imsmd:langstring>");
        sb.append("        </imsmd:source>");
        sb.append("        <imsmd:value>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">publisher</imsmd:langstring>");
        sb.append("        </imsmd:value>");
        sb.append("      </imsmd:role>");
        sb.append("      <imsmd:centity>");
        sb.append("        <imsmd:vcard>BEGIN:vCard ORG:IMS Global Learning Corporation END:vCard</imsmd:vcard>");
        sb.append("      </imsmd:centity>");
        sb.append("      <imsmd:date>");
        sb.append("        <imsmd:datetime>1999-12-31</imsmd:datetime>");
        sb.append("        <imsmd:description>");
        sb.append("          <imsmd:langstring xml:lang=\"en-US\">20th century</imsmd:langstring>");
        sb.append("        </imsmd:description>");
        sb.append("      </imsmd:date>");
        sb.append("    </imsmd:contribute>");
        sb.append("    <imsmd:contribute>");
        sb.append("      <imsmd:role>");
        sb.append("        <imsmd:source>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">LOMv1.0</imsmd:langstring>");
        sb.append("        </imsmd:source>");
        sb.append("        <imsmd:value>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">foo</imsmd:langstring>");
        sb.append("        </imsmd:value>");
        sb.append("      </imsmd:role>");
        sb.append("      <imsmd:centity>");
        sb.append("        <imsmd:vcard>xfoo</imsmd:vcard>");
        sb.append("      </imsmd:centity>");
        sb.append("      <imsmd:date>");
        sb.append("        <imsmd:datetime>2000-01-01</imsmd:datetime>");
        sb.append("      </imsmd:date>");
        sb.append("    </imsmd:contribute>");
        sb.append("    <imsmd:contribute>");
        sb.append("      <imsmd:role>");
        sb.append("        <imsmd:source>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">LOMv1.0</imsmd:langstring>");
        sb.append("        </imsmd:source>");
        sb.append("        <imsmd:value>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">bar</imsmd:langstring>");
        sb.append("        </imsmd:value>");
        sb.append("      </imsmd:role>");
        sb.append("      <imsmd:centity>");
        sb.append("        <imsmd:vcard>xbar</imsmd:vcard>");
        sb.append("      </imsmd:centity>");
        sb.append("      <imsmd:date>");
        sb.append("        <imsmd:datetime>2000-01-01</imsmd:datetime>");
        sb.append("      </imsmd:date>");
        sb.append("    </imsmd:contribute>");
        sb.append("  </imsmd:lifecycle>");
        sb.append("  <imsmd:technical>");
        sb.append("    <imsmd:format>XMLL 1.0</imsmd:format>");
        sb.append("    <imsmd:location type=\"URI\">http://www.imsglobal.org/content</imsmd:location>");
        sb.append("  </imsmd:technical>");
        sb.append("  <imsmd:educational>");
        sb.append("    <imsmd:learningresourcetype>");
        sb.append("        <imsmd:source>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">LOMv1.0</imsmd:langstring>");
        sb.append("        </imsmd:source>");
        sb.append("        <imsmd:value>");
        sb.append("          <imsmd:langstring xml:lang=\"en\">Exercise</imsmd:langstring>");
        sb.append("        </imsmd:value>");
        sb.append("    </imsmd:learningresourcetype>");
        sb.append("  </imsmd:educational>");
        sb.append("  <imsmd:rights>");
        sb.append("    <imsmd:description>");
        sb.append("      <imsmd:langstring xml:lang=\"en\">unrestricted</imsmd:langstring>");
        sb.append("    </imsmd:description>");
        sb.append("  </imsmd:rights>");
        sb.append("  <imsmd:relation>");
        sb.append("    <imsmd:resource>");
        sb.append("      <imsmd:identifier>urn:foo</imsmd:identifier>");
        sb.append("    </imsmd:resource>");
        sb.append("    <imsmd:kind>");
        sb.append("      <imsmd:value>");
        sb.append("        <imsmd:langstring xml:lang=\"en\">isParentOf</imsmd:langstring>");
        sb.append("      </imsmd:value>");
        sb.append("    </imsmd:kind>");
        sb.append("    <imsmd:resource type=\"URI\">http://www.example.org/foo</imsmd:resource>");
        sb.append("  </imsmd:relation>");
        sb.append("  <imsmd:relation>");
        sb.append("    <imsmd:resource>");
        sb.append("      <imsmd:identifier>blahblah</imsmd:identifier>");
        sb.append("    </imsmd:resource>");
        sb.append("    <imsmd:kind>");
        sb.append("      <imsmd:value>");
        sb.append("        <imsmd:langstring xml:lang=\"en\">IsBasedOn</imsmd:langstring>");
        sb.append("      </imsmd:value>");
        sb.append("    </imsmd:kind>");
        sb.append("  </imsmd:relation>");
        sb.append("</imsmd:lom>");
        imsmd = sb.toString();
    }

    @Test
    public void testTransform() throws Exception {
    	Crosswalk xwalk = new ImsmdToOaidc();
    	InputStream in = new ByteArrayInputStream(imsmd.getBytes("UTF-8"));
    	OutputStream out = new ByteArrayOutputStream();
    	xwalk.translate("imsmd", in, "oai_dc", out);

    	validate(new StreamSource(new StringReader(out.toString())));
    }

    private void validate(StreamSource source)
            throws Exception {
        SchemaFactory sf =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("src/test/resources/oai_dc.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(source);
    }
}
