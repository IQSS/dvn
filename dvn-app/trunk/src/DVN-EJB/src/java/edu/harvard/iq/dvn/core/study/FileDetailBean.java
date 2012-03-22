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
