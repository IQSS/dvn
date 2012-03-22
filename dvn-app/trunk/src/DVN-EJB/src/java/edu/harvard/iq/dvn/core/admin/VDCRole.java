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
 * VDCRole.java
 *
 * Created on July 28, 2006, 2:19 PM
 *
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCRole implements java.io.Serializable  {
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
 
   
    @ManyToOne
     @JoinColumn(nullable=false)
   private VDC vdc;
    
    
    @ManyToOne
    @JoinColumn(nullable=false)
    private Role role;
    
    @ManyToOne
    @JoinColumn(nullable=false)
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
