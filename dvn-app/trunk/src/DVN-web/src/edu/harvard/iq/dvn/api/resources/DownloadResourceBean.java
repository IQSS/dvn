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

    public DownloadInstance getDownloadInstance (@PathParam("stdyFileId") Long studyFileId) throws WebApplicationException, AuthorizationRequiredException{
        String authCredentials = null; 
        
        for (String header : headers.getRequestHeaders().keySet()) {
            if (header.equalsIgnoreCase("Authorization")) {
                String headerValue = headers.getRequestHeader(header).get(0);
                if (headerValue != null && headerValue.startsWith("Basic ")) {
                    authCredentials = headerValue.substring(6);
                }
            }
        }

                  
        DownloadInfo di = singleton.getDownloadInfo(studyFileId, authCredentials);
        
        if (di == null) {
            // Study not found;
            // returning 404
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Are we willing to give them content? 
        
        if (!di.isAccessGranted()) {
            if (authCredentials == null || authCredentials.equals("")) {
                // Access isn't authorized, but they haven't had a chance to 
                // authenticate yet. So we want to give them 401 / AUTH REQUIRED:
                throw new AuthorizationRequiredException();
            } else {
                // They have tried to authenticate, yet they are still not
                // authorized to get the content. They get 403:
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
        }
        
        return new DownloadInstance (di);
    }
            
    
}
