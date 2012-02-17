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