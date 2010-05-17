package com.yourmediashelf.fedora.client;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import eu.medsea.mimeutil.MimeUtil2;

/**
 * A client for Fedora's REST API.
 * 
 * @author Edwin Shin
 *
 */
public class FedoraClient {
	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
	
	private FedoraCredentials fc;
	private Client client;
	private MimeUtil2 mimeUtil;
	
	public FedoraClient(FedoraCredentials fc) {
		this.fc = fc;
		mimeUtil = new MimeUtil2();
		mimeUtil
				.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");

		// TODO validate fc
		// null check for username & password
		// null check for baseUrl

		// FIXME this isn't very security-minded
		if (fc.getBaseUrl().toString().startsWith("https")) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("SSL");
				ctx.init(null, null, null);
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage(), e);
			} catch (KeyManagementException e) {
				logger.error(e.getMessage(), e);
			}
			ClientConfig config = new DefaultClientConfig();
			// FIXME Consider using not-yet-commons-ssl hostnameverifier
			// config by property: strict, none, etc.
			config.getProperties().put(
					HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(null, ctx));
			client = Client.create(config);
		} else {
			client = Client.create();
		}
		client.setFollowRedirects(true);
	}

	/**
	 * Ingest (create) a new Fedora object.
	 * 
	 * @param the pid of the newly created object, or null for a server-assigned pid.
	 * @param queryParams ingest parameters (such as label, logMessage, namespace, etc) or null.
	 * @param requestContent String or File serialization of the object to ingest, 
	 * or null to create an empty object.
	 * @return ClientResponse
	 * @throws FedoraClientException
	 */
	public ClientResponse ingest(String pid, Map<String, String> queryParams,
			Object requestContent) throws FedoraClientException {
		ClientResponse response = null;
		WebResource wr = client.resource(fc.getBaseUrl().toString());
		wr
				.addFilter(new HTTPBasicAuthFilter(fc.getUsername(), fc
						.getPassword()));
		// wr.addFilter(new LoggingFilter(System.out));
		if (pid == null || pid.isEmpty()) {
			wr = wr.path("objects/new");
		} else {
			wr = wr.path(String.format("objects/%s", pid));
		}

		// queryParams such as label, logMessage, namespace, etc.
		if (queryParams != null) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				wr = wr.queryParam(e.getKey(), e.getValue());
			}
		}

		if (requestContent == null) {
			response = wr.post(ClientResponse.class);
		} else if (requestContent instanceof String) {
			response = wr.type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class,
					(String) requestContent);
		} else if (requestContent instanceof File) {
			File f = (File) requestContent;
			response = wr.type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class,
					f);
		} else {
			throw new IllegalArgumentException("unknown request content type");
		}
		if (response.getStatus() >= 400) {
			throw new FedoraClientException("HTTP Error: " + response.getStatus());
		}
		return response;
	}

	/**
	 * 
	 * @param pid the pid of the object. Required.
	 * @param dsId the id of the new datastream or null
	 * @param queryParams parameters (such as controlGroup, dsLocation, etc.) or null
	 * @param requestContent the String or File representation of the object or null
	 * @return ClientResponse
	 * @throws FedoraClientException
	 */
	public ClientResponse addDatastream(String pid, String dsId,
			Map<String, String> queryParams, Object requestContent) throws FedoraClientException {
		return addOrModifyDatastream(pid, dsId, queryParams, requestContent);
	}

	public ClientResponse modifyDatastream(String pid, String dsId,
			Map<String, String> queryParams, Object requestContent) throws FedoraClientException {
		return addOrModifyDatastream(pid, dsId, queryParams, requestContent);
	}
	
	public ClientResponse purgeObject(String pid, Map<String, String> queryParams) throws FedoraClientException {
		ClientResponse response = null;
		WebResource wr = client.resource(fc.getBaseUrl().toString());
		wr.addFilter(new HTTPBasicAuthFilter(fc.getUsername(), fc.getPassword()));
		wr = wr.path(String.format("objects/%s", pid));
		// queryParams such as logMessage
		if (queryParams != null) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				wr = wr.queryParam(e.getKey(), e.getValue());
			}
		}
		
		response = wr.delete(ClientResponse.class);
		if (response.getStatus() >= 400) {
			throw new FedoraClientException("HTTP Error: " + response.getStatus());
		}
		return response;
	}
	
	private ClientResponse addOrModifyDatastream(String pid, String dsId,
			Map<String, String> queryParams, Object requestContent) throws FedoraClientException {
		ClientResponse response = null;
		WebResource wr = client.resource(fc.getBaseUrl().toString());
		wr
				.addFilter(new HTTPBasicAuthFilter(fc.getUsername(), fc
						.getPassword()));
		//wr.addFilter(new LoggingFilter(System.out));
		if (dsId == null) {
			wr = wr.path(String.format("objects/%s/datastreams/", pid));
		} else {
			wr = wr.path(String.format("objects/%s/datastreams/%s", pid, dsId));
		}

		// queryParams such as controlGroup, dsLocation, etc.
		if (queryParams != null) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				wr = wr.queryParam(e.getKey(), e.getValue());
			}
		}

		if (requestContent == null) {
			response = wr.post(ClientResponse.class);
		} else if (requestContent instanceof String) {
			response = wr.type(MediaType.TEXT_XML_TYPE).post(ClientResponse.class,
					(String) requestContent);
		} else if (requestContent instanceof File) {
			File f = (File) requestContent;
			String mimeType = MimeUtil2.getMostSpecificMimeType(
					mimeUtil.getMimeTypes(f)).toString();
			response = wr.type(mimeType).post(ClientResponse.class, f);
		} else {
			throw new IllegalArgumentException("unknown request content type");
		}
		
		if (response.getStatus() >= 400) {
			throw new FedoraClientException("HTTP Error: " + response.getStatus());
		}
		return response;
	}
}
