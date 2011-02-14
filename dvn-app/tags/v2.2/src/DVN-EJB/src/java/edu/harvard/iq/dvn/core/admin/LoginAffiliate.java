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
 * LoginAffiliate.java
 *
 * Created on August 7, 2006, 10:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.admin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;


/**
 *
 * @author Ellen Kraffmiller
 */

@Entity
public class LoginAffiliate  implements java.io.Serializable  {
    
    /** Creates a new instance of LoginDomain */
    public LoginAffiliate() {
    }

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

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setName(String name) {
        this.name = name;
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
    @JoinColumn(nullable=false)
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

    /**
     * Holds value of property url.
     */
    private String url;

    /**
     * Getter for property url.
     * @return Value of property url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for property url.
     * @param url New value of property url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LoginAffiliate)) {
            return false;
        }
        LoginAffiliate other = (LoginAffiliate)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }          
}
