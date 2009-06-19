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
        } else if (fileType.equals("text/xml") && isGraphMLFile(file)) {
            dbgLog.fine("isGraphMLFile = true");
            this.studyFile = new NetworkDataFile();
        } else    {
            this.studyFile = new OtherFile(study);
        }


        this.getStudyFile().setFileType( fileType );

        dbgLog.fine("reached before original filename");
        this.setOriginalFileName(file.getName());

        dbgLog.fine("originalFileName=" + originalFileName);

        this.getStudyFile().setFileType(FileUtil.determineFileType(file));
        dbgLog.fine("after setFileType");

        // not yet supported as subsettable
        //this.getStudyFile().getFileType().equals("application/x-rlang-transport") );
        dbgLog.fine("before setFileName");
        // replace extension with ".tab" if this we are going to convert this to a tabular data file
        this.getStudyFile().setFileName(this.getStudyFile() instanceof TabularDataFile ? FileUtil.replaceExtension(this.getOriginalFileName()) : this.getOriginalFileName());
        dbgLog.fine("before tempsystemfilelocation");
        this.setTempSystemFileLocation(file.getAbsolutePath());
        this.getStudyFile().setFileSystemName(fileSystemName);
        dbgLog.fine("StudyFileEditBean: contents:\n" + this.toString());

        dbgLog.fine("***** within StudyFileEditBean: constructor: end *****");

    }
    private StudyFile studyFile;
    private String fileCategoryName;
    private String originalFileName;
    private String tempSystemFileLocation;
    private String controlCardSystemFileLocation;
    private String ingestedSystemFileLocation;
    private boolean deleteFlag;
    private Long sizeFormatted = null;

    //end of EV additions
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
        this.fileCategoryName = fileCategoryName.trim();
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

    public void setControlCardSystemFileLocation(String controlCardSystemFileLocation) {
        this.controlCardSystemFileLocation = controlCardSystemFileLocation;
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
        
        addFileToCategory(s);

        // also create study file activity object
        StudyFileActivity sfActivity = new StudyFileActivity();
        file.setStudyFileActivity(sfActivity);
        sfActivity.setStudyFile(file);
        sfActivity.setStudy(s);
    }

    public void addFileToCategory(Study s) {
        StudyFile file = this.getStudyFile();
        String catName = this.getFileCategoryName() != null ? this.getFileCategoryName() : "";

        Iterator iter = s.getFileCategories().iterator();
        while (iter.hasNext()) {
            FileCategory cat = (FileCategory) iter.next();
            if (cat.getName().equals(catName)) {
                file.setFileCategory(cat);
                cat.getStudyFiles().add(file);
                return;
            }
        }

        // category was not found, so we create a new file category
        FileCategory cat = new FileCategory();
        cat.setStudy(s);
        s.getFileCategories().add(cat);
        cat.setName(catName);
        cat.setStudyFiles(new ArrayList());

        // link cat to file
        file.setFileCategory(cat);
        cat.getStudyFiles().add(file);
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

    private boolean isGraphMLFile(File file) {
        boolean isGraphML = false;
        try{
            FileReader fileReader = new FileReader(file);
            javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
            xmlif.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);
      
            XMLStreamReader xmlr = xmlif.createXMLStreamReader(fileReader);
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("graphml")) {
                        dbgLog.fine("schema = "+xmlr.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"));
                        // to determine file - this attribute should have value "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd"
                        int count = xmlr.getAttributeCount();
                        for (int i=0; i<count; i++) {
                            dbgLog.fine("attrib["+i+"]="+xmlr.getAttributeName(i));
                        }
                        String schema = xmlr.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                        if (schema!=null && schema.indexOf("http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd")!=-1){
                            isGraphML = true;
                            break;
                        }
                        break;
                    }
                } 
            }
        } catch(XMLStreamException e) {
            this.dbgLog.fine("XML error - this is not a valid graphML file.");
            isGraphML = false;
        } catch(IOException e) {
            throw new EJBException(e);
        }

        return isGraphML;
    }
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
}
