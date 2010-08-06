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
 * StudyFileUI.java
 *
 * Created on January 25, 2007, 2:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.Resource.Options;
import com.icesoft.faces.context.effects.JavascriptContext;
import edu.harvard.iq.dvn.core.admin.UserGroup;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.util.WebStatisticsSupport;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFileUI implements java.io.Serializable {
    public enum DownloadFormat {FIXED_FIELD, TAB_DELIMITED, ORIGINAL_FILE, SPLUS, STATA, R, GRAPHML };
    /** Creates a new instance of StudyFileUI */
    public StudyFileUI() {
    }

    public StudyFileUI(FileMetadata fmd, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.fileMetadata = fmd;
        this.restrictedForUser = fileMetadata.getStudyFile().isFileRestrictedForUser(user, vdc, ipUserGroup);
        this.vdcId = vdc != null ? vdc.getId() : null;
    }
    /**
     * Holds value of property studyFile.
     */
  
    private FileMetadata fileMetadata;

    public StudyFile getStudyFile() {
        return this.fileMetadata.getStudyFile();
    }

    
    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }



    /**
     * Holds value of property restrictedForUser.
     */
    private boolean restrictedForUser;

    /**
     * Getter for property restrictedForUser.
     * @return Value of property restrictedForUser.
     */
    public boolean isRestrictedForUser() {
        return this.restrictedForUser;
    }

    /**
     * Setter for property restrictedForUser.
     * @param restrictedForUser New value of property restrictedForUser.
     */
    public void setRestrictedForUser(boolean restrictedForUser) {
        this.restrictedForUser = restrictedForUser;
    }    
    
    // variables used in download
    private Long vdcId;

    public Long getVdcId() {
        return vdcId;
    }

    public void setVdcId(Long vdcId) {
        this.vdcId = vdcId;
    }
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isImage() {

        if (getStudyFile().getFileType() != null && getStudyFile().getFileType().length() >= 6 &&
                getStudyFile().getFileType().substring(0, 6).equalsIgnoreCase("image/")) {
            return true;
        } else {
            return false;
        }
    }

    public String getUserFriendlyFileType() {
        return FileUtil.getUserFriendlyFileType(getStudyFile());
    }

   /* public String fileDownload_action() {
        try {
            String fileDownloadURL = getFileDownloadURL();

            if (!StringUtil.isEmpty(format)) {
                if (format.equals(DataFileFormatType.ORIGINAL_FILE_DATA_FILE_FORMAT)) {
                    fileDownloadURL += "&downloadOriginalFormat=true";
                } else {
                    fileDownloadURL += "&format=" + this.format;
                }
            }

            FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
            response.sendRedirect(fileDownloadURL);
            fc.responseComplete();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return "";
       
    }
 
    public String getFileDownloadURL() {
        
        String fileDownloadURL = "/dvn/FileDownload/" + "?fileId=" + this.getStudyFile().getId();
        if (vdcId != null) {
            fileDownloadURL += "&vdcId=" + this.vdcId;
        }
  
        //get the xff arg used for web stats text report
        WebStatisticsSupport webstatistics = new WebStatisticsSupport();
        int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
        String xff = webstatistics.getQSArgument("xff", headerValue);
        fileDownloadURL  += xff;
         
         return fileDownloadURL;
    }
     */
    
