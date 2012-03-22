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
 * VDCUI.java
 *
 * Created on March 9, 2007, 2:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.site;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCActivity;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.LocalizedDate;
import edu.harvard.iq.dvn.core.web.collection.CollectionUI;
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
    private String lastUpdateTimeString;
    private Long vdcId;
    private String activityClass;
    private String creator;
    private String type;
    private VDC vdc;
    private VDCServiceLocal vdcService = null;

    private static final String HARVESTING_DATAVERSE_LABEL = "Harvesting"; //constant to use when returning the dtype for this type of dataverse wjb
    private static final String SCHOLAR_DTYPE_LABEL        = "Scholar";

    
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
        if (lastUpdateTimeString == null) {
        // use date of most recently updated owned study
            StudyServiceLocal studyService = null;
            try {
                studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Timestamp lastUpdateTime = studyService.getLastUpdatedTime(vdc.getId());
            LocalizedDate localizedDate = new LocalizedDate();
            if (lastUpdateTime != null)
                lastUpdateTimeString = localizedDate.getLocalizedDate(lastUpdateTime, DateFormat.MEDIUM);
            else
                lastUpdateTimeString =  "--";
        }

        return lastUpdateTimeString;
    }

    public String getName() {
        if (vdc.getDtype().toLowerCase().equals(SCHOLAR_DTYPE_LABEL.toLowerCase()))
            return vdc.getLastName() + ", " + vdc.getFirstName();
        else
            return vdc.getName();
    }


    public Long getNumberOwnedStudies() {
        if (numberOwnedStudies == null) {
            initVdcService();
            numberOwnedStudies = vdcService.getOwnedStudyCount(vdcId);
        }

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

