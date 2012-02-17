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
