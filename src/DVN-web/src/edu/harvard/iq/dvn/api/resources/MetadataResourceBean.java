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

import edu.harvard.iq.dvn.api.entities.MetadataInstance;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import edu.harvard.iq.dvn.api.exceptions.PermissionDeniedException;
import edu.harvard.iq.dvn.api.exceptions.AuthorizationRequiredException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.HttpHeaders;


/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataResourceBean {
    @Context private UriInfo ui;
    @Context private HttpHeaders headers;

    @EJB MetadataSingletonBean singleton;

    
    // Lookup by local (database) study ID:
     
    @Path("{stdyId}")
    @GET
    @Produces({ "application/xml" })

    public MetadataInstance getMetadataInstance(@PathParam("stdyId") Long studyId, @QueryParam("formatType") String formatType, @QueryParam("versionNumber") Long versionNumber, @QueryParam("partialExclude") String partialExclude, @QueryParam("partialInclude") String partialInclude) throws NotFoundException, ServiceUnavailableException, PermissionDeniedException, AuthorizationRequiredException {
        String authCredentials = getAuthCredentials();
        
        MetadataInstance m = singleton.getMetadata(studyId, formatType, versionNumber, partialExclude, partialInclude, authCredentials);

        if (m == null) {
            // Study (and/or version) not found;
            // returning 404
            throw new NotFoundException();
        }
        
        if (!m.isAccessAuthorized()) {
            if (authCredentials != null) {
                throw new PermissionDeniedException();
            } else {
                throw new AuthorizationRequiredException(); 
            }
        }
        
        if (!m.isAvailable()) {
            // Study exists, but the requested metadata format is not available.
            throw new ServiceUnavailableException();
        }

        return m;

    }

    
    @Path("hdl:{nameSpace}/{stdyId}")
    @GET
    @Produces({ "application/xml" })
    
    public MetadataInstance getMetadataInstanceByGlobalId(@PathParam("nameSpace") String nameSpace, @PathParam("stdyId") String stdyId, @QueryParam("formatType") String formatType, @QueryParam("versionNumber") Long versionNumber, @QueryParam("partialExclude") String partialExclude, @QueryParam("partialInclude") String partialInclude) throws NotFoundException, ServiceUnavailableException, PermissionDeniedException, AuthorizationRequiredException {
        String authCredentials = getAuthCredentials();
        
        MetadataInstance m = null; // = singleton.addMetadata("hdl:"+nameSpace+"/"+stdyId);
       
        m = singleton.getMetadata("hdl:"+nameSpace+"/"+stdyId, formatType, versionNumber, partialExclude, partialInclude, authCredentials);
        
        
        if (m == null) {
            // Study (and/or version) not found;
            // returning 404
            throw new NotFoundException();
        }  
        
        if (!m.isAccessAuthorized()) {
            if (authCredentials != null) {
                throw new PermissionDeniedException();
            } else {
                throw new AuthorizationRequiredException(); 
            }
        }
        if (!m.isAvailable()) {
            // Study exists, but the requested metadata format is not available.
            throw new ServiceUnavailableException(); 
        }
        
        return m;
        
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





