/**
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * NetworkDataverseListing.java
 *
 * Created on October 17, 2007, 12:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

/**
 * This class creates a sortable object,
 * that is used to display the dataverses
 * on the network home page.
 *
 * @author wbossons
 */
public class NetworkDataverseListing implements Comparable, java.io.Serializable  {
    private String name, affiliation, alias, restricted, tooltip;
       
    /** Creates a new instance of NetworkDataverseListing */
    public NetworkDataverseListing() {
        
    }
    
    public NetworkDataverseListing(String name, String alias, String affiliation, String restricted) {
        this.name        = name;
        this.alias       = alias;
        this.affiliation = affiliation;
        this.restricted  = restricted;
    }
    
    public NetworkDataverseListing(String name, String alias, String affiliation, String restricted, String tooltip) {
        this.name        = name;
        this.alias       = alias;
        this.affiliation = affiliation;
        this.restricted  = restricted;
        this.tooltip     = tooltip;
    }
    
    public String getName() {
            return this.name;
    }
    
    public String getAlias() {
            return this.alias;
    }
    
    public String getAffiliation() {
            return this.affiliation;
    }
    
    public String getRestricted() {
            return this.restricted;
    }
    
    public String getTooltip() {
            return this.tooltip;
    }
    
    public String toString() {
        return "[name=" + name + " | affiliation=" + affiliation + " | alias=" + alias + " | restricted=" + restricted + " | tooltip=" + tooltip + "]";
      }

  public int compareTo(Object obj) {
    NetworkDataverseListing ndv = (NetworkDataverseListing) obj;
    int nameComparison = name.toUpperCase().compareTo(ndv.getName().toUpperCase());

    return ((nameComparison == 0) ? alias.compareTo(ndv.getAlias()) : nameComparison);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof NetworkDataverseListing)) {
      return false;
    }
    NetworkDataverseListing ndv = (NetworkDataverseListing) obj;
    return name.equals(ndv.getName())
        && alias.equals(ndv.getAlias());
  }

  public int hashCode() {
    return 31 * name.hashCode() + alias.hashCode();
  }
}
