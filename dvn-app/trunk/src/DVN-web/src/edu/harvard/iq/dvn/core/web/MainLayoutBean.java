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
 * AllUsersDataBean.java
 *
 * Created on October 27, 2006, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Wendy Bosson
 */
public class MainLayoutBean implements java.io.Serializable  {
    
    /**
     * Creates a new instance of MainLayoutBean
     */
    public MainLayoutBean() {
    }
    
    private String googleAnalyticsCode;
    private String googleAnalyticsSourceCode;
    private String googleAnalyticsKey;


     /**
     * Getter for property googleAnalyticsCode.
     * @return Value of property googleAnalyticsCode.
     */
    public String getGoogleAnalyticsCode() {
        Map applicationmap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        if (applicationmap.containsKey("googleAnalyticsCode"))  {
                return (String) applicationmap.get("googleAnalyticsCode");
        } else {
            setGoogleAnalyticsCode(getGoogleAnalyticsKey());
            if (googleAnalyticsCode != null) {
                applicationmap.put("googleAnalyticsCode", googleAnalyticsCode);
            }
        }
        return this.googleAnalyticsCode;
    }

    public String getGoogleAnalyticsSourceCode() {
        Map applicationmap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        if (applicationmap.containsKey("googleAnalyticsSourceCode"))  {
                return (String) applicationmap.get("googleAnalyticsSourceCode");
        } else {
            setGoogleAnalyticsSourceCode();
            if (googleAnalyticsSourceCode != null) {
                applicationmap.put("googleAnalyticsSourceCode", googleAnalyticsSourceCode);
            }
        }
        return googleAnalyticsSourceCode;
    }


    
    /**
     * Getter for property googleAnalyticsKey.
     * @return Value of property googleAnalyticsKey.
     */
    public String getGoogleAnalyticsKey() {
        Map applicationmap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        if (applicationmap.containsKey("googleAnalyticsKey"))  {
                return (String) applicationmap.get("googleAnalyticsKey");
        } else {
            setGoogleAnalyticsKey(getJvmOption("googleanalytics"));
            if (googleAnalyticsKey != null) {
                applicationmap.put("googleAnalyticsKey", googleAnalyticsKey);
            }
        }
        return this.googleAnalyticsKey;
    }

    /**
     * Setters
     */
    public void setGoogleAnalyticsCode(String key) {
        String googleCode = new String("");
        googleCode += "            <script type=\"text/javascript\">\n\r";
        googleCode += "                // <![CDATA[  \n\r"; 
        googleCode += "            var pageTracker = _gat._getTracker(\"" + key + "\");\n\r";
        googleCode += "            pageTracker._initData();\n\r";
        googleCode += "            pageTracker._trackPageview();\n\r";
        googleCode += "            //  ]]>\n\r";
        googleCode += "            </script>\n\r";
        this.googleAnalyticsCode = googleCode;
    }

    public void setGoogleAnalyticsSourceCode() {
        String googleCode = new String("");
        googleCode += "         <script type=\"text/javascript\">\n\r";
        googleCode += "         // <![CDATA[  \n\r" ;
        googleCode += "            var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\n\r";
        googleCode += "            document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\n\r";
        googleCode += "            //  ]]>\n\r";
        googleCode += "            </script>\n\r";
        this.googleAnalyticsSourceCode = googleCode;
    }



    public void setGoogleAnalyticsKey(String key) {
        this.googleAnalyticsKey = key;
    }
    
    public String getJvmOption(String key) {
        String jvmoption = "dvn." + key + ".key";
        return System.getProperty(jvmoption);
    }


    protected boolean writeStudyVersionNotesPopups = false;

    /**
     * Get the value of writeStudyVersionNotesPopups
     *
     * @return the value of writeStudyVersionNotesPopups
     */
    public boolean isWriteStudyVersionNotesPopups() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        writeStudyVersionNotesPopups = request.getRequestURI().indexOf("/StudyPage.xhtml") != -1
                || request.getRequestURI().indexOf("/StudyVersionDifferencesPage.xhtml") != -1
                || request.getRequestURI().indexOf("/EditStudyPage.xhtml") != -1
                || request.getRequestURI().indexOf("/AddFilesPage.xhtml") != -1
                || request.getRequestURI().indexOf("/NetworkOptionsPage.xhtml") != -1
                || request.getRequestURI().indexOf("/ManageStudiesPage.xhtml") != -1;
        return writeStudyVersionNotesPopups;
    }

    /**
     * Set the value of writeStudyVersionNotesPopups
     *
     * @param writeStudyVersionNotesPopups new value of writeStudyVersionNotesPopups
     */
    public void setWriteStudyVersionNotesPopups(boolean writeStudyVersionNotesPopups) {
        this.writeStudyVersionNotesPopups = writeStudyVersionNotesPopups;
    }

    protected boolean writeStudyDeletePopups = false;

    public boolean isWriteStudyDeletePopups() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        writeStudyDeletePopups = request.getRequestURI().indexOf("StudyPage.xhtml") != -1;
        return writeStudyDeletePopups;
    }

    public void setWriteStudyDeletePopups(boolean writeStudyDeletePopups) {
        this.writeStudyDeletePopups = writeStudyDeletePopups;
    }


    // These methods below are for the xhtml fragments that can be loaded from
    // different pages to determine what page they are in.

    public boolean isInStudyPage() {
        String viewId = getCurrentViewId();
        if (viewId != null) {
            return viewId.indexOf("/StudyPage.xhtml") != -1;
        }
        return false;
    }

    public boolean isInEditStudyPage() {
        String viewId = getCurrentViewId();
        if (viewId != null) {
            return viewId.indexOf("/EditStudyPage.xhtml") != -1;
        }
        return false;
    }

    public boolean isInAddFilesPage() {
        String viewId = getCurrentViewId();
        if (viewId != null) {
            return viewId.indexOf("/AddFilesPage.xhtml") != -1;
        }
        return false;
    }

    public boolean isInVersionDiffPage() {
        String viewId = getCurrentViewId();
        if (viewId != null) {
            return viewId.indexOf("/StudyVersionDifferencesPage.xhtml") != -1;
        }
        return false;
    }

    public boolean isInManageStudiesPage() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getRequestURI().indexOf("/ManageStudiesPage.xhtml") != -1;
    }

    public boolean isInManageStudiesPageByViewID() {
        String viewId = getCurrentViewId();
        if (viewId != null) {
            return viewId.indexOf("/ManageStudiesPage.xhtml") != -1;
        }
        return false;
    }


    private String getCurrentViewId() {
        UIViewRoot uiViewRoot = FacesContext.getCurrentInstance().getViewRoot();
        if (uiViewRoot != null) {
            return uiViewRoot.getViewId();
        }
        return null;
    }
    private boolean showVersionNotesPopup;
    public boolean isShowVersionNotesPopup(){
        return this.showVersionNotesPopup;
    }

    /**
     * @param showVersionNotesPopup the showVersionNotesPopup to set
     */
    public void setShowVersionNotesPopup(boolean showVersionNotesPopup) {
        this.showVersionNotesPopup = showVersionNotesPopup;
    }

    private boolean showVersionNotesRelease;

    /**
     * @return the showVersionNotesRelease
     */
    public boolean isShowVersionNotesRelease() {
        return showVersionNotesRelease;
    }

    /**
     * @param showVersionNotesReleaseButton the showVersionNotesRelease to set
     */
    public void setShowVersionNotesRelease(boolean showVersionNotesRelease) {
        this.showVersionNotesRelease = showVersionNotesRelease;
    }
}
