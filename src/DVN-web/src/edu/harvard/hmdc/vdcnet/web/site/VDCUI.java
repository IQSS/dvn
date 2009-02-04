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

import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
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
    
    private Long vdcId;
    private VDC vdc;
    private VDCServiceLocal vdcService = null;
    
    /** Creates a new instance of VDCUI */
    
    // TODO: change this to be more like StudyUI
    public VDCUI(Long vdcId) {
        this.vdcId = vdcId;
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

    public VDC getVdc() {
        // check to see if study is loaded or if we only have the studyId
        if (vdc == null) {
            initVdcService();
            vdc = vdcService.findById(vdcId);
        }

        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    public String getName() {
        return vdc.getName();
    }

    
    public String getLastUpdateTime() {
        //TODO: implement this
        // study has a column lastupdatetime, and will we use the release date or the created date or some other date as an alternate.
        StudyServiceLocal studyService = null;
        try {
            studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Long vdcId = vdc.getId();
        Timestamp lastUpdateTime = studyService.getLastUpdatedTime(vdc.getId());
        if (lastUpdateTime == null)
            lastUpdateTime = vdc.getReleaseDate();
        LocalizedDate localizedDate = new LocalizedDate();
        if (lastUpdateTime != null)
            return localizedDate.getLocalizedDate(lastUpdateTime, DateFormat.MEDIUM);
        else
            return "--";
    }

    public String getReleaseDate() {
        Timestamp releaseDate = vdc.getReleaseDate();
        LocalizedDate localizedDate = new LocalizedDate();
        if(releaseDate != null)
            return localizedDate.getLocalizedDate(releaseDate, DateFormat.MEDIUM);
        else
            return "--";
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
