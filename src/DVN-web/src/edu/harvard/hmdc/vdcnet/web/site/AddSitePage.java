/*
 * AddSitePage.java
 *
 * Created on September 19, 2006, 9:57 AM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.site;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import javax.ejb.EJB;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlInputText;
import com.sun.rave.web.ui.component.PanelGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.util.CharacterValidator;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AddSitePage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB UserServiceLocal userService;
    @EJB RoleServiceLocal roleService;
    @EJB MailServiceLocal mailService;
    StatusMessage msg;
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
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

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlPanelGrid gridPanel1 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel1() {
        return gridPanel1;
    }

    public void setGridPanel1(HtmlPanelGrid hpg) {
        this.gridPanel1 = hpg;
    }

    private HtmlOutputLabel componentLabel1 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel1() {
        return componentLabel1;
    }

    public void setComponentLabel1(HtmlOutputLabel hol) {
        this.componentLabel1 = hol;
    }

    private HtmlOutputText componentLabel1Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel1Text() {
        return componentLabel1Text;
    }

    public void setComponentLabel1Text(HtmlOutputText hot) {
        this.componentLabel1Text = hot;
    }

    private HtmlInputText dataverseName = new HtmlInputText();

    public HtmlInputText getDataverseName() {
        return dataverseName;
    }

    public void setDataverseName(HtmlInputText hit) {
        this.dataverseName = hit;
    }

    private HtmlOutputLabel componentLabel2 = new HtmlOutputLabel();

    public HtmlOutputLabel getComponentLabel2() {
        return componentLabel2;
    }

    public void setComponentLabel2(HtmlOutputLabel hol) {
        this.componentLabel2 = hol;
    }

    private HtmlOutputText componentLabel2Text = new HtmlOutputText();

    public HtmlOutputText getComponentLabel2Text() {
        return componentLabel2Text;
    }

    public void setComponentLabel2Text(HtmlOutputText hot) {
        this.componentLabel2Text = hot;
    }

    private HtmlInputText dataverseAlias = new HtmlInputText();

    public HtmlInputText getDataverseAlias() {
        return dataverseAlias;
    }

    public void setDataverseAlias(HtmlInputText hit) {
        this.dataverseAlias = hit;
    }

    private HtmlPanelGrid gridPanel2 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel2() {
        return gridPanel2;
    }

    public void setGridPanel2(HtmlPanelGrid hpg) {
        this.gridPanel2 = hpg;
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

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public AddSitePage() {
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
    
    public String create(){
        String name = (String)dataverseName.getValue();
        String alias = (String)dataverseAlias.getValue();
        Long userId = getVDCSessionBean().getLoginBean().getUser().getId();
        vdcService.create(userId,name,alias);
        VDC createdVDC = vdcService.findByAlias(alias);
        ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
        getVDCRequestBean().setCurrentVDC(createdVDC);
        //  add default values to the VDC table and commit/set the vdc bean props
        getVDCRequestBean().getCurrentVDC().setDisplayNetworkAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayAnnouncements());
        getVDCRequestBean().getCurrentVDC().setDisplayAnnouncements(getVDCRequestBean().getVdcNetwork().isDisplayVDCAnnouncements());
        getVDCRequestBean().getCurrentVDC().setAnnouncements(getVDCRequestBean().getVdcNetwork().getDefaultVDCAnnouncements());
        getVDCRequestBean().getCurrentVDC().setDisplayNewStudies(getVDCRequestBean().getVdcNetwork().isDisplayVDCRecentStudies());
        getVDCRequestBean().getCurrentVDC().setAboutThisDataverse(getVDCRequestBean().getVdcNetwork().getDefaultVDCAboutText());
        getVDCRequestBean().getCurrentVDC().setContactEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
        vdcService.edit(createdVDC);
        // Refresh User object in LoginBean so it contains the user's new role of VDC administrator.
        getVDCRequestBean().getCurrentVDCURL();
        StatusMessage msg = new StatusMessage();
        try {
            String hostUrl = System.getProperty("dvn.inetAddress");
            if (hostUrl != null) {
                msg.setMessageText("Your new dataverse has been successfully created. <br/>You can access it directly by entering this URL: <br/> http://" + hostUrl + "/dvn" + getVDCRequestBean().getCurrentVDCURL() + " <br/>Bear in mind that it is restricted by default. Go to <a href='/dvn" + getVDCRequestBean().getCurrentVDCURL() + "/faces/admin/OptionsPage.jsp'>My Options</a> to make it public.");
            } else {
                msg.setMessageText("Your new dataverse has been successfully created. <br/>You can access it directly by entering this URL: <br/> http://" + InetAddress.getLocalHost().getCanonicalHostName() + "/dvn" + getVDCRequestBean().getCurrentVDCURL() + " <br/>Bear in mind that it is restricted by default. Go to <a href='/dvn" + getVDCRequestBean().getCurrentVDCURL() + "/faces/admin/OptionsPage.jsp'>My Options</a> to make it public.");
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        VDCUser creator = userService.findByUserName(getVDCSessionBean().getLoginBean().getUser().getUserName());
        String toMailAddress = getVDCSessionBean().getLoginBean().getUser().getEmail();
        String siteAddress="unknown";
        try {
            siteAddress = InetAddress.getLocalHost().getCanonicalHostName() + "/dvn" + getVDCRequestBean().getCurrentVDCURL();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        mailService.sendAddSiteNotification(toMailAddress,name,siteAddress);

        getVDCSessionBean().getLoginBean().setUser(creator);
        
        return "home";
    }
    
    public String cancel(){
        return "home";
    }

    public void validateName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String name = (String) value;
        
        boolean nameFound = false;
        VDC vdc = vdcService.findByName(name);
        if (vdc != null) {
            nameFound=true;
        }
        
        if (nameFound) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("This name is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

    public void validateAlias(FacesContext context,
            UIComponent toValidate,
            Object value) {
        CharacterValidator charactervalidator = new CharacterValidator();
        charactervalidator.validate(context, toValidate, value);
        String alias = (String) value;
        
        boolean aliasFound = false;
        VDC vdc = vdcService.findByAlias(alias);
        if (vdc != null) {
            aliasFound=true;
        }
        
        if (aliasFound) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("This alias is already taken.");
            context.addMessage(toValidate.getClientId(context), message);
        }
    }

}

