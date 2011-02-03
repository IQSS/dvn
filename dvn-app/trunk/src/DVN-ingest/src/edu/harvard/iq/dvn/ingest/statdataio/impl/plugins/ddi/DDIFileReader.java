/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2009
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.ddi;

import java.io.*;
import java.util.logging.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.security.NoSuchAlgorithmException;


import org.apache.commons.lang.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;


import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;
import java.text.*;


/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * DDI/XML Control Card format.
 * 
 * @author Leonid Andreev
 *

 */
public class DDIFileReader extends StatDataFileReader{


    // static fields:

    private static String[] FORMAT_NAMES = {"ddi"};
    private static String[] EXTENSIONS = {"xml", "ddi"};
    private static String[] MIME_TYPE = {"text/xml"};

    private static Logger dbgLog =
       Logger.getLogger(DDIFileReader.class.getPackage().getName());


    public static final String VAR_WEIGHTED = "wgtd";
    public static final String VAR_INTERVAL_CONTIN = "contin";
    public static final String VAR_INTERVAL_DISCRETE = "discrete";
    public static final String CAT_STAT_TYPE_FREQUENCY = "freq";
    public static final String VAR_FORMAT_TYPE_NUMERIC = "numeric";
    public static final String VAR_FORMAT_TYPE_CHARACTER = "character";
    public static final String VAR_FORMAT_SCHEMA_ISO = "ISO";

    public static final String LEVEL_VARIABLE = "variable";
    public static final String LEVEL_CATEGORY = "category";


    private static String unfVersionNumber = "5";

    // global variables:

    private List<String> variableNameList = new ArrayList<String>();
    private Map<String, Integer> unfVariableTypes = new HashMap<String, Integer>();
    private List<Integer> variableTypeList= new ArrayList<Integer>();
    private Set<Integer> decimalVariableSet = new HashSet<Integer>();
    private List<Integer> printFormatList = new ArrayList<Integer>();
    private Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>();
    private Map<String, String> formatCategoryTable = new HashMap<String, String>();
    private Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();
    private Map<String, Map<String, String>> valueLabelTable =
                new LinkedHashMap<String, Map<String, String>>();
    private Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>();
    private Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();

    private Integer caseQnty = 0;
    private Integer varQnty = 0;
    private char delimiterChar;

    private XMLInputFactory xmlInputFactory = null;



    SDIOMetadata smd = new DDIMetadata();
    DataTable tabData = null;
    SDIOData sdiodata = null;

    NumberFormat doubleNumberFormatter = new DecimalFormat();


    /**
     * Constructs a <code>DDIFileReader</code> instance with a
     * <code>StatDataFileReaderSpi</code> object.
     *
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */

