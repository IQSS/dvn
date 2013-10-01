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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public Template getTemplate(Long templateId) {
        return em.find(Template.class, templateId);
    }

    public void updateTemplate(Template template) {
        em.merge(template);
    }

    public void deleteTemplate(Long templateId) {
        Template template = em.find(Template.class, templateId);
        em.remove(template);
    }

    public FieldInputLevel getFieldInputLevel(String name) {
        String queryStr = "SELECT f FROM FieldInputLevel f WHERE f.name = '" + name + "'"; 
        Query query= em.createQuery(queryStr);
        return (FieldInputLevel)query.getSingleResult();
    }    
    
    public boolean isTemplateUsed(Long templateId) {
        String queryStr = "SELECT count(id) from study s where s.template_id="+templateId;
        Query query = em.createNativeQuery(queryStr);
        Long count = (Long)query.getSingleResult();
        return count.compareTo(new Long(0))>0;    
    }
    
    public boolean isTemplateUsedAsVDCDefault(Long templateId) {
        String queryStr = "SELECT count(id) from vdc v where v.defaulttemplate_id="+templateId;
        Query query = em.createNativeQuery(queryStr);
        Long count = (Long)query.getSingleResult();
        return count.compareTo(new Long(0))>0;    
    }    
    
    public boolean isTemplateUsedAsNetworkDefault(Long templateId) {
        String queryStr = "SELECT count(id) from vdcNetwork v where v.defaulttemplate_id="+templateId;
        Query query = em.createNativeQuery(queryStr);
        Long count = (Long)query.getSingleResult();
        return count.compareTo(new Long(0))>0;    
    }   

    
    public Map getVdcTemplatesMap(VDC vdc) {
        Map templatesMap = new LinkedHashMap();
        
        for (Template template : getEnabledNetworkTemplates()) {
            if (template.getVdcNetwork().getId().equals(new Long(0))){
                  templatesMap.put(template.getName(), template.getId());
            }
        }
        if ( !vdc.getVdcNetwork().getId().equals(new Long(0)) ) {
            for (Long templateId : getEnabledSubnetworkTemplates(vdc.getVdcNetwork().getId())) {
                Template template = getTemplate(templateId);
                templatesMap.put(template.getName(), template.getId());
            }
        }       
        for (Template template : getEnabledVDCTemplates(vdc.getId())) {
            templatesMap.put(template.getName(), template.getId());
        }
        return templatesMap;
    } 
    
    // unused?
    public Map getVdcNetworkTemplatesMap(Long vdcNetworkId) {

        Map templatesMap = new LinkedHashMap();
        for (Object object : getEnabledSubnetworkTemplates(vdcNetworkId)) {
            Long template_id = new Long((Integer)object);
            Template template = getTemplate(template_id);
            templatesMap.put(template.getName(), template.getId());
        }
        if (templatesMap.isEmpty()) {
            for (Template template : getEnabledNetworkTemplates()) {
                templatesMap.put(template.getName(), template.getId());
            }
        }
        return templatesMap;
    } 
    
    public List<Template> getVDCTemplates(Long vdcId) {
        String query = "select object(o) FROM Template as o WHERE o.vdc.id = :vdcId ORDER BY o.name";
        return (List) em.createQuery(query).setParameter("vdcId", vdcId).getResultList();
    }

    public List<Template> getEnabledVDCTemplates(Long vdcId) {
        String query = "select object(o) FROM Template as o WHERE o.vdc.id = :vdcId and o.enabled = true ORDER BY o.name";
        return (List) em.createQuery(query).setParameter("vdcId", vdcId).getResultList();
    }

    public List<Template> getNetworkTemplates() {
        String query = "select object(o) FROM Template as o WHERE o.vdc is null ORDER BY o.name";
        return (List) em.createQuery(query).getResultList();
    }

    public List<Template> getEnabledNetworkTemplates() {
        String query = "select object(o) FROM Template as o WHERE o.vdc is null and o.enabled = true ORDER BY o.name";
        return (List) em.createQuery(query).getResultList();
    }
    
    public List<Long> getEnabledSubnetworkTemplates(Long id) {
        return getSubnetworkTemplates(id, true);
    }

    public List<Long> getSubnetworkTemplates(Long subnetworkId, boolean onlyEnabled) {
        String queryStr;
        if (onlyEnabled) {
            queryStr = "SELECT o.id from template o where o.vdc_id is null and o.enabled = true and (o.vdcnetwork_id=" + subnetworkId +  " or o.vdcnetwork_id=0) ORDER BY o.name";
        } else {
            queryStr = "SELECT o.id from template o where o.vdc_id is null and ( o.vdcnetwork_id=" + subnetworkId + " or o.vdcnetwork_id=0) ORDER BY o.name";
        }
        Query query = em.createNativeQuery(queryStr);
//        List<Long> templateIdsLong = query.getResultList(); // java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Long
        List<Integer> templateIdsInt = query.getResultList();
        ArrayList<Long> templateIdsLong = new ArrayList<Long>();
        for (Integer templateIdInt : templateIdsInt) {
            Long templateIdLong = new Long(templateIdInt);
            templateIdsLong.add(templateIdLong);
        }
        return templateIdsLong;
    }


    public List<ControlledVocabulary> getNetworkControlledVocabulary() {
        String query = "select object(o) FROM ControlledVocabulary as o ORDER BY o.name";
        return (List) em.createQuery(query).getResultList();
    }
    
    public ControlledVocabulary getControlledVocabulary(Long cvId) {
        return em.find(ControlledVocabulary.class, cvId);
    }
    
    
    public void saveControlledVocabulary(ControlledVocabulary controlledVocabulary) {
        if (controlledVocabulary.getId() == null) {
            em.persist(controlledVocabulary);
        } else {
            em.merge(controlledVocabulary);
        }
    }

     
}
