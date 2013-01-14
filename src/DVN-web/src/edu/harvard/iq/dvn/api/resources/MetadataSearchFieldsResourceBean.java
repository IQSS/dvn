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

import edu.harvard.iq.dvn.api.entities.MetadataSearchFields;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataSearchFieldsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    
    // The verb has no parameters:
    
    @Path("")
    @GET
    @Produces({ "application/xml" })

    public MetadataSearchFields getMetadataSearchFields() throws ServiceUnavailableException {
        
        MetadataSearchFields msf = singleton.getMetadataSearchFields();
        
        if (msf == null) {
            // Something bad happened - 
            // safe to say, the service is not available:
            throw new ServiceUnavailableException();
        }

        return msf;
    }

}





