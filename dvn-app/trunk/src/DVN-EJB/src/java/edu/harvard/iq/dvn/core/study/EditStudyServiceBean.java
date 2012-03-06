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

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.ingest.dsb.DSBWrapper;
import edu.harvard.iq.dvn.core.gnrs.GNRSServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateful
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EditStudyServiceBean implements edu.harvard.iq.dvn.core.study.EditStudyService, java.io.Serializable {
    @EJB IndexServiceLocal indexService;
    @EJB MailServiceLocal mailService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB GNRSServiceLocal gnrsService;
    
    @PersistenceContext(type = PersistenceContextType.EXTENDED,unitName="VDCNet-ejbPU")
    EntityManager em;
    @Resource(mappedName="jms/DSBIngest") Queue queue;
    @Resource(mappedName="jms/DSBQueueConnectionFactory") QueueConnectionFactory factory;
    StudyVersion studyVersion;
    private boolean newStudy=false;
    private List currentFiles = new ArrayList();
    private List newFiles = new ArrayList();
    private String ingestEmail;
    
    /**
     *  Initialize the bean with a studyVersion for editing
     */
    public void setStudyVersion(Long studyId ) {
        Study study = em.find(Study.class,studyId);
        if (study==null) {
            throw new IllegalArgumentException("Unknown study id: "+studyId);
        }

        studyVersion = study.getEditVersion();
       
        for (FileMetadata fm: studyVersion.getFileMetadatas()) {
            StudyFileEditBean fileBean = new StudyFileEditBean(fm);
            getCurrentFiles().add(fileBean);

        }
        
   
    }
    
 public void setStudyVersionByGlobalId(String globalId ) {
        Study study = getStudyByGlobalId(globalId);
         if (study==null) {
            throw new IllegalArgumentException("Unknown Global id: "+globalId);
        }

        studyVersion = study.getEditVersion();

        for (FileMetadata fm: studyVersion.getFileMetadatas()) {
            StudyFileEditBean fileBean = new StudyFileEditBean(fm);
            getCurrentFiles().add(fileBean);

        }


    }


    public void newStudy(Long vdcId, Long userId, Long templateId) {
        newStudy=true;
        
        VDC vdc = em.find(VDC.class, vdcId);
        VDCUser creator = em.find(VDCUser.class,userId);
       
      
        
        Study study = new Study(vdc, creator, StudyVersion.VersionState.DRAFT,  em.find(Template.class,templateId));
        em.persist(study);
        
        // set default protocol and authority
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        study.setProtocol(vdcNetwork.getProtocol());
        study.setAuthority(vdcNetwork.getAuthority());

        studyVersion = study.getLatestVersion();
        
    }
    
    public void removeCollectionElement(Collection coll, Object elem) {
        coll.remove(elem);
        em.remove(elem);
    }
     public void removeCollectionElement(List list,int index) {
        System.out.println("index is "+index+", list size is "+list.size());
        em.remove(list.get(index));
        list.remove(index);
    }  
    public void removeCollectionElement(Iterator iter, Object elem) {
        iter.remove();
        em.remove(elem);
    }
    
    public  Study getStudy() {
        return studyVersion.getStudy();
    }
    
    public  StudyVersion getStudyVersion() {
        return studyVersion;
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
            Metadata m = studyVersion.getMetadata();
            if (m.getStudyFieldValues()!=null){
                for (StudyFieldValue sfv : m.getStudyFieldValues()){
                        if (sfv.getId() == null){                                   
                                em.persist(sfv);
                        }
                }                
            }

           
            editFiles();
   
            studyService.saveStudyVersion(studyVersion, userId);

            // if new, register the handle
            if ( isNewStudy() && vdcNetworkService.find().isHandleRegistration() ) {
                String handle = studyVersion.getStudy().getAuthority() + "/" + studyVersion.getStudy().getStudyId();
                gnrsService.createHandle(handle);
               
            }

            
            em.flush(); // Always call flush(), so that we can detect an OptimisticLockException
           
           
        } catch(EJBException e) {
            System.out.println("EJBException "+e.getMessage()+" saving studyVersion "+studyVersion.getId()+" edited by " + user.getUserName() + " at "+ new Date().toString());
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
    
    /*
    private void addFiles(VDCUser user)  {
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
                
                String detail = "Ingest processing for " + subsettableFiles.size() + " file(s).";
                studyService.addStudyLock(study.getId(), user.getId(), detail);
                try {
                    sender.send(message);
                } catch(Exception ex) {
                    // If anything goes wrong, remove the study lock.
                    studyService.removeStudyLock(study.getId());
                    ex.printStackTrace();
                }
                 
                  
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
    */
    
    private void editFiles() {
        boolean recalculateStudyUNF = false;
        List filesToBeDeleted = new ArrayList();
        
        Iterator iter = currentFiles.iterator();
        while (iter.hasNext()) {
            StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
            StudyFile f = em.find(StudyFile.class,fileBean.getStudyFile().getId());
            if (fileBean.isDeleteFlag()) {

                recalculateStudyUNF = f.isUNFable() ? true : recalculateStudyUNF;
                // If there is only one study version that points to the file,
                // delete the file metadata and the file.  
                // Else, just delete the file metadata.
                if (f.getFileMetadatas().size()==1) {
                    f.getAllowedGroups().clear();
                    f.getAllowedUsers().clear();
                    filesToBeDeleted.add(f);
                }
                studyVersion.getFileMetadatas().remove(fileBean.getFileMetadata());
                em.remove(fileBean.getFileMetadata());
            }
        }

       
        em.flush();
        
        
        // and recalculate study UNF, if needed
        if (recalculateStudyUNF) {
            try {
                studyVersion.getMetadata().setUNF( new DSBWrapper().calculateUNF(studyVersion) );
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

               // TODO: Delete DataVariables and related data thru nativeSQL:
             
           
                 
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
    
    
   
    
    
    public String getIngestEmail() {
        return ingestEmail;
    }
    
    public void setIngestEmail(String ingestEmail) {
        this.ingestEmail = ingestEmail;
    }
    
    public boolean isNewStudy() {
        return newStudy;
    }
    
    public void changeTemplate(Long templateId) {
        Template newTemplate = em.find(Template.class, templateId);
        
        // we have to clear out the studyFieldValues from the transient StudyField list
        // (otherwise they hang on to the metadata object)
        for (StudyField sf : studyVersion.getMetadata().getStudyFields()) {
            sf.getStudyFieldValues().clear();    
        }
        // then remove the existing metadata from study version
        em.remove(studyVersion.getMetadata());
        
        // Copy Template Metadata into Study Metadata
        studyVersion.setMetadata(new Metadata(newTemplate.getMetadata(), false, false));
        studyVersion.getStudy().setTemplate(newTemplate);

        // prefill date of deposit
        studyVersion.getMetadata().setDateOfDeposit( new SimpleDateFormat("yyyy-MM-dd").format( new Date() ) );
        // Add empty row for entering data in currently empty collections
        studyVersion.getMetadata().initCollections();
    }
    
    private void clearCollection(Collection collection) {
        if (collection!=null) {
            for (Iterator it = collection.iterator(); it.hasNext();) {
                Object elem =  it.next();          
                it.remove();
                em.remove(elem);
            }
        }
    }

    public Study getStudyByGlobalId(String globalId) {
        return studyService.getStudyByGlobalId(globalId);
    }
    
}

