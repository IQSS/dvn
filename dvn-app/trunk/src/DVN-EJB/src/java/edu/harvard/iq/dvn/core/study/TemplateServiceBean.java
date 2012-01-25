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
 * TemplateServiceBean.java
 *
 * Created on July 22, 2008, 11:47 AM
 * Happy Birthday Gustavo
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class TemplateServiceBean implements edu.harvard.iq.dvn.core.study.TemplateServiceLocal, java.io.Serializable {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
   
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
   
  
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.study.TemplateServiceBean");
  
    /**
     * Creates a new instance of TemplateServiceBean
     */
    public TemplateServiceBean() {
    }

    private void addFields(Template template, Long vdcId) {
      VDC vdc = em.find(VDC.class, vdcId);
        Collection<TemplateField> defaultFields = vdc.getDefaultTemplate().getTemplateFields();
       
        template.setTemplateFields(new ArrayList());
        for( TemplateField defaultField: defaultFields) {
            TemplateField tf = new TemplateField();
            tf.setDefaultValue(defaultField.getDefaultValue());
            /*tf.setFieldInputLevel(defaultField.getFieldInputLevel());*/
            tf.setStudyField(defaultField.getStudyField());
            tf.setTemplate(template);
            template.getTemplateFields().add(tf);
        }

      
    }
    
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public  void createTemplate(Long vdcId) {
       // Template template = new Template();
        Template template = new Template();
        template.setName((new Date()).toString());
            TemplateField tf = new TemplateField();        
            tf.setTemplate(template);     
            template.setTemplateFields(new ArrayList<TemplateField>());
            template.getTemplateFields().add(tf);
        /*
        VDC vdc = em.find(VDC.class, vdcId);      
        template.setVdc(vdc);
        vdc.getTemplates().add(template);
        addFields(template, vdcId);
         * */
        em.persist(template);
    
   }

        // Also copy default file categories
        /* Not doing this anymore  
        createdTemplate.setTemplateFileCategories(new ArrayList<TemplateFileCategory>());
        for(TemplateFileCategory templateFileCategory: defTemplate.getTemplateFileCategories()) {
            TemplateFileCategory tfc = new TemplateFileCategory();
            tfc.setName(templateFileCategory.getName());
            tfc.setDisplayOrder(templateFileCategory.getDisplayOrder());
            tfc.setTemplate(templateFileCategory.getTemplate());
        }
         */  
   
      //  return template;
    
    /*
    public void createTemplate(String templateName, Long studyId, Long vdcId) {
        Template template = new Template();
        template.setName(templateName);
        Study study = em.find(Study.class, studyId);
        study.getMetadata().copyMetadata(template.getMetadata());
        template.setVdc(em.find(VDC.class, vdcId));
        em.persist(template);

    }
     */ 
    public void deleteTemplate(Long templateId) {
        Template template = em.find(Template.class, templateId);
        em.remove(template);
    }
    public Template getTemplate(Long templateId) {
        return em.find(Template.class, templateId);
    }
    
    public boolean isTemplateUsed(Long templateId) {
        String queryStr = "SELECT count(id) from study s where s.template_id="+templateId;
        Query query = em.createNativeQuery(queryStr);
        Long count = (Long)query.getSingleResult();
        return count.compareTo(new Long(0))>0;    
    }
    
    public FieldInputLevel getFieldInputLevel(String name) {
        String queryStr = "SELECT f FROM FieldInputLevel f WHERE f.name = '" + name + "'"; 
        Query query= em.createQuery(queryStr);
        return (FieldInputLevel)query.getSingleResult();
    }
    
}
