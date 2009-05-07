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
 * GNRS.java
 *
 * Created on August 7, 2006, 12:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.vdc;

/**
 *
 * @author Ellen Kraffmiller
 */
public class GNRS implements java.io.Serializable  {
    
    /** Creates a new instance of GNRS */
    public GNRS() {
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
    /**
     * @return Newly created namespace (for study handle)
     */
    public String newNameSpace(String vdcAddress, String vdcDescription, String vdcPassword) {
        // TODO: implementation
        return null;
    }
    /**
     * Register object in the namespace (need param's from Leonid)
     */
    public String register(String nameSpace, String objectId, String vdcPassword){
         // TODO: implementation
        return null;
    }
    /**
     *
     */
    public String lookup(String nameSpace, String objectId){
        // TODO: implementation
        return null;
    }
    
}
