
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataSearchResults;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Context;
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

    public MetadataSearchResults getMetadataSearchResults(@PathParam("queryString") String queryString) throws ServiceUnavailableException {
                 
        MetadataSearchResults msr = singleton.getMetadataSearchResults(queryString);

        if (msr == null) {
            // returning 503 - Service Unavailable
            // if no search hits were found, we would simply return an
            // empty hit list. So null searchResults object means something 
            // went wrong, preventing the search from completing. 
            throw new ServiceUnavailableException();
        }

        return msr;
    }

}





