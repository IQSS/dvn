/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author skraffmiller
 */
public class CustomQuestionUI {
    private CustomQuestion customQuestion;
    private List <SelectItem> questionSelectItems;
    private boolean editMode;
    
    public CustomQuestion getCustomQuestion() {
        return customQuestion;
    }

    public void setCustomQuestion(CustomQuestion customQuestion) {
        this.customQuestion = customQuestion;
    }

    public boolean isEditMode() {
        return editMode;
    }
    
    public boolean isRemovable(){
        return true;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public List<SelectItem> getQuestionSelectItems() {
        return questionSelectItems;
    }

    public void setQuestionSelectItems(List<SelectItem> questionSelectItems) {
        this.questionSelectItems = questionSelectItems;
    }
    
}
