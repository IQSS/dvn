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
 * VDCUI.java
 *
 * Created on March 9, 2007, 2:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCActivity;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.LocalizedDate;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionUI;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.List;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 * @author wbossons
 */
public class VDCUI  implements java.io.Serializable {


    private double maxDownloadCount = -1;
    private long activity = -1;
    private Long numberOwnedStudies;
    private Long vdcId;
    private String activityClass;
    private String creator;
    private String type;
    private VDC vdc;
    private VDCServiceLocal vdcService = null;

    private static final String HARVESTING_DATAVERSE_LABEL = "Harvesting"; //constant to use when returning the dtype for this type of dataverse wjb

    
    /** Creates a new instance of VDCUI */
    
    // TODO: change this to be more like StudyUI
    public VDCUI(Long vdcId, double maxDownloadCount) {
        this.vdcId = vdcId;
        this.maxDownloadCount = maxDownloadCount;
        //System.out.println("The vdc is " + getVdc().getName());
    }

    public VDCUI(VDC vdc) {
        this.vdc = vdc;
        //System.out.println("The vdc is " + getVdc().getName());
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


    // getters

    public Long getActivity() {
        if (activity == -1) {
            if ( getDownloadCount() == 0) {
                activity = 0;
            } else {

                if (maxDownloadCount == -1) {
                    initVdcService();
                    maxDownloadCount = vdcService.getMaxDownloadCount();
                }
                activity = Math.round( 4 * (getDownloadCount() / maxDownloadCount) ) + 1;
                activity = Math.min( 5, activity );
            }
        }
        
        return activity;
    }

    public String getActivityClass() {
        activityClass = String.valueOf(getActivity());
        switch (Integer.parseInt(activityClass)) {
           case 0: activityClass =  "activitylevelicon al-0"; break;
           case 1: activityClass =  "activitylevelicon al-1"; break;
           case 2: activityClass =  "activitylevelicon al-2"; break;
           case 3: activityClass =  "activitylevelicon al-3"; break;
           case 4: activityClass =  "activitylevelicon al-4"; break;
           case 5: activityClass =  "activitylevelicon al-5"; break;
       }
        return activityClass;
     }

    public String getCreator() {
        UserServiceLocal userService = null;
        try {
            userService = (UserServiceLocal) new InitialContext().lookup("java:comp/env/vdcUserService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        VDCUser vdcUser = vdc.getCreator();
        if (vdcUser == null) {
            return "--";
        } else {
        return vdcUser.getUserName();
        }
    }

    public String getCreatedDate() {
        Timestamp createdDate = vdc.getCreatedDate();
        LocalizedDate localizedDate = new LocalizedDate();
        if(createdDate != null)
            return localizedDate.getLocalizedDate(createdDate, DateFormat.MEDIUM);
        else
            return "--";
    }

    public double getDownloadCount() {
        VDCActivity vda = getVdc().getVDCActivity();
        return ( vda.getLocalStudyLocalDownloadCount() + vda.getLocalStudyNetworkDownloadCount() +
                (.5 * vda.getLocalStudyForeignDownloadCount()) + (.5 * vda.getForeignStudyLocalDownloadCount()) );
    }

    public String getLastUpdateTime() {
        // study has a column lastupdatetime, and will we use the release date or the created date or some other date as an alternate.
        StudyServiceLocal studyService = null;
        try {
            studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Long vdcId = vdc.getId();
        Timestamp lastUpdateTime = studyService.getLastUpdatedTime(vdc.getId());
        LocalizedDate localizedDate = new LocalizedDate();
        if (lastUpdateTime != null)
            return localizedDate.getLocalizedDate(lastUpdateTime, DateFormat.MEDIUM);
        else
            return "--";
    }

    public String getName() {
        return vdc.getName();
    }


    public Long getNumberOwnedStudies() {
        numberOwnedStudies = Long.parseLong(String.valueOf(vdc.getOwnedStudies().size()));
        return numberOwnedStudies;
    }

    public String getReleaseDate() {
        Timestamp releaseDate = vdc.getReleaseDate();
        LocalizedDate localizedDate = new LocalizedDate();
        if(releaseDate != null)
            return localizedDate.getLocalizedDate(releaseDate, DateFormat.MEDIUM);
        else
            return "--";
    }

    public String getType() {
        if (vdc.getDtype().equals(" ")) {
           return "--";
        } else if (vdc.getHarvestingDataverse() != null) {
            return HARVESTING_DATAVERSE_LABEL;
        } else {
            return vdc.getDtype();
        }
    }

    public VDC getVdc() {
        // check to see if study is loaded or if we only have the studyId
        if (vdc == null) {
            initVdcService();
            vdc = vdcService.findById(vdcId);
        }

        return vdc;
    }


    //setters
    
    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

     /* old fields -- these supposedly are no longer used, however
     * references still exist in the codebase, so remove with caution . . .
     */
    public List getLinkedCollections() {
        return getLinkedCollections(false);
    }

    public List getLinkedCollections(boolean getHiddenCollections) {
        // getHiddenCollections no longer used
        return vdc.getLinkedCollections();
    }
    public boolean containsOnlyLinkedCollections() {
        return vdc.getLinkedCollections().size() > 0 &&
               vdc.getRootCollection().getSubCollections().size() == 0 &&
               new CollectionUI(vdc.getRootCollection()).getStudyIds().size() == 0;

    }

}

