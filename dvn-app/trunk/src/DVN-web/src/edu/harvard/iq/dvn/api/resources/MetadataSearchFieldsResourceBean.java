
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataSearchFields;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataSearchFieldsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    
    // The verb has no parameters:
    
    @Path("")
    @GET
    @Produces({ "application/xml" })

    public MetadataSearchFields getMetadataSearchFields() throws WebApplicationException {
        
        MetadataSearchFields msf = singleton.getMetadataSearchFields();
        
        if (msf == null) {
            // Something bad happened - 
            // safe to say, the service is not available:
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        }

        return msf;
    }

}





