/*
 * DDIServiceBean.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.ddi;

import edu.harvard.hmdc.vdcnet.jaxb.ddi20.FileDscrType;
import edu.harvard.hmdc.vdcnet.jaxb.ddi20.ProducerType;
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAbstract;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyDistributor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyGeoBounding;
import edu.harvard.hmdc.vdcnet.study.StudyGrant;
import edu.harvard.hmdc.vdcnet.study.StudyKeyword;
import edu.harvard.hmdc.vdcnet.study.StudyNote;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.StudyOtherRef;
import edu.harvard.hmdc.vdcnet.study.StudyProducer;
import edu.harvard.hmdc.vdcnet.study.StudyRelMaterial;
import edu.harvard.hmdc.vdcnet.study.StudyRelPublication;
import edu.harvard.hmdc.vdcnet.study.StudyRelStudy;
import edu.harvard.hmdc.vdcnet.study.StudySoftware;
import edu.harvard.hmdc.vdcnet.study.StudyTopicClass;
import edu.harvard.hmdc.vdcnet.study.SummaryStatistic;
import edu.harvard.hmdc.vdcnet.study.SummaryStatisticType;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;
import edu.harvard.hmdc.vdcnet.study.VariableFormatType;
import edu.harvard.hmdc.vdcnet.study.VariableIntervalType;
import edu.harvard.hmdc.vdcnet.study.VariableRange;
import edu.harvard.hmdc.vdcnet.study.VariableRangeType;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.util.DateUtil;
import edu.harvard.hmdc.vdcnet.util.PropertyUtil;
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author Gustavo
 */
@Stateless
public class DDIServiceBean implements DDIServiceLocal {

    @EJB VariableServiceLocal varService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    
    // ddi constants
    public static final String AGENCY_HANDLE = "handle";
    public static final String REPLICATION_FOR_TYPE = "replicationFor";
    public static final String VAR_WEIGHTED = "wgtd";
    public static final String VAR_INTERVAL_CONTIN = "contin";
    
    public static final String EVENT_START = "start";
    public static final String EVENT_END = "end";
    public static final String EVENT_SINGLE = "single";
    
    public static final String LEVEL_STUDY = "study";
    public static final String LEVEL_FILE = "file";
    public static final String LEVEL_VARIABLE = "variable";
    public static final String LEVEL_CATEGORY = "category";
    
    public static final String NOTE_TYPE_UNF = "VDC:UNF";
    public static final String NOTE_SUBJECT_UNF = "Universal Numeric Fingerprint";
    
    public static final String NOTE_TYPE_TERMS_OF_USE = "DVN:TOU";
    public static final String NOTE_SUBJECT_TERMS_OF_USE = "Dataverse Terms Of Use";
    
    // db constants
    public static final String DB_VAR_INTERVAL_TYPE_CONTINUOUS = "continuous";
    public static final String DB_VAR_RANGE_TYPE_POINT = "point";
    public static final String DB_VAR_RANGE_TYPE_MIN = "min";
    public static final String DB_VAR_RANGE_TYPE_MIN_EX = "min exclusive";
    public static final String DB_VAR_RANGE_TYPE_MAX = "max";
    public static final String DB_VAR_RANGE_TYPE_MAX_EX = "max exclusive";    
    
    public List<VariableFormatType> variableFormatTypeList =  null;
    public List<VariableIntervalType> variableIntervalTypeList =  null;
    public List<SummaryStatisticType> summaryStatisticTypeList =  null;
    public List<VariableRangeType> variableRangeTypeList =  null;    

    public void ejbCreate() {
        // initialize lists
        variableFormatTypeList = varService.findAllVariableFormatType();
        variableIntervalTypeList = varService.findAllVariableIntervalType();
        summaryStatisticTypeList = varService.findAllSummaryStatisticType();
        variableRangeTypeList = varService.findAllVariableRangeType();
    }
    
    public boolean isXmlFormat() {
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="export Methods">
    //**********************
    // EXPORT METHODS
    //**********************

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study s, OutputStream os) throws IOException {
        try {
            javax.xml.stream.XMLOutputFactory xmlof = javax.xml.stream.XMLOutputFactory.newInstance();
            //xmlof.setProperty("javax.xml.stream.isPrefixDefaulting", java.lang.Boolean.TRUE);
            javax.xml.stream.XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os);
            xmlw.writeStartDocument();
            //xmlw.writeProcessingInstruction("xml-stylesheet href=\'catalog.xsl\' type=\'text/xsl\'");
            createCodeBook(xmlw,s);
            xmlw.writeEndDocument();
            xmlw.close();
            
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        
    }

    private void createCodeBook(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        xmlw.writeStartElement("codeBook");
        xmlw.writeDefaultNamespace("http://www.icpsr.umich.edu/DDI");
        writeAttribute( xmlw, "version", "2.0" );

        createDocDscr(xmlw, study);
        createStdyDscr(xmlw, study);

        // iterate through files, saving other material files for the end
        List<StudyFile> otherMatFiles = new ArrayList();
        for (StudyFile sf : study.getStudyFiles()) {
            if ( sf.isSubsettable() ) {
                createFileDscr(xmlw, sf);
            } else {
                otherMatFiles.add(sf);
            }
        }

        createDataDscr(xmlw, study);

        // now go through otherMat files
        for (StudyFile sf : otherMatFiles) {
            createOtherMat(xmlw, sf);
        }

        xmlw.writeEndElement(); // codeBook
    }

    private void createDocDscr(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        xmlw.writeStartElement("docDscr");
        xmlw.writeStartElement("citation");
        
        // titlStmt
        xmlw.writeStartElement("titlStmt");

        xmlw.writeStartElement("titl"); 
        xmlw.writeCharacters( study.getTitle() );
        xmlw.writeEndElement(); // titl  

        xmlw.writeStartElement("IDNo");
        writeAttribute( xmlw, "agency", "handle" );
        xmlw.writeCharacters( study.getGlobalId() );
        xmlw.writeEndElement(); // IDNo

        xmlw.writeEndElement(); // titlStmt
        
        // distStmt
        xmlw.writeStartElement("distStmt");

        xmlw.writeStartElement("distrbtr");
        xmlw.writeCharacters( vdcNetworkService.find().getName() + " Dataverse Network" );
        xmlw.writeEndElement(); // distrbtr

        String lastUpdateString = new SimpleDateFormat("yyyy-MM-dd").format(study.getLastUpdateTime());
        createDateElement( xmlw, "distDate", lastUpdateString );

        // holdings
        xmlw.writeEmptyElement("holdings");
        writeAttribute( xmlw, "uri", "http://" + PropertyUtil.getHostUrl() + "/dvn/study?globalId=" + study.getGlobalId() );

        xmlw.writeEndElement(); // distStmt

        xmlw.writeEndElement(); // citation
        xmlw.writeEndElement(); // docDscr
    }

    private void createStdyDscr(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        xmlw.writeStartElement("stdyDscr");
        createCitation(xmlw, study);
        createStdyInfo(xmlw, study);
        createMethod(xmlw, study);
        createDataAccs(xmlw, study);
        createOthrStdyMat(xmlw,study);
        createNotes(xmlw,study);
        xmlw.writeEndElement(); // stdyDscr
    }

