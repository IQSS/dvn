
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataFormats;
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
public class MetadataFormatsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    public MetadataFormatsResourceBean() {
        
    }
    
    // Lookup by local (database) study ID:
     
    @Path("{stdyId}")
    @GET
    @Produces({ "application/xml" })

    public MetadataFormats getMetadataFormats(@PathParam("stdyId") Long studyId) throws WebApplicationException {
         
        MetadataFormats mf = singleton.getMetadataFormatsAvailable(studyId);

        if (mf == null) {
            // Study not found;
            // returning 404
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return mf;
    }

    
    @Path("hdl:{nameSpace}/{stdyId}")
    @GET
    @Produces({ "application/xml" })
    
    public MetadataFormats getMetadataFormatsByGlobalId(@PathParam("nameSpace") String nameSpace, @PathParam("stdyId") String stdyId) throws WebApplicationException {
        
        
        MetadataFormats mf = null; // = singleton.addMetadata("hdl:"+nameSpace+"/"+stdyId);
       
        mf = singleton.getMetadataFormatsAvailable("hdl:"+nameSpace+"/"+stdyId);
        
        
        if (mf == null) {
            // Study not found;
            // returning 404
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        return mf;
    }
}