/*
    public List<DataFileFormatType> getDataFileFormatTypes() {

        List dataFileFormatTypes = new ArrayList();
        if (isTabularDataFile()) {

            String tabDelimitedValue = "";

            // first check for fixed field
            if ("text/x-fixed-field".equals(getStudyFile().getFileType())) {
                DataFileFormatType fixedFileType = new DataFileFormatType();
                fixedFileType.setName("Fixed-Field");
                fixedFileType.setValue("");
                dataFileFormatTypes.add(fixedFileType);

                tabDelimitedValue = "D00";
            }

            // now add tab delimited
            DataFileFormatType tabDelimitedType = new DataFileFormatType();
            tabDelimitedType.setName("Tab Delimited");
            tabDelimitedType.setValue(tabDelimitedValue);
            dataFileFormatTypes.add(tabDelimitedType);

            // and original file
            if ( !StringUtil.isEmpty( getStudyFile().getOriginalFileType() ) ) {
                DataFileFormatType originalFileType = new DataFileFormatType();
                originalFileType.setName("Original File");
                originalFileType.setValue(DataFileFormatType.ORIGINAL_FILE_DATA_FILE_FORMAT);
                dataFileFormatTypes.add(originalFileType);
            }

            // finally, add types from db
            StudyServiceLocal studyService = null;
            try {
                studyService = (StudyServiceLocal) new InitialContext().lookup("java:comp/env/studyService");
            } catch (Exception e) {
                e.printStackTrace();
            }

            dataFileFormatTypes.addAll(studyService.getDataFileFormatTypes());

        } else if(isNetworkDataFile()) {
            // now add tab delimited
            DataFileFormatType tabDelimitedType = new DataFileFormatType();
            tabDelimitedType.setName("GraphML");
            tabDelimitedType.setValue("");
            dataFileFormatTypes.add(tabDelimitedType);

            // and original file
            if ( !StringUtil.isEmpty( getStudyFile().getOriginalFileType() ) ) {
                DataFileFormatType originalFileType = new DataFileFormatType();
                originalFileType.setName("Original File");
                originalFileType.setValue(DataFileFormatType.ORIGINAL_FILE_DATA_FILE_FORMAT);
                dataFileFormatTypes.add(originalFileType);
            }

            // TODO: need to RData as download
        }
        return dataFileFormatTypes;
    }
    */
