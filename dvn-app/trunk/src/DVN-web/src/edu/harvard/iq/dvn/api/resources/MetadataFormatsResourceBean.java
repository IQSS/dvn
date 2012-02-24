
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
import javax.ws.rs.core.HttpHeaders;


/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataFormatsResourceBean {

    @Context private UriInfo ui;
    @Context private HttpHeaders headers;

    @EJB MetadataSingletonBean singleton;

    public MetadataFormatsResourceBean() {
        
    }
    
    // Lookup by local (database) study ID:
     
    @Path("{stdyId}")
    @GET
    @Produces({ "application/xml" })

    public MetadataFormats getMetadataFormats(@PathParam("stdyId") Long studyId) throws NotFoundException {
        String authCredentials = getAuthCredentials();

         
        MetadataFormats mf = singleton.getMetadataFormatsAvailable(studyId, authCredentials);

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
        String authCredentials = getAuthCredentials();        
        
        MetadataFormats mf = null; // = singleton.addMetadata("hdl:"+nameSpace+"/"+stdyId);
       
        mf = singleton.getMetadataFormatsAvailable("hdl:"+nameSpace+"/"+stdyId, authCredentials);
        
        
        if (mf == null) {
            // Study not found;
            // returning 404
            throw new NotFoundException();
        }
        
        return mf;
    }
    
    private String getAuthCredentials () {
        String authCredentials = null; 
        for (String header : headers.getRequestHeaders().keySet()) {
            if (header.equalsIgnoreCase("Authorization")) {
                String headerValue = headers.getRequestHeader(header).get(0);
                if (headerValue != null && headerValue.startsWith("Basic ")) {
                    authCredentials = headerValue.substring(6);
                }
            }
        }
        
        return authCredentials; 
    }
}





