/*
 * RoleRequest.java
 *
 * Created on October 19, 2006, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.*;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class RoleRequest implements Serializable {

    @SequenceGenerator(name="rolerequest_gen", sequenceName="rolerequest_id_seq") 
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="rolerequest_gen")       
    private Long id;
    
    /** Creates a new instance of RoleRequest */
    public RoleRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleRequest)) {
            return false;
        }
        RoleRequest other = (RoleRequest)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    public String toString() {
        return "edu.harvard.hmdc.vdcnet.vdc.RoleRequest[id=" + id + "]";
    }

    /**
     * Holds value of property role.
     */
    @ManyToOne
    private Role role;

    /**
     * Getter for property role.
     * @return Value of property role.
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Setter for property role.
     * @param role New value of property role.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Holds value of property vdc.
     */
    @ManyToOne
    private VDC vdc;

    /**
     * Getter for property vdc.
     * @return Value of property vdc.
     */
    public VDC getVdc() {
        return this.vdc;
    }

    /**
     * Setter for property vdc.
     * @param vdc New value of property vdc.
     */
    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    /**
     * Holds value of property vdcUser.
     */
    @ManyToOne
    private VDCUser vdcUser;

    /**
     * Getter for property vdcUser.
     * @return Value of property vdcUser.
     */
    public VDCUser getVdcUser() {
        return this.vdcUser;
    }

    /**
     * Setter for property vdcUser.
     * @param vdcUser New value of property vdcUser.
     */
    public void setVdcUser(VDCUser vdcUser) {
        this.vdcUser = vdcUser;
    }
   
}
