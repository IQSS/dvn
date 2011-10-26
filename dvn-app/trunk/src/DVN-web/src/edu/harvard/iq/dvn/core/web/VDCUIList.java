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
    private VDCUI vdcui;
    private DataPaginator paginator;

   
 
    // dataTable Columns to sort by:
    private static final String NAME_COLUMN_NAME            = "Name";
    private static final String AFFILIATION_COLUMN_NAME     = "Affiliation";
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
            List vdcIds = null;

            if (alphaCharacter != null && vdcGroupId != null && !vdcGroupId.equals(new Long("-1"))) {
                vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, alphaCharacter, orderBy, hideRestricted);
            } else if (alphaCharacter != null && !alphaCharacter.equals("") && (vdcGroupId == null || vdcGroupId.equals(new Long("-1")))) {
                vdcIds = vdcService.getOrderedVDCIds(null, alphaCharacter, orderBy, hideRestricted);
            } else if (vdcGroupId == null || vdcGroupId.equals(new Long("-1"))) {
                vdcIds = vdcService.getOrderedVDCIds(null, null, orderBy, hideRestricted);
            } else {
                vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, null, orderBy, hideRestricted);
            }


            //double maxDownloadCount = Math.max( 1.0, vdcService.getMaxDownloadCount() ); // minimum of 1, to avoid divide my zero issues
            double maxDownloadCount = 1.0; // -- the line above bombs, while porting under EE6; -- L.A. 
            vdcUIList = new ArrayList<VDCUI>();
            for (Object vdcId : vdcIds) {
                vdcUIList.add( new VDCUI( (Long)vdcId, maxDownloadCount ) );
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

   public VDCUI getVdcui() {
        return vdcui;
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

    public void setVdcui(VDCUI vdcUI) {
        this.vdcui = vdcUI;
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
