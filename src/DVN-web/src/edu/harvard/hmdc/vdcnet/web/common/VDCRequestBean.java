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
 * VDCRequestBean.java
 *
 * Created on November 1, 2005, 8:42 AM
 */
package edu.harvard.hmdc.vdcnet.web.common;

import edu.harvard.hmdc.vdcnet.admin.PageDef;
import edu.harvard.hmdc.vdcnet.admin.PageDefServiceLocal;
import edu.harvard.hmdc.vdcnet.util.PropertyUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.web.StudyListing;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Request scope data bean for your application.  Create properties
 *  here to represent data that should be made available across different
 *  pages in the same HTTP request, so that the page bean classes do not
 *  have to be directly linked to each other.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */
public class VDCRequestBean extends VDCBaseBean implements java.io.Serializable  {
    @EJB VDCServiceLocal vdcService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;   
    @EJB PageDefServiceLocal pageDefService;
      
 

    /** 
     * <p>Construct a new request data bean instance.</p>
     */
    public VDCRequestBean() {  
    }

    public String getRequestParam(String name) {
        return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(name);
    }    
    
  
  


  


    /** 
     * <p>Return a reference to the scoped data bean.</p>
     */
    protected VDCSessionBean getVDCSessionBean() {
        return (VDCSessionBean)getBean("VDCSessionBean");
    }

    

    /**
     * Holds value of property studyId.
     */
    private Long studyId;

    /**
     * Getter for property editMode.
     * @return Value of property editMode.
     */
    public Long getStudyId() {
        return this.studyId;
    }

    /**
     * Setter for property editMode.
     * @param editMode New value of property editMode.
     */
    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    private String selectedTab;

    public String getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }    

    private boolean currentVDCinitialized;
    private VDC currentVDC;

    /**
     * Getter for property vdcId.
     * @return Value of property vdcId.
     */
    public VDC getCurrentVDC() {
        if (!currentVDCinitialized) {
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            setCurrentVDC( vdcService.getVDCFromRequest(req) );
            // set method also sets initialization boolean to true
        }        
        return currentVDC;
    }

    private Boolean logoutPage = null;

    public boolean isLogoutPage() {
        if (logoutPage == null) {
            HttpServletRequest httpRequest = (HttpServletRequest)this.getExternalContext().getRequest();
            PageDef pageDef = pageDefService.findByPath(httpRequest.getPathInfo());
            if (pageDef!=null && pageDef.getName().equals(PageDefServiceLocal.LOGOUT_PAGE)) {
                logoutPage = Boolean.TRUE;
            } else {
                logoutPage = Boolean.FALSE;
            }
        }

        return logoutPage.booleanValue();

    }    
   
    public void setCurrentVDC(VDC currentVDC) {
        this.currentVDC = currentVDC;
        currentVDCinitialized = true;
    }

    
    public Long getCurrentVDCId() {
        if (getCurrentVDC() != null) {
            return getCurrentVDC().getId();
        } 
        
        return null;
    }
    
    public void setCurrentVDCId(Long id) {}  // dummy method since the get is just a wrapper around currentVDC

    public String getCurrentVDCURL() {
        String dataverseURL="";
        if (getCurrentVDC() != null) { 
            dataverseURL +="/dv/"+getCurrentVDC().getAlias();
        } 
        // needed for error page when service is passed, but still an exception thrown in the app. wjb Sept 2007
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("dataverseURL", dataverseURL); 
        // end code for exception case
        return dataverseURL;
    }
    
    public void setCurrentVDCURL(String dataverseURL) {}  // dummy method since the get is just a wrapper 
   
    
    /**
     * Holds value of property vdcNetwork.
     */
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        if (vdcNetwork == null) {
            VDCNetwork network = vdcNetworkService.find(new Long(1));  // There is only one network, which will always have Id=1
            setVdcNetwork(network);              
        }

        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }
    
    private StudyListing studyListing;

    public StudyListing getStudyListing() {
        return studyListing;
    }

    public void setStudyListing(StudyListing studyListing) {
        this.studyListing = studyListing;
    }

    private Long dtId;
    
    public Long getDtId() {
        return dtId;
    }

    public void setDtId(Long dtId) {
        this.dtId = dtId;
    }
    
    private String dvFilter;

    public String getDvFilter() {
        return dvFilter;
    }

    public void setDvFilter(String dvFilter) {
        this.dvFilter = dvFilter;
    }
    
    public String getHostUrl() {
        return PropertyUtil.getHostUrl();
    }  
    
}