/*
    public List getDataFileFormatTypeSelectItems() {
        List selectItems = new ArrayList();
        for (DataFileFormatType formatType : getDataFileFormatTypes()) {
            String value = getFileDownloadURL();
            if ( !StringUtil.isEmpty( formatType.getValue() ) ) {
                if ( formatType.isOriginalFileDataFileFormat() ) {
                    value += "&downloadOriginalFormat=true";
                } else {
                    value += "&format=" + formatType.getValue();
                }
            }
            selectItems.add( new SelectItem(value,formatType.getName()) );
        }

        return selectItems;
    }
    */
   public List getDataFileFormatTypeSelectItems() {
        String origFileType = fileMetadata.getStudyFile().getOriginalFileType();
        List selectItems = new ArrayList();
        if ( !StringUtil.isEmpty( getStudyFile().getOriginalFileType())){
            // If originalFileType is not empty, that means the original file
            // is available for download 
            selectItems.add(new SelectItem(DownloadFormat.ORIGINAL_FILE,"Original File"));
        }
        if (fileMetadata.getStudyFile() instanceof TabularDataFile) {
            if (origFileType.equals("text/x-fixed-field")) {
                selectItems.add(new SelectItem(DownloadFormat.FIXED_FIELD,"Fixed Field"));
            }
            selectItems.add(new SelectItem(DownloadFormat.TAB_DELIMITED,"Tab Delimited"));
            selectItems.add(new SelectItem(DownloadFormat.SPLUS,"Splus"));
            selectItems.add(new SelectItem(DownloadFormat.STATA,"Stata"));
            selectItems.add(new SelectItem(DownloadFormat.R,"R"));
        }
        else if (fileMetadata.getStudyFile() instanceof NetworkDataFile ) {
            selectItems.add(new SelectItem(DownloadFormat.GRAPHML,"GraphML"));
        }
        return selectItems;
    }


    public String getTxtFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "txt");
    }
    
    public String getTabFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "tab");
    }

    public String getZipFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "zip");
    }


    public String getSplusFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "ssc");
    }

    public String getStataFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "dta");
    }

    public String getRdataFileName() {
        return FileUtil.replaceExtension(this.fileMetadata.getLabel(), "RData");
    }

    public String getOrigFileName() {
        String origFileType = fileMetadata.getStudyFile().getOriginalFileType();
        String origFileName = fileMetadata.getLabel();
        String origExtension = "";

        // For the following file types, the label has an extension of "tab", so
        // we must replace the "tab" with the original extension.
        // For other types, just return the label.
        if (origFileType == null) {
            return null;
        } else if (origFileType.equalsIgnoreCase("application/x-spss-sav")) {
            origExtension= "sav";
        } else if (origFileType.equalsIgnoreCase("application/x-spss-por")) {
            origExtension= "por";
        } else if (origFileType.equalsIgnoreCase("application/x-stata")) {
            origExtension= "dta";
        }
        if (!origExtension.equals("")) {
            origFileName= FileUtil.replaceExtension(fileMetadata.getLabel(), origExtension);
        }
        return origFileName;
    }

    private String selectedDownload;

    public String getSelectedDownload() {
        return selectedDownload;
    }

    public void setSelectedDownload(String selectedDownload) {
        
        this.selectedDownload = selectedDownload;
        if (!StringUtil.isEmpty(selectedDownload)) {
            JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),"resetZipOutputResourceDisable();");
            notifyAll();
            
        }
           
    }

 /**
     * web statistics related
     * argument and methods
     *
     * @author wbossons
     */
    private String xff;

    public String getXff() {
        if (this.xff == null) {
            WebStatisticsSupport webstatistics = new WebStatisticsSupport();
            int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
            setXFF(webstatistics.getQSArgument("xff", headerValue));
    }
        return this.xff;
    }

    public void setXFF(String xff) {
        this.xff = xff;
    }
    
    public RFileResource getResource() {
        return new RFileResource(this, getXff());
    }

    static class RFileResource implements Resource, Serializable{
        File file;
        String xff;
        StudyFileUI studyFileUI;



        public RFileResource( StudyFileUI studyFileUI, String xff) {
            this.studyFileUI = studyFileUI;


        }

        public String calculateDigest() {
            return file != null ? file.getPath() : null;
        }

        public Date lastModified() {
            return file != null ? new Date(file.lastModified()) : null;
        }


        public InputStream open() throws IOException {
            String fileDownloadURL = "http://" + PropertyUtil.getHostUrl() + "/dvn" /*+ currentVDCURL */ + "/FileDownload/";
            if (studyFileUI.getStudyFile().isSubsettable() ) {
            // If necessary, wait for these fields to be filled by the icefaces partial submit - called by Javascript in the StudyPage
            while (StringUtil.isEmpty(studyFileUI.getSelectedDownload())) {
                try {
                    wait();
                } catch (Exception e) {
                }
            }

            String formatParam = "";
            StudyFileUI.DownloadFormat format = StudyFileUI.DownloadFormat.valueOf(studyFileUI.getSelectedDownload());
            switch (format) {
                case TAB_DELIMITED:
                    formatParam = "&format=D00";
                    break;
                case ORIGINAL_FILE:
                    formatParam = "&downloadOriginalFormat=true";
                    break;
                case SPLUS:
                    formatParam = "&format=D02";
                    break;
                case STATA:
                    formatParam = "&format=D03";
                    break;
                case R:
                    formatParam = "&format=D04";
                    break;
                // No formatParam needed for  GRAPHML or FIXED_FIELD, because no conversion is done.
            }
            fileDownloadURL += "?fileId=" + studyFileUI.getStudyFile().getId() + formatParam + studyFileUI.getXff();
           
        }
         else {
           // Non Subsettable file
           fileDownloadURL += "?fileId=" + studyFileUI.getStudyFile().getId() + "&versionNumber=" + studyFileUI.fileMetadata.getStudyVersion().getVersionNumber() + studyFileUI.getXff();
            
        }
           //  System.out.println("..........OPENING STREAM: " + fileDownloadURL);
            return new URL(fileDownloadURL).openStream();
        }
        public void withOptions(Options arg0) throws IOException {
        }
    }

    public String getFileSize() {
        File pFile = new File (getStudyFile().getFileSystemLocation());
        return FileUtils.byteCountToDisplaySize( pFile.length() );
        //return String.valueOf( pFile.length() );
    }
    
    public int getDownloadCount() {
        return getStudyFile().getStudyFileActivity() != null ? getStudyFile().getStudyFileActivity().getDownloadCount() : 0;
    }

    public boolean isTabularDataFile() {
        return getStudyFile() instanceof TabularDataFile;
    }

    public boolean isNetworkDataFile() {
        return getStudyFile() instanceof NetworkDataFile;
    }
}
