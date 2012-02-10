package edu.harvard.iq.dvn.api.entities;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ejb.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;

import edu.harvard.iq.dvn.core.web.dataaccess.OptionalAccessService;


/**
 *
 * @author leonidandreev
 */
@Singleton
@Provider
public class DownloadInfoWriter implements MessageBodyWriter<DownloadInfo> {

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == DownloadInfo.class;
    }

    public long getSize(DownloadInfo md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return -1;
    }

    public void writeTo(DownloadInfo di, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        if (di == null || di.getStudyFile() == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        String openingTag = "<FileDownloadInfo>\n <studyFile fileId=\"" + di.getStudyFileId() + "\">\n";
        outstream.write(openingTag.getBytes());
        
        
        // Basic file information: 
       
        String fileName = "  <fileName>" + di.getFileName() + "</fileName>\n";
        String fileType = "  <fileMimeType>" + di.getMimeType() + "</fileMimeType>\n";
        String fileSize = "  <fileSize>" + di.getFileSize() + "</fileSize>\n";

        String formatOut = fileName + fileType + fileSize;
        outstream.write(formatOut.getBytes());
        
        // Authentication Information: 
        
        String open = "  <Authentication>\n";
         
        String userName = ""; 
        if (di.getAuthUserName() != null && !(di.getAuthUserName().equals(""))) {
            userName = "    <authUser>" + di.getAuthUserName() + "</authUser>\n";
        } 
        String authMethod = "    <authMethod>" + di.getAuthMethod() + "</authMethod>\n";
        String close = "  </Authentication>\n";

        formatOut = open + userName + authMethod + close;
        outstream.write(formatOut.getBytes());
        
        // Authorization
        
        String accessGranted = di.isAccessGranted() ? "true" : "false";
        formatOut = "  <Authorization directAccess=\"" + accessGranted + "\"/>\n";
        outstream.write(formatOut.getBytes());

        // Access Permissions
        
        if (di.isAccessPermissionsApply()) {
            accessGranted = di.isPassAccessPermissions() ? "true" : "false";
            open = "  <accessPermissions granted=\"" + accessGranted + "\">";
            String text = "    Restricted Access\n";        
            close = "  </accessPermissions>\n";

            formatOut = open + text + close;
            outstream.write(formatOut.getBytes());    
        }
        
        // Access Restrictions (Terms of Use)
        
        if (di.isAccessRestrictionsApply()) {
            accessGranted = di.isPassAccessPermissions() ? "true" : "false";
            open = "  <accessRestrictions granted=\"" + accessGranted + "\">";
            String text = "    Terms of Use\n";        
            close = "  </accessRestrictions>\n";

            formatOut = open + text + close;
            outstream.write(formatOut.getBytes());    
        }
          
        // Services supported: format conversions, thumbnails, subsetting
        
        outstream.write("  <accessServicesSupported>\n".getBytes());
        
        for (OptionalAccessService s : di.getServicesAvailable()) {
            open = "    <accessService>\n";
            
            String serviceName = "      <serviceName>" + s.getServiceName() + "</serviceName>\n";
            String serviceArgs = "      <serviceArgs>" + s.getServiceArguments() + "</serviceArgs>\n";
            String contentType = "      <contentType>" + s.getMimeType() + "</contentType>\n";
            String serviceDesc = "      <serviceDesc>" + s.getServiceDescription() + "</serviceDesc>\n";

            close = "    </accessService>\n";
            
            formatOut = open + serviceName + serviceArgs + contentType + serviceDesc + close;
            outstream.write(formatOut.getBytes());
            
        }
        
        outstream.write("  </accessServicesSupported>\n".getBytes());
        outstream.write(" </studyFile>\n</FileDownloadInfo>\n".getBytes());
    }
}