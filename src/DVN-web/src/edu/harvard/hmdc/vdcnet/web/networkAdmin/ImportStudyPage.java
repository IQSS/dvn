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
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
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
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author gdurand
 */
@EJB(name="editStudy", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class ImportStudyPage extends VDCBaseBean  {
    
    @EJB VDCServiceLocal vdcService;
    @EJB StudyServiceLocal studyService;
    @EJB IndexServiceLocal indexService;
    
    private UploadedFile browserFile;
    private Long vdcId;
    private int xmlFileFormat;
    private boolean registerHandle;
    private boolean generateHandle;
    private boolean allowUpdates;
    private boolean checkRestrictions;
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
    
    public List getXmlFileFormatRadioItems() {
        List xmlFileFrmatRadioItems = new ArrayList();
        xmlFileFrmatRadioItems.add( new SelectItem(0, "ddi") );
        xmlFileFrmatRadioItems.add( new SelectItem(1, "mif") );
        return xmlFileFrmatRadioItems;
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
    
    public int getXmlFileFormat() {
        return xmlFileFormat;
    }

    public void setXmlFileFormat(int xmlFileFormat) {
        this.xmlFileFormat = xmlFileFormat;
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
                
                Study study = studyService.importStudy(xmlFile, xmlFileFormat, vdcId, lb.getUser().getId(), registerHandle, generateHandle, allowUpdates, checkRestrictions, copyFiles, null);
                indexService.updateStudy(study.getId());
                
                // create result message
                HttpServletRequest req = (HttpServletRequest) getExternalContext().getRequest();
                String link = req.getScheme() +"://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() 
                        + "/faces/study/StudyPage.jsp?studyId=" + study.getId();
                
                resultMsg = "Import succeeded: " + link;
                
            } catch (Exception ex) {
                resultMsg = "Import failed: " + ex.getMessage() ;
                ex.printStackTrace();
            }
        }
        
        FacesMessage message = new FacesMessage(resultMsg);
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, message);
        
        return null;
    }


    public boolean isRegisterHandle() {
        return registerHandle;
    }

    public void setRegisterHandle(boolean registerHandle) {
        this.registerHandle = registerHandle;
    }

    public boolean isGenerateHandle() {
        return generateHandle;
    }

    public void setGenerateHandle(boolean generateHandle) {
        this.generateHandle = generateHandle;
    }

    public boolean isAllowUpdates() {
        return allowUpdates;
    }

    public void setAllowUpdates(boolean allowUpdates) {
        this.allowUpdates = allowUpdates;
    }

    public boolean isCheckRestrictions() {
        return this.checkRestrictions;
    }

    public void setCheckRestrictions(boolean checkRestrictions) {
        this.checkRestrictions = checkRestrictions;
    }
    
    public boolean isCopyFiles() {
        return copyFiles;
    }
    
    public void setCopyFiles(boolean copyFiles) {
        this.copyFiles = copyFiles;
    }    

    
    
}
