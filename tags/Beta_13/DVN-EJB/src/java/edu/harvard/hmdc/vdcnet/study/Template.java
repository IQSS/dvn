/*
 * Template.java
 *
 * Created on August 2, 2006, 4:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.*;
import edu.harvard.hmdc.vdcnet.vdc.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class Template {
    @OneToMany (mappedBy="template",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<TemplateField> templateFields;
    
    
    /**
     * Creates a new instance of Template
     */
    public Template() {
    }

    public Collection<TemplateField> getTemplateFields() {
        return templateFields;
    }

    public void setTemplateFields(Collection<TemplateField> templateFields) {
        this.templateFields = templateFields;
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="template_gen", sequenceName="template_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="template_gen") 
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

    /**
     * Holds value of property templateFileCategories.
     */
    @OneToMany(mappedBy="template",cascade={CascadeType.REMOVE, CascadeType.MERGE})
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

    /**
     * Holds value of property vdcNetWork.
     */
   @OneToOne(mappedBy="defaultTemplate")
   private VDCNetwork vdcNetWork;

    /**
     * Getter for property vdcNetWork.
     * @return Value of property vdcNetWork.
     */
     public VDCNetwork getVdcNetWork() {
        return this.vdcNetWork;
    }

    /**
     * Setter for property vdcNetWork.
     * @param vdcNetWork New value of property vdcNetWork.
     */
    public void setVdcNetWork(VDCNetwork vdcNetWork) {
        this.vdcNetWork = vdcNetWork;
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
