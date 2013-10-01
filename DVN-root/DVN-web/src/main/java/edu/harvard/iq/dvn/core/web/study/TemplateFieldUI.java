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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.TemplateField;
import edu.harvard.iq.dvn.core.study.TemplateServiceLocal;
import edu.harvard.iq.dvn.core.util.FieldInputLevelConstant;
import javax.naming.InitialContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class TemplateFieldUI {
    
    public TemplateFieldUI() {}
    
    public TemplateFieldUI(TemplateField tf) {
        templateField=tf;
    }

    private TemplateField templateField;

    public TemplateField getTemplateField() {
        return templateField;
    }

    public void setTemplateField(TemplateField templateField) {
        this.templateField = templateField;
    }
    
    public boolean isRecommended() {
        return templateField.isRecommended();
    }
    
    public void setRecommended(boolean isRecommended) {
       // finally, add types from db
        TemplateServiceLocal templateService = null;
        try {
            templateService = (TemplateServiceLocal) new InitialContext().lookup("java:comp/env/templateService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isRecommended) {
              templateField.setFieldInputLevelString("recommended");
              /*templateField.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getRecommended()));*/
          } else {
                templateField.setFieldInputLevelString("optional");
              /*templateField.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getOptional()));*/
          }    }
    
    

}
