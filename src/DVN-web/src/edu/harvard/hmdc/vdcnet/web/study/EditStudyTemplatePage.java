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
 * EditStudyTemplatePage.java
 *
 * Created on October 19, 2006, 11:15 AM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import com.sun.rave.web.ui.appbase.AbstractPageBean;
import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import javax.faces.FacesException;
import com.sun.rave.web.ui.component.PanelLayout;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.TabSet;
import com.sun.rave.web.ui.component.Tab;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlInputTextarea;
import com.sun.rave.web.ui.component.HelpInline;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class EditStudyTemplatePage extends VDCBaseBean implements java.io.Serializable  {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
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

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelGroup groupPanel1 = new PanelGroup();

    public PanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(PanelGroup pg) {
        this.groupPanel1 = pg;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

    private TabSet tabSet1 = new TabSet();

    public TabSet getTabSet1() {
        return tabSet1;
    }

    public void setTabSet1(TabSet ts) {
        this.tabSet1 = ts;
    }

    private Tab tab1 = new Tab();

    public Tab getTab1() {
        return tab1;
    }

    public void setTab1(Tab t) {
        this.tab1 = t;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private PanelGroup groupPanel4 = new PanelGroup();

    public PanelGroup getGroupPanel4() {
        return groupPanel4;
    }

    public void setGroupPanel4(PanelGroup pg) {
        this.groupPanel4 = pg;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }

    private HtmlGraphicImage image1 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage1() {
        return image1;
    }

    public void setImage1(HtmlGraphicImage hgi) {
        this.image1 = hgi;
    }

    private HtmlPanelGrid gridPanel2 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel2() {
        return gridPanel2;
    }

    public void setGridPanel2(HtmlPanelGrid hpg) {
        this.gridPanel2 = hpg;
    }

    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
    }

    private HtmlOutputText outputText6 = new HtmlOutputText();

    public HtmlOutputText getOutputText6() {
        return outputText6;
    }

    public void setOutputText6(HtmlOutputText hot) {
        this.outputText6 = hot;
    }

    private HtmlOutputText outputText8 = new HtmlOutputText();

    public HtmlOutputText getOutputText8() {
        return outputText8;
    }

    public void setOutputText8(HtmlOutputText hot) {
        this.outputText8 = hot;
    }

    private HtmlOutputText outputText10 = new HtmlOutputText();

    public HtmlOutputText getOutputText10() {
        return outputText10;
    }

    public void setOutputText10(HtmlOutputText hot) {
        this.outputText10 = hot;
    }

    private PanelGroup groupPanel5 = new PanelGroup();

    public PanelGroup getGroupPanel5() {
        return groupPanel5;
    }

    public void setGroupPanel5(PanelGroup pg) {
        this.groupPanel5 = pg;
    }

    private HtmlOutputText outputText42 = new HtmlOutputText();

    public HtmlOutputText getOutputText42() {
        return outputText42;
    }

    public void setOutputText42(HtmlOutputText hot) {
        this.outputText42 = hot;
    }

    private HtmlGraphicImage image2 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage2() {
        return image2;
    }

    public void setImage2(HtmlGraphicImage hgi) {
        this.image2 = hgi;
    }

    private HtmlPanelGrid gridPanel3 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel3() {
        return gridPanel3;
    }

    public void setGridPanel3(HtmlPanelGrid hpg) {
        this.gridPanel3 = hpg;
    }

    private HtmlOutputText outputText43 = new HtmlOutputText();

    public HtmlOutputText getOutputText43() {
        return outputText43;
    }

    public void setOutputText43(HtmlOutputText hot) {
        this.outputText43 = hot;
    }

    private HtmlOutputText outputText45 = new HtmlOutputText();

    public HtmlOutputText getOutputText45() {
        return outputText45;
    }

    public void setOutputText45(HtmlOutputText hot) {
        this.outputText45 = hot;
    }

    private HtmlOutputText outputText47 = new HtmlOutputText();

    public HtmlOutputText getOutputText47() {
        return outputText47;
    }

    public void setOutputText47(HtmlOutputText hot) {
        this.outputText47 = hot;
    }

    private HtmlOutputText outputText49 = new HtmlOutputText();

    public HtmlOutputText getOutputText49() {
        return outputText49;
    }

    public void setOutputText49(HtmlOutputText hot) {
        this.outputText49 = hot;
    }

    private PanelGroup groupPanel6 = new PanelGroup();

    public PanelGroup getGroupPanel6() {
        return groupPanel6;
    }

    public void setGroupPanel6(PanelGroup pg) {
        this.groupPanel6 = pg;
    }

    private HtmlOutputText outputText67 = new HtmlOutputText();

    public HtmlOutputText getOutputText67() {
        return outputText67;
    }

    public void setOutputText67(HtmlOutputText hot) {
        this.outputText67 = hot;
    }

    private HtmlGraphicImage image3 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage3() {
        return image3;
    }

    public void setImage3(HtmlGraphicImage hgi) {
        this.image3 = hgi;
    }

    private HtmlPanelGrid gridPanel4 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel4() {
        return gridPanel4;
    }

    public void setGridPanel4(HtmlPanelGrid hpg) {
        this.gridPanel4 = hpg;
    }

    private PanelGroup groupPanel7 = new PanelGroup();

    public PanelGroup getGroupPanel7() {
        return groupPanel7;
    }

    public void setGroupPanel7(PanelGroup pg) {
        this.groupPanel7 = pg;
    }

    private HtmlOutputText outputText108 = new HtmlOutputText();

    public HtmlOutputText getOutputText108() {
        return outputText108;
    }

    public void setOutputText108(HtmlOutputText hot) {
        this.outputText108 = hot;
    }

    private HtmlGraphicImage image4 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage4() {
        return image4;
    }

    public void setImage4(HtmlGraphicImage hgi) {
        this.image4 = hgi;
    }

    private HtmlPanelGrid gridPanel5 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel5() {
        return gridPanel5;
    }

    public void setGridPanel5(HtmlPanelGrid hpg) {
        this.gridPanel5 = hpg;
    }

    private PanelGroup groupPanel8 = new PanelGroup();

    public PanelGroup getGroupPanel8() {
        return groupPanel8;
    }

    public void setGroupPanel8(PanelGroup pg) {
        this.groupPanel8 = pg;
    }

    private HtmlOutputText outputText137 = new HtmlOutputText();

    public HtmlOutputText getOutputText137() {
        return outputText137;
    }

    public void setOutputText137(HtmlOutputText hot) {
        this.outputText137 = hot;
    }

    private HtmlGraphicImage image5 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage5() {
        return image5;
    }

    public void setImage5(HtmlGraphicImage hgi) {
        this.image5 = hgi;
    }

    private HtmlPanelGrid gridPanel6 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel6() {
        return gridPanel6;
    }

    public void setGridPanel6(HtmlPanelGrid hpg) {
        this.gridPanel6 = hpg;
    }

    private Tab tab2 = new Tab();

    public Tab getTab2() {
        return tab2;
    }

    public void setTab2(Tab t) {
        this.tab2 = t;
    }

    private PanelLayout layoutPanel3 = new PanelLayout();

    public PanelLayout getLayoutPanel3() {
        return layoutPanel3;
    }

    public void setLayoutPanel3(PanelLayout pl) {
        this.layoutPanel3 = pl;
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private HtmlSelectBooleanCheckbox checkbox1 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox1 = hsbc;
    }

    private PanelGroup groupPanel2 = new PanelGroup();

    public PanelGroup getGroupPanel2() {
        return groupPanel2;
    }

    public void setGroupPanel2(PanelGroup pg) {
        this.groupPanel2 = pg;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    private PanelGroup groupPanel3 = new PanelGroup();

    public PanelGroup getGroupPanel3() {
        return groupPanel3;
    }

    public void setGroupPanel3(PanelGroup pg) {
        this.groupPanel3 = pg;
    }

    private PanelGroup groupPanel9 = new PanelGroup();

    public PanelGroup getGroupPanel9() {
        return groupPanel9;
    }

    public void setGroupPanel9(PanelGroup pg) {
        this.groupPanel9 = pg;
    }

    private PanelGroup groupPanel10 = new PanelGroup();

    public PanelGroup getGroupPanel10() {
        return groupPanel10;
    }

    public void setGroupPanel10(PanelGroup pg) {
        this.groupPanel10 = pg;
    }

    private PanelGroup groupPanel16 = new PanelGroup();

    public PanelGroup getGroupPanel16() {
        return groupPanel16;
    }

    public void setGroupPanel16(PanelGroup pg) {
        this.groupPanel16 = pg;
    }

    private PanelGroup groupPanel17 = new PanelGroup();

    public PanelGroup getGroupPanel17() {
        return groupPanel17;
    }

    public void setGroupPanel17(PanelGroup pg) {
        this.groupPanel17 = pg;
    }

    private PanelGroup groupPanel18 = new PanelGroup();

    public PanelGroup getGroupPanel18() {
        return groupPanel18;
    }

    public void setGroupPanel18(PanelGroup pg) {
        this.groupPanel18 = pg;
    }

    private PanelGroup groupPanel19 = new PanelGroup();

    public PanelGroup getGroupPanel19() {
        return groupPanel19;
    }

    public void setGroupPanel19(PanelGroup pg) {
        this.groupPanel19 = pg;
    }

    private PanelGroup groupPanel20 = new PanelGroup();

    public PanelGroup getGroupPanel20() {
        return groupPanel20;
    }

    public void setGroupPanel20(PanelGroup pg) {
        this.groupPanel20 = pg;
    }

    private HtmlGraphicImage image13 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage13() {
        return image13;
    }

    public void setImage13(HtmlGraphicImage hgi) {
        this.image13 = hgi;
    }

    private HtmlGraphicImage image14 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage14() {
        return image14;
    }

    public void setImage14(HtmlGraphicImage hgi) {
        this.image14 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox7 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox7() {
        return checkbox7;
    }

    public void setCheckbox7(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox7 = hsbc;
    }

    private HtmlGraphicImage image15 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage15() {
        return image15;
    }

    public void setImage15(HtmlGraphicImage hgi) {
        this.image15 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox8 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox8() {
        return checkbox8;
    }

    public void setCheckbox8(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox8 = hsbc;
    }

    private HtmlGraphicImage image16 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage16() {
        return image16;
    }

    public void setImage16(HtmlGraphicImage hgi) {
        this.image16 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox9 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox9() {
        return checkbox9;
    }

    public void setCheckbox9(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox9 = hsbc;
    }

    private HtmlGraphicImage image17 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage17() {
        return image17;
    }

    public void setImage17(HtmlGraphicImage hgi) {
        this.image17 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox10 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox10() {
        return checkbox10;
    }

    public void setCheckbox10(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox10 = hsbc;
    }

    private HtmlGraphicImage image18 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage18() {
        return image18;
    }

    public void setImage18(HtmlGraphicImage hgi) {
        this.image18 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox11 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox11() {
        return checkbox11;
    }

    public void setCheckbox11(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox11 = hsbc;
    }

    private HtmlGraphicImage image19 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage19() {
        return image19;
    }

    public void setImage19(HtmlGraphicImage hgi) {
        this.image19 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox12 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox12() {
        return checkbox12;
    }

    public void setCheckbox12(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox12 = hsbc;
    }

    private HtmlGraphicImage image20 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage20() {
        return image20;
    }

    public void setImage20(HtmlGraphicImage hgi) {
        this.image20 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox13 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox13() {
        return checkbox13;
    }

    public void setCheckbox13(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox13 = hsbc;
    }

    private HtmlGraphicImage image21 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage21() {
        return image21;
    }

    public void setImage21(HtmlGraphicImage hgi) {
        this.image21 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox14 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox14() {
        return checkbox14;
    }

    public void setCheckbox14(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox14 = hsbc;
    }

    private HtmlGraphicImage image22 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage22() {
        return image22;
    }

    public void setImage22(HtmlGraphicImage hgi) {
        this.image22 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox15 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox15() {
        return checkbox15;
    }

    public void setCheckbox15(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox15 = hsbc;
    }

    private HtmlGraphicImage image23 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage23() {
        return image23;
    }

    public void setImage23(HtmlGraphicImage hgi) {
        this.image23 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox16 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox16() {
        return checkbox16;
    }

    public void setCheckbox16(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox16 = hsbc;
    }

    private HtmlGraphicImage image24 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage24() {
        return image24;
    }

    public void setImage24(HtmlGraphicImage hgi) {
        this.image24 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox17 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox17() {
        return checkbox17;
    }

    public void setCheckbox17(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox17 = hsbc;
    }

    private HtmlGraphicImage image25 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage25() {
        return image25;
    }

    public void setImage25(HtmlGraphicImage hgi) {
        this.image25 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox18 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox18() {
        return checkbox18;
    }

    public void setCheckbox18(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox18 = hsbc;
    }

    private HtmlGraphicImage image26 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage26() {
        return image26;
    }

    public void setImage26(HtmlGraphicImage hgi) {
        this.image26 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox19 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox19() {
        return checkbox19;
    }

    public void setCheckbox19(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox19 = hsbc;
    }

    private HtmlGraphicImage image27 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage27() {
        return image27;
    }

    public void setImage27(HtmlGraphicImage hgi) {
        this.image27 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox20 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox20() {
        return checkbox20;
    }

    public void setCheckbox20(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox20 = hsbc;
    }

    private HtmlGraphicImage image28 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage28() {
        return image28;
    }

    public void setImage28(HtmlGraphicImage hgi) {
        this.image28 = hgi;
    }

    private HtmlSelectBooleanCheckbox checkbox21 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox21() {
        return checkbox21;
    }

    public void setCheckbox21(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox21 = hsbc;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    private HtmlOutputText outputText7 = new HtmlOutputText();

    public HtmlOutputText getOutputText7() {
        return outputText7;
    }

    public void setOutputText7(HtmlOutputText hot) {
        this.outputText7 = hot;
    }

    private PanelGroup groupPanel12 = new PanelGroup();

    public PanelGroup getGroupPanel12() {
        return groupPanel12;
    }

    public void setGroupPanel12(PanelGroup pg) {
        this.groupPanel12 = pg;
    }

    private HtmlInputTextarea textArea1 = new HtmlInputTextarea();

    public HtmlInputTextarea getTextArea1() {
        return textArea1;
    }

    public void setTextArea1(HtmlInputTextarea hit) {
        this.textArea1 = hit;
    }

    private HelpInline helpInline1 = new HelpInline();

    public HelpInline getHelpInline1() {
        return helpInline1;
    }

    public void setHelpInline1(HelpInline hi) {
        this.helpInline1 = hi;
    }

    private HtmlInputTextarea textArea2 = new HtmlInputTextarea();

    public HtmlInputTextarea getTextArea2() {
        return textArea2;
    }

    public void setTextArea2(HtmlInputTextarea hit) {
        this.textArea2 = hit;
    }

    private HtmlInputTextarea textArea3 = new HtmlInputTextarea();

    public HtmlInputTextarea getTextArea3() {
        return textArea3;
    }

    public void setTextArea3(HtmlInputTextarea hit) {
        this.textArea3 = hit;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditStudyTemplatePage() {
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


    public String tab1_action() {
        // TODO: Replace with your code
        
        return null;
    }


    public String tab2_action() {
        // TODO: Replace with your code
        
        return null;
    }
}

