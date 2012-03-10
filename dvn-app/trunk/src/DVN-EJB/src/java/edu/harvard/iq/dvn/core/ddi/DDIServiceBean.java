/*
 * DDIServiceBean.java
 *
 * Created on Jan 11, 2008, 3:08:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.ddi;

import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.OtherFile;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyDistributor;
import edu.harvard.iq.dvn.core.study.StudyField; 
import edu.harvard.iq.dvn.core.study.StudyFieldValue;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyGeoBounding;
import edu.harvard.iq.dvn.core.study.StudyGrant;
import edu.harvard.iq.dvn.core.study.StudyKeyword;
import edu.harvard.iq.dvn.core.study.StudyNote;
import edu.harvard.iq.dvn.core.study.StudyOtherId;
import edu.harvard.iq.dvn.core.study.StudyOtherRef;
import edu.harvard.iq.dvn.core.study.StudyProducer;
import edu.harvard.iq.dvn.core.study.StudyRelMaterial;
import edu.harvard.iq.dvn.core.study.StudyRelPublication;
import edu.harvard.iq.dvn.core.study.StudyRelStudy;
import edu.harvard.iq.dvn.core.study.StudySoftware;
import edu.harvard.iq.dvn.core.study.StudyTopicClass;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.SummaryStatistic;
import edu.harvard.iq.dvn.core.study.SummaryStatisticType;
import edu.harvard.iq.dvn.core.study.TabularDataFile;
import edu.harvard.iq.dvn.core.study.VariableCategory;
import edu.harvard.iq.dvn.core.study.VariableFormatType;
import edu.harvard.iq.dvn.core.study.VariableIntervalType;
import edu.harvard.iq.dvn.core.study.VariableRange;
import edu.harvard.iq.dvn.core.study.VariableRangeType;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.study.VersionContributor;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.util.PropertyUtil;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

/**
 *
 * @author Gustavo
 */
@Stateless
public class DDIServiceBean implements DDIServiceLocal {

    @EJB VariableServiceLocal varService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;

    // ddi constants
    public static final String SOURCE_DVN_3_0 = "DVN_3_0";
    
    public static final String AGENCY_HANDLE = "handle";
    public static final String REPLICATION_FOR_TYPE = "replicationFor";
    public static final String VAR_WEIGHTED = "wgtd";
    public static final String VAR_INTERVAL_CONTIN = "contin";
    public static final String VAR_INTERVAL_DISCRETE = "discrete";
    public static final String CAT_STAT_TYPE_FREQUENCY = "freq";
    public static final String VAR_FORMAT_TYPE_NUMERIC = "numeric";
    public static final String VAR_FORMAT_SCHEMA_ISO = "ISO";


    public static final String EVENT_START = "start";
    public static final String EVENT_END = "end";
    public static final String EVENT_SINGLE = "single";

    public static final String LEVEL_DVN = "dvn";
    public static final String LEVEL_DV = "dv";
    public static final String LEVEL_STUDY = "study";
    public static final String LEVEL_FILE = "file";
    public static final String LEVEL_VARIABLE = "variable";
    public static final String LEVEL_CATEGORY = "category";

    public static final String NOTE_TYPE_UNF = "VDC:UNF";
    public static final String NOTE_SUBJECT_UNF = "Universal Numeric Fingerprint";

    public static final String NOTE_TYPE_TERMS_OF_USE = "DVN:TOU";
    public static final String NOTE_SUBJECT_TERMS_OF_USE = "Terms Of Use";

    public static final String NOTE_TYPE_CITATION = "DVN:CITATION";
    public static final String NOTE_SUBJECT_CITATION = "Citation";

    public static final String NOTE_TYPE_VERSION_NOTE = "DVN:VERSION_NOTE";
    public static final String NOTE_SUBJECT_VERSION_NOTE= "Version Note";

    public static final String NOTE_TYPE_ARCHIVE_NOTE = "DVN:ARCHIVE_NOTE";
    public static final String NOTE_SUBJECT_ARCHIVE_NOTE= "Archive Note";

    public static final String NOTE_TYPE_ARCHIVE_DATE = "DVN:ARCHIVE_DATE";
    public static final String NOTE_SUBJECT_ARCHIVE_DATE= "Archive Date";
    
    public static final String NOTE_TYPE_EXTENDED_METADATA = "DVN:EXTENDED_METADATA";

    public static final String NOTE_TYPE_LOCKSS_CRAWL = "LOCKSS:CRAWLING";
    public static final String NOTE_SUBJECT_LOCKSS_PERM = "LOCKSS Permission";

    public static final String NOTE_TYPE_REPLICATION_FOR = "DVN:REPLICATION_FOR";
    
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

    private XMLInputFactory xmlInputFactory = null;
    private XMLOutputFactory xmlOutputFactory = null;


    public void ejbCreate() {
        // initialize lists
        variableFormatTypeList = varService.findAllVariableFormatType();
        variableIntervalTypeList = varService.findAllVariableIntervalType();
        summaryStatisticTypeList = varService.findAllSummaryStatisticType();
        variableRangeTypeList = varService.findAllVariableRangeType();

        xmlInputFactory = javax.xml.stream.XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);

