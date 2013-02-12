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
 * HarvesterServiceBean.java
 *
 * Created on February 12, 2007, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.harvest;

//import ORG.oclc.oai.harvester2.verb.GetRecord;
import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import ORG.oclc.oai.harvester2.verb.ListMetadataFormats;
import ORG.oclc.oai.harvester2.verb.ListSets;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.hmdc.vdcnet.jaxb.oai.HeaderType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListIdentifiersType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListMetadataFormatsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListSetsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.MetadataFormatType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHerrorType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHerrorcodeType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHtype;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ResumptionTokenType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.SetType;
import edu.harvard.iq.dvn.core.admin.DvnTimerLocal;
import edu.harvard.iq.dvn.core.mail.MailServiceLocal;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverse;
import edu.harvard.iq.dvn.core.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
//import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import no.uib.nsd.nesstar.NesstarHarvester.DDI;
import no.uib.nsd.nesstar.NesstarHarvester.NesstarHarvester;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless(name = "harvesterService")
//@EJB(name = "editStudyService", beanInterface = edu.harvard.iq.dvn.core.study.EditStudyService.class)
public class HarvesterServiceBean implements HarvesterServiceLocal {
    @Resource
    javax.ejb.TimerService timerService;
    @Resource
    SessionContext ejbContext;

    @EJB
    DvnTimerLocal dvnTimerService;    
    @EJB
    StudyServiceLocal studyService;
    @EJB
    HarvesterServiceLocal harvesterService;
    @EJB
    HarvestingDataverseServiceLocal harvestingDataverseService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    MailServiceLocal mailService;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    private static final Logger logger = Logger.getLogger("edu.harvard.iq.dvn.core.harvest.HarvesterServiceBean");
    private static final String HARVEST_TIMER = "HarvestTimer";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat logFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");

   
    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;

    private long processedSizeThisBatch = 0;
    private List<Long> harvestedStudyIdsThisBatch = null;


    public void ejbCreate() {
        try {

            jaxbContext = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new instance of HarvesterServiceBean
     */
    public HarvesterServiceBean() {

    }
    /**
     * Called to run an "On Demand" harvest.  
     * This method creates a timer that will go off immediately,
     * which will start an immediate asynchronous harvest.
     * @param dataverse
     */
    public void doAsyncHarvest(HarvestingDataverse dataverse) {
        Calendar cal = Calendar.getInstance();

        timerService.createTimer(cal.getTime(), new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
    }

    public void createScheduledHarvestTimers() {
        logger.log(Level.INFO, "HarvesterService: going to (re)create Scheduled harvest timers.");
        
        dvnTimerService.removeHarvestTimers();

        List dataverses = harvestingDataverseService.findAll();
        for (Iterator it = dataverses.iterator(); it.hasNext();) {
            HarvestingDataverse dataverse = (HarvestingDataverse) it.next();
            if (dataverse.isScheduled()) {
                createHarvestTimer(dataverse);
            }
        }
 

    }

    public void removeHarvestTimer(HarvestingDataverse dataverse) {
        dvnTimerService.removeHarvestTimer(dataverse);
    }

    public void updateHarvestTimer(HarvestingDataverse dataverse) {
        removeHarvestTimer(dataverse);
        createHarvestTimer(dataverse);
    }
    
    public List<HarvestTimerInfo> getHarvestTimers() {
        ArrayList timers = new ArrayList<HarvestTimerInfo>();
        // Clear dataverse timer, if one exists 
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo() instanceof HarvestTimerInfo) {
                HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
                timers.add(info);
            }
        }    
        return timers;
    }

    private void createHarvestTimer(HarvestingDataverse dataverse) {
        if (dataverse.isScheduled()) {
            long intervalDuration = 0;
            Calendar initExpiration = Calendar.getInstance();
            initExpiration.set(Calendar.MINUTE, 0);
            initExpiration.set(Calendar.SECOND, 0);
            if (dataverse.getSchedulePeriod().equals(dataverse.SCHEDULE_PERIOD_DAILY)) {
                intervalDuration = 1000 * 60 * 60 * 24;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());

            } else if (dataverse.getSchedulePeriod().equals(dataverse.SCHEDULE_PERIOD_WEEKLY)) {
                intervalDuration = 1000 * 60 * 60 * 24 * 7;
                initExpiration.set(Calendar.HOUR_OF_DAY, dataverse.getScheduleHourOfDay());
                initExpiration.set(Calendar.DAY_OF_WEEK, dataverse.getScheduleDayOfWeek());

            } else {
                logger.log(Level.WARNING, "Could not set timer for dataverse id, " + dataverse.getId() + ", unknown schedule period: " + dataverse.getSchedulePeriod());
                return;
            }
            Date initExpirationDate = initExpiration.getTime();
            Date currTime = new Date();
            if (initExpirationDate.before(currTime)) {
                initExpirationDate.setTime(initExpiration.getTimeInMillis() + intervalDuration);
            }
            logger.log(Level.INFO, "Setting timer for dataverse " + dataverse.getVdc().getName() + ", initial expiration: " + initExpirationDate);
//            timerService.createTimer(initExpirationDate, intervalDuration, new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
            dvnTimerService.createTimer(initExpirationDate, intervalDuration, new HarvestTimerInfo(dataverse.getId(), dataverse.getVdc().getName(), dataverse.getSchedulePeriod(), dataverse.getScheduleHourOfDay(), dataverse.getScheduleDayOfWeek()));
        }
    }
    
