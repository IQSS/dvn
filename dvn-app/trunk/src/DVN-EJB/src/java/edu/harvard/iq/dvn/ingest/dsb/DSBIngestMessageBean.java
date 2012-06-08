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
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author gdurand
 */
@MessageDriven(mappedName = "jms/DSBIngest", activationConfig =  {@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"), @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")})
@EJB(name="networkData", beanInterface=edu.harvard.iq.dvn.core.analysis.NetworkDataServiceLocal.class)
public class DSBIngestMessageBean implements MessageListener {
    @EJB DDIServiceLocal ddiService;
    @EJB StudyServiceLocal studyService;
    @EJB StudyFileServiceLocal studyFileService;
    @EJB MailServiceLocal mailService;
    @EJB IndexServiceLocal indexService;
    NetworkDataServiceLocal networkDataService;

    
    /**
     * Creates a new instance of DSBIngestMessageBean
     */

    public DSBIngestMessageBean() {
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message message) {
        DSBIngestMessage ingestMessage = null;
        StudyVersion sv = null;
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

                        Context ctx = new InitialContext();
                        networkDataService = (NetworkDataServiceLocal) ctx.lookup("java:comp/env/networkData");
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
                mailService.sendIngestCompletedNotification(ingestMessage, successfulFiles, problemFiles);
            }
            
        } catch (JMSException ex) {
            ex.printStackTrace(); // error in getting object from message; can't send e-mail
            
        } catch (Exception ex) { 
            ex.printStackTrace();
            // if a general exception is caught that means the entire upload failed
            if (ingestMessage.sendErrorMessage()) {
                mailService.sendIngestCompletedNotification(ingestMessage, null, ingestMessage.getFileBeans());
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
        Map filesMap = ddiService.mapDDI(xmlToParse, dummyStudy.getLatestVersion());
        Map variablesMap = ddiService.reMapDDI(xmlToParse, dummyStudy.getLatestVersion(), filesMap);
        if (variablesMap != null) {

            Object mapKey = variablesMap.keySet().iterator().next();
            List<DataVariable> variablesMapEntry = (List<DataVariable>)variablesMap.get(mapKey);

            if (variablesMapEntry != null) {
                DataVariable dv = variablesMapEntry.get(0);
                DataTable tmpDt = dv.getDataTable();

                if (tmpDt != null) {
                    tmpDt.setDataVariables(variablesMapEntry);

                    TabularDataFile file = (TabularDataFile) fileMetadata.getStudyFile();

                    // set to actual file (and copy over the UNF)
                    file.setDataTable( tmpDt );
                    tmpDt.setStudyFile(file);
                    file.setUnf(tmpDt.getUnf());
                }
            }
        }
    }
 
    
}
