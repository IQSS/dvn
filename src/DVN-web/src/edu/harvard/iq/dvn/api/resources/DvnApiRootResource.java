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

import edu.harvard.iq.dvn.api.exceptions.ServiceUnavailableException;


import java.net.URI; 

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;


/**
 *
 * @author leonidandreev
 */
@Stateless
@Path("/")
public class DvnApiRootResource {
    @Context UriInfo uriInfo;

    // Metadata API resources: 
    @EJB MetadataResourceBean r;
    @EJB MetadataFormatsResourceBean rf; 
    @EJB MetadataSearchFieldsResourceBean rs;
    @EJB MetadataSearchResultsResourceBean rsr;

    // Access (download) API resources:
    @EJB DownloadInfoResourceBean dir;
    @EJB DownloadResourceBean dr; 
    

    @Path("metadata")
    public MetadataResourceBean getMetadataResourceBean() throws ServiceUnavailableException{
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return r;
    }
    
    @Path("metadataFormatsAvailable")
    public MetadataFormatsResourceBean getMetadataFormatsResourceBean() throws ServiceUnavailableException {
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return rf;
    }
    
    @Path("metadataSearchFields")
    public MetadataSearchFieldsResourceBean getMetadataSearchFieldsResourceBean() throws ServiceUnavailableException {
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return rs; 
    }
    
    @Path("metadataSearch")
    public MetadataSearchResultsResourceBean getMetadataSearchResultsResourceBean() throws ServiceUnavailableException {
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return rsr;
    }
    
    @Path("downloadInfo")
    public DownloadInfoResourceBean getDownloadInfoResourceBean() throws ServiceUnavailableException {
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return dir; 
    }
    
    @Path("download")
    public DownloadResourceBean getDownloadResourceBean() throws ServiceUnavailableException {
        if (!httpsCheck()) {
            throw new ServiceUnavailableException();
        }
        return dr;
    }
    
    private boolean httpsCheck () {
        URI resourcePath = uriInfo.getAbsolutePath();
        if (resourcePath != null) {
            if ("https".regionMatches(0, resourcePath.toASCIIString(), 0, 5)) {
                return true;
            } 
        }
        return false; 
    }
    
    
}

