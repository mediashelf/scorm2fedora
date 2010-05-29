package com.yourmediashelf.scorm.scorm2fedora;

import static com.yourmediashelf.fedora.client.FedoraClient.addDatastream;
import static com.yourmediashelf.fedora.client.FedoraClient.ingest;
import static com.yourmediashelf.fedora.client.FedoraClient.modifyDatastream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.yourmediashelf.crosswalk.Crosswalk;
import com.yourmediashelf.crosswalk.CrosswalkException;
import com.yourmediashelf.crosswalk.impl.ImsmdToOaidc;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.RelsExt;
import com.yourmediashelf.fedora.client.response.IngestResponse;
import com.yourmediashelf.util.FileUtils;

/**
 * Scorm2Fedora accepts a SCORM 1.2 package with IMS Metadata and deposits it
 * into a Fedora repository. The IMS Metadata is converted into Dublin Core and
 * made available the Fedora object's DC datastream.
 *
 * @author Edwin Shin
 *
 */
public class Scorm2Fedora {
	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private final Properties props;
	private final FedoraClient fedora;

	/**
	 * Configuration properties
	 * <dl>
	 *   <dt>username
	 *   <dd>username for the Fedora repository to be used by Scorm2Fedora to
	 *   deposit SCORM packages.
	 *   <dt>password
	 *   <dd>password for the Fedora user
	 *   <dt>baseUrl
	 *   <dd>URL of the Fedora repository (e.g. http://localhost:8080/fedora)
	 *   <dt>namespace
	 *   <dd>PID namespace for the SCORM objects (optional)
	 *   <dt>cmodel
	 *   <dd>The URI of the cmodel newly created objects (optional)
	 *   <dt>scorm.dsid
	 *   <dd>The datastream ID for the SCORM package (e.g. SCORM)
	 * </dl>
	 *
	 * @param props configuration properties
	 */
	public Scorm2Fedora(Properties props) {
		if (props == null) {
			logger.debug("null properties file, will use defaults");
			props = new Properties();
			try {
				props.load(getClass().getResourceAsStream("/scorm2fedora.properties"));
			} catch (IOException e) {
				logger.warn("unable to load properties from classpath");
			}
		}
		this.props = props;

		String username = props.getProperty("username", "fedoraAdmin");
		String password = props.getProperty("password", "fedoraAdmin");
		URL baseUrl;
		try {
			baseUrl = new URL(props.getProperty("baseUrl",
					"http://localhost:8080/fedora"));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Malformed baseUrl: "
					+ e.getMessage(), e);
		}
		fedora = new FedoraClient(new FedoraCredentials(baseUrl, username,
				password));
	}

	/**
	 *
	 * @param in the SCORM package
	 * @param filename filename of the SCORM package, used as the dsLabel
	 * @return
	 * @throws IOException
	 * @throws CrosswalkException
	 * @throws FedoraClientException
	 */
	public ScormDeposit deposit(InputStream in, String filename) throws IOException, CrosswalkException, FedoraClientException {
		Set<File> tempFiles = new HashSet<File>();

		String pid = null;
		URI location = null;
		try {
			// create a temp file for the scorm package
			File scorm = File.createTempFile("scorm", null);
			tempFiles.add(scorm);
			OutputStream fout = new FileOutputStream(scorm);
			FileUtils.copy(in, fout);
			in.close();

			// extract the manifest
			in = new BufferedInputStream(new FileInputStream(scorm));
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry entry;
			File manifest = null;
			while ((entry = zin.getNextEntry()) != null) {
				if (entry.getName().equals("imsmanifest.xml")) {
					manifest = File.createTempFile("man", null);
					tempFiles.add(manifest);
					FileUtils.copy(zin, new FileOutputStream(manifest));
					break;
				}
			}
			zin.close();
			if (manifest == null) {
				throw new IllegalArgumentException("package has no manifest!");
			}

			// convert manifest's metadata to oai_dc
			in = new FileInputStream(manifest);
			File oaidc = File.createTempFile("oaidc", null);
			tempFiles.add(oaidc);
			OutputStream out = new FileOutputStream(oaidc);
			imsmd2oaidc(in, out);
			in.close();
			out.close();

			// create new fedora object
			String namespace = props.getProperty("namespace");
			IngestResponse response = ingest(pid).namespace(namespace).execute(fedora);

			pid = response.getPid();
			location = response.getLocation();

			// add original scorm package as datastream
			String dsId = props.getProperty("scorm.dsid", "SCORM");
			addDatastream(pid, dsId).controlGroup("M")
			                        .dsLabel(filename)
			                        .content(scorm).execute(fedora);

			// update DC datastream with new oai_dc
			modifyDatastream(pid, "DC").content(oaidc).execute(fedora);

			// Add content model to RELS-EXT
			String cmodel = props.getProperty("cmodel");
			if (cmodel != null) {
    			String relsExt = new RelsExt.Builder(pid).addCModel(cmodel).build().toString();
    			addDatastream(pid, "RELS-EXT")
    			    .dsLabel("RDF Statements about this object")
    			    .formatURI("info:fedora/fedora-system:FedoraRELSExt-1.0")
    			    .mimeType("application/rdf+xml").content(relsExt)
    			    .execute(fedora);
    			logger.debug("Created RELS-EXT with cmodel \"{}\".", cmodel);
			} else {
			    logger.info("Property \"cmodel\" not found. Skipping RELS-EXT.");
			}

		} finally {
			for (File f : tempFiles) {
				f.delete();
				logger.debug("Deleted temp file \"{}\".", f.getAbsolutePath());
			}
		}
		return new ScormDeposit(pid, location);
	}

	/**
	 * Crosswalks IMS Metadata to OAI DC per the IMS Learning Resource Meta-Data
	 * Best Practice and Implementation Guide, Version 1.2.1 Final Specification.
	 *
	 * @param imsmd
	 * @param oaidc
	 * @throws CrosswalkException
	 * @see http://www.imsglobal.org/metadata/imsmdv1p2p1/imsmd_bestv1p2p1.html#1242547
	 */
	public void imsmd2oaidc(InputStream imsmd, OutputStream oaidc) throws CrosswalkException {
		Crosswalk xwalk = new ImsmdToOaidc();
		xwalk.translate("imsmd", imsmd, "oai_dc", oaidc);
	}
}
