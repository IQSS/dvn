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
 * StudyPage.java
 *
 * Created on September 19, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.servlet.TermsOfUseFilter;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

/**
 *
 * @author Gustavo Durand
 */
public class TermsOfUsePage extends VDCBaseBean {
    @EJB  private StudyServiceLocal studyService;
    @EJB private VDCNetworkServiceLocal vdcNetworkService;
    
    public TermsOfUsePage() {}
    
  
    private Long studyId;
    private edu.harvard.iq.dvn.core.study.Study study;
    private String redirectPage;
    private String touParam;  // Describes type of terms of use to be displayed (download or deposit)
    private HtmlInputHidden hiddenTou;
    private boolean downloadDataverseTermsRequired;
    private boolean downloadDvnTermsRequired;
    private boolean downloadStudyTermsRequired;
    private boolean depositDataverseTermsRequired;
    private boolean depositDvnTermsRequired;

    public HtmlInputHidden getHiddenTou() {
        return hiddenTou;
    }

    public void setHiddenTou(HtmlInputHidden hiddenTou) {
        this.hiddenTou = hiddenTou;
    }
    
    
    public String getTouParam() {
        return touParam;
    }
   public void setTouParam(String tou) {
         this.touParam=tou;
    }

    public boolean isTouTypeDownload() {
        return getTouType()!=null && getTouType().equals(TermsOfUseFilter.TOU_DOWNLOAD);
    }
    
    public boolean isTouTypeDeposit() {
        return getTouType()!=null && getTouType().equals(TermsOfUseFilter.TOU_DEPOSIT);
    }
    
    private String getTouType() {
        String type=null;
        if (touParam!=null) {
            type = touParam;
        } else  {
            type = getRequestParam("form1:tou");
        }
        return type;
    }
    
    public edu.harvard.iq.dvn.core.study.Study getStudy() {
        return study;
    }

    public void setStudy(edu.harvard.iq.dvn.core.study.Study study) {
        
        this.study = study;
    }

    public Long getStudyId() {
        return this.studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
    
    public String getRedirectPage() {
        return redirectPage;
    }

    public void setRedirectPage(String redirectPage) {
        this.redirectPage = redirectPage;
    }

    public VDCNetwork getVdcNetwork() {
        return vdcNetworkService.find();
    }
    
    public void init() {
        super.init();
        // Set study - note that this is only necessary for touParam type DOWNLOAD
        try {
            studyId = new Long(getRequestParam("studyId"));
        } catch (NumberFormatException ex) {}
        
        if (studyId == null) {
            // now check specific JSF post parameters
            try {
                if ( isFromPage("TermsOfUsePage") ) {
                    studyId = new Long(getRequestParam("form1:studyId"));   
                } else if ( isFromPage("StudyPage") ) {
                    studyId = new Long(getRequestParam("form1:studyId"));
                } else { //check the requestBean; if coming from some other page
                    studyId = getVDCRequestBean().getStudyId();
                }
            } catch (NumberFormatException ex) {}
        }
        if (studyId != null) {
            study = studyService.getStudy(studyId);
        }

        setRequiredFlags();
      
    }       
    
    
    public String getDepositDataverseTerms() {
        String terms=null;
        VDC termsVDC = getDepositVDC();
        if (termsVDC!=null) {
            terms = termsVDC.getDepositTermsOfUse();
        }
        return terms;
    }
    
    public boolean isTermsAcceptanceRequired() {
        return isDownloadDataverseTermsRequired() || isDownloadStudyTermsRequired();
    }
    
    public void setTermsAcceptanceRequired(boolean termsAcceptanceRequired) {} // dummy method since the get is just a wrapper
    
    public boolean isDownloadDataverseTermsRequired() {
        return downloadDataverseTermsRequired;
    }

    public boolean isDownloadStudyTermsRequired() {
        return  downloadStudyTermsRequired;
    }     
    
    public boolean isDownloadDvnTermsRequired() {
        return   downloadDvnTermsRequired; 
    }
    
    public boolean isDepositDataverseTermsRequired() {
        return  depositDataverseTermsRequired;
    }
    
    public boolean isDepositDvnTermsRequired() {
        return   depositDvnTermsRequired; 
    }
 
    private boolean termsAccepted;
 
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }    
    
