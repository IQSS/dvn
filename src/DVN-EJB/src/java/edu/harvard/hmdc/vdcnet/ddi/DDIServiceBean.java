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
import edu.harvard.hmdc.vdcnet.study.DataTable;
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.FileCategory;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyAuthor;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.StudyFileEditBean;
import edu.harvard.hmdc.vdcnet.study.StudyOtherId;
import edu.harvard.hmdc.vdcnet.study.SummaryStatistic;
import edu.harvard.hmdc.vdcnet.study.SummaryStatisticType;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;
import edu.harvard.hmdc.vdcnet.study.VariableFormatType;
import edu.harvard.hmdc.vdcnet.study.VariableIntervalType;
import edu.harvard.hmdc.vdcnet.study.VariableRangeType;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    private void processDocDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("x")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("y")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("z")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("docDscr")) return;
            }   
        }
    }
    
    private void processStdyDscr(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("citation")) processCitation(xmlr, study);
                else if (xmlr.getLocalName().equals("stdyInfo")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("method")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("dataAccs")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("othrStdyMat")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("notes")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("stdyDscr")) return;
            }   
        }
    }
 
     private void processCitation(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("titlStmt")) processTitlStmt(xmlr, study);
                else if (xmlr.getLocalName().equals("rspStmt")) processRspStmt(xmlr,study);
                else if (xmlr.getLocalName().equals("prodStmt")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("distStmt")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("serStmt")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("verStmt")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");  
                else if (xmlr.getLocalName().equals("holdings")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED"); 
                else if (xmlr.getLocalName().equals("notes")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
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
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("titlStmt")) return;
            }   
        }
    } 

 

    
    private void processRspStmt(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("AuthEnty")) {
                    StudyAuthor author = new StudyAuthor();
                    author.setAffiliation( xmlr.getAttributeValue(null, "affiliation")) ;
                    author.setName( xmlr.getElementText() );
                    author.setStudy(study);
                    study.getStudyAuthors().add(author);                    
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("rspStmt")) return;
            }   
        }
    }
    
    private void processFileDscr(XMLStreamReader xmlr, Study study, Map filesMap) throws XMLStreamException {
        String catName = "";
        StudyFile sf = new StudyFile();
        DataTable dt = new DataTable();
        dt.setDataVariables( new ArrayList() );
        dt.setStudyFile(sf);
        sf.setDataTable(dt);
        sf.setSubsettable(true);
        
        sf.setFileSystemLocation( xmlr.getAttributeValue(null, "URI"));
        
        filesMap.put( xmlr.getAttributeValue(null, "ID"), sf);
        
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) processFileTxt(xmlr, sf);
                else if (xmlr.getLocalName().equals("notes")) {
                    String noteType = xmlr.getAttributeValue(null, "type");
                    if (NOTE_TYPE_UNF.equalsIgnoreCase(noteType) ) {
                        dt.setUnf( parseUNF( xmlr.getElementText() ) );    
                    } else if ("VDC:CATEGORY".equalsIgnoreCase(noteType) ) {
                        catName = xmlr.getElementText();
                    }
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("fileDscr")) {
                    // post process
                    if (sf.getFileName() == null || sf.getFileName().trim().equals("") ) {
                        sf.setFileName("file");
                    } 
                    addFileToCategory(sf, catName, study);
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
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) processVar(xmlr, study, filesMap);
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("dataDscr")) return;
            }   
        }
    }


 
    private void processVar(XMLStreamReader xmlr, Study study, Map filesMap) throws XMLStreamException {
        DataVariable dv = new DataVariable();
        dv.setSummaryStatistics( new ArrayList() );
        dv.setCategories( new ArrayList() );
        dv.setName( xmlr.getAttributeValue(null, "name") );
       
        
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("location")) processLocation(xmlr, dv, filesMap);
                else if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_VARIABLE );
                    if (_labl != null && !_labl.equals("") ) {
                        dv.setLabel( _labl );
                    }
                } else if (xmlr.getLocalName().equals("varFormat")) processVarFormat( xmlr, dv );
                else if (xmlr.getLocalName().equals("sumStat")) processSumStat( xmlr, dv );
                else if (xmlr.getLocalName().equals("catgry")) processCatgry( xmlr, dv );
                else if (xmlr.getLocalName().equals("notes")) {
                    String _note = processNotes( xmlr, NOTE_TYPE_UNF );
                    if (_note != null && !_note.equals("") ) {
                        dv.setUnf( parseUNF( _note ) );
                    }
                }
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
    
    private String processNotes (XMLStreamReader xmlr, String type) throws XMLStreamException {
        if (type.equalsIgnoreCase( xmlr.getAttributeValue(null, "type") ) ) {
            return xmlr.getElementText();
        } else {
            return null;
        }
    }

    private void processOtherMat(XMLStreamReader xmlr, Study study) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("x")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("y")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
                else if (xmlr.getLocalName().equals("z")) System.out.println("DDI Mapper: NOT YET IMPLEMENTED");
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("otherMat")) return;
            }   
        }
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
    
    private String parseUNF(String unfString) {
        if (unfString.indexOf("UNF:") != -1) {
            return unfString.substring( unfString.indexOf("UNF:") );
        } else {
            return null;
        }
    }
    
    private void addFileToCategory(StudyFile sf, String catName, Study study) {
        StudyFileEditBean fileBean = new StudyFileEditBean(sf);
        fileBean.setFileCategoryName(catName);
        fileBean.addFileToCategory(study);
    }    
}
