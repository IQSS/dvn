
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.MetadataFormats;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;

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
public class MetadataFormatsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    public MetadataFormatsResourceBean() {
        
    }
    
    // Lookup by local (database) study ID:
     
    @Path("{stdyId}")
    @GET
    @Produces({ "application/xml" })

    public MetadataFormats getMetadataFormats(@PathParam("stdyId") Long studyId) throws NotFoundException {
         
        MetadataFormats mf = singleton.getMetadataFormatsAvailable(studyId);

        if (mf == null) {
            // Study not found;
            // returning 404
            throw new NotFoundException();
        }

        return mf;
    }

    
    @Path("hdl:{nameSpace}/{stdyId}")
    @GET
    @Produces({ "application/xml" })
    
    public MetadataFormats getMetadataFormatsByGlobalId(@PathParam("nameSpace") String nameSpace, @PathParam("stdyId") String stdyId) throws NotFoundException {
        
        
        MetadataFormats mf = null; // = singleton.addMetadata("hdl:"+nameSpace+"/"+stdyId);
       
        mf = singleton.getMetadataFormatsAvailable("hdl:"+nameSpace+"/"+stdyId);
        
        
        if (mf == null) {
            // Study not found;
            // returning 404
            throw new NotFoundException();
        }
        
        return mf;
    }
}





