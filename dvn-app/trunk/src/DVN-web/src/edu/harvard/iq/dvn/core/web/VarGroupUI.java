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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.visualization.VarGroup;
import java.util.List;


/**
 *
 * @author skraffmiller
 */
public class VarGroupUI {
    private VarGroup varGroup;
    private List <VarGroupTypeUI> varGroupTypes;
    private List <VarGroupTypeUI> varGroupTypesSelectItems;
    private List <Long> dataVariablesSelected;
    private String groupTypesSelectedString;
    private Long numberOfVariablesSelected = new Long(0);

    public VarGroup getVarGroup() {
        return varGroup;
    }

    public void setVarGroup(VarGroup varGroup) {
        this.varGroup = varGroup;
    }

    public List<VarGroupTypeUI> getVarGroupTypes() {
        return varGroupTypes;
    }

    public void setVarGroupTypes(List<VarGroupTypeUI> varGroupTypesSelected) {
        this.varGroupTypes = varGroupTypesSelected;
    }

    public List<VarGroupTypeUI> getVarGroupTypesSelectItems(){
        return varGroupTypesSelectItems;
    }

    public void  setVarGroupTypesSelectItems(List <VarGroupTypeUI> varGroupTypesSelectItems ){
        this.varGroupTypesSelectItems = varGroupTypesSelectItems;
    }

    public List<Long> getDataVariablesSelected() {
        return dataVariablesSelected;
    }

    public void setDataVariablesSelected(List<Long> dataVariablesSelected) {
        this.dataVariablesSelected = dataVariablesSelected;
    }

    public Long getNumberOfVariablesSelected() {
        return numberOfVariablesSelected;
    }

    public void setNumberOfVariablesSelected(Long numberOfVariablesSelected) {
        this.numberOfVariablesSelected = numberOfVariablesSelected;
    }


    public String getGroupTypesSelectedString() {
        return groupTypesSelectedString;
    }

    public void setGroupTypesSelectedString(String groupTypesSelectedString) {
        this.groupTypesSelectedString = groupTypesSelectedString;
    }

    
}
