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
/*
 * DataFileFormatType.java
 * 
 * Created on Jul 19, 2007, 5:50:19 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Gustavo
 */
@Entity
public class DataFileFormatType implements Serializable {
    
    public static final String ORIGINAL_FILE_DATA_FILE_FORMAT = "_original";
    
    private static final long serialVersionUID = 1L;
    private Long id;

    public DataFileFormatType() {
    }

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    String name;
    String value;
    String mimeType; 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mt) {
        this.mimeType = mt; 
    }
    
    public boolean isOriginalFileDataFileFormat() {
        return ( ORIGINAL_FILE_DATA_FILE_FORMAT.equals(value) );
    }
    
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
}
