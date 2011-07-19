
package edu.harvard.iq.dvn.core.web;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.visualization.SortVarGroupByName;
import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGroupType;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author skraffmiller
 */
public class VarGroupUIList extends SortableList {
    private static final String NAME_COLUMN_NAME     = "Name";
    private static final String TYPE_COLUMN_NAME     = "Type";

    private List<VarGroupUI> varGroupUIList;
    private List<VarGroupUI> varGroupUIListIn;
    private List<VarGroup> varGroupListIn;
    
    public static String getNAME_COLUMN_NAME() {
        return NAME_COLUMN_NAME;
    }

    public static String getTYPE_COLUMN_NAME() {
        return TYPE_COLUMN_NAME;
    }
    
    public String getNameColumnName()  { return NAME_COLUMN_NAME; }
    public String getTypeColumnName()  { return TYPE_COLUMN_NAME; }
    
    public void init(){
        
        if (sortColumnName == null || sortColumnName.isEmpty()){
            sortColumnName = NAME_COLUMN_NAME;
        } 
        oldSort = "";
        ascending = true;
        oldAscending = ascending;

    }
    
    public List<VarGroupUI> getVarGroupUIList(){
        System.out.println("In get getVarGroupUIList = " + this.sortColumnName);
        if (oldSort == null || !oldSort.equals(sortColumnName) ) {
            System.out.println("!oldSort.eq");
            sort();
            oldSort         = sortColumnName;
            oldAscending    = ascending;
            System.out.println("oldSort = " + oldSort);
        } else if (oldAscending != ascending) {

            Collections.reverse(varGroupUIList);
            oldAscending    = ascending;
        }
        
        return varGroupUIList;
    }
    
    
    public VarGroupUIList(){
        init();
    }
    
    public VarGroupUIList(List varGroupUIListIn){
        if (varGroupUIList !=null){
            varGroupUIList.clear();
        } else {
            varGroupUIList = new ArrayList();
            System.out.println("Run Init");

        }
        for (Object varGroupInit: varGroupUIListIn ){
            VarGroupUI varGroupCast = (VarGroupUI) varGroupInit;
            varGroupUIList.add(varGroupCast);
        }
    }
    
        //setters
    /**
     * Set sortColumnName type.
     *
     * @param ascending true for ascending sortColumnName, false for descending sortColumnName.
     */
    public void setAscending(boolean ascending) {
        oldAscending = this.ascending;
        this.ascending = ascending;
       
    }

    /**
     * Sets the sortColumnName column
     *
     * @param sortColumnName column to sortColumnName
     */
    @Override
    public void setSortColumnName(String sortColumnName) {
        oldSort = this.sortColumnName;
        this.sortColumnName = sortColumnName;   
        sort();
        System.out.println("This.sortColumnName = " + this.sortColumnName);
    }

    @Override
    protected void sort() {
            System.out.println("in sort");
            System.out.println("This.sortColumnName = " + this.sortColumnName);
            String orderBy = null;
            if (sortColumnName == null) {
                System.out.println("sortColumnName == null");
                return;
            }
            
            if (this.sortColumnName.equals(NAME_COLUMN_NAME)) {
                orderBy = "Name";
            } else if (this.sortColumnName.equals(TYPE_COLUMN_NAME)) {
                orderBy = "Type";
            } else {
                throw new RuntimeException("Unknown sortColumnName: " + sortColumnName);
            }
            
            if (varGroupUIListIn !=null){
                varGroupUIListIn.clear();
            } else {
                varGroupUIListIn = new ArrayList();
            }
            
                        
            if (varGroupListIn !=null){
                varGroupListIn.clear();
            } else {
                varGroupListIn = new ArrayList();
            }
            
            for (VarGroupUI varGroupUIIn: varGroupUIList){
                varGroupListIn.add(varGroupUIIn.getVarGroup());
                varGroupUIListIn.add(varGroupUIIn);
            }
            
            if (orderBy.equals(TYPE_COLUMN_NAME)){
                System.out.println("sortColumnName == TYPE_COLUMN_NAME");
                Collections.sort(varGroupListIn);
            } else {
                System.out.println("sortColumnName == NAME");
                Collections.sort(varGroupListIn, new SortVarGroupByName());
            }
            
            varGroupUIList = new ArrayList<VarGroupUI>();
            for (VarGroup varGroupOut: varGroupListIn){
                for (VarGroupUI varGroupUIout: varGroupUIListIn ){
                    if (varGroupOut.equals(varGroupUIout.getVarGroup())){
                       varGroupUIList.add(varGroupUIout);
                    }
                }                                
            }
            System.out.println("sort done");
    }
    
    

    @Override
    protected boolean isDefaultAscending(String sortColumn) {
        return true;
    }
    
}
