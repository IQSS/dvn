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

import edu.harvard.iq.dvn.api.entities.MetadataSearchResults;
import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author leonidandreev
 */
@Stateless
public class MetadataSearchResultsResourceBean {

    @Context private UriInfo ui;
    @EJB MetadataSingletonBean singleton;

    
    @Path("{queryString}")
    @GET
    @Produces({ "application/xml" })

    public MetadataSearchResults getMetadataSearchResults(@PathParam("queryString") String queryString) throws ServiceUnavailableException {
                 
        MetadataSearchResults msr = singleton.getMetadataSearchResults(queryString);

        if (msr == null) {
            // returning 503 - Service Unavailable
            // if no search hits were found, we would simply return an
            // empty hit list. So null searchResults object means something 
            // went wrong, preventing the search from completing. 
            throw new ServiceUnavailableException();
        }

        return msr;
    }

}





