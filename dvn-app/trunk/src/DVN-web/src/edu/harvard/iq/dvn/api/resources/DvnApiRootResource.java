
package edu.harvard.iq.dvn.api.resources;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 *
 * @author leonidandreev
 */
@Stateless
@Path("/")
public class DvnApiRootResource {

    @EJB MetadataResourceBean r;
    @EJB MetadataFormatsResourceBean rf; 

    @Path("metadata")
    public MetadataResourceBean getMetadataResourceBean() {
        return r;
    }
    
    @Path("metadataFormatsAvailable")
    public MetadataFormatsResourceBean getMetadataFormatsResourceBean() {
        return rf;
    }
}

