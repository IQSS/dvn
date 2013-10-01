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
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectBooleanCheckbox;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

/**
 *
 * @author wbossons
 */
@ViewScoped
@Named("EditStudyCommentsPage")
public class EditStudyCommentsPage extends VDCBaseBean implements Serializable {
    @EJB VDCServiceLocal vdcService;

    protected boolean allowStudyComments = true;
    protected HtmlSelectBooleanCheckbox allowStudyCommentsCheckbox = new HtmlSelectBooleanCheckbox();
    protected VDC vdc;

     /**
     * <p> component initialization.  </p>
     */
    public void init(){
        super.init();
        vdc = getVDCRequestBean().getCurrentVDC();
        allowStudyComments = vdc.isAllowStudyComments();
        allowStudyCommentsCheckbox.setValue(allowStudyComments);
    }
    
    /* action methods */
    public String edit(){
        allowStudyComments = (Boolean)allowStudyCommentsCheckbox.getValue();
        vdc.setAllowStudyComments(allowStudyComments);
        vdcService.edit(vdc);
        getVDCRequestBean().setCurrentVDC(vdc);
        getVDCRenderBean().getFlash().put("successMessage", "Successfully updated study comments settings.");
        return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
    }

    public String cancel() {
        return "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
    }

    /* getters and setters */

    /**
     * Get the value of allowStudyCommentsCheckbox
     *
     * @return the value of allowStudyCommentsCheckbox
     */
    public HtmlSelectBooleanCheckbox getAllowStudyCommentsCheckbox() {
        return allowStudyCommentsCheckbox;
    }

    /**
     * Set the value of allowStudyCommentsCheckbox
     *
     * @param allowStudyCommentsCheckbox new value of allowStudyCommentsCheckbox
     */
    public void setAllowStudyCommentsCheckbox(HtmlSelectBooleanCheckbox allowStudyCommentsCheckbox) {
        this.allowStudyCommentsCheckbox = allowStudyCommentsCheckbox;
    }

   /**
     * Get the value of allowStudyComments
     *
     * @return the value of allowStudyComments
     */
    public boolean isAllowStudyComments() {
        return allowStudyComments;
    }

    /**
     * Set the value of allowStudyComments
     *
     * @param allowStudyComments new value of allowStudyComments
     */
    public void setAllowStudyComments(boolean allowStudyComments) {
        this.allowStudyComments = allowStudyComments;
    }




}
