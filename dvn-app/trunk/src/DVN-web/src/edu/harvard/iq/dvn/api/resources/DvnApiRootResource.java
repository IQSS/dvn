
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
    @EJB MetadataSearchFieldsResourceBean rs;
    @EJB MetadataSearchResultsResourceBean rsr;

    @EJB DownloadInfoResourceBean dir; 

    @Path("metadata")
    public MetadataResourceBean getMetadataResourceBean() {
        return r;
    }
    
    @Path("metadataFormatsAvailable")
    public MetadataFormatsResourceBean getMetadataFormatsResourceBean() {
        return rf;
    }
    
    @Path("metadataSearchFields")
    public MetadataSearchFieldsResourceBean getMetadataSearchFieldsResourceBean() {
        return rs; 
    }
    
    @Path("metadataSearch")
    public MetadataSearchResultsResourceBean getMetadataSearchResultsResourceBean() {
        return rsr;
    }
    
    @Path("downloadInfo")
    public DownloadInfoResourceBean getDownloadInfoResourceBean() {
        return dir; 
    }
    
}

