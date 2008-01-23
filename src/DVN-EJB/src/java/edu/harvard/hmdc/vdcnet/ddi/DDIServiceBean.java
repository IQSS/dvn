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
import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyFile;
import edu.harvard.hmdc.vdcnet.study.SummaryStatistic;
import edu.harvard.hmdc.vdcnet.study.VariableCategory;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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
 
}
