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
    
    public Boolean isDownloadServiceSupported (String serviceTag) {
        if (downloadInfo == null || serviceTag == null) {
            return false;
        }
        
        List<OptionalAccessService> servicesAvailable = downloadInfo.getServicesAvailable();
        
        for (OptionalAccessService dataService : servicesAvailable) {
            if (serviceTag.equals(dataService.getServiceName())) {
                return true; 
            }
        }
        
        return false; 
    }
    
}
