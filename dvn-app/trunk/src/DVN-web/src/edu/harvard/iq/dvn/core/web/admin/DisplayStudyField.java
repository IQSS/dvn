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
 * DisplayStudyField.java
 *
 * Created on October 23, 2006, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.admin;

/**
 *
 * @author roberttreacy
 */
public class DisplayStudyField implements java.io.Serializable  {
    
    /** Creates a new instance of DisplayStudyField */
    public DisplayStudyField() {
    }
    
    private String name;
    private boolean displayBasicSearch;
    private boolean displayAdvancedSearch;
    private boolean displaySearchResults;
    private boolean displaySearchResultsDisabled;
    
    public String getName() {
        return name;
    }
    
    public boolean isDisplaySearchResults() {
        return displaySearchResults;
    }
    
    public boolean isDisplayBasicSearch() {
        return displayBasicSearch;
    }
    
    public boolean isDisplayAdvancedSearch() {
        return displayAdvancedSearch;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayBasicSearch(boolean displayBasicSearch) {
        this.displayBasicSearch = displayBasicSearch;
    }

    public void setDisplayAdvancedSearch(boolean displayAdvancedSearch) {
        this.displayAdvancedSearch = displayAdvancedSearch;
    }

    public void setDisplaySearchResults(boolean displaySearchResults) {
        this.displaySearchResults = displaySearchResults;
    }

    public boolean isDisplaySearchResultsDisabled() {
        return displaySearchResultsDisabled;
    }

    public void setDisplaySearchResultsDisabled(boolean displaySearchResultsDisabled) {
        this.displaySearchResultsDisabled = displaySearchResultsDisabled;
    }
}
