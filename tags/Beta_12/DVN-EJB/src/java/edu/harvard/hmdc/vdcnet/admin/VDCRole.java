/*
 * VDCRole.java
 *
 * Created on July 28, 2006, 2:19 PM
 *
 */

package edu.harvard.hmdc.vdcnet.admin;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
public class VDCRole {
      /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="vdcrole_gen", sequenceName="vdcrole_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="vdcrole_gen")
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
 
   
    @ManyToOne
    private VDC vdc;
    
    
    @ManyToOne
    private Role role;
    
    @ManyToOne
    private VDCUser vdcUser;

    /** Creates a new instance of VDCRole */
    public VDCRole() {
    }

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getVdcId() {
        if (vdc!=null) {
        return vdc.getId();
        } else {
            return null;
        }
    }

   

    public Long getRoleId() {
        if (role!=null) {
        return role.getId();
        } else {
            return null;
        }
    }

   
    public Long getVdcUserId() {
        if (vdcUser!=null) {
            return vdcUser.getId();
        } else {
            return null;
        }
    }

    
    public VDCUser getVdcUser() {
        return vdcUser;
    }

    public void setVdcUser(VDCUser vdcUser) {
        this.vdcUser = vdcUser;
    }

    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDCRole)) {
            return false;
        }
        VDCRole other = (VDCRole)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }   
    
}
