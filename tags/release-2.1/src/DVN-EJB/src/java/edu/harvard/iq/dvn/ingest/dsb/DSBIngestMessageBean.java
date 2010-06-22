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
 * DSBIngestMessageBean.java
 *
 * Created on November 20, 2006, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.dsb;

import edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal;
import edu.harvard.iq.dvn.core.ddi.DDIServiceLocal;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author gdurand
 */
@MessageDriven(mappedName = "jms/DSBIngest", activationConfig =  {@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"), @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
public class DSBIngestMessageBean implements MessageListener {
    @EJB DDIServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB MailServiceLocal mailService;
    @EJB IndexServiceLocal indexService;
    @EJB NetworkDataServiceLocal networkDataService;

    
    /**
     * Creates a new instance of DSBIngestMessageBean
     */

    public DSBIngestMessageBean() {
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        DSBIngestMessage ingestMessage = null;
        List successfulFiles = new ArrayList();
        List problemFiles = new ArrayList();
        
        try {           
            ObjectMessage om = (ObjectMessage) message;
            ingestMessage = (DSBIngestMessage) om.getObject();
            String detail = "Ingest processing for " +ingestMessage.getFileBeans().size() + " file(s).";
                      
            Iterator iter = ingestMessage.getFileBeans().iterator();
            while (iter.hasNext()) {
                StudyFileEditBean fileBean = (StudyFileEditBean) iter.next();
                
                try {
                    if (fileBean.getStudyFile() instanceof NetworkDataFile ) {
                        // ELLEN TODO:  move this logic into SDIOReader component
                        networkDataService.ingest(fileBean);
                        successfulFiles.add(fileBean);
                    } else {
                        parseXML( new DSBWrapper().ingest(fileBean) , fileBean.getFileMetadata() );
                        successfulFiles.add(fileBean);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    problemFiles.add(fileBean);
                }
                
            }

            if (!successfulFiles.isEmpty()) {
                studyFileService.addIngestedFiles(ingestMessage.getStudyId(), ingestMessage.getVersionNote(), successfulFiles, ingestMessage.getIngestUserId());
            }
            
            
            if ( ingestMessage.sendInfoMessage() || ( problemFiles.size() >= 0 && ingestMessage.sendErrorMessage() ) ) {
                mailService.sendIngestCompletedNotification(ingestMessage.getIngestEmail(), successfulFiles, problemFiles);
            }
            
        } catch (JMSException ex) {
            ex.printStackTrace(); // error in getting object from message; can't send e-mail
            
        } catch (Exception ex) { 
            ex.printStackTrace();
            // if a general exception is caught that means the entire upload failed
            if (ingestMessage.sendErrorMessage()) {
                mailService.sendIngestCompletedNotification(ingestMessage.getIngestEmail(), null, ingestMessage.getFileBeans());
            }
            
        } finally {
            // when we're done, go ahead and remove the lock
            try {
                studyService.removeStudyLock( ingestMessage.getStudyId() );
            } catch (Exception ex) {
                ex.printStackTrace(); // application was unable to remove the studyLock
            }
        }
    }
    
    private void parseXML(String xmlToParse, FileMetadata fileMetadata) {
        // now map and get dummy dataTable
        Study dummyStudy = new Study();
        ddiService.mapDDI(xmlToParse, dummyStudy.getLatestVersion());
        TabularDataFile tdf = (TabularDataFile) dummyStudy.getStudyFiles().iterator().next();
        DataTable dt = tdf.getDataTable();

        TabularDataFile file = (TabularDataFile) fileMetadata.getStudyFile();
      
        // set to actual file (and copy over the UNF)
        file.setDataTable( dt );
        dt.setStudyFile(file);
        file.setUnf(dt.getUnf());
        //file.getFileMetadatas().add(fileMetadata);

    }
 
    
}
