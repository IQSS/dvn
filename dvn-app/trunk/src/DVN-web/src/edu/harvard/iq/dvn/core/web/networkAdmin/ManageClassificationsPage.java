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
    private boolean result;
    private String statusMessage;
    private String SUCCESS_MESSAGE   = new String("Success. The classifications and dataverses operation completed successfully.");
    private String FAIL_MESSAGE      = new String("Problems occurred during the form submission. Please see error messages below.");
    private HtmlDataTable mainTable   = new HtmlDataTable();
    private HtmlMessages   iceMessage = new HtmlMessages();
    private HtmlOutputText linkDelete = new HtmlOutputText();
    private DataverseGrouping parentItem = null;
    private DataverseGrouping childItem  = null;

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

    public String getFAIL_MESSAGE() {
        return FAIL_MESSAGE;
    }

    public void setFAIL_MESSAGE(String FAIL_MESSAGE) {
        this.FAIL_MESSAGE = FAIL_MESSAGE;
    }

    public String getSUCCESS_MESSAGE() {
        return SUCCESS_MESSAGE;
    }

    public void setSUCCESS_MESSAGE(String SUCCESS_MESSAGE) {
        this.SUCCESS_MESSAGE = SUCCESS_MESSAGE;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public HtmlMessages getIceMessage() {
        return iceMessage;
    }

    public void setIceMessage(HtmlMessages iceMessage) {
        this.iceMessage = iceMessage;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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

    

   
    /**
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
    }

    /**
     * <p>Callback method that is called just before rendering takes place.
     * This method will <strong>only</strong> be called for the page that
     * will actually be rendered (and not, for example, on a page that
     * handled a postback and then navigated to a different page).  Customize
     * this method to allocate resources that will be required for rendering
     * this page.</p>
     */
    public void prerender() {
    }

    /**
     * <p>Callback method that is called after rendering is completed for
     * this request, if <code>init()</code> was called (regardless of whether
     * or not this was the page that was actually rendered).  Customize this
     * method to release resources acquired in the <code>init()</code>,
     * <code>preprocess()</code>, or <code>prerender()</code> methods (or
     * acquired during execution of an event handler).</p>
     */
    public void destroy() {
    }

    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() {
    }

}