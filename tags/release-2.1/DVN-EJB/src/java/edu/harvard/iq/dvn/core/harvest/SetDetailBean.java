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
 * SetDetailBean.java
 *
 * Created on May 3, 2007, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.harvest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class SetDetailBean implements java.io.Serializable  {
    
    /** Creates a new instance of SetDetailBean */
    public SetDetailBean() {
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property spec.
     */
    private String spec;

    /**
     * Getter for property spec.
     * @return Value of property spec.
     */
    public String getSpec() {
        return this.spec;
    }

    /**
     * Setter for property spec.
     * @param spec New value of property spec.
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }
    
}
