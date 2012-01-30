
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataInstance;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataHolderSingletonBean singleton;

    
    // Lookup by local (database) study ID:
     
    @Path("{stdyId}")
    @GET
    @Produces({ "application/xml" })

    public MetadataInstance getMetadataInstance(@PathParam("stdyId") Long studyId, @QueryParam("formatType") String formatType, @QueryParam("versionNumber") Long versionNumber) throws WebApplicationException {
         
        MetadataInstance m = singleton.getMetadata(studyId, formatType, versionNumber);

        if (m == null) {
            // Study (and/or version) not found;
            // returning 404
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        if (!m.isAvailable()) {
            // Study exists, but the requested metadata format is not available.
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        }

        return m;

    }

    
    @Path("hdl:{nameSpace}/{stdyId}")
    @GET
    @Produces({ "application/xml" })
    
    public MetadataInstance getMetadataInstanceByGlobalId(@PathParam("nameSpace") String nameSpace, @PathParam("stdyId") String stdyId, @QueryParam("formatType") String formatType, @QueryParam("versionNumber") Long versionNumber) throws WebApplicationException {
        
        
        MetadataInstance m = null; // = singleton.addMetadata("hdl:"+nameSpace+"/"+stdyId);
       
        m = singleton.getMetadata("hdl:"+nameSpace+"/"+stdyId, formatType, versionNumber);
        
        
        if (m == null) {
            // Study (and/or version) not found;
            // returning 404
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }  
        
        if (!m.isAvailable()) {
            // Study exists, but the requested metadata format is not available.
            throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
        }
        
        return m;
        
    }
}





