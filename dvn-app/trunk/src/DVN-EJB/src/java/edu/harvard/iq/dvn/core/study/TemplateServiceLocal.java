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
package edu.harvard.iq.dvn.core.study;

import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 * This is the business interface for NewSession enterprise bean.
 */
@Local
public interface TemplateServiceLocal extends java.io.Serializable {
  
    
    public  void createTemplate(Long vdcId);    

    public Template getTemplate(Long templateId);
    public void updateTemplate(Template template);
    public void deleteTemplate(Long templateId);

    public FieldInputLevel getFieldInputLevel(String name);
    
    public boolean isTemplateUsed(Long templateId);
    public boolean isTemplateUsedAsVDCDefault(Long templateId);
    
    
    public Map getVdcTemplatesMap(Long vdcId);   
    public List<Template> getVDCTemplates(Long vdcId);
    public List<Template> getEnabledVDCTemplates(Long vdcId);
    public List<Template> getNetworkTemplates();
    public List<Template> getEnabledNetworkTemplates();
    
    public List<ControlledVocabulary> getNetworkControlledVocabulary();
    public ControlledVocabulary getControlledVocabulary(Long cvId);
    public void saveControlledVocabulary(ControlledVocabulary controlledVocabulary);
}
