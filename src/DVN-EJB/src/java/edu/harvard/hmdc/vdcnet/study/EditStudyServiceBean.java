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
public class EditStudyServiceBean implements edu.harvard.hmdc.vdcnet.study.EditStudyService {
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
        for (Iterator catIt = studyService.getOrderedFileCategories(study.getId()).iterator(); catIt.hasNext();) {
            FileCategory fc = (FileCategory) catIt.next();
            for (Iterator fileIt = studyService.getOrderedFilesByCategory(fc.getId()).iterator(); fileIt.hasNext();) {
                StudyFile sf = (StudyFile) fileIt.next();
                StudyFileEditBean fileBean = new StudyFileEditBean(em.find(StudyFile.class,sf.getId()));
                fileBean.setFileCategoryName(sf.getFileCategory().getName());
                getCurrentFiles().add(fileBean);
                
            }
            
        }
        /*
        Iterator iter = study.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile sf = (StudyFile) iter.next();
            StudyFileEditBean fileBean = new StudyFileEditBean(sf);
            fileBean.setFileCategoryName(sf.getFileCategory().getName());
            getCurrentFiles().add(fileBean);
        }
         */
    }
    
    public void newStudy(Long vdcId, Long userId) {
        newStudy=true;
        VDC vdc = em.find(VDC.class, vdcId);
        if (vdc==null) {
            throw new IllegalArgumentException("Unknown VDC id: "+vdcId);
        }
        VDCNetwork vdcNetwork = vdcNetworkService.find();
        
        study = new Study();
        em.persist(study);
        
        study.setOwner(vdc);
        VDCUser creator = em.find(VDCUser.class,userId);
        study.setCreator(creator);
        study.setCreateTime( new Date() );
        study.setProtocol(vdcNetwork.getProtocol());
        study.setAuthority(vdcNetwork.getAuthority());
        
        
        
        
        // If user is network admin, or has a vdc role > contributor, set the study to IN REVIEW
        // else (user is a contributor), set the study to NEW
        if ((creator.getNetworkRole()!=null && creator.getNetworkRole().getName().equals(NetworkRoleServiceLocal.ADMIN))
        || (creator.getVDCRole(vdc)!=null && !creator.getVDCRole(vdc).getRole().getName().equals(RoleServiceLocal.CONTRIBUTOR))) {
            study.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_IN_REVIEW));
        } else  {
            study.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_NEW));
        }
        study.setTemplate(vdc.getDefaultTemplate());
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
            // Add Study to root collection of it's VDC owner.
            if (study.getOwner()!=null) {
                if (!study.getStudyColls().contains(study.getOwner().getRootCollection())) {
                    study.getStudyColls().add(study.getOwner().getRootCollection());
                    study.getOwner().getRootCollection().getStudies().add(study);
                }
            }
            study.setLastUpdateTime(new Date());
            study.setLastUpdater(user);
            
            
            
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
            
            setDisplayOrders();
            // Call flush so we can use the study primary key to call the Indexer
            em.flush();
            
            
            
            
            
            indexService.updateStudy(study.getId());
        } catch (EJBException e) {
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
    
    private void setDisplayOrders() {
        int i=0;
        for (Iterator it = study.getStudyAuthors().iterator(); it.hasNext();) {
            StudyAuthor elem = (StudyAuthor) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyAbstracts().iterator(); it.hasNext();) {
            StudyAbstract elem = (StudyAbstract) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyDistributors().iterator(); it.hasNext();) {
            StudyDistributor elem = (StudyDistributor) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyGeoBoundings().iterator(); it.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) it.next();
            elem.setDisplayOrder(i);
            i++;
            
        }
        i=0;
        for (Iterator it = study.getStudyGrants().iterator(); it.hasNext();) {
            StudyGrant elem = (StudyGrant) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyKeywords().iterator(); it.hasNext();) {
            StudyKeyword elem = (StudyKeyword) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyNotes().iterator(); it.hasNext();) {
            StudyNote elem = (StudyNote) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudyOtherIds().iterator(); it.hasNext();) {
            StudyOtherId elem = (StudyOtherId) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudyProducers().iterator(); it.hasNext();) {
            StudyProducer elem = (StudyProducer) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        i=0;
        for (Iterator it = study.getStudySoftware().iterator(); it.hasNext();) {
            StudySoftware elem = (StudySoftware) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        i=0;
        for (Iterator it = study.getStudyTopicClasses().iterator(); it.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) it.next();
            elem.setDisplayOrder(i);
            i++;
        }
        
        
        
        
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
                studyService.addStudyLock(study, user, detail);
                
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
            StudyFile f = file.getStudyFile();
            if (file.isDeleteFlag()) {
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
            physicalFile.delete();
            if ( f.isSubsettable() ) {
                File originalPhysicalFile = new File(physicalFile.getParent(), "_" + f.getId().toString());
                originalPhysicalFile.delete();
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
    
    
    public void importHarvestStudy(File metadataFile) {
        importStudy(metadataFile, false, false,true);
    }
    
    public void importLegacyStudy(File xmlFile) {
        importStudy(xmlFile,true, true, false);
    }
  /*  
    public void importStudy( Node xmlNode, boolean checkRestrictions, boolean generateStudyId, boolean allowUpdates ) {
        
        // now import
        doImportStudy( xmlNode, checkRestrictions, generateStudyId,allowUpdates );
        
        // last create XML file for storage
        // create, if needed, the directory
        File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            
            DOMSource source = new DOMSource(xmlNode);
            FileOutputStream out = new FileOutputStream( new File(newDir, "original_imported_study.xml") );
            StreamResult result = new StreamResult( out );
            transformer.transform(source, result);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = "ImportStudy failed: ";
            if (ex.getMessage()!=null) {
                msg+=ex.getMessage();
            }
            EJBException e = new EJBException(msg );
            e.initCause(ex);
            throw e;
        }
        
    }
    
    */
    public void importStudy( File xmlFile, boolean checkRestrictions, boolean generateStudyId, boolean allowUpdates ) {
        
        doImportStudy( xmlFile, checkRestrictions, generateStudyId, allowUpdates );
        
        // lastly, copy XML file
        try {
            // create, if needed, the directory
            File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            FileUtil.copyFile( xmlFile, new File(newDir, "original_imported_study.xml"));
        } catch (IOException ex) {
            ex.printStackTrace();
            String msg = "ImportStudy failed: ";
            if (ex.getMessage()!=null) {
                msg+=ex.getMessage();
            }
            EJBException e = new EJBException(msg );
            e.initCause(ex);
            throw e;
        }
    }
    
    public void doImportStudy( Object obj, boolean checkRestrictions, boolean generateStudyId,  boolean allowUpdates ) {
        CodeBook _cb = null;
        if (obj instanceof CodeBook) {
            _cb = (CodeBook)obj;
        } else if (obj instanceof Node || obj instanceof File ) {
            // first unmarshall the XML
            try {
                JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.ddi20");
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                if (obj instanceof Node) {
                    _cb  = (CodeBook) unmarshaller.unmarshal( (Node)obj );
                } else {
                    Object unmarshalObj= unmarshaller.unmarshal( (File)obj );
                    _cb  = (CodeBook)unmarshalObj; 
                  
                }
            } catch(JAXBException ex) {
                EJBException e = new EJBException("Import Study failed: "+ex.getMessage() );
                e.initCause(ex);
                throw e;
            }
            
                   
        } else {
            throw new EJBException("Invalid type for parameter obj: "+obj.getClass().getName()+". obj must instance of Node or File.");
        }
        
      
        
        
        ddiService.mapDDI( _cb, study, allowUpdates );
        _cb=null;
        System.gc();
        
        if (study.getStudyId()==null || study.getStudyId().equals("")) {
            if (generateStudyId) {
                VDCNetwork vdcNetwork = vdcNetworkService.find();
                study.setProtocol(vdcNetwork.getProtocol());
                study.setAuthority(vdcNetwork.getAuthority());
                study.setStudyId(studyService.generateStudyIdSequence(study.getProtocol(),study.getAuthority()));
            } else {
                throw new EJBException("ImportStudy failed: DDI does not specify a handle for this study.");
            }
        }
        
        // set to released
        ReviewState released = reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED);
        study.setReviewState(released);
        
        // If study comes from old VDC, set restrictions based on  restrictions in VDC repository
        if (checkRestrictions) {
            setImportedStudyRestrictions(study);
        }
    }
    
    @Remove
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void retrieveFilesAndSave( Long vdcId, Long userId ) {
        try {
            
            // create, if needed, the directory
            File newDir = new File(FileUtil.getStudyFileDir(), study.getAuthority() + File.separator + study.getStudyId());
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
            
            // flush to create IDs
            em.flush();
            
            Iterator iter = study.getStudyFiles().iterator();
            while (iter.hasNext()) {
                StudyFile file = (StudyFile) iter.next();
                File inputFile = determineLegacyFile( file );
                file.setFileSystemName( inputFile.getName() );
                File outputFile = new File(newDir, file.getFileSystemName() );
                FileUtil.copyFile( inputFile, outputFile );
                file.setFileSystemLocation( outputFile.getAbsolutePath() );
                if (file.isSubsettable()) {
                    file.setFileType( "text/tab-separated-values" );
                } else {
                    // we need to incorprate something like JHOVE to determine otherMat filetypes
                }
            }
            
            save(vdcId, userId);
            
        } catch (IOException ex) {
            ex.printStackTrace();
            EJBException e= new EJBException("RetrieveFilesAndSave failed: " + ex.getMessage() );
            e.initCause(ex);
            throw e;
        }
    }
    
    
    
    
    private File determineLegacyFile(StudyFile file) {
        
        String legacyFileDir = FileUtil.getLegacyFileDir();
        
        int startIndex = file.getFileSystemLocation().indexOf("/hdl:") + 5;
        String parsedFileLocation = file.getFileSystemLocation().substring( startIndex );
        
        // this line is just for when testing on a windows machine!
        parsedFileLocation = parsedFileLocation.replace('/', File.separatorChar);
        
        File legacyFile = new File(legacyFileDir, parsedFileLocation);
        
        return legacyFile;
    }
    
    
    private void setImportedStudyRestrictions(Study study) {
        RepositoryWrapper repositoryWrapper = new RepositoryWrapper();
        try {
            String studyObjectHandle = study.getProtocol()+":"+study.getAuthority()+"/"+study.getStudyId();
            if (repositoryWrapper.isObjectRestricted(studyObjectHandle)) {
                study.setRestricted(true);
            }
            for (Iterator it = study.getFileCategories().iterator(); it.hasNext();) {
                FileCategory cat = (FileCategory) it.next();
                for (Iterator it2 = cat.getStudyFiles().iterator(); it2.hasNext();) {
                    StudyFile studyFile = (StudyFile) it2.next();
                    String fileSystemName = determineLegacyFile(studyFile).getName();
                    if (repositoryWrapper.isObjectRestricted(studyObjectHandle+"/"+fileSystemName)) {
                        studyFile.setRestricted(true);
                    }
                }
                
            }
        } catch (IOException e) {
            EJBException ex = new EJBException("Exception setting restrictions for study "+study.getGlobalId());
            ex.initCause(e);
            throw ex;
            
            
        } catch (SAXException e) {
            EJBException ex = new EJBException("Exception setting restrictions for study "+study.getGlobalId());
            ex.initCause(e);
            throw ex;
            
        }
        
        
    }
    
    
    private void clearStudy() {
        // should this be done with bulk deletes??
        
        for (Iterator iter = study.getFileCategories().iterator(); iter.hasNext();) {
            FileCategory elem = (FileCategory) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyAbstracts().iterator(); iter.hasNext();) {
            StudyAbstract elem = (StudyAbstract) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyAuthors().iterator(); iter.hasNext();) {
            StudyAuthor elem = (StudyAuthor) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyDistributors().iterator(); iter.hasNext();) {
            StudyDistributor elem = (StudyDistributor) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyGeoBoundings().iterator(); iter.hasNext();) {
            StudyGeoBounding elem = (StudyGeoBounding) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyGrants().iterator(); iter.hasNext();) {
            StudyGrant elem = (StudyGrant) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyKeywords().iterator(); iter.hasNext();) {
            StudyKeyword elem = (StudyKeyword) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyNotes().iterator(); iter.hasNext();) {
            StudyNote elem = (StudyNote) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyOtherIds().iterator(); iter.hasNext();) {
            StudyOtherId elem = (StudyOtherId) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyOtherRefs().iterator(); iter.hasNext();) {
            StudyOtherRef elem = (StudyOtherRef) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyProducers().iterator(); iter.hasNext();) {
            StudyProducer elem = (StudyProducer) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelMaterials().iterator(); iter.hasNext();) {
            StudyRelMaterial elem = (StudyRelMaterial) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelPublications().iterator(); iter.hasNext();) {
            StudyRelPublication elem = (StudyRelPublication) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyRelStudies().iterator(); iter.hasNext();) {
            StudyRelStudy elem = (StudyRelStudy) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudySoftware().iterator(); iter.hasNext();) {
            StudySoftware elem = (StudySoftware) iter.next();
            removeCollectionElement(iter,elem);
        }
        for (Iterator iter = study.getStudyTopicClasses().iterator(); iter.hasNext();) {
            StudyTopicClass elem = (StudyTopicClass) iter.next();
            removeCollectionElement(iter,elem);
        }
    }
    
}

