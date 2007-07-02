/*
 * AdvSearchPage.java
 *
 * Created on September 14, 2006, 11:27 AM
 * Copyright mcrosas
 */
package edu.harvard.hmdc.vdcnet.web;

import com.sun.rave.web.ui.component.Body;
import com.sun.rave.web.ui.component.Form;
import com.sun.rave.web.ui.component.Head;
import com.sun.rave.web.ui.component.Html;
import com.sun.rave.web.ui.component.Link;
import com.sun.rave.web.ui.component.Page;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.index.SearchTerm;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.collection.CollectionModel;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import edu.harvard.hmdc.vdcnet.web.site.VDCUI;
import edu.harvard.hmdc.vdcnet.web.study.StudyUI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import com.sun.rave.web.ui.component.PanelLayout;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import com.sun.jsfcl.data.DefaultSelectItemsArray;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputText;
import com.sun.rave.web.ui.component.PanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.component.html.HtmlDataTable;
import com.sun.jsfcl.data.DefaultTableDataModel;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlGraphicImage;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
public class AdvSearchPage extends VDCBaseBean {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB IndexServiceLocal indexServiceBean;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB ReviewStateServiceLocal reviewStateService;
    @EJB StudyServiceLocal studyService;
    private Locale currentLocale = getExternalContext().getRequestLocale();
    private ResourceBundle messages = ResourceBundle.getBundle("Bundle");
    private HashMap advSearchFieldMap= new HashMap();
    private HashMap operatorMap = new HashMap();
    private String[] advancedSearchFields = {"title","authorName","studyId","otherId","abstractText","keywordValue","keywordVocabulary","topicClassValue","topicClassVocabulary","producerName","distributorName","fundingAgency","productionDate","distributionDate","dateOfDeposit","timePeriodCoveredStart","timePeriodCoveredEnd","country","geographicCoverage","geographicUnit","universe","kindOfData"};
    private boolean collectionsIncluded;
    public boolean isCollectionsIncluded(){
        return collectionsIncluded;
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
        radioButtonList1DefaultItems.setItems(new String[] {messages.getString("searchAllCollections") , messages.getString("searchOnlySelectedCollections")});
//        dropdown4DefaultItems.setItems(new String[] {"contains", "does not contain", "is greater than", "is less than"});
        dropdown4DefaultItems.setItems(new String[] {messages.getString("contains"),messages.getString("doesNotContain"),messages.getString("isGreaterThan"), messages.getString("isLessThan")});
        dropdown4DateItems.setItems(new String[] {messages.getString("isGreaterThan"), messages.getString("isLessThan")});
        dropdown4NotDateItems.setItems(new String[] {messages.getString("contains"),messages.getString("doesNotContain")});
        operatorMap.put(messages.getString("contains"),"=");
        operatorMap.put(messages.getString("doesNotContain"),"-");
        operatorMap.put(messages.getString("isGreaterThan"),">");
        operatorMap.put(messages.getString("isLessThan"),"<");
        /*
        if (getVDCRequestBean().getCurrentVDC() != null){
            long vdcId=getVDCRequestBean().getCurrentVDC().getId().longValue();
            dropdown3DefaultItems.setItems(getSearchScopeList(vdcId));
        }
        else{
            dropdown3DefaultItems.setItems(getAdvSearchFieldDefaults());
        }
         */
        dropdown3DefaultItems.setItems(getAdvSearchFieldDefaults());
        dataTable1Model.setWrappedData(getCollectionsDisplay());
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

    private HtmlOutputText outputText1 = new HtmlOutputText();

    public HtmlOutputText getOutputText1() {
        return outputText1;
    }

    public void setOutputText1(HtmlOutputText hot) {
        this.outputText1 = hot;
    }

    private HtmlOutputText outputText2 = new HtmlOutputText();

    public HtmlOutputText getOutputText2() {
        return outputText2;
    }

    public void setOutputText2(HtmlOutputText hot) {
        this.outputText2 = hot;
    }

    private HtmlOutputText outputText3 = new HtmlOutputText();

    public HtmlOutputText getOutputText3() {
        return outputText3;
    }

    public void setOutputText3(HtmlOutputText hot) {
        this.outputText3 = hot;
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

    private HtmlInputText textField1 = new HtmlInputText();

    public HtmlInputText getTextField1() {
        return textField1;
    }

    public void setTextField1(HtmlInputText hit) {
        this.textField1 = hit;
    }

    private PanelLayout layoutPanel2 = new PanelLayout();

    public PanelLayout getLayoutPanel2() {
        return layoutPanel2;
    }

    public void setLayoutPanel2(PanelLayout pl) {
        this.layoutPanel2 = pl;
    }

    private HtmlOutputText outputText4 = new HtmlOutputText();

    public HtmlOutputText getOutputText4() {
        return outputText4;
    }

    public void setOutputText4(HtmlOutputText hot) {
        this.outputText4 = hot;
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

    private HtmlSelectOneMenu dropdown3 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown3() {
        return dropdown3;
    }

    public void setDropdown3(HtmlSelectOneMenu hsom) {
        this.dropdown3 = hsom;
    }

    private DefaultSelectItemsArray dropdown3DefaultItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown3DefaultItems() {
        return dropdown3DefaultItems;
    }

    public void setDropdown3DefaultItems(DefaultSelectItemsArray dsia) {
        this.dropdown3DefaultItems = dsia;
    }

    private UISelectItems dropdown3SelectItems = new UISelectItems();

    public UISelectItems getDropdown3SelectItems() {
        return dropdown3SelectItems;
    }

    public void setDropdown3SelectItems(UISelectItems uisi) {
        this.dropdown3SelectItems = uisi;
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
    
    private DefaultSelectItemsArray dropdown4DateItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown4DateItems() {
        return dropdown4DateItems;
    }

    public void setDropdown4DateItems(DefaultSelectItemsArray dsia) {
        this.dropdown4DateItems = dsia;
    }

    private DefaultSelectItemsArray dropdown4NotDateItems = new DefaultSelectItemsArray();

    public DefaultSelectItemsArray getDropdown4NotDateItems() {
        return dropdown4NotDateItems;
    }

    public void setDropdown4NotDateItems(DefaultSelectItemsArray dsia) {
        this.dropdown4NotDateItems = dsia;
    }

    private UISelectItems dropdown4SelectItems = new UISelectItems();

    public UISelectItems getDropdown4SelectItems() {
        return dropdown4SelectItems;
    }

    public void setDropdown4SelectItems(UISelectItems uisi) {
        this.dropdown4SelectItems = uisi;
    }

    private HtmlInputText textField2 = new HtmlInputText();

    public HtmlInputText getTextField2() {
        return textField2;
    }

    public void setTextField2(HtmlInputText hit) {
        this.textField2 = hit;
    }

    private PanelGroup groupPanel2 = new PanelGroup();

    public PanelGroup getGroupPanel2() {
        return groupPanel2;
    }

    public void setGroupPanel2(PanelGroup pg) {
        this.groupPanel2 = pg;
    }

    private HtmlSelectOneMenu dropdown5 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown5() {
        return dropdown5;
    }

    public void setDropdown5(HtmlSelectOneMenu hsom) {
        this.dropdown5 = hsom;
    }

    private UISelectItems dropdown3SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown3SelectItems1() {
        return dropdown3SelectItems1;
    }

    public void setDropdown3SelectItems1(UISelectItems uisi) {
        this.dropdown3SelectItems1 = uisi;
    }

    private HtmlSelectOneMenu dropdown6 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown6() {
        return dropdown6;
    }

    public void setDropdown6(HtmlSelectOneMenu hsom) {
        this.dropdown6 = hsom;
    }

    private UISelectItems dropdown4SelectItems1 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems1() {
        return dropdown4SelectItems1;
    }

    public void setDropdown4SelectItems1(UISelectItems uisi) {
        this.dropdown4SelectItems1 = uisi;
    }

    private HtmlInputText textField3 = new HtmlInputText();

    public HtmlInputText getTextField3() {
        return textField3;
    }

    public void setTextField3(HtmlInputText hit) {
        this.textField3 = hit;
    }

    private PanelGroup groupPanel3 = new PanelGroup();

    public PanelGroup getGroupPanel3() {
        return groupPanel3;
    }

    public void setGroupPanel3(PanelGroup pg) {
        this.groupPanel3 = pg;
    }

    private HtmlSelectOneMenu dropdown7 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown7() {
        return dropdown7;
    }

    public void setDropdown7(HtmlSelectOneMenu hsom) {
        this.dropdown7 = hsom;
    }

    private UISelectItems dropdown3SelectItems2 = new UISelectItems();

    public UISelectItems getDropdown3SelectItems2() {
        return dropdown3SelectItems2;
    }

    public void setDropdown3SelectItems2(UISelectItems uisi) {
        this.dropdown3SelectItems2 = uisi;
    }

    private HtmlSelectOneMenu dropdown8 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown8() {
        return dropdown8;
    }

    public void setDropdown8(HtmlSelectOneMenu hsom) {
        this.dropdown8 = hsom;
    }

    private UISelectItems dropdown4SelectItems2 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems2() {
        return dropdown4SelectItems2;
    }

    public void setDropdown4SelectItems2(UISelectItems uisi) {
        this.dropdown4SelectItems2 = uisi;
    }

    private HtmlInputText textField4 = new HtmlInputText();

    public HtmlInputText getTextField4() {
        return textField4;
    }

    public void setTextField4(HtmlInputText hit) {
        this.textField4 = hit;
    }

    private HtmlCommandButton button1 = new HtmlCommandButton();

    public HtmlCommandButton getButton1() {
        return button1;
    }

    public void setButton1(HtmlCommandButton hcb) {
        this.button1 = hcb;
    }

    private HtmlOutputText outputText5 = new HtmlOutputText();

    public HtmlOutputText getOutputText5() {
        return outputText5;
    }

    public void setOutputText5(HtmlOutputText hot) {
        this.outputText5 = hot;
    }

    private PanelLayout layoutPanel1 = new PanelLayout();

    public PanelLayout getLayoutPanel1() {
        return layoutPanel1;
    }

    public void setLayoutPanel1(PanelLayout pl) {
        this.layoutPanel1 = pl;
    }

    private PanelGroup groupPanel4 = new PanelGroup();

    public PanelGroup getGroupPanel4() {
        return groupPanel4;
    }

    public void setGroupPanel4(PanelGroup pg) {
        this.groupPanel4 = pg;
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

    private HtmlCommandButton button2 = new HtmlCommandButton();

    public HtmlCommandButton getButton2() {
        return button2;
    }

    public void setButton2(HtmlCommandButton hcb) {
        this.button2 = hcb;
    }

    private HtmlCommandButton button3 = new HtmlCommandButton();

    public HtmlCommandButton getButton3() {
        return button3;
    }

    public void setButton3(HtmlCommandButton hcb) {
        this.button3 = hcb;
    }

    private PanelGroup groupPanel5 = new PanelGroup();

    public PanelGroup getGroupPanel5() {
        return groupPanel5;
    }

    public void setGroupPanel5(PanelGroup pg) {
        this.groupPanel5 = pg;
    }

    private HtmlSelectOneMenu dropdown9 = new HtmlSelectOneMenu();

    public HtmlSelectOneMenu getDropdown9() {
        return dropdown9;
    }

    public void setDropdown9(HtmlSelectOneMenu hsom) {
        this.dropdown9 = hsom;
    }

    private UISelectItems dropdown4SelectItems3 = new UISelectItems();

    public UISelectItems getDropdown4SelectItems3() {
        return dropdown4SelectItems3;
    }

    public void setDropdown4SelectItems3(UISelectItems uisi) {
        this.dropdown4SelectItems3 = uisi;
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

    private HtmlOutputText outputText8 = new HtmlOutputText();

    public HtmlOutputText getOutputText8() {
        return outputText8;
    }

    public void setOutputText8(HtmlOutputText hot) {
        this.outputText8 = hot;
    }

    private HtmlDataTable dataTable1 = new HtmlDataTable();

    public HtmlDataTable getDataTable1() {
        return dataTable1;
    }

    public void setDataTable1(HtmlDataTable hdt) {
        this.dataTable1 = hdt;
    }

    private DefaultTableDataModel dataTable1Model = new DefaultTableDataModel();

    public DefaultTableDataModel getDataTable1Model() {
        return dataTable1Model;
    }

    public void setDataTable1Model(DefaultTableDataModel dtdm) {
        this.dataTable1Model = dtdm;
    }

    private UIColumn column1 = new UIColumn();

    public UIColumn getColumn1() {
        return column1;
    }

    public void setColumn1(UIColumn uic) {
        this.column1 = uic;
    }

    private HtmlOutputText outputText11 = new HtmlOutputText();

    public HtmlOutputText getOutputText11() {
        return outputText11;
    }

    public void setOutputText11(HtmlOutputText hot) {
        this.outputText11 = hot;
    }

    private HtmlSelectBooleanCheckbox checkbox1 = new HtmlSelectBooleanCheckbox();

    public HtmlSelectBooleanCheckbox getCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(HtmlSelectBooleanCheckbox hsbc) {
        this.checkbox1 = hsbc;
    }

    private HtmlGraphicImage image1 = new HtmlGraphicImage();

    public HtmlGraphicImage getImage1() {
        return image1;
    }

    public void setImage1(HtmlGraphicImage hgi) {
        this.image1 = hgi;
    }
    
    // </editor-fold>


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public AdvSearchPage() {
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


    public void checkboxList1_processValueChange(ValueChangeEvent vce) {
        // TODO: Replace with your code
        
    }
    
    public String[] getAdvSearchFieldDefaults(){
        
//        List advSearchFieldDefault = studyFieldService.findAdvSearchDefault();
        String [] advS = getFieldList(advancedSearchFields);
         
//        String [] advS = getFieldList(advancedSearchFields);
        return advS;
    }
    
    public String[] getSearchScopeList(long vdcId){
//        ArrayList displayNames = new ArrayList();
        VDC vdc = vdcService.find(new Long(vdcId));
        Collection advSearchFields = vdc.getAdvSearchFields();
        String[] advS = getFieldList(advSearchFields);
        return advS;
    }

    private String[] getFieldList(final Collection advSearchFields) {
        String [] advS = new String[advSearchFields.size()];
//        DefaultSelectItemsArray dsia = new DefaultSelectItemsArray();
        int i=0;
        for (Iterator it = advSearchFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            elem.getId();
            advS[i++]=messages.getString(elem.getName());
            advSearchFieldMap.put(messages.getString(elem.getName()),elem.getName());
            
        }
        return advS;
    }

    private String[] getFieldList(String [] advSearchFields) {
        String [] advS = new String[advSearchFields.length];
        for (int i = 0; i < advS.length; i++) {
            advS[i] = messages.getString(advSearchFields[i]);
            advSearchFieldMap.put(messages.getString(advSearchFields[i]),advSearchFields[i]);
        }
//        DefaultSelectItemsArray dsia = new DefaultSelectItemsArray();
        /*
        int i=0;
        for (Iterator it = advSearchFields.iterator(); it.hasNext();) {
            String name =  (String) it.next();
            advS[i++]=messages.getString(name);
            advSearchFieldMap.put(messages.getString(name),name);
            
        }
         */
        return advS;
    }
    
    public List getCollections(){
        ArrayList collections = new ArrayList();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        int treeLevel=1;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        Collection <VDCCollection> subcollections = vdcRootCollection.getSubCollections();
        treeLevel = buildList(collections, subcollections,treeLevel);
        return collections;
    }

    private int buildList( ArrayList collections, Collection<VDCCollection> vdcCollections,int level) {
            level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            elem.setLevel(level);
            collections.add(elem);
            Collection <VDCCollection> subcollections = elem.getSubCollections();
            if (subcollections.size()>0){
                buildList(collections,subcollections,level);
            }
        }
        level--;
        return level;
    }
    
    public List getCollectionsDisplay(){
        ArrayList collections = new ArrayList();
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        if (vdc != null){
            getVDCCollections(vdc, collections);
        }
        else{
            List <VDC> allVdc = vdcService.findAll();
            for (Iterator it = allVdc.iterator(); it.hasNext();) {
                VDC elem = (VDC) it.next();
                if (!elem.isRestricted()){
                    getVDCCollections(elem, collections);
                }
            }
            collectionsIncluded = false;
        }
        return collections;
    }

    private void getVDCCollections(final VDC vdc, final ArrayList collections) {
        int treeLevel=1;
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        CollectionModel row = getRow(vdcRootCollection,treeLevel);
        collections.add(row);
        List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(vdcRootCollection.getId(),false);
//        Collection <VDCCollection> subcollections = vdcRootCollection.getSubCollections();
        if (subcollections.size()>0){
            collectionsIncluded = true;
        }
        treeLevel = buildDisplayModel(collections, subcollections,treeLevel);
        VDCUI vdcUI = new VDCUI(vdc);
        List <VDCCollection> linkedCollections = vdcUI.getLinkedCollections(false);
        for (Iterator it = linkedCollections.iterator(); it.hasNext();) {
            VDCCollection link = (VDCCollection) it.next();
            CollectionModel linkedRow = getRow(link,treeLevel);
            collections.add(linkedRow);            
        }
    }

    private int buildDisplayModel( ArrayList collections, List<VDCCollection> vdcCollections,int level) {
            level++;
        for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            if (isViewable(elem)){
//            collection.setLevel(level);
                CollectionModel row = getRow(elem, level);
//            collections.add(collection);
                collections.add(row);
                List <VDCCollection> subcollections = vdcCollectionService.findSubCollections(elem.getId(),false);
//                Collection <VDCCollection> subcollections = elem.getSubCollections();
                if (subcollections.size()>0){
                    buildDisplayModel(collections,subcollections,level);
                }
            }
        }
        level--;
        return level;
    }

    private CollectionModel getRow(final VDCCollection collection, final int level) {
        CollectionModel row = new CollectionModel();
        row.setLevel(level);
        row.setId(collection.getId());
        row.setName(collection.getName());
        row.setSelected(false);
        return row;
    }
    
    private boolean isViewable(VDCCollection vdcCollection){
        boolean viewable = true;
        if (vdcCollection.getReviewState() != null){
            viewable = !vdcCollection.getReviewState().equals(reviewStateService.REVIEW_STATE_IN_REVIEW);
        }
        return viewable;
    }
    
    private boolean isViewable(Study s){
        boolean viewable=false;
        if (s != null){
            viewable = ((!s.getOwner().isRestricted() || s.getOwner().getId().equals(getVDCSessionBean().getLoginBean().getCurrentVDC().getId())) &&  !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW).getId()) && !s.getReviewState().getId().equals(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW).getId()) && (!s.isRestricted() || isUserAllowed(s.getAllowedUsers(),getVDCSessionBean().getLoginBean().getUser().getId())));
        }
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
    
    public String search(){
//        String query = buildQuery();
        List searchCollections = null;
        boolean searchOnlySelectedCollections = false;
        if ( radioButtonList1.getValue() != null){
            String radioButtonStr = (String) radioButtonList1.getValue();
            if (radioButtonStr.indexOf("Only") > 1){
                searchOnlySelectedCollections = true;
            }
            
            searchCollections = new ArrayList();
            List <CollectionModel> collectionModelList = (List <CollectionModel>) dataTable1Model.getWrappedData();
            for (Iterator it = collectionModelList.iterator(); it.hasNext();) {
                CollectionModel elem = (CollectionModel) it.next();
                if (searchOnlySelectedCollections){
                    if(elem.isSelected()){
                        VDCCollection selectedCollection = vdcCollectionService.find(elem.getId());
                        searchCollections.add(selectedCollection);
                    }
                } else{
                    VDCCollection selectedCollection = vdcCollectionService.find(elem.getId());
                    searchCollections.add(selectedCollection);
                }
            }
            if (searchCollections.size()==0){
                searchOnlySelectedCollections = false;
            }
            
        }         
        List <SearchTerm> searchTerms = buildSearchTermList();
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        List<Long> viewableIds = getViewableStudyIds(thisVDC, searchCollections, searchTerms);
        
        StudyListing sl = new StudyListing(StudyListing.VDC_SEARCH);
        sl.setStudyIds(viewableIds);
        sl.setSearchTerms(searchTerms);
       
        getVDCRequestBean().setStudyListing(sl);
        
        return "search";
    }

    private List<Long> getViewableStudyIds(final VDC thisVDC, final List searchCollections, final List<SearchTerm> searchTerms) {
        List matchedIds = null;
        matchedIds = indexServiceBean.search(thisVDC,searchCollections,searchTerms);
        List<Long> viewableIds = viewableStudiesFilter(matchedIds);
        return viewableIds;
    }

    private List<Long> viewableStudiesFilter(final List matchedIds) {
        VDC vdc = getVDCRequestBean().getCurrentVDC();
        VDCUser user = getVDCSessionBean().getUser();
        List <Long> viewableIds = new ArrayList();
        for (Iterator it = matchedIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            try{
                Study matchedStudy = studyService.getStudy(elem);
                if (StudyUI.isStudyVisibleToUser(matchedStudy,vdc,user)){
//                if (isViewable(matchedStudy)){
                    viewableIds.add(elem);
                }
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        return viewableIds;
    }
    

    public boolean isDateItem(String s){
        boolean retVal = s!=null && (s.equalsIgnoreCase("Production Date") || s.equalsIgnoreCase("Distribution Date") || s.equalsIgnoreCase("Date of Deposit") || s.startsWith("Time Period Covered"));
        return retVal;
    }
    
    public boolean isDateItem1(){
        return isDateItem(dropdown1.getValue().toString());
     }
    
    public boolean isDateItem2(){
        return isDateItem(dropdown3.getValue().toString());
    }
    
    public boolean isDateItem3(){
        return isDateItem(dropdown5.getValue().toString());
    }
    
    public boolean isDateItem4(){
        return isDateItem(dropdown7.getValue().toString());
    }

    public String cancel(){
        return "home";
    }
    
    public void searchFieldListener(ValueChangeEvent vce){
        FacesContext.getCurrentInstance().renderResponse();
    }

    
    public String indexAll(){
        indexServiceBean.indexAll();
        return "success";
    }
    
    protected String buildQuery(){
        StringBuffer query = new StringBuffer();
        if (((String)textField1.getValue()).length() > 0){
            query.append(advSearchFieldIndexName( (String) dropdown1.getValue()) + " " + operatorToken((String) dropdown9.getValue()) + " " + (String) textField1.getValue());
        }
        if (((String)textField2.getValue()).length() > 0){
            query.append(" AND "+ advSearchFieldIndexName((String) dropdown3.getValue()) + " "  + operatorToken((String) dropdown4.getValue()) + " " + (String) textField2.getValue());
        }
        if (((String)textField3.getValue()).length() >0){
            query.append(" AND "+ advSearchFieldIndexName((String) dropdown5.getValue()) + " "  + operatorToken((String) dropdown6.getValue()) + " "  + (String) textField3.getValue());
        }
        if (((String)textField4.getValue()).length() > 0){
            query.append(" AND "+ advSearchFieldIndexName((String) dropdown7.getValue()) + " "  + operatorToken((String) dropdown8.getValue()) + " "  + (String) textField4.getValue());
        }
        return query.toString();
    }
    
    protected List <SearchTerm> buildSearchTermList(){
        List <SearchTerm> searchTerms = new ArrayList();
        if (((String)textField1.getValue()).length() > 0){
            SearchTerm searchTerm1 = new SearchTerm();
            searchTerm1.setFieldName(advSearchFieldIndexName((String) dropdown1.getValue()));
            searchTerm1.setOperator(operatorToken((String) dropdown9.getValue()));
            searchTerm1.setValue((String) textField1.getValue());
            searchTerms.add(searchTerm1);
        }
        if (((String)textField2.getValue()).length() > 0){
            SearchTerm searchTerm2 = new SearchTerm();
            searchTerm2.setFieldName(advSearchFieldIndexName((String) dropdown3.getValue()));
            searchTerm2.setOperator(operatorToken((String) dropdown4.getValue()));
            searchTerm2.setValue((String) textField2.getValue());
            searchTerms.add(searchTerm2);

        }
        if (((String)textField3.getValue()).length() >0){
            SearchTerm searchTerm3 = new SearchTerm();
            searchTerm3.setFieldName(advSearchFieldIndexName((String) dropdown5.getValue()));
            searchTerm3.setOperator(operatorToken((String) dropdown6.getValue()));
            searchTerm3.setValue((String) textField3.getValue());
            searchTerms.add(searchTerm3);
        }
        if (((String)textField4.getValue()).length() > 0){
            SearchTerm searchTerm4 = new SearchTerm();
            searchTerm4.setFieldName(advSearchFieldIndexName((String) dropdown7.getValue()));
            searchTerm4.setOperator(operatorToken((String) dropdown8.getValue()));
            searchTerm4.setValue((String) textField4.getValue());
            searchTerms.add(searchTerm4);
        }
        return searchTerms;
    }
    
    protected String operatorToken(String operator)
    {
        return (String) operatorMap.get(operator);
    }
    
    protected String advSearchFieldIndexName(String displayName){
        return (String) advSearchFieldMap.get(displayName);
    }

    private boolean isValid(String dateString, String pattern) {
        boolean valid;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(dateString);
            valid=true;
        }catch (ParseException e) {
            valid=false;
        }
        return valid;
    }
    
    public void validateDate(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String dateString = (String) value;
        boolean valid=false;
        
        String monthDayYear = "yyyy-MM-dd";
        String monthYear = "yyyy-MM";
        String year = "yyyy";
        
        if (dateString.length()==4) {
            valid = isValid(dateString,year);
        } else if (dateString.length()>4 && dateString.length()<=7) {
            valid = isValid(dateString, monthYear);
        } else if (dateString.length()>7) {
            valid = isValid(dateString,monthDayYear);
        }
        
        if (!valid) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Date Format.  Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY.");
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    /**
     * <p>Callback method that is called whenever a page is navigated to,
     * either directly via a URL, or indirectly via page navigation.
     * Customize this method to acquire resources that will be needed
     * for event handlers and lifecycle methods, whether or not this
     * page is performing post back processing.</p>
     * 
     * <p>Note that, if the current request is a postback, the property
     * values of the components do <strong>not</strong> represent any
     * values submitted with this request.  Instead, they represent the
     * property values that were saved for this view when it was rendered.</p>
     */
            
}

