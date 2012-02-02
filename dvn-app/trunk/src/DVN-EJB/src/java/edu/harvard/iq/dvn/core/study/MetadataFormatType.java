/*
 * MetadataFormatType.java
 * 
 * Created on Oct 3, 2007, 3:23:21 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author leonidandreev
 */

@Entity
public class MetadataFormatType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String metadataFormatName;
    private String metadataFormatSchema;
    private String metadataFormatNameSpace;
    private String metadataFormatMimeType; 
    
    private Boolean partialSelectSupported;
    private Boolean partialExcludeSupported; 

    
    
    public MetadataFormatType() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public String getName() {
        return metadataFormatName;
    }

    public void setName(String name) {
        this.metadataFormatName = name;
    }

    public String getFormatSchema() {
        return metadataFormatSchema;
    }

    public void setFormatSchema(String formatSchema) {
        this.metadataFormatSchema = formatSchema;
    }

    public String getMimeType() {
        return metadataFormatMimeType;
    }

    public void setMimeType(String mimeType) {
        this.metadataFormatMimeType = mimeType;
    }
    
    public String getNameSpace() {
        return metadataFormatNameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.metadataFormatNameSpace = nameSpace;
    }
    
    public Boolean isPartialSelectSupported() {
        return partialSelectSupported;
    }
    
    public void setPartialSelectSupported(Boolean supported) {
        this.partialSelectSupported = supported; 
    }
    
    public Boolean isPartialExcludeSupported() {
        return partialExcludeSupported;
    }
    
    public void setPartialExcludeSupported(Boolean supported) {
        this.partialExcludeSupported = supported; 
    }
    
}
