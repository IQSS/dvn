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
