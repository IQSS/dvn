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
 * Template.java
 *
 * Created on August 2, 2006, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import java.util.Collection;
import javax.persistence.*;
import edu.harvard.iq.dvn.core.vdc.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class Template implements java.io.Serializable {
    @OneToMany (mappedBy="template",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy("displayOrder")
    private List<TemplateField> templateFields;
    
    
    /**
     * Creates a new instance of Template
     */
    public Template() {
        metadata = new Metadata();
        metadata.setTemplate(this);
    }
    
    public Template(Template source) {
        metadata = new Metadata();
        metadata.setTemplate(this); 
        String sourceDescription = source.getDescription();
        this.setDescription(sourceDescription);
        List<TemplateField> sourceTemplateFields = source.getTemplateFields();
        templateFields = new ArrayList();
        for(TemplateField sourceField : sourceTemplateFields) {
            TemplateField tf = new TemplateField();
            tf.setTemplate(this);
            tf.setStudyField(sourceField.getStudyField());

            tf.setControlledVocabulary(sourceField.getControlledVocabulary());
            tf.setFieldInputLevelString(sourceField.getFieldInputLevelString());
            tf.setDisplayOrder(sourceField.getDisplayOrder());

            templateFields.add(tf);
        }
    }

    public List<TemplateField> getTemplateFields() {
        return templateFields;
    }

    public void setTemplateFields(List<TemplateField> templateFields) {
        this.templateFields = templateFields;
    }

    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Holds value of property templateFileCategories.
     */
    @OneToMany(mappedBy="template",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<TemplateFileCategory> templateFileCategories;

    /**
     * Getter for property templateFileCategories.
     * @return Value of property templateFileCategories.
     */
    public Collection<TemplateFileCategory> getTemplateFileCategories() {
        return this.templateFileCategories;
    }

    /**
     * Setter for property templateFileCategories.
     * @param templateFileCategories New value of property templateFileCategories.
     */
    public void setTemplateFileCategories(Collection<TemplateFileCategory> templateFileCategories) {
        this.templateFileCategories = templateFileCategories;
    }
  /**
     * Holds value of property version.
     */
    @Version
    private Long version;

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }   

    

    public boolean isNetwork() {
        return (vdc == null);
    }

   


   @ManyToOne
   private VDCNetwork vdcNetwork;

   public VDCNetwork getVdcNetwork() {
        return this.vdcNetwork;
   }
   
   public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
   }

    @ManyToOne
    private VDC vdc;

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }


    @OneToOne(cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Metadata metadata;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    
 
    
   public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Template)) {
            return false;
        }
        Template other = (Template)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }        
}
