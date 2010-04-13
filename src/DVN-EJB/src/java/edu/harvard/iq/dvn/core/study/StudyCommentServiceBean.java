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

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class StudyCommentServiceBean implements StudyCommentService {
    @EJB
    MailServiceLocal mailService;
/*    @EJB
    StudyServiceLocal studyService;
    @EJB
    UserServiceLocal userService;
*/
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    @Override
    public void flagStudyCommentAbuse(Long abusiveStudyCommentId, Long flaggerId){
        StudyComment flaggedStudyComment = em.find(StudyComment.class, abusiveStudyCommentId);
        flaggedStudyComment.setStatus(StudyComment.Status.FLAGGED);
        VDCUser flagger = em.find(VDCUser.class, flaggerId);
        flaggedStudyComment.getFlaggedByUsers().add(flagger);
        em.persist(flaggedStudyComment);
    }

    @Override
    public List <StudyComment> getAbusiveStudyComments(){
        return findByStatus(StudyComment.Status.FLAGGED);
    }


    @Override
    public List <StudyComment> getStudyComments(Long studyId){
        String studyCommentsByStudyIdQuery = "Select c from StudyComment c where c.studyVersion.study.id = :commentStudyId and c.status <> :deleted order by c.createTime";
        Query query = em.createQuery(studyCommentsByStudyIdQuery);
        query.setParameter("commentStudyId", studyId);
        query.setParameter("deleted",StudyComment.Status.DELETED);
        List<StudyComment> studyComments = query.getResultList();
        return studyComments;
    }

    @Override
    public List <StudyComment> getStudyComments(){
        List <StudyComment> displayStudies = findByStatus(StudyComment.Status.OK);
        displayStudies.addAll(findByStatus(StudyComment.Status.FLAGGED));
        return displayStudies;
    }

    @Override
    public void okComment(Long commentId, String okMessage){
        StudyComment comment = em.find(StudyComment.class, commentId);
        comment.setStatus(StudyComment.Status.OK);
        em.persist(comment);
        // send mail to flaggedByUsers
        List<VDCUser> flaggedByUsers = (List<VDCUser>)comment.getFlaggedByUsers();
        Iterator iterator = flaggedByUsers.iterator();
        String flaggedByUserEmails= new String("");
        while (iterator.hasNext()) {
            VDCUser vdcuser = (VDCUser)iterator.next();
            if (flaggedByUsers.indexOf(vdcuser) > 0)
                flaggedByUserEmails += ", ";
            flaggedByUserEmails += vdcuser.getEmail();
        }
        if (!flaggedByUserEmails.isEmpty()) {
            mailService.sendDoNotReplyMail(flaggedByUserEmails, "Study Comment Action Ignored", okMessage);
        }
        // clear the list from the flaggedByUser table
        Iterator newIterator = flaggedByUsers.iterator();
        while (newIterator.hasNext()) {
            newIterator.next();
            newIterator.remove();
        }
    }

    @Override
    public void deleteComment(Long deletedStudyCommentId, String deletedMessage){
        StudyComment deletedComment = em.find(StudyComment.class,deletedStudyCommentId);
        deletedComment.setStatus(StudyComment.Status.DELETED);
        em.persist(deletedComment);
        // send mail to flaggedByUsers
        List<VDCUser> flaggedByUsers = (List<VDCUser>)deletedComment.getFlaggedByUsers();
        Iterator iterator = flaggedByUsers.iterator();
        String flaggedByUserEmails= new String("");
        while (iterator.hasNext()) {
            VDCUser vdcuser = (VDCUser)iterator.next();
            if (flaggedByUsers.indexOf(vdcuser) > 0)
                flaggedByUserEmails += ", ";
            flaggedByUserEmails += vdcuser.getEmail();
        }
        if (!flaggedByUserEmails.isEmpty()) { 
            mailService.sendDoNotReplyMail(flaggedByUserEmails, "Study Comment Action Deleted", deletedMessage);
        }
    }

    @Override
    public void addComment(String comment, Long commenterId, Long studyId) {
        Study study = em.find(Study.class, studyId);
        if (study != null){
            VDCUser commenter = em.find(VDCUser.class, commenterId);
            if (commenter != null){
                StudyVersion sv = null;
                if (study.getReleasedVersion() != null) {
                    sv = study.getReleasedVersion();
                } else {
                    throw new IllegalArgumentException("Study does not have released version, study.id = " + study.getId());
                }
                StudyComment studyComment = new StudyComment(comment, commenter, sv);
                studyComment.setStatus(StudyComment.Status.OK);
                sv.getStudyComments().add(studyComment);
                em.persist(study);
            }
        }
    }

    public List<StudyComment> findByStatus(StudyComment.Status status){
        String statusStudyCommentsQuery = "Select c from StudyComment c where c.status = :commentstatus";
        List<StudyComment> studyComments = em.createQuery(statusStudyCommentsQuery).setParameter("commentstatus", status).getResultList();
        Iterator iterator = studyComments.iterator();
        while (iterator.hasNext()) {
            StudyComment studyComment = (StudyComment)iterator.next();
            studyComment.getFlaggedByUsers().size(); // required to load the list from the join table. Otherwise it may not be instantiated.
            studyComment.getFlaggedByUsers();
        }
        return studyComments;
    }
}
