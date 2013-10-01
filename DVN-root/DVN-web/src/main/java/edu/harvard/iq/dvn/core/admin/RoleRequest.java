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
 * RoleRequest.java
 *
 * Created on October 19, 2006, 1:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.*;
import java.io.Serializable;
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
public class RoleRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        return "edu.harvard.iq.dvn.core.vdc.RoleRequest[id=" + id + "]";
    }

    /**
     * Holds value of property role.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
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
    @JoinColumn(nullable=false)
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
    @JoinColumn(nullable=false)
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