    public String acceptTerms_action () {
        Map termsOfUseMap = getTermsOfUseMap();
        
        /*
        if ( studyTermsAccepted )  {         
            termsOfUseMap.put( "study_" + study.getId(), "accepted" );
        }
        if ( vdcTermsAccepted ) { 
            termsOfUseMap.put( "vdc_" + study.getOwner().getId(), "accepted" );
        }
        */

        if ( termsAccepted && isTouTypeDownload() && isDownloadStudyTermsRequired() )  {         
            termsOfUseMap.put( "study_download_" + study.getId(), "accepted" );
        }
        if ( termsAccepted &&  isTouTypeDownload() && isDownloadDataverseTermsRequired() ) { 
            termsOfUseMap.put( "vdc_download_" + study.getOwner().getId(), "accepted" );
        }        
        if ( termsAccepted &&  isTouTypeDownload() && this.isDownloadDvnTermsRequired() ) { 
            termsOfUseMap.put( "dvn_download", "accepted" );
        }           
        if ( termsAccepted &&  isTouTypeDeposit() && this.isDepositDataverseTermsRequired() ) { 
            termsOfUseMap.put( "vdc_deposit_"+getDepositVDC().getId(), "accepted" );
        }      
        if ( termsAccepted &&  isTouTypeDeposit() && this.isDepositDvnTermsRequired() ) { 
            termsOfUseMap.put( "dvn_deposit", "accepted" );
        }      
        if (redirectPage != null) {
            // piggy back on the login redirect logic for now
            String loginRedirect = this.getExternalContext().getRequestContextPath() + getVDCRequestBean().getCurrentVDCURL() + redirectPage;
            getSessionMap().put("LOGIN_REDIRECT", loginRedirect);
            
            // we don't actually want to go to the home page, but we need to fake JSF,
            // so that the redirect happens
            return "home";            
        }
        
        return null;
    }    

    private Map getTermsOfUseMap() {
        if (getVDCSessionBean().getLoginBean() != null) {
            return getVDCSessionBean().getLoginBean().getTermsfUseMap();
        } else {
            return getVDCSessionBean().getTermsfUseMap();
        }        
    }
    
    
    public void validateTermsAccepted(FacesContext context,
            UIComponent toValidate,
            Object value) {

        Boolean acceptedValue = (Boolean) value;
        if (acceptedValue.booleanValue() == false) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("You must accept the terms of use to continue.");
            context.addMessage(toValidate.getClientId(context), message);
        }

    }
    
    private void setRequiredFlags() {
        if (isTouTypeDeposit()) {
            depositDataverseTermsRequired = TermsOfUseFilter.isDepositDataverseTermsRequired(getDepositVDC(), getTermsOfUseMap());
            depositDvnTermsRequired = TermsOfUseFilter.isDepositDvnTermsRequired(vdcNetworkService.find(), getTermsOfUseMap());
        }

        if (isTouTypeDownload()) {
            if (study.getReleasedVersion() != null) {
                downloadStudyTermsRequired = TermsOfUseFilter.isDownloadStudyTermsRequired(study, getTermsOfUseMap());
                downloadDataverseTermsRequired = TermsOfUseFilter.isDownloadDataverseTermsRequired(study, getTermsOfUseMap());
                downloadDvnTermsRequired = TermsOfUseFilter.isDownloadDvnTermsRequired(vdcNetworkService.find(), getTermsOfUseMap());
            }
        }
    }
    
    
    private VDC getDepositVDC() {          
        if (study!=null) {
            return study.getOwner();
        } else {
            return getVDCRequestBean().getCurrentVDC();
        }
    }

}
