package edu.harvard.iq.dvn.api.entities;

import java.util.List;

import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;

/**
 *
 * @author leonidandreev
 */
public class DownloadInstance {
    
    private DownloadInfo downloadInfo = null; 
    
    public DownloadInstance (DownloadInfo info) {
        this.downloadInfo = info; 
    }
    
    public DownloadInfo getDownloadInfo () {
        return downloadInfo; 
    }
    
    public void setDownloadInfo (DownloadInfo info) {
        this.downloadInfo = info; 
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
                if (serviceArg.equals("variables")) {
                    if ("subset".equals(dataService.getServiceName())) {
                        return true; 
                    }
                } else {
                    String argValuePair = serviceArg + "=" + serviceArgValue; 
                    if (argValuePair.equals(dataService.getServiceArguments())) {
                        return true; 
                    }
                }
            }
        }
        return false; 
    }
    
}
