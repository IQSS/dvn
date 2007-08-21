/*
 * DSBIngestMessageBean.java
 *
 * Created on November 20, 2006, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.dsb;

import edu.harvard.hmdc.vdcnet.ddi.DDI20ServiceLocal;
import edu.harvard.hmdc.vdcnet.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.CodeBook;
import edu.harvard.hmdc.vdcnet.mail.MailServiceLocal;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author gdurand
 */
@MessageDriven(mappedName = "jms/DSBIngest", activationConfig =  {@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"), @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
public class DSBIngestMessageBean implements MessageListener {
    @EJB DDI20ServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB MailServiceLocal mailService;
    @EJB IndexServiceLocal indexService;

    private JAXBContext jaxbContext = null;
    private Unmarshaller ddiUnmarshaller;
    
    public void ejbCreate() {
        try {
            jaxbContext = javax.xml.bind.JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.ddi20");
            ddiUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }    
    
    /**
     * Creates a new instance of DSBIngestMessageBean
     */

    public DSBIngestMessageBean() {
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        DSBIngestMessage ingestMessage = null;
        List successfuleFiles = new ArrayList();
        List problemFiles = new ArrayList();
        
        try {
            ObjectMessage om = (ObjectMessage) message;
            ingestMessage = (DSBIngestMessage) om.getObject();
            
            Iterator iter = ingestMessage.getFileBeans().iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                
                try {
                    parseXML( new DSBWrapper().ingest(fileBean) , fileBean.getStudyFile() );
                    successfuleFiles.add(fileBean);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    problemFiles.add(fileBean);
                }
                
            }
            
            studyService.addIngestedFiles( ingestMessage.getStudyId(),
                    successfuleFiles,
                    ingestMessage.getIngestUserId());
            
            
            // adding files succeeded; call indexer
            indexService.updateStudy(ingestMessage.getStudyId());
            
            mailService.sendIngestCompletedNotification(ingestMessage.getIngestEmail(), successfuleFiles, problemFiles);
            
        } catch (JMSException ex) {
            ex.printStackTrace(); // error in getting object from message; can't send e-mail
            
        } catch (Exception ex) { 
            ex.printStackTrace();
            // if a general exception is caught that means the entire upload failed
            mailService.sendIngestCompletedNotification(ingestMessage.getIngestEmail(), null, ingestMessage.getFileBeans());
            
        } finally {
            // when we're done, go ahead and remove the lock
            try {
                studyService.removeStudyLock( ingestMessage.getStudyId() );
            } catch (Exception ex) {
                ex.printStackTrace(); // application was unable to remove the studyLock
            }
        }
    }
    
    private void parseXML(String xmlToParse, StudyFile file) {
        try {
            // first transform xml to our jaxb objects
            CodeBook cb = (CodeBook) ddiUnmarshaller.unmarshal( new StreamSource( new StringReader( xmlToParse ) ) );
            
            // now map and get dummy dataTable
            Study dummyStudy = ddiService.mapDDI(cb);
            DataTable dt = dummyStudy.getFileCategories().iterator().next().getStudyFiles().iterator().next().getDataTable();
            
            // set to actual file
            file.setDataTable( dt );
            dt.setStudyFile(file);
            
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }
    
}
