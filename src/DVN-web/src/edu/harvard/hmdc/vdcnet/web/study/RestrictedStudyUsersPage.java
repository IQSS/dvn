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
 * RestrictedStudyUsersPage.java
 *
 * Created on October 16, 2006, 4:27 PM
 * 
 */
package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlDataTable;
import javax.faces.component.UIColumn;
import com.icesoft.faces.component.ext.HtmlOutputLink;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class RestrictedStudyUsersPage extends VDCBaseBean implements java.io.Serializable  {
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    private void _init() throws Exception {
    }

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }
    
    private HtmlDataTable dataTable3 = new HtmlDataTable();

    public HtmlDataTable getDataTable3() {
        return dataTable3;
    }

    public void setDataTable3(HtmlDataTable hdt) {
        this.dataTable3 = hdt;
    }

    private UIColumn column4 = new UIColumn();

    public UIColumn getColumn4() {
        return column4;
    }

    public void setColumn4(UIColumn uic) {
        this.column4 = uic;
    }

    private HtmlOutputText outputText9 = new HtmlOutputText();

    public HtmlOutputText getOutputText9() {
        return outputText9;
    }

    public void setOutputText9(HtmlOutputText hot) {
        this.outputText9 = hot;
    }

    private HtmlSelectBooleanCheckbox checkbox3 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox3() {
        return checkbox3;
    }

    public void setCheckbox3(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox3 = hsbc;
    }

    private UIColumn column8 = new UIColumn();

    public UIColumn getColumn8() {
        return column8;
    }

    public void setColumn8(UIColumn uic) {
        this.column8 = uic;
    }

    private HtmlOutputText outputText15 = new HtmlOutputText();

    public HtmlOutputText getOutputText15() {
        return outputText15;
    }

    public void setOutputText15(HtmlOutputText hot) {
        this.outputText15 = hot;
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

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public RestrictedStudyUsersPage() {
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
}

