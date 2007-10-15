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

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.admin.UserGroup;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.DataFileFormatType;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyFileUI {
    
    /** Creates a new instance of StudyFileUI */
    public StudyFileUI() {
    }
    
    public StudyFileUI(StudyFile studyFile, VDC vdc, VDCUser user, UserGroup ipUserGroup) {
        this.studyFile=studyFile;
        this.restrictedForUser = studyFile.isFileRestrictedForUser(user,vdc, ipUserGroup);
        this.vdcId = vdc != null ? vdc.getId() : null;
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
    
     public String fileDownload_action () {
        try {
            //get the xff arg used for web stats text report
            WebStatisticsSupport webstatistics = new WebStatisticsSupport();
            int headerValue = webstatistics.getParameterFromHeader("X-Forwarded-For");
            String xff = webstatistics.getQSArgument("xff", headerValue);
            String fileDownloadURL = "/dvn/FileDownload/" + "?fileId=" + this.studyFile.getId() + xff;
            if (!StringUtil.isEmpty(format)) {
                if ( format.equals(DataFileFormatType.ORIGINAL_FILE_DATA_FILE_FORMAT) ) {
                    fileDownloadURL += "&downloadOriginalFormat=true";    
                } else {
                    fileDownloadURL += "&format=" + this.format;
                }
            }
            if (vdcId != null) {
                fileDownloadURL += "&vdcId=" + this.vdcId;
            }            

            FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse response = (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
            response.sendRedirect(fileDownloadURL);
            fc.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        
        return null;
     }  
}
