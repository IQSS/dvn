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

import edu.harvard.iq.dvn.core.study.MetadataFormatType; 

/**
 *
 * @author leonidandreev
 */
@Singleton
@Provider
public class MetadataFormatsWriter implements MessageBodyWriter<MetadataFormats> {
    public MetadataFormatsWriter() {
        super();
    }

    public boolean isWriteable(Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return clazz == MetadataFormats.class;
    }

    public long getSize(MetadataFormats md, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType) {
        return -1;
    }

    public void writeTo(MetadataFormats mf, Class<?> clazz, Type type, Annotation[] annotation, MediaType mediaType, MultivaluedMap<String, Object> arg5, OutputStream outstream) throws IOException, WebApplicationException {
        if (mf == null || mf.getGlobalStudyId() == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        String openingTag = "<MetadataFormatsAvailable studyId=\"" + mf.getGlobalStudyId() + "\">\n";
        
        outstream.write(openingTag.getBytes());
        
        for (MetadataFormatType typeAvailable : mf.getFormatTypesAvailable()) {
            String open =  "  <formatAvailable";
            if (typeAvailable.isPartialSelectSupported()) {
                open = open.concat(" selectSupported=\"true\"");
            }
            if (typeAvailable.isPartialExcludeSupported()) {
                open = open.concat(" excludeSupported=\"true\"");
            }
            open = open.concat(">\n");

            
            String name =   "    <formatName>" + typeAvailable.getName() + "</formatName>\n";
            String schema = "    <formatSchema>" + typeAvailable.getFormatSchema() + "</formatSchema>\n";
            String mime =   "    <formatMime>" + typeAvailable.getMimeType() + "</formatMime>\n";
            String close =  "  </formatAvailable>\n";
            
            String formatOut = open + name + schema + mime + close; 
            outstream.write(formatOut.getBytes());
        }
        
        outstream.write("</MetadataFormatsAvailable>\n".getBytes());
    }
}