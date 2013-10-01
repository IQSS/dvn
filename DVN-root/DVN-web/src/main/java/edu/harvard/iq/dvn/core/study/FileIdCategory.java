/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.AlphaNumericComparator;
import java.io.Serializable;

/**
 *
 * @author xyang
 */
public class FileIdCategory implements Comparable, Serializable{
    
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();    
    
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    private String category = "";
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }   
    
    public int compareTo(Object obj) {
        FileIdCategory idCat = (FileIdCategory)obj;
        return alphaNumericComparator.compare(this.category, idCat.category);
    }
    
    public String toString() {
        return "FileIdCategory [id = " + id +"; category = " + category + "]";
    }
}
