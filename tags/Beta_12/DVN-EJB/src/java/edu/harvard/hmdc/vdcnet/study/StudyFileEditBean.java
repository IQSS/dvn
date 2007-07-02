/*
 * StudyFileEditBean.java
 *
 * Created on October 10, 2006, 5:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author gdurand
 */
public class StudyFileEditBean implements Serializable{
    
    /** Creates a new instance of StudyFileEditBean */
    public StudyFileEditBean(StudyFile sf) {
        this.studyFile = sf;
    }
    
    private StudyFile studyFile;
    
    private String fileCategoryName;
    private String originalFileName;
    private String tempSystemFileLocation;
    private String ingestedSystemFileLocation;
    private boolean deleteFlag;

    public StudyFile getStudyFile() {
        return studyFile;
    }
    
    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
}    
    
    public String getFileCategoryName() {
        return fileCategoryName;
    }
    
    public void setFileCategoryName(String fileCategoryName) {
        this.fileCategoryName = fileCategoryName;
    }
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getTempSystemFileLocation() {
        return tempSystemFileLocation;
    }

    public void setTempSystemFileLocation(String tempSystemFileLocation) {
        this.tempSystemFileLocation = tempSystemFileLocation;
    }

    public String getIngestedSystemFileLocation() {
        return ingestedSystemFileLocation;
    }

    public void setIngestedSystemFileLocation(String ingestedSystemFileLocation) {
        this.ingestedSystemFileLocation = ingestedSystemFileLocation;
    }
   
    
    public boolean isDeleteFlag() {
        return deleteFlag;
    }
    
    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }    

    public void addFileToCategory(Study s) {
        StudyFile file = this.getStudyFile();
        String catName = this.getFileCategoryName();
        
        Iterator iter = s.getFileCategories().iterator();
        while (iter.hasNext()) {
            FileCategory cat = (FileCategory) iter.next();
            if ( cat.getName().equals( catName ) ) {
                file.setFileCategory(cat);
                cat.getStudyFiles().add(file);
                return;
            }
        }   

        // category was not found, so we create a new file category
        FileCategory cat = new FileCategory();
        cat.setStudy(s);
        s.getFileCategories().add(cat);       
        cat.setName( catName );
        cat.setStudyFiles(new ArrayList());

        // link cat to file
        file.setFileCategory(cat);
        cat.getStudyFiles().add(file);
    }    

}
