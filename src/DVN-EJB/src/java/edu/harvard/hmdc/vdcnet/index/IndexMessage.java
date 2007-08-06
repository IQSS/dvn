/*
 * IndexMessage.java
 *
 * Created on February 22, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.index;

import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Entity class IndexMessage
 *
 * @author roberttreacy
 */
@MessageDriven(mappedName = "jms/IndexMessage", activationConfig =  {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class IndexMessage implements MessageListener {
    @EJB StudyServiceLocal studyService;
//    @EJB VDCServiceLocal vdcService;
    @EJB MailServiceLocal mailService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.index.IndexMessage");

    
    /** Creates a new instance of IndexMessage */
    public IndexMessage() {
    }
    
    public void onMessage(Message message) {
        IndexEdit operation = null;
        
        
        try {
            ObjectMessage om = (ObjectMessage) message;
            operation = (IndexEdit) om.getObject();
            switch (operation.getOperation()){
                case ADD:
                    indexStudy(operation.getStudyId());
                    break;
                case UPDATE:
                    updateStudy(operation.getStudyId());
                    break;
                case DELETE:
                    deleteStudy(operation.getStudyId());
                    break;
                    
            }
        } catch (JMSException ex) {
            ex.printStackTrace(); // error in getting object from message; can't send e-mail
            
        } catch (Exception ex) { // we catch any exception that may have happened during the process
            ex.printStackTrace();
        }
    }
    
    private void indexStudy(long studyId){
        addDocument(studyId);
    }
    
    private void updateStudy(long studyId){
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(studyId);
        addDocument(studyId);
    }
    
    private void deleteStudy(long studyId){
        Indexer indexer = Indexer.getInstance();
        indexer.deleteDocument(studyId);
    }
    
    private void addDocument(final long studyId) {
        Study study=null;
        try {
            study = studyService.getStudy(studyId);
        } catch (IllegalArgumentException e) {
            // The study may not exist because it was deleted before the indexer 
            // could process the message.  Just ignore this.
           logger.warning("Could not find study for id "+studyId+", study probably deleted before asynchronous call to Indexer.");
            
        }
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
                mailService.sendDoNotReplyMail(indexAdminMail,"IO problem", "Check index write lock "+InetAddress.getLocalHost().getHostAddress() + " , studyId " + studyId);
            } catch (UnknownHostException u) {
                u.printStackTrace();
            }
        }
    }
    
}
