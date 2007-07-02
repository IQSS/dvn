/*
 * DisplayStudyField.java
 *
 * Created on October 23, 2006, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.admin;

/**
 *
 * @author roberttreacy
 */
public class DisplayStudyField {
    
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
