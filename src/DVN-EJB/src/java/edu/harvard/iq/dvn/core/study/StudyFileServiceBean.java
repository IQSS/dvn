/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author gdurand
 */
@Stateless
public class StudyFileServiceBean implements StudyFileServiceLocal {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;


    public StudyFile getStudyFile(Long fileId) {
        StudyFile file = em.find(StudyFile.class, fileId);
        if (file == null) {
            throw new IllegalArgumentException("Unknown studyFileId: " + fileId);
        }


        return file;
    }

    //TODO: VERSION: this method should be removed
    public FileCategory getFileCategory(Long fileCategoryId) {
        FileCategory fileCategory = em.find(FileCategory.class, fileCategoryId);
        if (fileCategory == null) {
            throw new IllegalArgumentException("Unknown fileCategoryId: " + fileCategoryId);
        }


        return fileCategory;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<FileMetadata> getStudyFilesByExtension(String extension) {

        String queryStr = "SELECT fm from FileMetadata sf where lower(sf.fileName) LIKE :extension";

        Query query = em.createQuery(queryStr);
        query.setParameter("extension", "%." + extension.toLowerCase());
        return query.getResultList();

    }

    public void updateStudyFile(StudyFile detachedStudyFile) {
        em.merge(detachedStudyFile);
    }



    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public java.util.List<FileMetadata> getOrderedFilesByStudy(Long studyId) {
        // Note: This ordering is case-sensitive, so names beginning with upperclass chars will appear first.
        // (I tried using UPPER(f.name) to make the sorting case-insensitive, but the EJB query language doesn't seem
        // to like this.)
        String queryStr = "SELECT f FROM FileMetadata f  WHERE f.studyVersion.study.id = " + studyId + " ORDER BY f.category, f.label";
        Query query = em.createQuery(queryStr);
        List<FileMetadata> studyFiles = query.getResultList();
   //     for (StudyFile sf: studyFiles) {
   //         sf.getDataTables().size();
   //     }
        return studyFiles;
    }

    public Boolean doesStudyHaveSubsettableFiles(Long studyId) {
        // TODO: VERSION: change this to use a study version object
        List<String> subsettableList = new ArrayList();
        Query query = em.createNativeQuery("select fileclass from studyfile where study_id = " + studyId);
        for (Object currentResult : query.getResultList()) {
            subsettableList.add( ((String) ((Vector) currentResult).get(0)) );
        }

        if ( !subsettableList.isEmpty() ) {
            for (String fclass : subsettableList) {
                if ("TabularDataFile".equals(fclass) || "NetworkDataFile".equals(fclass))
                    return Boolean.TRUE;
            }

            return Boolean.FALSE;
        }

        return null;


    }

 
}
