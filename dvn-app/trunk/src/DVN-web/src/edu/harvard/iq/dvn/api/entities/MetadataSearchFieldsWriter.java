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

import edu.harvard.iq.dvn.core.study.StudyField;
/**
 *
 * @author leonidandreev
 */
@Singleton
@Provider
public class MetadataSearchFieldsWriter implements MessageBodyWriter<MetadataSearchFields> {

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == MetadataSearchFields.class;
    }

    public long getSize(MetadataSearchFields md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return -1;
    }

    public void writeTo(MetadataSearchFields msf, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        if (msf == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        String openingTag = "<MetadataSearchFields>\n";
        
        outstream.write(openingTag.getBytes());
        
        for (StudyField searchField : msf.getSearchableFields()) {
            String open =  "  <SearchableField";
            // In the future we can add attributes specifying the type of the
            // field, numeric formats, etc. 
            open = open.concat(">\n");

            
            String name =   "    <fieldName>" + searchField.getName() + "</fieldName>\n";
            String description = "    <fieldDescription>" + searchField.getDescription() + "</fieldDescription>\n";
            String close =  "  </SearchableField>\n";
            
            String formatOut = open + name + description + close; 
            outstream.write(formatOut.getBytes());
        }
        
        outstream.write("</MetadataSearchFields>\n".getBytes());
    }
}