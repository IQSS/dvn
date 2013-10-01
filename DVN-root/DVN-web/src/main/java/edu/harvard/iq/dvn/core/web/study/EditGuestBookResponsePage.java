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
 Version 3.1.
 */
package edu.harvard.iq.dvn.core.web.study;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("EditGuestBookResponsePage")
public class EditGuestBookResponsePage extends VDCBaseBean implements java.io.Serializable {

    @EJB
    GuestBookResponseServiceBean guestBookResponseServiceBean;
    @EJB
    private StudyServiceLocal studyService;
    @EJB
    private StudyFileServiceLocal studyFileService;
    private VDC vdc;
    private GuestBookQuestionnaire guestBookQuestionnaire;
    private GuestBookResponse guestBookResponse;
    private Long studyId;
    private Long studyFileId;
    
    private Study study;
    private StudyFile studyFile;

    private List <CustomQuestionResponseUI> customQuestionResponseUIs = new ArrayList();
    
    public void init() {
        super.init();
        vdc = getVDCRequestBean().getCurrentVDC();

        try {
            studyId = new Long(getRequestParam("studyId"));
        } catch (NumberFormatException ex) {
        }

        if (studyId == null) {
            try {
                if (isFromPage("TermsOfUsePage")) {
                    studyId = new Long(getRequestParam("form1:studyId"));
                } else if (isFromPage("StudyPage")) {
                    studyId = new Long(getRequestParam("form1:studyId"));
                } else { //check the requestBean; if coming from some other page
                    studyId = getVDCRequestBean().getStudyId();
                }
            } catch (NumberFormatException ex) {
            }
        }
        if (studyId != null) {
            study = studyService.getStudy(studyId);
        }

        studyFileId = new Long(getRequestParam("studyFileId"));
        studyFile = studyFileService.getStudyFile(studyFileId);
        guestBookQuestionnaire = getVDCRequestBean().getCurrentVDC().getGuestBookQuestionnaire();
        guestBookResponse = new GuestBookResponse();
        guestBookResponse.setGuestBookQuestionnaire(guestBookQuestionnaire);
        guestBookResponse.setStudy(study);
        guestBookResponse.setStudyFile(studyFile);
        guestBookResponse.setResponseTime(new Date());

        if (guestBookQuestionnaire.getCustomQuestions() != null && !guestBookQuestionnaire.getCustomQuestions().isEmpty()) {
            guestBookResponse.setCustomQuestionResponses(new ArrayList());
            customQuestionResponseUIs.clear();
            for (CustomQuestion cq : guestBookQuestionnaire.getCustomQuestions()) {
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
        if (getVDCSessionBean().getLoginBean() != null) {
            guestBookResponse.setEmail(getVDCSessionBean().getLoginBean().getUser().getEmail());
            guestBookResponse.setFirstname(getVDCSessionBean().getLoginBean().getUser().getFirstName());
            guestBookResponse.setLastname(getVDCSessionBean().getLoginBean().getUser().getLastName());
            guestBookResponse.setInstitution(getVDCSessionBean().getLoginBean().getUser().getInstitution());
            guestBookResponse.setPosition(getVDCSessionBean().getLoginBean().getUser().getPosition());
            guestBookResponse.setVdcUser(getVDCSessionBean().getLoginBean().getUser());
        }
    }

    public List<CustomQuestionResponseUI> getCustomQuestionResponseUIs() {
        return customQuestionResponseUIs;
    }

    public void setCustomQuestionResponseUIs(List<CustomQuestionResponseUI> customQuestionResponseUI) {
        this.customQuestionResponseUIs = customQuestionResponseUI;
    }
    
    private List <SelectItem> setResponseUISelectItems(CustomQuestion cq){
        List  <SelectItem> retList = new ArrayList();
        for (CustomQuestionValue cqv: cq.getCustomQuestionValues()){
            SelectItem si = new SelectItem(cqv.getValueString(), cqv.getValueString());
            retList.add(si);
        }
        return retList;
    }

    public GuestBookResponse getGuestBookResponse() {
        return guestBookResponse;
    }

    public void setGuestBookResponse(GuestBookResponse guestBookResponse) {
        this.guestBookResponse = guestBookResponse;
    }


    public GuestBookQuestionnaire getGuestBookQuestionnaire() {
        return guestBookQuestionnaire;
    }

    public void setGuestBookQuestionnaire(GuestBookQuestionnaire guestBookQuestionnaire) {
        this.guestBookQuestionnaire = guestBookQuestionnaire;
    }

    private HtmlDataTable customQuestionsDataTable;

    public HtmlDataTable getCustomQuestionsDataTable() {
        return customQuestionsDataTable;
    }

    public void setCustomQuestionsDataTable(HtmlDataTable customQuestionsPanelSeries) {
        this.customQuestionsDataTable = customQuestionsPanelSeries;
    }

    private boolean validateEntries() {
        boolean retval = true;

        if (guestBookQuestionnaire.isFirstNameRequired() && guestBookResponse.getFirstname().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputFirstNameWarningMessage", "Please enter your first name.");
            retval = false;
        }
        if (guestBookQuestionnaire.isLastNameRequired() && guestBookResponse.getLastname().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputLastNameWarningMessage", "Please enter your last name.");
            retval = false;
        }
        if (guestBookQuestionnaire.isEmailRequired() && guestBookResponse.getEmail().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputEmailWarningMessage", "Please enter your email address.");
            retval = false;
        }
        if (guestBookQuestionnaire.isInstitutionRequired() && guestBookResponse.getInstitution().trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("inputInstitutionWarningMessage", "Please enter your institution.");
            retval = false;
        }
        if (guestBookQuestionnaire.isPositionRequired() && guestBookResponse.getPosition().trim().isEmpty()) {
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

    public String save_action() {

        if (!validateEntries()) {
            return "";
        }

        guestBookResponseServiceBean.update(guestBookResponse);
        String forwardPage = "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        return forwardPage;
    }

    public String cancel_action() {
        String forwardPage = "/admin/OptionsPage?faces-redirect=true" + getContextSuffix();
        return forwardPage;
    }
}
