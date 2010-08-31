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
