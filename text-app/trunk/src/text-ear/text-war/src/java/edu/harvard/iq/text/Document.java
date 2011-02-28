/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ekraffmiller
 */
public class Document {
    private String id;
   
    private String filename;
    private DocumentSet documentSet;
    // Metadata ordered according to how it is read from the DocumentSet spreadsheet
    private LinkedHashMap<String, String> metadata = new LinkedHashMap<String,String>();

    public Document(DocumentSet set) {
        documentSet = set;
    }

    public String getSetId() {
        return documentSet.getSetId();
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedHashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(LinkedHashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    /*
     *  Special function for title since most documents should have at least this
     *  metadata field.
     */
    public String getTitle() {
        return  metadata.get("Title");
       
    }

    /**
     *
     * @param summaryFields list of summary metadata field names defined for the document set
     * @return ordered List of metadata (key value pairs) for this document, ordered first by summaryFields order, and then alphabetically
     */
    public ArrayList<Map.Entry>  getOrderedMetadata() {

        ArrayList ordered = new ArrayList<Map.Entry>();
        HashMap tempMap = (HashMap)metadata.clone();
        for(String field: documentSet.getSummaryFields()) {
            ordered.add(new AbstractMap.SimpleEntry(field,metadata.get(field)));
            tempMap.remove(field);
        }
        List remainingFields = Arrays.asList(tempMap.keySet().toArray());
        Collections.sort(remainingFields);
        for (Object field: remainingFields) {
              ordered.add(new AbstractMap.SimpleEntry(field,metadata.get(field)));
        }
        return ordered;
    }

    public String toString() {
        return "Document: setId="+documentSet.getSetId()+", id="+id+", filename="+filename+", metadata="+metadata;
    }

}
