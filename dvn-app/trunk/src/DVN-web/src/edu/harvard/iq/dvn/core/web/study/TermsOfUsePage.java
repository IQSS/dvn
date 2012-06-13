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
 * StudyPage.java
 *
 * Created on September 19, 2006, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import edu.harvard.iq.dvn.core.web.servlet.TermsOfUseFilter;
import edu.harvard.iq.dvn.core.web.util.CharacterValidator;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author Gustavo Durand
 */
@Named("TermsOfUsePage")
@ViewScoped
public class TermsOfUsePage extends VDCBaseBean {
    @EJB private StudyServiceLocal studyService;
    @EJB private VDCNetworkServiceLocal vdcNetworkService;
    @EJB private GuestBookResponseServiceBean guestBookResponseServiceBean;
    @EJB StudyFileServiceLocal studyFileService;
    
    public TermsOfUsePage() {}
    
  
    private Long studyId;
    private String fileId;
    private edu.harvard.iq.dvn.core.study.Study study;
    private String redirectPage;
    private String touParam;  // Describes type of terms of use to be displayed (download or deposit)
    private HtmlInputHidden hiddenTou;
    private boolean downloadDataverseTermsRequired;
    private boolean downloadDvnTermsRequired;
    private boolean downloadStudyTermsRequired;
    private boolean depositDataverseTermsRequired;
    private boolean depositDvnTermsRequired;
    private boolean guestbookRequired;
    private GuestBookResponse guestBookResponse;
    private List <CustomQuestionResponseUI> customQuestionResponseUIs = new ArrayList();
    
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
    
    public boolean isTouTypeGuestBook() {
        return  getVDCRequestBean().getCurrentVDC().getGuestBookQuestionnaire().isEnabled();
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
        fileId = getRequestParam("fileId");
        setRequiredFlags();
        if (guestbookRequired){
            initGuestBookResponse ();
        }
      
    }   
    