    private void createCitation(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        xmlw.writeStartElement("citation");
        
        // titlStmt
        xmlw.writeStartElement("titlStmt");

        xmlw.writeStartElement("titl"); 
        xmlw.writeCharacters( study.getTitle() );
        xmlw.writeEndElement(); // titl  

        if ( !StringUtil.isEmpty( study.getSubTitle() ) ) {
            xmlw.writeStartElement("subTitl"); 
            xmlw.writeCharacters( study.getSubTitle() );
            xmlw.writeEndElement(); // subTitl  
        }

        xmlw.writeStartElement("IDNo");
        writeAttribute( xmlw, "agency", "handle" );
        xmlw.writeCharacters( study.getGlobalId() );
        xmlw.writeEndElement(); // IDNo

        for (StudyOtherId otherId : study.getStudyOtherIds()) {
            xmlw.writeStartElement("IDNo");
            writeAttribute( xmlw, "agency", otherId.getAgency() );
            xmlw.writeCharacters( otherId.getOtherId() );
            xmlw.writeEndElement(); // IDNo
        }

        xmlw.writeEndElement(); // titlStmt

        // rspStmt
        if (study.getStudyAuthors() != null && study.getStudyAuthors().size() > 0) {
            xmlw.writeStartElement("rspStmt");
            for (StudyAuthor author : study.getStudyAuthors()) {
                xmlw.writeStartElement("AuthEnty");
                if ( !StringUtil.isEmpty(author.getAffiliation()) ) {
                    writeAttribute( xmlw, "affiliation", author.getAffiliation() );
                }
                xmlw.writeCharacters( author.getName() );
                xmlw.writeEndElement(); // AuthEnty
            }
            xmlw.writeEndElement(); // rspStmt
        }


        // prodStmt
        boolean prodStmtAdded = false;
        for (StudyProducer prod : study.getStudyProducers()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("producer");
            writeAttribute( xmlw, "abbr", prod.getAbbreviation() );
            writeAttribute( xmlw, "affiliation", prod.getAffiliation() );
            xmlw.writeCharacters( prod.getName() );
            createExtLink(xmlw, prod.getUrl(), null);
            createExtLink(xmlw, prod.getLogo(), "image");
            xmlw.writeEndElement(); // producer
        }
        if (!StringUtil.isEmpty( study.getProductionDate() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            createDateElement( xmlw, "prodDate", study.getProductionDate() );
        }
        if (!StringUtil.isEmpty( study.getProductionPlace() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("prodPlac");
            xmlw.writeCharacters( study.getProductionPlace() );
            xmlw.writeEndElement(); // prodPlac
        }
        for (StudySoftware soft : study.getStudySoftware()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("software");
            writeAttribute( xmlw, "version", soft.getSoftwareVersion() );
            xmlw.writeCharacters( soft.getName() );
            xmlw.writeEndElement(); // software
        }
        if (!StringUtil.isEmpty( study.getFundingAgency() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("fundAg");
            xmlw.writeCharacters( study.getFundingAgency() );
            xmlw.writeEndElement(); // fundAg
        }
        for (StudyGrant grant : study.getStudyGrants()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("grantNo");
            writeAttribute( xmlw, "agency", grant.getAgency() );
            xmlw.writeCharacters( grant.getNumber() );
            xmlw.writeEndElement(); // grantNo
        }
        if (prodStmtAdded) xmlw.writeEndElement(); // prodStmt

        
        // distStmt
        boolean distStmtAdded = false;
        for (StudyDistributor dist : study.getStudyDistributors()) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            xmlw.writeStartElement("distrbtr");
            writeAttribute( xmlw, "abbr", dist.getAbbreviation() );
            writeAttribute( xmlw, "affiliation", dist.getAffiliation() );
            xmlw.writeCharacters( dist.getName() );
            createExtLink(xmlw, dist.getUrl(), null);
            createExtLink(xmlw, dist.getLogo(), "image");
            xmlw.writeEndElement(); // distrbtr
        }
        if (!StringUtil.isEmpty( study.getDistributorContact()) ||
                !StringUtil.isEmpty( study.getDistributorContactEmail()) ||
                !StringUtil.isEmpty( study.getDistributorContactAffiliation()) ) {

            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);          
            xmlw.writeStartElement("contact");
            writeAttribute( xmlw, "email", study.getDistributorContactEmail() );
            writeAttribute( xmlw, "affiliation", study.getDistributorContactAffiliation() );
            xmlw.writeCharacters( study.getDistributorContact() );
            xmlw.writeEndElement(); // contact
        }
        if (!StringUtil.isEmpty( study.getDepositor() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            xmlw.writeStartElement("depositr");
            xmlw.writeCharacters( study.getDepositor() );
            xmlw.writeEndElement(); // depositr
        }
        if (!StringUtil.isEmpty( study.getDateOfDeposit() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            createDateElement( xmlw, "depDate", study.getDateOfDeposit() );
        } 
        if (!StringUtil.isEmpty( study.getDistributionDate() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            createDateElement( xmlw, "distDate", study.getDistributionDate() );
        }        
        if (distStmtAdded) xmlw.writeEndElement(); // distStmt


        // serStmt
        boolean serStmtAdded = false;
        if (!StringUtil.isEmpty( study.getSeriesName() )) {
            serStmtAdded = checkParentElement(xmlw, "serStmt", serStmtAdded);
            xmlw.writeStartElement("serName");
            xmlw.writeCharacters( study.getSeriesName() );
            xmlw.writeEndElement(); // serName
        }
        
        if (!StringUtil.isEmpty( study.getSeriesInformation() )) {
            serStmtAdded = checkParentElement(xmlw, "serStmt", serStmtAdded);
            xmlw.writeStartElement("serInfo");
            xmlw.writeCharacters( study.getSeriesInformation() );
            xmlw.writeEndElement(); // serInfo
        }
        if (serStmtAdded) xmlw.writeEndElement(); // serStmt


        // verStmt
        boolean verStmtAdded = false;
        if (!StringUtil.isEmpty( study.getStudyVersion()) || !StringUtil.isEmpty( study.getVersionDate()) ) {
            verStmtAdded = checkParentElement(xmlw, "verStmt", verStmtAdded);
            xmlw.writeStartElement("version");
            writeAttribute( xmlw, "date", study.getVersionDate() );
            xmlw.writeCharacters( study.getStudyVersion() );
            xmlw.writeEndElement(); // version
        }
       if (verStmtAdded) xmlw.writeEndElement(); // verStmt

        // UNF note
        if (! StringUtil.isEmpty( study.getUNF()) ) {
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "level", LEVEL_STUDY );
            writeAttribute( xmlw, "type", NOTE_TYPE_UNF );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_UNF );
            xmlw.writeCharacters( study.getUNF() );
            xmlw.writeEndElement(); // notes
        }

        xmlw.writeEndElement(); // citation
    }

    private void createStdyInfo(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        boolean stdyInfoAdded = false;

        // subject
        boolean subjectAdded = false;
        for (StudyKeyword kw : study.getStudyKeywords()) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            subjectAdded = checkParentElement(xmlw, "subject", subjectAdded);
            xmlw.writeStartElement("keyword");
            writeAttribute( xmlw, "vocab", kw.getVocab() );
            writeAttribute( xmlw, "vocabURI", kw.getVocabURI() );
            xmlw.writeCharacters( kw.getValue() );
            xmlw.writeEndElement(); // keyword
        }
        for (StudyTopicClass tc : study.getStudyTopicClasses()) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            subjectAdded = checkParentElement(xmlw, "subject", subjectAdded);
            xmlw.writeStartElement("topcClas");
            writeAttribute( xmlw, "vocab", tc.getVocab() );
            writeAttribute( xmlw, "vocabURI", tc.getVocabURI() );
            xmlw.writeCharacters( tc.getValue() );
            xmlw.writeEndElement(); // topcClas
        }
        if (subjectAdded) xmlw.writeEndElement(); // subject


        // abstract
        for (StudyAbstract abst : study.getStudyAbstracts()) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            xmlw.writeStartElement("abstract");
            writeAttribute( xmlw, "date", abst.getDate() );
            xmlw.writeCharacters( abst.getText() );
            xmlw.writeEndElement(); // abstract
        }


