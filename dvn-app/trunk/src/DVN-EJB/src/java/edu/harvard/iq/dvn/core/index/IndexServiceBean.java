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
import edu.harvard.iq.dvn.core.vdc.VDCCollection;
import edu.harvard.iq.dvn.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
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
            if (timer.getInfo().equals(INDEX_TIMER)) {
                logger.log(Level.INFO, "Index update");
                indexBatch();
            } else if (timer.getInfo().equals(INDEX_NOTIFICATION_TIMER)) {
                logger.log(Level.INFO, "Index notify");
                indexProblemNotify();
            }
        } catch (Throwable e) {
            mailService.sendIndexUpdateErrorNotification(vdcNetworkService.find().getContactEmail(), vdcNetworkService.find().getName());
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
                mailService.sendDoNotReplyMail(vdcNetworkService.find().getContactEmail(), "IO problem", ioProblemCount +" studies may not have been indexed" + InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException ex) {
                Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void indexProblemNotify() {
        List<Study> studies = (List<Study>) em.createQuery("SELECT s from Study s where s.lastIndexTime < s.lastUpdateTime OR s.lastIndexTime is NULL").getResultList();
        if (studies.size() > 0) {
            mailService.sendIndexErrorNotification(vdcNetworkService.find().getContactEmail(), vdcNetworkService.find().getName(), studies.size());
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

    private void deleteDocument(final long studyId) {
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(study.getId().longValue());
    }

    public void indexAll() {
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
        List<Study> studies = studyService.getStudies();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            try {
                addDocument(elem.getId().longValue());
            } catch (IOException ex) {
                ioProblem = true;
                ioProblemCount++;
                Logger.getLogger(IndexServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
