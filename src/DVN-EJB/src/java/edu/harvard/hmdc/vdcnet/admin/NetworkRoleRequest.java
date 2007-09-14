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
 * RoleRequest.java
 *
 * Created on October 19, 2006, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.admin;

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
public class NetworkRoleRequest implements Serializable {

    @SequenceGenerator(name="networkrolerequest_gen", sequenceName="networkrolerequest_id_seq") 
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="networkrolerequest_gen")       
    private Long id;
    
    /** Creates a new instance of RoleRequest */
    public NetworkRoleRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

   

 

    public String toString() {
        return "edu.harvard.hmdc.vdcnet.vdc.RoleRequest[id=" + id + "]";
    }

    /**
     * Holds value of property networkRole.
     */
    @ManyToOne
    private edu.harvard.hmdc.vdcnet.admin.NetworkRole networkRole;

    /**
     * Getter for property role.
     * @return Value of property role.
     */
    public edu.harvard.hmdc.vdcnet.admin.NetworkRole getNetworkRole() {
        return this.networkRole;
    }

    /**
     * Setter for property role.
     * @param role New value of property role.
     */
    public void setNetworkRole(edu.harvard.hmdc.vdcnet.admin.NetworkRole networkRole) {
        this.networkRole = networkRole;
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
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NetworkRoleRequest)) {
            return false;
        }
        NetworkRoleRequest other = (NetworkRoleRequest)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }                 
}
