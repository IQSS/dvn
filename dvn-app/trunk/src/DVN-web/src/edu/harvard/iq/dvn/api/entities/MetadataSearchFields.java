package edu.harvard.iq.dvn.api.entities;

import java.util.List;
import java.util.ArrayList;
import edu.harvard.iq.dvn.core.study.StudyField;

/**
 *
 * @author leonidandreev
 */
public class MetadataSearchFields {

    //private List<String> searchableMetadataFields;
    private List<StudyField> searchableMetadataFields;
    
    public MetadataSearchFields(List<StudyField> fields) {
        this.searchableMetadataFields = fields;        
    }

    public MetadataSearchFields() {
        super();
        this.searchableMetadataFields = new ArrayList<StudyField>();
        //lookupMetadataFiles(); 
    }
    
    public List<StudyField> getSearchableFields() {
	return searchableMetadataFields; 
    }
    
    public void setSearchableFields(List<StudyField> fields) {
        searchableMetadataFields = fields; 
    }
   
    
}