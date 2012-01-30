package edu.harvard.iq.dvn.api.entities;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream; 
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ejb.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author leonidandreev
 */
@Singleton
@Provider
public class MetadataWriter implements MessageBodyWriter<MetadataInstance> {

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == MetadataInstance.class;
    }

    public long getSize(MetadataInstance md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        if (md.isAvailable()) {
            if (md.isCached() && md.getCachedMetadataFile().exists()) {
                return md.getCachedMetadataFile().length();
            } else if (md.isByteArray() && md.getByteArray() != null) {
                return md.getByteArray().length; 
            }           
        }
        return -1;
    }

    public void writeTo(MetadataInstance md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        if (md.isAvailable()) {
            if (md.isByteArray()) {
                outstream.write(md.toString().getBytes());
            } else if (md.isCached()) {
                InputStream instream = new FileInputStream(md.getCachedMetadataFile());
                byte[] dataReadBuffer = new byte[4 * 8192];
                
                int i = 0;

                while ((i = instream.read(dataReadBuffer)) > 0) {
                    outstream.write(dataReadBuffer, 0, i);
                    outstream.flush(); // ?
                }

                instream.close();
            }
        }
    }
}