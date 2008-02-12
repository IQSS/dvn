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

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.admin.NetworkRoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.dsb.DSBIngestMessage;
import edu.harvard.hmdc.vdcnet.dsb.DSBWrapper;
import edu.harvard.hmdc.vdcnet.gnrs.GNRSServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetwork;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.study.DataFileFormatType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditStudyServiceBean implements edu.harvard.hmdc.vdcnet.study.EditStudyService, java.io.Serializable {
    @EJB IndexServiceLocal indexService;
    @EJB ReviewStateServiceLocal reviewStateService;
    @EJB MailServiceLocal mailService;
    @EJB DDI20ServiceLocal ddiService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
    @EJB GNRSServiceLocal gnrsService;
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName="jms/DSBIngest") Queue queue;
    @Resource(mappedName="jms/DSBQueueConnectionFactory") QueueConnectionFactory factory;
    Study study;
    private boolean newStudy=false;
    private List currentFiles = new ArrayList();
    private List newFiles = new ArrayList();
    private String ingestEmail;
    
    /**
     *  Initialize the bean with a Study for editing
     */
    public void setStudy(Long id ) {
        study = em.find(Study.class,id);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+id);
        }
        
        // now set the files
        for (Iterator fileIt = studyService.getOrderedFilesByStudy(study.getId()).iterator(); fileIt.hasNext();) {
            StudyFile sf = (StudyFile) fileIt.next();
            StudyFileEditBean fileBean = new StudyFileEditBean(em.find(StudyFile.class,sf.getId()));
            fileBean.setFileCategoryName(sf.getFileCategory().getName());
            getCurrentFiles().add(fileBean);

        }
   
    }
    
    public void newStudy(Long vdcId, Long userId) {
        newStudy=true;
        
        VDC vdc = em.find(VDC.class, vdcId);
        VDCUser creator = em.find(VDCUser.class,userId);
        ReviewState reviewState = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW);
        
        // If user is network admin, or has a vdc role > contributor, set the study to IN REVIEW
        // else (user is a contributor), set the study to NEW
        if ((creator.getNetworkRole()!=null && creator.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN))
        || (creator.getVDCRole(vdc)!=null && !creator.getVDCRole(vdc).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR))) {
            reviewState = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW);
        }
        
        study = new Study(vdc, creator, em.find(ReviewState.class, reviewState.getId()));
        em.persist(study);
        
        // set default protocol and authority
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        study.setProtocol(vdcNetwork.getProtocol());
        study.setAuthority(vdcNetwork.getAuthority());
        
    }
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
    
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public  Study getStudy() {
        return study;
    }
    
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteStudy() {
        studyService.deleteStudy(study.getId());
        
        
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
    public void save(Long vdcId, Long userId) {
        VDCUser user = em.find(VDCUser.class,userId);
        try {
            // If user is a Contributor and is editing a study with review state "Released", then
            // revert the review state back to In Review.
            if (!isNewStudy()
            && user.getVDCRole(study.getOwner())!=null
                    && user.getVDCRole(study.getOwner()).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR)
                    && study.getReviewState().getName().equals(ReviewStateServiceLocal.REVIEW_STATE_RELEASED)) {
                study.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW));
            }
            
            // check newFiles list; if > 0, we are coming from addPage
            if (newFiles.size() > 0) {
                addFiles(user);
                
            } else {
                // otherwise we are coming from edit; check current files for changes
                editFiles();
            }
       
            studyService.saveStudy(study, userId);
            
            // if new, register the handle
            if ( isNewStudy() && vdcNetworkService.find().isHandleRegistration() ) {
                String handle = study.getAuthority() + "/" + study.getStudyId();
                gnrsService.createHandle(handle);
            }
            
            if (study.getId() == null) {
                // we need to flush to get the id for the indexer
                em.flush();
            }
            em.flush(); // Always call flush(), so that we can detect an OptimisticLockException
            indexService.updateStudy(study.getId());
        } catch(EJBException e) {
            System.out.println("EJBException "+e.getMessage()+" saving study "+study.getId()+" edited by " + user.getUserName() + " at "+ new Date().toString());
            e.printStackTrace();
            throw e;
        
        }
            
      
    }
    
    
    /**
     * Remove this Stateful Session bean from the EJB Container without
     * saving updates to the database.
     */
    @Remove
    public void cancel() {
        
    }
    
    
    private void addFiles(VDCUser user) {
        // step 0: start with some initialization
        File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        
        // step 1: divide the files, based on subsettable or not
        List subsettableFiles = new ArrayList();
        List otherFiles = new ArrayList();
        
        Iterator iter = newFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            if ( fileBean.getStudyFile().isSubsettable() ) {
                subsettableFiles.add(fileBean);
            } else {
                otherFiles.add(fileBean);
                // also add to category, so that it will be flushed for the ids
                addFileToCategory(fileBean.getStudyFile(), fileBean.getFileCategoryName(), study);
            }
        }
        
        // step 2: iterate through nonsubsettable files, moving from temp to new location
        iter = otherFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            StudyFile f = fileBean.getStudyFile();
            File tempFile = new File(fileBean.getTempSystemFileLocation());
            File newLocationFile = new File(newDir, f.getFileSystemName());
            try {
                FileUtil.copyFile( tempFile, newLocationFile );
                tempFile.delete();
                f.setFileSystemLocation(newLocationFile.getAbsolutePath());
            } catch (IOException ex) {
                throw new EJBException(ex);
            }
        }
        
        // step 3: iterate through subsettable files, sending a message via JMS
        if (subsettableFiles.size() > 0) {
            QueueConnection conn = null;
            QueueSession session = null;
            QueueSender sender = null;
            try {
                conn = factory.createQueueConnection();
                session = conn.createQueueSession(false,0);
                sender = session.createSender(queue);
                
                DSBIngestMessage ingestMessage = new DSBIngestMessage();
                ingestMessage.setFileBeans(subsettableFiles);
                ingestMessage.setIngestEmail(ingestEmail);
                ingestMessage.setIngestUserId(user.getId());
                ingestMessage.setStudyId(study.getId());
                Message message = session.createObjectMessage(ingestMessage);
                sender.send(message);
                
                String detail = "Ingest processing for " + subsettableFiles.size() + " file(s).";
                studyService.addStudyLock(study.getId(), user.getId(), detail);
                
                // send an e-mail
                mailService.sendIngestRequestedNotification(ingestEmail, subsettableFiles);
                
                
            } catch (JMSException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (sender != null) {sender.close();}
                    if (session != null) {session.close();}
                    if (conn != null) {conn.close();}
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void editFiles() {
        boolean recalculateStudyUNF = false;
        List filesToBeDeleted = new ArrayList();
        
        Iterator iter = currentFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean file = (StudyFileEditBean) iter.next();
            StudyFile f = em.find(StudyFile.class,file.getStudyFile().getId());
            if (file.isDeleteFlag()) {
                f.getAllowedGroups().clear();
                f.getAllowedUsers().clear();
                removeCollectionElement(f.getFileCategory().getStudyFiles(),f);
                recalculateStudyUNF = f.isSubsettable() ? true : recalculateStudyUNF;
                filesToBeDeleted.add(f);
                
            } else {
                if (!f.getFileCategory().getName().equals(file.getFileCategoryName()) ) {
                    // move to new cat
                    f.getFileCategory().getStudyFiles().remove(f);
                    addFileToCategory(file.getStudyFile(), file.getFileCategoryName(), study);
                }
            }
        }
        
        // now delete categories that no longer have files
        Iterator catIter = study.getFileCategories().iterator();
        while (catIter.hasNext()) {
            FileCategory cat = (FileCategory) catIter.next();
            if (cat.getStudyFiles().size() == 0) {
                removeCollectionElement(catIter,cat);
            }
        }
        
        // and recalculate study UNF, if needed
        if (recalculateStudyUNF) {
            try {
                study.setUNF( new DSBWrapper().calculateUNF(study) );
            } catch (IOException e) {
                throw new EJBException("Could not calculate new study UNF");
            }
        }
        
        // finally delete the physical files
        Iterator tbdIter = filesToBeDeleted.iterator();
        while (tbdIter.hasNext()) {
            StudyFile f = (StudyFile) tbdIter.next();
            File physicalFile = new File(f.getFileSystemLocation());

            if ( f.isSubsettable() ) {
		// preserved original datafile, if available:
                //File originalPhysicalFile = new File(physicalFile.getParent(), "_" + f.getId().toString());
		File originalPhysicalFile = new File(physicalFile.getParent(), "_" + f.getFileSystemName());
		if ( originalPhysicalFile.exists() ) {
		    originalPhysicalFile.delete();
		}

               // Delete DataVariables and related data thru nativeSQL:
                studyService.deleteDataVariables(f.getDataTable().getId());
             
           
                 
		// and any cached copies of this file in formats other 
		// than tab-delimited: 
		
		for (DataFileFormatType type : studyService.getDataFileFormatTypes()) {
		    File cachedDataFile = new File(f.getFileSystemLocation() + "." + type.getValue());
		    if ( cachedDataFile.exists() ) {
			cachedDataFile.delete();
		    }
		}
            }

	    if ( physicalFile.exists() ) {
		physicalFile.delete();
	    }
        }
    }
    
    /**
     * Creates a new instance of EditStudyServiceBean
     */
    public EditStudyServiceBean() {
    }
    
    public List getCurrentFiles() {
        return currentFiles;
    }
    
    public void setCurrentFiles(List currentFiles) {
        this.currentFiles = currentFiles;
    }
    
    public List getNewFiles() {
        return newFiles;
    }
    
    public void setNewFiles(List newFiles) {
        this.newFiles = newFiles;
    }
    
    
    private void addFileToCategory(StudyFile file, String catName, Study s) {
        
        Iterator iter = s.getFileCategories().iterator();
        while (iter.hasNext()) {
            FileCategory cat = (FileCategory) iter.next();
            if ( cat.getName().equals( catName ) ) {
                file.setFileCategory(cat);
                cat.getStudyFiles().add(file);
                return;
            }
        }
        
        // category was not found, so we create a new file category
        FileCategory cat = new FileCategory();
        cat.setStudy(s);
        s.getFileCategories().add(cat);
        cat.setName( catName );
        cat.setStudyFiles(new ArrayList());
        
        // link cat to file
        file.setFileCategory(cat);
        cat.getStudyFiles().add(file);
    }
    
    
    
    public String getIngestEmail() {
        return ingestEmail;
    }
    
    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }
    
    public boolean isNewStudy() {
        return newStudy;
    }
    
}

