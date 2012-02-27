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
    
}
