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
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author wbossons
 */
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
        getVDCRequestBean().setSuccessMessage("Successfully updated study comments settings.");
        getVDCRequestBean().setCurrentVDC(vdc);
        return "myOptions";
    }

    public String cancel() {
        return "cancelVDC";
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
