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
 * IndexMessage.java
 *
 * Created on February 22, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.index;

import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
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
public class IndexMessage implements MessageListener, java.io.Serializable {
    @EJB StudyServiceLocal studyService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB MailServiceLocal mailService;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.index.IndexMessage");

    
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
        Date indexTime = new Date();
        try {
            study = studyService.getStudy(studyId);
        } catch (IllegalArgumentException e) {
            // The study may not exist because it was deleted before the indexer 
            // could process the message.  Just ignore this.
           logger.warning("Could not find study for id "+studyId+", study probably deleted before asynchronous call to Indexer.");
            
        }
        Indexer indexer = Indexer.getInstance();
       
        try {
            indexer.addDocument(study);
            try {
                studyService.setIndexTime(studyId, indexTime);
            } catch (Exception e) {
                e.printStackTrace(); // print stacktrace, but continue processing
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                mailService.sendDoNotReplyMail(vdcNetworkService.find().getContactEmail(),"IO problem", "Check index write lock "+InetAddress.getLocalHost().getHostAddress() + " , studyId " + studyId);
            } catch (UnknownHostException u) {
                u.printStackTrace();
            }
        }
    }
    
}
