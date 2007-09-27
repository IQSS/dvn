/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

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
    public static final int INCORRECT_VDC           = -2;
    public static final int EXPIRED_LIST            = -1;
    public static final int GENERIC_ERROR           =  0;
    
    public static final int COLLECTION_STUDIES      =  1;
    public static final int VDC_SEARCH              =  3;
    public static final int VDC_RECENT_STUDIES      =  4;
    public static final int GENERIC_LIST            =  100;    
    
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
    
    private Long vdcId;
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

    public Long getVdcId() {
        return vdcId;
    }

    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
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
