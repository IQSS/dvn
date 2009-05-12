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

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class StudyCommentServiceBean implements StudyCommentService {
    
    @EJB
    StudyServiceLocal studyService;
    @EJB
    UserServiceLocal userService;

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    @Override
    public void flagStudyCommentAbuse(Long abusiveStudyCommentId){
        StudyComment flaggedStudyComment = em.find(StudyComment.class, abusiveStudyCommentId);
        flaggedStudyComment.setStatus(StudyComment.Status.FLAGGED);
        em.persist(flaggedStudyComment);
    }

    @Override
    public List <StudyComment> getAbusiveStudyComments(){
        return findByStatus(StudyComment.Status.FLAGGED);
    }

    @Override
    public List <StudyComment> getStudyComments(){
        List <StudyComment> displayStudies = findByStatus(StudyComment.Status.OK);
        displayStudies.addAll(findByStatus(StudyComment.Status.FLAGGED));
        return displayStudies;
    }

    public void ignoreFlaggedComment(Long flaggedCommentId){
        StudyComment flaggedComment = em.find(StudyComment.class, flaggedCommentId);
        flaggedComment.setStatus(StudyComment.Status.OK);
        em.persist(flaggedComment);
    }

    @Override
    public void deleteComment(Long deletedStudyCommentId){
        StudyComment deletedComment = em.find(StudyComment.class,deletedStudyCommentId);
        deletedComment.setStatus(StudyComment.Status.DELETED);
        em.persist(deletedComment);
    }

    @Override
    public void addComment(String comment, Long commenterId, Long studyId) {
        Study study = em.find(Study.class, studyId);
        if (study != null){
            VDCUser commenter = em.find(VDCUser.class, commenterId);
            if (commenter != null){
                StudyComment studyComment = new StudyComment(comment, commenter, study);
                study.getStudyComments().add(studyComment);
                em.persist(study);
            }
        }
    }

    public List<StudyComment> findByStatus(StudyComment.Status status){
        String statusStudyCommentsQuery = "Select c from StudyComment c where c.status = :commentstatus";
        List<StudyComment> studyComments = em.createQuery(statusStudyCommentsQuery).setParameter("commentstatus", status).getResultList();
        return studyComments;
    }
}