    /**
     * This method is called whenever an EJB Timer goes off.
     * Check to see if this is a Harvest Timer, and if it is
     * Run the harvest for the given (scheduled) dataverse
     * @param timer
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void handleTimeout(javax.ejb.Timer timer) {
        // We have to put all the code in a try/catch block because
        // if an exception is thrown from this method, Glassfish will automatically
        // call the method a second time. (The minimum number of re-tries for a Timer method is 1)
       
            if (timer.getInfo() instanceof HarvestTimerInfo) {
            HarvestTimerInfo info = (HarvestTimerInfo) timer.getInfo();
            try {
                // First, check if we are in read-only mode: 

                if (vdcNetworkService.defaultTransactionReadOnly()) {
                    logger.log(Level.ALL, "Network is in read-only mode.");
                    return;

                }

                // Proceeding with the scheduled harvest: 
                
                logger.log(Level.INFO, "DO HARVESTING of dataverse " + info.getHarvestingDataverseId());
                doHarvesting(info.getHarvestingDataverseId());

            } catch (Throwable e) {
                harvestingDataverseService.setHarvestResult(info.getHarvestingDataverseId(), HarvestingDataverse.HARVEST_RESULT_FAILED);
                mailService.sendHarvestErrorNotification(vdcNetworkService.find().getSystemEmail(), vdcNetworkService.find().getName());
                logException(e, logger);
            }
        }
    }

    /**
     * Harvest an individual Dataverse
     * @param dataverseId
     */
    public void doHarvesting(Long dataverseId) throws IOException {
        HarvestingDataverse dataverse = em.find(HarvestingDataverse.class, dataverseId);
        MutableBoolean harvestErrorOccurred = new MutableBoolean(false);
        String logTimestamp = logFormatter.format(new Date());
        Logger hdLogger = Logger.getLogger("edu.harvard.iq.dvn.core.harvest.HarvesterServiceBean." + dataverse.getVdc().getAlias() + logTimestamp);
        String logFileName = FileUtil.getImportFileDir() + File.separator + "harvest_" + dataverse.getVdc().getAlias() + logTimestamp + ".log";
        FileHandler fileHandler = new FileHandler(logFileName);
        hdLogger.addHandler(fileHandler);
        List<Long> harvestedStudyIds = null;

    	this.processedSizeThisBatch = 0;
        this.harvestedStudyIdsThisBatch = new ArrayList<Long>();

        List<String> failedIdentifiers = new ArrayList<String>();
        try {
            boolean harvestingNow = dataverse.isHarvestingNow();
        
            if (harvestingNow) {
                harvestErrorOccurred.setValue(true);
                hdLogger.log(Level.SEVERE, "Cannot begin harvesting, Dataverse " + dataverse.getVdc().getName() + " is currently being harvested.");

            } else {
                harvestingDataverseService.resetHarvestingStatus(dataverse.getId());
                String until = null;  // If we don't set until date, we will get all the changes since the last harvest.
                String from = null;
                Date lastSuccessfulHarvestTime = dataverse.getLastSuccessfulHarvestTime();
                if (lastSuccessfulHarvestTime != null) {
                    from = formatter.format(lastSuccessfulHarvestTime);
                }
                if (dataverse.isOai() || dataverse.isNesstar()) {
                    harvestingDataverseService.setHarvestingNow(dataverse.getId(), true);
                    Date currentTime = new Date();
                    harvestingDataverseService.setLastHarvestTime(dataverse.getId(), currentTime);
   
                    hdLogger.log(Level.INFO, "BEGIN HARVEST..., oaiUrl=" + dataverse.getServerUrl() + ",set=" + dataverse.getHarvestingSet() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix() + ", from=" + from + ", until=" + until);

                    if (dataverse.isOai()) {
                        harvestedStudyIds = harvestOAI(dataverse, hdLogger, from, until, harvestErrorOccurred, failedIdentifiers);

                    } else  {
                        harvestedStudyIds = harvestNesstar(dataverse, hdLogger, harvestErrorOccurred, failedIdentifiers);
                    } 
                    harvestingDataverseService.setHarvestSuccess(dataverse.getId(),currentTime, harvestedStudyIds.size(), failedIdentifiers.size());
                    hdLogger.log(Level.INFO, "COMPLETED HARVEST, server=" + dataverse.getServerUrl() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix());

                    if (harvestedStudyIds.size() > 0){
                        harvestingDataverseService.setHarvestSuccessNotEmpty(dataverse.getId(),currentTime, harvestedStudyIds.size(), failedIdentifiers.size());
                        hdLogger.log(Level.INFO, "COMPLETED HARVEST with results");
                    }
                    // now index all studies (need to modify for update)
                    if (this.processedSizeThisBatch > 0) {
                        hdLogger.log(Level.INFO, "POST HARVEST, reindexing the remaining studies.");
                        if (this.harvestedStudyIdsThisBatch != null) {
                            hdLogger.log(Level.INFO, this.harvestedStudyIdsThisBatch.size()+" studies in the batch");
                        }
                        hdLogger.log(Level.INFO, this.processedSizeThisBatch + " bytes of content");
                        indexService.updateIndexList(this.harvestedStudyIdsThisBatch);
                        hdLogger.log(Level.INFO, "POST HARVEST, calls to index finished.");
                    } else {
                        hdLogger.log(Level.INFO, "(All harvested content already reindexed)");
                    }
                }else {
                    harvestErrorOccurred.setValue(true);
                    harvestingDataverseService.setHarvestFailure(dataverse.getId(), harvestedStudyIds.size(), failedIdentifiers.size());
          
                    hdLogger.log(Level.SEVERE, "Cannot begin harvesting, Unknown harvest type."); 
                }
            }
            mailService.sendHarvestNotification(vdcNetworkService.find().getSystemEmail(), dataverse.getVdc().getName(), logFileName, logTimestamp, harvestErrorOccurred.booleanValue(), harvestedStudyIds.size(), failedIdentifiers);
          } catch (Throwable e) {
            harvestErrorOccurred.setValue(true);
            String message = "Exception processing harvest, server= " + dataverse.getServerUrl() + ",format=" + dataverse.getHarvestFormatType().getMetadataPrefix() + " " + e.getClass().getName() + " " + e.getMessage();
            hdLogger.log(Level.SEVERE, message);
            logException(e, hdLogger);
            hdLogger.log(Level.INFO, "HARVEST NOT COMPLETED DUE TO UNEXPECTED ERROR.");
            harvestingDataverseService.setHarvestFailure(dataverse.getId(), harvestedStudyIds.size(), failedIdentifiers.size());
              
          
        } finally {
            harvestingDataverseService.setHarvestingNow(dataverse.getId(), false);
            fileHandler.close();
            hdLogger.removeHandler(fileHandler);
        }
    }

