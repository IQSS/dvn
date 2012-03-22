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
/** Source File Name:   ManageClassifications.java
 *
 * 
 *
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlGraphicImage;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.icesoft.faces.component.ext.HtmlOutputText;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.web.util.DateUtils;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.DataverseGrouping;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;

import edu.harvard.iq.dvn.core.web.site.ClassificationList;
import edu.harvard.iq.dvn.core.web.site.ClassificationUI;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.inject.Named;

@ViewScoped
@Named("manageClassificationsPage")
public class ManageClassificationsPage extends VDCBaseBean implements Serializable {
    @EJB StudyServiceLocal studyService;
    @EJB VDCGroupServiceLocal vdcGroupService;
    @EJB VDCServiceLocal vdcService;

    private Long cid;
    private int itemBeansSize       = 0; //used to output the number of classifications
    private HtmlDataTable mainTable   = new HtmlDataTable();


    public ManageClassificationsPage() {
    }

     @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        List list = (List)vdcGroupService.findAll();
        itemBeansSize = list.size();
        initClassifications();
     }

     ClassificationList list = null;

     protected void initClassifications() {
         if (list == null) {
             list = new ClassificationList();
         }
         list.getClassificationUIs(new Long("-1"));
     }

     /**
     * Toggles the expanded state of this dataverse group.
     *
     * @param event
     */
    public void toggleChildren(javax.faces.event.ActionEvent event) {
        Long parentNodeId = new Long(toggleImage.getAttributes().get("groupingId").toString());
        list.getClassificationUIs(parentNodeId);  
    }

    public ClassificationList getList() {
        return list;
    }

   private HtmlGraphicImage toggleImage = new HtmlGraphicImage();

    public HtmlGraphicImage getToggleImage() {
        return toggleImage;
    }

    public void setToggleImage(HtmlGraphicImage toggleImage) {
        this.toggleImage = toggleImage;
    }


    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }


    public HtmlDataTable getMainTable() {
        return mainTable;
    }

    public void setMainTable(HtmlDataTable mainTable) {
        this.mainTable = mainTable;
    }

    public int getItemBeansSize() {
        return itemBeansSize;
    }

    


}