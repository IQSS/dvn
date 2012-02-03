package edu.harvard.iq.dvn.api.entities;

import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author leonidandreev
 */
public class MetadataSearchFields {

    private List<String> searchableMetadataFields; 
    
    public MetadataSearchFields(List<String> fields) {
        this.searchableMetadataFields = fields;        
    }

    public MetadataSearchFields() {
        super();
        this.searchableMetadataFields = new ArrayList<String>();
        //lookupMetadataFiles(); 
    }
    
    public List<String> getSearchableFields() {
	return searchableMetadataFields; 
    }
    
    public void setSearchableFields(List<String> fields) {
        searchableMetadataFields = fields; 
    }
   
    
}