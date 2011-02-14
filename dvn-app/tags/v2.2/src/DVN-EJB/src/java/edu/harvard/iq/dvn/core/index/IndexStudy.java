/*
 * IndexStudy.java
 * 
 * Created on Sep 19, 2007, 1:49:23 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.index;

/**
 *
 * @author roberttreacy
 */
public class IndexStudy  implements java.io.Serializable  {
    private long studyId;

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }
    
    public boolean equals(IndexStudy is){
        return is.studyId == this.studyId;
    }

}
