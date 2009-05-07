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
 * HandlePrefix.java
 *
 * Created on June 13, 2007, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Entity class HandlePrefix
 * 
 * @author Ellen Kraffmiller
 */
@Entity
public class HandlePrefix implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Creates a new instance of HandlePrefix */
    public HandlePrefix() {
    }

    /**
     * Gets the id of this HandlePrefix.
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the id of this HandlePrefix to the specified value.
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes 
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this HandlePrefix.  The result is 
     * <code>true</code> if and only if the argument is not null and is a HandlePrefix object that 
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HandlePrefix)) {
            return false;
        }
        HandlePrefix other = (HandlePrefix)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs 
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.HandlePrefix[id=" + id + "]";
    }

    /**
     * Holds value of property prefix.
     */
    private String prefix;

    /**
     * Getter for property prefix.
     * @return Value of property prefix.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Setter for property prefix.
     * @param prefix New value of property prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
}
