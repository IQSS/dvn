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

package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
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
public class StudyRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Creates a new instance of RoleRequest */
    public StudyRequest() {
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

   
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.RoleRequest[id=" + id + "]";
    }

    /**
     * Holds value of property study.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private Study study;

    /**
     * Getter for property role.
     * @return Value of property role.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property role.
     * @param role New value of property role.
     */
    public void setStudy(Study study) {
        this.study = study;
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
    


    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyRequest)) {
            return false;
        }
        StudyRequest other = (StudyRequest)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }        
}
