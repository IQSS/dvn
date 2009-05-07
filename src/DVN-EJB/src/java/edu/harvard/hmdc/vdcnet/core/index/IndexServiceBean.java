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
 * IndexServiceBean.java
 *
 * Created on September 26, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.core.index;

import edu.harvard.hmdc.vdcnet.core.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.core.study.Study;
import edu.harvard.hmdc.vdcnet.core.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.core.util.FileUtil;
import edu.harvard.hmdc.vdcnet.core.vdc.VDC;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCCollectionServiceLocal;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCServiceLocal;
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
public class IndexServiceBean implements edu.harvard.hmdc.vdcnet.core.index.IndexServiceLocal {

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
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.core.index.IndexServiceBean");
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

    private void addDocument(final long studyId) {
        Date indexTime = new Date();
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();
        String indexAdminMail = System.getProperty("dvn.indexadmin");
        if (indexAdminMail == null) {
            indexAdminMail = "dataverse@lists.hmdc.harvard.edu";
        }
        try {
            indexer.addDocument(study);
            try {
                studyService.setIndexTime(studyId, indexTime);
            } catch (Exception e) {
                e.printStackTrace(); // print stacktrace, but continue processing
            }
        } catch (IOException ex) {
            /*
            IndexStudy s = new IndexStudy();
            s.setStudyId(studyId);
            em.persist(s);
             */
            ex.printStackTrace();
            try {
                mailService.sendDoNotReplyMail(indexAdminMail, "IO problem", "Check index write lock " + InetAddress.getLocalHost().getHostAddress() + " , study id " + studyId);
            } catch (UnknownHostException u) {
                u.printStackTrace();
            }
        }
    }

    private void deleteDocument(final long studyId) {
        Study study = studyService.getStudy(studyId);
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(study.getId().longValue());
    }

    public void indexAll() {
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
            addDocument(elem.getId().longValue());
        }
    }

    public void indexList(List<Long> studyIds) {
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
            addDocument(elem.longValue());
        }
    }

    public void updateIndexList(List<Long> studyIds) {
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
                addDocument(elem.longValue());
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
        HashSet s = getUnindexedStudies();
        for (Iterator it = s.iterator(); it.hasNext();) {
            Study study = (Study) it.next();
            addDocument(study.getId().longValue());
        }
    }
    
    private List<Long> getStudiesforCollections(List<VDCCollection> searchCollections) {
        List<Long> studyIds = new ArrayList();
        
        for (VDCCollection coll : searchCollections) {
            studyIds.addAll( collService.getStudyIds( coll  ) );
        }        

        return studyIds;
    }
}
