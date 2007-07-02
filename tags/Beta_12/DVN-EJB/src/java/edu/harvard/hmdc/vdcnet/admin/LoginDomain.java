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
public class LoginDomain {
    
    /** Creates a new instance of LoginDomain */
    public LoginDomain() {
    }

    /**
     * Holds value of property id.
     */
   @SequenceGenerator(name="logindomain_gen", sequenceName="logindomain_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="logindomain_gen")        
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
     * Holds value of property ipAddress.
     */
    private String ipAddress;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
     * Holds value of property userGroup.
     */
    @ManyToOne
    private UserGroup userGroup;

    /**
     * Getter for property userGroup.
     * @return Value of property userGroup.
     */
    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    /**
     * Setter for property userGroup.
     * @param userGroup New value of property userGroup.
     */
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
 public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LoginDomain)) {
            return false;
        }
        LoginDomain other = (LoginDomain)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }              
}
