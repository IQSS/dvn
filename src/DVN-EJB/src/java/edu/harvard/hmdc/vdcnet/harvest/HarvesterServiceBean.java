/*
 * HarvesterServiceBean.java
 *
 * Created on February 12, 2007, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.harvest;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import ORG.oclc.oai.harvester2.verb.ListMetadataFormats;
import ORG.oclc.oai.harvester2.verb.ListSets;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.jaxb.oai.HeaderType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListIdentifiersType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListMetadataFormatsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ListSetsType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.MetadataFormatType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHerrorType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.OAIPMHtype;
import edu.harvard.hmdc.vdcnet.jaxb.oai.ResumptionTokenType;
import edu.harvard.hmdc.vdcnet.jaxb.oai.SetType;
import edu.harvard.hmdc.vdcnet.study.EditStudyService;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.util.FileUtil;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverse;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceBean;
import edu.harvard.hmdc.vdcnet.vdc.HarvestingDataverseServiceLocal;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless( name="harvesterService")

@EJB(name="editStudyService", beanInterface=edu.harvard.hmdc.vdcnet.study.EditStudyService.class)
public class HarvesterServiceBean implements HarvesterServiceLocal {
    @PersistenceUnit(unitName="VDCNet-test") EntityManagerFactory emf;
    @PersistenceContext(unitName="VDCNet-ejbPU") EntityManager em;
    
    @Resource javax.ejb.TimerService timerService;
    @Resource SessionContext ejbContext;
    @EJB StudyServiceLocal studyService;
    @EJB HarvestingDataverseServiceLocal havestingDataverseService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.harvest.HarvestServiceBean");
    private static final String HARVEST_TIMER = "HarvestTimer";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    
    static {
        try {
            logger.addHandler(new FileHandler(FileUtil.getImportFileDir()+ File.separator+ "harvest.log"));
        } catch(IOException e) {
            throw new EJBException(e);
        }
    }
    
    
    /**
     * Creates a new instance of HarvesterServiceBean
     */
    public HarvesterServiceBean() {
        
    }
    
    
    public void doAsyncHarvest(HarvestingDataverse dataverse) {
        Calendar cal = Calendar.getInstance();
     
        timerService.createTimer(cal.getTime(),dataverse.getId());
    }
    
    public void createHarvestTimer() {
        for (Iterator it = timerService.getTimers().iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if (timer.getInfo().equals(HARVEST_TIMER)) {
                logger.info("Cannot create HarvestTimer, timer already exists.");
                logger.info("HarvestTimer next timeout is " +timer.getNextTimeout());
                return;
           
            }
            
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,1);
        cal.set(Calendar.HOUR_OF_DAY,1);
        
        // Test Only - have timer expire in 5 minutes
        // cal.add(Calendar.MINUTE,5);
        logger.log(Level.INFO,"Harvester timer set for "+cal.getTime());
        System.out.println("Harvester timer set for "+cal.getTime());
        Date initialExpiration = cal.getTime();  // First timeout is 1:00 AM of next day
        long intervalDuration = 1000*60 *60*24;  // repeat every 24 hours
        timerService.createTimer(initialExpiration, intervalDuration,HARVEST_TIMER);
        
    }
    
    
    @Timeout
    public void handleTimeout(javax.ejb.Timer timer) {
        if (timer.getInfo().equals(HARVEST_TIMER)) {
            doScheduledHarvesting();
        } else {
            doImmediateHarvesting((Long)timer.getInfo());
        }
    }
    
    
    public void doImmediateHarvesting(Long dataverseId) {
        System.out.println("DO immediate HARVESTING of "+dataverseId);
        HarvestingDataverse dataverse = em.find(HarvestingDataverse.class, dataverseId);
        harvest(dataverse);
        
    }
    
    public void doScheduledHarvesting() {
            // Get list of Harvested dataverses
            // For each dataverse that is scheduled, call OAIServer to get list of updated records.
            // Call import to save each study in the database.
            List dataverses = havestingDataverseService.findAll();
            
            for (Iterator it = dataverses.iterator(); it.hasNext();) {
                HarvestingDataverse dataverse = (HarvestingDataverse) it.next();
                harvest(dataverse);
            }
    }
    
    
    public void harvest(HarvestingDataverse dataverse) {
        if (dataverse.isHarvestingNow()) {
                throw new EJBException("Cannot begin harvesting, Dataverse "+dataverse.getVdc().getName()+" is currently being harvested.");
        }
        String from = null;
        Date today = new Date();
        String until= formatter.format(today);
        if (dataverse.getLastHarvestTime()!=null) {
            from= formatter.format(dataverse.getLastHarvestTime());
        }
        harvest(dataverse,from, until);
    }
    
    
    public void harvest( HarvestingDataverse dataverse, String from, String until) {
        Logger hdLogger = Logger.getLogger("edu.harvard.hmdc.vdcnet.harvest.HarvestServiceBean."+dataverse.getVdc().getAlias());
   
        try {
            hdLogger.addHandler(new FileHandler(FileUtil.getImportFileDir()+ File.separator+ "harvest_"+dataverse.getVdc().getAlias()+".log"));
        } catch(IOException e) {
            throw new EJBException(e);
        }
   
        Date lastHarvestTime;
        try {
          
            havestingDataverseService.setHarvestingNow(dataverse.getId(), true);
            lastHarvestTime = formatter.parse(until);
            hdLogger.log(Level.INFO,"BEGIN HARVEST..., oaiUrl="+dataverse.getOaiServer()+",set="+ dataverse.getHarvestingSet()+", metadataPrefix="+dataverse.getFormat()+ ", from="+from+", until="+until);
            ResumptionTokenType resumptionToken = null;
            do {
                resumptionToken= harvestFromIdentifiers(hdLogger, resumptionToken,dataverse,from,until);
            } while(resumptionToken!=null);
            
            // Get managed version of the dataverse so that update to lastHarvestTime will be persisted
            dataverse = em.find(HarvestingDataverse.class, dataverse.getId());         
            dataverse.setLastHarvestTime(lastHarvestTime);
            
            hdLogger.log(Level.INFO,"COMPLETED HARVEST, oaiUrl="+dataverse.getOaiServer()+",set="+ dataverse.getHarvestingSet()+", metadataPrefix="+dataverse.getFormat()+ ", from="+from+", until="+until);
 
        } catch (ParseException ex) {
            hdLogger.log(Level.SEVERE, "ParseException harvesting dataverse "+dataverse.getVdc().getName()+", until Str="+until+", exception: "+ex.getMessage() );
        } finally {
             havestingDataverseService.setHarvestingNow(dataverse.getId(), false);
        }
        
    }
    
    
    
    private ResumptionTokenType harvestFromIdentifiers(Logger hdLogger, ResumptionTokenType resumptionToken, HarvestingDataverse dataverse, String from, String until) {
        String encodedSet=null;
        
        try {
            encodedSet = dataverse.getHarvestingSet()==null ? null : URLEncoder.encode(dataverse.getHarvestingSet(), "UTF-8");
            ListIdentifiers listIdentifiers=null;
            if (resumptionToken==null) {
                listIdentifiers= new ListIdentifiers( dataverse.getOaiServer(),
                        from,
                        until,
                        encodedSet,
                        URLEncoder.encode(dataverse.getFormat(),"UTF-8"));
            } else {
                listIdentifiers= new ListIdentifiers(dataverse.getOaiServer(), resumptionToken.getValue());
            }
            Document doc =listIdentifiers.getDocument();
            
            JAXBContext jc = JAXBContext.newInstance("edu.harvard.hmdc.vdcnet.jaxb.oai");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement unmarshalObj = (JAXBElement)unmarshaller.unmarshal(doc);
            OAIPMHtype oaiObj = (OAIPMHtype)unmarshalObj.getValue();
            
            if (oaiObj.getError()!=null && oaiObj.getError().size()>0) {
                handleOAIError(hdLogger, oaiObj, "calling listIdentifiers, oaiServer= "+dataverse.getOaiServer()+",from="+from+",until="+until+",encodedSet="+encodedSet+",format="+dataverse.getFormat());
            } else {
                ListIdentifiersType listIdentifiersType = oaiObj.getListIdentifiers();
                if (listIdentifiersType!=null) {
                    resumptionToken= listIdentifiersType.getResumptionToken();
                    for (Iterator it = listIdentifiersType.getHeader().iterator(); it.hasNext();) {
                        HeaderType header = (HeaderType) it.next(); 
                        getRecord(hdLogger, dataverse, header.getIdentifier(),dataverse.getFormat(),jc);
                    }
                    
                }
            }
        } catch (Exception e) {
            hdLogger.log(Level.SEVERE, "Exception processing listIdentifiers(), oaiServer= "+dataverse.getOaiServer()+",from="+from+",until="+until+",encodedSet="+encodedSet+",format="+dataverse.getFormat()+" "+ e.getClass().getName()+ " "+e.getMessage());
            if (e.getCause()!=null) {
                String stackTrace = "StackTrace: \n";
                hdLogger.severe("Exception caused by: "+e.getCause()+"\n, nested Exception: "+e.getCause().getCause());            
                StackTraceElement[] ste = e.getCause().getStackTrace();
                for(int m=0;m<ste.length;m++) {
                    stackTrace+=ste[m].toString()+"\n";
                }
                hdLogger.severe(stackTrace);
            }          
            throw new EJBException(e);
        }
        return resumptionToken;
    }
    
    private void handleOAIError(Logger hdLogger, OAIPMHtype oaiObj, String message) {
        for (Iterator it = oaiObj.getError().iterator(); it.hasNext();) {
            OAIPMHerrorType error = (OAIPMHerrorType)it.next();
            message +=", error code: "+error.getCode();
            message +=", error value: "+error.getValue();
            hdLogger.log(Level.SEVERE,message);
            
        }
    }
    
    
  
    private void getRecord(Logger hdLogger, HarvestingDataverse dataverse, String identifier, String metadataPrefix, JAXBContext jc) {
        String oaiUrl= dataverse.getOaiServer();
        hdLogger.log(Level.INFO,"Calling GetRecord: oaiUrl= "+oaiUrl+", identifier= "+identifier+", metadataPrefix= "+metadataPrefix);
        try {
            
            GetRecord record = new GetRecord(oaiUrl, identifier, metadataPrefix);
            String errMessage=record.getErrorMessage();
            if (errMessage!=null) {
                hdLogger.log(Level.SEVERE,"Error calling GetRecord - "+errMessage);
            } else if (record.isDeleted()) {
                Study study = studyService.getStudyByGlobalId(identifier);
                studyService.deleteStudy(study.getId());
            } else {
                VDCUser networkAdmin = vdcNetworkService.find().getDefaultNetworkAdmin();
                Study study= studyService.getStudyByGlobalId(identifier);
                Long studyId = study != null ? study.getId() : null;
               
                studyService.importHarvestStudy(record.getMetadataFile(), studyId, dataverse.getVdc().getId(),networkAdmin.getId());
                
                hdLogger.log(Level.INFO,"Harvest Successful for identifier "+identifier );
            }
            
        } catch (Exception e) {
            hdLogger.log(Level.SEVERE,"Exception processing getRecord(), oaiUrl="+oaiUrl+",identifier="+identifier +" "+ e.getClass().getName()+" "+ e.getMessage());
            if (e.getCause()!=null) {
                String stackTrace = "StackTrace: \n";
                hdLogger.severe("Exception caused by: "+e.getCause()+"\n, nested Exception: "+e.getCause().getCause());
                StackTraceElement[] ste = e.getCause().getStackTrace();
                for(int m=0;m<ste.length;m++) {
                    stackTrace+=ste[m].toString()+"\n";
                }
                hdLogger.severe(stackTrace);
            }
           
        }
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
        
        OAIPMHtype OAIObj = (OAIPMHtype)unmarshalObj.getValue();
        ListMetadataFormatsType listMetadataFormats = OAIObj.getListMetadataFormats();
        List<String> formats = null;
        if (listMetadataFormats!=null) {
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
        JAXBElement unmarshalObj=null;
        
        try {
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
            OAIPMHtype OAIObj = (OAIPMHtype)value;
            ListSetsType listSetsType = OAIObj.getListSets();
            
            
            if (listSetsType!=null) {
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
    
    
    
}