        xmlOutputFactory = javax.xml.stream.XMLOutputFactory.newInstance();
        //xmlof.setProperty("javax.xml.stream.isPrefixDefaulting", java.lang.Boolean.TRUE);
    }

    public boolean isXmlFormat() {
        return true;
    }

    //**********************
    // EXPORT METHODS
    //**********************

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study s, OutputStream os) {
        if (s.getReleasedVersion() == null) {
            throw new IllegalArgumentException("Study does not have released version, study.id = " + s.getId());
        }

        if (s.isIsHarvested()) {
            exportOriginalDDIPlus(s.getReleasedVersion(), os);
        } else {
            XMLStreamWriter xmlw = null;
            try {
                xmlw = xmlOutputFactory.createXMLStreamWriter(os);
                xmlw.writeStartDocument();
                createCodeBook(xmlw, s.getReleasedVersion(), null, null);
                xmlw.writeEndDocument();
            } catch (XMLStreamException ex) {
                Logger.getLogger("global").log(Level.SEVERE, null, ex);
                throw new EJBException("ERROR occurred in exportStudy.", ex);
            } finally {
                try {
                    if (xmlw != null) {
                        xmlw.close();
                    }
                } catch (XMLStreamException ex) {
                }
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study s, OutputStream os, String xpathExclude, String xpathInclude) {
        if (s == null) {
            throw new IllegalArgumentException("ExportStudy called with a null study.");
        }
        if (s.getReleasedVersion() == null) {
            throw new IllegalArgumentException("Study does not have released version, study.id = " + s.getId());
        }
        
        if ((xpathExclude == null || "".equals(xpathExclude))
                &&
            (xpathInclude == null || "".equals(xpathInclude))) {
            this.exportStudy(s, os);
        } else {
            // partial export 
            if (s.isIsHarvested()) {
                throw new IllegalArgumentException("Partial export requested on a harvested study. (study id = " + s.getId() + ")");          
            }
            XMLStreamWriter xmlw = null;
            try {
                xmlw = xmlOutputFactory.createXMLStreamWriter(os);
                xmlw.writeStartDocument();
                createCodeBook(xmlw, s.getReleasedVersion(), xpathExclude, xpathInclude);
                xmlw.writeEndDocument();
            } catch (XMLStreamException ex) {
                Logger.getLogger("global").log(Level.SEVERE, null, ex);
                throw new EJBException("ERROR occurred during partial export of a study.", ex);
            } finally {
                try {
                    if (xmlw != null) {
                        xmlw.close();
                    }
                } catch (XMLStreamException ex) {
                }
            }
            
            
            
            
        }
        
    }
    
    public void exportDataFile(TabularDataFile tdf, OutputStream os)  {
        if (tdf.getReleasedFileMetadata() == null) {
            throw new IllegalArgumentException("StudyFile does not have a released version, study file id = " + tdf.getId() );
        }


        XMLStreamWriter xmlw = null;
        try {
            xmlw = xmlOutputFactory.createXMLStreamWriter(os);

            xmlw.writeStartDocument();

            xmlw.writeStartElement("codeBook");
            xmlw.writeDefaultNamespace("http://www.icpsr.umich.edu/DDI");
            writeAttribute( xmlw, "version", "2.0" );

            createFileDscr(xmlw, tdf.getReleasedFileMetadata(), null, null, null);
            createDataDscr(xmlw, tdf);

            xmlw.writeEndElement(); // codeBook

            xmlw.writeEndDocument();

        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in exportDataFile.", ex);
        } finally {
            try {
                if (xmlw != null) { xmlw.close(); }
            } catch (XMLStreamException ex) {}
        }
    }

    private void exportOriginalDDIPlus (StudyVersion sv, OutputStream os) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        XMLStreamWriter xmlw = null;

        File studyDir = new File(FileUtil.getStudyFileDir(), sv.getStudy().getAuthority() + File.separator + sv.getStudy().getStudyId());
        File originalImport = new File(studyDir, "original_imported_study.xml");

        if (originalImport.exists()) {
            try {
                in = new BufferedReader( new FileReader(originalImport) );
                out = new OutputStreamWriter(os);
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while (( line = in.readLine()) != null){
                    // check to see if this is the StudyDscr in order to add the extra docDscr
                    if (line.indexOf("<stdyDscr>") != -1) {
                        out.write( line.substring(0, line.indexOf("<stdyDscr>") ) );
                        out.write(System.getProperty("line.separator"));
                        out.flush();
                        
                        // now create DocDscr element (using StAX)
                        xmlw = xmlOutputFactory.createXMLStreamWriter(os);
                        createDocDscr(xmlw, sv.getMetadata(), null, null, null);
                        xmlw.close();

                        out.write(System.getProperty("line.separator"));
                        out.write( line.substring(line.indexOf("<stdyDscr>") ) );
                        out.write(System.getProperty("line.separator"));
                        out.flush();
                    } else {
                        out.write(line);
                        out.write(System.getProperty("line.separator"));
                        out.flush();
                    }
                }

            } catch (IOException ex) {
                throw new EJBException ("A problem occurred trying to export this study (original DDI Plus).");
            } catch (XMLStreamException ex) {
                throw new EJBException ("A problem occurred trying to create the DocDscr for this study (original DDI Plus).");
            } finally {
                try {
                    if (xmlw != null) { xmlw.close(); }
                } catch (XMLStreamException ex) { ex.printStackTrace(); }

                try {
                    if (in!=null) { in.close(); }
                } catch (IOException ex) { ex.printStackTrace(); }

                try {
                    if (out!=null) { out.close(); }
                } catch (IOException ex) { ex.printStackTrace(); }

            }
        } else {
            throw new EJBException ("There is no original import DDI for this study.");
        }
    }    


    // <editor-fold defaultstate="collapsed" desc="export methods">
    private void createCodeBook(XMLStreamWriter xmlw, StudyVersion sv, String xpathExclude, String xpathInclude) throws XMLStreamException {
        Metadata md = sv.getMetadata();
        
        String xpathCurrent = "codeBook";

        xmlw.writeStartElement(xpathCurrent);
        xmlw.writeDefaultNamespace("http://www.icpsr.umich.edu/DDI");
        writeAttribute( xmlw, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
        writeAttribute( xmlw, "xsi:schemaLocation", "http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd" );
        writeAttribute( xmlw, "version", "2.0" );

        createDocDscr(xmlw, md, xpathCurrent, xpathExclude, xpathInclude);
        createStdyDscr(xmlw, md, xpathCurrent, xpathExclude, xpathInclude);

        // iterate through files, saving other material files for the end
        List<FileMetadata> otherMatFiles = new ArrayList();
        for (FileMetadata fmd : sv.getFileMetadatas()) {
            StudyFile sf = fmd.getStudyFile();
            if ( sf instanceof TabularDataFile ) {
                createFileDscr(xmlw, fmd, xpathCurrent, xpathExclude, xpathInclude);
            } else {
                otherMatFiles.add(fmd);
            }
        }

        createDataDscr(xmlw, sv, xpathCurrent, xpathExclude, xpathInclude);

        // now go through otherMat files
        for (FileMetadata fmd : otherMatFiles) {
            createOtherMat(xmlw, fmd, xpathCurrent, xpathExclude, xpathInclude);
        }

        xmlw.writeEndElement(); // codeBook
    }

    private void createDocDscr(XMLStreamWriter xmlw, Metadata metadata, String xpathParent, String xpathExclude, String xpathInclude) throws XMLStreamException {
        Study study = metadata.getStudy();

        String currentElement = "docDscr"; 
        String xpathCurrent = xpathParent + "/" + currentElement; 
        
        if (xpathExclude != null && (xpathExclude.equals(xpathCurrent))) {
            return; 
        }
        
        if (xpathInclude == null || xpathInclude.startsWith(xpathCurrent)) {
            xmlw.writeStartElement(currentElement);
        
            // TODO: perform similar exclude/include tests on all the child 
            // elements of docDscr.
            // (for now we only support the exclusions and inclusions of the top-
            // level DDI parts)
        
            xmlw.writeStartElement("citation");

            // titlStmt
            xmlw.writeStartElement("titlStmt");

            xmlw.writeStartElement("titl");
            xmlw.writeCharacters( metadata.getTitle() );
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

            xmlw.writeEndElement(); // distStmt

            // verStmt (DVN versions)
            for (StudyVersion sv : study.getStudyVersions()) {
                if (sv.isWorkingCopy()) {
                    continue; // we do not want to incude any info about a working copy
                }

                xmlw.writeStartElement("verStmt");
                writeAttribute( xmlw, "source", "DVN" );

                xmlw.writeStartElement("version");
                writeAttribute( xmlw, "date", new SimpleDateFormat("yyyy-MM-dd").format(sv.getReleaseTime()) );
                writeAttribute( xmlw, "type", sv.getVersionState().toString() );
                xmlw.writeCharacters( sv.getVersionNumber().toString() );
                xmlw.writeEndElement(); // version

                String versionContributors = "";
                for (VersionContributor vc : sv.getVersionContributors()) {
                    if (!"".equals(versionContributors)) {
                        versionContributors += ", ";
                    }
                    versionContributors += vc.getContributor().getUserName();
                }
                xmlw.writeStartElement("verResp");
                xmlw.writeCharacters( versionContributors );
                xmlw.writeEndElement(); // verResp

                if (!StringUtil.isEmpty( sv.getVersionNote() )) {
                    xmlw.writeStartElement("notes");
                    writeAttribute( xmlw, "type", NOTE_TYPE_VERSION_NOTE );
                    writeAttribute( xmlw, "subject", NOTE_SUBJECT_VERSION_NOTE );
                    xmlw.writeCharacters( sv.getVersionNote());
                    xmlw.writeEndElement(); // notes
                }

                if (!StringUtil.isEmpty( sv.getArchiveNote() )) {
                    xmlw.writeStartElement("notes");
                    writeAttribute( xmlw, "type", NOTE_TYPE_ARCHIVE_NOTE );
                    writeAttribute( xmlw, "subject", NOTE_SUBJECT_ARCHIVE_NOTE );
                    xmlw.writeCharacters( sv.getArchiveNote());
                    xmlw.writeEndElement(); // notes
                }

                if (sv.getArchiveTime() != null) {
                    xmlw.writeStartElement("notes");
                    writeAttribute( xmlw, "type", NOTE_TYPE_ARCHIVE_DATE );
                    writeAttribute( xmlw, "subject", NOTE_SUBJECT_ARCHIVE_DATE );
                    xmlw.writeCharacters( new SimpleDateFormat("yyyy-MM-dd").format(sv.getArchiveTime()) );
                    xmlw.writeEndElement(); // notes
                }

                xmlw.writeEndElement(); // verStmt
            }

            // biblCit
            xmlw.writeStartElement("biblCit");
            writeAttribute( xmlw, "format", "DVN" );
            xmlw.writeCharacters( metadata.getTextCitation() );
            xmlw.writeEndElement(); // biblCit

            // holdings
            xmlw.writeEmptyElement("holdings");
            writeAttribute( xmlw, "URI", "http://" + PropertyUtil.getHostUrl() + "/dvn/study?globalId=" + study.getGlobalId() );


            xmlw.writeEndElement(); // citation
            xmlw.writeEndElement(); // docDscr
        }

    }

    private void createStdyDscr(XMLStreamWriter xmlw, Metadata metadata, String xpathParent, String xpathExclude, String xpathInclude) throws XMLStreamException {
        String currentElement = "stdyDscr"; 
        String xpathCurrent = xpathParent + "/" + currentElement; 
        
        if (xpathExclude != null && (xpathExclude.equals(xpathCurrent))) {
            return; 
        }
        
        if (xpathInclude == null || xpathInclude.startsWith(xpathCurrent)) {
            xmlw.writeStartElement(currentElement);
            createCitation(xmlw, metadata);
            createStdyInfo(xmlw, metadata);
            createMethod(xmlw, metadata);
            createDataAccs(xmlw, metadata);
            createOthrStdyMat(xmlw,metadata);
            createNotes(xmlw,metadata);
            xmlw.writeEndElement(); // stdyDscr
        }
    }

    private void createCitation(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        xmlw.writeStartElement("citation");

        // titlStmt
        xmlw.writeStartElement("titlStmt");

        xmlw.writeStartElement("titl");
        xmlw.writeCharacters( metadata.getTitle() );
        xmlw.writeEndElement(); // titl

        if ( !StringUtil.isEmpty( metadata.getSubTitle() ) ) {
            xmlw.writeStartElement("subTitl");
            xmlw.writeCharacters( metadata.getSubTitle() );
            xmlw.writeEndElement(); // subTitl
        }

        xmlw.writeStartElement("IDNo");
        writeAttribute( xmlw, "agency", "handle" );
        xmlw.writeCharacters( metadata.getStudy().getGlobalId() );
        xmlw.writeEndElement(); // IDNo

        for (StudyOtherId otherId : metadata.getStudyOtherIds()) {
            xmlw.writeStartElement("IDNo");
            writeAttribute( xmlw, "agency", otherId.getAgency() );
            xmlw.writeCharacters( otherId.getOtherId() );
            xmlw.writeEndElement(); // IDNo
        }

        xmlw.writeEndElement(); // titlStmt

        // rspStmt
        if (metadata.getStudyAuthors() != null && metadata.getStudyAuthors().size() > 0) {
            xmlw.writeStartElement("rspStmt");
            for (StudyAuthor author : metadata.getStudyAuthors()) {
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
        for (StudyProducer prod : metadata.getStudyProducers()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("producer");
            writeAttribute( xmlw, "abbr", prod.getAbbreviation() );
            writeAttribute( xmlw, "affiliation", prod.getAffiliation() );
            xmlw.writeCharacters( prod.getName() );
            createExtLink(xmlw, prod.getUrl(), null);
            createExtLink(xmlw, prod.getLogo(), "image");
            xmlw.writeEndElement(); // producer
        }
        if (!StringUtil.isEmpty( metadata.getProductionDate() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            createDateElement( xmlw, "prodDate", metadata.getProductionDate() );
        }
        if (!StringUtil.isEmpty( metadata.getProductionPlace() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("prodPlac");
            xmlw.writeCharacters( metadata.getProductionPlace() );
            xmlw.writeEndElement(); // prodPlac
        }
        for (StudySoftware soft : metadata.getStudySoftware()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("software");
            writeAttribute( xmlw, "version", soft.getSoftwareVersion() );
            xmlw.writeCharacters( soft.getName() );
            xmlw.writeEndElement(); // software
        }
        if (!StringUtil.isEmpty( metadata.getFundingAgency() )) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("fundAg");
            xmlw.writeCharacters( metadata.getFundingAgency() );
            xmlw.writeEndElement(); // fundAg
        }
        for (StudyGrant grant : metadata.getStudyGrants()) {
            prodStmtAdded = checkParentElement(xmlw, "prodStmt", prodStmtAdded);
            xmlw.writeStartElement("grantNo");
            writeAttribute( xmlw, "agency", grant.getAgency() );
            xmlw.writeCharacters( grant.getNumber() );
            xmlw.writeEndElement(); // grantNo
        }
        if (prodStmtAdded) xmlw.writeEndElement(); // prodStmt


        // distStmt
        boolean distStmtAdded = false;
        for (StudyDistributor dist : metadata.getStudyDistributors()) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            xmlw.writeStartElement("distrbtr");
            writeAttribute( xmlw, "abbr", dist.getAbbreviation() );
            writeAttribute( xmlw, "affiliation", dist.getAffiliation() );
            xmlw.writeCharacters( dist.getName() );
            createExtLink(xmlw, dist.getUrl(), null);
            createExtLink(xmlw, dist.getLogo(), "image");
            xmlw.writeEndElement(); // distrbtr
        }
        if (!StringUtil.isEmpty( metadata.getDistributorContact()) ||
                !StringUtil.isEmpty( metadata.getDistributorContactEmail()) ||
                !StringUtil.isEmpty( metadata.getDistributorContactAffiliation()) ) {

            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            xmlw.writeStartElement("contact");
            writeAttribute( xmlw, "email", metadata.getDistributorContactEmail() );
            writeAttribute( xmlw, "affiliation", metadata.getDistributorContactAffiliation() );
            xmlw.writeCharacters( metadata.getDistributorContact() );
            xmlw.writeEndElement(); // contact
        }
        if (!StringUtil.isEmpty( metadata.getDepositor() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            xmlw.writeStartElement("depositr");
            xmlw.writeCharacters( metadata.getDepositor() );
            xmlw.writeEndElement(); // depositr
        }
        if (!StringUtil.isEmpty( metadata.getDateOfDeposit() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            createDateElement( xmlw, "depDate", metadata.getDateOfDeposit() );
        }
        if (!StringUtil.isEmpty( metadata.getDistributionDate() )) {
            distStmtAdded = checkParentElement(xmlw, "distStmt", distStmtAdded);
            createDateElement( xmlw, "distDate", metadata.getDistributionDate() );
        }
        if (distStmtAdded) xmlw.writeEndElement(); // distStmt


        // serStmt
        boolean serStmtAdded = false;
        if (!StringUtil.isEmpty( metadata.getSeriesName() )) {
            serStmtAdded = checkParentElement(xmlw, "serStmt", serStmtAdded);
            xmlw.writeStartElement("serName");
            xmlw.writeCharacters( metadata.getSeriesName() );
            xmlw.writeEndElement(); // serName
        }

        if (!StringUtil.isEmpty( metadata.getSeriesInformation() )) {
            serStmtAdded = checkParentElement(xmlw, "serStmt", serStmtAdded);
            xmlw.writeStartElement("serInfo");
            xmlw.writeCharacters( metadata.getSeriesInformation() );
            xmlw.writeEndElement(); // serInfo
        }
        if (serStmtAdded) xmlw.writeEndElement(); // serStmt


        // verStmt
        boolean verStmtAdded = false;
        if (!StringUtil.isEmpty( metadata.getStudyVersionText()) || !StringUtil.isEmpty( metadata.getVersionDate()) ) {
            verStmtAdded = checkParentElement(xmlw, "verStmt", verStmtAdded);
            xmlw.writeStartElement("version");
            writeAttribute( xmlw, "date", metadata.getVersionDate() );
            xmlw.writeCharacters( metadata.getStudyVersionText() );
            xmlw.writeEndElement(); // version
        }
        if (verStmtAdded) xmlw.writeEndElement(); // verStmt
        
        // DVN Version info
        StudyVersion sv = metadata.getStudyVersion();
        xmlw.writeStartElement("verStmt");
        writeAttribute( xmlw, "source", "DVN" );
        xmlw.writeStartElement("version");
        writeAttribute( xmlw, "date", new SimpleDateFormat("yyyy-MM-dd").format(sv.getReleaseTime()) );
        writeAttribute( xmlw, "type", sv.getVersionState().toString() );
        xmlw.writeCharacters( sv.getVersionNumber().toString() );
        xmlw.writeEndElement(); // version
        xmlw.writeEndElement(); // verStmt

        // UNF note
        if (! StringUtil.isEmpty( metadata.getUNF()) ) {
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "level", LEVEL_STUDY );
            writeAttribute( xmlw, "type", NOTE_TYPE_UNF );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_UNF );
            xmlw.writeCharacters( metadata.getUNF() );
            xmlw.writeEndElement(); // notes
        }

        xmlw.writeEndElement(); // citation
    }

    private void createStdyInfo(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        boolean stdyInfoAdded = false;

        // subject
        boolean subjectAdded = false;
        for (StudyKeyword kw : metadata.getStudyKeywords()) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            subjectAdded = checkParentElement(xmlw, "subject", subjectAdded);
            xmlw.writeStartElement("keyword");
            writeAttribute( xmlw, "vocab", kw.getVocab() );
            writeAttribute( xmlw, "vocabURI", kw.getVocabURI() );
            xmlw.writeCharacters( kw.getValue() );
            xmlw.writeEndElement(); // keyword
        }
        for (StudyTopicClass tc : metadata.getStudyTopicClasses()) {
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
        for (StudyAbstract abst : metadata.getStudyAbstracts()) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            xmlw.writeStartElement("abstract");
            writeAttribute( xmlw, "date", abst.getDate() );
            xmlw.writeCharacters( abst.getText() );
            xmlw.writeEndElement(); // abstract
        }


        // sumDscr
        boolean sumDscrAdded = false;
        if (!StringUtil.isEmpty( metadata.getTimePeriodCoveredStart() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("timePrd");
            writeAttribute( xmlw, "event", EVENT_START );
            writeDateAttribute( xmlw, metadata.getTimePeriodCoveredStart() );
            xmlw.writeCharacters( metadata.getTimePeriodCoveredStart() );
            xmlw.writeEndElement(); // timePrd
        }
        if (!StringUtil.isEmpty( metadata.getTimePeriodCoveredEnd() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("timePrd");
            writeAttribute( xmlw, "event", EVENT_END );
            writeDateAttribute( xmlw, metadata.getTimePeriodCoveredEnd() );
            xmlw.writeCharacters( metadata.getTimePeriodCoveredEnd() );
            xmlw.writeEndElement(); // timePrd
        }
        if (!StringUtil.isEmpty( metadata.getDateOfCollectionStart() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("collDate");
            writeAttribute( xmlw, "event", EVENT_START );
            writeDateAttribute( xmlw, metadata.getDateOfCollectionStart() );
            xmlw.writeCharacters( metadata.getDateOfCollectionStart() );
            xmlw.writeEndElement(); // collDate
        }
        if (!StringUtil.isEmpty( metadata.getDateOfCollectionEnd() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("collDate");
            writeAttribute( xmlw, "event", EVENT_END );
            writeDateAttribute( xmlw, metadata.getDateOfCollectionEnd() );
            xmlw.writeCharacters( metadata.getDateOfCollectionEnd() );
            xmlw.writeEndElement(); // collDate
        }
        if (!StringUtil.isEmpty( metadata.getCountry() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("nation");
            xmlw.writeCharacters( metadata.getCountry() );
            xmlw.writeEndElement(); // nation
        }
        if (!StringUtil.isEmpty( metadata.getGeographicCoverage() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("geogCover");
            xmlw.writeCharacters( metadata.getGeographicCoverage() );
            xmlw.writeEndElement(); // geogCover
        }
        if (!StringUtil.isEmpty( metadata.getGeographicUnit() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("geogUnit");
            xmlw.writeCharacters( metadata.getGeographicUnit() );
            xmlw.writeEndElement(); // geogUnit
        }
        // we store geoboundings as list but there is only one
        if (metadata.getStudyGeoBoundings() != null && metadata.getStudyGeoBoundings().size() != 0) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            StudyGeoBounding gbb = metadata.getStudyGeoBoundings().get(0);
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

        if (!StringUtil.isEmpty( metadata.getUnitOfAnalysis() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("anlyUnit");
            xmlw.writeCharacters( metadata.getUnitOfAnalysis() );
            xmlw.writeEndElement(); // anlyUnit
        }

        if (!StringUtil.isEmpty( metadata.getUniverse() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("universe");
            xmlw.writeCharacters( metadata.getUniverse() );
            xmlw.writeEndElement(); // universe
        }

        if (!StringUtil.isEmpty( metadata.getKindOfData() )) {
            stdyInfoAdded = checkParentElement(xmlw, "stdyInfo", stdyInfoAdded);
            sumDscrAdded = checkParentElement(xmlw, "sumDscr", sumDscrAdded);
            xmlw.writeStartElement("dataKind");
            xmlw.writeCharacters( metadata.getKindOfData() );
            xmlw.writeEndElement(); // dataKind
        }
        if (sumDscrAdded) xmlw.writeEndElement(); // sumDscr


        if (stdyInfoAdded) xmlw.writeEndElement(); // stdyInfo
    }

    private void createMethod(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        boolean methodAdded = false;

        // dataColl
        boolean dataCollAdded = false;
        if (!StringUtil.isEmpty( metadata.getTimeMethod() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("timeMeth");
            xmlw.writeCharacters( metadata.getTimeMethod() );
            xmlw.writeEndElement(); // timeMeth
        }
        if (!StringUtil.isEmpty( metadata.getDataCollector() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("dataCollector");
            xmlw.writeCharacters( metadata.getDataCollector() );
            xmlw.writeEndElement(); // dataCollector
        }
        if (!StringUtil.isEmpty( metadata.getFrequencyOfDataCollection() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("frequenc");
            xmlw.writeCharacters( metadata.getFrequencyOfDataCollection() );
            xmlw.writeEndElement(); // frequenc
        }
        if (!StringUtil.isEmpty( metadata.getSamplingProcedure() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("sampProc");
            xmlw.writeCharacters( metadata.getSamplingProcedure() );
            xmlw.writeEndElement(); // sampProc
        }
        if (!StringUtil.isEmpty( metadata.getDeviationsFromSampleDesign() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("deviat");
            xmlw.writeCharacters( metadata.getDeviationsFromSampleDesign() );
            xmlw.writeEndElement(); // deviat
        }
        if (!StringUtil.isEmpty( metadata.getCollectionMode() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("collMode");
            xmlw.writeCharacters( metadata.getCollectionMode() );
            xmlw.writeEndElement(); // collMode
        }

        if (!StringUtil.isEmpty( metadata.getResearchInstrument() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("resInstru");
            xmlw.writeCharacters( metadata.getResearchInstrument() );
            xmlw.writeEndElement(); // resInstru
        }
        //source
        boolean sourcesAdded = false;
        if (!StringUtil.isEmpty( metadata.getDataSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("dataSrc");
            xmlw.writeCharacters( metadata.getDataSources() );
            xmlw.writeEndElement(); // dataSrc
        }

        if (!StringUtil.isEmpty( metadata.getOriginOfSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcOrig");
            xmlw.writeCharacters( metadata.getOriginOfSources() );
            xmlw.writeEndElement(); // srcOrig
        }

        if (!StringUtil.isEmpty( metadata.getCharacteristicOfSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcChar");
            xmlw.writeCharacters( metadata.getCharacteristicOfSources() );
            xmlw.writeEndElement(); // srcChar
        }

        if (!StringUtil.isEmpty( metadata.getAccessToSources() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            sourcesAdded = checkParentElement(xmlw, "sources", sourcesAdded);
            xmlw.writeStartElement("srcDocu");
            xmlw.writeCharacters( metadata.getAccessToSources() );
            xmlw.writeEndElement(); // srcDocu
        }
        if (sourcesAdded) xmlw.writeEndElement(); // sources

        if (!StringUtil.isEmpty( metadata.getDataCollectionSituation() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("collSitu");
            xmlw.writeCharacters( metadata.getDataCollectionSituation() );
            xmlw.writeEndElement(); // collSitu
        }

        if (!StringUtil.isEmpty( metadata.getActionsToMinimizeLoss() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("actMin");
            xmlw.writeCharacters( metadata.getActionsToMinimizeLoss() );
            xmlw.writeEndElement(); // actMin
        }

        if (!StringUtil.isEmpty( metadata.getControlOperations() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("ConOps");
            xmlw.writeCharacters( metadata.getControlOperations() );
            xmlw.writeEndElement(); // ConOps
        }

        if (!StringUtil.isEmpty( metadata.getWeighting() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("weight");
            xmlw.writeCharacters( metadata.getWeighting() );
            xmlw.writeEndElement(); // weight
        }

        if (!StringUtil.isEmpty( metadata.getCleaningOperations() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            dataCollAdded = checkParentElement(xmlw, "dataColl", dataCollAdded);
            xmlw.writeStartElement("cleanOps");
            xmlw.writeCharacters( metadata.getCleaningOperations() );
            xmlw.writeEndElement(); // cleanOps
        }
        if (dataCollAdded) xmlw.writeEndElement(); // dataColl


        // notes
        
        // We use this "notes" section (<method><dataColl.../><notes .../>)  
        // for different things:
        // Its default use has been to store the StudyLevelErrorNotes from 
        // the standard DVN Metadata; we continue storing these as 
        // simply "<notes>" - with no attributes. 
        //
        // We'll also be using these notes to store extended, template-based
        // metadata fields. For these, the note should be storing both 
        // the name and the value of the field. These notes will be tagged
        // with the special attributes: type="DVN:EXTENDED_METADATA" and
        // subject="TEMPLATE:XXX;FIELD:YYY"
        
        // StudyLevelErrorNotes:
        // TODO: create an explicit type attribute for this.
        if (!StringUtil.isEmpty( metadata.getStudyLevelErrorNotes() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            xmlw.writeStartElement("notes");
            xmlw.writeCharacters( metadata.getStudyLevelErrorNotes() );
            xmlw.writeEndElement(); // notes
        }

        // Extended metadata (will produce multiple notes for different and/or
        // multiple extended fields: 
        String templateName = metadata.getStudy().getTemplate().getName();
        for (StudyField extField : metadata.getStudyFields()) {
            for (StudyFieldValue extFieldValue : extField.getStudyFieldValues()) {
                try {
                    String extFieldName = extField.getName();
                    String extFieldStrValue = extFieldValue.getStrValue();

                    if (extFieldName != null
                            && !extFieldName.equals("")
                            && extFieldStrValue != null
                            && !extFieldStrValue.equals("")) {

                        String fieldDefinition = "TEMPLATE:" + templateName + ";FIELD:" + extFieldName;

                        methodAdded = checkParentElement(xmlw, "method", methodAdded);
                        xmlw.writeStartElement("notes");
                        writeAttribute(xmlw, "type", NOTE_TYPE_EXTENDED_METADATA);
                        writeAttribute(xmlw, "subject", fieldDefinition);
                        xmlw.writeCharacters(extFieldStrValue);
                        xmlw.writeEndElement(); // notes
                    }

                } catch (Exception ex) {
                        
                // do nothing - if we can't retrieve the field, we are 
                // not going to export it, that's all. 
                }
            }
        }
            
        // anlyInfo
        boolean anlyInfoAdded = false;
        if (!StringUtil.isEmpty( metadata.getResponseRate() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("respRate");
            xmlw.writeCharacters( metadata.getResponseRate() );
            xmlw.writeEndElement(); // getResponseRate
        }

        if (!StringUtil.isEmpty( metadata.getSamplingErrorEstimate() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("EstSmpErr");
            xmlw.writeCharacters( metadata.getSamplingErrorEstimate() );
            xmlw.writeEndElement(); // EstSmpErr
        }

        if (!StringUtil.isEmpty( metadata.getOtherDataAppraisal() )) {
            methodAdded = checkParentElement(xmlw, "method", methodAdded);
            anlyInfoAdded = checkParentElement(xmlw, "anlyInfo", anlyInfoAdded);
            xmlw.writeStartElement("dataAppr");
            xmlw.writeCharacters( metadata.getOtherDataAppraisal() );
            xmlw.writeEndElement(); // dataAppr
        }
        if (anlyInfoAdded) xmlw.writeEndElement(); // anlyInfo


        if (methodAdded) xmlw.writeEndElement(); // method
    }

    private void createDataAccs(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        boolean dataAccsAdded = false;

        // setAvail
        boolean setAvailAdded = false;
        if (!StringUtil.isEmpty( metadata.getPlaceOfAccess() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("accsPlac");
            xmlw.writeCharacters( metadata.getPlaceOfAccess() );
            xmlw.writeEndElement(); // getStudyCompletion
        }
        if (!StringUtil.isEmpty( metadata.getOriginalArchive() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("origArch");
            xmlw.writeCharacters( metadata.getOriginalArchive() );
            xmlw.writeEndElement(); // origArch
        }
        if (!StringUtil.isEmpty( metadata.getAvailabilityStatus() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("avlStatus");
            xmlw.writeCharacters( metadata.getAvailabilityStatus() );
            xmlw.writeEndElement(); // avlStatus
        }
        if (!StringUtil.isEmpty( metadata.getCollectionSize() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("collSize");
            xmlw.writeCharacters( metadata.getCollectionSize() );
            xmlw.writeEndElement(); // collSize
        }
        if (!StringUtil.isEmpty( metadata.getStudyCompletion() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            setAvailAdded = checkParentElement(xmlw, "setAvail", setAvailAdded);
            xmlw.writeStartElement("complete");
            xmlw.writeCharacters( metadata.getStudyCompletion() );
            xmlw.writeEndElement(); // complete
        }
        if (setAvailAdded) xmlw.writeEndElement(); // setAvail

        // useStmt
        boolean useStmtAdded = false;
        if (!StringUtil.isEmpty( metadata.getConfidentialityDeclaration() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("confDec");
            xmlw.writeCharacters( metadata.getConfidentialityDeclaration() );
            xmlw.writeEndElement(); // confDec
        }

        if (!StringUtil.isEmpty( metadata.getSpecialPermissions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("specPerm");
            xmlw.writeCharacters( metadata.getSpecialPermissions() );
            xmlw.writeEndElement(); // specPerm
        }

        if (!StringUtil.isEmpty( metadata.getRestrictions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("restrctn");
            xmlw.writeCharacters( metadata.getRestrictions() );
            xmlw.writeEndElement(); // restrctn
        }


        if (!StringUtil.isEmpty( metadata.getContact() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("contact");
            xmlw.writeCharacters( metadata.getContact() );
            xmlw.writeEndElement(); // contact
        }

        if (!StringUtil.isEmpty( metadata.getCitationRequirements() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("citReq");
            xmlw.writeCharacters( metadata.getCitationRequirements() );
            xmlw.writeEndElement(); // citReq
        }

        if (!StringUtil.isEmpty( metadata.getDepositorRequirements() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("deposReq");
            xmlw.writeCharacters( metadata.getDepositorRequirements() );
            xmlw.writeEndElement(); // deposReq
        }

        if (!StringUtil.isEmpty( metadata.getConditions() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("conditions");
            xmlw.writeCharacters( metadata.getConditions() );
            xmlw.writeEndElement(); // conditions
        }

        if (!StringUtil.isEmpty( metadata.getDisclaimer() )) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            useStmtAdded = checkParentElement(xmlw, "useStmt", useStmtAdded);
            xmlw.writeStartElement("disclaimer");
            xmlw.writeCharacters( metadata.getDisclaimer() );
            xmlw.writeEndElement(); // disclaimer
        }
        if (useStmtAdded) xmlw.writeEndElement(); // useStmt


        // terms of use notes
        Study study = metadata.getStudy();
        String dvTermsOfUse = null;
        String dvnTermsOfUse = null;

        if (study.isIsHarvested() ) {
            dvTermsOfUse = metadata.getHarvestDVTermsOfUse();
            dvnTermsOfUse = metadata.getHarvestDVNTermsOfUse();
        } else {
            dvTermsOfUse = study.getOwner().isDownloadTermsOfUseEnabled() ? study.getOwner().getDownloadTermsOfUse() : null;
            dvnTermsOfUse = vdcNetworkService.find().isDownloadTermsOfUseEnabled() ? vdcNetworkService.find().getDownloadTermsOfUse() : null;
        }
        
        // Are we missing the study-level terms of use here? 
        // that would be:
        // if (metadata.isTermsOfUseEnabled()) { ... }
        //  -- Actually, no, it looks like isTermsOfUseEnabled does not rely on a 
        // boolean stored in the database, but checks if any field from a list
        // of "terms of use-related" fields is present. 
        // Presumably, all these values, if present, are getting exported as 
        // the corresponding native DDI fields. 
        //      -- L.A., Feb. 2012
        
        if (!StringUtil.isEmpty(dvTermsOfUse)) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "level", LEVEL_DV );
            writeAttribute( xmlw, "type", NOTE_TYPE_TERMS_OF_USE );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_TERMS_OF_USE );
            xmlw.writeCharacters( dvTermsOfUse );
            xmlw.writeEndElement(); // notes
        }
        if (!StringUtil.isEmpty(dvnTermsOfUse)) {
            dataAccsAdded = checkParentElement(xmlw, "dataAccs", dataAccsAdded);
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "level", LEVEL_DVN );
            writeAttribute( xmlw, "type", NOTE_TYPE_TERMS_OF_USE );
            writeAttribute( xmlw, "subject", NOTE_SUBJECT_TERMS_OF_USE );
            xmlw.writeCharacters( dvnTermsOfUse );
            xmlw.writeEndElement(); // notes
        }


        if (dataAccsAdded) xmlw.writeEndElement(); // dataAccs
    }

    private void createOthrStdyMat(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        boolean othrStdyMatAdded = false;

        for (StudyRelMaterial rm : metadata.getStudyRelMaterials()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relMat");
            xmlw.writeCharacters( rm.getText() );
            xmlw.writeEndElement(); // relMat
        }
        for (StudyRelStudy rs : metadata.getStudyRelStudies()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relStdy");
            xmlw.writeCharacters( rs.getText() );
            xmlw.writeEndElement(); // relStdy
        }
        for (StudyRelPublication rp : metadata.getStudyRelPublications()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("relPubl");
            createInnerCitation(xmlw,rp);
            xmlw.writeEndElement(); // relPubl
        }
        for (StudyOtherRef or : metadata.getStudyOtherRefs()) {
            othrStdyMatAdded = checkParentElement(xmlw, "othrStdyMat", othrStdyMatAdded);
            xmlw.writeStartElement("otherRefs");
            xmlw.writeCharacters( or.getText() );
            xmlw.writeEndElement(); // otherRefs
        }


        if (othrStdyMatAdded) xmlw.writeEndElement(); // othrStdyMat
    }
    
    private void createInnerCitation(XMLStreamWriter xmlw, StudyRelPublication publication) throws XMLStreamException {
        // currently this field only accepts related publications, but could in theory generate a citation for other elements
        // if we do this, let's creater an interface, and then have StudyRelPublication and others extend that
        xmlw.writeStartElement("citation");
        writeAttribute( xmlw, "source", SOURCE_DVN_3_0 );
        
        if (!StringUtil.isEmpty(publication.getIdNumber())) {
            xmlw.writeStartElement("titlStmt");
            xmlw.writeStartElement("IDNo");
            writeAttribute( xmlw, "agency", publication.getIdType() );
            xmlw.writeCharacters( publication.getIdNumber() );
            xmlw.writeEndElement(); // IDNo
            xmlw.writeEndElement(); // titlStmt
        }
        
        xmlw.writeStartElement("biblCit");
        xmlw.writeCharacters( publication.getText() );
        xmlw.writeEndElement(); // biblCit
        
        if (!StringUtil.isEmpty(publication.getUrl())) {
            xmlw.writeStartElement("holdings");
            writeAttribute( xmlw, "URI", publication.getUrl() );
            xmlw.writeCharacters( publication.getUrl() ); // for now, just put URL here, since we don't have a title for the holdings
            xmlw.writeEndElement(); // holdings
        }
        
        if (publication.isReplicationData()) {
            xmlw.writeEmptyElement("notes");
            writeAttribute( xmlw, "type", NOTE_TYPE_REPLICATION_FOR );
        }
        
        xmlw.writeEndElement(); // citation       
        
        
    }

    private void createNotes(XMLStreamWriter xmlw, Metadata metadata) throws XMLStreamException {
        for (StudyNote note : metadata.getStudyNotes()) {
            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "type", note.getType() );
            writeAttribute( xmlw, "subject", note.getSubject() );
            xmlw.writeCharacters( note.getText() );
            xmlw.writeEndElement(); // notes
        }
    }

    private void createFileDscr(XMLStreamWriter xmlw, FileMetadata fm, String xpathParent, String xpathExclude, String xpathInclude) throws XMLStreamException {
        String currentElement = "fileDscr"; 
        String xpathCurrent = xpathParent + "/" + currentElement; 
        
        if (xpathExclude != null && (xpathExclude.equals(xpathCurrent))) {
            return; 
        }
        
        if (xpathInclude == null || xpathInclude.startsWith(xpathCurrent)) {       
            TabularDataFile tdf = (TabularDataFile) fm.getStudyFile();
            DataTable dt = tdf.getDataTable();

            xmlw.writeStartElement(currentElement);
            writeAttribute( xmlw, "ID", "f" + tdf.getId().toString() );
            writeAttribute( xmlw, "URI", determineFileURI(fm) );

            // fileTxt
            xmlw.writeStartElement("fileTxt");

            xmlw.writeStartElement("fileName");
            xmlw.writeCharacters( fm.getLabel() );
            xmlw.writeEndElement(); // fileName

            xmlw.writeStartElement("fileCont");
            xmlw.writeCharacters( fm.getDescription() );
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
            xmlw.writeCharacters( tdf.getFileType() );
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
            xmlw.writeCharacters( fm.getCategory() );
            xmlw.writeEndElement(); // notes

            // A special note for LOCKSS crawlers indicating the restricted
            // status of the file:

            if (tdf != null && isRestrictedFile(tdf)) {
                xmlw.writeStartElement("notes");
                writeAttribute( xmlw, "type", NOTE_TYPE_LOCKSS_CRAWL );
                writeAttribute( xmlw, "level", LEVEL_FILE );
                writeAttribute( xmlw, "subject", NOTE_SUBJECT_LOCKSS_PERM );
                xmlw.writeCharacters( "restricted" );
                xmlw.writeEndElement(); // notes

            }

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
    }

    private String determineFileURI(FileMetadata fm) {
        String fileURI = "";
        StudyFile sf = fm.getStudyFile();
        Study s = sf.getStudy();

        // determine whether file is local or harvested
        if (sf.isRemote()) {
            fileURI = sf.getFileSystemLocation();
        } else {
            fileURI = "http://" + PropertyUtil.getHostUrl() + "/dvn/dv/" + s.getOwner().getAlias() + "/FileDownload/";
            try {
                fileURI += URLEncoder.encode(fm.getLabel(), "UTF-8") + "?fileId=" + sf.getId();
            } catch (IOException e) {
                throw new EJBException(e);
            }
        }

        return fileURI;
    }

    private void createOtherMat(XMLStreamWriter xmlw, FileMetadata fm, String xpathParent, String xpathExclude, String xpathInclude) throws XMLStreamException {
        StudyFile sf = fm.getStudyFile();

        String currentElement = "otherMat"; 
        String xpathCurrent = xpathParent + "/" + currentElement; 
        
        if (xpathExclude != null && (xpathExclude.equals(xpathCurrent))) {
            return; 
        }
        
        if (xpathInclude == null || xpathInclude.startsWith(xpathCurrent)) {
            xmlw.writeStartElement("otherMat");
            writeAttribute( xmlw, "level", LEVEL_STUDY );
            writeAttribute( xmlw, "URI", determineFileURI(fm) );

            xmlw.writeStartElement("labl");
            xmlw.writeCharacters( fm.getLabel() );
            xmlw.writeEndElement(); // labl

            xmlw.writeStartElement("txt");
            xmlw.writeCharacters( fm.getDescription() );
            xmlw.writeEndElement(); // txt

            xmlw.writeStartElement("notes");
            writeAttribute( xmlw, "type", "vdc:category" );
            xmlw.writeCharacters( fm.getCategory() );
            xmlw.writeEndElement(); // notes

            // A special note for LOCKSS crawlers indicating the restricted
            // status of the file:

            if (sf != null && isRestrictedFile(sf)) {
                xmlw.writeStartElement("notes");
                writeAttribute( xmlw, "type", NOTE_TYPE_LOCKSS_CRAWL );
                writeAttribute( xmlw, "level", LEVEL_FILE );
                writeAttribute( xmlw, "subject", NOTE_SUBJECT_LOCKSS_PERM );
                xmlw.writeCharacters( "restricted" );
                xmlw.writeEndElement(); // notes

            }

            xmlw.writeEndElement(); // otherMat
        }
    }

    private Boolean isRestrictedFile(StudyFile file) {
        Study study = null;
        VDC vdc = null;

        study = file.getStudy();

        if (study != null) {
            vdc = study.getOwner();
        }


        if (vdc != null && vdc.isFilesRestricted()) {
            return true;
        }

        if (study != null && study.isRestricted()) {
            return true;
        }

        if (file.isRestricted()) {
            return true;
        }

        return false;
    }

    private void createDataDscr(XMLStreamWriter xmlw, StudyVersion studyVersion, String xpathParent, String xpathExclude, String xpathInclude) throws XMLStreamException {
        boolean dataDscrAdded = false;

        String currentElement = "dataDscr"; 
        String xpathCurrent = xpathParent + "/" + currentElement; 
        
        if (xpathExclude != null && (xpathExclude.equals(xpathCurrent))) {
            return; 
        }
        
        if (xpathInclude == null || xpathInclude.startsWith(xpathCurrent)) {
            for (FileMetadata fmd : studyVersion.getFileMetadatas()) {
                StudyFile sf = fmd.getStudyFile();
                if ( sf instanceof TabularDataFile ) {
                    TabularDataFile tdf = (TabularDataFile) sf;
                    if ( tdf.getDataTable().getDataVariables().size() > 0 ) {
                        dataDscrAdded = checkParentElement(xmlw, "dataDscr", dataDscrAdded);
                        Iterator varIter = varService.getDataVariablesByFileOrder( tdf.getDataTable().getId() ).iterator();
                        while (varIter.hasNext()) {
                            DataVariable dv = (DataVariable) varIter.next();
                            createVar(xmlw, dv);
                        }
                    }
                }
            }

            if (dataDscrAdded) xmlw.writeEndElement(); //dataDscr
        }
    }


    private void createDataDscr(XMLStreamWriter xmlw, TabularDataFile tdf) throws XMLStreamException {
        // this version is to produce the dataDscr for just one file
        if (tdf.getDataTable().getDataVariables().size() > 0 ) {
            xmlw.writeStartElement("dataDscr");
            Iterator varIter = varService.getDataVariablesByFileOrder( tdf.getDataTable().getId() ).iterator();
            while (varIter.hasNext()) {
                DataVariable dv = (DataVariable) varIter.next();
                createVar(xmlw, dv);
            }
            xmlw.writeEndElement(); // dataDscr
        }
    }

    private void createVar(XMLStreamWriter xmlw, DataVariable dv) throws XMLStreamException {
        xmlw.writeStartElement("var");
        writeAttribute( xmlw, "ID", "v" + dv.getId().toString() );
        writeAttribute( xmlw, "name", dv.getName() );

        if (dv.getNumberOfDecimalPoints() != null) {
            writeAttribute(xmlw, "dcml", dv.getNumberOfDecimalPoints().toString() );
        }

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
            if (cat.isMissing()) {
                writeAttribute(xmlw, "missing", "Y");
            }
            
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
                // if frequency is actually a long value, we want to write "100" instead of "100.0"
                if ( Math.floor(cat.getFrequency()) == cat.getFrequency() ) {
                    xmlw.writeCharacters( new Long(cat.getFrequency().longValue()).toString() );
                } else {
                    xmlw.writeCharacters( cat.getFrequency().toString() );
                }
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
        writeAttribute( xmlw, "category", dv.getFormatCategory() );

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
        if ( !StringUtil.isEmpty(uri) ) {
            xmlw.writeEmptyElement("ExtLink");
            writeAttribute( xmlw, "URI", uri );
            if (role != null) {
                writeAttribute( xmlw, "role", role );
            }
        }
    }

    private void createDateElement(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        xmlw.writeStartElement(name);
        writeDateAttribute(xmlw, value);
        xmlw.writeCharacters( value );
        xmlw.writeEndElement();
}

    private void writeAttribute(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
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




    //**********************
    // IMPORT METHODS
    //**********************

    public Map mapDDI(String xmlToParse, StudyVersion studyVersion) {
        StringReader reader = null;
        XMLStreamReader xmlr = null;
        Map filesMap = new HashMap();
        
                
        try {
            reader = new StringReader(xmlToParse);
            xmlr =  xmlInputFactory.createXMLStreamReader(reader);
            processDDI( xmlr, studyVersion, filesMap);
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI.", ex);
        } finally {
            try {
                if (xmlr != null) { xmlr.close(); }
            } catch (XMLStreamException ex) {}

            if (reader != null) { reader.close();}
        }
        return filesMap;
    }

    public Map mapDDI(File ddiFile, StudyVersion studyVersion) {
        FileInputStream in = null;
        XMLStreamReader xmlr = null;
        Map filesMap = new HashMap();

        try {
            in = new FileInputStream(ddiFile);
            xmlr =  xmlInputFactory.createXMLStreamReader(in);
            processDDI( xmlr, studyVersion, filesMap );
        } catch (FileNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI: File Not Found!");
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI.", ex);
        } finally {
            try {
                if (xmlr != null) { xmlr.close(); }
            } catch (XMLStreamException ex) {}

            try {
                if (in != null) { in.close();}
            } catch (IOException ex) {}
        }

        return filesMap;
    }

    public Map reMapDDI(String xmlToParse, StudyVersion studyVersion, Map filesMap) {
        StringReader reader = null;
        XMLStreamReader xmlr = null;
        Map variablesMap;

        try {
            reader = new StringReader(xmlToParse);
            xmlr =  xmlInputFactory.createXMLStreamReader(reader);
            variablesMap = processDDIdataSection( xmlr, studyVersion, filesMap );
        }  catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI.", ex);
        } finally {
            try {
                if (xmlr != null) { xmlr.close(); }
            } catch (XMLStreamException ex) {}

            if (reader != null) { reader.close();}

        }
        return variablesMap;
    }



    public Map reMapDDI(File ddiFile, StudyVersion studyVersion, Map filesMap) {
        FileInputStream in = null;
        XMLStreamReader xmlr = null;
        Map variablesMap;

        try {
            in = new FileInputStream(ddiFile);
            xmlr =  xmlInputFactory.createXMLStreamReader(in);
            variablesMap = processDDIdataSection( xmlr, studyVersion, filesMap );
        } catch (FileNotFoundException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI: File Not Found!");
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new EJBException("ERROR occurred in mapDDI.", ex);
        } finally {
            try {
                if (xmlr != null) { xmlr.close(); }
            } catch (XMLStreamException ex) {}

            try {
                if (in != null) { in.close();}
            } catch (IOException ex) {}
        }
        return variablesMap;
    }

    // <editor-fold defaultstate="collapsed" desc="import methods">
    private void processDDI( XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {
        initializeCollections(studyVersion); // not sure we need this call; to be investigated
        
        // make sure we have a codeBook
        //while ( xmlr.next() == XMLStreamConstants.COMMENT ); // skip pre root comments
        xmlr.nextTag();
        xmlr.require(XMLStreamConstants.START_ELEMENT, null, "codeBook");

        // Some DDIs provide an ID in the <codeBook> section.
        // We are going to treat it as just another otherId.
        // (we've seen instances where this ID was the only ID found in
        // in a harvested DDI).

        String codeBookLevelId = xmlr.getAttributeValue(null, "ID");
        
        // (but first we will parse and process the entire DDI - and only 
        // then add this codeBook-level id to the list of identifiers; i.e., 
        // we don't want it to be the first on the list, if one or more
        // ids are available in the studyDscr section - those should take 
        // precedence!)
        // In fact, we should only use these IDs when no ID is available down 
        // in the study description section!
        
        processCodeBook(xmlr, studyVersion, filesMap);
        
        if (codeBookLevelId != null && !codeBookLevelId.equals("")) {
            if (studyVersion.getMetadata().getStudyOtherIds().size() == 0) {
                // this means no ids were found during the parsing of the 
                // study description section. we'll use the one we found in 
                // the codeBook entry:
                StudyOtherId sid = new StudyOtherId();
                sid.setOtherId( codeBookLevelId );
                sid.setMetadata(studyVersion.getMetadata());
                studyVersion.getMetadata().getStudyOtherIds().add(sid);
            }
        }

    }

     private Map processDDIdataSection( XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {
        Map variablesMap = null;

        xmlr.nextTag();
        xmlr.require(XMLStreamConstants.START_ELEMENT, null, "codeBook");
        variablesMap = processCodeBookDataSection(xmlr, studyVersion, filesMap);
        return variablesMap;
    }


    private void initializeCollections(StudyVersion studyVersion) {
        // initialize the collections
        Metadata metadata = studyVersion.getMetadata();
        metadata.getStudyVersion().getStudy().setStudyFiles( new ArrayList() );
        metadata.setStudyAbstracts( new ArrayList() );
        metadata.setStudyAuthors( new ArrayList() );
        metadata.setStudyDistributors( new ArrayList() );
        metadata.setStudyGeoBoundings(new ArrayList());
        metadata.setStudyGrants(new ArrayList());
        metadata.setStudyKeywords(new ArrayList());
        metadata.setStudyNotes(new ArrayList());
        metadata.setStudyOtherIds(new ArrayList());
        metadata.setStudyOtherRefs(new ArrayList());
        metadata.setStudyProducers(new ArrayList());
        metadata.setStudyRelMaterials(new ArrayList());
        metadata.setStudyRelPublications(new ArrayList());
        metadata.setStudyRelStudies(new ArrayList());
        metadata.setStudySoftware(new ArrayList());
        metadata.setStudyTopicClasses(new ArrayList());

        studyVersion.setFileMetadatas( new ArrayList() );
    }

    private void processCodeBook( XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {
        Metadata metadata = studyVersion.getMetadata();

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("docDscr")) {
                    processDocDscr(xmlr, metadata);
                }
                else if (xmlr.getLocalName().equals("stdyDscr")) {
                    processStdyDscr(xmlr, studyVersion);
                }
                else if (xmlr.getLocalName().equals("fileDscr")) {
                    processFileDscr(xmlr, studyVersion, filesMap);
                }
                //else if (xmlr.getLocalName().equals("dataDscr")) processDataDscr(xmlr, filesMap);
                else if (xmlr.getLocalName().equals("otherMat")) {
                    processOtherMat(xmlr, studyVersion);
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("codeBook")) return;
            }
        }
    }

    private Map processCodeBookDataSection( XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {
        //Map filesMap = new HashMap();
        Map variablesMap = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataDscr")) {
                    variablesMap = processDataDscrForReal(xmlr, filesMap);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("codeBook")) return variablesMap;
            }
        }

        return variablesMap;
    }

    private void processDocDscr(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("IDNo") && StringUtil.isEmpty(metadata.getStudy().getStudyId()) ) {
                    // this will set a StudyId if it has not yet been set; it will get overridden by a metadata
                    // id in the StudyDscr section, if one exists
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( parseText(xmlr), metadata.getStudy() );
                    }
                } else if ( xmlr.getLocalName().equals("holdings") && StringUtil.isEmpty(metadata.getHarvestHoldings()) ) {
                    metadata.setHarvestHoldings( xmlr.getAttributeValue(null, "URI") );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("docDscr")) return;
            }
        }
    }

    private void processStdyDscr(XMLStreamReader xmlr, StudyVersion sv) throws XMLStreamException {
        Metadata metadata = sv.getMetadata();
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) processCitation(xmlr, sv);
                else if (xmlr.getLocalName().equals("stdyInfo")) processStdyInfo(xmlr, metadata);
                else if (xmlr.getLocalName().equals("method")) processMethod(xmlr, metadata);
                else if (xmlr.getLocalName().equals("dataAccs")) processDataAccs(xmlr, metadata);
                else if (xmlr.getLocalName().equals("othrStdyMat")) processOthrStdyMat(xmlr, metadata);
                else if (xmlr.getLocalName().equals("notes")) processNotes(xmlr, metadata);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("stdyDscr")) return;
            }
        }
    }

     private void processCitation(XMLStreamReader xmlr, StudyVersion sv) throws XMLStreamException {
        Metadata metadata = sv.getMetadata();
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) processTitlStmt(xmlr, metadata);
                else if (xmlr.getLocalName().equals("rspStmt")) processRspStmt(xmlr,metadata);
                else if (xmlr.getLocalName().equals("prodStmt")) processProdStmt(xmlr,metadata);
                else if (xmlr.getLocalName().equals("distStmt")) processDistStmt(xmlr,metadata);
                else if (xmlr.getLocalName().equals("serStmt")) processSerStmt(xmlr,metadata);
                else if (xmlr.getLocalName().equals("verStmt")) processVerStmt(xmlr,sv);
                else if (xmlr.getLocalName().equals("notes")) {
                    String _note = parseNoteByType( xmlr, NOTE_TYPE_UNF );
                    if (_note != null) {
                        metadata.setUNF( parseUNF( _note ) );
                    } else {
                        processNotes(xmlr, metadata);
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) return;
            }
        }
    }

    private void processTitlStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titl")) {
                    metadata.setTitle( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("subTitl")) {
                    metadata.setSubTitle( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("IDNo")) {
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( parseText(xmlr), metadata.getStudyVersion().getStudy() );
                    } else {
                        StudyOtherId sid = new StudyOtherId();
                        sid.setAgency( xmlr.getAttributeValue(null, "agency")) ;
                        sid.setOtherId( parseText(xmlr) );
                        sid.setMetadata(metadata);
                        metadata.getStudyOtherIds().add(sid);
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) return;
            }
        }
    }

    private void processRspStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("AuthEnty")) {
                    StudyAuthor author = new StudyAuthor();
                    author.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    author.setName( parseText(xmlr) );
                    author.setMetadata(metadata);
                    metadata.getStudyAuthors().add(author);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("rspStmt")) return;
            }
        }
    }

    private void processProdStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("producer")) {
                    StudyProducer prod = new StudyProducer();
                    metadata.getStudyProducers().add(prod);
                    prod.setMetadata(metadata);
                    prod.setAbbreviation(xmlr.getAttributeValue(null, "abbr") );
                    prod.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    Map<String,String> prodDetails = parseCompoundText(xmlr, "producer");
                    prod.setName( prodDetails.get("name") );
                    prod.setUrl(  prodDetails.get("url") );
                    prod.setLogo(  prodDetails.get("logo") );
                } else if (xmlr.getLocalName().equals("prodDate")) {
                    metadata.setProductionDate(parseDate(xmlr,"prodDate"));
                } else if (xmlr.getLocalName().equals("prodPlac")) {
                    metadata.setProductionPlace( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("software")) {
                    StudySoftware ss = new StudySoftware();
                    metadata.getStudySoftware().add(ss);
                    ss.setMetadata(metadata);
                    ss.setSoftwareVersion( xmlr.getAttributeValue(null, "version") );
                    ss.setName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("fundAg")) {
                    metadata.setFundingAgency( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("grantNo")) {
                    StudyGrant sg = new StudyGrant();
                    metadata.getStudyGrants().add(sg);
                    sg.setMetadata(metadata);
                    sg.setAgency( xmlr.getAttributeValue(null, "agency") );
                    sg.setNumber( parseText(xmlr) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("prodStmt")) return;
            }
        }
    }

    private void processDistStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("distrbtr")) {
                    StudyDistributor dist = new StudyDistributor();
                    metadata.getStudyDistributors().add(dist);
                    dist.setMetadata(metadata);
                    dist.setAbbreviation(xmlr.getAttributeValue(null, "abbr") );
                    dist.setAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    Map<String,String> distDetails = parseCompoundText(xmlr, "distrbtr");
                    dist.setName( distDetails.get("name") );
                    dist.setUrl(  distDetails.get("url") );
                    dist.setLogo(  distDetails.get("logo") );
                } else if (xmlr.getLocalName().equals("contact")) {
                    metadata.setDistributorContactEmail( xmlr.getAttributeValue(null, "email") );
                    metadata.setDistributorContactAffiliation( xmlr.getAttributeValue(null, "affiliation") );
                    metadata.setDistributorContact( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("depositr")) {
                    Map<String,String> depDetails = parseCompoundText(xmlr, "depositr");
                    metadata.setDepositor( depDetails.get("name") );
                } else if (xmlr.getLocalName().equals("depDate")) {
                    metadata.setDateOfDeposit( parseDate(xmlr,"depDate") );
                } else if (xmlr.getLocalName().equals("distDate")) {
                    metadata.setDistributionDate( parseDate(xmlr,"distDate") );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("distStmt")) return;
            }
        }
    }


    private void processSerStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("serName")) {
                    metadata.setSeriesName( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("serInfo")) {
                    metadata.setSeriesInformation( parseText(xmlr) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("serStmt")) return;
            }
        }
    }

    private void processVerStmt(XMLStreamReader xmlr, StudyVersion sv) throws XMLStreamException {
        Metadata metadata = sv.getMetadata();
        if (!"DVN".equals(xmlr.getAttributeValue(null, "source"))) {
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("version")) {
                        metadata.setVersionDate( xmlr.getAttributeValue(null, "date") );
                        metadata.setStudyVersionText( parseText(xmlr) );
                    } else if (xmlr.getLocalName().equals("notes")) { processNotes(xmlr, metadata); }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if (xmlr.getLocalName().equals("verStmt")) return;
                }
            }
        } else {
            // this is the DVN version info; get version number for StudyVersion object
            for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
                 if (event == XMLStreamConstants.START_ELEMENT) {
                    if (xmlr.getLocalName().equals("version")) {
                        String elementText = getElementText(xmlr);
                        System.out.println("Reading DVN version: "+elementText);
                        sv.setVersionNumber(Long.parseLong(elementText));
                     }
                } else if(event == XMLStreamConstants.END_ELEMENT) {
                    if (xmlr.getLocalName().equals("verStmt")) return;
                }
            }
        }
    }

     private void processStdyInfo(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("subject")) processSubject(xmlr, metadata);
                else if (xmlr.getLocalName().equals("abstract")) {
                    StudyAbstract abs = new StudyAbstract();
                    metadata.getStudyAbstracts().add(abs);
                    abs.setMetadata(metadata);
                    abs.setDate( xmlr.getAttributeValue(null, "date") );
                    abs.setText( parseText(xmlr, "abstract") );
                } else if (xmlr.getLocalName().equals("sumDscr")) processSumDscr(xmlr, metadata);
                else if (xmlr.getLocalName().equals("notes")) processNotes(xmlr, metadata);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("stdyInfo")) return;
            }
        }
    }

     private void processSubject(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("keyword")) {
                    StudyKeyword kw = new StudyKeyword();
                    metadata.getStudyKeywords().add(kw);
                    kw.setMetadata(metadata);
                    kw.setVocab( xmlr.getAttributeValue(null, "vocab") );
                    kw.setVocabURI( xmlr.getAttributeValue(null, "vocabURI") );
                    kw.setValue( parseText(xmlr));
                } else if (xmlr.getLocalName().equals("topcClas")) {
                    StudyTopicClass tc = new StudyTopicClass();
                    metadata.getStudyTopicClasses().add(tc);
                    tc.setMetadata(metadata);
                    tc.setVocab( xmlr.getAttributeValue(null, "vocab") );
                    tc.setVocabURI( xmlr.getAttributeValue(null, "vocabURI") );
                    tc.setValue( parseText(xmlr));
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("subject")) return;
            }
        }
    }

     private void processSumDscr(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("timePrd")) {
                    String eventAttr = xmlr.getAttributeValue(null, "event");
                    if ( eventAttr == null || EVENT_SINGLE.equalsIgnoreCase(eventAttr) || EVENT_START.equalsIgnoreCase(eventAttr) ) {
                        metadata.setTimePeriodCoveredStart( parseDate(xmlr, "timePrd") );
                    } else if ( EVENT_END.equals(eventAttr) ) {
                        metadata.setTimePeriodCoveredEnd( parseDate(xmlr, "timePrd") );
                    }
                } else if (xmlr.getLocalName().equals("collDate")) {
                    String eventAttr = xmlr.getAttributeValue(null, "event");
                    if ( eventAttr == null || EVENT_SINGLE.equalsIgnoreCase(eventAttr) || EVENT_START.equalsIgnoreCase(eventAttr) ) {
                        metadata.setDateOfCollectionStart( parseDate(xmlr, "collDate") );
                    } else if ( EVENT_END.equals(eventAttr) ) {
                        metadata.setDateOfCollectionEnd( parseDate(xmlr, "collDate") );
                    }
                } else if (xmlr.getLocalName().equals("nation")) {
                    if (StringUtil.isEmpty( metadata.getCountry() ) ) {
                        metadata.setCountry( parseText(xmlr) );
                    } else {
                        metadata.setCountry( metadata.getCountry() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geogCover")) {
                    if (StringUtil.isEmpty( metadata.getGeographicCoverage() ) ) {
                        metadata.setGeographicCoverage( parseText(xmlr) );
                    } else {
                        metadata.setGeographicCoverage( metadata.getGeographicCoverage() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geogUnit")) {
                    if (StringUtil.isEmpty( metadata.getGeographicUnit() ) ) {
                        metadata.setGeographicUnit( parseText(xmlr) );
                    } else {
                        metadata.setGeographicUnit( metadata.getGeographicUnit() + "; " + parseText(xmlr) );
                    }
                } else if (xmlr.getLocalName().equals("geoBndBox")) {
                    processGeoBndBox(xmlr,metadata);
                } else if (xmlr.getLocalName().equals("anlyUnit")) {
                    if (StringUtil.isEmpty( metadata.getUnitOfAnalysis() ) ) {
                        metadata.setUnitOfAnalysis( parseText(xmlr,"anlyUnit") );
                    } else {
                        metadata.setUnitOfAnalysis( metadata.getUnitOfAnalysis() + "; " + parseText(xmlr,"anlyUnit") );
                    }
                } else if (xmlr.getLocalName().equals("universe")) {
                    if (StringUtil.isEmpty( metadata.getUniverse() ) ) {
                        metadata.setUniverse( parseText(xmlr,"universe") );
                    } else {
                        metadata.setUniverse( metadata.getUniverse() + "; " + parseText(xmlr,"universe") );
                    }
                } else if (xmlr.getLocalName().equals("dataKind")) {
                    if (StringUtil.isEmpty( metadata.getKindOfData() ) ) {
                        metadata.setKindOfData( parseText(xmlr) );
                    } else {
                        metadata.setKindOfData( metadata.getKindOfData() + "; " + parseText(xmlr) );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("sumDscr")) return;
            }
        }
    }

    private void processGeoBndBox(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        StudyGeoBounding geoBound = new StudyGeoBounding();
        metadata.getStudyGeoBoundings().add(geoBound);
        geoBound.setMetadata(metadata);

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

    private void processMethod(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataColl")) {
                    processDataColl(xmlr, metadata);
                } else if (xmlr.getLocalName().equals("notes")) {
                    // As of 3.0, this note is going to be used for the extended
                    // metadata fields. For now, we are not going to try to 
                    // process these in any meaningful way.
                    // We also don't want them to become the "Study-Level Error
                    // notes" -- that's what the note was being used for 
                    // exclusively in pre-3.0 practice. 
                    // TODO: this needs to be revisited after 3.0, when we 
                    // figure out how DVNs will be harvesting each others'
                    // extended metadata.
                    
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (!NOTE_TYPE_EXTENDED_METADATA.equalsIgnoreCase(noteType) ) {
                        if (StringUtil.isEmpty( metadata.getStudyLevelErrorNotes() ) ) {
                            metadata.setStudyLevelErrorNotes( parseText( xmlr,"notes" ) );
                        } else {
                            metadata.setStudyLevelErrorNotes( metadata.getStudyLevelErrorNotes() + "; " + parseText( xmlr, "notes" ) );
                        }
                    }
                } else if (xmlr.getLocalName().equals("anlyInfo")) {
                    processAnlyInfo(xmlr, metadata);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("method")) return;
            }
        }
    }

    private void processDataColl(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("timeMeth")) {
                    metadata.setTimeMethod( parseText( xmlr, "timeMeth" ) );
                } else if (xmlr.getLocalName().equals("dataCollector")) {
                    metadata.setDataCollector( parseText( xmlr, "dataCollector" ) );
                } else if (xmlr.getLocalName().equals("frequenc")) {
                    metadata.setFrequencyOfDataCollection( parseText( xmlr, "frequenc" ) );
                } else if (xmlr.getLocalName().equals("sampProc")) {
                    metadata.setSamplingProcedure( parseText( xmlr, "sampProc" ) );
                } else if (xmlr.getLocalName().equals("deviat")) {
                    metadata.setDeviationsFromSampleDesign( parseText( xmlr, "deviat" ) );
                } else if (xmlr.getLocalName().equals("collMode")) {
                    metadata.setCollectionMode( parseText( xmlr, "collMode" ) );
                } else if (xmlr.getLocalName().equals("resInstru")) {
                    metadata.setResearchInstrument( parseText( xmlr, "resInstru" ) );
                } else if (xmlr.getLocalName().equals("sources")) {
                    processSources(xmlr,metadata);
                } else if (xmlr.getLocalName().equals("collSitu")) {
                    metadata.setDataCollectionSituation( parseText( xmlr, "collSitu" ) );
                } else if (xmlr.getLocalName().equals("actMin")) {
                    metadata.setActionsToMinimizeLoss( parseText( xmlr, "actMin" ) );
                } else if (xmlr.getLocalName().equals("ConOps")) {
                    metadata.setControlOperations( parseText( xmlr, "ConOps" ) );
                } else if (xmlr.getLocalName().equals("weight")) {
                    metadata.setWeighting( parseText( xmlr, "weight" ) );
                } else if (xmlr.getLocalName().equals("cleanOps")) {
                    metadata.setCleaningOperations( parseText( xmlr, "cleanOps" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataColl")) return;
            }
        }
    }

    private void processSources(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataSrc")) {
                    metadata.setDataSources( parseText( xmlr, "dataSrc" ) );;
                } else if (xmlr.getLocalName().equals("srcOrig")) {
                    metadata.setOriginOfSources( parseText( xmlr, "srcOrig" ) );
                } else if (xmlr.getLocalName().equals("srcChar")) {
                    metadata.setCharacteristicOfSources( parseText( xmlr, "srcChar" ) );
                } else if (xmlr.getLocalName().equals("srcDocu")) {
                    metadata.setAccessToSources( parseText( xmlr, "srcDocu" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("sources")) return;
            }
        }
    }

    private void processAnlyInfo(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("respRate")) {
                    metadata.setResponseRate( parseText( xmlr, "respRate" ) );
                } else if (xmlr.getLocalName().equals("EstSmpErr")) {
                    metadata.setSamplingErrorEstimate( parseText( xmlr, "EstSmpErr" ) );
                } else if (xmlr.getLocalName().equals("dataAppr")) {
                    metadata.setOtherDataAppraisal( parseText( xmlr, "dataAppr" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("anlyInfo")) return;
            }
        }
    }

    private void processDataAccs(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("setAvail")) processSetAvail(xmlr,metadata);
                else if (xmlr.getLocalName().equals("useStmt")) processUseStmt(xmlr,metadata);
                else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (NOTE_TYPE_TERMS_OF_USE.equalsIgnoreCase(noteType) ) {
                        String noteLevel = xmlr.getAttributeValue(null, "level");
                        if (LEVEL_DV.equalsIgnoreCase(noteLevel) ) {
                            metadata.setHarvestDVTermsOfUse( parseText(xmlr) );
                        } else if (LEVEL_DVN.equalsIgnoreCase(noteLevel) )  {
                            metadata.setHarvestDVNTermsOfUse( parseText(xmlr) );
                        }
                    } else {
                        processNotes( xmlr, metadata );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataAccs")) return;
            }
        }
    }

    private void processSetAvail(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("accsPlac")) {
                    metadata.setPlaceOfAccess( parseText( xmlr, "accsPlac" ) );
                } else if (xmlr.getLocalName().equals("origArch")) {
                    metadata.setOriginalArchive( parseText( xmlr, "origArch" ) );
                } else if (xmlr.getLocalName().equals("avlStatus")) {
                    metadata.setAvailabilityStatus( parseText( xmlr, "avlStatus" ) );
                } else if (xmlr.getLocalName().equals("collSize")) {
                    metadata.setCollectionSize( parseText( xmlr, "collSize" ) );
                } else if (xmlr.getLocalName().equals("complete")) {
                    metadata.setStudyCompletion( parseText( xmlr, "complete" ) );
                } else if (xmlr.getLocalName().equals("notes")) {
                    processNotes( xmlr, metadata );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("setAvail")) return;
            }
        }
    }

    private void processUseStmt(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("confDec")) {
                    metadata.setConfidentialityDeclaration( parseText( xmlr, "confDec" ) );
                } else if (xmlr.getLocalName().equals("specPerm")) {
                    metadata.setSpecialPermissions( parseText( xmlr, "specPerm" ) );
                } else if (xmlr.getLocalName().equals("restrctn")) {
                    metadata.setRestrictions( parseText( xmlr, "restrctn" ) );
                } else if (xmlr.getLocalName().equals("contact")) {
                    metadata.setContact( parseText( xmlr, "contact" ) );
                } else if (xmlr.getLocalName().equals("citReq")) {
                    metadata.setCitationRequirements( parseText( xmlr, "citReq" ) );
                } else if (xmlr.getLocalName().equals("deposReq")) {
                    metadata.setDepositorRequirements( parseText( xmlr, "deposReq" ) );
                } else if (xmlr.getLocalName().equals("conditions")) {
                    metadata.setConditions( parseText( xmlr, "conditions" ) );
                } else if (xmlr.getLocalName().equals("disclaimer")) {
                    metadata.setDisclaimer( parseText( xmlr, "disclaimer" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("useStmt")) return;
            }
        }
    }

    private void processOthrStdyMat(XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        boolean replicationForFound = false;
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("relMat")) {
                    // this code is still here to handle imports from old DVN created ddis
                    if (!replicationForFound && REPLICATION_FOR_TYPE.equals( xmlr.getAttributeValue(null, "type") ) ) {
                        StudyRelPublication rp = new StudyRelPublication();
                        metadata.getStudyRelPublications().add(rp);
                        rp.setMetadata(metadata);
                        rp.setText( parseText( xmlr, "relMat" ) );
                        rp.setReplicationData(true);
                        replicationForFound = true;
                    } else {                    
                        StudyRelMaterial rm = new StudyRelMaterial();
                        metadata.getStudyRelMaterials().add(rm);
                        rm.setMetadata(metadata);
                        rm.setText( parseText( xmlr, "relMat" ) );
                    }
                } else if (xmlr.getLocalName().equals("relStdy")) {
                    StudyRelStudy rs = new StudyRelStudy();
                    metadata.getStudyRelStudies().add(rs);
                    rs.setMetadata(metadata);
                    rs.setText( parseText( xmlr, "relStdy" ) );
                } else if (xmlr.getLocalName().equals("relPubl")) {
                    StudyRelPublication rp = new StudyRelPublication();
                    metadata.getStudyRelPublications().add(rp);
                    rp.setMetadata(metadata);
                    
                    // call new parse text logic
                    Object rpFromDDI = parseTextNew( xmlr, "relPubl" );
                    if (rpFromDDI instanceof Map) {
                      Map rpMap = (Map) rpFromDDI;
                      rp.setText((String) rpMap.get("text"));
                      rp.setIdType((String) rpMap.get("idType"));
                      rp.setIdNumber((String) rpMap.get("idNumber"));
                      rp.setUrl((String) rpMap.get("url"));
                      if (!replicationForFound && rpMap.get("replicationData") != null) {
                        rp.setReplicationData(true);
                        replicationForFound = true;
                      }
                    } else {
                        rp.setText( (String) rpFromDDI );
                    }
                } else if (xmlr.getLocalName().equals("otherRefs")) {
                    StudyOtherRef or = new StudyOtherRef();
                    metadata.getStudyOtherRefs().add(or);
                    or.setMetadata(metadata);
                    or.setText( parseText( xmlr, "otherRefs" ) );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("othrStdyMat")) return;
            }
        }
    }
    
    
    private void processFileDscr(XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {
        FileMetadata fmd = new FileMetadata();
        fmd.setStudyVersion(studyVersion);
        studyVersion.getFileMetadatas().add(fmd);

        //StudyFile sf = new OtherFile(studyVersion.getStudy()); // until we connect the sf and dt, we have to assume it's an other file
        // as an experiment, I'm going to do it the other way around:
        // assume that every fileDscr is a subsettable file now, and convert them
        // to otherFiles later if no variables are referemming it -- L.A.


        TabularDataFile sf = new TabularDataFile(studyVersion.getStudy()); 
        DataTable dt = new DataTable();
        dt.setStudyFile(sf);
        sf.setDataTable(dt);

        fmd.setStudyFile(sf);

        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));
        String ddiFileId = xmlr.getAttributeValue(null, "ID");

        /// the following Strings are used to determine the category

        String catName = null;
        String icpsrDesc = null;
        String icpsrId = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) {
                    String tempDDIFileId = processFileTxt(xmlr, fmd, dt);
                    ddiFileId = ddiFileId != null ? ddiFileId : tempDDIFileId;
                }
                else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (NOTE_TYPE_UNF.equalsIgnoreCase(noteType) ) {
                        String unf = parseUNF( parseText(xmlr) );
                        sf.setUnf(unf);
                        dt.setUnf(unf);
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
                    if (fmd.getLabel() == null || fmd.getLabel().trim().equals("") ) {
                        fmd.setLabel("file");
                    }

                    fmd.setCategory(determineFileCategory(catName, icpsrDesc, icpsrId));


                    if (ddiFileId != null) {
                        List filesMapEntry = new ArrayList();
                        filesMapEntry.add(fmd);
                        filesMapEntry.add(dt);
                        filesMap.put( ddiFileId, filesMapEntry);
                    }

                    return;
                }
            }
        }
    }


    // Not used -- L.A.
    /*
    private void reProcessFileDscr(XMLStreamReader xmlr, StudyVersion studyVersion, Map filesMap) throws XMLStreamException {

        String ddiFileId = xmlr.getAttributeValue(null, "ID");
        String fileLabel = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) {
                    fileLabel = reProcessFileTxt(xmlr);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileDscr")) {

                    if (ddiFileId != null && fileLabel != null) {

                        for (FileMetadata fmd : studyVersion.getFileMetadatas()) {
                            String studyFileLabel = fmd.getLabel();

                            if (fileLabel.equals(studyFileLabel)) {

                                if ( fmd.getStudyFile() instanceof TabularDataFile) {
                                    TabularDataFile tbf = (TabularDataFile)fmd.getStudyFile();

                                    if (tbf != null) {
                                        filesMap.put(ddiFileId, tbf.getDataTable());
                                    }
                                }
                            }
                        }

                    }

                    return;
                }
            }
        }
    }*/

    private String processFileTxt(XMLStreamReader xmlr, FileMetadata fmd, DataTable dt) throws XMLStreamException {
        String ddiFileId = null;
        StudyFile sf = fmd.getStudyFile();

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileName")) {
                    ddiFileId = xmlr.getAttributeValue(null, "ID");
                    fmd.setLabel( parseText(xmlr) );
                    sf.setFileType( FileUtil.determineFileType( fmd.getLabel() ) );
                } else if (xmlr.getLocalName().equals("fileCont")) {
                    fmd.setDescription( parseText(xmlr) );
                }  else if (xmlr.getLocalName().equals("dimensns")) processDimensns(xmlr, dt);
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) return ddiFileId;
            }
        }
        return ddiFileId;
    }

    private String reProcessFileTxt(XMLStreamReader xmlr) throws XMLStreamException {
        String fileLabel = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileName")) {
                    fileLabel = parseText(xmlr);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) return fileLabel;
            }
        }
        return fileLabel;
    }


    private void processDimensns(XMLStreamReader xmlr, DataTable dt) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("caseQnty")) {
                    try {
                        dt.setCaseQuantity( new Long( parseText(xmlr) ) );
                    } catch (NumberFormatException ex) {}
                } else if (xmlr.getLocalName().equals("varQnty")) {
                    try{
                        dt.setVarQuantity( new Long( parseText(xmlr) ) );
                    } catch (NumberFormatException ex) {}
                } else if (xmlr.getLocalName().equals("recPrCas")) {
                    try {
                        dt.setRecordsPerCase( new Long( parseText(xmlr) ) );
                    } catch (NumberFormatException ex) {}
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("dimensns")) return;
            }
        }
    }

    // Not used -- L.A.
    /*
    private void processDataDscr(XMLStreamReader xmlr, Map filesMap) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) {
                    processVar(xmlr, filesMap);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataDscr")) return;
            }
        }
    }
     */

    private Map processDataDscrForReal(XMLStreamReader xmlr, Map filesMap) throws XMLStreamException {
        Map variableMap = new HashMap();
        //Map variableMapByTableId = new HashMap();

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) {
                    processVarForReal(xmlr, filesMap, variableMap);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataDscr")) {

                    for (Object fileId : filesMap.keySet()) {
                        List<DataVariable> variablesMapEntry = (List<DataVariable>) variableMap.get(fileId);
                        if (variablesMapEntry != null) {
                            // OK, this looks like we have found variables for this
                            // data file entry.
                        } else {
                            // TODO:
                            //  otherwise, the studyfile needs to be converted
                            //  from TabularFile to OtherFile; i.e., it should
                            //  be treated as non-subsettable, if there are
                            //  no variables in the <dataDscr> section of the
                            //  DDI referencing the file.
                            //  This actually happens in real life. For example,
                            //  Roper puts some of their files into the <fileDscr>
                            //  section, even though there's no <dataDscr>
                            //  provided for them.
                            //      -- L.A. 
                            //
                        }
                    }

                    return variableMap;
                }
            }
        }
        return null;
    }


    // this method is not used, as of now;
    // (this is an experiment in progress)
    // see processVarForReal()
    // -- L.A.

    /*
    private void processVar(XMLStreamReader xmlr, Map filesMap) throws XMLStreamException {
        DataVariable dv = new DataVariable();
        dv.setInvalidRanges(new ArrayList());
        dv.setSummaryStatistics( new ArrayList() );
        dv.setCategories( new ArrayList() );
        dv.setName( xmlr.getAttributeValue(null, "name") );

        // associate dv with the correct file

        // the attribute "files" in the <var> element below is legit;
        // it is never used in the DVN-produced DDIs (we use the <location>
        // element to link the variable to the datafile); it must have
        // been added to process DDIs produced by one of our partners (who?).
        // -- L.A.
        String fileId = xmlr.getAttributeValue(null, "files");
        if ( fileId != null ) {
            linkDataVariableToDatable(filesMap, xmlr.getAttributeValue(null, "fileid"), dv );
        }


        // interval type (DB value may be different than DDI value)
        String _interval = xmlr.getAttributeValue(null, "intrvl");

        _interval = (_interval == null ? VAR_INTERVAL_DISCRETE : _interval); // default is discrete
        _interval = VAR_INTERVAL_CONTIN.equals(_interval) ? DB_VAR_INTERVAL_TYPE_CONTINUOUS : _interval; // translate contin to DB value
        dv.setVariableIntervalType( varService.findVariableIntervalTypeByName(variableIntervalTypeList, _interval ));

        dv.setWeighted( VAR_WEIGHTED.equals( xmlr.getAttributeValue(null, "wgt") ) );
        // default is not-wgtd, so null sets weighted to false
 
        try {
            dv.setNumberOfDecimalPoints( new Long( xmlr.getAttributeValue(null, "dcml") ) );
        } catch (NumberFormatException nfe) {}

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("location")) {
                    processLocation(xmlr, dv, filesMap);
                }
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
 
                else if (xmlr.getLocalName().equals("varFormat")) {
                    processVarFormat( xmlr, dv );
                }
                
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
     */

    private void processVarForReal(XMLStreamReader xmlr, Map filesMap, Map variableMap) throws XMLStreamException {
        DataVariable dv = new DataVariable();
        dv.setInvalidRanges(new ArrayList());
        dv.setSummaryStatistics( new ArrayList() );
        dv.setCategories( new ArrayList() );
        dv.setName( xmlr.getAttributeValue(null, "name") );

        try {
            dv.setNumberOfDecimalPoints( new Long( xmlr.getAttributeValue(null, "dcml") ) );
        } catch (NumberFormatException nfe) {}

        // interval type (DB value may be different than DDI value)
        String _interval = xmlr.getAttributeValue(null, "intrvl");

        _interval = (_interval == null ? VAR_INTERVAL_DISCRETE : _interval); // default is discrete
        _interval = VAR_INTERVAL_CONTIN.equals(_interval) ? DB_VAR_INTERVAL_TYPE_CONTINUOUS : _interval; // translate contin to DB value
        dv.setVariableIntervalType( varService.findVariableIntervalTypeByName(variableIntervalTypeList, _interval ));

        dv.setWeighted( VAR_WEIGHTED.equals( xmlr.getAttributeValue(null, "wgt") ) );
        // default is not-wgtd, so null sets weighted to false


        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("location")) {
                    processLocationForReal(xmlr, dv, filesMap, variableMap);
                }
                else if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_VARIABLE );
                    if (_labl != null && !_labl.equals("") ) {
                        dv.setLabel( _labl );
                    }
                } else if (xmlr.getLocalName().equals("universe")) {
                    dv.setUniverse( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("concept")) {
                    dv.setConcept( parseText(xmlr) );
                } else if (xmlr.getLocalName().equals("invalrng")) {
                    processInvalrng( xmlr, dv );
                } else if (xmlr.getLocalName().equals("varFormat")) {
                    processVarFormat( xmlr, dv );
                } else if (xmlr.getLocalName().equals("sumStat")) {
                    processSumStat( xmlr, dv );
                } else if (xmlr.getLocalName().equals("catgry")) {
                    processCatgry( xmlr, dv );
                } else if (xmlr.getLocalName().equals("notes")) {
                    String _note = parseNoteByType( xmlr, NOTE_TYPE_UNF );
                    if (_note != null && !_note.equals("") ) {
                        dv.setUnf( parseUNF( _note ) );
                    }
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) return;
            }
        }
    }


    // not used; 
    // see processLocationForReal();
    /*
    private void processLocation(XMLStreamReader xmlr, DataVariable dv, Map filesMap) throws XMLStreamException {
        // associate dv with the correct file
        if ( dv.getDataTable() == null ) {
            linkDataVariableToDatable(filesMap, xmlr.getAttributeValue(null, "fileid"), dv );
        }

         if ( dv.getDataTable() == null ) {
            String fileId = xmlr.getAttributeValue(null, "fileid");
            if (fileId != null && !fileId.equals("")) {
                List filesMapEntry = (List) filesMap.get( fileId );
                if (filesMapEntry != null) {
                    FileMetadata fmd = (FileMetadata) filesMapEntry.get(0);
                    DataTable dt = (DataTable) filesMapEntry.get(1);
                    // set fileOrder to size of list (pre add, since indexes start at 0)
                    dv.setFileOrder( dt.getDataVariables().size() );

                    dv.setDataTable(dt);
                    dt.getDataVariables().add(dv);
                }
            }
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
     */

    private void processLocationForReal(XMLStreamReader xmlr, DataVariable dv, Map filesMap, Map variableMap) throws XMLStreamException {

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


        if ( dv.getDataTable() == null ) {
            String fileId = xmlr.getAttributeValue(null, "fileid");

            if (fileId != null && !fileId.equals("")) {

                //DataTable filesMapEntry = (DataTable) filesMap.get( fileId );

                List filesMapEntry = (List) filesMap.get( fileId );
                if (filesMapEntry != null) {
                    //FileMetadata fmd = (FileMetadata) filesMapEntry.get(0);
                    DataTable dt = (DataTable) filesMapEntry.get(1);

                    Long fileDbId = dt.getStudyFile().getId();

                    dv.setDataTable(dt);

                    if (variableMap.get(fileDbId) == null) {
                        variableMap.put(fileDbId, new ArrayList());
                    }

                    List<DataVariable> variablesMapEntry = (List<DataVariable>) variableMap.get(fileDbId);

                    // fileOrder storeds the physical position of the variable
                    // in the file. We are operating under the assumption that
                    // the order in which the <var> sections appear in the DDI
                    // reflects this physical order.
                    // Which is a little dangerous; technically, the order of
                    // elements is considered meaningless in XML, and
                    // transformations do NOT guarantee to preserve it.
                    //  -- L.A.
                    dv.setFileOrder( variablesMapEntry.size() );

                    variablesMapEntry.add(dv);
                }
            }
        }

    }


    // not used -- L.A.
    /*
    private void linkDataVariableToDatable(Map filesMap, String fileId, DataVariable dv) {
        List filesMapEntry = (List) filesMap.get( fileId );
        if (filesMapEntry != null) {
            FileMetadata fmd = (FileMetadata) filesMapEntry.get(0);
            DataTable dt = (DataTable) filesMapEntry.get(1);

     if ( fmd.getStudyFile() instanceof OtherFile) {
                // first time with this file, so attach the dt to the file and set as subsettable)
                TabularDataFile tdf = converOtherFileToTabularDataFile( fmd);

                // now add link to datatable
                dt.setStudyFile(tdf);
                tdf.setDataTable(dt);
                tdf.setFileType( FileUtil.determineTabularDataFileType( tdf ) ); // redetermine file type (suing dt), now that we know it's subsettable
            }
             

            // set fileOrder to size of list (pre add, since indexes start at 0)
            dv.setFileOrder( dt.getDataVariables().size() );

            dv.setDataTable(dt);
            dt.getDataVariables().add(dv);
        }
    }
    */

    // not used -- L.A.
    /*
    private TabularDataFile converOtherFileToTabularDataFile(FileMetadata fmd) {
        OtherFile of = (OtherFile) fmd.getStudyFile();
        TabularDataFile tdf = new TabularDataFile();

        tdf.setFileType( of.getFileType() );
        tdf.setFileSystemLocation( of.getFileSystemLocation() );
        tdf.setUnf( of.getUnf() );
        tdf.setOriginalFileType( of.getOriginalFileType() );
        tdf.setDisplayOrder( of.getDisplayOrder() );

        // reset links
        fmd.setStudyFile(tdf);

        Study study = of.getStudy();
        tdf.setStudy( study );
        study.getStudyFiles().remove(of);
        study.getStudyFiles().add(tdf);

        StudyFileActivity sfa = of.getStudyFileActivity();
        tdf.setStudyFileActivity(sfa);
        sfa.setStudyFile(tdf);

        return tdf;
    }
    */

    private void processInvalrng(XMLStreamReader xmlr, DataVariable dv) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("item")) {
                    VariableRange range = new VariableRange();
                    // commented out: -- L.A.
                    dv.getInvalidRanges().add(range);
                    range.setDataVariable(dv);

                    range.setBeginValue( xmlr.getAttributeValue(null, "VALUE") );
                    range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList, DB_VAR_RANGE_TYPE_POINT )  );
                } else if (xmlr.getLocalName().equals("range")) {
                    VariableRange range = new VariableRange();
                    // commented out: -- L.A.
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
        String type = xmlr.getAttributeValue(null, "type");
        type = (type == null ? VAR_FORMAT_TYPE_NUMERIC : type); // default is numeric

        String schema = xmlr.getAttributeValue(null, "schema");
        schema = (schema == null ? VAR_FORMAT_SCHEMA_ISO : schema); // default is ISO

        dv.setVariableFormatType( varService.findVariableFormatTypeByName( variableFormatTypeList, type ) );
        
        dv.setFormatSchema(schema);
        dv.setFormatSchemaName( xmlr.getAttributeValue(null, "formatname") );
        dv.setFormatCategory( xmlr.getAttributeValue(null, "category") );

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
        cat.setMissing( "Y".equals( xmlr.getAttributeValue(null, "missing") ) ); // default is N, so null sets missing to false
        cat.setDataVariable(dv);
        // commented out: -- L.A.
        dv.getCategories().add(cat);

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_CATEGORY );
                    if (_labl != null && !_labl.equals("") ) {
                        cat.setLabel( _labl );
                    }
                } else if (xmlr.getLocalName().equals("catValu")) {
                    cat.setValue( parseText(xmlr, false) );
                }
                else if (xmlr.getLocalName().equals("catStat")) {
                    String type = xmlr.getAttributeValue(null, "type");
                    if (type == null || CAT_STAT_TYPE_FREQUENCY.equalsIgnoreCase( type ) ) {
                        String _freq = parseText(xmlr);
                        if (_freq != null && !_freq.equals("") ) {
                            cat.setFrequency( new Double( _freq ) );
                        }
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

    private void processOtherMat(XMLStreamReader xmlr, StudyVersion studyVersion) throws XMLStreamException {
        FileMetadata fmd = new FileMetadata();
        fmd.setStudyVersion(studyVersion);
        studyVersion.getFileMetadatas().add(fmd);

        StudyFile sf = new OtherFile(studyVersion.getStudy());
        fmd.setStudyFile(sf);
        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));
        
        /// the following Strings are used to determine the category
        String catName = null;
        String icpsrDesc = null;
        String icpsrId = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("labl")) {
                    fmd.setLabel( parseText(xmlr) );
                    sf.setFileType( FileUtil.determineFileType( fmd.getLabel() ) );
                } else if (xmlr.getLocalName().equals("txt")) {
                    fmd.setDescription( parseText(xmlr) );
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
                    if (fmd.getLabel() == null || fmd.getLabel().trim().equals("") ) {
                        fmd.setLabel("file");
                    }

                    fmd.setCategory(determineFileCategory(catName, icpsrDesc, icpsrId));
                    return;
                }
            }
        }
    }

    private void processNotes (XMLStreamReader xmlr, Metadata metadata) throws XMLStreamException {
        StudyNote note = new StudyNote();
        metadata.getStudyNotes().add(note);
        note.setMetadata(metadata);
        note.setSubject( xmlr.getAttributeValue(null, "subject") );
        note.setType( xmlr.getAttributeValue(null, "type") );
        note.setText( parseText(xmlr, "notes") );
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
        return parseText(xmlr,true);
     }

     private String parseText(XMLStreamReader xmlr, boolean scrubText) throws XMLStreamException {
        String tempString = getElementText(xmlr);
        if (scrubText) {
            tempString = tempString.trim().replace('\n',' ');
        }
        return tempString;
     }

     private String parseText(XMLStreamReader xmlr, String endTag) throws XMLStreamException {
         return (String) parseTextNew(xmlr,endTag);
     }
     
     
     private Object parseTextNew(XMLStreamReader xmlr, String endTag) throws XMLStreamException {
        String returnString = "";
        Map returnMap = null;

        while (true) {
            if (!returnString.equals("")) { returnString += "\n";}
            int event = xmlr.next();
            if (event == XMLStreamConstants.CHARACTERS) {
                returnString += xmlr.getText().trim().replace('\n',' ');
           } else if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("p")) {
                    returnString += "<p>" + parseText(xmlr, "p") + "</p>";
                } else if (xmlr.getLocalName().equals("emph")) {
                    returnString += "<em>" + parseText(xmlr, "emph") + "</em>";
                } else if (xmlr.getLocalName().equals("hi")) {
                    returnString += "<strong>" + parseText(xmlr, "hi") + "</strong>";
                } else if (xmlr.getLocalName().equals("ExtLink")) {
                    String uri = xmlr.getAttributeValue(null, "URI");
                    String text = parseText(xmlr, "ExtLink").trim();
                    returnString += "<a href=\"" + uri + "\">" + ( StringUtil.isEmpty(text) ? uri : text) + "</a>";
                } else if (xmlr.getLocalName().equals("list")) {
                    returnString += parseText_list(xmlr);
                } else if (xmlr.getLocalName().equals("citation")) {
                    if (SOURCE_DVN_3_0.equals(xmlr.getAttributeValue(null, "source")) ) {
                        returnMap = parseDVNCitation(xmlr);
                }
                    returnString += parseText_citation(xmlr);
                } else {
                    throw new EJBException("ERROR occurred in mapDDI (parseText): tag not yet supported: <" + xmlr.getLocalName() + ">" );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals(endTag)) break;
            }
        }
        
        if (returnMap != null) {
            // this is one of our new citation areas for DVN3.0
            return returnMap;
        }
        
        // otherwise it's a standard section and just return the String like we always did
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
            // this includes the default list type of "simple"
            throw new EJBException("ERROR occurred in mapDDI (parseText): ListType of types other than {bulleted, ordered} not currently supported.");
        }

        while (true) {
            int event = xmlr.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("itm")) {
                    listString += "<li>" + parseText(xmlr,"itm") + "</li>\n";
                } else {
                    throw new EJBException("ERROR occurred in mapDDI (parseText): ListType does not currently supported contained LabelType.");
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
                    String uri = xmlr.getAttributeValue(null, "URI");
                    String holdingsText = parseText(xmlr);

                    if ( !StringUtil.isEmpty(uri) || !StringUtil.isEmpty(holdingsText)) {
                        holdings += addHoldings ? ", " : "";
                        addHoldings = true;

                        if ( StringUtil.isEmpty(uri) ) {
                            holdings += holdingsText;
                        } else if ( StringUtil.isEmpty(holdingsText) ) {
                            holdings += "<a href=\"" + uri + "\">" + uri + "</a>";
                        } else {
                            // both uri and text have values
                            holdings += "<a href=\"" + uri + "\">" + holdingsText + "</a>";
                        }
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
    
    private Map parseDVNCitation(XMLStreamReader xmlr) throws XMLStreamException {
        Map returnValues = new HashMap();
        
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
               if (xmlr.getLocalName().equals("IDNo")) {
                    returnValues.put("idType", xmlr.getAttributeValue(null, "agency") );
                    returnValues.put("idNumber", parseText(xmlr) );                   
               }
                else if (xmlr.getLocalName().equals("biblCit")) {
                    returnValues.put("text", parseText(xmlr) );                   
                }
                else if (xmlr.getLocalName().equals("holdings")) {
                    returnValues.put("url", xmlr.getAttributeValue(null, "URI") );                 
                }
                else if (xmlr.getLocalName().equals("notes")) {
                    if (NOTE_TYPE_REPLICATION_FOR.equals(xmlr.getAttributeValue(null, "type")) ) {
                        returnValues.put("replicationData", new Boolean(true));
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) break;
            }
        } 
        
        return returnValues;
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
                    String mapKey  = ("image".equalsIgnoreCase( xmlr.getAttributeValue(null, "role") ) || "logo".equalsIgnoreCase(xmlr.getAttributeValue(null, "title")))? "logo" : "url";
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

    /* We had to add this method because the ref getElementText has a bug where it
     * would append a null before the text, if there was an escaped apostrophe; it appears
     * that the code finds an null ENTITY_REFERENCE in this case which seems like a bug;
     * the workaround for the moment is to comment or handling ENTITY_REFERENCE in this case
     */
    private String getElementText(XMLStreamReader xmlr) throws XMLStreamException {
        if(xmlr.getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text", xmlr.getLocation());
        }
        int eventType = xmlr.next();
        StringBuffer content = new StringBuffer();
        while(eventType != XMLStreamConstants.END_ELEMENT ) {
            if(eventType == XMLStreamConstants.CHARACTERS
            || eventType == XMLStreamConstants.CDATA
            || eventType == XMLStreamConstants.SPACE
            /* || eventType == XMLStreamConstants.ENTITY_REFERENCE*/) {
                content.append(xmlr.getText());
            } else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT
                || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                // skipping
            } else if(eventType == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException("unexpected end of document when reading element text content");
            } else if(eventType == XMLStreamConstants.START_ELEMENT) {
                throw new XMLStreamException("element text content may not contain START_ELEMENT", xmlr.getLocation());
            } else {
                throw new XMLStreamException("Unexpected event type "+eventType, xmlr.getLocation());
            }
            eventType = xmlr.next();
        }
        return content.toString();
    }
    // </editor-fold>
}
