/*
 * StudyFieldServiceBean.java
 *
 * Created on October 23, 2006, 9:26 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class StudyFieldServiceBean implements StudyFieldServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    private static final String NAME_QUERY = "SELECT sf from StudyField sf where sf.name= :fieldName ";
    private static final String ID_QUERY = "SELECT sf from StudyField sf where id = :fieldId";

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
    }

    
}
