/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * StudyListing.java
 *
 * Created on October 17, 2006, 6:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultTreeModel;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;

/**
 *
 * @author gdurand
 */
public class StudyListing  implements java.io.Serializable {
    public static final int INCORRECT_VDC           = -2;
    public static final int EXPIRED_LIST            = -1;
    public static final int GENERIC_ERROR           =  0;

    public static final int COLLECTION_STUDIES      =  1;
    public static final int SEARCH                  =  2;
    public static final int COLLECTION_FILTER       =  3;
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
    private DefaultTreeModel collectionTree;

    private Long vdcId;
    private List searchTerms;
    private Map variableMap;
    private Map versionMap;
    private List displayStudyVersionsList;

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

    public DefaultTreeModel getCollectionTree() {
        return collectionTree;
    }

    public void setCollectionTree(DefaultTreeModel collectionTree) {
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

    /**
     * @return the versionMap
     */
    public Map getVersionMap() {
        return versionMap;
    }

    /**
     * @param versionMap the versionMap to set
     */
    public void setVersionMap(Map versionMap) {
        this.versionMap = versionMap;
    }

    public static String addToStudyListingMap(StudyListing sl, Map sessionMap) {
        Long slCount = (Long) sessionMap.get("studyListingsCount");
        OrderedMap slMap = (OrderedMap) sessionMap.get("studyListings");
        String sessionId =  ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();

        if (slCount == null) {
            slCount = new Long(0);

        } else {
            slCount = slCount + 1;
        }


        if (slMap == null) {
            slMap = new LinkedMap();
            sessionMap.put("studyListings", slMap);
        }

        sessionMap.put("studyListingsCount", slCount);
        String newIndex = slCount + "_" + sessionId;
        slMap.put(newIndex ,sl);

        if (slMap.size() > 5) {
            slMap.remove(slMap.firstKey());
        }

        return newIndex;
    }

    public static StudyListing getStudyListingFromMap(String slIndex, Map sessionMap, Long currentVdcId) {
        OrderedMap slMap = (OrderedMap) sessionMap.get("studyListings");
        if (slMap != null) {
            StudyListing sl = (StudyListing) slMap.get(slIndex);

            if (sl != null) {

                // make sure the user is in the current vdc for this study listing
                //Long currentVdcId = getVDCRequestBean().getCurrentVDCId();
                if ( currentVdcId == null ) {
                     if (sl.getVdcId() != null) {
                        sl = new StudyListing(StudyListing.INCORRECT_VDC);
                    }
                } else {
                    if ( !currentVdcId.equals(sl.getVdcId()) ) {
                        sl = new StudyListing(StudyListing.INCORRECT_VDC);
                    }
                }

                return sl;
            }
        }

        // this means that this studyListing or the session has expired
        return new StudyListing(StudyListing.EXPIRED_LIST);
    }

    public static void clearStudyListingMap(Map sessionMap) {
        sessionMap.remove("studyListings");
    }

    /**
     * @return the displayStudyVersionsList
     */
    public List getDisplayStudyVersionsList() {
        return displayStudyVersionsList;
    }

    /**
     * @param displayStudyVersionsList the displayStudyVersionsList to set
     */
    public void setDisplayStudyVersionsList(List displayStudyVersionsList) {
        this.displayStudyVersionsList = displayStudyVersionsList;
    }

}
