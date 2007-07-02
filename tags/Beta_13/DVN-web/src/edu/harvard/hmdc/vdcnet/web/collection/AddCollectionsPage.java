/*
 * AddCollectionsPage.java
 *
 * Created on September 15, 2006, 1:33 PM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web.collection;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import com.sun.rave.web.ui.model.Option;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.StatusMessage;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.component.html.HtmlPanelGrid;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneRadio;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import com.sun.rave.web.ui.component.AddRemove;
import com.sun.rave.web.ui.model.MultipleSelectOptionsList;
import com.sun.rave.web.ui.component.HelpInline;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ActionEvent;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AddCollectionsPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    @EJB ReviewStateServiceLocal reviewStateService;
    private ResourceBundle messages = ResourceBundle.getBundle("Bundle");
    
    long vdcId =  0;
    VDCCollection vdcCollection;
    private HashMap searchFields = new HashMap();
    private Map collectionsMap = new HashMap();
    private String searchField;
    private String searchValue;
    private static final String [] emptyCollection = {"No collections available for selection"};


    // <editor-fold defaultstate="collapsed" desc="Creator-managed Component Definition">
    private int __placeholder;
    private boolean edit;
    public boolean isEdit(){
        return edit;
    }
    public void setEdit(boolean edit){
        this.edit = edit;
    }
    private Long collId;
    public Long getCollId(){
        return collId;
    }
    public void setCollId(Long collId){
        this.collId = collId;
    }
    
    String collectionName;
    public String getCollectionName(){
        return collectionName;
    }
    public void setCollectionName(String name){
        this.collectionName = name;
    }
    
    private Long parentId;
    public Long getParentId(){
        return parentId;
    }
    public void setParentId(Long parentId){
        this.parentId = parentId;
    }
    
    private String parentCollectionSelected;
    
    private boolean rootCollection;
    /**
     * <p>Automatically managed component initialization.  <strong>WARNING:</strong>
     * This method is automatically generated, so any user-specified code inserted
     * here is subject to being replaced.</p>
     */
    public void init() {
        super.init();
        initVdcId();
        Long id =  (Long)getRequestMap().get("collectionId");
        parentId = (Long)getRequestMap().get("parentId");
        if (id != null){
            collId = id;
        } else{
            String collIdStr = getRequestParam("collectionId");
            if (collIdStr == null){
                collIdStr = getRequestParam("content:addCollectionStudiesPageView:form1:collId");
            }
            if (collIdStr == null){
                collIdStr = getRequestParam("content:addCollectionQueryPageView:form1:collId");
            }
            if (collIdStr != null && collIdStr.length()>0){
                collId = Long.parseLong(collIdStr.trim());
            }
            if (parentId == null){
                String parentIdStr = getRequestParam("parentId");
                if (parentIdStr == null){
                    parentIdStr = getRequestParam("content:addCollectionStudiesPageView:form1:parentId");
                }
                if (parentIdStr == null){
                    parentIdStr = getRequestParam("content:addCollectionQueryPageView:form1:parentId");
                }
                if (parentIdStr != null && parentIdStr.length()>0){
                    parentId = Long.parseLong(parentIdStr.trim());
                }
            }
           
        }
        if (parentId != null){
            rootCollection = parentId == 0;
        }
        if (collId != null){
            edit = true;
        }
        if (edit){
            vdcCollection = vdcCollectionService.find(collId);
            setCollectionName(vdcCollection.getName());
            textFieldCollectionName.setValue(vdcCollection.getName());
            textAreaQuery.setValue(vdcCollection.getQuery());
            VDCCollection editCollection = vdcCollectionService.find(vdcCollection.getId());
            Collection <Study> editStudies = releasedStudies(vdcCollection.getStudies());
            int i=0;
            Option [] studies = new Option[editStudies.size()];
            Object [] studiesValues = new Object[editStudies.size()];
            for (Iterator it = editStudies.iterator(); it.hasNext();) {
                Study elem = (Study) it.next();
                studiesValues[i] = Long.toString(elem.getId().longValue());
                studies[i++]= new Option(Long.toString(elem.getId().longValue()),elem.getTitle());
            }
            addRemoveList1DefaultOptions.setOptions(studies);
            addRemoveList1DefaultOptions.setSelectedValue(studiesValues);
            addRemoveList1.setSelected(addRemoveList1DefaultOptions.getSelectedValue()); 
        } else{
            addRemoveList1DefaultOptions.setOptions(new com.sun.rave.web.ui.model.Option[] {});            
        }
        collectionsMap = getCollectionsMap();
        dropdown1DefaultItems.setItems( getOtherPublicVDCs());
        long vdcId =  getVDCRequestBean().getCurrentVDC().getId().longValue();
        try{
            dropdown2DefaultItems.setItems(getParentVDCCollections(vdcId));
        } catch (NullPointerException n){
            n.printStackTrace();
        }
        if (parentCollectionSelected != null){
            dropdown3.setValue(parentCollectionSelected);
        }
        dropdown4DefaultItems.setItems(getAllViewableCollections());
        dropdown5DefaultItems.setItems(getSearchFields());
        radioButtonList1DefaultItems.setItems(new String[] {"Search Studies in All Dataverses", "Search from \"Studies to Choose from\" box"});
    }
    
    private Collection <Study> releasedStudies(Collection <Study> studies){
        ReviewState releasedState = reviewStateService.findByName(reviewStateService.REVIEW_STATE_RELEASED);
        List released = new ArrayList();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            if (elem.getReviewState().equals(releasedState)){
                released.add(elem);
            }
        }
        return released;
    }
    
    private void initVdcId(){
        vdcId = getVDCRequestBean().getCurrentVDC().getId().longValue();;
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

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private PanelGroup groupPanel1 = new PanelGroup();

    public PanelGroup getGroupPanel1() {
        return groupPanel1;
    }

    public void setGroupPanel1(PanelGroup pg) {
        this.groupPanel1 = pg;
    }

    private PanelGroup groupPanel2 = new PanelGroup();

    public PanelGroup getGroupPanel2() {
        return groupPanel2;
    }

    public void setGroupPanel2(PanelGroup pg) {
        this.groupPanel2 = pg;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
    }

    private HtmlInputText textFieldCollectionName = new HtmlInputText();

    public HtmlInputText getTextFieldCollectionName() {
        return textFieldCollectionName;
    }

    public void setTextFieldCollectionName(HtmlInputText hit) {
        this.textFieldCollectionName = hit;
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

    private UISelectItems dropdown1SelectItems = new UISelectItems();

    public UISelectItems getDropdown1SelectItems() {
        return dropdown1SelectItems;
    }

    public void setDropdown1SelectItems(UISelectItems uisi) {
        this.dropdown1SelectItems = uisi;
    }

    private PanelGroup groupPanel3 = new PanelGroup();

    public PanelGroup getGroupPanel3() {
        return groupPanel3;
    }

    public void setGroupPanel3(PanelGroup pg) {
        this.groupPanel3 = pg;
    }

    private HtmlSelectOneMenu dropdown2 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown2() {
        return dropdown2;
    }

    public void setDropdown2(HtmlSelectOneMenu hsom) {
        this.dropdown2 = hsom;
    }

    private DefaultSelectItemsArray dropdown2DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown2DefaultItems() {
        return dropdown2DefaultItems;
    }

    public void setDropdown2DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown2DefaultItems = dsia;
    }

    private UISelectItems dropdown2SelectItems = new UISelectItems();

    public UISelectItems getDropdown2SelectItems() {
        return dropdown2SelectItems;
    }

    public void setDropdown2SelectItems(UISelectItems uisi) {
        this.dropdown2SelectItems = uisi;
    }

    private UISelectItems dropdown3SelectItems = new UISelectItems();

    public UISelectItems getDropdown3SelectItems() {
        return dropdown3SelectItems;
    }

    public void setDropdown3SelectItems(UISelectItems uisi) {
        this.dropdown3SelectItems = uisi;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    private HtmlOutputText outputText6 = new HtmlOutputText();

    public HtmlOutputText getOutputText6() {
        return outputText6;
    }

    public void setOutputText6(HtmlOutputText hot) {
        this.outputText6 = hot;
    }

    private HtmlOutputText outputText7 = new HtmlOutputText();

    public HtmlOutputText getOutputText7() {
        return outputText7;
    }

    public void setOutputText7(HtmlOutputText hot) {
        this.outputText7 = hot;
    }

    private HtmlPanelGrid gridPanel2 = new HtmlPanelGrid();

    public HtmlPanelGrid getGridPanel2() {
        return gridPanel2;
    }

    public void setGridPanel2(HtmlPanelGrid hpg) {
        this.gridPanel2 = hpg;
    }

    private HtmlOutputText outputText8 = new HtmlOutputText();

    public HtmlOutputText getOutputText8() {
        return outputText8;
    }

    public void setOutputText8(HtmlOutputText hot) {
        this.outputText8 = hot;
    }

    private HtmlSelectOneMenu dropdown4 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown4() {
        return dropdown4;
    }

    public void setDropdown4(HtmlSelectOneMenu hsom) {
        this.dropdown4 = hsom;
    }

    private DefaultSelectItemsArray dropdown4DefaultItems = new DefaultSelectItemsArray();
    

    public DefaultSelectItemsArray getDropdown4DefaultItems() {
        return dropdown4DefaultItems;
    }

    public void setDropdown4DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown4DefaultItems = dsia;
    }

    private UISelectItems dropdown4SelectItems = new UISelectItems();

    public UISelectItems getDropdown4SelectItems() {
        return dropdown4SelectItems;
    }

    public void setDropdown4SelectItems(UISelectItems uisi) {
        this.dropdown4SelectItems = uisi;
    }

    private HtmlOutputText outputText9 = new HtmlOutputText();

    public HtmlOutputText getOutputText9() {
        return outputText9;
    }

    public void setOutputText9(HtmlOutputText hot) {
        this.outputText9 = hot;
    }

    private PanelGroup groupPanel4 = new PanelGroup();

    public PanelGroup getGroupPanel4() {
        return groupPanel4;
    }

    public void setGroupPanel4(PanelGroup pg) {
        this.groupPanel4 = pg;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlInputText textField1 = new HtmlInputText();

    public HtmlInputText getTextField1() {
        return textField1;
    }

    public void setTextField1(HtmlInputText hit) {
        this.textField1 = hit;
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

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    private HtmlInputTextarea textAreaQuery = new HtmlInputTextarea();

    public HtmlInputTextarea getTextAreaQuery() {
        return textAreaQuery;
    }

    public void setTextAreaQuery(HtmlInputTextarea hit) {
        this.textAreaQuery = hit;
    }

    private HtmlOutputText outputText11 = new HtmlOutputText();

    public HtmlOutputText getOutputText11() {
        return outputText11;
    }

    public void setOutputText11(HtmlOutputText hot) {
        this.outputText11 = hot;
    }

    private HtmlOutputText outputText12 = new HtmlOutputText();

    public HtmlOutputText getOutputText12() {
        return outputText12;
    }

    public void setOutputText12(HtmlOutputText hot) {
        this.outputText12 = hot;
    }

    private AddRemove addRemoveList1 = new AddRemove();

    public AddRemove getAddRemoveList1() {
        return addRemoveList1;
    }

    public void setAddRemoveList1(AddRemove ar) {
        this.addRemoveList1 = ar;
    }

    private MultipleSelectOptionsList addRemoveList1DefaultOptions = new MultipleSelectOptionsList();

    public MultipleSelectOptionsList getAddRemoveList1DefaultOptions() {
        return addRemoveList1DefaultOptions;
    }

    public void setAddRemoveList1DefaultOptions(MultipleSelectOptionsList msol) {
        this.addRemoveList1DefaultOptions = msol;
    }

    private PanelGroup groupPanel6 = new PanelGroup();

    public PanelGroup getGroupPanel6() {
        return groupPanel6;
    }

    public void setGroupPanel6(PanelGroup pg) {
        this.groupPanel6 = pg;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }

    private HtmlCommandButton button4 = new HtmlCommandButton();

    public HtmlCommandButton getButton4() {
        return button4;
    }

    public void setButton4(HtmlCommandButton hcb) {
        this.button4 = hcb;
    }

    private PanelGroup groupPanel7 = new PanelGroup();

    public PanelGroup getGroupPanel7() {
        return groupPanel7;
    }

    public void setGroupPanel7(PanelGroup pg) {
        this.groupPanel7 = pg;
    }

    private HtmlCommandButton button5 = new HtmlCommandButton();

    public HtmlCommandButton getButton5() {
        return button5;
    }

    public void setButton5(HtmlCommandButton hcb) {
        this.button5 = hcb;
    }

    private HelpInline helpInline1 = new HelpInline();

    public HelpInline getHelpInline1() {
        return helpInline1;
    }

    public void setHelpInline1(HelpInline hi) {
        this.helpInline1 = hi;
    }

    private PanelGroup groupPanel8 = new PanelGroup();

    public PanelGroup getGroupPanel8() {
        return groupPanel8;
    }

    public void setGroupPanel8(PanelGroup pg) {
        this.groupPanel8 = pg;
    }

    private HtmlSelectOneMenu dropdown3 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown3() {
        return dropdown3;
    }

    public void setDropdown3(HtmlSelectOneMenu hsom) {
        this.dropdown3 = hsom;
    }

    private UISelectItems dropdown2SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown2SelectItems1() {
        return dropdown2SelectItems1;
    }

    public void setDropdown2SelectItems1(UISelectItems uisi) {
        this.dropdown2SelectItems1 = uisi;
    }

    private HtmlSelectOneMenu dropdown5 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown5() {
        return dropdown5;
    }

    public void setDropdown5(HtmlSelectOneMenu hsom) {
        this.dropdown5 = hsom;
    }

    private DefaultSelectItemsArray dropdown5DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown5DefaultItems() {
        return dropdown5DefaultItems;
    }

    public void setDropdown5DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown5DefaultItems = dsia;
    }

    private UISelectItems dropdown5SelectItems = new UISelectItems();

    public UISelectItems getDropdown5SelectItems() {
        return dropdown5SelectItems;
    }

    public void setDropdown5SelectItems(UISelectItems uisi) {
        this.dropdown5SelectItems = uisi;
    }

    private HtmlOutputText outputText13 = new HtmlOutputText();

    public HtmlOutputText getOutputText13() {
        return outputText13;
    }

    public void setOutputText13(HtmlOutputText hot) {
        this.outputText13 = hot;
    }

    private AddRemove addRemoveList2 = new AddRemove();

    public AddRemove getAddRemoveList2() {
        return addRemoveList2;
    }

    public void setAddRemoveList2(AddRemove ar) {
        this.addRemoveList2 = ar;
    }

    private MultipleSelectOptionsList addRemoveList2DefaultOptions = new MultipleSelectOptionsList();

    public MultipleSelectOptionsList getAddRemoveList2DefaultOptions() {
        return addRemoveList2DefaultOptions;
    }

    public void setAddRemoveList2DefaultOptions(MultipleSelectOptionsList msol) {
        this.addRemoveList2DefaultOptions = msol;
    }

    private HelpInline helpInline2 = new HelpInline();

    public HelpInline getHelpInline2() {
        return helpInline2;
    }

    public void setHelpInline2(HelpInline hi) {
        this.helpInline2 = hi;
    }

    private HtmlSelectOneRadio radioButtonList1 = new HtmlSelectOneRadio();

    public HtmlSelectOneRadio getRadioButtonList1() {
        return radioButtonList1;
    }

    public void setRadioButtonList1(HtmlSelectOneRadio hsor) {
        this.radioButtonList1 = hsor;
    }

    private DefaultSelectItemsArray radioButtonList1DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getRadioButtonList1DefaultItems() {
        return radioButtonList1DefaultItems;
    }

    public void setRadioButtonList1DefaultItems(DefaultSelectItemsArray dsia) {
        this.radioButtonList1DefaultItems = dsia;
    }

    private UISelectItems radioButtonList1SelectItems = new UISelectItems();

    public UISelectItems getRadioButtonList1SelectItems() {
        return radioButtonList1SelectItems;
    }

    public void setRadioButtonList1SelectItems(UISelectItems uisi) {
        this.radioButtonList1SelectItems = uisi;
    }

    private PanelGroup groupPanel9 = new PanelGroup();

    public PanelGroup getGroupPanel9() {
        return groupPanel9;
    }

    public void setGroupPanel9(PanelGroup pg) {
        this.groupPanel9 = pg;
    }

    private HtmlCommandButton button6 = new HtmlCommandButton();

    public HtmlCommandButton getButton6() {
        return button6;
    }

    public void setButton6(HtmlCommandButton hcb) {
        this.button6 = hcb;
    }

    private HtmlCommandButton button7 = new HtmlCommandButton();

    public HtmlCommandButton getButton7() {
        return button7;
    }

    public void setButton7(HtmlCommandButton hcb) {
        this.button7 = hcb;
    }
    
    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public AddCollectionsPage() {
    }

    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */



    /** 
     * <p>Callback method that is called after the component tree has been
     * restored, but before any event processing takes place.  This method
     * will <strong>only</strong> be called on a postback request that
     * is processing a form submit.  Customize this method to allocate
     * resources that will be required in your event handlers.</p>
     */
    public void preprocess() {
        Map m = getRequestMap();
        m.put("collectionId",collId);
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

    private String[] getOtherPublicVDCs() {
        String[] otherVDCs = null;
        List <VDC> allVDC = vdcService.findAll();
        List otherVDCCollections = new ArrayList();
        long vdcId = getVDCRequestBean().getCurrentVDC().getId().longValue();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            if ((elem.getId().longValue() != vdcId) && (elem.isRestricted() == false)){
                otherVDCCollections.addAll(getCollections(elem.getId().longValue()));
            }
        }
        if (otherVDCCollections.size() > 0){
            otherVDCs = new String[otherVDCCollections.size()];
            int i=0;
            for (Iterator it = otherVDCCollections.iterator(); it.hasNext();) {
                VDCCollection elem = (VDCCollection) it.next();
                otherVDCs[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
                
            }
        }
        return otherVDCs;
    }
    
    public Map getCollectionsMap(){
        List <VDC> allVDC = vdcService.findAll();
        List allVDCCollections = new ArrayList();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            allVDCCollections.addAll(getCollections(elem.getId().longValue()));
        }
        String [] allVDCs = new String[allVDCCollections.size()];
        for (Iterator it = allVDCCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            collectionsMap.put(getLevelRepStr(elem.getLevel())+elem.getName(),elem.getId());

        }

        return collectionsMap;
    }
    
    private String[] getAllViewableCollections() {
        List <VDC> allVDC = vdcService.findAll();
        List allVDCCollections = new ArrayList();
        for (Iterator it = allVDC.iterator(); it.hasNext();) {
            VDC elem = (VDC) it.next();
            if (!elem.isRestricted() || elem.getId() ==getVDCRequestBean().getCurrentVDC().getId()){
                List <VDCCollection> collections = getCollections(elem.getId().longValue());
                List viewableCollections = new ArrayList();
                for (int i = 0; i < collections.size(); i++) {
                    VDCCollection collection = collections.get(i);
                    if ((collection.isVisible()) || collection.getOwner().getId().equals(getVDCRequestBean().getCurrentVDCId())){
                        viewableCollections.add(collection);
                    }
                }
                allVDCCollections.addAll(viewableCollections);
            }
        }
        String [] allVDCs = null;
        if (allVDCCollections.size()!=0){
            allVDCs = new String[allVDCCollections.size()];
        } else {
            allVDCs = emptyCollection;
        }
  
    
        int i=0;
        for (Iterator it = allVDCCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            String name=null;
            if (elem.isVisible()){
                name = getLevelRepStr(elem.getLevel()) + elem.getName()+ " (Visible)";
            } else{
                name = getLevelRepStr(elem.getLevel()) + elem.getName()+ " (Hidden) ";
            }
            allVDCs[i++] = name;

        }
        return allVDCs;
    }
    
    private String[] getVDCCollections(List <VDCCollection> vdcCollections){
        String[] thisVDCCollections = new String[vdcCollections.size()+1];
        int i = 0;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            thisVDCCollections[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
        }
        return thisVDCCollections;
    }

    private String[] getVDCCollections(long vdcId) {
        List thisVDC = getCollections(vdcId);
        String[] thisVDCCollections = new String[thisVDC.size() ];
        int i = 0;
        for (Iterator it = thisVDC.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            thisVDCCollections[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
        }
       return thisVDCCollections;
    }
    
    private String[] getParentVDCCollections(long vdcId) {
        List thisVDC = getParentCollections(vdcId);
        String[] thisVDCCollections = new String[thisVDC.size() ];
        int i = 0;
        for (Iterator it = thisVDC.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            thisVDCCollections[i++] = getLevelRepStr(elem.getLevel()) + elem.getName();
        }
       return thisVDCCollections;
    }
    
    public String saveCollection(){
        VDC thisVDC =  getVDCRequestBean().getCurrentVDC();
        Object[] s = (Object[]) addRemoveList1DefaultOptions.getSelectedValue();
        VDCCollection node = null;
        if (!edit || !rootCollection){
            node = vdcCollectionService.find(collectionsMap.get((String) dropdown3.getValue()));
        }else{
            //node = new VDCCollection();
            //node.setName("root collection");
        }
        VDCCollection addCollection = null;
        if (edit){
            addCollection = vdcCollectionService.find(collId);
            addCollection.removeParentRelationship();
        } else{
            addCollection = new VDCCollection();
        }
        addCollection.setParentRelationship(node);
        addCollection.setName((String)textFieldCollectionName.getValue());
        ArrayList studies = new ArrayList();
        HashSet studiesSet = new HashSet();
        for (int i=0;i<s.length;i++){
            String studyStr =  s[i].toString();
            Long studyId = Long.valueOf(studyStr);
            studiesSet.add(studyId);
        }
        for (Iterator it = studiesSet.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            Study study = studyService.getStudy(elem);
            study.getStudyColls().add(addCollection);
            studies.add(study);
         
        }
        addCollection.setStudies(studies);
        addCollection.setOwner(thisVDC);
        addCollection.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED));
        addCollection.setVisible(true);
        if (edit){
            vdcCollectionService.edit(addCollection);
        }else{
            vdcCollectionService.create(addCollection);
        }
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Collection "+ addCollection.getName()+ " has been saved");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "manageCollections";
    }
    
    public List getCollections(long vdcId){
        ArrayList collections = new ArrayList();
        VDC vdc = vdcService.find(new Long(vdcId));
        int treeLevel=0;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        if (vdcRootCollection != null){
                vdcRootCollection.setLevel(treeLevel);
                collections.add(vdcRootCollection);
                List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(vdcRootCollection.getId());
                treeLevel = buildList(collections, subcollections,treeLevel);
        }
        return collections;
    }

    private int buildList( ArrayList collections, List <VDCCollection> vdcCollections,int level) {
        level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
                if (elem.getId().equals(parentId)){
                    parentCollectionSelected = getLevelRepStr(level)+elem.getName();
                }
                elem.setLevel(level);
                collections.add(elem);
                List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(elem.getId());
                if (subcollections.size()>0){
                    buildList(collections,subcollections,level);
                }
        }
        level--;
        return level;
    }

    public List getParentCollections(long vdcId){
        ArrayList collections = new ArrayList();
        VDC vdc = vdcService.find(new Long(vdcId));
        int treeLevel=0;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        if (vdcRootCollection != null){
            if (!edit || !vdcRootCollection.getId().equals(collId) ){
                vdcRootCollection.setLevel(treeLevel);
                collections.add(vdcRootCollection);
                List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(vdcRootCollection.getId());
                treeLevel = buildParentList(collections, subcollections,treeLevel);
            } else {
                if (vdcRootCollection.getId().equals(collId)){
                    VDCCollection dummy = new VDCCollection();
                    dummy.setId(new Long(0));
                    dummy.setName("Root collection cannot be changed");
                    dummy.setLevel(treeLevel);
                    collections.add(dummy);
                    parentCollectionSelected = getLevelRepStr(treeLevel)+dummy.getName();                }
            }
        }
        return collections;
    }

    private int buildParentList( ArrayList collections, Collection<VDCCollection> vdcCollections,int level) {
        level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            if (!edit || !elem.getId().equals(collId) ){
                if (elem.getId().equals(parentId)){
                    parentCollectionSelected = getLevelRepStr(level)+elem.getName();
                }
                elem.setLevel(level);
                collections.add(elem);
                List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(elem.getId());
                if (subcollections.size()>0){
                    buildParentList(collections,subcollections,level);
                }
            }
        }
        level--;
        return level;
    }

    private String getLevelRepStr(int i) {
        StringBuffer levelRep = new StringBuffer();
        for (int level = 0; level < i; level++) {
            levelRep.append('-');
        }
        return levelRep.toString();
    }
    

    public String[] getSearchFields() {
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        Collection advSearchFields = vdc.getAdvSearchFields();
        String[] fields = new String[advSearchFields.size()];
        int i=0;
        for (Iterator it = advSearchFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            fields[i++]=elem.getDescription();
            searchFields.put(messages.getString(elem.getName()),elem.getName());
            
        }
        return fields;
    }
    
    public void selectCollectionsStudies(ActionEvent e){
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        VDCUser user = getVDCSessionBean().getUser();
        String collectionNameStr = (String) dropdown4.getValue();
       int x =collectionNameStr.indexOf(" (Hidden)");
       if (x>-1){
           collectionNameStr = collectionNameStr.substring(0,x);
       }else {
           x=collectionNameStr.indexOf(" (Visible)");
           if (x>-1){
               collectionNameStr = collectionNameStr.substring(0,x);
           }
       }
       Long collectionId = (Long) collectionsMap.get(collectionNameStr);
       long collectionIdSelected = collectionId.longValue() ;
       VDCCollection collectionSelected = vdcCollectionService.find(new Long(collectionIdSelected));
       Collection <Study> collectionStudies = null;
       if (collectionSelected.getQuery() != null){
           List<Long> studyIds = indexService.query(collectionSelected.getQuery());
           ArrayList studies = new ArrayList();
           int i=0;
           for (Iterator it = studyIds.iterator(); it.hasNext();) {
               Long elem = (Long) it.next();
               Study study = studyService.getStudy(elem);
               studies.add(study);
           }
           collectionStudies = studies;
           
       }else{
           collectionStudies = collectionSelected.getStudies();
       }
       List <Study> viewableStudies = new ArrayList();
       for (Iterator it = collectionStudies.iterator(); it.hasNext();) {
           Study elem = (Study) it.next();
           if (StudyUI.isStudyVisibleToUser(elem,vdc,user)){
               viewableStudies.add(elem);
           }
           
       }
       int i=0;
       Option [] studiesOptions = (Option []) addRemoveList1.getItems();
       Option [] studies = new Option[viewableStudies.size()+studiesOptions.length];
       for (int j = 0; j < studiesOptions.length; j++) {           
           studies[i++] = studiesOptions[j];
        }
       for (Iterator it = viewableStudies.iterator(); it.hasNext();) {
           Study elem = (Study) it.next();
           Option o = new Option(Long.toString(elem.getId().longValue()),elem.getTitle());
           studies[i++]= o;
       }

       addRemoveList1.setItems(studies);       
    }
    
    private boolean isViewable(Study s){
        boolean viewable;
        viewable = ((s.getOwner().getId().equals(getVDCSessionBean().getLoginBean().getCurrentVDC().getId()) && s.getStudyId() != null) || (!s.isRestricted() || isUserAllowed(s.getAllowedUsers(),getVDCSessionBean().getLoginBean().getUser().getId())) && !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW).getId()) && !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW).getId()) && (!s.getOwner().isRestricted() || s.getOwner().getId().equals(getVDCSessionBean().getLoginBean().getCurrentVDC().getId())));
        return viewable;

    }

    private boolean isUserAllowed(Collection allowedList, Long userId){
        boolean userAllowed = false;
        for (Iterator it = allowedList.iterator(); it.hasNext();) {
            VDCUser elem = (VDCUser) it.next();
            if (elem.getId().equals(userId)){
                userAllowed = true;
                break;
            }
        }
        return userAllowed;
    }

    public void searchStudies(ActionEvent e){
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        VDCUser user = getVDCSessionBean().getUser();
        
        StringBuffer query = new StringBuffer();
        query.append(searchField + "=" + searchValue);
        SearchTerm searchTerm = new SearchTerm();
        searchTerm.setFieldName(searchField);
        searchTerm.setOperator("=");
        searchTerm.setValue(searchValue);
        List<Long> studyIds = indexService.search(searchTerm);
        int i = 0;
        Option [] studiesOptions = (Option []) addRemoveList1.getItems();
        Option [] studies = new Option[studyIds.size()+studiesOptions.length];
        for (int j = 0; j < studiesOptions.length; j++) {
            studies[i++] = studiesOptions[j];
        }
        List <Study> releasedStudies = new ArrayList();
        for (Iterator it = studyIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            Study study = studyService.getStudy(elem);
            if (StudyUI.isStudyVisibleToUser(study,vdc,user)){
                releasedStudies.add(study);
            }
        }
        for (Iterator it = releasedStudies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            studies[i++] = new Option(Long.toString(elem.getId().longValue()),elem.getTitle());            
        }
        addRemoveList1.setItems(studies);
    }
    
    public String queryStudies(){
        VDC thisVDC =  getVDCRequestBean().getCurrentVDC();
        VDCCollection addCollection = null; // addCollection is the new collection (subcollection)
        if (edit){
            addCollection = vdcCollectionService.find(collId);
        } else{
            addCollection = new VDCCollection();
            
        }
        VDCCollection node =null;  // node is the collection to which you are adding (parent collection)
        if (!edit || parentId != 0){
            node=vdcCollectionService.find(collectionsMap.get((String) dropdown3.getValue()));
        }else{ // what does this represent?
            //node = new VDCCollection();
            //node.setName("root collection");
        }
        addCollection.setName((String)textFieldCollectionName.getValue());
        addCollection.setOwner(thisVDC);
        addCollection.setQuery((String) textAreaQuery.getValue());
        addCollection.setVisible(true);
        if (edit){
            vdcCollectionService.edit(addCollection);
        } else {
            addCollection.setParentRelationship(node); 
            vdcCollectionService.create(addCollection);
        }
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Collection "+ addCollection.getName()+ " has been saved");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "manageCollections";        
    }
    
    public String editQueryStudies(){
        VDC thisVDC =  getVDCRequestBean().getCurrentVDC();
        VDCCollection addCollection = new VDCCollection();
        VDCCollection node = vdcCollectionService.find(collectionsMap.get((String) dropdown3.getValue()));
        addCollection.setName((String)textFieldCollectionName.getValue());
        addCollection.setOwner(thisVDC);
        addCollection.setQuery((String) textAreaQuery.getValue());
        addCollection.setVisible(true);
        addCollection.setParentRelationship(node);
        if (edit){
            vdcCollectionService.edit(addCollection);
        } else {
            vdcCollectionService.create(addCollection);
        }
        vdcCollectionService.edit(node);
        StatusMessage msg = new StatusMessage();
        msg.setMessageText("Collection "+ addCollection.getName()+ " has been saved");
        msg.setStyleClass("successMessage");
        Map m = getRequestMap();
        m.put("statusMessage",msg);
        return "manageCollections";        
    }

    public String cancel(){
        return "myOptions";
    }

    public boolean isRootCollection() {
        return rootCollection;
    }

    public void setRootCollection(boolean rootCollection) {
        this.rootCollection = rootCollection;
    }

}