        // sumDscr
        boolean sumDscrAdded = false;
        if (!StringUtil.isEmpty( study.getTimePeriodCoveredStart() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("timePrd");
            writeAttribute( xmlw, "event", EVENT_START );
            writeDateAttribute( xmlw, study.getTimePeriodCoveredStart() );
            xmlw.writeCharacters( study.getTimePeriodCoveredStart() );
            xmlw.writeEndElement(); // timePrd
        }
        if (!StringUtil.isEmpty( study.getTimePeriodCoveredEnd() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("timePrd");
            writeAttribute( xmlw, "event", EVENT_END );
            writeDateAttribute( xmlw, study.getTimePeriodCoveredEnd() );
            xmlw.writeCharacters( study.getTimePeriodCoveredEnd() );
            xmlw.writeEndElement(); // timePrd
        }
        if (!StringUtil.isEmpty( study.getDateOfCollectionStart() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("collDate");
            writeAttribute( xmlw, "event", EVENT_START );
            writeDateAttribute( xmlw, study.getDateOfCollectionStart() );
            xmlw.writeCharacters( study.getDateOfCollectionStart() );
            xmlw.writeEndElement(); // collDate
        }
        if (!StringUtil.isEmpty( study.getDateOfCollectionEnd() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("collDate");
            writeAttribute( xmlw, "event", EVENT_END );
            writeDateAttribute( xmlw, study.getDateOfCollectionEnd() );
            xmlw.writeCharacters( study.getDateOfCollectionEnd() );
            xmlw.writeEndElement(); // collDate
        }
        if (!StringUtil.isEmpty( study.getCountry() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("nation");
            xmlw.writeCharacters( study.getCountry() );
            xmlw.writeEndElement(); // nation
        }
        if (!StringUtil.isEmpty( study.getGeographicCoverage() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("geogCover");
            xmlw.writeCharacters( study.getGeographicCoverage() );
            xmlw.writeEndElement(); // geogCover
        }
        if (!StringUtil.isEmpty( study.getGeographicUnit() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("geogUnit");
            xmlw.writeCharacters( study.getGeographicCoverage() );
            xmlw.writeEndElement(); // geogUnit
        }
        // we store geoboundings as list but there is only one
        if (study.getStudyGeoBoundings() != null && study.getStudyGeoBoundings().size() != 0) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            StudyGeoBounding gbb = study.getStudyGeoBoundings().get(0);
            xmlw.writeStartElement("geoBndBox");
            xmlw.writeStartElement("westBL");
            xmlw.writeCharacters( gbb.getWestLongitude() );
            xmlw.writeEndElement(); // westBL
            xmlw.writeStartElement("eastBL");
            xmlw.writeCharacters( gbb.getEastLongitude() );
            xmlw.writeEndElement(); // eastBL
            xmlw.writeStartElement("southBL");
            xmlw.writeCharacters( gbb.getSouthLatitude() );
            xmlw.writeEndElement(); // southBL
            xmlw.writeStartElement("northBL");
            xmlw.writeCharacters( gbb.getNorthLatitude() );
            xmlw.writeEndElement(); // northBL
            xmlw.writeEndElement(); // geoBndBox
        }
        
        if (!StringUtil.isEmpty( study.getUnitOfAnalysis() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("anlyUnit");
            xmlw.writeCharacters( study.getUnitOfAnalysis() );
            xmlw.writeEndElement(); // anlyUnit
        }
        
        if (!StringUtil.isEmpty( study.getUniverse() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("universe");
            xmlw.writeCharacters( study.getUniverse() );
            xmlw.writeEndElement(); // universe
        }
        
        if (!StringUtil.isEmpty( study.getKindOfData() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("dataKind");
            xmlw.writeCharacters( study.getKindOfData() );
            xmlw.writeEndElement(); // dataKind
        }
        if (sumDscrAdded) xmlw.writeEndElement(); // sumDscr


        if (stdyInfoAdded) xmlw.writeEndElement(); // stdyInfo
    }

    private void createMethod(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        boolean methodAdded = false;

        // dataColl
        boolean dataCollAdded = false;
        if (!StringUtil.isEmpty( study.getTimeMethod() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("timeMeth");
            xmlw.writeCharacters( study.getTimeMethod() );
            xmlw.writeEndElement(); // timeMeth
        }
        if (!StringUtil.isEmpty( study.getDataCollector() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("dataCollector");
            xmlw.writeCharacters( study.getDataCollector() );
            xmlw.writeEndElement(); // dataCollector
        }
        if (!StringUtil.isEmpty( study.getFrequencyOfDataCollection() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("frequenc");
            xmlw.writeCharacters( study.getDataCollector() );
            xmlw.writeEndElement(); // frequenc
        }
        if (!StringUtil.isEmpty( study.getSamplingProcedure() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("sampProc");
            xmlw.writeCharacters( study.getSamplingProcedure() );
            xmlw.writeEndElement(); // sampProc
        }
        if (!StringUtil.isEmpty( study.getDeviationsFromSampleDesign() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("deviat");
            xmlw.writeCharacters( study.getDeviationsFromSampleDesign() );
            xmlw.writeEndElement(); // deviat
        }
        if (!StringUtil.isEmpty( study.getCollectionMode() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("collMode");
            xmlw.writeCharacters( study.getCollectionMode() );
            xmlw.writeEndElement(); // collMode
        }
        
        if (!StringUtil.isEmpty( study.getResearchInstrument() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("resInstru");
            xmlw.writeCharacters( study.getResearchInstrument() );
            xmlw.writeEndElement(); // resInstru
        }
        //source
        boolean sourcesAdded = false;
        if (!StringUtil.isEmpty( study.getDataSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("dataSrc");
            xmlw.writeCharacters( study.getDataSources() );
            xmlw.writeEndElement(); // dataSrc
        }
        
        if (!StringUtil.isEmpty( study.getOriginOfSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcOrig");
            xmlw.writeCharacters( study.getOriginOfSources() );
            xmlw.writeEndElement(); // srcOrig
        }
        
        if (!StringUtil.isEmpty( study.getCharacteristicOfSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcChar");
            xmlw.writeCharacters( study.getCharacteristicOfSources() );
            xmlw.writeEndElement(); // srcChar
        }
        
        if (!StringUtil.isEmpty( study.getAccessToSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcDocu");
            xmlw.writeCharacters( study.getAccessToSources() );
            xmlw.writeEndElement(); // srcDocu
        }
        if (sourcesAdded) xmlw.writeEndElement(); // sources
        
        if (!StringUtil.isEmpty( study.getDataCollectionSituation() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("collSitu");
            xmlw.writeCharacters( study.getDataCollectionSituation() );
            xmlw.writeEndElement(); // collSitu
        }
        
        if (!StringUtil.isEmpty( study.getActionsToMinimizeLoss() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("actMin");
            xmlw.writeCharacters( study.getActionsToMinimizeLoss() );
            xmlw.writeEndElement(); // actMin
        }
        
        if (!StringUtil.isEmpty( study.getControlOperations() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("ConOps");
            xmlw.writeCharacters( study.getControlOperations() );
            xmlw.writeEndElement(); // ConOps
        }
        
        if (!StringUtil.isEmpty( study.getWeighting() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("weight");
            xmlw.writeCharacters( study.getWeighting() );
            xmlw.writeEndElement(); // weight
        }
        
        if (!StringUtil.isEmpty( study.getCleaningOperations() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("cleanOps");
            xmlw.writeCharacters( study.getCleaningOperations() );
            xmlw.writeEndElement(); // cleanOps
        }
        if (dataCollAdded) xmlw.writeEndElement(); // dataColl


        // notes
        if (!StringUtil.isEmpty( study.getStudyLevelErrorNotes() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            xmlw.writeStartElement("notes");
            xmlw.writeCharacters( study.getStudyLevelErrorNotes() );
            xmlw.writeEndElement(); // notes
        }


        // anlyInfo
        boolean anlyInfoAdded = false;
        if (!StringUtil.isEmpty( study.getResponseRate() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("respRate");
            xmlw.writeCharacters( study.getResponseRate() );
            xmlw.writeEndElement(); // getResponseRate
        }
        
        if (!StringUtil.isEmpty( study.getSamplingErrorEstimate() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("EstSmpErr");
            xmlw.writeCharacters( study.getSamplingErrorEstimate() );
            xmlw.writeEndElement(); // EstSmpErr
        }
        
        if (!StringUtil.isEmpty( study.getOtherDataAppraisal() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("dataAppr");
            xmlw.writeCharacters( study.getOtherDataAppraisal() );
            xmlw.writeEndElement(); // dataAppr
        }
        if (anlyInfoAdded) xmlw.writeEndElement(); // anlyInfo


        if (methodAdded) xmlw.writeEndElement(); // method
    }

    private void createDataAccs(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        boolean dataAccsAdded = false;

        // setAvail
        boolean setAvailAdded = false;
        if (!StringUtil.isEmpty( study.getPlaceOfAccess() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("accsPlac");
            xmlw.writeCharacters( study.getPlaceOfAccess() );
            xmlw.writeEndElement(); // getStudyCompletion
        }
        if (!StringUtil.isEmpty( study.getOriginalArchive() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("origArch");
            xmlw.writeCharacters( study.getOriginalArchive() );
            xmlw.writeEndElement(); // origArch
        }
        if (!StringUtil.isEmpty( study.getAvailabilityStatus() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("avlStatus");
            xmlw.writeCharacters( study.getAvailabilityStatus() );
            xmlw.writeEndElement(); // avlStatus
        }
        if (!StringUtil.isEmpty( study.getCollectionSize() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("collSize");
            xmlw.writeCharacters( study.getCollectionSize() );
            xmlw.writeEndElement(); // collSize
        }
        if (!StringUtil.isEmpty( study.getStudyCompletion() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("complete");
            xmlw.writeCharacters( study.getStudyCompletion() );
            xmlw.writeEndElement(); // complete
        }
        if (setAvailAdded) xmlw.writeEndElement(); // setAvail

        // useStmt
        boolean useStmtAdded = false;
        if (!StringUtil.isEmpty( study.getConfidentialityDeclaration() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("confDec");
            xmlw.writeCharacters( study.getConfidentialityDeclaration() );
            xmlw.writeEndElement(); // confDec
        }
        
        if (!StringUtil.isEmpty( study.getSpecialPermissions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("specPerm");
            xmlw.writeCharacters( study.getSpecialPermissions() );
            xmlw.writeEndElement(); // specPerm
        }
        
        if (!StringUtil.isEmpty( study.getRestrictions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("restrctn");
            xmlw.writeCharacters( study.getRestrictions() );
            xmlw.writeEndElement(); // restrctn
        }
        
        
        if (!StringUtil.isEmpty( study.getContact() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("contact");
            xmlw.writeCharacters( study.getContact() );
            xmlw.writeEndElement(); // contact
        }
        
        if (!StringUtil.isEmpty( study.getCitationRequirements() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("citReq");
            xmlw.writeCharacters( study.getCitationRequirements() );
            xmlw.writeEndElement(); // citReq
        }
        
        if (!StringUtil.isEmpty( study.getDepositorRequirements() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("deposReq");
            xmlw.writeCharacters( study.getDepositorRequirements() );
            xmlw.writeEndElement(); // deposReq
        }
        
        if (!StringUtil.isEmpty( study.getConditions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("conditions");
            xmlw.writeCharacters( study.getConditions() );
            xmlw.writeEndElement(); // conditions
        }
        
        if (!StringUtil.isEmpty( study.getDisclaimer() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("disclaimer");
            xmlw.writeCharacters( study.getDisclaimer() );
            xmlw.writeEndElement(); // disclaimer
        }
        if (useStmtAdded) xmlw.writeEndElement(); // useStmt


        // terms of use notes
        if (!StringUtil.isEmpty(vdcNetworkService.find().getDownloadTermsOfUse()) && vdcNetworkService.find().isDownloadTermsOfUseEnabled()) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "type", NOTE_TYPE_TERMS_OF_USE );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_TERMS_OF_USE );
            xmlw.writeCharacters( vdcNetworkService.find().getDownloadTermsOfUse() );
            xmlw.writeEndElement(); // notes
        }
        if (!StringUtil.isEmpty(study.getOwner().getDownloadTermsOfUse()) && study.getOwner().isDownloadTermsOfUseEnabled()) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "type", NOTE_TYPE_TERMS_OF_USE );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_TERMS_OF_USE );
            xmlw.writeCharacters( study.getOwner().getDownloadTermsOfUse() );
            xmlw.writeEndElement(); // notes
        }


        if (dataAccsAdded) xmlw.writeEndElement(); // dataAccs
    }

    private void createOthrStdyMat(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        boolean othrStdyMatAdded = false;

        // add replication for as a related material
        if (!StringUtil.isEmpty( study.getReplicationFor() )) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relMat");
            writeAttribute( xmlw, "type", "replicationFor" );
            xmlw.writeCharacters( study.getReplicationFor() );
            xmlw.writeEndElement(); // relMat
        }
        for (StudyRelMaterial rm : study.getStudyRelMaterials()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relMat");
            xmlw.writeCharacters( rm.getText() );
            xmlw.writeEndElement(); // relMat
        }
        for (StudyRelStudy rs : study.getStudyRelStudies()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relStdy");
            xmlw.writeCharacters( rs.getText() );
            xmlw.writeEndElement(); // relStdy
        }
        for (StudyRelPublication rp : study.getStudyRelPublications()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relPub");
            xmlw.writeCharacters( rp.getText() );
            xmlw.writeEndElement(); // relPub
        }
        for (StudyOtherRef or : study.getStudyOtherRefs()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("otherRefs");
            xmlw.writeCharacters( or.getText() );
            xmlw.writeEndElement(); // otherRefs
        }


        if (othrStdyMatAdded) xmlw.writeEndElement(); // othrStdyMat
    }

    private void createNotes(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        for (StudyNote note : study.getStudyNotes()) {
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "type", note.getType() );
            writeAttribute( xmlw, "subject", note.getSubject() );
            xmlw.writeCharacters( note.getText() );
            xmlw.writeEndElement(); // notes
        }    
    }

    private void createFileDscr(XMLStreamWriter xmlw, StudyFile sf) throws XMLStreamException {
        DataTable dt = sf.getDataTable();
        
        xmlw.writeStartElement("fileDscr");
        writeAttribute( xmlw, "ID", "f" + sf.getId().toString() );
        writeAttribute( xmlw, "URI", determineFileURI(sf) );
        
        // fileTxt
        xmlw.writeStartElement("fileTxt");

        xmlw.writeStartElement("fileName");
        xmlw.writeCharacters( sf.getFileName() );
        xmlw.writeEndElement(); // fileName

        xmlw.writeStartElement("fileCont");
        xmlw.writeCharacters( sf.getDescription() );
        xmlw.writeEndElement(); // fileCont

        // dimensions
        if (dt.getCaseQuantity() != null || dt.getVarQuantity() != null || dt.getRecordsPerCase() != null) {
            xmlw.writeStartElement("dimensns");

            if (dt.getCaseQuantity() != null) {
                xmlw.writeStartElement("caseQnty");
                xmlw.writeCharacters( dt.getCaseQuantity().toString() );
                xmlw.writeEndElement(); // caseQnty
            }
            if (dt.getVarQuantity() != null) {
                xmlw.writeStartElement("varQnty");
                xmlw.writeCharacters( dt.getVarQuantity().toString() );
                xmlw.writeEndElement(); // varQnty
            }
            if (dt.getRecordsPerCase() != null) {
                xmlw.writeStartElement("recPrCas");
                xmlw.writeCharacters( dt.getRecordsPerCase().toString() );
                xmlw.writeEndElement(); // recPrCas
            }        

            xmlw.writeEndElement(); // dimensns
        }

        xmlw.writeStartElement("fileType");
        xmlw.writeCharacters( sf.getFileType() );
        xmlw.writeEndElement(); // fileType        

        xmlw.writeEndElement(); // fileTxt

        // notes
        xmlw.writeStartElement("notes");
        writeAttribute( xmlw, "level", LEVEL_FILE );
        writeAttribute( xmlw, "type", NOTE_TYPE_UNF );
        writeAttribute( xmlw, "subject", NOTE_SUBJECT_UNF );
        xmlw.writeCharacters( dt.getUnf() );
        xmlw.writeEndElement(); // notes

        xmlw.writeStartElement("notes");
        writeAttribute( xmlw, "type", "vdc:category" );
        xmlw.writeCharacters( sf.getFileCategory().getName() );
        xmlw.writeEndElement(); // notes

        // THIS IS OLD CODE FROM JAXB, but a reminder that we may want to add original fileType
        // we don't yet store original file type!!!'
        // do we want this in the DDI export????
        //NotesType _origFileType = objFactory.createNotesType();
        //_origFileType.setLevel(LEVEL_FILE);
        //_origFileType.setType("VDC:MIME");
        //_origFileType.setSubject("original file format");
        //_origFileType.getContent().add( ORIGINAL_FILE_TYPE );

        xmlw.writeEndElement(); // fileDscr
    }

    private String determineFileURI(StudyFile sf) {
        String fileURI = "";
        Study s = sf.getFileCategory().getStudy();
        
        // determine whether file is local or harvested
        if (sf.isRemote() ) {
            return sf.getFileSystemLocation();
        } else {
            fileURI = "http://" + PropertyUtil.getHostUrl() + "/dvn/dv/" + s.getOwner().getAlias() + "/FileDownload/";
            fileURI += sf.getFileName()+ "?fileId=" + sf.getId();
            return fileURI;
        }
    }

    private void createOtherMat(XMLStreamWriter xmlw, StudyFile sf) throws XMLStreamException {
        xmlw.writeStartElement("otherMat");
        writeAttribute( xmlw, "level", LEVEL_STUDY );
        writeAttribute( xmlw, "URI", determineFileURI(sf) );

        xmlw.writeStartElement("labl");
        xmlw.writeCharacters( sf.getFileName() );
        xmlw.writeEndElement(); // labl

        xmlw.writeStartElement("txt");
        xmlw.writeCharacters( sf.getDescription() );
        xmlw.writeEndElement(); // txt

        xmlw.writeStartElement("notes");
        writeAttribute( xmlw, "type", "vdc:category" );
        xmlw.writeCharacters( sf.getFileCategory().getName() );
        xmlw.writeEndElement(); // notes

        xmlw.writeEndElement(); // otherMat
    }

    private void createDataDscr(XMLStreamWriter xmlw, Study study) throws XMLStreamException {
        boolean dataDscrAdded = false;

        for (StudyFile sf : study.getStudyFiles()) {
            if ( sf.isSubsettable() ) {
                if ( sf.getDataTable().getDataVariables().size() > 0 ) {
                    dataDscrAdded = checkParentElement(xmlw, "dataDscr", dataDscrAdded);
                    Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
                    while (varIter.hasNext()) {
                        DataVariable dv = (DataVariable) varIter.next();
                        createVar(xmlw, dv);
                    }
                }
            }
        }           

        if (dataDscrAdded) xmlw.writeEndElement(); //dataDscr
    }

    private void createVar(XMLStreamWriter xmlw, DataVariable dv) throws XMLStreamException {
        xmlw.writeStartElement("var");
        writeAttribute( xmlw, "ID", "v" + dv.getId().toString() );
        writeAttribute( xmlw, "name", dv.getName() );
        if (dv.getVariableIntervalType() != null) {
            String interval = dv.getVariableIntervalType().getName();
            interval = DB_VAR_INTERVAL_TYPE_CONTINUOUS.equals(interval) ? VAR_INTERVAL_CONTIN : interval;
            writeAttribute( xmlw, "intrvl", interval );
        }        
        
        // location
        xmlw.writeEmptyElement("location");
        if (dv.getFileStartPosition() != null) writeAttribute( xmlw, "StartPos", dv.getFileStartPosition().toString() );
        if (dv.getFileEndPosition() != null) writeAttribute( xmlw, "EndPos", dv.getFileEndPosition().toString() );
        if (dv.getRecordSegmentNumber() != null) writeAttribute( xmlw, "width", dv.getRecordSegmentNumber().toString());
        writeAttribute( xmlw, "fileid", "f" + dv.getDataTable().getStudyFile().getId().toString() );

        // labl
        if (!StringUtil.isEmpty( dv.getLabel() )) {
            xmlw.writeStartElement("labl");
            writeAttribute( xmlw, "level", "variable" );
            xmlw.writeCharacters( dv.getLabel() );
            xmlw.writeEndElement(); //labl
        }

        // invalrng
        boolean invalrngAdded = false;
        for (VariableRange range : dv.getInvalidRanges()) {
            if (range.getBeginValueType() != null && range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_POINT)) {
                if (range.getBeginValue() != null ) {
                    invalrngAdded = checkParentElement(xmlw, "invalrng", invalrngAdded);
                    xmlw.writeEmptyElement("item");
                    writeAttribute( xmlw, "VALUE", range.getBeginValue() );
                }
            } else {
                invalrngAdded = checkParentElement(xmlw, "invalrng", invalrngAdded);
                xmlw.writeEmptyElement("range");
                if ( range.getBeginValueType() != null && range.getBeginValue() != null ) {
                    if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN) ) {
                        writeAttribute( xmlw, "min", range.getBeginValue() );
                    } else if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN_EX) ) {
                        writeAttribute( xmlw, "minExclusive", range.getBeginValue() );
                    }
                }
                if ( range.getEndValueType() != null && range.getEndValue() != null) {
                    if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX) ) {
                        writeAttribute( xmlw, "max", range.getEndValue() );
                    } else if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX_EX) ) {
                        writeAttribute( xmlw, "maxExclusive", range.getEndValue() );
                    }
                }
            }
        }
        if (invalrngAdded) xmlw.writeEndElement(); // invalrng

        //universe
        if (!StringUtil.isEmpty( dv.getUniverse() )) {
            xmlw.writeStartElement("universe");
            xmlw.writeCharacters( dv.getUniverse() );
            xmlw.writeEndElement(); //universe
        }

        //sum stats
        for (SummaryStatistic sumStat : dv.getSummaryStatistics()) {
            xmlw.writeStartElement("sumStat");
            writeAttribute( xmlw, "type", sumStat.getType().getName() );
            xmlw.writeCharacters( sumStat.getValue() );
            xmlw.writeEndElement(); //sumStat
        }

        // categories
        for (VariableCategory cat : dv.getCategories()) {
            xmlw.writeStartElement("catgry");


            // catValu
            xmlw.writeStartElement("catValu");
            xmlw.writeCharacters( cat.getValue());
            xmlw.writeEndElement(); //catValu
            
            // label
            if (!StringUtil.isEmpty( cat.getLabel() )) {
                xmlw.writeStartElement("labl");
                writeAttribute( xmlw, "level", "category" );
                xmlw.writeCharacters( cat.getLabel() );
                xmlw.writeEndElement(); //labl
            }

            // catStat
            if (cat.getFrequency() != null) {
                xmlw.writeStartElement("catStat");
                writeAttribute( xmlw, "type", "freq" );
                xmlw.writeCharacters( cat.getFrequency().toString() );
                xmlw.writeEndElement(); //catStat
        }
        
            xmlw.writeEndElement(); //catgry   
        }
        
        //concept
        if (!StringUtil.isEmpty( dv.getConcept() )) {
            xmlw.writeStartElement("concept");
            xmlw.writeCharacters( dv.getConcept() );
            xmlw.writeEndElement(); //concept
        }

        // varFormat
        xmlw.writeEmptyElement("varFormat");
        writeAttribute( xmlw, "type", dv.getVariableFormatType().getName() );
        writeAttribute( xmlw, "formatname", dv.getFormatSchemaName() );
        writeAttribute( xmlw, "schema", dv.getFormatSchema() );

        // notes
        xmlw.writeStartElement("notes");
        writeAttribute( xmlw, "subject", "Universal Numeric Fingerprint" );
        writeAttribute( xmlw, "level", "variable" );
        writeAttribute( xmlw, "type", "VDC:UNF" );
        xmlw.writeCharacters( dv.getUnf() );
        xmlw.writeEndElement(); //notes        

        xmlw.writeEndElement(); //var      
    }

    private void createExtLink(XMLStreamWriter xmlw, String uri, String role) throws XMLStreamException {
        xmlw.writeEmptyElement("ExtLink");
        writeAttribute( xmlw, "URI", uri );
        if (role != null) {
            writeAttribute( xmlw, "role", role );
        }
    }

    private void createDateElement(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        xmlw.writeStartElement(name);
        writeDateAttribute(xmlw, value);
        xmlw.writeCharacters( value );
        xmlw.writeEndElement();
}

    private void writeAttribute(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        // only write attribute if value is a valid date
        if ( !StringUtil.isEmpty(value) ) {
            xmlw.writeAttribute(name, value);
        } 
    }

    private void writeDateAttribute(XMLStreamWriter xmlw, String value) throws XMLStreamException {
        // only write attribute if value is a valid date
        if ( DateUtil.validateDate(value) ) {
            xmlw.writeAttribute( "date", value);
        } 
    }

    private boolean checkParentElement(XMLStreamWriter xmlw, String elementName, boolean elementAdded) throws XMLStreamException {
        if (!elementAdded) {
            xmlw.writeStartElement(elementName);
        }

        return true;
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="import methods">    
    //**********************
    // IMPORT METHODS
    //**********************
  
    public void mapDDI( File ddiFile, Study study) {
        Map filesMap = new HashMap();
        initializeCollections(study);        
        
        try {
            javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLStreamReader xmlr = xmlif.createXMLStreamReader(new java.io.FileReader(ddiFile));
        
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("docDscr")) processDocDscr(xmlr, study);
                    else if (xmlr.getLocalName().equals("stdyDscr")) processStdyDscr(xmlr, study);
                    else if (xmlr.getLocalName().equals("fileDscr")) processFileDscr(xmlr, study, filesMap);
                    else if (xmlr.getLocalName().equals("dataDscr")) processDataDscr(xmlr, study, filesMap);
                    else if (xmlr.getLocalName().equals("otherMat")) processOtherMat(xmlr, study);
                } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                    return;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapping: File Not Found!");
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred while processing DDI!!!!");
        }
    }

    private void initializeCollections(Study study) {
        // initialize the collections
        study.setFileCategories( new ArrayList() );
        study.setStudyAbstracts( new ArrayList() );
        study.setStudyAuthors( new ArrayList() );
        study.setStudyDistributors( new ArrayList() );
        study.setStudyGeoBoundings(new ArrayList());
        study.setStudyGrants(new ArrayList());
        study.setStudyKeywords(new ArrayList());
        study.setStudyNotes(new ArrayList());
        study.setStudyOtherIds(new ArrayList());
        study.setStudyOtherRefs(new ArrayList());
        study.setStudyProducers(new ArrayList());
        study.setStudyRelMaterials(new ArrayList());
        study.setStudyRelPublications(new ArrayList());
        study.setStudyRelStudies(new ArrayList());
        study.setStudySoftware(new ArrayList());
        study.setStudyTopicClasses(new ArrayList());
}

    private void processDocDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("IDNo") && StringUtil.isEmpty(study.getStudyId()) ) {
                    // this will set a StudyId if it has not yet been set; it will get overridden by a study
                    // id in the StudyDscr section, if one exists
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( parseText(xmlr), study );
                    }
                } else if ( xmlr.getLocalName().equals("holdings") && StringUtil.isEmpty(study.getHarvestHoldings()) ) {
                    study.setHarvestHoldings( xmlr.getAttributeValue(null, "URI") );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("docDscr")) return;
            }   
        }
    }

    private void processStdyDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) processCitation(xmlr, study);
                else if (xmlr.getLocalName().equals("stdyInfo")) processStdyInfo(xmlr, study);
                else if (xmlr.getLocalName().equals("method")) processMethod(xmlr, study);
                else if (xmlr.getLocalName().equals("dataAccs")) processDataAccs(xmlr, study);
                else if (xmlr.getLocalName().equals("othrStdyMat")) processOthrStdyMat(xmlr, study);
                else if (xmlr.getLocalName().equals("notes")) processNotes(xmlr, study);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("stdyDscr")) return;
            }   
        }
    }
 
     private void processCitation(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) processTitlStmt(xmlr, study);
                else if (xmlr.getLocalName().equals("rspStmt")) processRspStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("prodStmt")) processProdStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("distStmt")) processDistStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("serStmt")) processSerStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("verStmt")) processVerStmt(xmlr,study);  
                else if (xmlr.getLocalName().equals("notes")) {
                    String _note = parseNoteByType( xmlr, NOTE_TYPE_UNF );
                    if (_note != null && !_note.equals("") ) {
                        study.setUNF( parseUNF( _note ) );
                    } else {                   
                        processNotes(xmlr, study);
                    }   
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) return;
            }   
        }
    }   

    private void processTitlStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titl")) {
                    study.setTitle( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("subTitl")) {
                    study.setSubTitle( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("IDNo")) {
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( parseText(xmlr), study );
                    } else {
                        StudyOtherId sid = new StudyOtherId();
                        sid.setAgency( xmlr.getAttributeValue(null, "agency")) ;
                        sid.setOtherId( parseText(xmlr) );
                        sid.setStudy(study);
                        study.getStudyOtherIds().add(sid);
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) return;
            }   
        }
    } 
    
    private void processRspStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("AuthEnty")) {
                    StudyAuthor author = new StudyAuthor();
                    author.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    author.setName( parseText(xmlr) );
                    author.setStudy(study);
                    study.getStudyAuthors().add(author);                    
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("rspStmt")) return;
            }   
        }
    }

