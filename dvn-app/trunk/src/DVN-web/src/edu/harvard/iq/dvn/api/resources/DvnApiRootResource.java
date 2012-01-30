
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

    @Path("metadata")
    public MetadataResourceBean getMetadataResourceBean() {
        return r;
    }
}

