/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.api.resources;

import edu.harvard.iq.dvn.api.entities.DownloadInfo;
import edu.harvard.iq.dvn.api.entities.DownloadInstance;

import edu.harvard.iq.dvn.api.exceptions.AuthorizationRequiredException;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;
import edu.harvard.iq.dvn.api.exceptions.PermissionDeniedException; 


import javax.ejb.EJB;
import javax.ejb.Stateless;
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
