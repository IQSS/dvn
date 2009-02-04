/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.naming.InitialContext;

/**
 *
 * @author wbossons
 */
public class VDCUIList extends SortableList {
    private @EJB VDCServiceLocal vdcService;
    private List<VDCUI> vdcUIList;

    // dataTable Columns to sort by:
    private static final String NAME_COLUMN_NAME            = "Name";
    private static final String AFFILIATION_COLUMN_NAME     = "Affiliation";
    private static final String DATERELEASED_COLUMN_NAME    = "Released";
    private static final String LASTUPDATED_COLUMN_NAME     = "Last Updated";
    private static final String ACTIVITY_COLUMN_NAME        = "Activity";

    private Long vdcGroupId;
    private String alphaCharacter;

    public VDCUIList() {
        super(NAME_COLUMN_NAME);
    }

    public VDCUIList(Long vdcGroupId) {
        super(NAME_COLUMN_NAME);
        this.vdcGroupId = vdcGroupId;
    }

    public VDCUIList(Long vdcGroupId, String alphaCharacter) {
        super(NAME_COLUMN_NAME);
        this.vdcGroupId = vdcGroupId;
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

    protected void sort() {
            String orderBy = null;
            if (sortColumnName == null) {
                return;
            }
            System.out.println("the sort column is " + sortColumnName);
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
           
            // TODO: Adapt this to the all dataverses/grouped dataverses world
            if (orderBy.equals("lastupdatetime")) {
                // TODO: implement this
                //vdcIds = vdcService.getOrderedVdcIdsByLastUpdateTime(vdcGroupId);
            } else if (orderBy.equals("activity")) {
                // TODO: implement this
            } else {
                // TODO: rework this to account for the above orderby sql queries
                // to be both for all dataverses and for classified dataverses.
                if (alphaCharacter != null && vdcGroupId != null) {
                    vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, alphaCharacter, orderBy);
                } else if (alphaCharacter != null && vdcGroupId == null) {
                    vdcIds = vdcService.getOrderedVDCIds(alphaCharacter, orderBy);
                } else if (vdcGroupId == null) {
                    System.out.println("The ordering will be by " + orderBy);
                    vdcIds = vdcService.getOrderedVDCIds(orderBy);
                } else {
                    vdcIds = vdcService.getOrderedVDCIds(vdcGroupId, orderBy);
                }
            }

            double maxDownloadCount = Math.max( 1, vdcService.getMaxDownloadCount() ); // minimum of 1, to avoid divide my zero issues
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
        if (vdcUIList == null) {
            initVdcService();
            sort();
        } else {
            checkSort();
        }
        return vdcUIList;
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

    
}
