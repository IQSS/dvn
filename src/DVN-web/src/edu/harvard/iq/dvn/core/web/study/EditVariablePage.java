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
 * EditVariablePage.java
 *
 * Created on Nov 7, 2007, 2:08:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Gustavo
 */
@Named("EditVariablePage")
@ViewScoped
public class EditVariablePage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB VariableServiceLocal varService;
    
    
    public EditVariablePage() {
    }
    
    private Long dtId;
    private DataTable dt;
    
    public DataTable getDt() {
        return dt;
    }
    
    public void setDt(DataTable dt) {
        this.dt = dt;
    }
    
    private String dvFilter;
    private List<DataVariable> dataVariables = new ArrayList();
    
    private List validationDvNames = new ArrayList();
    
    
    public void init() {
        super.init();
        
        if ( isFromPage("EditVariablePage") ) {
            dtId = new Long(getRequestParam("form1:dtId"));
            dvFilter = getRequestParam("form1:dvFilter");
        }
        
        if (dtId == null) {
            dtId = getVDCRequestBean().getDtId();
        }
        if (dvFilter == null) {
            dvFilter = getVDCRequestBean().getDvFilter();
        }
        
        if (dtId != null) {
            dt = varService.getDataTable(dtId);
            
            // now check filter
            if (dvFilter != null && !dvFilter.equals("")) {
                // set up names for validation (we will remove them as we
                // iterate through the filter
                for (DataVariable dv : dt.getDataVariables()) {
                    validationDvNames.add(dv.getName());
                }
                
                StringTokenizer st = new StringTokenizer( dvFilter, ",");
                while (st.hasMoreTokens()) {
                    try {
                        Long dvId =  new Long( st.nextToken() );
                        for (DataVariable dv : dt.getDataVariables()) {
                            if ( dv.getId().equals(dvId) ) {
                                dataVariables.add(dv);
                                // since we are editing this one, remove it from the validation names
                                validationDvNames.remove(dv.getName());
                            }
                        }
                    } catch (NumberFormatException nfe) {
                        // do nothing; possibly a recode
                    }
                }
                
                
            } else {
                dataVariables = dt.getDataVariables();
            }
            
        } else {
            // WE SHOULD HAVE A DTID ID, throw an error
            System.out.println("ERROR: in EditVariablePage, without a dtId");
        }
        
    }
    
    public Long getDtId() {
        return dtId;
    }
    
    public void setDtId(Long dtId) {
        this.dtId = dtId;
    }
    
    
    public List<DataVariable> getDataVariables() {
        return dataVariables;
    }
    
    public void setDataVariables(List<DataVariable> dataVariables) {
        this.dataVariables = dataVariables;
    }
    
    public String getDvFilter() {
        return dvFilter;
    }
    
    public void setDvFilter(String dvFilter) {
        this.dvFilter = dvFilter;
    }
    
    
    
    public String save_action() {
                
        // this lets the Analysis page know which dt, and also to reset the session
        getVDCRequestBean().setDtId(dtId);
        
        // this is used by the jsp itself to set hidden dt variable
        HttpServletRequest req = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
        req.setAttribute("dtId", dtId.toString() );
        
        return "subsetting";
    }
    
    
    public String cancel_action() {
        // this lets the Analysis page know which dt, and also to reset the session
        getVDCRequestBean().setDtId(dtId);
        
        // this is used by the jsp itself to set hidden dt variable
        HttpServletRequest req = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
        req.setAttribute("dtId", dtId.toString() );
        return "subsetting";
    }
    
    public void validateDVName(FacesContext context,
            UIComponent toValidate,
            Object value) {
        String dvName = (String) value;
        String errorMessage = null;
        
        // check invalid characters
        if (    dvName.contains("\\") ||
                dvName.contains("/") ||
                dvName.contains(":") ||
                dvName.contains("*") ||
                dvName.contains("?") ||
                dvName.contains("\"") ||
                dvName.contains("<") ||
                dvName.contains(">") ||
                dvName.contains("|") ||
                dvName.contains(";") ||
                dvName.contains("#")) {
            errorMessage = "cannot contain any of the following characters: \\ / : * ? \" < > | ; #";
        }
        
        // now check unique varName against other dv names
        Iterator iter = validationDvNames.iterator();
        while (iter.hasNext()) {
            if ( dvName.equals( (String) iter.next() ) ) {
                errorMessage = "must be unique.";
                break;
            }
        }
        
        // now add this name to the validation list
        validationDvNames.add(dvName);
        
        if (errorMessage != null) {
            ((UIInput)toValidate).setValid(false);
            
            FacesMessage message = new FacesMessage("Invalid Variable Name - " + errorMessage);
            context.addMessage(toValidate.getClientId(context), message);
        }
        
    }
    
    
    
}
