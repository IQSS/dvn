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
import edu.harvard.hmdc.vdcnet.util.StringUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void exportStudy(Study s, OutputStream os) throws IOException {
        int exportMode = 0;
        try {
            exportMode = Integer.parseInt( System.getProperty("dvn.test.export.mode") );
        } catch (Exception e) {}

        if (exportMode == 2) {
            exportStudyPlain(s, os);
        } else if (exportMode == 3) {
            exportStudySax(s, os);
        }else if (exportMode == 4) {
            exportStudyStax(s, os);
        }
        
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void exportStudyStax(Study s, OutputStream os) throws IOException {
        try {
            javax.xml.stream.XMLOutputFactory xmlof = javax.xml.stream.XMLOutputFactory.newInstance();
            //xmlof.setProperty("javax.xml.stream.isPrefixDefaulting", java.lang.Boolean.TRUE);
            javax.xml.stream.XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os);
            xmlw.writeStartDocument();
            //xmlw.writeProcessingInstruction("xml-stylesheet href=\'catalog.xsl\' type=\'text/xsl\'");
            
            xmlw.writeStartElement("codeBook");
            xmlw.writeDefaultNamespace("http://www.icpsr.umich.edu/DDI");
            
            xmlw.writeStartElement("dataDscr");

            Iterator iter = s.getStudyFiles().iterator();
            while (iter.hasNext()) {
                StudyFile sf = (StudyFile) iter.next();

                if ( sf.isSubsettable() ) {
                    if ( sf.getDataTable().getDataVariables().size() > 0 ) {
                        Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
                        while (varIter.hasNext()) {
                            DataVariable dv = (DataVariable) varIter.next();
                            exportDataVariable(dv, xmlw);
                        }
                    }
                }
            }           
            
            xmlw.writeEndElement(); //dataDscr
            xmlw.writeEndElement(); // codeBook
            xmlw.writeEndDocument();
            xmlw.close();
            
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        
    }

    private void exportDataVariable(DataVariable dv, XMLStreamWriter xmlw) throws XMLStreamException {
        xmlw.writeStartElement("var");
        xmlw.writeAttribute( "ID", dv.getId().toString() );
        xmlw.writeAttribute( "name", dv.getName() );
        
        xmlw.writeEmptyElement("location");

        xmlw.writeStartElement("labl");
        xmlw.writeAttribute( "level", "variable" );
        xmlw.writeCharacters( dv.getLabel() );
        xmlw.writeEndElement(); //labl
        
        //sum stats
        for (SummaryStatistic sumStat : dv.getSummaryStatistics()) {
            xmlw.writeStartElement("sumStat");
            xmlw.writeAttribute( "type", sumStat.getType().getName() );
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
            xmlw.writeStartElement("labl");
            xmlw.writeAttribute( "level", "category" );
            xmlw.writeCharacters( cat.getLabel() );
            xmlw.writeEndElement(); //labl
            
            // catStat
            if (cat.getFrequency() != null) {
                xmlw.writeStartElement("catStat");
                xmlw.writeAttribute( "type", "freq" );
                xmlw.writeCharacters( cat.getFrequency().toString() );
                xmlw.writeEndElement(); //catStat
        }
        
            xmlw.writeEndElement(); //catgry   
        }

        // varFormat
        xmlw.writeStartElement("varFormat");
        xmlw.writeEndElement(); //varFormat        

        // notes
        xmlw.writeStartElement("notes");
        xmlw.writeAttribute( "subject", "Universal Numeric Fingerprint" );
        xmlw.writeAttribute( "level", "variable" );
        xmlw.writeAttribute( "type", "VDC:UNF" );
        xmlw.writeCharacters( dv.getUnf() );
        xmlw.writeEndElement(); //notes        

        xmlw.writeEndElement(); //var      
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void exportStudySax(Study s, OutputStream os) throws IOException {
        try {
        Writer out = new OutputStreamWriter(os);
        StreamResult streamResult = new StreamResult(out);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        // SAX2.0 ContentHandler.
        TransformerHandler hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        //serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
        serializer.setOutputProperty(OutputKeys.INDENT,"yes");
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
        hd.setResult(streamResult);

        AttributesImpl atts = new AttributesImpl();

        hd.startDocument();

        atts.addAttribute("","","version","CDATA","2.0");
        atts.addAttribute("","","xsi:schemaLocation","CDATA","http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd");
        atts.addAttribute("","","xmlns:xsi","CDATA","http://www.w3.org/2001/XMLSchema-instance");
        atts.addAttribute("","","xmlns","CDATA","http://www.icpsr.umich.edu/DDI");
        hd.startElement("","","codeBook",atts);
        
        atts.clear();
        hd.startElement("","","dataDscr",atts);
        


        Iterator iter = s.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile sf = (StudyFile) iter.next();

            if ( sf.isSubsettable() ) {
                if ( sf.getDataTable().getDataVariables().size() > 0 ) {
                    Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
                    while (varIter.hasNext()) {
                        DataVariable dv = (DataVariable) varIter.next();
                        exportDataVariable(dv, hd);
                    }
                }
            }
        }

        hd.endElement("","","dataDscr");
        hd.endElement("","","codeBook");
        hd.endDocument();

        } catch (Exception e) {}
        
    }

    private void exportDataVariable(DataVariable dv, TransformerHandler hd) throws SAXException {
        AttributesImpl atts = new AttributesImpl();

        atts.addAttribute("","","ID","CDATA", dv.getId().toString() );
        atts.addAttribute("","","name","CDATA", dv.getName());
        hd.startElement("","","var",atts);

        // location
        atts.clear();
        hd.startElement("","","location",atts);
        hd.endElement("","","location");

        // label
        atts.addAttribute("","","level","CDATA", "variable");
        hd.startElement("","","labl",atts);

        hd.endElement("","","labl");

        // sumstats
        // categories

        // varFormat
        hd.startElement("","","varFormat",atts);
        hd.endElement("","","varFormat");

        // UNF
        hd.startElement("","","notes",atts);
        hd.endElement("","","notes");

        hd.endElement("","","var");

   }












    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void exportStudyPlain(Study s, OutputStream os) throws IOException {
        Writer out = new OutputStreamWriter(os);
        out.write("<codeBook version=\"2.0\" xsi:schemaLocation=\"http://www.icpsr.umich.edu/DDI http://www.icpsr.umich.edu/DDI/Version2-0.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.icpsr.umich.edu/DDI\">");
        out.write(System.getProperty("line.separator"));

        out.write("\t<dataDscr>");
        out.write(System.getProperty("line.separator"));

        Iterator iter = s.getStudyFiles().iterator();
        while (iter.hasNext()) {
            StudyFile sf = (StudyFile) iter.next();

            if ( sf.isSubsettable() ) {
                if ( sf.getDataTable().getDataVariables().size() > 0 ) {
                    Iterator varIter = varService.getDataVariablesByFileOrder( sf.getDataTable().getId() ).iterator();
                    while (varIter.hasNext()) {
                        DataVariable dv = (DataVariable) varIter.next();
                        exportDataVariable(dv, null, out);
                    }
                }
            }


        }

        out.write("\t</dataDscr>");
        out.write(System.getProperty("line.separator"));

        out.write("</codeBook>");
        out.flush();                   
    }

    
    private void exportDataVariable(DataVariable dv, FileDscrType _fd, Writer out) throws IOException {
        
        out.write("\t\t<var ");
        out.write("ID=\"V" + dv.getId().toString() +"\" ");
        out.write("name=\"" + dv.getName() +"\" ");
        //out.write("intrvl\"" + dv.getId() +"\" ");
        //out.write("wgt=\"" + dv.getId() +"\" ");
        out.write(">" + System.getProperty("line.separator"));
        
        //location
        out.write("\t\t\t<location ");
        //out.write("fileid=\"V" + dv.getId().toString() +"\" ");
        out.write("/>" + System.getProperty("line.separator"));

        // label
        out.write("\t\t\t<labl level=\"variable\">");
        out.write("/>" + parseForXML(dv.getLabel()) );
        out.write("</labl>" + System.getProperty("line.separator"));

        //sum stats
        for (SummaryStatistic sumStat : dv.getSummaryStatistics()) {
            out.write("\t\t\t<sumStat type=\"" +  sumStat.getType().getName() + "\">");
            out.write("/>" + sumStat.getValue() );
            out.write("</sumStat>" + System.getProperty("line.separator"));
        }

        // categories
        for (VariableCategory cat : dv.getCategories()) {
            out.write("\t\t\t<catgry>" + System.getProperty("line.separator"));

            // catValu
            out.write("\t\t\t\t<catValu>");
            out.write( cat.getValue() );
            out.write("</catValu>" + System.getProperty("line.separator"));

            // label
            out.write("\t\t\t\t<labl level=\"category\">");
            out.write("/>" + cat.getLabel() );
            out.write("</labl>" + System.getProperty("line.separator"));

            // catStat
            out.write("\t\t\t\t<catStat type=\"freq\">");
            out.write("/>" + cat.getFrequency() );
            out.write("</catStat>" + System.getProperty("line.separator"));

            out.write("\t\t\t</catgry>" + System.getProperty("line.separator"));
        }

        // varFormat
        out.write("\t\t\t<varFormat ");
        out.write("/>" + System.getProperty("line.separator"));

        // notes
        out.write("\t\t\t<notes subject=\"Universal Numeric Fingerprint\" level=\"variable\" type=\"VDC:UNF\">");
        out.write("/>" + dv.getUnf());
        out.write("</notes>" + System.getProperty("line.separator"));

        out.write("\t\t</var>" + System.getProperty("line.separator"));
        /*
        ObjectFactory objFactory = new ObjectFactory();
        VarType _dv = objFactory.createVarType();
        if (dv.getVariableIntervalType() != null) {
            String interval = dv.getVariableIntervalType().getName();
            interval = DB_VAR_INTERVAL_TYPE_CONTINUOUS.equals(interval) ? VAR_INTERVAL_CONTIN : interval;
            _dv.setIntrvl( interval );
        }
        
        LocationType _loc = objFactory.createLocationType();
        _loc.setFileid( _fd );
        _loc.setRecSegNo( dv.getRecordSegmentNumber() != null ? dv.getRecordSegmentNumber().toString() : null );
        _loc.setStartPos( dv.getFileStartPosition() != null ? dv.getFileStartPosition().toString() : null );
        _loc.setEndPos( dv.getFileEndPosition() != null ? dv.getFileEndPosition().toString() : null );
        _dv.getLocation().add( _loc );
        
        if (!StringUtil.isEmpty( dv.getLabel() )) {
            LablType _labl = objFactory.createLablType();
            _labl.setLevel(LEVEL_VARIABLE);
            _labl.getContent().add(dv.getLabel());
            _dv.getLabl().add(_labl);
        }
        
        // summary stats
        Iterator iter = dv.getSummaryStatistics().iterator();
        while (iter.hasNext()) {
            SummaryStatistic ss = (SummaryStatistic) iter.next();
            SumStatType _ss = objFactory.createSumStatType();
            _ss.setType(ss.getType().getName());
            _ss.getContent().add(ss.getValue());
            _dv.getSumStat().add(_ss);
            
        }
        
        // category
        iter = dv.getCategories().iterator();
        while (iter.hasNext()) {
            VariableCategory cat = (VariableCategory) iter.next();
            CatgryType _cat = objFactory.createCatgryType();
            _cat.setMissing( cat.isMissing() ? "Y" : "N" );
            
            // catValu
            CatValuType _catValu = objFactory.createCatValuType();
            _catValu.getContent().add(cat.getValue());
            _cat.setCatValu(_catValu);
            
            // label
            if (!StringUtil.isEmpty( cat.getLabel() )) {
                LablType _catLabl = objFactory.createLablType();
                _catLabl.setLevel(LEVEL_CATEGORY);
                _catLabl.getContent().add(cat.getLabel());
                _cat.getLabl().add(_catLabl);
            }
            
            // catStat: freq
            if (cat.getFrequency() != null) {
                CatStatType _catStat = objFactory.createCatStatType();
                _catStat.setType("freq");
                _catStat.getContent().add(cat.getFrequency().toString());
                _cat.getCatStat().add(_catStat);
            }
            
            _dv.getCatgry().add(_cat);
            
        }
        
        // invalid ranges
        iter = dv.getInvalidRanges().iterator();
        InvalrngType _invalidRange = objFactory.createInvalrngType();
        
        while (iter.hasNext()) {
            VariableRange range = (VariableRange) iter.next();
            if (range.getBeginValueType() != null && range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_POINT)) {
                // create item
                if (range.getBeginValue() != null ) {
                    ItemType _item = objFactory.createItemType();
                    _item.setVALUE( range.getBeginValue() );
                    _invalidRange.getItemOrRange().add( _item );
                }
                
            } else {
                //create range
                RangeType _range = objFactory.createRangeType();
                _invalidRange.getItemOrRange().add( _range );
                
                if ( range.getBeginValueType() != null && range.getBeginValue() != null ) {
                    if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN) ) {
                        _range.setMin( range.getBeginValue() );
                    } else if ( range.getBeginValueType().getName().equals(DB_VAR_RANGE_TYPE_MIN_EX) ) {
                        _range.setMinExclusive( range.getBeginValue() );
                    }
                }
                
                if ( range.getEndValueType() != null && range.getEndValue() != null) {
                    if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX) ) {
                        _range.setMax( range.getEndValue() );
                    } else if ( range.getEndValueType().getName().equals(DB_VAR_RANGE_TYPE_MAX_EX) ) {
                        _range.setMaxExclusive( range.getEndValue() );
                    }
                    
                }
            }
        }
        
        if (_invalidRange.getItemOrRange().size() > 0) {
            _dv.getInvalrng().add(_invalidRange);
        }
        
        VarFormatType _format = objFactory.createVarFormatType();
        _format.setType(dv.getVariableFormatType().getName());
        _format.setSchema( dv.getFormatSchema() );
        _format.setFormatname( dv.getFormatSchemaName() );
        _dv.setVarFormat(_format);
        
        //concept
        if (!StringUtil.isEmpty( dv.getConcept() )) {
            ConceptType _concept = objFactory.createConceptType();
            _concept.getContent().add( dv.getConcept() );
            _dv.getConcept().add( _concept );
        }
        //universe
        if (!StringUtil.isEmpty( dv.getUniverse() )) {
            UniverseType _universe = objFactory.createUniverseType();
            _universe.getContent().add( dv.getUniverse() );
            _dv.getUniverse().add( _universe );
        }
        
        // notes
        NotesType _unf = objFactory.createNotesType();
        _unf.setLevel(LEVEL_VARIABLE);
        _unf.setType(NOTE_TYPE_UNF);
        _unf.setSubject(NOTE_SUBJECT_UNF);
        _unf.getContent().add(dv.getUnf());
        _dv.getNotes().add(_unf);
        
        return _dv;
        */
    }

    private String createElement(String element, List attributes, int numTabs, boolean isEndElement) {
        String returnString = "";

        for (int i=0; i < numTabs; i ++) {
            returnString += "\t";
        }
        returnString += "<" + element;
        
        // attributes
        //  + "labl level=\"category\">";
        returnString += (isEndElement ? "/" : "") + ">";

        return returnString;
    }

    private String parseForXML(String s) {
        if (s != null) {
            s = s.replaceAll("&", "&amp;");
        }
        
        return s;
    }
    
    
    //**********************
    // IMPORT METHODS
    //**********************
    
    public void mapDDI( File ddiFile, Study study) {
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
        
        Map filesMap = new HashMap();
        
        
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
            throw new EJBException("ERROR occurred in mapping!!!!");
        }
    }

    private void processDocDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("IDNo") && StringUtil.isEmpty(study.getStudyId()) ) {
                    // this will set a StudyId if it has not yet been set; it will get overridden by a study
                    // id in the StudyDscr section, if one exists
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( xmlr.getElementText(), study );
                    }
                } else if ( xmlr.getLocalName().equals("holdings") && StringUtil.isEmpty(study.getHarvestHoldings()) ) {
                    processCitationInDocDscr(xmlr, study);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("docDscr")) return;
            }   
        }
    }

    private void processCitationInDocDscr (XMLStreamReader xmlr, Study study) throws XMLStreamException {
        while (true) {
            int event = xmlr.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ( xmlr.getLocalName().equals("holdings") && StringUtil.isEmpty(study.getHarvestHoldings()) ) {
                    study.setHarvestHoldings( xmlr.getAttributeValue(null, "URI") );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) break;
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
                    study.setTitle( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("subTitl")) {
                    study.setSubTitle( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("IDNo")) {
                    if ( AGENCY_HANDLE.equals( xmlr.getAttributeValue(null, "agency") ) ) {
                        parseStudyId( xmlr.getElementText(), study );
                    } else {
                        StudyOtherId sid = new StudyOtherId();
                        sid.setAgency( xmlr.getAttributeValue(null, "agency")) ;
                        sid.setOtherId( xmlr.getElementText() );
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
                    author.setName( xmlr.getElementText() );
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
                    study.setProductionPlace( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("software")) {
                    StudySoftware ss = new StudySoftware();
                    study.getStudySoftware().add(ss);
                    ss.setStudy(study);
                    ss.setSoftwareVersion( xmlr.getAttributeValue(null, "version") );
                    ss.setName( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("fundAg")) {
                    study.setFundingAgency( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("grantNo")) {
                    StudyGrant sg = new StudyGrant();
                    study.getStudyGrants().add(sg);
                    sg.setStudy(study);
                    sg.setAgency( xmlr.getAttributeValue(null, "agency") );
                    sg.setNumber( xmlr.getElementText() );
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
                    study.setDistributorContact( xmlr.getElementText() );
                    study.setDistributorContactEmail( xmlr.getAttributeValue(null, "email") );
                    study.setDistributorContactAffiliation( xmlr.getAttributeValue(null, "affiliation") );
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
                    study.setSeriesName( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("serInfo")) {
                    study.setSeriesInformation( xmlr.getElementText() );
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
                    study.setStudyVersion( xmlr.getElementText() );
                    study.setVersionDate( xmlr.getAttributeValue(null, "date") );
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
                    kw.setValue( xmlr.getElementText());
                } else if (xmlr.getLocalName().equals("topcClass")) {
                    StudyTopicClass tc = new StudyTopicClass();
                    study.getStudyTopicClasses().add(tc);
                    tc.setStudy(study);
                    tc.setVocab( xmlr.getAttributeValue(null, "vocab") );
                    tc.setVocabURI( xmlr.getAttributeValue(null, "vocabURI") );
                    tc.setValue( xmlr.getElementText());
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
                        study.setCountry( xmlr.getElementText() );
                    } else {
                        study.setCountry( study.getCountry() + "; " + xmlr.getElementText() );
                    }
                } else if (xmlr.getLocalName().equals("geogCover")) {
                    if (StringUtil.isEmpty( study.getGeographicCoverage() ) ) {
                        study.setGeographicCoverage( xmlr.getElementText() );
                    } else {
                        study.setGeographicCoverage( study.getGeographicCoverage() + "; " + xmlr.getElementText() );
                    }
                } else if (xmlr.getLocalName().equals("geogUnit")) {
                    if (StringUtil.isEmpty( study.getGeographicUnit() ) ) {
                        study.setGeographicUnit( xmlr.getElementText() );
                    } else {
                        study.setGeographicUnit( study.getGeographicUnit() + "; " + xmlr.getElementText() );
                    }
                } else if (xmlr.getLocalName().equals("geoBndBox")) { 
                    processGeoBndBox(xmlr,study);
                } else if (xmlr.getLocalName().equals("anlyUnit*")) {
                    if (StringUtil.isEmpty( study.getUnitOfAnalysis() ) ) {
                        study.setUnitOfAnalysis( xmlr.getElementText() );
                    } else {
                        study.setUnitOfAnalysis( study.getUnitOfAnalysis() + "; " + xmlr.getElementText() );
                    }
                } else if (xmlr.getLocalName().equals("universe")) {
                    if (StringUtil.isEmpty( study.getUniverse() ) ) {
                        study.setUniverse( xmlr.getElementText() );
                    } else {
                        study.setUniverse( study.getUniverse() + "; " + xmlr.getElementText() );
                    }
                } else if (xmlr.getLocalName().equals("dataKind")) {
                    if (StringUtil.isEmpty( study.getKindOfData() ) ) {
                        study.setKindOfData( xmlr.getElementText() );
                    } else {
                        study.setKindOfData( study.getKindOfData() + "; " + xmlr.getElementText() );
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
                    geoBound.setWestLongitude( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("eastBL")) {
                    geoBound.setEastLongitude( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("southBL")) {
                    geoBound.setSouthLatitude( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("northBL")) {
                    geoBound.setNorthLatitude( xmlr.getElementText() );
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("method")) return;
            }   
        }
    }

    private void processMethod(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dataColl")) processDataColl(xmlr, study);
                else if (xmlr.getLocalName().equals("notes")) {
                    if (StringUtil.isEmpty( study.getStudyLevelErrorNotes() ) ) {
                        study.setStudyLevelErrorNotes( xmlr.getElementText() );
                    } else {
                        study.setStudyLevelErrorNotes( study.getStudyLevelErrorNotes() + "; " + xmlr.getElementText() );
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
        DataTable dt = new DataTable();
        dt.setDataVariables( new ArrayList() );
        dt.setStudyFile(sf);
        sf.setDataTable(dt);
        sf.setSubsettable(true);
        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));
        filesMap.put( xmlr.getAttributeValue(null, "ID"), sf);

        /// the following Strings are used to determine the category
        String catName = null;
        String icpsrDesc = null;
        String icpsrId = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) processFileTxt(xmlr, sf);
                else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (NOTE_TYPE_UNF.equalsIgnoreCase(noteType) ) {
                        dt.setUnf( parseUNF( xmlr.getElementText() ) );    
                    } else if ("vdc:category".equalsIgnoreCase(noteType) ) {
                        catName = xmlr.getElementText();
                    } else if ("icpsr:category".equalsIgnoreCase(noteType) ) {
                        String subjectType = xmlr.getAttributeValue(null, "subject");
                        if ("description".equalsIgnoreCase(subjectType)) {
                            icpsrDesc = xmlr.getElementText();
                        } else if ("id".equalsIgnoreCase(subjectType)) {
                            icpsrId = xmlr.getElementText();
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

    private void processFileTxt(XMLStreamReader xmlr, StudyFile sf) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileName")) {
                    sf.setFileName( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("fileCont")) {
                    sf.setDescription( xmlr.getElementText() );
                }  else if (xmlr.getLocalName().equals("dimensns")) processDimensns(xmlr, sf.getDataTable());
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) return;
            } 
        }
    }     

    private void processDimensns(XMLStreamReader xmlr, DataTable dt) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("caseQnty")) {
                    dt.setCaseQuantity( new Long( xmlr.getElementText() ) );
                } else if (xmlr.getLocalName().equals("varQnty")) {
                    dt.setVarQuantity( new Long( xmlr.getElementText() ) );
                } else if (xmlr.getLocalName().equals("recPrCas")) {
                    dt.setRecordsPerCase( new Long( xmlr.getElementText() ) );
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
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("dataDscr")) return;
            }   
        }
    }


 
    private void processVar(XMLStreamReader xmlr, Study study, Map filesMap, int fileOrder) throws XMLStreamException {
        DataVariable dv = new DataVariable();
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
                    dv.setUniverse( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("concept")) {
                    dv.setConcept( xmlr.getElementText() );
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
        StudyFile sf = (StudyFile) filesMap.get( xmlr.getAttributeValue(null, "fileid" ) );
        dv.setDataTable(sf.getDataTable());
        sf.getDataTable().getDataVariables().add(dv);
        
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
        VariableRange range = new VariableRange();
        dv.getInvalidRanges().add(range);
        range.setDataVariable(dv);

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("item")) {
                    range.setBeginValue( xmlr.getAttributeValue(null, "VALUE") );
                    range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList, DB_VAR_RANGE_TYPE_POINT )  );
                } else if (xmlr.getLocalName().equals("range")) {
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
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
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
        ss.setValue( xmlr.getElementText()) ;
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
                    cat.setValue( xmlr.getElementText() );
                }
                else if (xmlr.getLocalName().equals("catStat")) {
                    String _freq = processCatStat( xmlr, "freq" );
                    if (_freq != null && !_freq.equals("") ) {
                        cat.setFrequency( new Long( _freq ) );
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("catgry")) return;
            }   
        }
    }

    private String processLabl(XMLStreamReader xmlr, String level) throws XMLStreamException {
        if (level.equalsIgnoreCase( xmlr.getAttributeValue(null, "level") ) ) {
            return xmlr.getElementText();
        } else {
            return null;
        }
    }
    
    private String processCatStat(XMLStreamReader xmlr, String type) throws XMLStreamException {
        if (type.equalsIgnoreCase( xmlr.getAttributeValue(null, "type") ) ) {
            return xmlr.getElementText();
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
                    sf.setFileName( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("txt")) {
                    sf.setDescription( xmlr.getElementText() );
                } else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if ("vdc:category".equalsIgnoreCase(noteType) ) {
                        catName = xmlr.getElementText();
                    } else if ("icpsr:category".equalsIgnoreCase(noteType) ) {
                        String subjectType = xmlr.getAttributeValue(null, "subject");
                        if ("description".equalsIgnoreCase(subjectType)) {
                            icpsrDesc = xmlr.getElementText();
                        } else if ("id".equalsIgnoreCase(subjectType)) {
                            icpsrId = xmlr.getElementText();
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
        note.setText( xmlr.getElementText() );
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
            return xmlr.getElementText();
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

     private String parseText(XMLStreamReader xmlr, String endTag) throws XMLStreamException {
        String returnString = "";

        while (true) {
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
                    listString += "<li>" + xmlr.getElementText() + "</li>\n";
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
                                citation += xmlr.getElementText();
                            }
                        } else if (event == XMLStreamConstants.END_ELEMENT) {
                            if (xmlr.getLocalName().equals("titlStmt")) break;
                        }
                    }
                } else if (xmlr.getLocalName().equals("holdings")) {
                    citation += addHoldings ? ", " : "";
                    addHoldings = true;
                    
                    String uri = xmlr.getAttributeValue(null, "URI");
                    if ( StringUtil.isEmpty(uri) ) {
                        citation += xmlr.getElementText();
                    } else {
                        citation += "<a href=\"" + uri + "\">" + xmlr.getElementText() + "</a>";
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
                text += xmlr.getText();
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
            date = xmlr.getElementText();
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
}
