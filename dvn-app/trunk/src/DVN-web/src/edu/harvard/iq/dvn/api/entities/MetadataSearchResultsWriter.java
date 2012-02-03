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

/**
 *
 * @author leonidandreev
 */
@Singleton
@Provider
public class MetadataSearchResultsWriter implements MessageBodyWriter<MetadataSearchResults> {

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == MetadataSearchResults.class;
    }

    public long getSize(MetadataSearchResults md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return -1;
    }

    public void writeTo(MetadataSearchResults msr, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        if (msr == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        String openingTag = "<MetadataSearchResults>\n";
        String queryString = "  <searchQuery>"+msr.getQueryString()+"</searchQuery>\n";
        String hitsOpen = "  <searchHits>\n";
        
        String head = openingTag + queryString + hitsOpen; 
        
        outstream.write(head.getBytes());
        
        for (String studyId : msr.getStudyIds()) {
            String studyLine =  "    <study ID=\"" + studyId + "\"/>\n";
            outstream.write(studyLine.getBytes());
        }
        
        String hitsClose = "  </searchHits>\n";
        String closingTag = "</MetadataSearchResults>\n";

        String tail = hitsClose + closingTag; 
        
        outstream.write(tail.getBytes());
    }
}