/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.web.DataverseGrouping;
import edu.harvard.hmdc.vdcnet.web.LocalizedDate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author wbossons
 */
public class DataverseGroupingTest extends TestCase {
    
    public DataverseGroupingTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private ArrayList itemBeans = new ArrayList();
    private ArrayList dvGroupItemBeans;
    private boolean isInit;
    public static final String GROUP_INDENT_STYLE_CLASS = "GROUP_INDENT_STYLE_CLASS";
    public static final String GROUP_ROW_STYLE_CLASS = "groupRow";
    public static final String CHILD_INDENT_STYLE_CLASS = "CHILD_INDENT_STYLE_CLASS";
    public String CHILD_ROW_STYLE_CLASS;
    public static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";
    
    // ************** DataverseGrouping Fields ********************
    ArrayList childItems = new ArrayList();
    private Map allChildren = new LinkedHashMap();//used by addchilditem to collect all of the children for expansion and contraction support
    protected String expandImage;   // + or >
    protected String contractImage; // - or v
    protected static final String DEFAULT_IMAGE_DIR = "/resources/";
     
    //sort fields
    protected String sortColumnName;
    protected boolean ascending;
    // we only want to resort if the oder or column has changed.
    protected String oldSort;
    protected boolean oldAscending;
    
    // dataTableColumn Names
    private static final String nameColumnName          = "Name";
    private static final String affiliationColumnName   = "Affiliation";
    private static final String dateReleasedColumnName  = "Date Released";
    private static final String lastUpdatedColumnName   = "Last Updated";
    private static final String activityColumnName      = "Activity";
    
    //other props
    private Long id; //TBD if needed
    private String alias;
    private String groupKey;
    private String name;
    private String affiliation;
    private String dvnDescription;
    private Long downloads;
    private Timestamp releaseDate;
    private Timestamp lastUpdateTime;
    private Timestamp creationDate;
    private LocalizedDate localizedDate = new LocalizedDate();
    private String recordType;
    private String activity;
    
    ArrayList parentItems = new ArrayList();
    //ArrayList childItems = new ArrayList();
    
    DataverseGrouping[] items;
    
    /**
     * Test of addChildItem method, of class DataverseGrouping.
     */
    public void testAddChildItem() {
        System.out.println("addChildItem");
        Date date = new Date();
        Timestamp releasedTimeStamp = new Timestamp(date.getTime());
        Timestamp lastUpdateTimeStamp = new Timestamp(date.getTime());
        DataverseGrouping instance = new DataverseGrouping(new Long("1"), "Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, false);
        DataverseGrouping dvGroupRecord = new DataverseGrouping("Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        assertEquals("The child item should have one record", 1, itemBeans.size());
        
    }

     
    /**
     * Test of sort method, of class DataverseGrouping.
     */
    public void testSort() {
        System.out.println("sort");
        Date date = new Date();
        
        Timestamp releasedTimeStamp = new Timestamp(date.getTime());
        Timestamp lastUpdateTimeStamp = new Timestamp(date.getTime());
        DataverseGrouping instance = new DataverseGrouping(new Long("1"), "Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, false);
        DataverseGrouping dvGroupRecord = new DataverseGrouping("A Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        //2
        releasedTimeStamp = new Timestamp(date.getTime());
        lastUpdateTimeStamp = new Timestamp(date.getTime());
        //DataverseGrouping instance = new DataverseGrouping("Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE);
        dvGroupRecord = new DataverseGrouping("B Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        //3
        releasedTimeStamp = new Timestamp(date.getTime());
        lastUpdateTimeStamp = new Timestamp(date.getTime());
        //DataverseGrouping instance = new DataverseGrouping("Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE);
        dvGroupRecord = new DataverseGrouping("C Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        //4
        releasedTimeStamp = new Timestamp(date.getTime());
        lastUpdateTimeStamp = new Timestamp(date.getTime());
        //DataverseGrouping instance = new DataverseGrouping("Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE);
        dvGroupRecord = new DataverseGrouping("D Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        //5
        releasedTimeStamp = new Timestamp(date.getTime());
        lastUpdateTimeStamp = new Timestamp(date.getTime());
        //DataverseGrouping instance = new DataverseGrouping("Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE);
        dvGroupRecord = new DataverseGrouping("E Child Dataverse", "childdv", "No Affiliation", releasedTimeStamp, lastUpdateTimeStamp, "This is the description.", "dataverse", "activityClassName");
        instance.addChildItem(dvGroupRecord);
        
        ascending = false;
        sortColumnName = "affiliation";
        instance.sort();
        
        String lastKey = new String();
        String lastControlKey = new String();
         //Control group
        ArrayList controlChildren = new ArrayList();
        Iterator iterator = controlChildren.iterator();
        Iterator itemiterator = childItems.iterator();
        boolean isSorted = true;
        while (itemiterator.hasNext()) {
           DataverseGrouping grouping = (DataverseGrouping)itemiterator.next();
           DataverseGrouping control  = (DataverseGrouping)iterator.next();
           lastKey = grouping.getAffiliation();
           lastControlKey = control.getAffiliation();
           assertNotNull("The affiliation value is null ", control.getAffiliation());
           assertNotNull("The affiliation value is null ", control.getLastUpdateTime());
           assertNotNull("The affiliation value is null ", control.getReleaseDate());
           if (lastKey.equals(lastControlKey)) {
               isSorted = false;
               break;
           }
        }
        if (!isSorted) {
            fail("The sort failed.");
        } else {
            System.out.println("Success: End sort test!");
        }
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }


    /**
     * Test of getGroupKey method, of class DataverseGrouping.
     */
    public String testGetGroupKey() {
        System.out.println("getGroupKey");
        DataverseGrouping instance = new DataverseGrouping(new Long("1"), "Parent Dataverse", "group", itemBeans, true, EXPAND_IMAGE, CONTRACT_IMAGE, true);
        String expResult = "ParentDataverse".toLowerCase();
        String result = instance.getGroupKey();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        return(this.groupKey);
    }

  
}
