package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.DownloadInfo;
import edu.harvard.iq.dvn.api.entities.DownloadInstance;

import edu.harvard.iq.dvn.api.exceptions.AuthorizationRequiredException;
import edu.harvard.iq.dvn.core.admin.VDCUser;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class DownloadResourceBean {
    @Context HttpHeaders headers;


    @EJB FileAccessSingletonBean singleton;
    
    public DownloadResourceBean() {};

         
    @Path("{stdyFileId}")
    @GET

    public DownloadInstance getDownloadInstance () {
        return null; 
    }
            
    
}
