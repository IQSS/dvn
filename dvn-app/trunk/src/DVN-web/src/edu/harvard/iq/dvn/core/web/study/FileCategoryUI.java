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
 * FileCategoryUI.java
 *
 * Created on September 28, 2006, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.util.AlphaNumericComparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.event.ActionEvent;

/**
 *
 * @author gdurand
 */

/*
 * This class was originally written to wrap around the FileCategory class. We have since removed that class, however
 * we still display files in the StudyFilesFragment per Category, so this helper class is still useful to represent a
 * category.
 */
public class FileCategoryUI implements Comparable, java.io.Serializable   {
    
    private String category;
    private List<StudyFileUI> studyFileUIs = new ArrayList();
    private boolean rendered=true;
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();
    


    /** Creates a new instance of FileCategoryUI */
    public FileCategoryUI(String category) {
        this.category = category;
    }

    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<StudyFileUI> getStudyFileUIs() {
        return studyFileUIs;
    }

    public void setStudyFileUIs(List<StudyFileUI> studyFileUIs) {
        this.studyFileUIs = studyFileUIs;
    }

    public boolean isRendered() {
        return rendered;
    }

    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    
    public void toggleRendered (ActionEvent ae) {
        setRendered(!isRendered());
    }




    public String getDownloadName() {
        if ( category == null || category.trim().equals("") ) {
            return "defaultCategory";
        }
        
        String downloadName = category.trim();
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
    
 
    public int compareTo(Object obj) {
        FileCategoryUI catUI = (FileCategoryUI)obj;
        return alphaNumericComparator.compare(this.category, catUI.category);
    }    
}
