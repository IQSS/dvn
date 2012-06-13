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
package edu.harvard.iq.dvn.core.web.admin;

import com.icesoft.faces.component.ext.HtmlSelectOneListbox;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.panelseries.PanelSeries;
import edu.harvard.iq.dvn.core.study.StudyFieldValue;
import edu.harvard.iq.dvn.core.vdc.*;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("EditGuestbookQuestionnairePage")
public class EditGuestbookQuestionnairePage extends VDCBaseBean implements java.io.Serializable {

    @EJB
    VDCServiceLocal vdcService;
    private GuestBookQuestionnaire guestBookQuestionnaire;
    private List<CustomQuestionUI> customQuestions = new ArrayList();
    private VDC vdc;
    private String questionType;
    private CustomQuestion newQuestion;

    public void init() {
        super.init();
        vdc = getVDCRequestBean().getCurrentVDC();
        guestBookQuestionnaire = getVDCRequestBean().getCurrentVDC().getGuestBookQuestionnaire();
        newQuestion = new CustomQuestion();
        newQuestion.setCustomQuestionValues(new ArrayList());
        if (guestBookQuestionnaire == null) { // set up default guest book questionnaire
            guestBookQuestionnaire = new GuestBookQuestionnaire();
            guestBookQuestionnaire.setEnabled(false);
            guestBookQuestionnaire.setEmailRequired(true);
            guestBookQuestionnaire.setFirstNameRequired(true);
            guestBookQuestionnaire.setLastNameRequired(true);
            guestBookQuestionnaire.setPositionRequired(true);
            guestBookQuestionnaire.setInstitutionRequired(true);
            guestBookQuestionnaire.setVdc(vdc);
            guestBookQuestionnaire.setCustomQuestions(new ArrayList());
            vdc.setGuestBookQuestionnaire(guestBookQuestionnaire);
        } else {
            if (guestBookQuestionnaire.getCustomQuestions() == null) {
                guestBookQuestionnaire.setCustomQuestions(new ArrayList());
            } else {
                for (CustomQuestion customQuestion : guestBookQuestionnaire.getCustomQuestions()) {
                    if (!customQuestion.isHidden()) {
                        CustomQuestionUI customQuestionUI = new CustomQuestionUI();
                        customQuestionUI.setCustomQuestion(customQuestion);
                        customQuestionUI.setEditMode(false);
                        customQuestions.add(customQuestionUI);

                    }
                }
            }
        }
        newQuestion.setGuestBookQuestionnaire(guestBookQuestionnaire);
        questionTypeSelectItems = loadQuestionTypeSelectItems();
    }

    public String save_action() {
        if (validateTerms()) {
            vdcService.save(vdc);
            String forwardPage = "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
            getVDCRenderBean().getFlash().put("successMessage", "Successfully updated guest book questionnaire.");
            return forwardPage;
        } else {
            return null;
        }

    }

    public String cancel_action() {
        return "/admin/OptionsPage?faces-redirect=true&vdcId=" + getVDCRequestBean().getCurrentVDC().getId();
    }

    private boolean validateTerms() {
        return true;
    }

    public boolean isQuestionRemovable() {
        return true;
    }

    private List<SelectItem> loadQuestionTypeSelectItems() {
        List selectItems = new ArrayList<SelectItem>();
        selectItems.add(new SelectItem("text", "Plain Text Input"));
        selectItems.add(new SelectItem("radiobuttons", "Radio Buttons"));
        return selectItems;
    }
    private List<SelectItem> questionTypeSelectItems = new ArrayList();

    public List<SelectItem> getQuestionTypeSelectItems() {
        return questionTypeSelectItems;
    }

    public void setQuestionTypeSelectItems(List<SelectItem> questionTypeSelectItems) {
        this.questionTypeSelectItems = questionTypeSelectItems;
    }
    private List<SelectItem> questionInputLevelSelectItems = new ArrayList();

    public List<SelectItem> getQuestionInputLevelSelectItems() {
        return this.questionInputLevelSelectItems;
    }
    private HtmlSelectOneMenu questionTypeListBox;
    private HtmlInputText inputCustomQuestionText;
    private HtmlInputText inputCustomQuestionTextTable;
    private HtmlDataTable customQuestionsDataTable;

    public HtmlDataTable getCustomQuestionsDataTable() {
        return customQuestionsDataTable;
    }

    public void setCustomQuestionsDataTable(HtmlDataTable customQuestionsPanelSeries) {
        this.customQuestionsDataTable = customQuestionsPanelSeries;
    }

    public HtmlInputText getInputCustomQuestionText() {
        return inputCustomQuestionText;
    }

    public void setInputCustomQuestionText(HtmlInputText inputQuestionText) {
        this.inputCustomQuestionText = inputQuestionText;
    }

    public HtmlInputText getInputCustomQuestionTextTable() {
        return inputCustomQuestionTextTable;
    }

    public void setInputCustomQuestionTextTable(HtmlInputText inputCustomQuestionTextTable) {
        this.inputCustomQuestionTextTable = inputCustomQuestionTextTable;
    }

    public HtmlSelectOneMenu getQuestionTypeListBox() {
        return questionTypeListBox;
    }

    public void setQuestionTypeListBox(HtmlSelectOneMenu questionTypeListBox) {
        this.questionTypeListBox = questionTypeListBox;
    }

    public GuestBookQuestionnaire getGuestBookQuestionnaire() {
        return guestBookQuestionnaire;
    }

