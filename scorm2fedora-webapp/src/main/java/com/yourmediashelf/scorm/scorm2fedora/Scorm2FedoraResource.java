package com.yourmediashelf.scorm.scorm2fedora;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.yourmediashelf.crosswalk.Crosswalk;
import com.yourmediashelf.crosswalk.CrosswalkException;
import com.yourmediashelf.crosswalk.impl.ImsmdToOaidc;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * A web service wrapper for Scorm2Fedora.
 *
 * @author Edwin Shin
 *
 */
@Path("/")
public class Scorm2FedoraResource {
	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private final Scorm2Fedora s2f;
	@Context ServletConfig sc;

	public Scorm2FedoraResource(@Context ServletConfig sc) {
	    Properties props = null;
        if (sc != null && sc.getInitParameter("baseUrl") != null) {
            logger.debug("Using web.xml init-params for configuration");
            props = new Properties();
            String[] pnames = {"username", "password", "baseUrl", "namespace", "scorm.dsid", "cmodel"};
            for (String pname : pnames) {
                String pval = sc.getInitParameter(pname);
                if (pval == null) {
                    logger.warn("init-param {} is null", pname);
                } else {
                    props.setProperty(pname, pval);
                }
            }
        } else {
            logger.debug("configuration init-params not found. Using default properties.");
        }
        s2f = new Scorm2Fedora(props);
	}

	/**
	 * Deposits a SCORM 1.2 package with IMS Metadata into a Fedora repository.
	 *
	 * @param request the multipart/form-data request that contains the SCORM
	 * package
	 * @return the pid and location (URI) of the newly created Fedora object.
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ScormDeposit deposit(@Context HttpServletRequest request) {
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			logger.warn("Request did not contain multipart content.");
			throw new WebApplicationException(Response.status(
					Response.Status.BAD_REQUEST).entity(
					"request does not contain multipart content.").type(
					MediaType.TEXT_PLAIN).build());
		}

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		// Parse the request
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();
				if (item.isFormField()) {
					logger.debug("Ignoring form field \"{}\".", name);
					stream.close();
				} else {
					logger.debug("Found file field {} with file name \"{}\".",
							name, item.getName());
					return s2f.deposit(stream, item.getName());
				}
			}
		} catch (FileUploadException e) {
			logger.debug(e.getMessage(), e);
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			logger.debug(e.getMessage(), e);
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (CrosswalkException e) {
			logger.debug(e.getMessage(), e);
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		} catch (FedoraClientException e) {
			logger.debug(e.getMessage(), e);
			throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
		}
		logger.warn("Request did not contain a file.");
		throw new WebApplicationException(Response.status(
				Response.Status.BAD_REQUEST).entity(
				"request does not contain a file.").type("text/plain").build());
	}

	/**
	 * Placeholder for future implementation
	 *
	 * @param imsmd
	 * @param oaidc
	 * @throws CrosswalkException
	 */
	public void imsmd2oaidc(InputStream imsmd, OutputStream oaidc)
			throws CrosswalkException {
		Crosswalk xwalk = new ImsmdToOaidc();
		xwalk.translate("imsmd", imsmd, "oai_dc", oaidc);
	}
}
