/*
 * StudyListing.java
 *
 * Created on October 17, 2006, 6:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import com.sun.rave.web.ui.component.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gdurand
 */
public class StudyListing {
    public static final int EXPIRED_LIST            = -1;
    public static final int GENERIC_LIST            =  0;
    public static final int COLLECTION_STUDIES      =  1;
    public static final int VDC_SEARCH              =  3;
    public static final int VDC_RECENT_STUDIES      =  4;
    
    /** Creates a new instance of StudyListing */
    public StudyListing() {
    }

    public StudyListing(int mode) {
        this.mode=mode;
    }
    
    private int mode;
    private List studyIds = new ArrayList();
    private Long collectionId;
    private Tree collectionTree;
    
    private List searchTerms;
    private Map variableMap;
    
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public List getStudyIds() {
        return studyIds;
    }

    public void setStudyIds(List studyIds) {
        this.studyIds = studyIds;
    }      

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public Tree getCollectionTree() {
        return collectionTree;
    }

    public void setCollectionTree(Tree collectionTree) {
        this.collectionTree = collectionTree;
    }

    public List getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(List searchTerms) {
        this.searchTerms = searchTerms;
    }

    public Map getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(Map variableMap) {
        this.variableMap = variableMap;
    }
    
    
}
