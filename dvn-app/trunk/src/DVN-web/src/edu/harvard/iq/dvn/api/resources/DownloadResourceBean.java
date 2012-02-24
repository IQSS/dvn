package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.DownloadInfo;
import edu.harvard.iq.dvn.api.entities.DownloadInstance;

import edu.harvard.iq.dvn.api.exceptions.AuthorizationRequiredException;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;
import edu.harvard.iq.dvn.api.exceptions.PermissionDeniedException; 


import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.net.URI; 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class DownloadResourceBean {
    @Context HttpHeaders headers;
    @Context UriInfo uriInfo;


    @EJB FileAccessSingletonBean singleton;
    
    public DownloadResourceBean() {};

         
    @Path("{stdyFileId}")
    @GET

    public DownloadInstance getDownloadInstance (@PathParam("stdyFileId") Long studyFileId) throws NotFoundException, AuthorizationRequiredException, ServiceUnavailableException, PermissionDeniedException {
        String authCredentials = null; 
        
        URI resourcePath = uriInfo.getAbsolutePath();
        if (resourcePath != null) {
            if (!"https".regionMatches(0, resourcePath.toASCIIString(), 0, 5)) {
                throw new ServiceUnavailableException(); 
            }
        }
                
        for (String header : headers.getRequestHeaders().keySet()) {
            if (header.equalsIgnoreCase("Authorization")) {
                String headerValue = headers.getRequestHeader(header).get(0);
                if (headerValue != null && headerValue.startsWith("Basic ")) {
                    authCredentials = headerValue.substring(6);
                }
            }
        }

                  
        DownloadInfo dInfo = singleton.getDownloadInfo(studyFileId, authCredentials);
        
        if (dInfo == null) {
            // Study not found;
            // returning 404
            throw new NotFoundException(); 
        }

        // Create download instance: 
        
        DownloadInstance dInstance = new DownloadInstance (dInfo);
        
        if (dInstance == null) {
            return null;
        }
        
        // Check the parameters supplied: 
        
        String optionalParam = null; 
        String optionalParamValue = null; 
        
        for (String key : uriInfo.getQueryParameters().keySet()) {
            String value = uriInfo.getQueryParameters().getFirst(key);
            
            if (!dInstance.isDownloadServiceSupported(key, value)) {
                // Service unknown/not supported/bad arguments, etc.:
                throw new ServiceUnavailableException(); 
            }
            
            optionalParam = key; 
            optionalParamValue = value; 
        }
     
        // Are we willing to give them content? 
        
        if (!dInfo.isAccessGranted()) {
            if (authCredentials == null || authCredentials.equals("")) {
                // Access isn't authorized, but they haven't had a chance to 
                // authenticate yet. So we want to give them 401 / AUTH REQUIRED:
                throw new AuthorizationRequiredException();
            } else {
                // They have tried to authenticate, yet they are still not
                // authorized to get the content. They get 403:
                throw new PermissionDeniedException();
            }
        }
        
        return dInstance; 
        
    }
            
    
}
