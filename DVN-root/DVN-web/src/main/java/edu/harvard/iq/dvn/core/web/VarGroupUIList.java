/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dvn.core.web;

// Commented out to compile under IceFaces 2.0! -- L.A. import edu.emory.mathcs.backport.java.util.Collections;
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
        if (oldSort == null || !oldSort.equals(sortColumnName) ) {
            sort();
            oldSort         = sortColumnName;
            oldAscending    = ascending;
        } else if (oldAscending != ascending) {

	    // Commented out to compile under IceFaces 2.0! -- L.A.            Collections.reverse(varGroupUIList);
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
        }
        for (Object varGroupInit: varGroupUIListIn ){
            VarGroupUI varGroupCast = (VarGroupUI) varGroupInit;
            varGroupUIList.add(varGroupCast);
        }
    }
    
    public VarGroupUIList(List varGroupUIListSet, boolean sort){
        
        if (varGroupUIListSet == null){
            return;
        }
        
            if (varGroupListIn !=null){
                varGroupListIn.clear();
            } else {
                varGroupListIn = new ArrayList();
            }
            
            if (varGroupUIListIn !=null){
                varGroupUIListIn.clear();
            } else {
                varGroupUIListIn = new ArrayList();
            }
            

            
            for (Object varGroupUIIn: varGroupUIListSet){
                VarGroupUI varGroupUICast = (VarGroupUI) varGroupUIIn;
                varGroupListIn.add(varGroupUICast.getVarGroup());
                varGroupUIListIn.add(varGroupUICast);
            }
            
            
            if (sort){               
	    // Commented out to compile under IceFaces 2.0! -- L.A.            Collections.reverse(varGroupUIList);
            } else {
	    // Commented out to compile under IceFaces 2.0! -- L.A.            Collections.reverse(varGroupUIList);
            }
            
            varGroupUIList = new ArrayList<VarGroupUI>();
            for (VarGroup varGroupOut: varGroupListIn){
                for (VarGroupUI varGroupUIout: varGroupUIListIn ){
                    if (varGroupOut.equals(varGroupUIout.getVarGroup())){
                       varGroupUIList.add(varGroupUIout);
                    }
                }                                
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
        
    }

    @Override
    protected void sort() {
            String orderBy = null;
            if (sortColumnName == null) {                
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
	    // Commented out to compile under IceFaces 2.0! -- L.A.            Collections.reverse(varGroupUIList);
            } else {
	    // Commented out to compile under IceFaces 2.0! -- L.A.            Collections.reverse(varGroupUIList);
            }
            
            varGroupUIList = new ArrayList<VarGroupUI>();
            for (VarGroup varGroupOut: varGroupListIn){
                for (VarGroupUI varGroupUIout: varGroupUIListIn ){
                    if (varGroupOut.equals(varGroupUIout.getVarGroup())){
                       varGroupUIList.add(varGroupUIout);
                    }
                }                                
            }
    }
    
    

    @Override
    protected boolean isDefaultAscending(String sortColumn) {
        return true;
    }
    
}
