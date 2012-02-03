package edu.harvard.iq.dvn.api.entities;

import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author leonidandreev
 */
public class MetadataSearchResults {

    private List<String> studyIds = null;
    private String queryString; 
    
    public MetadataSearchResults(List<String> ids) {
        studyIds = ids;        
    }

    public MetadataSearchResults() {        
        studyIds = new ArrayList<String>();
    }
    
    public List<String> getStudyIds() {
	return studyIds; 
    }
    
    public void setStudyIds(List<String> ids) {
        studyIds = ids; 
    }
    
    public void addStudyId(String id) {
        if (studyIds == null) {
            studyIds = new ArrayList<String>(); 
        }
        studyIds.add(id);
    }
    
    public String getQueryString () {
        return queryString; 
    }
    
    public void setQueryString (String q) {
        queryString = q; 
    }
   
}