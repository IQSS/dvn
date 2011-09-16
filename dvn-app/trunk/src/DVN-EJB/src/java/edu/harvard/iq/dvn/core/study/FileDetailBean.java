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
 * FileDetailBean.java
 *
 * Created on November 3, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.util.Collection;

/**
 *
 * @author Ellen Kraffmiller
 */
public class FileDetailBean implements java.io.Serializable {
    
    /**
     * Creates a new instance of FileDetailBean
     */
    public FileDetailBean() {
    }

    /**
     * Holds value of property studyFile.
     */
    private StudyFile studyFile;

    /**
     * Getter for property studyFile.
     * @return Value of property studyFile.
     */
    public StudyFile getStudyFile() {
        return this.studyFile;
    }

    /**
     * Setter for property studyFile.
     * @param studyFile New value of property studyFile.
     */
    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }

    /**
     * Holds value of property checked.
     */
    private boolean checked;

    /**
     * Getter for property checked.
     * @return Value of property checked.
     */
    public boolean isChecked() {
        return this.checked;
    }

    /**
     * Setter for property checked.
     * @param checked New value of property checked.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    private boolean currentVersion;
    
    public boolean isCurrentVerstion(){
        return this.currentVersion;
    }
    
    public void setCurrentVersion(long currentVersion){
                 
        this.currentVersion = false;        
        for ( int i = 0; i < studyFile.getFileMetadatas().size(); i++) {         
            if (currentVersion == studyFile.getFileMetadatas().get(i).getStudyVersion().getVersionNumber().longValue()){
               this.currentVersion = true; 
            }
        }

    }

    /**
     * Holds value of property filePermissions.
     */
    private Collection<PermissionBean> filePermissions;

    /**
     * Getter for property filePermissions.
     * @return Value of property filePermissions.
     */
    public Collection<PermissionBean> getFilePermissions() {
        return this.filePermissions;
    }

    /**
     * Setter for property filePermissions.
     * @param filePermissions New value of property filePermissions.
     */
    public void setFilePermissions(Collection<PermissionBean> filePermissions) {
        this.filePermissions = filePermissions;
    }

    /**
     * Holds value of property fileRestriction.
     */
    private String fileRestriction;

    /**
     * Getter for property fileRestriction.
     * @return Value of property fileRestriction.
     */
    public String getFileRestriction() {
        return this.fileRestriction;
    }

    /**
     * Setter for property fileRestriction.
     * @param fileRestriction New value of property fileRestriction.
     */
    public void setFileRestriction(String fileRestriction) {
        this.fileRestriction = fileRestriction;
    }

    /**
     *
     * @return a String listing all the study versionNumbers that this file
     *  is associated with.
     */
    public String getFileVersions() {
        String versions = "";

        for ( int i = 0; i < studyFile.getFileMetadatas().size(); i++) {
            versions += studyFile.getFileMetadatas().get(i).getStudyVersion().getVersionNumber();
            if (i+1< studyFile.getFileMetadatas().size()) {
                versions +=", ";
            }
        }
        return versions;

    }
       
}
