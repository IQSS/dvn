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
public class DownloadInstanceWriter implements MessageBodyWriter<DownloadInstance>{
    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == DownloadInstance.class;
    }

    public long getSize(DownloadInstance di, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return -1;
    }

    public void writeTo(DownloadInstance di, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        
    }
    
}
