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
 * StudyFileEditBean.java
 *
 * Created on October 10, 2006, 5:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.FileUtil;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;



import java.util.logging.*;
import javax.ejb.EJBException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author gdurand
 */
public class StudyFileEditBean implements Serializable {

    private static Logger dbgLog = Logger.getLogger(StudyFileEditBean.class.getCanonicalName());

    /** Creates a new instance of StudyFileEditBean */
    public StudyFileEditBean(StudyFile sf) {
        this.studyFile = sf;

    }

    /** Creates a new instance of StudyFileEditBean */
    public StudyFileEditBean(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
        this.studyFile = fileMetadata.getStudyFile();

    }

    public StudyFileEditBean(File file, String fileSystemName, Study study) throws IOException {
        dbgLog.fine("***** within StudyFileEditBean: constructor: start *****");
        dbgLog.fine("reached before studyFile constructor");

        String fileType = FileUtil.determineFileType(file);
        dbgLog.fine("return from FileUtil.determineFileType(file), fileType="+fileType);

        if (fileType.equals("application/x-stata") ||
            fileType.equals("application/x-spss-por") ||
            fileType.equals("application/x-spss-sav") ) {
            dbgLog.fine("tablularFile");
            this.studyFile = new TabularDataFile(); // do not yet attach to study, as it has to be ingested
        } else if (fileType.equals("text/xml-graphml")) {
            dbgLog.fine("isGraphMLFile = true");
            this.studyFile = new NetworkDataFile();
        } else    {
            this.studyFile = new OtherFile(study);
        }

        fileMetadata = new FileMetadata();
        fileMetadata.setStudyFile(studyFile);


        this.getStudyFile().setFileType( fileType );

        dbgLog.fine("reached before original filename");
        this.setOriginalFileName(file.getName());

       
        this.getStudyFile().setFileType(fileType);
        dbgLog.fine("after setFileType");

        // not yet supported as subsettable
        //this.getStudyFile().getFileType().equals("application/x-rlang-transport") );
        dbgLog.fine("before setFileName");
        // replace extension with ".tab" if this we are going to convert this to a tabular data file
        fileMetadata.setLabel(this.getStudyFile() instanceof TabularDataFile ? FileUtil.replaceExtension(this.getOriginalFileName()) : this.getOriginalFileName());
        dbgLog.fine("before tempsystemfilelocation");
        this.setTempSystemFileLocation(file.getAbsolutePath());
        this.getStudyFile().setFileSystemName(fileSystemName);
        dbgLog.fine("StudyFileEditBean: contents:\n" + this.toString());

        dbgLog.fine("***** within StudyFileEditBean: constructor: end *****");

    }

    public StudyFileEditBean(File file, String fileSystemName, Study study, String controlCardTempLocation, String controlCardType) throws IOException {
        dbgLog.fine("***** within StudyFileEditBean: control card constructor: start *****");
        dbgLog.fine("reached before studyFile constructor");

        String fileType = FileUtil.determineFileType(file);
        dbgLog.fine("return from FileUtil.determineFileType(file), fileType="+fileType);

        this.studyFile = new TabularDataFile(); // do not yet attach to study, as it has to be ingested

        fileMetadata = new FileMetadata();
        fileMetadata.setStudyFile(studyFile);


        this.getStudyFile().setFileType( fileType );

        dbgLog.fine("reached before original filename");
        this.setOriginalFileName(file.getName());


        this.getStudyFile().setFileType(fileType);
        dbgLog.fine("after setFileType");
        dbgLog.fine("before setFileName");
        // replace extension with ".tab" if we are going to convert this to a tabular data file
        fileMetadata.setLabel(FileUtil.replaceExtension(this.getOriginalFileName()));
        dbgLog.fine("before tempsystemfilelocation");
        this.setTempSystemFileLocation(file.getAbsolutePath());
        this.setControlCardSystemFileLocation(controlCardTempLocation);
        if (controlCardType != null && !controlCardType.equals("")) {
            this.setControlCardType(controlCardType);
        }

        this.getStudyFile().setFileSystemName(fileSystemName);
        dbgLog.fine("StudyFileEditBean: contents:\n" + this.toString());

        dbgLog.fine("***** within StudyFileEditBean: constructor: end *****");

    }

    private FileMetadata fileMetadata;
    private StudyFile studyFile;
    private String originalFileName;
    private String tempSystemFileLocation;
    private String controlCardSystemFileLocation;
    private String controlCardType;
    private String rawDataTempSystemFileLocation;
    private String ingestedSystemFileLocation;
    private boolean deleteFlag;
    private Long sizeFormatted = null;

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }




    //end of EV additions
    public StudyFile getStudyFile() {
        return studyFile;
    }

    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
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

    public String getControlCardSystemFileLocation() {
        return controlCardSystemFileLocation;
    }

    public String getControlCardFileName() {
        if (controlCardSystemFileLocation != null
                && !controlCardSystemFileLocation.equals("")) {
            return (new File(controlCardSystemFileLocation)).getName(); 
        }

        return null;
    }

    public void setControlCardSystemFileLocation(String controlCardSystemFileLocation) {
        this.controlCardSystemFileLocation = controlCardSystemFileLocation;
    }

    public String getControlCardType() {
        return controlCardType;
    }

    public void setControlCardType(String cct) {
        this.controlCardType = cct;
    }
    public String getRawDataTempSystemFileLocation() {
        return rawDataTempSystemFileLocation;
    }

    public void setRawDataTempSystemFileLocation(String rawDataTempSystemFileLocation) {
        this.rawDataTempSystemFileLocation = rawDataTempSystemFileLocation;
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

    public void addFiletoStudy(Study s) {
        StudyFile file = this.getStudyFile();
        file.setStudy(s);
        s.getStudyFiles().add(file);
        
        //addFileToCategory(s);

        // also create study file activity object
        StudyFileActivity sfActivity = new StudyFileActivity();
        file.setStudyFileActivity(sfActivity);
        sfActivity.setStudyFile(file);
        sfActivity.setStudy(s);
    }


    public String getUserFriendlyFileType() {
        return FileUtil.getUserFriendlyFileType(studyFile);
    }
    //EV added for compatibility with icefaces

    /**
     * Method to return the file size as a formatted string
     * For example, 4000 bytes would be returned as 4kb
     *
     *@return formatted file size
     */
    public Long getSizeFormatted() {

        return sizeFormatted;
    }

    public void setSizeFormatted(Long s) {
        sizeFormatted = s;
    }

 
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
}
