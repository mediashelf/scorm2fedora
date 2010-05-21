package com.yourmediashelf.fedora.client.request;

import com.sun.jersey.api.client.ClientResponse;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * <p>A request object for FedoraClient.</p>
 *
 * <p>A FedoraRequest is built using any of the static methods that return a
 * representation of the various Fedora methods.</p>
 *
 * For example:
 * <pre>
 * {@code
 * FedoraRequest.ingest(null).build();
 * }</pre>
 *
 * creates an ingest request that can be passed to FedoraClient:
 * <pre>
 * {@code
 * FedoraClient fedora = new FedoraClient(credentials);
 * fedora.execute(FedoraRequest.ingest(null).build());
 * }</pre>
 *
 * @author Edwin Shin
 * @version $Id$
 */
public class FedoraRequest {
    private final FedoraMethod method;

    protected FedoraRequest(FedoraMethod method) {
        this.method = method;
    }

    public ClientResponse execute(FedoraClient fedora) throws FedoraClientException {
        ClientResponse response = method.execute(fedora);
        int status = response.getStatus();
        if (status >= 400) {
            String msg = response.getEntity(String.class);
            throw new FedoraClientException(String.format("HTTP %d Error: %s", status, msg));
        }
        return response;
    }

    /**
     *
     * @param pid
     * @param dsId
     * @return
     * @see AddDatastream
     */
    public static AddDatastream addDatastream(String pid, String dsId) {
        return new AddDatastream(pid, dsId);
    }

    /**
     *
     * @param pid
     * @return
     * @see Ingest
     */
    public static Ingest ingest(String pid) {
        return new Ingest(pid);
    }

    public static ModifyDatastream modifyDatastream(String pid, String dsId) {
        return new ModifyDatastream(pid, dsId);
    }

    public static PurgeObject purgeObject(String pid) {
        return new PurgeObject(pid);
    }

    public static GetRelationships getRelationships(String pid) {
        return new GetRelationships(pid);
    }

    public static AddRelationship addRelationship(String pid) {
        return new AddRelationship(pid);
    }

    public static PurgeRelationship purgeRelationship(String pid) {
        return new PurgeRelationship(pid);
    }
}
