/*
 * ImportStudyPage.java
 *
 * Created on January 12, 2007, 2:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import com.sun.rave.web.ui.model.UploadedFile;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.LoginBean;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class ImportStudyPage extends VDCBaseBean  {
    
    @EJB VDCServiceLocal vdcService;
    @EJB StudyServiceLocal studyService;
    
    private UploadedFile browserFile;
    private Long vdcId;
    private boolean copyFiles;
    
    
    /** Creates a new instance of ImportStudyPage */
    public ImportStudyPage() {
    }
    
    public void init() {
        super.init();
        vdcId = getVDCRequestBean().getCurrentVDCId();
    }
    
    public List getVdcRadioItems() {
        List vdcRadioItems = new ArrayList();
        Iterator iter = vdcService.findAll().iterator();
        while (iter.hasNext()) {
            VDC vdc = (VDC) iter.next();
            vdcRadioItems.add( new SelectItem(vdc.getId(), vdc.getName()) );
            
        }
        return vdcRadioItems;
    }
    
    public UploadedFile getBrowserFile() {
        return browserFile;
    }
    
    public void setBrowserFile(UploadedFile browserFile) {
        this.browserFile = browserFile;
    }
    
    public Long getVdcId() {
        return vdcId;
    }
    
    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }
    
    public boolean isCopyFiles() {
        return copyFiles;
    }
    
    public void setCopyFiles(boolean copyFiles) {
        this.copyFiles = copyFiles;
    }
    
    public String import_action() {
        String resultMsg = null;
        
        LoginBean lb = getVDCSessionBean().getLoginBean();
        if (lb == null) {
            resultMsg = "You must be logged in to import a study.";
        } else {
            try {
                File xmlFile = File.createTempFile("ddi", ".xml");
                browserFile.write(xmlFile);
                studyService.importStudy(xmlFile, null, vdcId, lb.getUser().getId(), checkRestrictions, true, false, copyFiles);
                resultMsg = "Import succeeded.";
            } catch (Exception ex) {
                resultMsg = "Import failed.";
                ex.printStackTrace();
            }
        }
        
        FacesMessage message = new FacesMessage(resultMsg);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, message);
        
        return null;
    }

    /**
     * Holds value of property checkRestrictions.
     */
    private boolean checkRestrictions;

    /**
     * Getter for property checkRestrictions.
     * @return Value of property checkRestrictions.
     */
    public boolean isCheckRestrictions() {
        return this.checkRestrictions;
    }

    /**
     * Setter for property checkRestrictions.
     * @param checkRestrictions New value of property checkRestrictions.
     */
    public void setCheckRestrictions(boolean checkRestrictions) {
        this.checkRestrictions = checkRestrictions;
    }
    
    
    
}
