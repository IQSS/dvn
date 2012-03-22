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
 * StudyFieldServiceBean.java
 *
 * Created on October 23, 2006, 9:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.study;

import java.util.List;
import java.util.ArrayList; 
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class StudyFieldServiceBean implements StudyFieldServiceLocal, java.io.Serializable {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    private static final String NAME_QUERY = "SELECT sf from StudyField sf where sf.name= :fieldName ";
    private static final String ID_QUERY = "SELECT sf from StudyField sf where id = :fieldId";
    
    private static String[] advancedSearchFields = {"title", "authorName", "globalId", "otherId", "abstractText", "keywordValue", "keywordVocabulary", "topicClassValue", "topicClassVocabulary", "producerName", "distributorName", "fundingAgency", "productionDate", "distributionDate", "dateOfDeposit", "timePeriodCoveredStart", "timePeriodCoveredEnd", "country", "geographicCoverage", "geographicUnit", "universe", "kindOfData", "extnFld4"};
    private static List<StudyField> advancedStudyFields = null; 

    /** Creates a new instance of StudyFieldServiceBean */
    public StudyFieldServiceBean() {
    }

    public List findAll() {
        List <StudyField> studyFields = (List <StudyField>) em.createQuery("SELECT sf from StudyField sf").getResultList();
        return studyFields;
    }

    public StudyField findByName(String name) {
        StudyField sf = (StudyField) em.createQuery(NAME_QUERY).setParameter("fieldName",name).getSingleResult();
        return sf;
    }

    public StudyField findById(Long id) {
        StudyField sf = (StudyField) em.createQuery(ID_QUERY).setParameter("fieldId", id).getSingleResult();
        return sf;
    }

    public List findAdvSearchDefault() {
        List <StudyField> studyFields = (List <StudyField>) em.createQuery("SELECT sf from StudyField sf where sf.advancedSearchField = true").getResultList();
        return studyFields;
        
        /* hard-coded defaults; used until 3.0: 
        if (advancedStudyFields == null) {
            advancedStudyFields = new ArrayList<StudyField>();
            for (int i=0; i < advancedSearchFields.length; i++) {
                StudyField sf = new StudyField();
                sf.setName(advancedSearchFields[i]);
                sf.setDescription(getUserFriendlySearchField(advancedSearchFields[i]));
                advancedStudyFields.add(sf);
            }
        }
              
        return advancedStudyFields; 
         *  -- L.A.
         */
    }

    private String getUserFriendlySearchField(String searchField) {
        try {
            return ResourceBundle.getBundle("SearchFieldBundle").getString(searchField);
        } catch (MissingResourceException e) {
            return searchField;
        }
    }
    
}
