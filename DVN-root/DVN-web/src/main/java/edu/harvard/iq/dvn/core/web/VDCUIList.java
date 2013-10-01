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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import com.icesoft.faces.component.datapaginator.DataPaginator;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.site.VDCUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import java.util.Iterator;
   

/**
 *
 * @author wbossons
 */
public class VDCUIList extends SortableList {
    
    private @EJB VDCServiceLocal vdcService;
    private @EJB VDCGroupServiceLocal vdcGroupService;
     @EJB VDCNetworkServiceLocal vdcNetworkService;

    private boolean hideRestricted; //show unrestricted and restricted dataverses
    private List<VDCUI> vdcUIList;
    private Long   vdcGroupId;
    private String alphaCharacter;
    private DataPaginator paginator;
    private String filterTerm;
    private Long networkId;
 
    // dataTable Columns to sort by:
    private static final String NAME_COLUMN_NAME            = "Name";
    private static final String AFFILIATION_COLUMN_NAME     = "Affiliation";
    private static final String SUBNETWORK_COLUMN_NAME     = "Subnetwork";
    private static final String DATERELEASED_COLUMN_NAME    = "Released";
    private static final String LASTUPDATED_COLUMN_NAME     = "Last Updated";
    private static final String ACTIVITY_COLUMN_NAME        = "Activity";
    // network admin fields
    private static final String CREATEDBY_COLUMN_NAME     = "Creator";
    private static final String DATECREATED_COLUMN_NAME   = "Created";
    private static final String OWNEDSTUDIES_COLUMN_NAME      = "Owned Studies";
    private static final String TYPE_COLUMN_NAME          = "Type";
    
    
    public void init() {

        if (sortColumnName == null || sortColumnName.isEmpty()){
            sortColumnName = DATERELEASED_COLUMN_NAME;
        } 
        
        ascending = true;
        oldSort = "";
        if (paginator!=null) {
            paginator.gotoFirstPage();
        }
        // make sure sortColumnName on first render
        oldAscending = ascending;
        initVdcService();
        initVdcGroupService();
    }

    public VDCUIList() {
        init();
    }
    
    public VDCUIList( String networkSortOrder) {
        if (networkSortOrder!=null && !networkSortOrder.isEmpty() ){
            sortColumnName = networkSortOrder;
        } else {
            sortColumnName = DATERELEASED_COLUMN_NAME;
        }
        init();
    }

    public VDCUIList(Long vdcGroupId, boolean hideRestricted, String networkSortOrder) {
        this.vdcGroupId = vdcGroupId;
        this.hideRestricted    = hideRestricted;
        if (networkSortOrder!=null && !networkSortOrder.isEmpty() ){
            sortColumnName = networkSortOrder;
        } else {
            sortColumnName = DATERELEASED_COLUMN_NAME;
        }
        init();  
    }
    
    public VDCUIList(Long vdcGroupId, boolean hideRestricted) {
        this.vdcGroupId = vdcGroupId;
        this.hideRestricted    = hideRestricted;
        init();  
    }

    public VDCUIList(Long vdcGroupId, String alphaCharacter, boolean hideRestricted , String networkSortOrder) {
        this.vdcGroupId = vdcGroupId;
        this.hideRestricted    = hideRestricted;
        if (networkSortOrder!=null && !networkSortOrder.isEmpty() ){
            sortColumnName = networkSortOrder;
        } else {
            sortColumnName = DATERELEASED_COLUMN_NAME;
        }
        init();
        this.alphaCharacter = alphaCharacter;        
    }
    
    public VDCUIList(Long vdcGroupId, String alphaCharacter, boolean hideRestricted) {
        this.vdcGroupId = vdcGroupId;
        this.hideRestricted    = hideRestricted;
        init();
        this.alphaCharacter = alphaCharacter;        
    }

