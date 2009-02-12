/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
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
    private List<VDCUI> vdcUIList;

    // dataTable Columns to sort by:
    private static final String NAME_COLUMN_NAME            = "Name";
    private static final String AFFILIATION_COLUMN_NAME     = "Affiliation";
    private static final String DATERELEASED_COLUMN_NAME    = "Released";
    private static final String LASTUPDATED_COLUMN_NAME     = "Last Updated";
    private static final String ACTIVITY_COLUMN_NAME        = "Activity";

    private Long   vdcGroupId;
    private int    vdcGroupSize;
    private String alphaCharacter;
    
    private void init() {
        sortColumnName = DATERELEASED_COLUMN_NAME;
        ascending = true;
        oldSort = "";
        // make sure sortColumnName on first render
        oldAscending = ascending;
        initVdcService();
        initVdcGroupService();
        vdcGroupSize = (vdcGroupId != null && !vdcGroupId.equals(new Long("-1"))) ? ((VDCGroup)vdcGroupService.findById(vdcGroupId)).getVdcs().size() : vdcService.findAll().size();
    }

    public VDCUIList() {
        init();
    }

    public VDCUIList(Long vdcGroupId) {
        this.vdcGroupId = vdcGroupId;
        init();
        
    }

    public VDCUIList(Long vdcGroupId, String alphaCharacter) {     
        this.vdcGroupId = vdcGroupId;
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

    protected void sort() {
            String orderBy = null;
            if (sortColumnName == null) {
                return;
            }
            if (sortColumnName.equals(NAME_COLUMN_NAME)) {
                orderBy = NAME_COLUMN_NAME;
            } else if (sortColumnName.equals(AFFILIATION_COLUMN_NAME)) {
                orderBy = AFFILIATION_COLUMN_NAME;
            } else if (sortColumnName.equals(DATERELEASED_COLUMN_NAME)){
                orderBy = "releasedate";
            } else if (sortColumnName.equals(LASTUPDATED_COLUMN_NAME)) {
                orderBy = "lastupdatetime";
            } else if (sortColumnName.equals(ACTIVITY_COLUMN_NAME)) {
                orderBy = "activity";
            } else {
                throw new RuntimeException("Unknown sortColumnName: " + sortColumnName);
            }
            List vdcIds = null;


            if (alphaCharacter != null && vdcGroupId != null && !vdcGroupId.equals(new Long("-1"))) {
                vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, alphaCharacter, orderBy);
            } else if (alphaCharacter != null && (vdcGroupId == null || vdcGroupId.equals(new Long("-1")))) {
                vdcIds = vdcService.getOrderedVDCIds(alphaCharacter, orderBy);
            } else if (vdcGroupId == null || vdcGroupId.equals(new Long("-1"))) {
                vdcIds = vdcService.getOrderedVDCIds(orderBy);
            } else {
                vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, orderBy);
            }


            double maxDownloadCount = Math.max( 1.0, vdcService.getMaxDownloadCount() ); // minimum of 1, to avoid divide my zero issues
            vdcUIList = new ArrayList<VDCUI>();
            for (Object vdcId : vdcIds) {
                vdcUIList.add( new VDCUI( (Long)vdcId, maxDownloadCount ) );
            }
    }

    public boolean isDefaultAscending(String columnName) {
        return true;
    }

    public String getNameColumnName()         { return NAME_COLUMN_NAME; }
    public String getAffiliationColumnName()  { return AFFILIATION_COLUMN_NAME; }
    public String getDateReleasedColumnName() { return DATERELEASED_COLUMN_NAME; }
    public String getLastUpdatedColumnName()  { return LASTUPDATED_COLUMN_NAME; }
    public String getActivityColumnName()     { return ACTIVITY_COLUMN_NAME; }

 

    
    public List<VDCUI> getVdcUIList() {
        if (!oldSort.equals(sortColumnName) ) {
            sort();
            oldSort         = sortColumnName;
            oldAscending    = ascending;
        } else if (oldAscending != ascending) {
            Collections.reverse(vdcUIList);
            oldAscending    = ascending;
        }
        return vdcUIList;
    }

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

    public void setVdcUIList(List<VDCUI> vdcUIList) {
        this.vdcUIList = vdcUIList;
    }

    public Long getVdcGroupId() {
        return vdcGroupId;
    }

    public void setVdcGroupId(Long vdcGroupId) {
        this.vdcGroupId = vdcGroupId;
    }

    private VDCUI vdcui;

    public VDCUI getVdcui() {
        return vdcui;
    }

    public void setVdcui(VDCUI vdcUI) {
        this.vdcui = vdcUI;
    }

    public String getAlphaCharacter() {
        return alphaCharacter;
    }

    public void setAlphaCharacter(String alphaCharacter) {
        this.alphaCharacter = alphaCharacter;
    }

    public int getVdcGroupSize() {
        return vdcGroupSize;
    }

    public void setVdcGroupSize(int vdcGroupSize) {
        this.vdcGroupSize = vdcGroupSize;
    }


}
