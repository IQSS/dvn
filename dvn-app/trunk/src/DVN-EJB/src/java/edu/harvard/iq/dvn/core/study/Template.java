/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class Template implements java.io.Serializable {
    @OneToMany (mappedBy="template",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<TemplateField> templateFields;
    
    
    /**
     * Creates a new instance of Template
     */
    public Template() {
        metadata = new Metadata();
    }

     public Template(ArrayList templateFields ) {
        metadata = new Metadata();
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

   


   @OneToOne(mappedBy="defaultTemplate")
   private VDCNetwork vdcNetwork;


     public VDCNetwork getVdcNetwork() {
        return this.vdcNetwork;
    }
     
    @ManyToOne
    private VDC vdc;

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }


    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
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
