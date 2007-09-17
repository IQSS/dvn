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
 * ReviewStudiesPage.java
 *
 * Created on October 10, 2006, 6:03 PM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.component.html.HtmlOutputText;
import com.sun.rave.web.ui.component.HelpInline;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlSelectOneMenu;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import javax.faces.component.UISelectItems;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class ReviewStudiesPage extends VDCBaseBean {
    @EJB StudyServiceLocal studyService;
    @EJB ReviewStateServiceLocal reviewStateService;
    @EJB MailServiceLocal mailService;
    DataModel reviewStudies;
    
    DataModel newStudies;
    ReviewState released;
    ReviewState inReview;
    ReviewState newStudy;

    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        dropdown1DefaultItems.setItems(new String[] {"New", "In Review", "Released"});
        dropdown1InReviewItems.setItems(new String [] {"In Review", "Released"} );
        dropdown1NewItems.setItems(new String[] {"New", "In Review"});
        released = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
        inReview = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW);
        newStudy = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW);
    }
    
    private Page page1 = new Page();
    
    public Page getPage1() {
        return page1;
    }
    
    public void setPage1(Page p) {
        this.page1 = p;
    }
    
    private Html html1 = new Html();
    
    public Html getHtml1() {
        return html1;
    }
    
    public void setHtml1(Html h) {
        this.html1 = h;
    }
    
    private Head head1 = new Head();
    
    public Head getHead1() {
        return head1;
    }
    
    public void setHead1(Head h) {
        this.head1 = h;
    }
    
    private Link link1 = new Link();
    
    public Link getLink1() {
        return link1;
    }
    
    public void setLink1(Link l) {
        this.link1 = l;
    }
    
    private Body body1 = new Body();
    
    public Body getBody1() {
        return body1;
    }
    
    public void setBody1(Body b) {
        this.body1 = b;
    }
    
    private Form form1 = new Form();
    
    public Form getForm1() {
        return form1;
    }
    
    public void setForm1(Form f) {
        this.form1 = f;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private PanelLayout layoutPanel3 = new PanelLayout();

    public PanelLayout getLayoutPanel3() {
        return layoutPanel3;
    }

    public void setLayoutPanel3(PanelLayout pl) {
        this.layoutPanel3 = pl;
    }

    private HelpInline helpInline1 = new HelpInline();

    public HelpInline getHelpInline1() {
        return helpInline1;
    }

    public void setHelpInline1(HelpInline hi) {
        this.helpInline1 = hi;
    }

    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
        return dataTable1;
    }

    public void setDataTable1(HtmlDataTable hdt) {
        this.dataTable1 = hdt;
    }

    private UIColumn column1 = new UIColumn();

    public UIColumn getColumn1() {
        return column1;
    }

    public void setColumn1(UIColumn uic) {
        this.column1 = uic;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlOutputLink hyperlink1 = new HtmlOutputLink();

    public HtmlOutputLink getHyperlink1() {
        return hyperlink1;
    }

    public void setHyperlink1(HtmlOutputLink hol) {
        this.hyperlink1 = hol;
    }

    private HtmlOutputText hyperlink1Text1 = new HtmlOutputText();

    public HtmlOutputText getHyperlink1Text1() {
        return hyperlink1Text1;
    }

    public void setHyperlink1Text1(HtmlOutputText hot) {
        this.hyperlink1Text1 = hot;
    }

    private UIColumn column2 = new UIColumn();

    public UIColumn getColumn2() {
        return column2;
    }

    public void setColumn2(UIColumn uic) {
        this.column2 = uic;
    }

    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
    }

    private HtmlSelectOneMenu dropdown1 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown1() {
        return dropdown1;
    }

    public void setDropdown1(HtmlSelectOneMenu hsom) {
        this.dropdown1 = hsom;
    }

    private DefaultSelectItemsArray dropdown1DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown1DefaultItems() {
        return dropdown1DefaultItems;
    }

    public void setDropdown1DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown1DefaultItems = dsia;
    }

    private DefaultSelectItemsArray dropdown1NewItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown1NewItems() {
        return dropdown1NewItems;
    }

    public void setDropdown1NewItems(DefaultSelectItemsArray dsia) {
        this.dropdown1NewItems = dsia;
    }

    private DefaultSelectItemsArray dropdown1InReviewItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown1InReviewItems() {
        return dropdown1InReviewItems;
    }

    public void setDropdown1InReviewItems(DefaultSelectItemsArray dsia) {
        this.dropdown1InReviewItems = dsia;
    }

    private UISelectItems dropdown1SelectItems = new UISelectItems();

    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }

    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }

    private UIColumn column3 = new UIColumn();

    public UIColumn getColumn3() {
        return column3;
    }

    public void setColumn3(UIColumn uic) {
        this.column3 = uic;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }

    private HtmlOutputLink hyperlink2 = new HtmlOutputLink();

    public HtmlOutputLink getHyperlink2() {
        return hyperlink2;
    }

    public void setHyperlink2(HtmlOutputLink hol) {
        this.hyperlink2 = hol;
    }

    private HtmlOutputText hyperlink2Text = new HtmlOutputText();

    public HtmlOutputText getHyperlink2Text() {
        return hyperlink2Text;
    }

    public void setHyperlink2Text(HtmlOutputText hot) {
        this.hyperlink2Text = hot;
    }

    private UIColumn column4 = new UIColumn();

    public UIColumn getColumn4() {
        return column4;
    }

    public void setColumn4(UIColumn uic) {
        this.column4 = uic;
    }

    private HtmlOutputLink hyperlink3 = new HtmlOutputLink();

    public HtmlOutputLink getHyperlink3() {
        return hyperlink3;
    }

    public void setHyperlink3(HtmlOutputLink hol) {
        this.hyperlink3 = hol;
    }

    private HtmlOutputText hyperlink2Text1 = new HtmlOutputText();

    public HtmlOutputText getHyperlink2Text1() {
        return hyperlink2Text1;
    }

    public void setHyperlink2Text1(HtmlOutputText hot) {
        this.hyperlink2Text1 = hot;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    private PanelGroup groupPanel1 = new PanelGroup();

    public PanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(PanelGroup pg) {
        this.groupPanel1 = pg;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private PanelGroup groupPanel2 = new PanelGroup();

    public PanelGroup getGroupPanel2() {
        return groupPanel2;
    }

    public void setGroupPanel2(PanelGroup pg) {
        this.groupPanel2 = pg;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public ReviewStudiesPage() {
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
    
    public DataModel getReviewStudies(){
        List <Study> studies = studyService.getReviewerStudies(getVDCSessionBean().getLoginBean().getCurrentVDC().getId());
        List displayFields = new ArrayList();
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        for (Iterator it = studies.iterator(); it.hasNext();) {
           String[] row = new String[10];
            Study elem = (Study) it.next();
            row[0] = elem.getId().toString();
            row[1] = elem.getGlobalId();
            row[2] = elem.getTitle();
            row[3] = elem.getReviewState().getName();
            row[4] = elem.getCreator().getFirstName()+ " "+elem.getCreator().getLastName();
            row[5] = elem.getReviewer() != null ? elem.getReviewer().getFirstName()+ " "+ elem.getReviewer().getLastName():"";
            row[6] = Long.toString(elem.getCreator().getId());
            row[7] = elem.getReviewer() != null ? Long.toString(elem.getReviewer().getId()):"-1";
            row[8] = yyyyMMdd.format(elem.getCreateTime());
            row[9] = elem.getLastUpdateTime() != null ? yyyyMMdd.format(elem.getLastUpdateTime()):"";
            displayFields.add(row);
        }
        reviewStudies = new ListDataModel(displayFields);
        return reviewStudies;
    }

      public DataModel getNewStudies(){
      //  List <Study> studies = studyService.getNewStudies(getVDCSessionBean().getLoginBean().getCurrentVDC().getId());
        List displayFields = new ArrayList();
      /*  SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        for (Iterator it = studies.iterator(); it.hasNext();) {
           String[] row = new String[10];
            Study elem = (Study) it.next();
            row[0] = elem.getId().toString();
            row[1] = elem.getGlobalId();
            row[2] = elem.getTitle();
            row[3] = elem.getReviewState().getName();
            row[4] = elem.getCreator().getFirstName()+ " "+elem.getCreator().getLastName();
            row[5] = elem.getReviewer() != null ? elem.getReviewer().getFirstName()+ " "+ elem.getReviewer().getLastName():"";
            row[6] = Long.toString(elem.getCreator().getId());
            row[7] = elem.getReviewer() != null ? Long.toString(elem.getReviewer().getId()):"-1";
            row[8] = yyyyMMdd.format(elem.getCreateTime());
            row[9] = elem.getLastUpdateTime() != null ? yyyyMMdd.format(elem.getLastUpdateTime()):"";
            displayFields.add(row);
        }
       */
        newStudies = new ListDataModel(displayFields);
        return newStudies;
    }
      
    public boolean isDisplayReviewStudies() {
        return reviewStudies!=null && reviewStudies.getRowCount()>0;
    }
       
    public void updateStudy(ActionEvent ae) {
        List <String[]> displayFields = (List) reviewStudies.getWrappedData();
        for (Iterator it = displayFields.iterator(); it.hasNext();) {
            String[] elem = (String[]) it.next();
            Study study = studyService.getStudy(Long.parseLong(elem[0]));
            if(elem[3].equalsIgnoreCase("In Review")){
                if (!study.getReviewState().getId().equals(inReview.getId())){
                    study.setReviewState(inReview);
                    study.setReviewer(getVDCSessionBean().getLoginBean().getUser());
                    mailService.sendStudyInReviewNotification(study.getCreator().getEmail(),study.getTitle());
                }
            }
            if (elem[3].equalsIgnoreCase("Released")){  // Bundle this
                if (!study.getReviewState().getId().equals(released.getId())){
                    ReviewState accepted = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
                    study.setReviewState(accepted);
                    mailService.sendStudyReleasedNotification(study.getCreator().getEmail(),study.getTitle(),getVDCRequestBean().getCurrentVDC().getName());
                }
            }
            studyService.updateStudy(study);
        }
      
    }
}

