/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import java.util.HashMap;

/**
 *
 * @author ekraffmiller
 */
public class Document {
    private String id;
    private String setId;
    private String filename;
    private HashMap<String, String> metadata = new HashMap<String,String>();

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
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

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, String> metadata) {
        this.metadata = metadata;
    }

    /*
     *  Special function for title since most documents should have at least this
     *  metadata field.
     */
    public String getTitle() {
        return  metadata.get("Title");
       
    }

    public String toString() {
        return "Document: setId="+setId+", id="+id+", filename="+filename+", metadata="+metadata;
    }

}
