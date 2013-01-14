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
