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
package edu.harvard.iq.dvn.api.entities;

import java.util.List;

import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;

/**
 *
 * @author leonidandreev
 */
public class DownloadInstance {
    
    private DownloadInfo downloadInfo = null; 
    private String conversionParam = null; 
    private String conversionParamValue = null; 
    
    public DownloadInstance (DownloadInfo info) {
        this.downloadInfo = info; 
    }
    
    public DownloadInfo getDownloadInfo () {
        return downloadInfo; 
    }
    
    public void setDownloadInfo (DownloadInfo info) {
        this.downloadInfo = info; 
    }
    
    public String getConversionParam () {
        return conversionParam; 
    }
    
    public void setConversionParam (String param) {
        this.conversionParam = param; 
    }
    
    public String getConversionParamValue () {
        return conversionParamValue; 
    }
    
    public void setConversionParamValue (String paramValue) {
        this.conversionParamValue = paramValue; 
    }
    
    // Move this method into the DownloadInfo instead -- ?
    
    public Boolean isDownloadServiceSupported (String serviceArg, String serviceArgValue) {
        if (downloadInfo == null || serviceArg == null) {
            return false;
        }
        
        List<OptionalAccessService> servicesAvailable = downloadInfo.getServicesAvailable();
        
        for (OptionalAccessService dataService : servicesAvailable) {
            if (dataService != null) {
                // Special case for the subsetting parameter (variables=<LIST>):
                //if (serviceArg.equals("variables")) {
                //    if ("subset".equals(dataService.getServiceName())) {
                //        conversionParam = "subset";
                //        conversionParamValue = serviceArgValue; 
                //        return true; 
                //    }
                //} else {
                    String argValuePair = serviceArg + "=" + serviceArgValue; 
                    if (argValuePair.equals(dataService.getServiceArguments())) {
                        conversionParam = serviceArg; 
                        conversionParamValue = serviceArgValue; 
                        return true; 
                    }
                //}
            }
        }
        return false; 
    }
    
    public String getServiceFormatType (String serviceArg, String serviceArgValue) {
        if (downloadInfo == null || serviceArg == null) {
            return null;
        }
        
        List<OptionalAccessService> servicesAvailable = downloadInfo.getServicesAvailable();
        
        for (OptionalAccessService dataService : servicesAvailable) {
            if (dataService != null) {
                // Special case for the subsetting parameter (variables=<LIST>):
                if (serviceArg.equals("variables")) {
                    if ("subset".equals(dataService.getServiceName())) {
                        conversionParam = "subset";
                        conversionParamValue = serviceArgValue; 
                        return dataService.getMimeType(); 
                    }
                } else {
                    String argValuePair = serviceArg + "=" + serviceArgValue; 
                    if (argValuePair.equals(dataService.getServiceArguments())) {
                        conversionParam = serviceArg; 
                        conversionParamValue = serviceArgValue; 
                        return dataService.getMimeType(); 
                    }
                }
            }
        }
        return null; 
    }
    
}
