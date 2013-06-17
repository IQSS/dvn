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
 * IndexServiceBean.java
 *
 * Created on September 26, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.index;

import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.index.DvnQuery;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
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
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class IndexServiceBean implements edu.harvard.iq.dvn.core.index.IndexServiceLocal {

    @Resource
    javax.ejb.TimerService timerService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCCollectionServiceLocal collService;
    @EJB
    MailServiceLocal mailService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @Resource(mappedName = "jms/IndexMessage")
    Queue queue;
    @Resource(mappedName = "jms/IndexMessageFactory")
    QueueConnectionFactory factory;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.index.IndexServiceBean");
    private static final String INDEX_TIMER = "IndexTimer";
    private static final String COLLECTION_INDEX_TIMER = "CollectionIndexTimer";
    private static final String INDEX_NOTIFICATION_TIMER = "IndexNotificationTimer";


    static {
        try {
            logger.addHandler(new FileHandler(FileUtil.getImportFileDir() + File.separator + "index.log"));
        } catch (IOException e) {


            throw new EJBException(e);
        }
    }

    /**
     * Creates a new instance of IndexServiceBean
     */
    public IndexServiceBean() {
    }

    public void createIndexTimer() {
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo().equals(INDEX_TIMER)) {
                logger.info("Cannot create IndexTimer, timer already exists.");
                logger.info("IndexTimer next timeout is " + timer.getNextTimeout());
                return;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 1);

        logger.log(Level.INFO, "Indexer timer set for " + cal.getTime());
        Date initialExpiration = cal.getTime();  // First timeout is 1:00 AM of next day
        long intervalDuration = 1000 * 60 * 60 * 24;  // repeat every 24 hours
        timerService.createTimer(initialExpiration, intervalDuration, INDEX_TIMER);

    }

    public void createCollectionIndexTimer() {
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo().equals(COLLECTION_INDEX_TIMER)) {
                logger.info("Cannot create COllectionIndexTimer, timer already exists.");
                logger.info("IndexTimer next timeout is " + timer.getNextTimeout());
                return;
            }
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5); // First run 5 minutes from now

        Date initialRun = cal.getTime();
        
        long intervalInMinutes = 60; // default value
      
        String intervalJVMOption = System.getProperty("dvn.index.collection.reindex.interval"); 
        
        if (intervalJVMOption != null) {
            Long intervalValue = null; 
            try {
                intervalValue = new Long (intervalJVMOption);
            } catch (Exception ex) {}
            if (intervalValue != null && (intervalValue.longValue() > 0L)) {
                intervalInMinutes = intervalValue.longValue();
            }
        }    
        
        long intervalDuration = 1000 * 60 * intervalInMinutes; 
        timerService.createTimer(initialRun, intervalDuration, COLLECTION_INDEX_TIMER);
        logger.log(Level.INFO, "Collection index timer set for " + initialRun);

    }
    
    public void createIndexNotificationTimer() {
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo().equals(INDEX_NOTIFICATION_TIMER)) {
                logger.info("Cannot create IndexNotificationTimer, timer already exists.");
                logger.info("IndexNotificationTimer next timeout is " + timer.getNextTimeout());
                return;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 15);

        logger.log(Level.INFO, "Indexer notification timer set for " + cal.getTime());
        Date initialExpiration = cal.getTime();  // First timeout is 1:00 AM of next day
        long intervalDuration = 1000 * 60 * 60 * 24;  // repeat every 24 hours
        timerService.createTimer(initialExpiration, intervalDuration, INDEX_NOTIFICATION_TIMER);

    }

    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer) {
        System.out.println("in handleTimeout, timer = "+timer.getInfo());
        
        try {
            // read-only mode check:
            boolean readOnly = vdcNetworkService.defaultTransactionReadOnly();

            if (timer.getInfo().equals(INDEX_TIMER)) {
                if (readOnly) {
                    logger.log(Level.ALL, "Network is in read-only mode; skipping scheduled index job.");
                } else {
                    logger.log(Level.INFO, "Index update");
                    indexBatch();
                }
            } else if (timer.getInfo().equals(INDEX_NOTIFICATION_TIMER)) {
                if (readOnly) {
                    logger.log(Level.ALL, "Network is in read-only mode; skipping scheduled index notification.");
                } else {
                    logger.log(Level.INFO, "Index notify");
                    indexProblemNotify();
                } 
            } else if (timer.getInfo().equals(COLLECTION_INDEX_TIMER)) {
                if (readOnly) {
                    logger.log(Level.ALL, "Network is in read-only mode; skipping scheduled collection reindexing.");
                } else {
                    logger.log(Level.INFO, "Collection ReIndex");
                    updateStudiesInCollections ();
                } 
            }
        } catch (Throwable e) {
            mailService.sendIndexUpdateErrorNotification(vdcNetworkService.find().getSystemEmail(), vdcNetworkService.find().getName());
            e.printStackTrace();
        }
    }

    public void indexStudy(long studyId) {
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.ADD);
        sendMessage(op);
    }

    private void handleIOProblems(boolean ioProblem, Long ioProblemCount) {
        if (ioProblem) {
            try {
                mailService.sendDoNotReplyMail(vdcNetworkService.find().getSystemEmail(), "IO problem", ioProblemCount +" studies may not have been indexed" + InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ex) {
                Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void indexProblemNotify() {
        List<Study> studies = (List<Study>) em.createQuery("SELECT s from Study s where s.lastIndexTime < s.lastUpdateTime OR s.lastIndexTime is NULL").getResultList();
        if (studies.size() > 0) {
            mailService.sendIndexErrorNotification(vdcNetworkService.find().getSystemEmail(), vdcNetworkService.find().getName(), studies.size());
        }
    }

    private void sendMessage(final IndexEdit op) {
        QueueConnection conn = null;
        QueueSession session = null;
        QueueSender sender = null;
        try {
            conn = factory.createQueueConnection();
            session = conn.createQueueSession(false, 0);
            sender = session.createSender(queue);

            Message message = session.createObjectMessage(op);
            sender.send(message);


        } catch (JMSException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (sender != null) {
                    sender.close();
                }
                if (session != null) {
                    session.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateStudy(long studyId) {
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.UPDATE);
        sendMessage(op);
    }

    public void deleteStudy(long studyId) {
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.DELETE);
        sendMessage(op);
    }

    private void addDocument(final long studyId) throws IOException {
        Date indexTime = new Date();
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();

        indexer.addDocument(study);
        try {
            studyService.setIndexTime(studyId, indexTime);
        } catch (Exception e) {
            logger.warning("caught an exception trying to update index time.");
            //e.printStackTrace(); // print stacktrace, but continue processing
        }
    }
    
    private void addDocument(Study study) throws IOException {
        Date indexTime = new Date();
        Indexer indexer = Indexer.getInstance();
        indexer.addDocument(study);
        try {
            studyService.setIndexTime(study.getId(), indexTime);
        } catch (Exception e) {
            logger.warning("caught an exception trying to update index time.");
            //e.printStackTrace(); // print stacktrace, but continue processing
        }
    }

    private void deleteDocument(final long studyId) {
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(study.getId().longValue());
    }

    public void indexAll() throws IOException {
        boolean ioProblem = false;
        long ioProblemCount = 0;
        Indexer indexer = Indexer.getInstance();
        
        String dvnIndexLocation = System.getProperty("dvn.index.location");
        String lockFileName = dvnIndexLocation + "/IndexAll.lock";
        File indexAllLockFile = new File(lockFileName);

        // Before we do anything else, 
        // Check for an existing lock file: 


        if (indexAllLockFile.exists()) {
            String errorMessage = "Cannot reindex: collection reindexing already in progress;";
            errorMessage += ("lock file " + lockFileName + ", created on " + (new Date(indexAllLockFile.lastModified())).toString() + ".");
            logger.warning(errorMessage);
            throw new IOException(errorMessage);
        }

        // Create a lock file: 
        try {
            indexAllLockFile.createNewFile();
        } catch (IOException ex) {
            String errorMessage = "Error: could not create lock file (";
            errorMessage += (lockFileName + ")");
            throw new IOException(errorMessage);
        }
        
        List<Long> studyIds = studyService.getAllStudyIds(); 
        
        Long studyId = null; 
        Study study = null; 
        int count = 0;
        
        if (studyIds != null) {
            logger.info("IndexAll: Re-indexing "+(studyIds.size())+" studies.");

            for (Iterator it = studyIds.iterator(); it.hasNext();) {
                studyId = (Long) it.next();
                
                if (studyId != null) {
                    study = studyService.getStudy(studyId);
                    if (study != null) {
                        try {
                            addDocument(study);
                            count++;
                        } catch (Exception ex) {
                            ioProblem = true;
                            ioProblemCount++;
                            Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                            logger.severe("Caught exception trying to reindex study " + study.getId());
                        }
                    }
                
                    if ((count % 100) == 0) {
                        logger.fine("Processed "+count+" studies; last processed id: "+studyId.toString());
                    }
                }

                study = null;
                studyId = null; 
             
            }
            
        }
        logger.info("IndexAll: Finished.");
        if (indexAllLockFile.exists()) {
            indexAllLockFile.delete();
        }
        handleIOProblems(ioProblem, ioProblemCount);
    }
    
    public void indexList(List<Long> studyIds) {
        long ioProblemCount = 0;
        boolean ioProblem = false;
        Indexer indexer = Indexer.getInstance();
        /*
        try {
        indexer.setup();
        } catch (IOException ex) {
        ex.printStackTrace();
        }
         */
        for (Iterator it = studyIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            try {
                addDocument(elem.longValue());
            } catch (IOException ex) {
                ioProblem = true;
                ioProblemCount++;
                Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        handleIOProblems(ioProblem, ioProblemCount);
    }

    public void updateIndexList(List<Long> studyIds) {
        long ioProblemCount = 0;
        boolean ioProblem = false;
        Indexer indexer = Indexer.getInstance();
        boolean deleteSuccess = true;
        /*
        try {
        indexer.setup();
        } catch (IOException ex) {
        ex.printStackTrace();
        }
         */
        for (Iterator it = studyIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            try {
                deleteSuccess = true; 
                try {
                    indexer.deleteDocumentCarefully(elem.longValue());
                } catch (IOException ioe) {
                    deleteSuccess = false;
                }
                if (deleteSuccess) {
                    try {
                        addDocument(elem.longValue());
                    } catch (IOException ex) {
                        ioProblem = true;
                        ioProblemCount++;
                        Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (EJBException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    System.out.println("Study id " + elem.longValue() + " not found");
                    e.printStackTrace();
                } else {
                    throw e;
                }
            }
        }
        handleIOProblems(ioProblem,ioProblemCount);

    }
    
    
    public void updateStudiesInCollections () {
        long ioProblemCount = 0;
        boolean ioProblem = false;
        Indexer indexer = Indexer.getInstance();
        
        String dvnIndexLocation = System.getProperty("dvn.index.location");
        
        String lockFileName = dvnIndexLocation + "/IndexAll.lock";
        File indexAllLockFile = new File(lockFileName);
        
        // Before we do anything else, check if the index directory is
        // locked for IndexAll: 
        
        if (indexAllLockFile.exists()) {
            logger.info("Detected IndexAll in progress; skipping reindexing ofn collection-linked studies.");
            return; 
        }

        logger.info("Starting batch reindex of collection-linked studies.");

        lockFileName = dvnIndexLocation + "/collReindex.lock";
        File collReindexLockFile = new File(lockFileName);

        
        try {
            // Check for an existing lock file: 

            
            if (collReindexLockFile.exists()) {
                String errorMessage = "Cannot reindex: collection reindexing already in progress;";
                errorMessage += ("lock file " + lockFileName + ", created on " + (new Date(collReindexLockFile.lastModified())).toString() + ".");
                throw new IOException(errorMessage);
            }

            // Create a lock file: 
            try {
                collReindexLockFile.createNewFile();
            } catch (IOException ex) {
                String errorMessage = "Error: could not create lock file (";
                errorMessage += (lockFileName + ")");
                throw new IOException(errorMessage);
            }
            
            List<Long> vdcIdList = vdcService.findAllIds();
            logger.fine("Found "+vdcIdList.size()+" dataverses."); 
            
            Long maxStudyId = studyService.getMaxStudyTableId(); 
            
            if (maxStudyId == null) {
                throw new IOException("Could not determine the last database id in the study table.");
            }
            
            if (maxStudyId.intValue() != maxStudyId.longValue()) {
                logger.severe("There appears to be more than 2^^31 objects in the study table; the subnetwork cross-indexing hack isn't going to work.");
                throw new IOException("There appears to be more than 2^^31 objects in the study table; the subnetwork cross-indexing hack isn't going to work.");
                /* This is quite unlikely to happen, but still... */
            }
            
            ArrayList<VDCNetwork> subNetworks = getSubNetworksAsArray(); //vdcNetworkService.getVDCSubNetworks();
            // This is an array of [sub]networks organized by *network id*; 
            // i.e., if there are subnetworks with the ids 0, 2 and 5 the array 
            // will contain {0, NULL, network_2, NULL, NULL, network_5}
                        
            if (subNetworks == null || (subNetworks.size() < 1)) {
                // No subnetworks in this DV Network; nothing to do. 
                logger.fine("There's only one network in the DVN; nothing to do. Exiting");
                return; 
            }
            
            int maxSubnetworkId = subNetworks.size() - 1; 

            if (maxSubnetworkId > 63) {
                logger.severe("There are more than 63 VDC (sub)networks. The subnetwork cross-indexing hack isn't going to work."+
                        "(we are using longs as bitstrings to store network cross-linked status of a study)");
                throw new IOException("There are more than 63 VDC (sub)networks. The subnetwork cross-indexing hack isn't going to work."+
                        "(we are using longs as bitstrings to store network cross-linked status of a study)");
                /* Not very likely to happen either... */
            }
            
            
            
            long linkedVdcNetworkMap[] = new long[maxStudyId.intValue()+1];
            Long vdcId = null; 
            VDC vdc = null; 
            List<Long> linkedStudyIds = null;
            Long vdcNetworkId = null; 
            Long studyNetworkId = null;
            Study linkedStudy = null; 
            
            for (Iterator it = vdcIdList.iterator(); it.hasNext();) {
                vdcId = (Long) it.next();
                vdc = vdcService.findById(vdcId);

                if (vdc != null && vdc.getVdcNetwork() != null) {
                    vdcNetworkId = vdc.getVdcNetwork().getId();

                    if (vdcNetworkId.longValue() > 0) {
                        // We are not interested in the VDCs in the top-level
                        // network (network id 0); because the top-level network
                        // already contains all the studies in it. Whatever 
                        // studies the dynamic collections may be linking, they 
                        // are still in the same DVN. 
                        linkedStudyIds = indexer.findStudiesInCollections(vdc);

                        if (linkedStudyIds != null) {
                            logger.fine("Found "+linkedStudyIds.size()+" linked studies in VDC "+vdc.getId()+", subnetwork "+vdcNetworkId.toString());

                            for (Long studyId : linkedStudyIds) {
                                if (studyId.longValue() <= maxStudyId.longValue()) {
                                    // otherwise this is a new study, created since we 
                                    // have started this process; we'll be skipping it,
                                    // this time around. 
                                    try {
                                        linkedStudy = studyService.getStudy(studyId);
                                    } catch (Exception ex) {
                                        linkedStudy = null; 
                                    }
                                    
                                    if (linkedStudy != null) {
                                        studyNetworkId = linkedStudy.getOwner().getVdcNetwork().getId();
                                        if ((studyNetworkId != null) && 
                                                (vdcNetworkId.compareTo(studyNetworkId) != 0)) {
                                            // this study is cross-linked from another VDC network!
                                            logger.fine("Study "+linkedStudy.getId()+" from subnetwork "+studyNetworkId+" is linked to this VDC ("+vdc.getId()+").");

                                            linkedVdcNetworkMap[linkedStudy.getId().intValue()] |= (1 << vdcNetworkId.intValue());
                                        }
                                    }
                                    linkedStudy = null;
                                    studyNetworkId = null;
                                }
                            }
                        }
                        linkedStudyIds = null;
                        vdcNetworkId = null;
                    }
                }
                vdcId = null;
                vdc = null; 
            }

            // Now go through the list of studies and reindex those for which 
            // the cross-linked status has changed:
            
            logger.fine("Checking the cross-linking status and reindexing the studies for which it has changed:");
            
            List<Long> linkedToNetworkIds = null;
            boolean reindexNecessary = false; 
            
            // Check for the studies that are no longer linked to any foreign
            // subnetworks: 
            
            List<Long> existingLinkedStudies = studyService.getAllLinkedStudyIds();
            
            Long sid = null;
            for (Iterator it = existingLinkedStudies.iterator(); it.hasNext();) {
                sid = (Long) it.next();
                if (linkedVdcNetworkMap[sid.intValue()] == 0) {
                    // study no longer linked to any subnetworks
                    linkedVdcNetworkMap[sid.intValue()] = -1;
                }
            }
            // TODO: would be faster still to retrieve the entire map of crosslinks
            // from the db in a single query here, cook another array of bitstrings
            // and then just go and compare the 2, without making any further 
            // queries... --L.A.
            
            List<VDCNetwork> currentCrossLinks = null; 

            
            for (int i = 0; i < maxStudyId.intValue() + 1; i++) {
                if (linkedVdcNetworkMap[i] != 0) {
                    logger.fine("study "+i+": cross-linked outside of its network; (still need to check if we need to reindex it)");
                    try {
                        linkedStudy = studyService.getStudy(new Long(i));
                    } catch (Exception ex) {
                        linkedStudy = null; 
                    }
                    reindexNecessary = false; 
                    
                    if (linkedStudy != null) {
                        // Only released studies get indexed.
                        // (but studies that are no longer released may 
                        // need to be dropped from the crosslinking map, and
                        // from the index)
                        
                        currentCrossLinks = linkedStudy.getLinkedToNetworks();
                        
                        if (linkedVdcNetworkMap[i] == -1) {
                            // If it's an "unlinked" study,
                            // remove the existing links in the database: 
                            logger.fine("study "+i+" no longer cross-linked to any subnetworks.");
                            //linkedStudy.setLinkedToNetworks(null);
                            linkedStudy = studyService.setLinkedToNetworks(linkedStudy.getId(), null);
                           
                            reindexNecessary = true; 
                        } else if (linkedStudy.isReleased()) {
                            // else find what subnetworks this study is already linked 
                            // to in the database:
                        
                            linkedToNetworkIds = linkedStudy.getLinkedToNetworkIds();
                        
                            long linkedNetworkBitString = produceLinkedNetworksBitstring(linkedToNetworkIds);
                        
                            if (linkedNetworkBitString != linkedVdcNetworkMap[i]) {
                                // This means the cross-linking status of the study has changed!

                                logger.fine("study "+i+": cross-linked status has changed; updating");
                                
                                // Update it in the database: 
                                //linkedStudy.setLinkedToNetworks(newLinkedToNetworks(subNetworks, linkedVdcNetworkMap[i]));
                                linkedStudy = studyService.setLinkedToNetworks(linkedStudy.getId(), newLinkedToNetworks(subNetworks, linkedVdcNetworkMap[i]));

                                //studyService.updateStudy(linkedStudy);
                                
                                reindexNecessary = true; 
                            }
                        }
                        
                        if (reindexNecessary) {
                            // Re-index the study: 
                            
                            indexer = Indexer.getInstance();
                            boolean indexSuccess = true; 
                            try { 
                                indexer.deleteDocumentCarefully(linkedStudy.getId());
                            } catch (IOException ioe) {
                                indexSuccess = false; 
                            }
                            
                            if (indexSuccess) {
                                try {
                                    //indexer.addDocument(linkedStudy);
                                    addDocument(linkedStudy);
                                } catch (Exception ex) {
                                    ioProblem = true;
                                    ioProblemCount++;
                                    logger.severe("Caught exception attempting to re-index re-linked study " + linkedStudy.getId() + "; " + ex.getMessage());
                                    ex.printStackTrace();
                                    indexSuccess = false; 
                                }
                            } else {
                                logger.fine("Could not delete study "+linkedStudy.getId()+" from index; skipping reindexing.");
                            }
                            
                            if (!indexSuccess) {
                                // Make sure we leave the db linking status entry
                                // in the same shape it was before the reindexing
                                // attempt; so that it'll hopefully get caught
                                // by the next reindexing now. 
                                //linkedStudy.setLinkedToNetworks(currentCrossLinks);
                                linkedStudy = studyService.setLinkedToNetworks(linkedStudy.getId(), currentCrossLinks);
                            }
                        }
                    }
                }                
            }
            logger.info("Done reindexing collection-linked studies.");

        } catch (Exception ex) {
            ioProblem = true;
            ioProblemCount++; 
            logger.severe("Caught exception while trying to update studies in collections: "+ex.getMessage());
            ex.printStackTrace();
        } finally {
            // delete the lock file:
            if (collReindexLockFile.exists()) {
                collReindexLockFile.delete();
            }
        }
        
        handleIOProblems(ioProblem, ioProblemCount);
    }
    
    private ArrayList<VDCNetwork> getSubNetworksAsArray () {
        
        List<VDCNetwork> subNetworks = vdcNetworkService.getVDCNetworksOrderedById(); 
        
        if (subNetworks != null) {
            ArrayList subNetworksArray = new ArrayList <VDCNetwork>(); 

            
            for (int i = 0; i < subNetworks.size(); i++) {
                if (subNetworks.get(i) != null) {
                    int insertIndex = subNetworks.get(i).getId().intValue();
                    for (int j = subNetworksArray.size(); j < insertIndex; j++) {
                        logger.fine("padding arraylist with null, "+j);
                        // padding the ArrayList with nulls:
                        subNetworksArray.add(null);
                    }
                    subNetworksArray.add(subNetworks.get(i).getId().intValue(), subNetworks.get(i));
                }
            }
            
            return subNetworksArray; 
        }
        
        return null; 
    }
    
    
    private List<VDCNetwork> newLinkedToNetworks(List<VDCNetwork> subNetworks, long bitString) {
        List<VDCNetwork> newList = null; 
        
        if (bitString != 0 && subNetworks != null) {
            newList = new ArrayList<VDCNetwork>(); 
            
            for (int i=0; i < subNetworks.size(); i++) {
                if ((bitString & (1 << i)) != 0) {
                    if (subNetworks.get(i) != null) {
                        // it should never be null at this point - but won't 
                        // hurt to check anyway. 
                        logger.fine("New linked to network: "+i);
                        newList.add(subNetworks.get(i));
                    }
                }
            }
        }
        
        return newList; 
    }
    
    private long produceLinkedNetworksBitstring(List<Long> linkedToNetworkIds) {
        long bitString = 0; 
        
        if (linkedToNetworkIds != null) {
            for (int i = 0; i < linkedToNetworkIds.size(); i++) {
                int networkId = 0; 
                
                if (linkedToNetworkIds.get(i) != null) {
                    networkId = linkedToNetworkIds.get(i).intValue();
                    bitString |= (1 << networkId);    
                }
            }
        }
        
        return bitString; 
    }

    public void deleteIndexList(List<Long> studyIds) {
        Indexer indexer = Indexer.getInstance();
        /*
        try {
        indexer.setup();
        } catch (IOException ex) {
        ex.printStackTrace();
        }
         */
        for (Iterator it = studyIds.iterator(); it.hasNext();) {
            Long elem = (Long) it.next();
            try {
                deleteDocument(elem.longValue());
            } catch (EJBException e) {
                if (e.getCause() instanceof IllegalArgumentException) {
                    System.out.println("Study id " + elem.longValue() + " not found");
                    e.printStackTrace();
                } else {
                    throw e;
                }
            }
        }
    }

    public List search(String query) {
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(query);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return matchingStudyIds;
    }

    public List search(VDC vdc, List<VDCCollection> searchCollections, List<SearchTerm> searchTerms) {
        Indexer indexer = Indexer.getInstance();
        List<Long> studyIds = getStudiesforCollections(searchCollections);
        
        List matchingStudyIds = null;
        if (studyIds.isEmpty()) {
            matchingStudyIds = new ArrayList();
        } else {
            try {
                matchingStudyIds = indexer.search(studyIds, searchTerms);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return matchingStudyIds;
    }

    public List search(VDC vdc, List<SearchTerm> searchTerms) {
        List studyIds = vdc != null ? listVdcStudyIds(vdc) : null;

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(studyIds, searchTerms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }
    
    public ResultsWithFacets searchNew(DvnQuery dvnQuery) {
        logger.fine("in searchNew in IndexServiceBean");
        VDC vdc = dvnQuery.getVdc();
        List<SearchTerm> searchTerms = dvnQuery.getSearchTerms();
        List studyIds = vdc != null ? listVdcStudyIds(vdc) : null;
        ResultsWithFacets resultsWithFacets = null;
        Indexer indexer = Indexer.getInstance();
        try {
            resultsWithFacets = indexer.searchNew(dvnQuery);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return resultsWithFacets;
    }

    public List<Query> getCollectionQueries(VDC vdc) {
        Indexer indexer = Indexer.getInstance();
        List<Query> collectionQueries = indexer.getCollectionQueries(vdc);
        return collectionQueries;
    }

    public Query constructDvOwnerIdQuery(VDC vdc) {
        Indexer indexer = Indexer.getInstance();
        Query dvOwnerIdQuery = indexer.constructDvOwnerIdQuery(vdc);
        return dvOwnerIdQuery;

    }
    
    public Query constructNetworkIdQuery(Long dvNetworkId) {
        Indexer indexer = Indexer.getInstance();
        Query dvNetworkIdQuery = indexer.constructDvNetworkIdQuery(dvNetworkId);
        return dvNetworkIdQuery;
    }

    public Query constructNetworkOwnerIdQuery(Long dvNetworkId) {
        Indexer indexer = Indexer.getInstance();
        Query dvNetworkIdQuery = indexer.constructDvNetworkOwnerIdQuery(dvNetworkId);
        return dvNetworkIdQuery;
    }
    
    public BooleanQuery andSearchTermClause(List<SearchTerm> studyLevelSearchTerms) {
        Indexer indexer = Indexer.getInstance();
        return indexer.andSearchTermClause(studyLevelSearchTerms);

    }

    public BooleanQuery andQueryClause(List<BooleanQuery> searchParts) {
        Indexer indexer = Indexer.getInstance();
        return indexer.andQueryClause(searchParts);
    }

    private List listVdcStudyIds(final VDC vdc) {
        List studyIds = new ArrayList();

        if (vdc != null) {
            studyIds.addAll(collService.getStudyIds(vdc.getRootCollection()));
            studyIds.addAll( getStudiesforCollections( vdc.getLinkedCollections() ) );
        }
        
        return studyIds;
    }

    public List search(List<Long> studyIds, List<SearchTerm> searchTerms) {

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(studyIds, searchTerms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }

    public List search(SearchTerm searchTerm) {
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }

    /*
     * This version of searchVariables method doesn't seem to be needed any 
     * more; plus it doesn't seem to be implemented properly anyway. 
     * Removing (?). 
    public List searchVariables(SearchTerm searchTerm) {
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = new ArrayList();
        List matchingVarIds = null;
        try {
            matchingVarIds = indexer.searchVariables(searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Map variableMap = new HashMap();
        return matchingStudyIds;
    }
    * */

    public List searchVariables(VDC vdc, SearchTerm searchTerm) {
        List studyIds = vdc != null ? listVdcStudyIds(vdc) : null;
        List<Long> matchingStudyIds = new ArrayList();

        Indexer indexer = Indexer.getInstance();
        List matchingVarIds = null;
        try {
            matchingVarIds = indexer.searchVariables(studyIds, searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Map variableMap = new HashMap();
        return matchingVarIds;
    }

    public List searchVariables(VDC vdc, List<VDCCollection> searchCollections, SearchTerm searchTerm) {
        Indexer indexer = Indexer.getInstance();
        List<Long> studyIds = getStudiesforCollections(searchCollections);

        List matchingStudyIds = null;
        if (studyIds.isEmpty()) {
            matchingStudyIds = new ArrayList();
        } else {
            try {
                matchingStudyIds = indexer.searchVariables(studyIds, searchTerm);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return matchingStudyIds;
    }

    public List searchVariables(List studyIds, SearchTerm searchTerm) {

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.searchVariables(studyIds, searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }

    public List <Long> searchVersionUnf(VDC vdc, String unf){
        List studyIds = vdc != null ? listVdcStudyIds(vdc) : null;

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.searchVersionUnf(studyIds, unf);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }

    public List query(String adhocQuery) {

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.query(adhocQuery);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList() : matchingStudyIds;
    }

    private HashSet<Study> getUnindexedStudies() {
        List<Study> studies = (List<Study>) em.createQuery("SELECT s from Study s where s.lastIndexTime < s.lastUpdateTime OR s.lastIndexTime is NULL").getResultList();
        if (studies!=null) {
            logger.fine("getUnindexedStudies(), found "+studies.size()+" studies.");
        } else {
            logger.fine("getUnindexedStudies(), no studies found.");
        }
        return new HashSet(studies);
    }

    public void indexBatch() {
        long ioProblemCount = 0;
        boolean ioProblem = false;
        HashSet s = getUnindexedStudies();
        for (Iterator it = s.iterator(); it.hasNext();) {
            Study study = (Study) it.next();
            try {
                addDocument(study.getId().longValue());
            } catch (IOException ex) {
                Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                ioProblemCount++;
                ioProblem = true;
            }
        }
        handleIOProblems(ioProblem, ioProblemCount);
    }
    
    private List<Long> getStudiesforCollections(List<VDCCollection> searchCollections) {
        List<Long> studyIds = new ArrayList();
        
        for (VDCCollection coll : searchCollections) {
            studyIds.addAll( collService.getStudyIds( coll  ) );
        }        

        return studyIds;
    }
}
