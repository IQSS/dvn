/*
 * IndexEdit.java
 *
 * Created on March 1, 2007, 5:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

import java.io.Serializable;

/**
 *
 * @author roberttreacy
 */
public class IndexEdit implements Serializable {
    public enum Op {ADD, UPDATE, DELETE};
    /**
     * Creates a new instance of IndexEdit
     */
    public IndexEdit() {
    }

    private long studyId;


    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }

    
    private Op operation;

    public Op getOperation() {
        return operation;
    }

    public void setOperation(Op operation) {
        this.operation = operation;
    }
    
    
}