    /**
     * 
     * @param dataverse  the dataverse to harvest into
     * @param from       get updated studies from this beginning date
     * @param until      get updated studies until this end date
     * @param harvestErrorOccurred  have we encountered any errors during harvest?
     * @param failedIdentifiers     Study Identifiers for failed "GetRecord" requests
     */
    private List<Long> harvestOAI(HarvestingDataverse dataverse, Logger hdLogger, String from, String until, MutableBoolean harvestErrorOccurred, List<String> failedIdentifiers)
            throws IOException, ParserConfigurationException,SAXException, TransformerException, JAXBException {
   
        List<Long> harvestedStudyIds = new ArrayList<Long>();
   

            ResumptionTokenType resumptionToken = null;

            do {
                //resumptionToken = harvesterService.harvestFromIdentifiers(hdLogger, resumptionToken, dataverse, from, until, harvestedStudyIds, failedIdentifiers, harvestErrorOccurred
                resumptionToken = harvestFromIdentifiers(hdLogger, resumptionToken, dataverse, from, until, harvestedStudyIds, failedIdentifiers, harvestErrorOccurred);
            } while (resumptionToken != null && !resumptionToken.equals(""));

            hdLogger.log(Level.INFO, "COMPLETED HARVEST, oaiUrl=" + dataverse.getServerUrl() + ",set=" + dataverse.getHarvestingSet() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix() + ", from=" + from + ", until=" + until);
           
        return harvestedStudyIds;
     
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ResumptionTokenType harvestFromIdentifiers(Logger hdLogger, ResumptionTokenType resumptionToken, HarvestingDataverse dataverse, String from, String until, List<Long> harvestedStudyIds, List<String> failedIdentifiers, MutableBoolean harvestErrorOccurred)
            throws java.io.IOException, ParserConfigurationException, SAXException, TransformerException, JAXBException {
        String encodedSet = dataverse.getHarvestingSet() == null ? null : URLEncoder.encode(dataverse.getHarvestingSet(), "UTF-8");
        ListIdentifiers listIdentifiers = null;

        if (resumptionToken == null) {
            listIdentifiers = new ListIdentifiers(dataverse.getServerUrl(),
                    from,
                    until,
                    encodedSet,
                    URLEncoder.encode(dataverse.getHarvestFormatType().getMetadataPrefix(), "UTF-8"));
        } else {
            hdLogger.log(Level.INFO, "harvestFromIdentifiers(), resumptionToken=" + resumptionToken.getValue());
            listIdentifiers = new ListIdentifiers(dataverse.getServerUrl(), resumptionToken.getValue());
        }
        
        Document doc = listIdentifiers.getDocument();

        //       JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
        //       Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        OAIPMHtype oaiObj = (OAIPMHtype) unmarshalObj.getValue();

        if (oaiObj.getError() != null && oaiObj.getError().size() > 0) {
            if (oaiObj.getError().get(0).getCode().equals(OAIPMHerrorcodeType.NO_RECORDS_MATCH)) {
                 hdLogger.info("ListIdentifiers returned NO_RECORDS_MATCH - no studies found to be harvested.");
            } else {
                handleOAIError(hdLogger, oaiObj, "calling listIdentifiers, oaiServer= " + dataverse.getServerUrl() + ",from=" + from + ",until=" + until + ",encodedSet=" + encodedSet + ",format=" + dataverse.getHarvestFormatType().getMetadataPrefix());
                throw new EJBException("Received OAI Error response calling ListIdentifiers");
            }
        } else {
            ListIdentifiersType listIdentifiersType = oaiObj.getListIdentifiers();
            if (listIdentifiersType != null) {
                resumptionToken = listIdentifiersType.getResumptionToken();
                for (Iterator it = listIdentifiersType.getHeader().iterator(); it.hasNext();) {
                    HeaderType header = (HeaderType) it.next();
                    MutableBoolean getRecordErrorOccurred = new MutableBoolean(false);
                    Long studyId = getRecord(hdLogger, dataverse, header.getIdentifier(), dataverse.getHarvestFormatType().getMetadataPrefix(), getRecordErrorOccurred);
                    if (studyId != null) {
                        harvestedStudyIds.add(studyId);
                    }
                    if (getRecordErrorOccurred.booleanValue()==true) {
                        failedIdentifiers.add(header.getIdentifier());
                    }
                    
                }

            }
        }
        String logMsg = "Returning from harvestFromIdentifiers";

        if (resumptionToken == null) {
            logMsg += " resumptionToken is null";
        } else if (!StringUtil.isEmpty(resumptionToken.getValue())) {
            logMsg += " resumptionToken is " + resumptionToken.getValue();
        } else {
            // Some OAIServers return an empty resumptionToken element when all
            // the identifiers have been sent, so need to check  for this, and 
            // treat it as if resumptiontoken is null.
            logMsg += " resumptionToken is empty, setting return value to null.";
            resumptionToken = null;
        }
        hdLogger.info(logMsg);
        return resumptionToken;
    }

    private void handleOAIError(Logger hdLogger, OAIPMHtype oaiObj, String message) {
        for (Iterator it = oaiObj.getError().iterator(); it.hasNext();) {
            OAIPMHerrorType error = (OAIPMHerrorType) it.next();
            message += ", error code: " + error.getCode();
            message += ", error value: " + error.getValue();
            hdLogger.log(Level.SEVERE, message);

        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getRecord(HarvestingDataverse dataverse, String identifier, String metadataPrefix) {
        return getRecord(logger, dataverse, identifier, metadataPrefix, null);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Long getRecord(Logger hdLogger, HarvestingDataverse dataverse, String identifier, String metadataPrefix, MutableBoolean recordErrorOccurred) {

        String errMessage = null;
        Study harvestedStudy = null;
        String oaiUrl = dataverse.getServerUrl();
        try {
            hdLogger.log(Level.INFO, "Calling GetRecord: oaiUrl =" + oaiUrl + "?verb=GetRecord&identifier=" + identifier + "&metadataPrefix=" + metadataPrefix);

            DvnFastGetRecord record = new DvnFastGetRecord(oaiUrl, identifier, metadataPrefix);
            errMessage = record.getErrorMessage();
            //errMessage=null;

            if (errMessage != null) {
                hdLogger.log(Level.SEVERE, "Error calling GetRecord - " + errMessage);
            } else if (record.isDeleted()) {
                hdLogger.log(Level.INFO, "Received 'deleted' status from OAI Server.");
                Study study = studyService.getStudyByHarvestInfo(dataverse.getVdc(), identifier);
                if (study != null) {
                    hdLogger.log(Level.INFO, "Deleting study " + study.getGlobalId());
                    studyService.deleteStudy(study.getId());
                } else {
                    hdLogger.log(Level.INFO, "No study found for this record, skipping delete. ");
                }

            } else {
                hdLogger.log(Level.INFO, "Successfully retreived GetRecord response.");

                VDCUser networkAdmin = vdcNetworkService.find().getDefaultNetworkAdmin();

                harvestedStudy = studyService.importHarvestStudy(record.getMetadataFile(), dataverse.getVdc().getId(), networkAdmin.getId(), identifier);
                //hdLogger.log(Level.INFO, "imported study (step 1., no data); proceeding with step 2.");
                //studyService.importHarvestStudyExperimental(harvestedStudyFile, harvestedStudy);
                hdLogger.log(Level.INFO, "Harvest Successful for identifier " + identifier);

        		this.processedSizeThisBatch += record.getMetadataFile().length();
                if ( this.harvestedStudyIdsThisBatch == null ) {
                    this.harvestedStudyIdsThisBatch = new ArrayList<Long>();
                }
                this.harvestedStudyIdsThisBatch.add(harvestedStudy.getId());

                if ( this.processedSizeThisBatch > 10000000 ) {

                    hdLogger.log(Level.INFO, "REACHED CONTENT BATCH SIZE LIMIT; calling index ("+this.harvestedStudyIdsThisBatch.size()+" studies in the batch).");
                    indexService.updateIndexList(this.harvestedStudyIdsThisBatch);
                    hdLogger.log(Level.INFO, "REINDEX DONE.");


                    this.processedSizeThisBatch = 0;
                    this.harvestedStudyIdsThisBatch = null;
                }
            }
        } catch (Throwable e) {
            errMessage = "Exception processing getRecord(), oaiUrl=" + oaiUrl + ",identifier=" + identifier + " " + e.getClass().getName() + " " + e.getMessage();
            hdLogger.log(Level.SEVERE, errMessage);
            logException(e, hdLogger);
                
        }

        // If we got an Error from the OAI server or an exception happened during import, then
        // set recordErrorOccurred to true (if recordErrorOccurred is being used)
        // otherwise throw an exception (if recordErrorOccurred is not used, i.e null)
        if (errMessage != null) {
            if (recordErrorOccurred  != null) {
                recordErrorOccurred.setValue(true);
            } else {
                throw new EJBException(errMessage);
            }
        }

        return harvestedStudy != null ? harvestedStudy.getId() : null;
    }
    
    public List<String> getMetadataFormats(String oaiUrl) {
        JAXBElement unmarshalObj;
        try {

            Document doc = new ListMetadataFormats(oaiUrl).getDocument();
            JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        } catch (TransformerException ex) {
            throw new EJBException(ex);
        } catch (ParserConfigurationException ex) {
            throw new EJBException(ex);
        } catch (JAXBException ex) {
            throw new EJBException(ex);
        } catch (SAXException ex) {
            throw new EJBException(ex);
        } catch (IOException ex) {
            throw new EJBException(ex);
        }

        OAIPMHtype OAIObj = (OAIPMHtype) unmarshalObj.getValue();
       if (OAIObj.getError()!=null && OAIObj.getError().size()>0) {
            List<OAIPMHerrorType> errList = OAIObj.getError();
            String errMessage="";
            for (OAIPMHerrorType error : OAIObj.getError()){
                 errMessage += error.getCode()+ " " +error.getValue(); 
            }
            throw new EJBException(errMessage);
        }
        ListMetadataFormatsType listMetadataFormats = OAIObj.getListMetadataFormats();
        List<String> formats = null;
        if (listMetadataFormats != null) {
            formats = new ArrayList<String>();
            for (Iterator it = listMetadataFormats.getMetadataFormat().iterator(); it.hasNext();) {
                //  Object elem = it.next();
                MetadataFormatType elem = (MetadataFormatType) it.next();
                formats.add(elem.getMetadataPrefix());
            }
        }
        return formats;
    }

    /**
     *
     *  SetDetailBean returned rather than the ListSetsType because we get strange errors when trying
     *  to refer to JAXB generated classes in both Web and EJB tiers.
     */
    public List<SetDetailBean> getSets(String oaiUrl) {
        JAXBElement unmarshalObj = null;

        try {
            ListSets listSets = new ListSets(oaiUrl);
            int nodeListLength = listSets.getErrors().getLength();
            if (nodeListLength==1) {
                 System.out.println("err Node: "+ listSets.getErrors().item(0));
            }
           
            
            Document doc = new ListSets(oaiUrl).getDocument();
            JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshalObj = (JAXBElement) unmarshaller.unmarshal(doc);
        } catch (ParserConfigurationException ex) {
            throw new EJBException(ex);
        } catch (SAXException ex) {
            throw new EJBException(ex);
        } catch (TransformerException ex) {
            throw new EJBException(ex);
        } catch (IOException ex) {
            throw new EJBException(ex);
        } catch (JAXBException ex) {
            throw new EJBException(ex);
        }
        List<SetDetailBean> sets = null;
        Object value = unmarshalObj.getValue();

        Package valPackage = value.getClass().getPackage();
        if (value instanceof edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHtype) {
            OAIPMHtype OAIObj = (OAIPMHtype) value;
            if (OAIObj.getError()!=null && OAIObj.getError().size()>0 ) {
                List<OAIPMHerrorType> errList = OAIObj.getError();
                String errMessage="";
                for (OAIPMHerrorType error : OAIObj.getError()){
                     // NO_SET_HIERARCHY is not an error from the perspective of the DVN,
                     // it just means that the OAI server doesn't support sets.
                     if (!error.getCode().equals(OAIPMHerrorcodeType.NO_SET_HIERARCHY)) {
                        errMessage += error.getCode()+ " " +error.getValue(); 
                     }
                }
                if (errMessage!="")  {
                     throw new EJBException(errMessage);
                }
               
            }
         
            ListSetsType listSetsType = OAIObj.getListSets();
            if (listSetsType != null) {
                sets = new ArrayList<SetDetailBean>();
                for (Iterator it = listSetsType.getSet().iterator(); it.hasNext();) {
                    SetType elem = (SetType) it.next();
                    SetDetailBean setDetail = new SetDetailBean();
                    setDetail.setName(elem.getSetName());
                    setDetail.setSpec(elem.getSetSpec());
                    sets.add(setDetail);
                }
            }
        }
        return sets;
    }

    private void logException(Throwable e, Logger logger) {

        boolean cause = false;
        String fullMessage = "";
        do {
            String message = e.getClass().getName() + " " + e.getMessage();
            if (cause) {
                message = "\nCaused By Exception.................... " + e.getClass().getName() + " " + e.getMessage();
            }
            StackTraceElement[] ste = e.getStackTrace();
            message += "\nStackTrace: \n";
            for (int m = 0; m < ste.length; m++) {
                message += ste[m].toString() + "\n";
            }
            fullMessage += message;
            cause = true;
        } while ((e = e.getCause()) != null);
        logger.severe(fullMessage);
    }
    
    public List<HarvestFormatType> findAllHarvestFormatTypes() {
        String queryStr = "SELECT f FROM HarvestFormatType f";
        Query query = em.createQuery(queryStr);
        return query.getResultList();
    }    
    
    public HarvestFormatType findHarvestFormatTypeByMetadataPrefix(String metadataPrefix) {
        String queryStr = "SELECT f FROM HarvestFormatType f WHERE f.metadataPrefix = '" + metadataPrefix + "'";
        Query query = em.createQuery(queryStr);
        List resultList = query.getResultList();
        HarvestFormatType hft = null;
        if (resultList.size() > 1) {
            throw new EJBException("More than one HarvestFormatType found with metadata Prefix= '" + metadataPrefix + "'");
        }
        if (resultList.size() == 1) {
            hft = (HarvestFormatType) resultList.get(0);
        }
        return hft;
    }
    
    private List<Long> harvestNesstar(HarvestingDataverse dataverse, Logger hdLogger, MutableBoolean harvestErrorOccurred, List<String> failedIdentifiers) throws MalformedURLException {
        VDCUser networkAdmin = vdcNetworkService.find().getDefaultNetworkAdmin();
        int count = 0;

        List<Long> harvestedStudyIds = new ArrayList<Long>();


            hdLogger.log(Level.INFO, "BEGIN HARVEST..., nesstarServer=" + dataverse.getServerUrl() + ", metadataPrefix=" + dataverse.getHarvestFormatType().getMetadataPrefix());

            //Instantiate the NesstarHarvester class:
            NesstarHarvester nh = new NesstarHarvester();
            //Add a server (remember to use a standards compliant URL)
            nh.addServer(dataverse.getServerUrl());
            hdLogger.log(Level.INFO, "Created an instance of NesstarHarvester; about to start harvest/retreival of Nesstar DDIs");
            //Harvest the server:
            DDI[] ddis = nh.harvest();
            hdLogger.log(Level.INFO, "Completed NesstarHarvester.harvest()");
            if (ddis != null) {
                hdLogger.log(Level.INFO, "NesstarHarvester.harvest() returned a list of " + ddis.length + " DDIs; attempting to import."); 
                for (DDI ddi : ddis) {
                    count++; 
                    Writer out = null; 
                    try {
                        //URL nesstarStudyURL = ddi.getStudy();
                        //if (nesstarStudyURL != null) {
                        //    hdLogger.log(Level.INFO, "Nesstar study URL: "+nesstarStudyURL.toString()+"; Bookmark: "+ddi.getBookmark()); 
                        //}
                        File xmlFile = File.createTempFile("study", ".xml");
                        out = new BufferedWriter(new FileWriter(xmlFile));
                        out.write( ddi.getXml() );
                        out.close();

                        //Study harvestedStudy = studyService.importHarvestStudy(xmlFile, dataverse.getVdc().getId(), networkAdmin.getId(), String.valueOf(++count) );
                        Study harvestedStudy = studyService.importHarvestStudy(xmlFile, dataverse.getVdc().getId(), networkAdmin.getId(), null );
                        if (harvestedStudy != null) {
                            hdLogger.log(Level.INFO, "local (database) id for the imported Nesstar study: "+harvestedStudy.getId());
                            if (harvestedStudy.getId() != null && !(harvestedStudy.getId().equals(""))) {
                                if (!(harvestedStudyIds.contains(harvestedStudy.getId()))) {
                                    harvestedStudyIds.add(harvestedStudy.getId());  
                                } else {
                                    hdLogger.log(Level.INFO, "Note: id "+harvestedStudy.getId()+" is already on the map - skipping!");
                                }                                
                            } else {
                                hdLogger.log(Level.WARNING, "Note: importHarvestStudy returned null or empty study id!");
                            }

                        } else {
                            hdLogger.log(Level.WARNING, "importHarvestStudy() returned null study!");
                        }

                    } catch (Exception e) {
                        String errMessage = "Exception Importing Nesstar DDI, identifier not available, sequential number in the harvest batch: " + String.valueOf(count) + " " + e.getClass().getName() + " " + e.getMessage();
                        hdLogger.log(Level.SEVERE, errMessage);
                        //logException(e, hdLogger);
                        failedIdentifiers.add( String.valueOf(count) );
                    }
                }
            } else {
                hdLogger.log(Level.WARNING, "NesstarHarvester.harvest() returned a null list of DDIs"); 
            }
            
     
      
        return harvestedStudyIds;

    }
}
