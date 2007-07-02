/*
 * LoginDomain.java
 *
 * Created on August 7, 2006, 10:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;


/**
 *
 * @author Ellen Kraffmiller
 */

@Entity
public class PageDef {
    
    /** Creates a new instance of LoginDomain */
    public PageDef() {
    }

    /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="pagedef_gen", sequenceName="pagedef_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="pagedef_gen")        
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
     * Holds value of property path.
     */
    private String path;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setPath(String path) {
        this.path = path;
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
     * Holds value of property role.
     */
    @ManyToOne
    private edu.harvard.hmdc.vdcnet.admin.Role role;

    /**
     * Getter for property userGroup.
     * @return Value of property userGroup.
     */
    public edu.harvard.hmdc.vdcnet.admin.Role getRole() {
        return this.role;
    }

    /**
     * Setter for property userGroup.
     * @param userGroup New value of property userGroup.
     */
    public void setRole(edu.harvard.hmdc.vdcnet.admin.Role role) {
        this.role = role;
    }

    /**
     * Holds value of property networkRole.
     */
    @ManyToOne
    private NetworkRole networkRole;

    /**
     * Getter for property networkRole.
     * @return Value of property networkRole.
     */
    public NetworkRole getNetworkRole() {
        return this.networkRole;
    }

    /**
     * Setter for property networkRole.
     * @param networkRole New value of property networkRole.
     */
    public void setNetworkRole(NetworkRole networkRole) {
        this.networkRole = networkRole;
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
 public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PageDef)) {
            return false;
        }
        PageDef other = (PageDef)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }                     
}
