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
            e.printStackTrace(); // print stacktrace, but continue processing
        }
    }
    
    private void addDocument(Study study) throws IOException {
        Date indexTime = new Date();
        Indexer indexer = Indexer.getInstance();
        indexer.addDocument(study);
        try {
            studyService.setIndexTime(study.getId(), indexTime);
        } catch (Exception e) {
            e.printStackTrace(); // print stacktrace, but continue processing
        }
    }

    private void deleteDocument(final long studyId) {
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(study.getId().longValue());
    }

    public void indexAll() {
        boolean ioProblem = false;
        long ioProblemCount = 0;
        Indexer indexer = Indexer.getInstance();
        
        List<Long> studyIds = studyService.getAllStudyIds(); 
        
        Long studyId = null; 
        Study study = null; 
        int count = 0;
        
        if (studyIds != null) {
            logger.info("Re-indexing "+(studyIds.size())+" studies.");

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
                        logger.info("Processed "+count+" studies; last processed id: "+studyId.toString());
                    }
                }

                study = null;
                studyId = null; 
             
            }
            
        }
        logger.info("Finished index-all.");
        handleIOProblems(ioProblem,ioProblemCount);
    }
    
    public void indexAllpreserved() {
        boolean ioProblem = false;
        long ioProblemCount = 0;
        Indexer indexer = Indexer.getInstance();
        /*
        try {
        indexer.setup();
        } catch (IOException ex) {
        ex.printStackTrace();
        }
         */
       
        
        
        Long maxStudyTableId = studyService.getMaxStudyTableId();
        
        logger.info("MAX database id in the study table: "+maxStudyTableId);
       
        
        long  indexingBatchSize = 1L; //00; // needs to be made configurable.
        List<Study> studies = null;
        
        for (long i = 0L; i < (maxStudyTableId.longValue() + 1L); i += indexingBatchSize) {
            //logger.info("Processing batch " + i +";");

            long rangeEnd = (i + indexingBatchSize) > maxStudyTableId.longValue() ? maxStudyTableId.longValue() + 1 : i + indexingBatchSize + 1;
            
            logger.info("Processing batch " + i + "; Range end: "+rangeEnd);
            
            studies = studyService.getStudiesByIdRange(i, rangeEnd);

            int batchDocCount = 0;
            int batchProblemCount = 0;
            int batchSkipCount = 0; 

            if (studies != null) {
                if (studies.size() == 0) {
                    logger.info("Batch " + i + ": zero studies found.");
                } else {
                    for (Iterator it = studies.iterator(); it.hasNext();) {
                        Study study = (Study) it.next();

                        /*
                         try {
                         if (study.getLastExportTime() != null) {
                         deleteDocument(study.getId());
                         }
                         } catch (Exception ex) {
                         // skip this study, just to be safe.
                         batchSkipCount++; 
                         continue;
                         }
                         * */
                        try {
                            addDocument(study);
                            batchDocCount++;
                        } catch (Exception ex) {
                            ioProblem = true;
                            ioProblemCount++;
                            batchProblemCount++;
                            Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                            logger.severe("Caught exception trying to reindex study " + study.getId());
                        }
                    }
                }
                logger.info("Processed batch; " + batchDocCount + " documents, " + batchProblemCount + " exceptions, "+batchSkipCount+" skipped.");
            } else {
                logger.info("Batch "+i+": no studies found.");
            }
            
            studies = null; 
        }
        logger.info("Finished index-all.");
        handleIOProblems(ioProblem,ioProblemCount);
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
                try {
                    addDocument(elem.longValue());
                } catch (IOException ex) {
                    ioProblem = true;
                    ioProblemCount++;
                    Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
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
        logger.info("Starting batch reindex of collection-linked studies.");
        
        try {
            List<Long> vdcIdList = vdcService.findAllIds();
            
            Long maxStudyId = studyService.getMaxStudyTableId(); 
            
            if (maxStudyId == null) {
                throw new IOException("Could not determine the last database id in the study table.");
            }
            
            if (maxStudyId.intValue() != maxStudyId.longValue()) {
                logger.severe("There appears to be more than 2^^31 objects in the study table; the subnetwork cross-indexing hack isn't going to work.");
                throw new IOException("There appears to be more than 2^^31 objects in the study table; the subnetwork cross-indexing hack isn't going to work.");
                /* This is quite unlikely to happen, but still... */
            }
            
            int numberOfNetworks = findNumberOfSubnetworks(); 
            
            if (numberOfNetworks < 1) {
                // No subnetworks in this DV Network; nothing to do. 
                logger.info("There's only one network in the DVN; exiting");
                return; 
            }
            
            if (numberOfNetworks > 63) {
                logger.severe("There is more than 63 VDC (sub)networks. The subnetwork cross-indexing hack isn't going to work.");
                throw new IOException("There is more than 63 VDC (sub)networks. The subnetwork cross-indexing hack isn't going to work.");
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
                        // already contains all the studies in it. 
                        linkedStudyIds = indexer.findStudiesInCollections(vdc);

                        if (linkedStudyIds != null) {
                            logger.info("Found "+linkedStudyIds.size()+" linked studies in VDC "+vdcNetworkId.toString());

                            for (Long studyId : linkedStudyIds) {
                                if (studyId.longValue() <= maxStudyId.longValue()) {
                                    // otherwise this is a new study, created since we 
                                    // have started this process; we'll be skipping it,
                                    // this time around.  
                                    linkedStudy = studyService.getStudy(studyId);
                                    if (linkedStudy != null) {
                                        studyNetworkId = linkedStudy.getOwner().getVdcNetwork().getId();
                                        if (studyNetworkId != null && vdcNetworkId.compareTo(studyNetworkId) != 0) {
                                            // this study is cross-linked from another VDC network!
                                            linkedVdcNetworkMap[linkedStudy.getId().intValue()] |= studyNetworkId.longValue();
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
            
            logger.info("Checking the cross-linking status and reindexing the studies for which it has changed:");
            
            List<Long> linkedToNetworkIds = null; 
            
            for (int i = 0; i < maxStudyId.intValue() + 1; i++) {
                if (linkedVdcNetworkMap[i] > 0) {
                    linkedStudy = studyService.getStudy(new Long(i));
                    // Only released studies get indexed!
                    if (linkedStudy != null && linkedStudy.isReleased()) {
                        linkedToNetworkIds = linkedStudy.getLinkedToNetworkIds();
                        boolean indexUpdateRequired = false;
                        int linkedNetworkCounter = 0; 

                        for (int network = 0; network < numberOfNetworks; i++) {
                            if ((linkedVdcNetworkMap[i] & (1 >> network)) > 0) {
                                logger.info("study "+i+" linked to Network "+network);
                                linkedNetworkCounter++; 
                                if (!alreadyLinkedToThisVdcNetwork(linkedToNetworkIds, network)) {
                                    linkedStudy.setLinkedToNetwork(vdcNetworkService.findById(new Long(network)));
                                    // TODO: 
                                    // instead of doing "findById()" each time, create
                                    // an array of vdcNetworks and cache them. -- L.A.
                                    indexUpdateRequired = true;
                                }
                                // TODO: 
                                
                            } else {
                                // And if the study is still listed in the DB as 
                                // linked to a network, even it is no longer 
                                // in the search results for any of the collections
                                // in that network - the linking needs to be removed:
                                // -- L.A. 
                                if (alreadyLinkedToThisVdcNetwork(linkedToNetworkIds, network)) {
                                    linkedStudy.unsetLinkedToNetwork(vdcNetworkService.findById(new Long(network)));
                                    indexUpdateRequired = true; 
                                }
                            }
                        }
                        
                        if (indexUpdateRequired) {
                            // Can reindex it now... or put it on some list for a later 
                            // batch reindex - ?
                            try {
                                indexer.deleteDocument(linkedStudy.getId());
                                indexer.addDocument(linkedStudy);
                            } catch (Exception ex) {
                                ioProblem = true;
                                ioProblemCount++;
                                logger.severe("Caught exception attempting to re-index study " + linkedStudy.getId());
                            }
                        }
                    }
                }                
            }
            logger.info("Done reindexing collection-linked studies.");

        } catch (Exception ex) {
            ioProblem = true;
            ioProblemCount++; 
            logger.severe("Caught exception while trying to update studies in collections.");
        }
        handleIOProblems(ioProblem, ioProblemCount);
    }

    private int findNumberOfSubnetworks () {
        Long ret = null;
        
        List<VDCNetwork> subNetworks = vdcNetworkService.getVDCSubNetworks();
        
        if (subNetworks == null) {
            return 0;
        }
        
        return subNetworks.size();
    }
    
    boolean alreadyLinkedToThisVdcNetwork(List<Long> linkedToNetworks, int network) {
        if (linkedToNetworks == null) {
            return false;
        }
        
        if (linkedToNetworks.contains(new Long(network))) {
            return true; 
        }   
        return false; 
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
            logger.info("getUnindexedStudies(), found "+studies.size()+" studies.");
        } else {
            logger.info("getUnindexedStudies(), no studies found.");
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
