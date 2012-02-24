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
 * EditStudyServiceBean.java
 *
 * Created on September 29, 2006, 1:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.util.FieldInputLevelConstant;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.study.TemplateFieldControlledVocabulary;
import edu.harvard.iq.dvn.core.web.study.TemplateFieldValue;
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
    

    public void newNetworkTemplate() {
        newTemplate(null,null);
    }    
    
    public void newTemplate(Long vdcId) {
        newTemplate(vdcId, null);
    }


    public void newTemplate(Long vdcId, Long studyVersionId) {
        newTemplate = true;
        template = new Template();
        
        // set the vdc or nwtwork
        if (vdcId != null) {
            VDC vdc = em.find(VDC.class, vdcId);
            template.setVdc(vdc);
            vdc.getTemplates().add(template);
            addFields(vdcId);
        } else {
            VDCNetwork network = vdcNetworkService.find(new Long(1));
            template.setVdcNetwork(network);
            addNetworkFields(network);

        }

        // copy metadata if from a study
        if (studyVersionId != null) {
            Metadata metadata = em.find(StudyVersion.class, studyVersionId).getMetadata();
            template.setMetadata(new Metadata(metadata));
            template.getMetadata().setDateOfDeposit("");
            template.getMetadata().setUNF(null);
        }
        
        em.persist(template);
    } 
    

    public void  newClonedTemplate(Long vdcId, Template cloneSource) {
        newTemplate=true;
        template = new Template();
        Metadata clonedMetadata = new Metadata(cloneSource.getMetadata(), false, false );
        template.setMetadata(clonedMetadata);        
        Collection<TemplateField> defaultFields = cloneSource.getTemplateFields();  
                
        template.setTemplateFields(new ArrayList());
        for( TemplateField defaultField: defaultFields) {
            TemplateField tf = new TemplateField();
            tf.setDefaultValue(defaultField.getDefaultValue());
            tf.setStudyField(defaultField.getStudyField());
            // bring over field values separately
            //get template field values from cloned metadata
            List <TemplateFieldValue> tfvList = new  ArrayList();
            for (TemplateFieldValue tfv : clonedMetadata.getTemplateFieldValues()){
                if(tfv.getTemplateField().getStudyField().equals(tf.getStudyField())){
                    tfv.setTemplateField(tf);
                    tfvList.add(tfv);
                }
            }
            /* 
            for (TemplateFieldValue tfv: defaultField.getTemplateFieldValues()){ 
                TemplateFieldValue tfvn = new TemplateFieldValue();
                tfvn.setStrValue(tfv.getStrValue());                
                tfvn.setMetadata(clonedMetadata);
                tfvn.setDisplayOrder(tfv.getDisplayOrder());
                tfvn.setTemplateField(tf);
                
            }*/
            tf.setTemplateFieldValues(tfvList);
            // bring over field contolled vocab separately
            List <TemplateFieldControlledVocabulary> tfcvList = new  ArrayList();
            for (TemplateFieldControlledVocabulary tfcv: defaultField.getTemplateFieldControlledVocabulary()){
                TemplateFieldControlledVocabulary tfcvn = new TemplateFieldControlledVocabulary();
                tfcvn.setStrValue(tfcv.getStrValue());
                tfcvn.setTemplateField(tf);
                tfcvn.setMetadata(clonedMetadata);
                tfcvList.add(tfcvn);
            }
            tf.setTemplateFieldControlledVocabulary(tfcvList);
            tf.setTemplate(template);
            tf.setFieldInputLevelString(defaultField.getFieldInputLevelString());
            tf.setdcmSortOrder(defaultField.getDcmSortOrder());
            
            template.getTemplateFields().add(tf);
        }
        VDC vdc = em.find(VDC.class, vdcId);      
        template.setVdc(vdc);
        template.getMetadata().setDateOfDeposit("");
        template.getMetadata().setUNF(null);
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
    
 
   
    /**
     * Get the default template for the given dataverse, and add fields to the 
     *  new template based on the default template. 
     */
    private void addFields(Long vdcId) {
        
        VDC vdc = em.find(VDC.class, vdcId);
        Collection<TemplateField> defaultFields = vdc.getDefaultTemplate().getTemplateFields();
       
        template.setTemplateFields(new ArrayList());
        for( TemplateField defaultField: defaultFields) {
            TemplateField tf = new TemplateField();
            tf.setDefaultValue(defaultField.getDefaultValue());
            tf.setTemplateFieldControlledVocabulary(new ArrayList());
            /*tf.setFieldInputLevel(defaultField.getFieldInputLevel());*/
            tf.setFieldInputLevelString(defaultField.getFieldInputLevelString());
            tf.setStudyField(defaultField.getStudyField());
            tf.setTemplate(template);
            tf.getStudyField().setDcmField(defaultField.getStudyField().isDcmField());
            tf.setdcmSortOrder(defaultField.getDcmSortOrder());
            template.getTemplateFields().add(tf);
        }
    }
    
    private void addNetworkFields(VDCNetwork network) {
        
        Collection<TemplateField> defaultFields = network.getDefaultTemplate().getTemplateFields();
       
        template.setTemplateFields(new ArrayList());
        for( TemplateField defaultField: defaultFields) {
            TemplateField tf = new TemplateField();
            tf.setDefaultValue(defaultField.getDefaultValue());
            tf.setFieldInputLevelString(defaultField.getFieldInputLevelString());
            tf.setTemplateFieldControlledVocabulary(new ArrayList());
            tf.setStudyField(defaultField.getStudyField());
            tf.setTemplate(template);
            tf.getStudyField().setDcmField(defaultField.getStudyField().isDcmField());
            tf.setdcmSortOrder(defaultField.getDcmSortOrder());
            template.getTemplateFields().add(tf);
        }
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
    
}