    private void initGuestBookResponse (){
        guestBookResponse = new GuestBookResponse();
        guestBookResponse.setGuestBookQuestionnaire(study.getOwner().getGuestBookQuestionnaire());
        guestBookResponse.setStudy(study);
        guestBookResponse.setResponseTime(new Date());

        if (study.getOwner().getGuestBookQuestionnaire().getCustomQuestions() != null && !study.getOwner().getGuestBookQuestionnaire().getCustomQuestions().isEmpty()) {
            guestBookResponse.setCustomQuestionResponses(new ArrayList());
            customQuestionResponseUIs.clear();
            for (CustomQuestion cq : study.getOwner().getGuestBookQuestionnaire().getCustomQuestions()) {
                if (!cq.isHidden()){
                    CustomQuestionResponse response = new CustomQuestionResponse();
                    CustomQuestionResponseUI responseUI = new CustomQuestionResponseUI();
                    response.setGuestBookResponse(guestBookResponse);
                    response.setResponse("");
                    response.setCustomQuestion(cq);
                    responseUI.setCustomQuestionResponse(response);
                    responseUI.setRequired(cq.isRequired());
                    responseUI.setQuestionType(cq.getQuestionType());
                    if (cq.getQuestionType().equals("radiobuttons")){
                        responseUI.setResponseSelectItems(setResponseUISelectItems(cq));
                    }
                    customQuestionResponseUIs.add(responseUI);
                    guestBookResponse.getCustomQuestionResponses().add(response);                    
                }
            }
        }
        if (getVDCSessionBean().getLoginBean() != null) {
            guestBookResponse.setEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
            guestBookResponse.setFirstname(getVDCSessionBean().getLoginBean().getUser().getFirstName());
            guestBookResponse.setLastname(getVDCSessionBean().getLoginBean().getUser().getLastName());
            guestBookResponse.setInstitution(getVDCSessionBean().getLoginBean().getUser().getInstitution());
            guestBookResponse.setPosition(getVDCSessionBean().getLoginBean().getUser().getPosition());
            guestBookResponse.setVdcUser(getVDCSessionBean().getLoginBean().getUser());
        }
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
    
    public boolean isGuestbookRequired() {
        return guestbookRequired;
    }
    
    public boolean isGuestbookOnlyRequired() {
        return guestbookRequired && !(downloadDataverseTermsRequired || downloadStudyTermsRequired || downloadDvnTermsRequired  );
    }

    public void setGuestbookRequired(boolean guestbookRequired) {
        this.guestbookRequired = guestbookRequired;
    }
    
    public GuestBookResponse getGuestBookResponse() {
        return guestBookResponse;
    }

    public void setGuestBookResponse(GuestBookResponse guestBookResponse) {
        this.guestBookResponse = guestBookResponse;
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
    
    public List<CustomQuestionResponseUI> getCustomQuestionResponseUIs() {
        return customQuestionResponseUIs;
    }

    public void setCustomQuestionResponseUIs(List<CustomQuestionResponseUI> customQuestionResponseUIs) {
        this.customQuestionResponseUIs = customQuestionResponseUIs;
    }
    
    public String acceptTerms_action () {
        Map termsOfUseMap = getTermsOfUseMap();
        Map guestbookResponseMap = getGuestbookResponseMap();
        
        if (isGuestbookOnlyRequired()){
            termsAccepted = true;
        }
        /*
        if ( studyTermsAccepted )  {         
            termsOfUseMap.put( "study_" + study.getId(), "accepted" );
        }
        if ( vdcTermsAccepted ) { 
            termsOfUseMap.put( "vdc_" + study.getOwner().getId(), "accepted" );
        }
        */

        if (guestbookRequired) {
            if (!validateEntries()) {
                return "";
            }
            try { //if there are multiple files in the download
                // each one gets its own guest book response for reporting purposes
                
                if (fileId.indexOf(",") > -1) {
                    StringTokenizer st = new StringTokenizer(fileId, ",");
                    while (st.hasMoreTokens()) {
                        StudyFile file = studyFileService.getStudyFile(new Long(st.nextToken()));
                        GuestBookResponse gbSave = new GuestBookResponse(guestBookResponse);
                        gbSave.setStudyFile(file);
                        //Change to add to hashmap
                        guestbookResponseMap.put("guestBookResponse_" + file.getId(), guestBookResponse);
                                                    System.out.print("Add to map" + guestBookResponse );
                        //guestBookResponseServiceBean.update(gbSave);
                    }
                } else {
                    //only one file downloaded.....
                    Long fileLongId = new Long(fileId);
                    StudyFile file = studyFileService.getStudyFile(fileLongId);
                    guestBookResponse.setStudyFile(file);
                    //Change to add to hashmap
                    guestbookResponseMap.put("guestBookResponse_" + file.getStudy().getId(), guestBookResponse);
                    //guestBookResponseServiceBean.update(guestBookResponse);
                }

            } catch (Exception ex) {
                if (ex.getCause() instanceof IllegalArgumentException) {
                    // do nothing.
                    // if the file does not exist, there sure 
                    // isn't a license/terms of use for it!
                } else {
                    ex.printStackTrace();
                }
            }
        }

        
        if ( termsAccepted && isTouTypeDownload() && isDownloadStudyTermsRequired() )  {    
            termsOfUseMap.put( "study_download_" + study.getId(), "accepted" );
        }
        if ( termsAccepted &&  isTouTypeDownload() && isDownloadDataverseTermsRequired() ) { 
            termsOfUseMap.put( "vdc_download_" + study.getOwner().getId(), "accepted" );
        }        
        if ( termsAccepted &&  isTouTypeDownload() && this.isDownloadDvnTermsRequired() ) { 
            termsOfUseMap.put( "dvn_download", "accepted" );
        }
        if ( termsAccepted && this.isGuestbookRequired()) { 
            termsOfUseMap.put( "study_guestbook_"  + study.getId(), "accepted" );
        }        
        if ( termsAccepted &&  isTouTypeDeposit() && this.isDepositDataverseTermsRequired() ) { 
            termsOfUseMap.put( "vdc_deposit_"+getDepositVDC().getId(), "accepted" );
        }      
        if ( termsAccepted &&  isTouTypeDeposit() && this.isDepositDvnTermsRequired() ) { 
            termsOfUseMap.put( "dvn_deposit", "accepted" );
        }  
        if (redirectPage != null) {
            // piggy back on the login redirect logic for now
            String redirect = this.getExternalContext().getRequestContextPath() + getVDCRequestBean().getCurrentVDCURL() + redirectPage;

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);         
            } catch (IOException ex) {
                Logger.getLogger(TermsOfUsePage.class.getName()).log(Level.SEVERE, null, ex);
                throw new FacesException(ex);
            }
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
    
    private Map getGuestbookResponseMap(){
        return getVDCSessionBean().getGuestbookResponseMap();
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
                guestbookRequired =  TermsOfUseFilter.isGuestbookRequired(study, getTermsOfUseMap());
                downloadDvnTermsRequired = TermsOfUseFilter.isDownloadDvnTermsRequired(vdcNetworkService.find(), getTermsOfUseMap());
            }
        }
    }
    
