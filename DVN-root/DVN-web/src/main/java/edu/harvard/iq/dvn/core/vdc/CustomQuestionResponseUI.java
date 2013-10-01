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
package edu.harvard.iq.dvn.core.vdc;

import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author skraffmiller
 */
public class CustomQuestionResponseUI {
    private CustomQuestionResponse customQuestionResponse;
    private List <SelectItem> responseSelectItems;
    private boolean required;
    private String questionType;

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public CustomQuestionResponse getCustomQuestionResponse() {
        return customQuestionResponse;
    }

    public void setCustomQuestionResponse(CustomQuestionResponse customQuestionResponse) {
        this.customQuestionResponse = customQuestionResponse;
    }

    public List<SelectItem> getResponseSelectItems() {
        return responseSelectItems;
    }

    public void setResponseSelectItems(List<SelectItem> responseSelectItems) {
        this.responseSelectItems = responseSelectItems;
    }

    
    
}