   public VDCUIList(Long vdcGroupId, String alphaCharacter, String filterTerm, boolean hideRestricted) {
        this.vdcGroupId = vdcGroupId;
        this.hideRestricted    = hideRestricted;
        this.filterTerm = filterTerm;
        init();
        this.alphaCharacter = alphaCharacter;        
    }
    
    private void initVdcService() {
        if (vdcService == null) {
            try {
                vdcService = (VDCServiceLocal) new InitialContext().lookup("java:comp/env/vdcService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initVdcGroupService() {
        if (vdcGroupService == null) {
            try {
                vdcGroupService = (VDCGroupServiceLocal) new InitialContext().lookup("java:comp/env/vdcGroupService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public DataPaginator getPaginator() {
        return paginator;
    }

    public void setPaginator(DataPaginator paginator) {
        this.paginator = paginator;
    }
    protected void sort() {
            String orderBy = null;
            if (sortColumnName == null) {
                return;
            }
            if (sortColumnName.equals(NAME_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_NAME;
            } else if (sortColumnName.equals(ACTIVITY_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_ACTIVITY;
            } else if (sortColumnName.equals(AFFILIATION_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_AFFILIATION;
            } else if (sortColumnName.equals(SUBNETWORK_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_SUBNETWORK;
            } else if (sortColumnName.equals(DATECREATED_COLUMN_NAME)){
                orderBy = VDC.ORDER_BY_CREATE_DATE;
            } else if (sortColumnName.equals(TYPE_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_TYPE;
            } else if (sortColumnName.equals(LASTUPDATED_COLUMN_NAME)) {
                orderBy = VDC.ORDER_BY_LAST_STUDY_UPDATE_TIME;
            } else if (sortColumnName.equals(OWNEDSTUDIES_COLUMN_NAME)){
                orderBy = VDC.ORDER_BY_OWNED_STUDIES;
            } else if (sortColumnName.equals(DATERELEASED_COLUMN_NAME)){
                orderBy = VDC.ORDER_BY_RELEASE_DATE;
            } else if (sortColumnName.equals(CREATEDBY_COLUMN_NAME)){
                orderBy = VDC.ORDER_BY_CREATOR;
            } else {
                throw new RuntimeException("Unknown sortColumnName: " + sortColumnName);
            }
            List<Long> vdcIds = null;
            
            if (alphaCharacter != null && vdcGroupId != null && !vdcGroupId.equals(new Long("-1"))) {
                if (networkId != null && networkId.intValue() > 0){
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, alphaCharacter, orderBy, hideRestricted, filterTerm, networkId); 
                } else {
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, alphaCharacter, orderBy, hideRestricted, filterTerm); 
                }                
            } else if (alphaCharacter != null && !alphaCharacter.equals("") && (vdcGroupId == null || vdcGroupId.equals(new Long("-1")))) {                
                if (networkId != null && networkId.intValue() > 0){
                   vdcIds = vdcService.getOrderedVDCIds(null, alphaCharacter, orderBy, hideRestricted, filterTerm, networkId);
                } else {
                   vdcIds = vdcService.getOrderedVDCIds(null, alphaCharacter, orderBy, hideRestricted, filterTerm); 
                } 
            } else if (vdcGroupId == null || vdcGroupId.equals(new Long("-1"))) {               
                if (networkId != null && networkId.intValue() > 0){
                   vdcIds = vdcService.getOrderedVDCIds(null, null, orderBy, hideRestricted, filterTerm, networkId);
                } else {
                   vdcIds = vdcService.getOrderedVDCIds(null, null, orderBy, hideRestricted, filterTerm);
                }
            } else if (filterTerm != null) {               
                if (networkId != null && networkId.intValue() > 0){
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, null, orderBy, hideRestricted, filterTerm, networkId);
                } else {
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, null, orderBy, hideRestricted, filterTerm);
                }
            } else {               
                if (networkId != null && networkId.intValue() > 0){
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, null, orderBy, hideRestricted, null, networkId);
                } else {
                   vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, null, orderBy, hideRestricted);
                }
            }


            double maxDownloadCount = Math.max( 1.0, vdcService.getMaxDownloadCount() ); // minimum of 1, to avoid divide my zero issues
            vdcUIList = new ArrayList<VDCUI>();
            if (vdcIds != null) {
                for (Iterator<Long> itr = vdcIds.iterator(); itr.hasNext();) {
                    vdcUIList.add( new VDCUI( itr.next(), maxDownloadCount ) );
                }
            }
    }


    //getters

    public boolean isDefaultAscending(String columnName) {
        return true;
    }

    public String getAlphaCharacter() {
        return alphaCharacter;
    }

    public String getNameColumnName()         { return NAME_COLUMN_NAME; }
    public String getAffiliationColumnName()  { return AFFILIATION_COLUMN_NAME; }
    public String getSubnetworkColumnName()   { return SUBNETWORK_COLUMN_NAME; }
    public String getDateReleasedColumnName() { return DATERELEASED_COLUMN_NAME; }
    public String getLastUpdatedColumnName()  { return LASTUPDATED_COLUMN_NAME; }
    public String getActivityColumnName()     { return ACTIVITY_COLUMN_NAME; }
    //NETWORK ADMIN FIELDS
    public String getCreatedByColumnName()    { return CREATEDBY_COLUMN_NAME; }
    public String getDateCreatedColumnName()  { return DATECREATED_COLUMN_NAME; }
    public String getOwnedStudiesColumnName() { return OWNEDSTUDIES_COLUMN_NAME; }
    public String getTypeColumnName()         { return TYPE_COLUMN_NAME; }

 
   public Long getVdcGroupId() {
        return vdcGroupId;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public List<VDCUI> getVdcUIList() {  
        if (!oldSort.equals(sortColumnName) ) {
            // Check for null paginator because this method first
            // gets called from Hompage.init(), before paginator is initialized
            if (paginator!=null) {
                paginator.gotoFirstPage();
            }
            sort();
            oldSort         = sortColumnName;
            oldAscending    = ascending;
        } else if (oldAscending != ascending) {
            if (paginator!=null) {
                paginator.gotoFirstPage();
            }
            Collections.reverse(vdcUIList);
            oldAscending    = ascending;
        }
        return vdcUIList;
    }
    
    public List<Long> getVdcIds() {
        List <Long> retList = new ArrayList();
        for (VDCUI vdcui: vdcUIList){
            retList.add(vdcui.getVdcId());
        }
        return retList;
    }

    public int getVdcGroupSize() {
        return getVdcUIList().size();
    }


    //setters
    /**
     * Set sortColumnName type.
     *
     * @param ascending true for ascending sortColumnName, false for descending sortColumnName.
     */
    public void setAscending(boolean ascending) {
        oldAscending = this.ascending;
        this.ascending = ascending;
       
    }

    /**
     * Sets the sortColumnName column
     *
     * @param sortColumnName column to sortColumnName
     */
    public void setSortColumnName(String sortColumnName) {
        oldSort = this.sortColumnName;
        this.sortColumnName = sortColumnName;
    }

    /**
     * Sets the oldSort field
     *
     * @param oldSort set to value
     */
    public void setOldSort(String oldsort) {
        oldSort = oldsort;
    }


    public void setVdcUIList(List<VDCUI> vdcUIList) {
        this.vdcUIList = vdcUIList;
    }

    public void setVdcGroupId(Long vdcGroupId) {
        this.vdcGroupId = vdcGroupId;
    }
    
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public void setAlphaCharacter(String alphaCharacter) {
        this.alphaCharacter = alphaCharacter;
    }

    //utils
    public String toString() {
        String tostring = " [ Groupid: " + this.vdcGroupId +
                "; alphaCharacter: " + this.alphaCharacter +
                "; orderBy: " + this.sortColumnName +
                "; hideRestricted: " +  this.hideRestricted + " ]";
        return tostring;
    }
}
