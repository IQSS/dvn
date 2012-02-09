package edu.harvard.iq.dvn.core.web.dataaccess;

/**
 *
 * @author leonidandreev
 */
public class OptionalAccessService {
    private String serviceName; 
    private String serviceDescription; 
    private String contentMimeType; 
    private String serviceArguments; 
    
    public OptionalAccessService (String name, String mimeType, String arguments, String desc) {
        this.serviceName = name; 
        this.serviceDescription = desc; 
        this.contentMimeType = mimeType; 
        this.serviceArguments = arguments; 
    }
    
    public OptionalAccessService (String name, String mimeType, String arguments) {
        this(name, mimeType, arguments, null);
    }
    
    public String getServiceName() {
        return serviceName; 
    }
    
    public String getServiceDescription() {
        return serviceDescription; 
    }
    
    public String getMimeType() {
        return contentMimeType; 
    }
    
    public String getServiceArguments() {
        return serviceArguments;
    }
}
