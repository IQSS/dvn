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
 * EditStudyServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditTemplateServiceBean implements edu.harvard.iq.dvn.core.study.EditTemplateService, java.io.Serializable {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB TemplateServiceLocal templateService;
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    Template template;
    private boolean newTemplate=false;
  
  
    
    /**
     *  Initialize the bean with a Template for editing
     */
    public void setTemplate(Long id ) {
        template = em.find(Template.class,id);
        if (template==null) {
            throw new IllegalArgumentException("Unknown template id: "+id);
        }
        
      
   
    } 
    
    // used to create a new Network template from the default Network templates (exact clone)
    public void newNetworkTemplate() {
        VDCNetwork network = vdcNetworkService.find(new Long(1));  
        newTemplate(network.getDefaultTemplate(), null, true, true);        
    }    
    
    // used to create a new VDC template from the default VDC templates (exact clone)
    public void newTemplate(Long vdcId) {
        VDC vdc = em.find(VDC.class, vdcId);
        newTemplate(vdc.getDefaultTemplate(), vdc, true, true);
    }
    
    // used to create a new VDC template from a preexiting study (no disabled fields)
    public void newTemplate(Long studyVersionId, Long vdcId) {
        StudyVersion sv = em.find(StudyVersion.class, studyVersionId);
        VDC vdc = em.find(VDC.class, vdcId);
        newTemplate(sv.getStudy().getTemplate(), sv.getMetadata(), vdc, true, false);
        
        // clear out fields
        template.getMetadata().setDateOfDeposit("");
        template.getMetadata().setUNF(null);        
    }    
    
    // used to create to clone any template; always copies hidden; only copies disabled if target is network
    public void  newClonedTemplate(Long sourceId, Long vdcId) {
        VDC vdc = (vdcId != null ? em.find(VDC.class, vdcId) : null);
        Template source = em.find(Template.class, sourceId);
        boolean copyDisabled = vdc != null;         
      
        newTemplate(source, vdc, true, copyDisabled);
    }
 
 
    private void newTemplate(Template sourceTemplate, VDC vdc, boolean copyHidden, boolean copyDisabled) {
       newTemplate(sourceTemplate, null, vdc, copyHidden, copyDisabled);
    }
    
    private void newTemplate(Template sourceTemplate, Metadata sourceMetadata, VDC vdc, boolean copyHidden, boolean copyDisabled) {
        newTemplate = true;
        template = new Template(sourceTemplate);       
        
        // if no passed metadata, use metadata from template
        if (sourceMetadata == null) {
            sourceMetadata = sourceTemplate.getMetadata();
        }
        
        if (copyHidden && copyDisabled) {
            template.setMetadata( new Metadata(sourceMetadata) );
        } else {
            template.setMetadata( new Metadata(sourceMetadata,copyHidden, copyDisabled) );
        }
        template.getMetadata().setTemplate(template);
        
        if (vdc != null) {
            template.setVdc(vdc);
        }
        
        em.persist(template);
    }    
    
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
    
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public void removeCollectionElement(List list,int index) {
        em.remove(list.get(index));
        list.remove(index);
    }    
    
    public  Template getTemplate() {
        return template;
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudy() {
       em.remove(template);
        
    }
    
    private HashMap studyMap;
    public HashMap getStudyMap() {
        return studyMap;
    }
    
    public void setStudyMap(HashMap studyMap) {
        this.studyMap=studyMap;
    }
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save() {
        //Need to save any newly created study fields
        Collection<TemplateField> templateFields = template.getTemplateFields();
        for (TemplateField tf : templateFields ){
            if(tf.getId()== null){
                em.persist(tf);
            }
            StudyField sf = tf.getStudyField();            
            if (sf.getId() == null){
                em.persist(sf);
            }
        }     
    }
    
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    
  
   
    
    /**
     * Creates a new instance of EditStudyServiceBean
     */
    public EditTemplateServiceBean() {
    }
    
 
    
    
   
    
 
    
    public boolean isNewTemplate() {
        return newTemplate;
    }
    
 
       
     public void changeRecommend(TemplateField tf, boolean isRecommended) {
          if (isRecommended) {
              tf.setFieldInputLevelString("recommended");
              /*tf.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getRecommended()));*/
          } else {
              tf.setFieldInputLevelString("optional");
              /*tf.setFieldInputLevel(templateService.getFieldInputLevel(FieldInputLevelConstant.getOptional()));*/
          }
     }
     
     public void changeFieldInputLevel(TemplateField tf, String inputLevel) {
          tf.setFieldInputLevelString(inputLevel);
     }

     public void setTemplateFieldControlledVocabulary(TemplateField tf, Long cvId) {
         tf.setControlledVocabulary( em.find(ControlledVocabulary.class, cvId) );
         
     }
     
}

