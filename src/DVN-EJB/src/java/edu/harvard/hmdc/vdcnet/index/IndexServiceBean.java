/*
 * IndexServiceBean.java
 *
 * Created on September 26, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import edu.harvard.hmdc.vdcnet.vdc.VDCServiceLocal;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

/**
 *
 * @author roberttreacy
 */
@Stateless
public class IndexServiceBean implements edu.harvard.hmdc.vdcnet.index.IndexServiceLocal {
    @EJB StudyServiceLocal studyService;
    @EJB VDCServiceLocal vdcService;
    @EJB MailServiceLocal mailService;
    @Resource(mappedName="jms/IndexMessage") Queue queue;
    @Resource(mappedName="jms/IndexMessageFactory") QueueConnectionFactory factory;
    
    /**
     * Creates a new instance of IndexServiceBean
     */
    public IndexServiceBean() {
    }
    
    public void indexStudy(long studyId){
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.ADD);
        sendMessage(op);
    }

    private void sendMessage(final IndexEdit op) {
        QueueConnection conn = null;
        QueueSession session = null;
        QueueSender sender = null;
        try {
            conn = factory.createQueueConnection();
            session = conn.createQueueSession(false,0);
            sender = session.createSender(queue);
            
                Message message = session.createObjectMessage(op);
                sender.send(message);
            
            
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
    
    public void updateStudy(long studyId){
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.UPDATE);
        sendMessage(op);
    }

    public void deleteStudy(long studyId){
        IndexEdit op = new IndexEdit();
        op.setStudyId(studyId);
        op.setOperation(IndexEdit.Op.DELETE);
        sendMessage(op);
     }

    private void addDocument(final long studyId) {
        Study study = studyService.getStudy(studyId);    
        Indexer indexer = Indexer.getInstance();
        String indexAdminMail = System.getProperty("dvn.indexadmin");
        if (indexAdminMail == null){
            indexAdminMail = "dataverse@lists.hmdc.harvard.edu";
        }
        try {
            indexer.addDocument(study);
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                mailService.sendDoNotReplyMail(indexAdminMail ,"IO problem", "Check index write lock "+InetAddress.getLocalHost().getHostAddress() + " , study id " + studyId);
            } catch (UnknownHostException u) {
                u.printStackTrace();
            }
        }
    }
    
    public void indexAll(){
        Indexer indexer = Indexer.getInstance();
        try {
            indexer.setup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<Study> studies = studyService.getStudies();
        for (Iterator it = studies.iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            addDocument(elem.getId().longValue());
        }
    }
    
    public List search(String query){
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(query);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return matchingStudyIds;
    }
    
    public List search(VDC vdc, List<VDCCollection> searchCollections, List <SearchTerm> searchTerms){
        Indexer indexer = Indexer.getInstance();
        List <Long> studyIds = new ArrayList();
        for (Iterator it = searchCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            Collection studies = null;
            
            if (elem.getQuery() != null){
                try {
                    List <Long> queryStudyIds = indexer.query(elem.getQuery());
                    studyIds.addAll(queryStudyIds);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                studies = elem.getStudies();
                for (Iterator it2 = studies.iterator(); it2.hasNext();) {
                    Study elem2 = (Study) it2.next();
                    if (!studyIds.contains(elem2.getId())) {
                        studyIds.add(elem2.getId());
                    }
                }
            }
        }
        List matchingStudyIds = null;
        if (studyIds.isEmpty()){
            matchingStudyIds = new ArrayList();
        }else{
            try {
                matchingStudyIds = indexer.search(studyIds, searchTerms);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return matchingStudyIds;
    }
    
    public List search(VDC vdc, List<SearchTerm> searchTerms){
        List studyIds = listVdcStudyIds(vdc);

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(studyIds, searchTerms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }

    private List listVdcStudyIds(final VDC vdc) {
        List studyIds = new ArrayList();
        if (vdc != null){
            List <VDCCollection> vdcCollections = getCollections(vdc);
            vdcCollections.add(vdc.getRootCollection()); // may want to change getCollections to include root collection
            for (Iterator it = vdcCollections.iterator(); it.hasNext();) {
                VDCCollection elem = (VDCCollection) it.next();
                Collection studies = elem.getStudies();
                for (Iterator it2 = studies.iterator(); it2.hasNext();) {
                    Study elem2 = (Study) it2.next();
                    studyIds.add(elem2.getId());
                }
                
            }
        }
        return studyIds;
    }

    public List search(List <Long> studyIds, List<SearchTerm> searchTerms){

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(studyIds, searchTerms);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }
    
    public List search(SearchTerm searchTerm){
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.search(searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }
    
    public List searchVariables(SearchTerm searchTerm){
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.searchVariables(searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }
    
    public List searchVariables(VDC vdc,SearchTerm searchTerm){
        List studyIds = listVdcStudyIds(vdc);

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.searchVariables(studyIds, searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }
    
    public List searchVariables(VDC vdc,List<VDCCollection> searchCollections,SearchTerm searchTerm){
        Indexer indexer = Indexer.getInstance();
        List <Long> studyIds = new ArrayList();
        for (Iterator it = searchCollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            Collection studies = null;
            
            if (elem.getQuery() != null){
                try {
                    List <Long> queryStudyIds = indexer.query(elem.getQuery());
                    studyIds.addAll(queryStudyIds);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                studies = elem.getStudies();
                for (Iterator it2 = studies.iterator(); it2.hasNext();) {
                    Study elem2 = (Study) it2.next();
                    studyIds.add(elem2.getId());
                }
            }
        }

        List matchingStudyIds = null;
        if (studyIds.isEmpty()){
            matchingStudyIds = new ArrayList();
        }else{
            try {
                matchingStudyIds = indexer.searchVariables(studyIds, searchTerm);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return matchingStudyIds;
    }
    
    public List searchVariables(List studyIds, SearchTerm searchTerm){

        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.searchVariables(studyIds, searchTerm);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }

    public List getCollections(VDC vdc){
        ArrayList collections = new ArrayList();
        VDCCollection vdcRootCollection = vdc.getRootCollection();
        Collection <VDCCollection> subcollections = vdcRootCollection.getSubCollections();
        buildList(collections, subcollections);
        return collections;
    }
    
    private void buildList( ArrayList collections, Collection<VDCCollection> vdccollections) {

        for (Iterator it = vdccollections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            collections.add(elem);
            Collection <VDCCollection> subcollections = elem.getSubCollections();
            if (!subcollections.isEmpty()){
                buildList(collections,subcollections);
            }
        }
    }

    public List query(String adhocQuery) {
        
        Indexer indexer = Indexer.getInstance();
        List matchingStudyIds = null;
        try {
            matchingStudyIds = indexer.query(adhocQuery);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return matchingStudyIds == null ? new ArrayList(): matchingStudyIds;
    }
    
}
