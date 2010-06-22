/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * FileCategoryUI.java
 *
 * Created on September 28, 2006, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.FileCategory;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
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
public class FileCategoryUI implements Comparable, java.io.Serializable   {
    
    private FileCategory fileCategory;
    
    /** Creates a new instance of FileCategoryUI */

    /* no longer used
    public FileCategoryUI(FileCategory fileCategory, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.fileCategory = fileCategory;
        initStudyFiles(vdc,user, ipUserGroup);

    }
    */
    
      /** Creates a new instance of FileCategoryUI */
    public FileCategoryUI(FileCategory fileCategory) {
        this.fileCategory = fileCategory;

    }

    /** Creates a new instance of FileCategoryUI */
    public FileCategoryUI(String category) {
        this.fileCategory = new FileCategory();
        this.fileCategory.setName(category);

    }
    /* no longer used!!!!!
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
    */
    
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
 
    public int compareTo(Object obj) {
        FileCategoryUI catUI = (FileCategoryUI)obj;
        return this.getFileCategory().compareTo( catUI.getFileCategory() );
        
    }    
}
