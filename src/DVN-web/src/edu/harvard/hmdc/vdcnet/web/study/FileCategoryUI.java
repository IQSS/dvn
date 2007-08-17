/*
 * FileCategoryUI.java
 *
 * Created on September 28, 2006, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
public class FileCategoryUI  {
    
    private FileCategory fileCategory;
    
    /** Creates a new instance of FileCategoryUI */
    public FileCategoryUI(FileCategory fileCategory, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.fileCategory = fileCategory;
        initStudyFiles(vdc,user, ipUserGroup);

    }  
    
      /** Creates a new instance of FileCategoryUI */
    public FileCategoryUI(FileCategory fileCategory) {
        this.fileCategory = fileCategory;

    }  
    private void initStudyFiles(VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        StudyServiceLocal studyService = null;
        try {
            studyService=(StudyServiceLocal)new InitialContext().lookup("java:comp/env/studyService");
        } catch(Exception e) {
            e.printStackTrace();
        }
        studyFileUIs = new ArrayList<StudyFileUI>();
        List<StudyFile> studyFiles = studyService.getOrderedFilesByCategory(fileCategory.getId());
        for (Iterator it = studyFiles.iterator(); it.hasNext();) {
            StudyFile studyFile = (StudyFile) it.next();
            StudyFileUI studyFileUI = new StudyFileUI(studyFile, vdc, user, ipUserGroup);
            studyFileUIs.add(studyFileUI);
        }
    }
    
    private List<StudyFileUI> studyFileUIs = new ArrayList();
    
    public FileCategory getFileCategory() {
        return fileCategory;
    }
    
    
    private boolean rendered=true;
    
    public void toggleRendered (ActionEvent ae) {
        setRendered(!isRendered());
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    
    public String getDownloadName() {
        if ( fileCategory.getName() == null || fileCategory.getName().trim().equals("") ) {
            return "defaultCategory";
        }
        
        String downloadName = fileCategory.getName().trim();
        downloadName = downloadName.replace('\\','_');
        downloadName = downloadName.replace('/','_');
        downloadName = downloadName.replace(':','_');
        downloadName = downloadName.replace('*','_');
        downloadName = downloadName.replace('?','_');
        downloadName = downloadName.replace('\"','_');
        downloadName = downloadName.replace('<','_');
        downloadName = downloadName.replace('>','_');
        downloadName = downloadName.replace('|','_');
        downloadName = downloadName.replace(';','_');
        downloadName = downloadName.replace('#','_');
        
        return downloadName;
    }

    public boolean isAnyFileUnrestricted() {
       for (Iterator it = getStudyFileUIs().iterator(); it.hasNext();) {
                StudyFileUI studyFileUI = (StudyFileUI) it.next();
                if (!studyFileUI.isRestrictedForUser()) {
                    return true;
                }
            }
        return false;   
    }
    
  
    public List<StudyFileUI> getStudyFileUIs() {
        return studyFileUIs;
    }

    public void setStudyFileUIs(List<StudyFileUI> studyFileUIs) {
        this.studyFileUIs = studyFileUIs;
    }
    
}
