
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataSearchResults;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataSearchResultsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    
    @Path("{queryString}")
    @GET
    @Produces({ "application/xml" })

    public MetadataSearchResults getMetadataSearchResults(@PathParam("queryString") String queryString) throws WebApplicationException {
                 
        MetadataSearchResults msr = singleton.getMetadataSearchResults(queryString);

        if (msr == null) {
            // returning 404 (?)
            // (we should probably return some error code here, instead of 
            // 404; if no search hits have been found, we are returning an 
            // empty hit list. So null searchResults object means something 
            // went wrong, preventing the search from completing. 
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        }

        return msr;
    }

}