    public void validateEmail(FacesContext context, 
                          UIComponent toValidate,
                          Object value) {
    CharacterValidator charactervalidator = new CharacterValidator();
    charactervalidator.validateWEmail(context, toValidate, value);
        }
    
    private List <SelectItem> setResponseUISelectItems(CustomQuestion cq){
        List  <SelectItem> retList = new ArrayList();
        for (CustomQuestionValue cqv: cq.getCustomQuestionValues()){
            SelectItem si = new SelectItem(cqv.getValueString(), cqv.getValueString());
            retList.add(si);
        }
        return retList;
    }
    
    private VDC getDepositVDC() {          
        if (study!=null) {
            return study.getOwner();
        } else {
            return getVDCRequestBean().getCurrentVDC();
        }
    }
    
    private boolean validateEntries() {
        boolean retval = true;

        if (study.getOwner().getGuestBookQuestionnaire().isFirstNameRequired() && guestBookResponse.getFirstname().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputFirstNameWarningMessage", "Please enter your first name.");
            retval = false;
        }
        if (study.getOwner().getGuestBookQuestionnaire().isLastNameRequired() && guestBookResponse.getLastname().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputLastNameWarningMessage", "Please enter your last name.");
            retval = false;
        }
        if (study.getOwner().getGuestBookQuestionnaire().isEmailRequired() && guestBookResponse.getEmail().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputEmailWarningMessage", "Please enter your email address.");
            retval = false;
        }
        if (study.getOwner().getGuestBookQuestionnaire().isInstitutionRequired() && guestBookResponse.getInstitution().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputInstitutionWarningMessage", "Please enter your institution.");
            retval = false;
        }
        if (study.getOwner().getGuestBookQuestionnaire().isPositionRequired() && guestBookResponse.getPosition().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputPositionWarningMessage", "Please enter your position.");
            retval = false;
        }
        Iterator iterator = customQuestionResponseUIs.iterator();
        while (iterator.hasNext()) {
            CustomQuestionResponseUI customQuestionResponseUI = (CustomQuestionResponseUI) iterator.next();
            if ((customQuestionResponseUI.getCustomQuestionResponse().getResponse() == null  || customQuestionResponseUI.getCustomQuestionResponse().getResponse().trim().isEmpty())
                    && customQuestionResponseUI.isRequired()) {
                retval = false;
                getVDCRenderBean().getFlash().put("inputCustomReponse", "Please complete required response(s).");
            }
        }
        return retval;
    }

}
