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

import edu.harvard.iq.dvn.api.entities.MetadataFormats;
import edu.harvard.iq.dvn.api.exceptions.NotFoundException;
import edu.harvard.iq.dvn.api.exceptions.AuthorizationRequiredException;

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

    public MetadataFormats getMetadataFormats(@PathParam("stdyId") Long studyId) throws NotFoundException, AuthorizationRequiredException {
        String authCredentials = getAuthCredentials();

        // This is a special hack for testing Basic auth through a browser: 
        // if 0 is supplied as a file id, the resource will throw a 401,
        // prompting the browser to show a login prompt:
        if (studyId.equals(new Long(0))) {
            
            if (authCredentials == null) {
                throw new AuthorizationRequiredException(); 
            } else {
                if (singleton.authenticateAccess(authCredentials) == null) {
                    throw new AuthorizationRequiredException();
                }
            }
        }     
         
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