    private void processProdStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("producer")) {
                    StudyProducer prod = new StudyProducer();
                    study.getStudyProducers().add(prod);
                    prod.setStudy(study);
                    prod.setAbbreviation(xmlr.getAttributeValue(null, "abbr") );
                    prod.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    Map<String,String> prodDetails = parseCompoundText(xmlr, "producer");
                    prod.setName( prodDetails.get("name") );
                    prod.setUrl(  prodDetails.get("url") );
                    prod.setLogo(  prodDetails.get("logo") );
                } else if (xmlr.getLocalName().equals("prodDate")) {
                    study.setProductionDate(parseDate(xmlr,"prodDate"));
                } else if (xmlr.getLocalName().equals("prodPlac")) {
                    study.setProductionPlace( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("software")) {
                    StudySoftware ss = new StudySoftware();
                    study.getStudySoftware().add(ss);
                    ss.setStudy(study);
                    ss.setSoftwareVersion( xmlr.getAttributeValue(null, "version") );
                    ss.setName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("fundAg")) {
                    study.setFundingAgency( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("grantNo")) {
                    StudyGrant sg = new StudyGrant();
                    study.getStudyGrants().add(sg);
                    sg.setStudy(study);
                    sg.setAgency( xmlr.getAttributeValue(null, "agency") );
                    sg.setNumber( parseText(xmlr) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("prodStmt")) return;
            }   
        }
    }

    private void processDistStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("distrbtr")) {
                    StudyDistributor dist = new StudyDistributor();
                    study.getStudyDistributors().add(dist);
                    dist.setStudy(study);
                    dist.setAbbreviation(xmlr.getAttributeValue(null, "abbr") );
                    dist.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    Map<String,String> distDetails = parseCompoundText(xmlr, "distrbtr");
                    dist.setName( distDetails.get("name") );
                    dist.setUrl(  distDetails.get("url") );
                    dist.setLogo(  distDetails.get("logo") );               
                } else if (xmlr.getLocalName().equals("contact")) {
                    study.setDistributorContactEmail( xmlr.getAttributeValue(null, "email") );
                    study.setDistributorContactAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    study.setDistributorContact( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("depositr")) {
                    Map<String,String> depDetails = parseCompoundText(xmlr, "depositr");
                    study.setDepositor( depDetails.get("name") );
                } else if (xmlr.getLocalName().equals("depDate")) {
                    study.setDateOfDeposit( parseDate(xmlr,"depDate") );
                } else if (xmlr.getLocalName().equals("distDate")) {
                    study.setDistributionDate( parseDate(xmlr,"distDate") );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("distStmt")) return;
            }   
        }
    }


    private void processSerStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("serName")) {
                    study.setSeriesName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("serInfo")) {
                    study.setSeriesInformation( parseText(xmlr) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("serStmt")) return;
            }   
        }
    }

    private void processVerStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("version")) {
                    study.setVersionDate( xmlr.getAttributeValue(null, "date") );
                    study.setStudyVersion( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("notes")) { processNotes(xmlr, study); }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("verStmt")) return;
            }   
        }
    } 
 
     private void processStdyInfo(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("subject")) processSubject(xmlr, study);
                else if (xmlr.getLocalName().equals("abstract")) {
                    StudyAbstract abs = new StudyAbstract();
                    study.getStudyAbstracts().add(abs);
                    abs.setStudy(study);
                    abs.setDate( xmlr.getAttributeValue(null, "date") );
                    abs.setText( parseText(xmlr, "abstract") );
                } else if (xmlr.getLocalName().equals("sumDscr")) processSumDscr(xmlr, study);
                else if (xmlr.getLocalName().equals("notes")) processNotes(xmlr, study);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("stdyInfo")) return;
            }   
        }
    }

     private void processSubject(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("keyword")) {
                    StudyKeyword kw = new StudyKeyword();
                    study.getStudyKeywords().add(kw);
                    kw.setStudy(study);
                    kw.setVocab( xmlr.getAttributeValue(null, "vocab") );
                    kw.setVocabURI( xmlr.getAttributeValue(null, "vocabURI") );
                    kw.setValue( parseText(xmlr));
                } else if (xmlr.getLocalName().equals("topcClas")) {
                    StudyTopicClass tc = new StudyTopicClass();
                    study.getStudyTopicClasses().add(tc);
                    tc.setStudy(study);
                    tc.setVocab( xmlr.getAttributeValue(null, "vocab") );
                    tc.setVocabURI( xmlr.getAttributeValue(null, "vocabURI") );
                    tc.setValue( parseText(xmlr));
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("subject")) return;
            }   
        }
    }

     private void processSumDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("timePrd")) {
                    String eventAttr = xmlr.getAttributeValue(null, "event");
                    if ( EVENT_START.equals(eventAttr) || EVENT_SINGLE.equals(eventAttr) ) {
                        study.setTimePeriodCoveredStart( parseDate(xmlr, "timePrd") );
                    } else if ( EVENT_END.equals(eventAttr) ) {
                        study.setTimePeriodCoveredEnd( parseDate(xmlr, "timePrd") );
                    }
                } else if (xmlr.getLocalName().equals("collDate")) {
                    String eventAttr = xmlr.getAttributeValue(null, "event");
                    if ( EVENT_START.equals(eventAttr) || EVENT_SINGLE.equals(eventAttr) ) {
                        study.setDateOfCollectionStart( parseDate(xmlr, "collDate") );
                    } else if ( EVENT_END.equals(eventAttr) ) {
                        study.setDateOfCollectionEnd( parseDate(xmlr, "collDate") );
                    }
                } else if (xmlr.getLocalName().equals("nation")) {
                    if (StringUtil.isEmpty( study.getCountry() ) ) {
                        study.setCountry( parseText(xmlr) );
                    } else {
                        study.setCountry( study.getCountry() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geogCover")) {
                    if (StringUtil.isEmpty( study.getGeographicCoverage() ) ) {
                        study.setGeographicCoverage( parseText(xmlr) );
                    } else {
                        study.setGeographicCoverage( study.getGeographicCoverage() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geogUnit")) {
                    if (StringUtil.isEmpty( study.getGeographicUnit() ) ) {
                        study.setGeographicUnit( parseText(xmlr) );
                    } else {
                        study.setGeographicUnit( study.getGeographicUnit() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geoBndBox")) { 
                    processGeoBndBox(xmlr,study);
                } else if (xmlr.getLocalName().equals("anlyUnit*")) {
                    if (StringUtil.isEmpty( study.getUnitOfAnalysis() ) ) {
                        study.setUnitOfAnalysis( parseText(xmlr) );
                    } else {
                        study.setUnitOfAnalysis( study.getUnitOfAnalysis() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("universe")) {
                    if (StringUtil.isEmpty( study.getUniverse() ) ) {
                        study.setUniverse( parseText(xmlr) );
                    } else {
                        study.setUniverse( study.getUniverse() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("dataKind")) {
                    if (StringUtil.isEmpty( study.getKindOfData() ) ) {
                        study.setKindOfData( parseText(xmlr) );
                    } else {
                        study.setKindOfData( study.getKindOfData() + "; " + parseText(xmlr) );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("sumDscr")) return;
            }   
        }
    }    

    private void processGeoBndBox(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        StudyGeoBounding geoBound = new StudyGeoBounding();
        study.getStudyGeoBoundings().add(geoBound);
        geoBound.setStudy(study);

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("westBL")) {
                    geoBound.setWestLongitude( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("eastBL")) {
                    geoBound.setEastLongitude( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("southBL")) {
                    geoBound.setSouthLatitude( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("northBL")) {
                    geoBound.setNorthLatitude( parseText(xmlr) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("geoBndBox")) return;
            }   
        }
    }

    private void processMethod(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataColl")) processDataColl(xmlr, study);
                else if (xmlr.getLocalName().equals("notes")) {
                    if (StringUtil.isEmpty( study.getStudyLevelErrorNotes() ) ) {
                        study.setStudyLevelErrorNotes( parseText(xmlr) );
                    } else {
                        study.setStudyLevelErrorNotes( study.getStudyLevelErrorNotes() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("anlyInfo")) processAnlyInfo(xmlr, study);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("method")) return;
            }   
        }
    }

    private void processDataColl(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("timeMeth")) { 
                    study.setTimeMethod( parseText( xmlr, "timeMeth" ) );
                } else if (xmlr.getLocalName().equals("dataCollector")) {
                    study.setDataCollector( parseText( xmlr, "dataCollector" ) );
                } else if (xmlr.getLocalName().equals("frequenc")) {
                    study.setFrequencyOfDataCollection( parseText( xmlr, "frequenc" ) );
                } else if (xmlr.getLocalName().equals("sampProc")) {
                    study.setSamplingProcedure( parseText( xmlr, "sampProc" ) );
                } else if (xmlr.getLocalName().equals("deviat")) {
                    study.setDeviationsFromSampleDesign( parseText( xmlr, "deviat" ) );;
                } else if (xmlr.getLocalName().equals("collMode")) {
                    study.setCollectionMode( parseText( xmlr, "collMode" ) );
                } else if (xmlr.getLocalName().equals("resInstru")) {
                    study.setResearchInstrument( parseText( xmlr, "resInstru" ) );
                } else if (xmlr.getLocalName().equals("sources")) {
                    processSources(xmlr,study);
                } else if (xmlr.getLocalName().equals("collSitu")) {
                    study.setDataCollectionSituation( parseText( xmlr, "collSitu" ) );;
                } else if (xmlr.getLocalName().equals("actMin")) {
                    study.setActionsToMinimizeLoss( parseText( xmlr, "actMin" ) );
                } else if (xmlr.getLocalName().equals("ConOps")) {
                    study.setControlOperations( parseText( xmlr, "ConOps" ) );
                } else if (xmlr.getLocalName().equals("weight")) {
                    study.setWeighting( parseText( xmlr, "weight" ) );
                } else if (xmlr.getLocalName().equals("cleanOps")) {
                    study.setCleaningOperations( parseText( xmlr, "cleanOps" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataColl")) return;
            }   
        }
    }

    private void processSources(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataSrc")) {
                    study.setDataSources( parseText( xmlr, "dataSrc" ) );;
                } else if (xmlr.getLocalName().equals("srcOrig")) {
                    study.setOriginOfSources( parseText( xmlr, "srcOrig" ) );
                } else if (xmlr.getLocalName().equals("srcChar")) {
                    study.setCharacteristicOfSources( parseText( xmlr, "srcChar" ) );
                } else if (xmlr.getLocalName().equals("srcDocu")) {
                    study.setAccessToSources( parseText( xmlr, "srcDocu" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("sources")) return;
            }   
        }
    }

    private void processAnlyInfo(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("respRate")) { 
                    study.setResponseRate( parseText( xmlr, "respRate" ) );
                } else if (xmlr.getLocalName().equals("EstSmpErr")) {
                    study.setSamplingErrorEstimate( parseText( xmlr, "EstSmpErr" ) );
                } else if (xmlr.getLocalName().equals("dataAppr")) {
                    study.setOtherDataAppraisal( parseText( xmlr, "dataAppr" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("anlyInfo")) return;
            }   
        }
    }

    private void processDataAccs(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("setAvail")) processSetAvail(xmlr,study);
                else if (xmlr.getLocalName().equals("useStmt")) processUseStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("notes")) {
                    processNotes( xmlr, study );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataAccs")) return;
            }   
        }
    }

    private void processSetAvail(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("accsPlac")) { 
                    study.setPlaceOfAccess( parseText( xmlr, "accsPlac" ) );
                } else if (xmlr.getLocalName().equals("origArch")) {
                    study.setOriginalArchive( parseText( xmlr, "origArch" ) );
                } else if (xmlr.getLocalName().equals("avlStatus")) {
                    study.setAvailabilityStatus( parseText( xmlr, "avlStatus" ) );
                } else if (xmlr.getLocalName().equals("collSize")) {
                    study.setCollectionSize( parseText( xmlr, "collSize" ) );
                } else if (xmlr.getLocalName().equals("complete")) {
                    study.setStudyCompletion( parseText( xmlr, "complete" ) );;
                } else if (xmlr.getLocalName().equals("notes")) {
                    processNotes( xmlr, study );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("setAvail")) return;
            }   
        }
    }

    private void processUseStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("confDec")) { 
                    study.setConfidentialityDeclaration( parseText( xmlr, "confDec" ) );
                } else if (xmlr.getLocalName().equals("specPerm")) {
                    study.setSpecialPermissions( parseText( xmlr, "specPerm" ) );
                } else if (xmlr.getLocalName().equals("restrctn")) {
                    study.setRestrictions( parseText( xmlr, "restrctn" ) );
                } else if (xmlr.getLocalName().equals("contact")) {
                    study.setContact( parseText( xmlr, "contact" ) );
                } else if (xmlr.getLocalName().equals("citReq")) {
                    study.setCitationRequirements( parseText( xmlr, "citReq" ) );;
                } else if (xmlr.getLocalName().equals("deposReq")) {
                    study.setDepositorRequirements( parseText( xmlr, "deposReq" ) );
                } else if (xmlr.getLocalName().equals("conditions")) {
                    study.setConditions( parseText( xmlr, "conditions" ) );
                } else if (xmlr.getLocalName().equals("disclaimer")) {
                    study.setDisclaimer( parseText( xmlr, "disclaimer" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("useStmt")) return;
            }   
        }
    }

    private void processOthrStdyMat(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        boolean replicationForFound = false;
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("relMat")) {
                    if (!replicationForFound && REPLICATION_FOR_TYPE.equals( xmlr.getAttributeValue(null, "type") ) ) {
                        study.setReplicationFor( parseText( xmlr, "relMat" ) );
                        replicationForFound = true;
                    } else { 
                        StudyRelMaterial rm = new StudyRelMaterial();
                        study.getStudyRelMaterials().add(rm);
                        rm.setStudy(study);
                        rm.setText( parseText( xmlr, "relMat" ) );
                    }
                } else if (xmlr.getLocalName().equals("relStdy")) {
                    StudyRelStudy rs = new StudyRelStudy();
                    study.getStudyRelStudies().add(rs);
                    rs.setStudy(study);
                    rs.setText( parseText( xmlr, "relStdy" ) );
                } else if (xmlr.getLocalName().equals("relPubl")) {
                    StudyRelPublication rp = new StudyRelPublication();
                    study.getStudyRelPublications().add(rp);
                    rp.setStudy(study);
                    rp.setText( parseText( xmlr, "relPubl" ) );
                } else if (xmlr.getLocalName().equals("otherRefs")) {
                    StudyOtherRef or = new StudyOtherRef();
                    study.getStudyOtherRefs().add(or);
                    or.setStudy(study);
                    or.setText( parseText( xmlr, "otherRefs" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("othrStdyMat")) return;
            }   
        }
    }

    private void processFileDscr(XMLStreamReader xmlr, Study study, Map filesMap) throws XMLStreamException {
        StudyFile sf = new StudyFile();
        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));

        DataTable dt = new DataTable();
        dt.setDataVariables( new ArrayList() );
       
        List filesMapEntry = new ArrayList();
        filesMapEntry.add(sf);
        filesMapEntry.add(dt);
        filesMap.put( xmlr.getAttributeValue(null, "ID"), filesMapEntry);

        /// the following Strings are used to determine the category
        String catName = null;
        String icpsrDesc = null;
        String icpsrId = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) processFileTxt(xmlr, sf, dt);
                else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (NOTE_TYPE_UNF.equalsIgnoreCase(noteType) ) {
                        dt.setUnf( parseUNF( parseText(xmlr) ) );    
                    } else if ("vdc:category".equalsIgnoreCase(noteType) ) {
                        catName = parseText(xmlr);
                    } else if ("icpsr:category".equalsIgnoreCase(noteType) ) {
                        String subjectType = xmlr.getAttributeValue(null, "subject");
                        if ("description".equalsIgnoreCase(subjectType)) {
                            icpsrDesc = parseText(xmlr);
                        } else if ("id".equalsIgnoreCase(subjectType)) {
                            icpsrId = parseText(xmlr);
                        }
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("fileDscr")) {
                    // post process
                    if (sf.getFileName() == null || sf.getFileName().trim().equals("") ) {
                        sf.setFileName("file");
                    } 
                    addFileToCategory(sf, determineFileCategory(catName, icpsrDesc, icpsrId), study);
                    return;
                }
            }   
        }
    }   

    private void processFileTxt(XMLStreamReader xmlr, StudyFile sf, DataTable dt) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileName")) {
                    sf.setFileName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("fileCont")) {
                    sf.setDescription( parseText(xmlr) );
                }  else if (xmlr.getLocalName().equals("dimensns")) processDimensns(xmlr, dt);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) return;
            } 
        }
    }     

    private void processDimensns(XMLStreamReader xmlr, DataTable dt) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("caseQnty")) {
                    dt.setCaseQuantity( new Long( parseText(xmlr) ) );
                } else if (xmlr.getLocalName().equals("varQnty")) {
                    dt.setVarQuantity( new Long( parseText(xmlr) ) );
                } else if (xmlr.getLocalName().equals("recPrCas")) {
                    dt.setRecordsPerCase( new Long( parseText(xmlr) ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("dimensns")) return;
            }   
        }
    } 
    
    private void processDataDscr(XMLStreamReader xmlr, Study study, Map filesMap) throws XMLStreamException {
        int fileOrder = 0;
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) processVar(xmlr, study, filesMap, fileOrder++);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataDscr")) return;
            }   
        }
    }


 
    private void processVar(XMLStreamReader xmlr, Study study, Map filesMap, int fileOrder) throws XMLStreamException {
        DataVariable dv = new DataVariable();
        dv.setInvalidRanges(new ArrayList());
        dv.setSummaryStatistics( new ArrayList() );
        dv.setCategories( new ArrayList() );
        dv.setName( xmlr.getAttributeValue(null, "name") );
        dv.setFileOrder(fileOrder);
       
        // interval type (DB value may be different than DDI value)
        String _interval = xmlr.getAttributeValue(null, "intrvl");
        _interval = VAR_INTERVAL_CONTIN.equals(_interval) ? DB_VAR_INTERVAL_TYPE_CONTINUOUS : _interval;
        dv.setVariableIntervalType( varService.findVariableIntervalTypeByName(variableIntervalTypeList, _interval ));

        dv.setWeighted( VAR_WEIGHTED.equals( xmlr.getAttributeValue(null, "wgt") ) );
       
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("location")) processLocation(xmlr, dv, filesMap);
                else if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_VARIABLE );
                    if (_labl != null && !_labl.equals("") ) {
                        dv.setLabel( _labl );
                    }
                } else if (xmlr.getLocalName().equals("universe")) {
                    dv.setUniverse( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("concept")) {
                    dv.setConcept( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("invalrng")) processInvalrng( xmlr, dv );
                else if (xmlr.getLocalName().equals("varFormat")) processVarFormat( xmlr, dv );
                else if (xmlr.getLocalName().equals("sumStat")) processSumStat( xmlr, dv );
                else if (xmlr.getLocalName().equals("catgry")) processCatgry( xmlr, dv );
                else if (xmlr.getLocalName().equals("notes")) {
                    String _note = parseNoteByType( xmlr, NOTE_TYPE_UNF );
                    if (_note != null && !_note.equals("") ) {
                        dv.setUnf( parseUNF( _note ) );
                    }
                }

                // todo: qstnTxt: wait to handle until we know more of how we will use it
                // todo: wgt-var : waitng to see example

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) return;
            }   
        }
    }    

    private void processLocation(XMLStreamReader xmlr, DataVariable dv, Map filesMap) throws XMLStreamException {
        // associate dv with the correct file
        List filesMapEntry = (List) filesMap.get( xmlr.getAttributeValue(null, "fileid" ) );
        if (filesMapEntry != null) {
            StudyFile sf = (StudyFile) filesMapEntry.get(0);
            DataTable dt = (DataTable) filesMapEntry.get(1);
            if (!sf.isSubsettable()) {
                // first time with this file, so attach the dt to the file and set as subsettable)
                dt.setStudyFile(sf);
                sf.setDataTable(dt);
                sf.setSubsettable(true); 
            }

            dv.setDataTable(dt);
            dt.getDataVariables().add(dv);
        }
        
        // fileStartPos, FileEndPos, and RecSegNo
        // if these fields don't convert to Long, just leave blank
        try {
            dv.setFileStartPosition( new Long( xmlr.getAttributeValue(null, "StartPos") ) );
        } catch (NumberFormatException ex) {}
        try {
            dv.setFileEndPosition( new Long( xmlr.getAttributeValue(null, "EndPos") ) );
        } catch (NumberFormatException ex) {}
        try {
            dv.setRecordSegmentNumber( new Long( xmlr.getAttributeValue(null, "RecSegNo") ) );  
        } catch (NumberFormatException ex) {}
    }    

    private void processInvalrng(XMLStreamReader xmlr, DataVariable dv) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("item")) {
                    VariableRange range = new VariableRange();
                    dv.getInvalidRanges().add(range);
                    range.setDataVariable(dv);
                    
                    range.setBeginValue( xmlr.getAttributeValue(null, "VALUE") );
                    range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList, DB_VAR_RANGE_TYPE_POINT )  );
                } else if (xmlr.getLocalName().equals("range")) {
                    VariableRange range = new VariableRange();
                    dv.getInvalidRanges().add(range);
                    range.setDataVariable(dv);

                    String min = xmlr.getAttributeValue(null, "min");
                    String minExclsuive = xmlr.getAttributeValue(null, "minExclusive");
                    String max = xmlr.getAttributeValue(null, "max");
                    String maxExclusive = xmlr.getAttributeValue(null, "maxExclusive");

                    if ( !StringUtil.isEmpty(min) ) {
                        range.setBeginValue( min );
                        range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MIN )  );
                    } else if ( !StringUtil.isEmpty(minExclsuive) ) {
                        range.setBeginValue( minExclsuive );
                        range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MIN_EX )  );
                    }

                    if ( !StringUtil.isEmpty(max) ) {
                        range.setEndValue( max );
                        range.setEndValueType(varService.findVariableRangeTypeByName( variableRangeTypeList,DB_VAR_RANGE_TYPE_MAX )  );
                    } else if ( !StringUtil.isEmpty(maxExclusive) ) {
                        range.setEndValue( maxExclusive );
                        range.setEndValueType(varService.findVariableRangeTypeByName(variableRangeTypeList, DB_VAR_RANGE_TYPE_MAX_EX )  );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("invalrng")) return;
            }   
        }
    }
    
    private void processVarFormat(XMLStreamReader xmlr, DataVariable dv) throws XMLStreamException {
        dv.setVariableFormatType( varService.findVariableFormatTypeByName(variableFormatTypeList, xmlr.getAttributeValue(null, "type") ) );
        dv.setFormatSchema( xmlr.getAttributeValue(null, "schema") );
        dv.setFormatSchemaName( xmlr.getAttributeValue(null, "formatName") );        
    }
    
    private void processSumStat(XMLStreamReader xmlr, DataVariable dv) throws XMLStreamException {
        SummaryStatistic ss = new SummaryStatistic();
        ss.setType( varService.findSummaryStatisticTypeByName( summaryStatisticTypeList, xmlr.getAttributeValue(null, "type") ) );
        ss.setValue( parseText(xmlr)) ;
        ss.setDataVariable(dv);
        dv.getSummaryStatistics().add(ss);
    }

    private void processCatgry(XMLStreamReader xmlr, DataVariable dv) throws XMLStreamException {
        VariableCategory cat = new VariableCategory();
        cat.setMissing( "Y".equals( xmlr.getAttributeValue(null, "missing") ) );
        cat.setDataVariable(dv);
        dv.getCategories().add(cat);
        
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_CATEGORY );
                    if (_labl != null && !_labl.equals("") ) {
                        cat.setLabel( _labl );
                    }                    
                } else if (xmlr.getLocalName().equals("catValu")) {
                    cat.setValue( parseText(xmlr) );
                }
                else if (xmlr.getLocalName().equals("catStat")) {
                    String _freq = processCatStat( xmlr, "freq" );
                    if (_freq != null && !_freq.equals("") ) {
                        cat.setFrequency( new Long( _freq ) );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("catgry")) return;
            }   
        }
    }

    private String processLabl(XMLStreamReader xmlr, String level) throws XMLStreamException {
        if (level.equalsIgnoreCase( xmlr.getAttributeValue(null, "level") ) ) {
            return parseText(xmlr);
        } else {
            return null;
        }
    }
    
    private String processCatStat(XMLStreamReader xmlr, String type) throws XMLStreamException {
        if (type.equalsIgnoreCase( xmlr.getAttributeValue(null, "type") ) ) {
            return parseText(xmlr);
        } else {
            return null;
        }
    }    
    

    private void processOtherMat(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        StudyFile sf = new StudyFile();
        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));


        /// the following Strings are used to determine the category
        String catName = null;
        String icpsrDesc = null;
        String icpsrId = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("labl")) {
                    sf.setFileName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("txt")) {
                    sf.setDescription( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if ("vdc:category".equalsIgnoreCase(noteType) ) {
                        catName = parseText(xmlr);
                    } else if ("icpsr:category".equalsIgnoreCase(noteType) ) {
                        String subjectType = xmlr.getAttributeValue(null, "subject");
                        if ("description".equalsIgnoreCase(subjectType)) {
                            icpsrDesc = parseText(xmlr);
                        } else if ("id".equalsIgnoreCase(subjectType)) {
                            icpsrId = parseText(xmlr);
                        }
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("otherMat")) {
                    // post process
                    if (sf.getFileName() == null || sf.getFileName().trim().equals("") ) {
                        sf.setFileName("file");
                    } 
                    addFileToCategory(sf, determineFileCategory(catName, icpsrDesc, icpsrId), study);
                    return;
                }
            }   
        }
    }

    private void processNotes (XMLStreamReader xmlr, Study study) throws XMLStreamException {
        StudyNote note = new StudyNote();
        study.getStudyNotes().add(note);
        note.setStudy(study);
        note.setSubject( xmlr.getAttributeValue(null, "subject") );
        note.setType( xmlr.getAttributeValue(null, "type") );
        note.setText( parseText(xmlr) );
    }    
    
    private void parseStudyId(String _id, Study s) {
        
        int index1 = _id.indexOf(':');
        int index2 = _id.indexOf('/');
        if (index1==-1) {
            throw new EJBException("Error parsing IdNo: "+_id+". ':' not found in string");
        } else {
            s.setProtocol(_id.substring(0,index1));
        }
        if (index2 == -1) {
            throw new EJBException("Error parsing IdNo: "+_id+". '/' not found in string");
            
        } else {
            s.setAuthority(_id.substring(index1+1, index2));
        }
        s.setStudyId(_id.substring(index2+1));
    }    

    private String parseNoteByType (XMLStreamReader xmlr, String type) throws XMLStreamException {
        if (type.equalsIgnoreCase( xmlr.getAttributeValue(null, "type") ) ) {
            return parseText(xmlr);
        } else {
            return null;
        }
    }
    
    private String parseUNF(String unfString) {
        if (unfString.indexOf("UNF:") != -1) {
            return unfString.substring( unfString.indexOf("UNF:") );
        } else {
            return null;
        }
    }

     private String parseText(XMLStreamReader xmlr) throws XMLStreamException {
        return xmlr.getElementText().trim().replace('\n',' ');
     }

     private String parseText(XMLStreamReader xmlr, String endTag) throws XMLStreamException {
        String returnString = "";

        while (true) {
            if (returnString != "") { returnString += "\n";}
            int event = xmlr.next();
            if (event == XMLStreamConstants.CHARACTERS) {
                returnString += xmlr.getText().trim().replace('\n',' ');
            } else if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("p")) {
                    returnString += "<p>" + parseText(xmlr, "p") + "</p>";
                }
                else if (xmlr.getLocalName().equals("ExtLink")) {
                    String uri = xmlr.getAttributeValue(null, "URI");
                    String text = parseText(xmlr, "ExtLink").trim();
                    returnString += "<a href=\"" + uri + "\">" + ( StringUtil.isEmpty(text) ? uri : text) + "</a>";
                } else if (xmlr.getLocalName().equals("list")) {
                    returnString += parseText_list(xmlr);
                } else if (xmlr.getLocalName().equals("citation")) {
                    returnString += parseText_citation(xmlr);
                } else {
                    System.out.println("DDI Mapper: parseText: tag not yet supported");
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals(endTag)) break;
            }   
        }

        return returnString;
    }

    private String parseText_list (XMLStreamReader xmlr) throws XMLStreamException {
        String listString = null;
        String listCloseTag = null;

        // check type
        String listType = xmlr.getAttributeValue(null, "type");
        if ("bulleted".equals(listType) ){
            listString = "<ul>\n";
            listCloseTag = "</ul>";
        } else if ("ordered".equals(listType) ) {
            listString = "<ol>\n";
            listCloseTag = "</ol>";
        } else {
            throw new EJBException("mapContent: ListType of types other than {bulleted, ordered} not currently supported.");    
        }

        while (true) {
            int event = xmlr.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("itm")) {
                    listString += "<li>" + parseText(xmlr) + "</li>\n";
                } else {
                    throw new EJBException("mapContent: ListType does not currently supported contained LabelType.");
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("list")) break;
            }
        }

        return (listString + listCloseTag);
    }

    private String parseText_citation (XMLStreamReader xmlr) throws XMLStreamException {
        String citation = "<!--  parsed from DDI citation title and holdings -->";
        boolean addHoldings = false;
        String holdings = "";

        while (true) {
            int event = xmlr.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) {
                    while (true) {
                        event = xmlr.next();
                        if (event == XMLStreamConstants.START_ELEMENT) {
                            if (xmlr.getLocalName().equals("titl")) {
                                citation += parseText(xmlr);
                            }
                        } else if (event == XMLStreamConstants.END_ELEMENT) {
                            if (xmlr.getLocalName().equals("titlStmt")) break;
                        }
                    }
                } else if (xmlr.getLocalName().equals("holdings")) {
                    holdings += addHoldings ? ", " : "";
                    addHoldings = true;
                    
                    String uri = xmlr.getAttributeValue(null, "URI");
                    if ( StringUtil.isEmpty(uri) ) {
                        holdings += parseText(xmlr);
                    } else {
                        holdings += "<a href=\"" + uri + "\">" + parseText(xmlr) + "</a>";
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) break;
            }
        }

        if (addHoldings) {
            citation += " (" + holdings + ")";
        }

        return citation;
    }

    private Map<String,String> parseCompoundText (XMLStreamReader xmlr, String endTag) throws XMLStreamException {
        Map<String,String> returnMap = new HashMap<String,String>();
        String text = "";

        while (true) {
            int event = xmlr.next();
            if (event == XMLStreamConstants.CHARACTERS) {
                if (text != "") { text += "\n";}
                text += xmlr.getText().trim().replace('\n',' ');
            } else if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("ExtLink")) {
                    String mapKey  = "image".equals( xmlr.getAttributeValue(null, "role") ) ? "logo" : "url";
                    returnMap.put( mapKey, xmlr.getAttributeValue(null, "URI") );
                    parseText(xmlr, "ExtLink"); // this line effectively just skips though until the end of the tag
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals(endTag)) break;
            }   
        }

        returnMap.put( "name", text );
        return returnMap;
    }

    private String parseDate (XMLStreamReader xmlr, String endTag) throws XMLStreamException {
        String date = xmlr.getAttributeValue(null, "date");
        if (date == null) {
            date = parseText(xmlr);
        }
        return date;
    }
    
    private void addFileToCategory(StudyFile sf, String catName, Study study) {
        StudyFileEditBean fileBean = new StudyFileEditBean(sf);
        fileBean.setFileCategoryName(catName);
        fileBean.addFileToCategory(study);
    } 

    private String determineFileCategory(String catName, String icpsrDesc, String icpsrId) {
        if (catName == null) {
            catName = icpsrDesc;
            
            if (catName != null) {
                if (icpsrId != null && !icpsrId.trim().equals("") ) {
                    catName = icpsrId + ". " + catName;
                }
            }
        }  
        
        return (catName != null ? catName : "");
    } 

    public Map determineId(File ddiFile) {
        Study dummyStudy = new Study();
        initializeCollections(dummyStudy);

        try {
            javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
            javax.xml.stream.XMLStreamReader xmlr = xmlif.createXMLStreamReader(new java.io.FileReader(ddiFile));
        
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("codeBook")) ; // skip the codeBook tag
                    else if (xmlr.getLocalName().equals("docDscr")) processDocDscr(xmlr, dummyStudy);
                    else if (xmlr.getLocalName().equals("stdyDscr")) processStdyDscr(xmlr, dummyStudy);
                    else break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapping: File Not Found!");
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred while processing DDI!!!!");
        }
        
        Map idMap = new HashMap();
        if ( !StringUtil.isEmpty( dummyStudy.getStudyId() ) ) idMap.put( "globalId", dummyStudy.getGlobalId() );
        if ( dummyStudy.getStudyOtherIds().size() > 0 ) idMap.put( "otherId", dummyStudy.getStudyOtherIds().get(0).getOtherId() );
        return idMap;
    }

    // </editor-fold>  
}
