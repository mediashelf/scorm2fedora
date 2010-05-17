/**
 * 
 */
package com.yourmediashelf.crosswalk.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.yourmediashelf.crosswalk.Crosswalk;
import com.yourmediashelf.crosswalk.CrosswalkException;

/**
 * A metadata crosswalk for IMS Metadata to OAI DC.
 * 
 * @author Edwin Shin
 * 
 */
public class ImsmdToOaidc implements Crosswalk {
	Map<String, Set<String>> supportedCrosswalks = new HashMap<String, Set<String>>();
	
	public ImsmdToOaidc() {
		Set<String> imsmdOutputs = new HashSet<String>();
		imsmdOutputs.add("oai_dc");
		supportedCrosswalks.put("imsmd", imsmdOutputs);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yourmediashelf.xwalk.Crosswalk#getSupportedCrosswalks()
	 */
	@Override
	public Map<String, Set<String>> getSupportedCrosswalks() {
		return supportedCrosswalks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yourmediashelf.xwalk.Crosswalk#isSupported(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isSupported(String inFormat, String outFormat) {
		Set<String> supported = getSupportedCrosswalks().get(inFormat);
		return supported != null && supported.contains(outFormat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yourmediashelf.xwalk.Crosswalk#translate(java.lang.String,
	 * java.io.InputStream, java.lang.String, java.io.OutputStream)
	 */
	@Override
	public void translate(String inFormat, InputStream in, String outFormat,
			OutputStream out) throws CrosswalkException {
		Source xsltSource = getXsltSource(inFormat, outFormat);
		TransformerFactory transFact = TransformerFactory.newInstance();
		transFact.setAttribute("http://saxon.sf.net/feature/version-warning",
				Boolean.FALSE);
		try {
			Transformer trans = transFact.newTransformer(xsltSource);

			Source source = new StreamSource(in);
			StreamResult xformResult = new StreamResult(out);
			trans.transform(source, xformResult);
		} catch (TransformerException e) {
			throw new CrosswalkException(e.getMessage(), e);
		}

	}
	
	private Source getXsltSource(String inFormat, String outFormat) {
		if (isSupported(inFormat, outFormat)) {
			return new StreamSource(getClass().getResourceAsStream("/imsmd2oaidc.xsl"));
		} else {
			throw new IllegalArgumentException("Unsupported: " + inFormat + " to " + outFormat);
		}
	}
}
