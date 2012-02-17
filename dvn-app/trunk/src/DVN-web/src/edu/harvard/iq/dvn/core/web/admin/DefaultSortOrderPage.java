
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("DefaultSortOrderPage")
public class DefaultSortOrderPage extends VDCBaseBean implements java.io.Serializable  {
     @EJB VDCServiceLocal vdcService;
     private String defaultSortOrder = "";

    public String getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public void setDefaultSortOrder(String defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public void init() {
        super.init();
        defaultSortOrder = getVDCRequestBean().getCurrentVDC().getDefaultSortOrder();

    }

    public String cancel(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();
        getVDCRequestBean().setCurrentVDC(thisVDC);
        String    forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        return forwardPage;
    }

    public String save(){
        VDC thisVDC = getVDCRequestBean().getCurrentVDC();

        thisVDC.setDefaultSortOrder(defaultSortOrder);
        vdcService.edit(thisVDC);

        getVDCRequestBean().setCurrentVDC(thisVDC);
        String    forwardPage="/admin/OptionsPage?faces-redirect=true&vdcId="+getVDCRequestBean().getCurrentVDC().getId();
        getExternalContext().getFlash().put("successMessage","Successfully updated default sort order.");
        return forwardPage;
        
    }

}
