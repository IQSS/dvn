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