    public DDIFileReader(StatDataFileReaderSpi originator) {
        super(originator);
        init();
    }

    
    private void init(){
        doubleNumberFormatter.setGroupingUsed(false);
        doubleNumberFormatter.setMaximumFractionDigits(340);
        xmlInputFactory = javax.xml.stream.XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);
    }
    
    void setCaseQnty (int cQ) {
        caseQnty = cQ; 
    }

    void setVarQnty (int vQ) {
        varQnty = vQ;
    }

    void setDelimiterChar (char c) {
        delimiterChar = c; 
    }

    int getCaseQnty () {
        return caseQnty;
    }

    int getVarQnty () {
        return varQnty;
    }

    char getDelimiterChar () {
        return delimiterChar;
    }


    // Methods ---------------------------------------------------------------//
    /*
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if a reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream cardStream, File rawDataFile) throws IOException {

        dbgLog.fine("***** DDIFileReader: read() start *****");
	    

        smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
        smd.getFileInformation().put("fileFormat", FORMAT_NAMES[0]);
        smd.getFileInformation().put("varFormat_schema", "SPSS");

        setDelimiterChar('\t');

        // parse the DDI and populate the data set metadata (smd) object:

        try {
            processDDI (cardStream, smd);
        } catch (IOException ex) {
            dbgLog.info("Failed to parse the supplied DDI file. Exception caught: "+ex.getMessage());
            throw ex;
        }


        // Now read the data file:

        CSVFileReader  csvFileReader = new CSVFileReader (getDelimiterChar());
        BufferedReader csvRd = new BufferedReader(new InputStreamReader(new FileInputStream(rawDataFile)));
        PrintWriter pwout = createOutputWriter();

        int casesRead = 0;

        try {
            casesRead = csvFileReader.read(csvRd, smd, pwout);
        } catch (Exception ex) {
            dbgLog.info ("Could not read and store TAB data file. Empty or corrupted file?" +
                        "Exception caught:" + ex.getMessage());
            throw new IOException ("Could not read and store TAB data file. Empty or corrupted file?" +
                        "Exception caught:" + ex.getMessage());
        }

        if (casesRead < 1) {

            dbgLog.info ("Could not read and store TAB data file: " +
                        "Empty or corrupted file?");
            throw new IOException ("Could not read and store TAB data file: " +
                        "Empty or corrupted file?");
        }

        if (getCaseQnty() > 0) {
             if (getCaseQnty() != casesRead) {
                dbgLog.info ("Could not read and store TAB data file: " +
                            "Number of cases read doesn't match the number " +
                            "specified in the DDI.");
                throw new IOException ("Could not read and store TAB data file: " +
                            "Number of cases read doesn't match the number " +
                            "specified in the DDI.");
             }
        } else {
            setCaseQnty(casesRead);
            smd.getFileInformation().put("caseQnty", getCaseQnty());
            dbgLog.fine("Number of cases not specified in the card; using the number " +
                        "of cases read from the TAB file: "+getCaseQnty());

        }

        dbgLog.info("Produced tab data file: "+(String)smd.getFileInformation().get("tabDelimitedDataFileLocation"));

        tabData = readTabDataFile ();

        // Calculate the datasets statistics, summary and category, and 
        // the UNF signatures:

        calculateDatasetStats(tabData);

        // Create and return the SDIOData object:
        sdiodata = new SDIOData(smd, tabData);

        dbgLog.info("***** DDIFileReader: read() end *****");


        return sdiodata;
    }

    // isValid method attempts to parse and process the supplied DDI
    // the same exact way as the read() method above, without processing
    // the raw data. Thus it can be used for quick, on the fly validation
    // of the control card as it gets uploaded by the user.

    public boolean isValid(File ddiFile) throws IOException {

        dbgLog.fine("***** DDIFileReader: validate() start *****");

        boolean fileIsValid = true;


        // try parsing the DDI;
        // if it's not fully to our liking, this will throw an exception.

        SDIOMetadata newSmd = new DDIMetadata();

        try {
            BufferedInputStream ddiStream = new BufferedInputStream (new FileInputStream(ddiFile));
            processDDI (ddiStream, newSmd);
        } catch (IOException ex) {
            dbgLog.info("Failed to read or parse the supplied DDI file: "+ex.getMessage());
            throw ex;
        }


        dbgLog.info("***** DDIFileReader: validate() end *****");


        return fileIsValid;
    }

    
    private void processDDI(BufferedInputStream ddiStream, SDIOMetadata smd) throws IOException {
        XMLStreamReader xmlr = null;
        try {
            xmlr =  xmlInputFactory.createXMLStreamReader(ddiStream);
            //processDDI( xmlr, smd );
            xmlr.nextTag();
            xmlr.require(XMLStreamConstants.START_ELEMENT, null, "codeBook");
            processCodeBook(xmlr, smd);
            dbgLog.info("processed DDI.");

        } catch (XMLStreamException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            try {
                if (xmlr != null) { 
                    xmlr.close();
                }
            } catch (XMLStreamException ex) {
                // The message in the exception should contain diagnostics
                // information -- what was wrong with the DDI, etc.
                throw new IOException (ex.getMessage());
            }
            if (ddiStream != null) { 
                ddiStream.close();
            }
        }

        // Having processed the entire ddi, we should have obtained all the metadata
        // describing the data set.
        // Configure the SMD metadata object:

        if (getVarQnty() > 0) {
            smd.getFileInformation().put("varQnty", getVarQnty());
            dbgLog.info("var quantity: "+getVarQnty());
            // TODO:
            // Validate the value against the actual number of variable sections
            // found in the DDI.
        } else {
            throw new IOException ("Failed to obtain the variable quantity from the DDI supplied.");
        }


        if (getCaseQnty() > 0) {
            smd.getFileInformation().put("caseQnty", getCaseQnty());
        }
        // It's ok if caseQnty was not defined in the DDI, we'll try to read
        // the tab file supplied and assume that the number of lines is the
        // number of observations.

        smd.setVariableName(variableNameList.toArray(new String[variableNameList.size()]));

        // "minimal" variable types: SPSS type binary definition:
        // 0 means numeric, >0 means string.

        smd.setVariableTypeMinimal(ArrayUtils.toPrimitive(
        variableTypeList.toArray(new Integer[variableTypeList.size()])));

        // This is how the "discrete" and "continuous" numeric values are
        // distinguished in the data set metadata:

        smd.setDecimalVariables(decimalVariableSet);

        //TODO: smd.getFileInformation().put("caseWeightVariableName", caseWeightVariableName);

        smd.setVariableFormat(printFormatList);
        smd.setVariableFormatName(printFormatNameTable);
        smd.setVariableFormatCategory(formatCategoryTable); //TODO: verify

        // Store the variable labels, if supplied:

        if (!variableLabelMap.isEmpty()) {
            smd.setVariableLabel(variableLabelMap);
        }

        // Value labels, if supplied:

        if (!valueLabelTable.isEmpty()) {
            smd.setValueLabelTable(valueLabelTable);
            smd.setValueLabelMappingTable(valueVariableMappingTable);
        }

        // And missing values:

        if (!missingValueTable.isEmpty()) {
            smd.setMissingValueTable(missingValueTable);
        }

    }

    //private void processDDI( XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {
    //    xmlr.nextTag();
    //    xmlr.require(XMLStreamConstants.START_ELEMENT, null, "codeBook");
    //    processCodeBook(xmlr, smd);
    //    dbgLog.info("processed DDI.");
    //}

    private void processCodeBook( XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileDscr")) {
                    processFileDscr(xmlr, smd);
                } else if (xmlr.getLocalName().equals("dataDscr")) {
                    processDataDscr(xmlr, smd);
                } else {
                    throw new XMLStreamException ("Unsupported DDI Element:"+xmlr.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("codeBook")) {
                    dbgLog.info("processed codeBook section;");
                    return;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </codeBook> expected, found "+xmlr.getLocalName());
                }
            }
        }



    }


    private void processFileDscr(XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) {
                    processFileTxt(xmlr, smd);
                } else if (xmlr.getLocalName().equals("notes")) {
                    // ignore, at least for now.
                    // (the only notes in our fileDscr sections are those with
                    // the UNFs and original file information, i.e., things we
                    // supply during ingest -- so there's no reason for us to
                    // be interested in what's in them; we may, however want to
                    // treat is as an error when any notes are encountered
                    // during TAB+DDI ingest. -- TBD)
                } else  {
                    throw new XMLStreamException ("Unsupported DDI Element: codeBook/fileDscr/"+xmlr.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("fileDscr")) {
                    return;
                } else if (xmlr.getLocalName().equals("notes")) {
                    // continue;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </fileDscr> expected, found "+xmlr.getLocalName());
                }
            }
        }
    }

    private void processFileTxt(XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("dimensns")) {
                    processDimensns(xmlr, smd);
                } else if (xmlr.getLocalName().equals("fileType")) {
                    // ignore.
                } else {
                    throw new XMLStreamException ("Unsupported DDI Element: codeBook/fileDscr/fileTxt/"+xmlr.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("fileTxt")) {
                    return;
                } else if (xmlr.getLocalName().equals("fileType")) {
                    // continue;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </fileTxt> expected, found "+xmlr.getLocalName());
                }
            }
        }
    }


    private void processDimensns(XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("caseQnty")) {
                    try {
                        setCaseQnty(new Integer( parseText(xmlr) ));
                    } catch (NumberFormatException ex) {
                        throw new XMLStreamException ("Invalid case quantity value in codeBook/fileDscr/fileTxt/dimensns: "+parseText(xmlr));
                    }
                } else if (xmlr.getLocalName().equals("varQnty")) {
                    try{
                        setVarQnty( new Integer( parseText(xmlr) ) );
                    } catch (NumberFormatException ex) {
                        throw new XMLStreamException ("Invalid variable quantity value in codeBook/fileDscr/fileTxt/dimensns: "+parseText(xmlr));
                    }
                } else if (xmlr.getLocalName().equals("recPrCas")) {
                    throw new XMLStreamException ("recPrCas (Records per Case) not supported for this type of data ingest, yet");
                } else {
                    throw new XMLStreamException ("Unsupported DDI Element: codeBook/fileDscr/fileTxt/dimensns/"+xmlr.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {// </codeBook>
                if (xmlr.getLocalName().equals("dimensns")) {
                    return;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </dimensns> expected, found "+xmlr.getLocalName());
                }
            }
        }
    }

    private void processLocation (XMLStreamReader xmlr, SDIOMetadata smd) {
        // For now we are ingesting these TAB file + DDI card pairs one at a time;
        // So the location entries can be simply ignored.

        return;
    }

    private String processLabl(XMLStreamReader xmlr, String level) throws XMLStreamException {
        String levelAttributeValue = xmlr.getAttributeValue(null, "level");
        if ( level.equalsIgnoreCase(levelAttributeValue) ) {
            return parseText(xmlr);
        }
        throw new XMLStreamException ("Invalid label, level \""+level+"\" expected, \""+levelAttributeValue+"\" found.");
    }



    private void processDataDscr(XMLStreamReader xmlr, SDIOMetadata smd) throws XMLStreamException {

        int variableCounter = 0;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) {
                    processVar(xmlr, smd, variableCounter);
                    variableCounter++;
                } else {
                    throw new XMLStreamException ("Unsupported element in the DDI: codeBook/dataDscr/"+xmlr.getLocalName());
                }

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("dataDscr")) {
                    dbgLog.info("processed dataDscr section");
                    return;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </dataDscr> expected, found "+xmlr.getLocalName());
                }
            }
        }
    }

    private void processVar(XMLStreamReader xmlr, SDIOMetadata smd, int variableNumber) throws XMLStreamException {



        // Attributes:

        // ID -- can be ignored (will be reassigned);

        // name:

        String variableName = xmlr.getAttributeValue(null, "name");
        if ( variableName == null || variableName.equals("")) {
            throw new XMLStreamException ("NULL or empty variable name attribute.");
        }

        variableNameList.add(variableName);

        // interval type:
        String varIntervalType = xmlr.getAttributeValue(null, "intrvl");

        // OK if not specified; defaults to discrete:
        varIntervalType = (varIntervalType == null ? VAR_INTERVAL_DISCRETE : varIntervalType);

        // Number of decimal points:

        String dcmlAttr = xmlr.getAttributeValue(null, "dcml");
        Long dcmlPoints = null;

        if (dcmlAttr != null && !dcmlAttr.equals("")) {
            try {
                dcmlPoints = new Long( dcmlAttr );
            } catch (NumberFormatException nfe) {
                throw new XMLStreamException ("Invalid variable dcml attribute: "+dcmlAttr);
            }
        }

        // weighed variables not supported yet -- L.A.
        //dv.setWeighted( VAR_WEIGHTED.equals( xmlr.getAttributeValue(null, "wgt") ) );
        // default is not-wgtd, so null sets weighted to false


        Map <String, String> valueLabelPairs = new LinkedHashMap<String, String>();
        List<String> missingValues = new ArrayList<String>();

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("location")) {
                    processLocation(xmlr, smd);
                }
                else if (xmlr.getLocalName().equals("labl")) {
                    String _labl = processLabl( xmlr, LEVEL_VARIABLE );
                    if (_labl != null && !_labl.equals("") ) {
                        variableLabelMap.put(variableName, _labl);
                    }
                } else if (xmlr.getLocalName().equals("invalrng")) {
                    processInvalrng( xmlr, smd, variableName );
                } else if (xmlr.getLocalName().equals("varFormat")) {
                    int simpleVariableFormat = processVarFormat( xmlr, smd, variableName );
                    variableTypeList.add(simpleVariableFormat);

                } else if (xmlr.getLocalName().equals("catgry")) {
                    processCatgry( xmlr, valueLabelPairs, missingValues, variableName );

                } else if (xmlr.getLocalName().equals("universe")) {
                    // Should not occur in TAB+DDI ingest (?)
                    // ignore.
                } else if (xmlr.getLocalName().equals("concept")) {
                    // Same deal.
                } else if (xmlr.getLocalName().equals("notes")) {
                    // Same.
                } else if (xmlr.getLocalName().equals("sumStat")) {
                    throw new XMLStreamException ("sumStat (Summary Statistics) section found in a variable section; not supported!");
                }


                // the "todos" below are from DDIServiceBean -- L.A.
                // todo: qstnTxt: wait to handle until we know more of how we will use it
                // todo: wgt-var : waitng to see example

            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("var")) {
                    // Before returning, set rich variable type metadata:
                    if (variableTypeList.get(variableNumber) == 0) {
                        // this is a numeric variable;
                        // It could be discrete or continuous, with the number
                        // of decimal points explicitely specified.
                        unfVariableTypes.put(variableName, 0); // default
                        printFormatList.add(5);

                        formatCategoryTable.put(variableName, "other");

                        if ((dcmlPoints != null && dcmlPoints > new Long(0)) || (!varIntervalType.equals(VAR_INTERVAL_DISCRETE))) {
                            unfVariableTypes.put(variableName, 1);
                            decimalVariableSet.add(variableNumber);
                            if (dcmlPoints == null || dcmlPoints == new Long(0)) {
                                dcmlPoints = new Long (7);
                            }
                            printFormatNameTable.put(variableName, "F8."+dcmlPoints);
                        } else {
                            unfVariableTypes.put(variableName, 0);

                        }

                    } else {
                        // this is a string variable.
                        unfVariableTypes.put(variableName, -1);
                        printFormatList.add(1);
                        formatCategoryTable.put(variableName, "other");

                        // TODO: special case for dates.
                    }

                    // Value Labels:

                    if ( !valueLabelPairs.isEmpty() ) {
                        valueLabelTable.put(variableName,valueLabelPairs);
                        valueVariableMappingTable.put(variableName, variableName);
                    }

                    // Missing Values:

                    if (!missingValues.isEmpty()) {
                        missingValueTable.put(variableName, missingValues);
                    }

                    dbgLog.info("processed variable number "+variableNumber);

                    return;
                }
            }
        }
    }

    private void processInvalrng(XMLStreamReader xmlr, SDIOMetadata smd, String variableName) throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("item")) {
                    // Ranges -- not supported? (TODO)
                    //VariableRange range = new VariableRange();
                    //dv.getInvalidRanges().add(range);
                    //range.setDataVariable(dv);
                    //range.setBeginValue( xmlr.getAttributeValue(null, "VALUE") );
                    //range.setBeginValueType(varService.findVariableRangeTypeByName( variableRangeTypeList, DB_VAR_RANGE_TYPE_POINT )  );

                    String invalidValue = xmlr.getAttributeValue(null, "VALUE");
                    // STORE  (?)
                    // TODO:
                    // Figure out what to do with these.
                    // Aren't the same values specified as "MISSING" in the
                    // categories? What's the difference? -- otherwise this
                    // is kind of redundant.

                //} else if (xmlr.getLocalName().equals("range")) {
                // ... }
                } else {
                    throw new XMLStreamException ("Unsupported DDI Element: codeBook/dataDscr/var/invalrng/"+xmlr.getLocalName());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("invalrng")) {
                    return;
                } else if (xmlr.getLocalName().equals("item")) {
                    // continue;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </invalrng> expected, found "+xmlr.getLocalName());
                }
            }
        }
    }

    private int processVarFormat(XMLStreamReader xmlr, SDIOMetadata smd, String variableName) throws XMLStreamException {
        String type = xmlr.getAttributeValue(null, "type");

        String formatCategory = xmlr.getAttributeValue(null, "category"); 
        String formatSchema = xmlr.getAttributeValue(null, "schema");
        String formatName = xmlr.getAttributeValue(null, "formatname");
        //schema = (schema == null ? VAR_FORMAT_SCHEMA_ISO : schema); // default is ISO

        // STORE type and schema, if not null. (TODO NOW)

        //dv.setVariableFormatType( varService.findVariableFormatTypeByName( variableFormatTypeList, type ) );
        //dv.setFormatSchema(schema);
        //dv.setFormatSchemaName( xmlr.getAttributeValue(null, "formatname") );
        //dv.setFormatCategory( xmlr.getAttributeValue(null, "category") );

        if (type == null || type.equals("")) {
            throw new XMLStreamException ("no varFormat type supplied for variable "+variableName);
        }

        if (type.equals(VAR_FORMAT_TYPE_NUMERIC)) {
            return 0;
        }

        if (type.equals(VAR_FORMAT_TYPE_CHARACTER)) {
            return 1;
        }

        throw new XMLStreamException ("unknown or unsupported varFormat type supplied for variable "+variableName);

    }

    private void processCatgry(XMLStreamReader xmlr, Map <String, String> valueLabelPairs, List<String> missingValues, String variableName) throws XMLStreamException {

        boolean isMissing = "Y".equals( xmlr.getAttributeValue(null, "missing"));
        // (default is N, so null sets missing to false)

        // STORE -- (TODO NOW)
        //cat.setDataVariable(dv);
        //dv.getCategories().add(cat);
        String varValue = null;
        String valueLabel = null;

        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("labl")) {
                    valueLabel = processLabl( xmlr, LEVEL_CATEGORY );
                } else if (xmlr.getLocalName().equals("catValu")) {
                    varValue = parseText(xmlr, false);
                }
                else if (xmlr.getLocalName().equals("catStat")) {
                    // category statistics should not be present in the
                    // TAB file + DDI card ingest:
                    throw new XMLStreamException ("catStat (Category Statistics) section found in a variable section; not supported!");
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("catgry")) {

                    if (varValue != null && !varValue.equals("")) {
                        if (valueLabel != null && !valueLabel.equals("") ) {
                            dbgLog.fine("DDI Reader: storing label "+valueLabel+" for value "+varValue);
                            valueLabelPairs.put(varValue, valueLabel );
                        }
                        if (isMissing) {
                            missingValues.add(varValue);
                        } 
                    }

                    return;
                } else if (xmlr.getLocalName().equals("catValu")) {
                    // continue;
                } else if (xmlr.getLocalName().equals("labl")) {
                    // continue;
                } else {
                    throw new XMLStreamException ("Mismatched DDI Formatting: </catgry> expected, found "+xmlr.getLocalName());
                }
            }
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

    /*
     * (The comment and code below are taken from the DDIServiceBean implementation -- L.A.)
     * We had to add this method because the ref getElementText has a bug where it
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


    // method for creating the output writer for the temporary tab file.
    // this shouldn't be in the plugins really.

    PrintWriter createOutputWriter () throws IOException {
        PrintWriter pwout = null;
        FileOutputStream fileOutTab = null;
	        
        try {

            // create a File object to save the tab-delimited data file
            File tabDelimitedDataFile = File.createTempFile("tempTabfile.", ".tab");

            String tabDelimitedDataFileName   = tabDelimitedDataFile.getAbsolutePath();

            // save the temp file name in the metadata object
            smd.getFileInformation().put("tabDelimitedDataFileLocation", tabDelimitedDataFileName);

            fileOutTab = new FileOutputStream(tabDelimitedDataFile);
            
            pwout = new PrintWriter(new OutputStreamWriter(fileOutTab, "utf8"), true);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex){
            //ex.printStackTrace();
	    throw ex; 
        }

        return pwout;

    }

    private void storeTabFileData (DataTable csvData, PrintWriter pwout) {
        String[] caseRow = new String[getVarQnty()];

        for (int i=0; i<getCaseQnty(); i++) {
            for (int j=0; j<getVarQnty(); j++) {
                caseRow[j] = (String)csvData.getData()[j][i];
            }

            pwout.println(StringUtils.join(caseRow, "\t"));
        }
        pwout.close(); 
    }

    private DataTable readTabDataFile () throws IOException {
        DataTable tabData = new DataTable();
        Object[][] dataTable = null;
        dataTable = new Object[getVarQnty()][getCaseQnty()];

        String tabFileName = (String)smd.getFileInformation().get("tabDelimitedDataFileLocation");
        BufferedReader tabFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(tabFileName)));

        boolean[] isCharacterVariable = smd.isStringVariable();

        String line;
        String[] valueTokens = new String[getVarQnty()];


        for ( int j = 0; j < getCaseQnty(); j++ ) {
            if ((line = tabFileReader.readLine()) == null) {
                throw new IOException("Failed to read "+getCaseQnty()+" lines " +
                        "from tabular data file "+tabFileName);
            }
            valueTokens = line.split("\t", getVarQnty());

            for ( int i = 0; i < getVarQnty(); i++ ) {
                if (isCharacterVariable[i]) {
                    valueTokens[i] = valueTokens[i].replaceFirst("^\"", "");
                    valueTokens[i] = valueTokens[i].replaceFirst("\"$", "");
                    dataTable[i][j] = valueTokens[i];
                } else {
                    dataTable[i][j] = valueTokens[i];
                }
            }
        }

        tabFileReader.close();

        tabData.setData(dataTable);
        return tabData;
    }
    // Method for calculating the UNF signatures.
    //
    // It really isn't awesome that each of our file format readers has 
    // its own UNF calculation! It should be format-independent; the 
    // method should be defined in just one place, and, preferably, it should
    // run on the TAB file and the data set metadata from the database. 
    // I.e., it should be reproducible outside of the ingest. 
    // 
    // TODO: bring this up, soon.
    //
    // (the 2 methods below are more or less cut-and-pasted as is from Akio's
    // SPSS/SAV file reader).
    
    private String getUNF(Object[] varData, String[] dateFormats, int variableType,
        String unfVersionNumber, int variablePosition)
        throws NumberFormatException, UnfException,
        IOException, NoSuchAlgorithmException {
        String unfValue = null;


        dbgLog.fine("variableType="+variableType);
        dbgLog.finer("unfVersionNumber="+unfVersionNumber);
        dbgLog.fine("variablePosition="+variablePosition);
        //dbgLog.fine("variableName="+variableNameList.get(variablePosition));

        switch(variableType){
            case 0:
                // Integer (Long):

                dbgLog.fine("Integer case");

                // Convert array of Strings to array of Longs
                Long[] ldata = new Long[varData.length];
                for (int i = 0; i < varData.length; i++) {
                    //if (varData[i] != null) {
                    try {
                        ldata[i] = new Long((String) varData[i]);
                    } catch (Exception ex) {
                        ldata[i] = null;
                    }
                    //}
                }
                unfValue = UNF5Util.calculateUNF(ldata);
                dbgLog.finer("integer:unfValue=" + unfValue);

                //dbgLog.finer("sumstat:long case=" + Arrays.deepToString(
                //        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata))));

                smd.getSummaryStatisticsTable().put(variablePosition,
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata)));


                Map<String, Integer> catStat = StatHelper.calculateCategoryStatistics(ldata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;

            case 1:
                // Double:

                dbgLog.finer("double case");

                 // Convert array of Strings to array of Doubles
                Double[]  ddata = new Double[varData.length];
                for (int i=0;i<varData.length;i++) {
                    //if (varData[i]!=null) {
                    try {
                        ddata[i] = new Double((String)varData[i]);
                    } catch (Exception ex) {
                        ddata[i] = null;
                    }
                    //}
                }
                unfValue = UNF5Util.calculateUNF(ddata);
                dbgLog.finer("double:unfValue="+unfValue);
                smd.getSummaryStatisticsTable().put(variablePosition,
                    ArrayUtils.toObject(StatHelper.calculateSummaryStatisticsContDistSample(ddata)));

                break;
            case -1:
                // String:
                //
                // i.e., this is something *stored* as string; it may still be
                // a more complex data type than just a string of characters.
                // Namely, it can be some date or time type that we support.
                // These should be handled differently when calculating the
                // UNFs.

                dbgLog.finer("string case");


                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);
                dbgLog.finer("string array passed to calculateUNF: "+Arrays.deepToString(strdata));

                if (dateFormats != null) {
                    unfValue = UNF5Util.calculateUNF(strdata, dateFormats);
                } else {
                    unfValue = UNF5Util.calculateUNF(strdata);
                }
                dbgLog.finer("string:unfValue="+unfValue);

                smd.getSummaryStatisticsTable().put(variablePosition,
                    StatHelper.calculateSummaryStatistics(strdata));

                Map<String, Integer> StrCatStat = StatHelper.calculateCategoryStatistics(strdata);
                //out.println("catStat="+StrCatStat);

                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), StrCatStat);

                break;
            default:
                dbgLog.fine("unknown variable type found");
                String errorMessage =
                    "unknow variable Type found at varData section";
                    throw new IllegalArgumentException(errorMessage);

        } // switch

        dbgLog.fine("unfvalue(last)="+unfValue);
        return unfValue;
    }

    // This method calculates the summary and category statistics, as well
    // as the UNF signatures for the variables and the dataset as a whole.

    private void calculateDatasetStats (DataTable csvData) {
        String fileUNFvalue = null;

        String[] unfValues = new String[getVarQnty()];


         // TODO:
         // Catch and differentiate between different exception
         // that the UNF methods throw.

        for (int j=0; j<getVarQnty(); j++){
            int variableTypeNumer = unfVariableTypes.get(variableNameList.get(j));
            String varFormat = smd.getVariableFormatName().get(smd.getVariableName()[j]);

            try {
                dbgLog.finer("j = "+j);

                // Before we pass the variable vector to the UNF calculator,
                // we need to check if is of any supported date/time type.
                // If so, we'll also need to create and pass a list of
                // date formats, so that the UNFs could be properly calculated.
                // (otherwise the date/time values will be treated simply as
                // strings!)


                if ( varFormat != null
                    && ( varFormat.equals("WKDAY")
                        || varFormat.equals("MONTH")
                        || "date".equals(SPSSConstants.FORMAT_CATEGORY_TABLE.get(varFormat))
                        || "time".equals(SPSSConstants.FORMAT_CATEGORY_TABLE.get(varFormat)) )
                        ) {

                    // TODO:
                    // All these date, time, weekday, etc. values need to be validated!

                    String[] dateFormats = new String[getCaseQnty()];

                    /*for (int k = 0; k < getCaseQnty(); k++) {
                        if (SPSSConstants.FORMAT_CATEGORY_TABLE.get(varFormat).equals("date")) {
                            dbgLog.finer("date case");
                            dateFormats[k] = sdf_ymd.toPattern();
                        } else if (SPSSConstants.FORMAT_CATEGORY_TABLE.get(varFormat).equals("time")) {
                            dbgLog.finer("time case: DTIME or DATETIME or TIME");

                            if (varFormat.equals("DTIME")) {
                                dateFormats[k] = sdf_dhms.toPattern();
                            } else if (varFormat.equals("DATETIME")) {
                                dateFormats[k] = sdf_ymdhms.toPattern();
                            } else if (varFormat.equals("TIME")) {
                                dateFormats[k] = sdf_hms.toPattern();
                            }
                        } else if (varFormat.equals("WKDAY")) {
                            // TODO: these need to be validated only.
                            dateFormats = null;

                        } else if (varFormat.equals("MONTH")) {
                            // TODO: these need to be validated only.
                            dateFormats = null; 
                        }
                    }*/

                    unfValues[j] = getUNF(csvData.getData()[j], dateFormats, variableTypeNumer,
                        unfVersionNumber, j);
                } else {
                    unfValues[j] = getUNF(csvData.getData()[j], null, variableTypeNumer,
                        unfVersionNumber, j);
                }
                dbgLog.fine(j+"th unf value"+unfValues[j]);

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            } catch (UnfException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
                //throw ex;
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }


        dbgLog.fine("unf set:\n"+Arrays.deepToString(unfValues));

        try {
            fileUNFvalue = UNF5Util.calculateUNF(unfValues);

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
            //throw ex;
        }

        // Set the UNFs we have calculated, the ones for the individual
        // variables and the file-level UNF:

        csvData.setUnf(unfValues);
        csvData.setFileUnf(fileUNFvalue);

        smd.setVariableUNF(unfValues);
        smd.getFileInformation().put("fileUNF", fileUNFvalue);

        dbgLog.fine("file-level unf value:\n"+fileUNFvalue);

    }

    public static void main(String[] args) {
        BufferedInputStream ddiCardStream = null;
        SDIOData processedCard = null;
        DDIFileReader ddiReader = null;

        String ddiFile = args[0];
        String tabDataFile = args[1];


   
        try {
            ddiReader = new DDIFileReader (null);

            if (ddiReader == null) {
                System.out.println("failed to create DDI file reader.");
            }

            ddiCardStream = new BufferedInputStream(new FileInputStream(ddiFile));

            processedCard = ddiReader.read(ddiCardStream, new File(args[1]));
        } catch (IOException ex) {
            System.out.println("exception caught!");
         }



    }

}