    public boolean getGuestBookQuestionnaireEnabled() {
        return guestBookQuestionnaire.isEnabled();
    }

    public void setGuestBookQuestionnaire(GuestBookQuestionnaire guestBookQuestionnaire) {
        this.guestBookQuestionnaire = guestBookQuestionnaire;
    }

    public void toggleQuestionType(ValueChangeEvent ae) {
        if (newQuestion.getCustomQuestionValues().isEmpty()) {
            CustomQuestionValue addCQV = new CustomQuestionValue();
            addCQV.setCustomQuestion(newQuestion);
            newQuestion.getCustomQuestionValues().add(addCQV);
        }

    }

    public void addCustomQuestion() {
        String questionText = (String) inputCustomQuestionText.getValue();
        if (questionText.trim().isEmpty()) {
            getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter question text.");
            return;
        }
        newQuestion.setQuestionType(questionType);
        if (newQuestion.getQuestionType().equals("radiobuttons")) {
            if (newQuestion.getCustomQuestionValues().isEmpty()) {
                getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter answer text.");
                return;
            } else {
                for (CustomQuestionValue cqv : newQuestion.getCustomQuestionValues()) {
                    if (cqv.getValueString().trim().isEmpty()) {
                        getVDCRenderBean().getFlash().put("customQuestionWarningMessage", "Please enter answer text.");
                        return;
                    }
                }
            }
        } else {
            newQuestion.setCustomQuestionValues(null);
        }
        newQuestion.setGuestBookQuestionnaire(guestBookQuestionnaire);
        newQuestion.setQuestionString(questionText);



        if (guestBookQuestionnaire.getCustomQuestions() == null) {
            guestBookQuestionnaire.setCustomQuestions(new ArrayList());
        }

        CustomQuestionUI customQuestionUI = new CustomQuestionUI();
        customQuestionUI.setCustomQuestion(newQuestion);
        customQuestionUI.setEditMode(false);
        customQuestions.add(customQuestionUI);
        guestBookQuestionnaire.getCustomQuestions().add(newQuestion);
        inputCustomQuestionText.setValue("");
        newQuestion = new CustomQuestion();
        newQuestion.setCustomQuestionValues(new ArrayList());
        questionType = "text";
    }

    public void removeCustomQuestion(ActionEvent ae) {
        //we will do an actual remove if there are no responses and a virtual remove if there are responses
        boolean remove = false;
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        CustomQuestionUI customQuestionUIRemove = new CustomQuestionUI();
        for (CustomQuestionUI customQuestionUITest : customQuestions) {
            if (customQuestionUITest.getCustomQuestion().getQuestionString().equals(customQuestionUI.getCustomQuestion().getQuestionString())) {
                customQuestionUIRemove = customQuestionUITest;
                if (customQuestionUITest.getCustomQuestion().getId() != null && !customQuestionUITest.getCustomQuestion().getCustomQuestionResponses().isEmpty()) {
                    customQuestionUITest.getCustomQuestion().setHidden(true);
                } else {
                    remove = true;
                }

            }
        }
        customQuestions.remove(customQuestionUIRemove);
        if (remove) {
            vdc.getGuestBookQuestionnaire().getCustomQuestions().remove(customQuestionsDataTable.getRowIndex());
        }
    }

    public void editCustomQuestion(ActionEvent ae) {
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        getInputCustomQuestionTextTable().setValue(customQuestionUI.getCustomQuestion().getQuestionString());
        customQuestionUI.setEditMode(true);
    }

    public void saveCustomQuestion(ActionEvent ae) {
        CustomQuestionUI customQuestionUI = (CustomQuestionUI) customQuestionsDataTable.getRowData();
        customQuestionUI.setEditMode(false);
    }

    public List<CustomQuestionUI> getCustomQuestions() {
        return customQuestions;
    }

    public void setCustomQuestions(List<CustomQuestionUI> customQuestions) {
        this.customQuestions = customQuestions;
    }

    public void addCustomRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        int row = dataTable.getRowIndex();
        CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
        CustomQuestionValue newElem = new CustomQuestionValue();
        newElem.setCustomQuestion(data.getCustomQuestion());
        newElem.setValueString("");
        data.getCustomQuestion().getCustomQuestionValues().add(newElem);
    }

    public void removeCustomRow(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount() > 1) {
            CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
            for (CustomQuestion cq : getGuestBookQuestionnaire().getCustomQuestions()) {
                if (cq.getQuestionString().equals(data.getCustomQuestion().getQuestionString())) {
                    cq.getCustomQuestionValues().remove(data);
                }
            }
        }
    }

    public void addCustomRowInit(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        int row = dataTable.getRowIndex();
        CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
        CustomQuestionValue newElem = new CustomQuestionValue();
        newElem.setCustomQuestion(newQuestion);
        newElem.setValueString("");
        data.getCustomQuestion().getCustomQuestionValues().add(newElem);
    }

    public void removeCustomRowInit(ActionEvent ae) {
        HtmlDataTable dataTable = (HtmlDataTable) ae.getComponent().getParent().getParent();
        if (dataTable.getRowCount() > 1) {
            CustomQuestionValue data = (CustomQuestionValue) dataTable.getRowData();
            newQuestion.getCustomQuestionValues().remove(data);
        }
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public CustomQuestion getNewQuestion() {
        return newQuestion;
    }

    public void setNewQuestion(CustomQuestion newQuestion) {
        this.newQuestion = newQuestion;
    }
}
