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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.spss;

import java.io.*;
import java.util.logging.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.security.NoSuchAlgorithmException;


import org.apache.commons.lang.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.unf.*;


import edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util.*;
import java.text.*;


/**
 * A DVN-Project-implementation of <code>StatDataFileReader</code> for the 
 * SPSS Control Card format.
 * 
 * @author Leonid Andreev
 *
 * implemented based on Akio Sone's implementation of the SPSS/SAV format reader
 * and his older SPSS control card parser implementation in Perl.
 */
public class SPSSFileReader extends StatDataFileReader{


    // static fields:

    private static String[] FORMAT_NAMES = {"spss", "SPSS"};
    private static String[] EXTENSIONS = {"spss", "sps"};
    private static String[] MIME_TYPE = {"text/plain"};

    private static int DATA_LIST_DELIMITER_NOT_FOUND = 1;
    private static int DATA_LIST_NO_SLASH_SEPARATOR = 2;
    private static int DATA_LIST_UNSUPPORTED_VARIABLE_TYPE = 3;
    private static int DATA_LIST_ILLEGAL_NUMERIC_TYPE = 4;


    private static Logger dbgLog =
       Logger.getLogger(SPSSFileReader.class.getPackage().getName());

    private static final Map<String, String> formatCategoryTable = new HashMap<String, String>();
    private static final List<String> commandNames = new ArrayList<String>();
    
    private static String unfVersionNumber = "5";

    // global variables:

    private int caseQnty = 0;
    private int varQnty = 0;
    private char delimiterChar;


    private Map<String, String> commandStrings = new HashMap<String, String>();
    private List<String> variableNameList = new ArrayList<String>();
    private Map<String, Integer> unfVariableTypes = new HashMap<String, Integer>();

    SDIOMetadata smd = new SPSSMetadata();
    DataTable csvData = null;
    SDIOData sdiodata = null;

    NumberFormat doubleNumberFormatter = new DecimalFormat();


    /**
     * Constructs a <code>SPSSFileReader</code> instance with a
     * <code>StatDataFileReaderSpi</code> object.
     *
     * @param originator a <code>StatDataFileReaderSpi</code> object.
     */

    public SPSSFileReader(StatDataFileReaderSpi originator) {
        super(originator);
        init();
    }

    
    //String[] variableFormatTypeList= null;
    //Map<String, String> printFormatTable = new LinkedHashMap<String, String>();
    //Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>();



    static {
        commandNames.add("DataList");
        commandNames.add("VarLabel");
        commandNames.add("ValLabel");
        commandNames.add("MisValue");

        // also:
        // RECODE
        // FORMATS
        // (?)

    }
    
    private void init(){
        doubleNumberFormatter.setGroupingUsed(false);
        doubleNumberFormatter.setMaximumFractionDigits(340);

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
    /**
     * Read the given SPSS Control Card via a <code>BufferedInputStream</code>
     * object.  This method calls an appropriate method associated with the given 
     * field header by reflection.
     * 
     * @param stream a <code>BufferedInputStream</code>.
     * @return an <code>SDIOData</code> object
     * @throws java.io.IOException if a reading error occurs.
     */
    @Override
    public SDIOData read(BufferedInputStream cardStream, File rawDataFile) throws IOException {

        dbgLog.fine("***** SPSSFileReader: read() start *****");
	    

        // TODO:
        // catch specific exceptions, provide quality diagnostics

        getSPSScommandLines(cardStream);

        smd.getFileInformation().put("mimeType", MIME_TYPE[0]);
        smd.getFileInformation().put("fileFormat", FORMAT_NAMES[0]);
        smd.getFileInformation().put("varFormat_schema", "SPSS");


        // Now we have the control card pre-parsed and the individual
        // parts of the card ("commands") stored separately.
        // Now we can go through these parts and evaluate the commands,
        // which is going to give us the metadata describing the data set.
        //
        // These are the parts of the SPSS data definition card that we are
        // interested in:
        // DATA LIST 
        // VAR LABELS 
        // VALUE LABELS 
        // FORMATS (?)
        // MISSING VALUES 
        // RECODE (?)

        int readStatus = 0;

        readStatus = read_DataList(commandStrings.get("DataList"));
        dbgLog.fine ("reading DataList. status: "+readStatus);

        if (readStatus != 0) {
            if (readStatus == DATA_LIST_DELIMITER_NOT_FOUND) {
                throw new IOException ("Invalid SPSS Command Syntax: " +
                        "no delimiter specified in the DATA LIST command.");
            } else if (readStatus == DATA_LIST_NO_SLASH_SEPARATOR) {
                 throw new IOException ("Invalid SPSS Command Syntax: " +
                        "missing / delimiter on the DATA LIST line.");

            } else if (readStatus == DATA_LIST_UNSUPPORTED_VARIABLE_TYPE) {
                 throw new IOException ("Invalid SPSS Command Syntax: " +
                        "unsupported variable type definition in DATA LIST.");

            } else if (readStatus == DATA_LIST_ILLEGAL_NUMERIC_TYPE) {
                 throw new IOException ("Invalid SPSS Command Syntax: " +
                        "illegal numeric type definition.");

            }
        }

        readStatus = read_VarLabels(commandStrings.get("VarLabel"));
        dbgLog.fine ("reading VarLabels. status: "+readStatus);

        readStatus = read_ValLabels(commandStrings.get("ValLabel"));
        dbgLog.fine ("reading ValLabels. status: "+readStatus);

        readStatus = read_MisValues(commandStrings.get("MisValue"));
        dbgLog.fine ("reading MisValues. status: "+readStatus);

        // Now read the data file:

        CSVFileReader  csvFileReader = new CSVFileReader (getDelimiterChar());
        BufferedReader csvRd = new BufferedReader(new InputStreamReader(new FileInputStream(rawDataFile)));

        csvData = csvFileReader.read(csvRd, smd);

        PrintWriter pwout = createOutputWriter(null);

        storeTabFileData (csvData, pwout);


        // Calculate the datasets statistics, summary and category, and 
        // the UNF signatures:

        calculateDatasetStats(csvData);

        // Create and return the SDIOData object:
        sdiodata = new SDIOData(smd, csvData);

        dbgLog.fine("***** SPSSFileReader: read() end *****");


        return sdiodata;
    }

    // This method reads the card file and separates the individual parts (commands)
    // for further parsing.

    private void getSPSScommandLines (BufferedInputStream cardStream) throws IOException {
        dbgLog.fine("start dividing SPSS data definition file");
        int counter = 0;

        BufferedReader rd = new BufferedReader(new InputStreamReader(cardStream));
        String line = null;
        String linesCombined = null;

        List<String> SPSScommands = new ArrayList<String>();

        while ((line = rd.readLine()) != null) {
            // chop all blanks at the end, replace with a single whitespace:

            line = line.replaceFirst("[ \t\n]*$", " ");

            // skip blank, and similar lines:

            if (line.equals(" ") ||
                line.startsWith("comment") ||
                line.matches("^[ \t]*.$")) {
                    dbgLog.fine("skipping line");
            }

            // check first character:

            //String firstChar = line.substring(0, 1);

            if (line.startsWith(" ") || line.startsWith("\t")) {
                // continuation line;
                line = line.replaceAll("^[ \t]*", "");
                linesCombined = linesCombined + line;
            } else {
                // a new command line:
                if (linesCombined != null) {
                    SPSScommands.add(linesCombined);
                }
                linesCombined = line;
            }
         }

        rd.close();
        if (linesCombined != null) {
            SPSScommands.add(linesCombined);
        }

        String regexCommandLine = "^(\\w+?)\\s+?(\\w+)(.*)";
        Pattern patternCommandLine = Pattern.compile(regexCommandLine);

        for (int i = 0; i < SPSScommands.size(); i++) {
            String commandLine = SPSScommands.get(i);

            // Note that SPSS commands are not case-sensitive.

            String command1 = null;
            String command2 = null;
            String rest = null;

            Matcher commandMatcher = patternCommandLine.matcher(commandLine);

            if (commandMatcher.find()) {
                command1 = commandMatcher.group(1);
                command2 = commandMatcher.group(2);
                rest = commandMatcher.group(3);
            }

            dbgLog.fine("command1: "+command1);
            dbgLog.fine("command2: "+command2);
            dbgLog.fine("rest: "+rest);


            // TODO: code below executed only if rest != null -- ?

            // DATA LIST:
            
            if (command1 != null &&
                command2 != null &&
                command1.regionMatches(true, 0, "data", 0, 4) &&
                command2.regionMatches(true, 0, "list", 0, 4)) {

                if ( rest != null ) {
                    rest = rest.trim();
                    dbgLog.fine("saving "+rest+" as a DataList command");

                    if (commandStrings.get("DataList") == null) {
                        commandStrings.put("DataList", rest);
                    } else {
                        commandStrings.put("DataList", commandStrings.get("DataList")+"/"+rest);
                    }
                }
                
            // VARIABLE LABELS:    
             
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "var", 0, 3) &&
                        command2.regionMatches(true, 0, "lab", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-1);
                    }
                    dbgLog.fine("saving "+rest+" as a VarLabel command");
                    if (commandStrings.get("VarLabel") == null) {
                        commandStrings.put("VarLabel", rest);
                    } else {
                        commandStrings.put("VarLabel", commandStrings.get("VarLabel")+" "+rest);
                    }

                }
                
            // VALUE LABELS:

            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "val", 0, 3) &&
                        command2.regionMatches(true, 0, "lab", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-2);
                    }
                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals("/")) {
                            rest = rest.substring(0, rest.length()-2);
                    }

                    dbgLog.fine("saving "+rest+"/ as a ValLabel command");
                    if (commandStrings.get("ValLabel") == null) {
                        commandStrings.put("ValLabel", rest+"/");
                    } else {
                        commandStrings.put("ValLabel", commandStrings.get("ValLabel")+rest+"/");
                    }
                }

            // MISSING VALUES: 
                
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "mis", 0, 3) &&
                        command2.regionMatches(true, 0, "val", 0, 3)) {

                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.length()>0 &&
                        rest.substring(rest.length()-1).equals(".")) {
                            rest = rest.substring(0, rest.length()-2);
                    }

                    // TODO:
                    // Find out if converting these .toUpperCase() is the
                    // right thing to do.

                    dbgLog.fine("saving "+rest.toUpperCase()+" as the "+i+"-th MisValue command");

                    if (commandStrings.get("MisValue") == null) {
                        commandStrings.put("MisValue", rest);
                    } else {
                        commandStrings.put("MisValue", commandStrings.get("MisValue")+" "+rest.toUpperCase());
                    }

                }

            // FORMATS:
            //
            // (not supported, for now)

            // NUMBER OF CASES: (optional -- may not be present)
                
            } else if ( command1 != null &&
                        command2 != null &&
                        command1.regionMatches(true, 0, "n", 0, 1) &&
                        command2.regionMatches(true, 0, "of", 0, 2)) {
                if ( rest != null ) {
                    rest = rest.trim();

                    if (rest.regionMatches(true,0,"cases",0,5)) {
                        rest = rest.substring(5);
                        rest = rest.trim();
                        String regexNumberOfCases = "^([0-9]*)";
                        Pattern patternNumberOfCases = Pattern.compile(regexNumberOfCases);
                        Matcher casesMatcher = patternNumberOfCases.matcher(rest);

                        if (casesMatcher.find()) {
                            setCaseQnty(Integer.valueOf(casesMatcher.group(1)));
                            smd.getFileInformation().put("caseQnty", getCaseQnty());
                            dbgLog.fine("Number of cases found: "+getCaseQnty());
                        }
                    }
                }

            } // also:
            // RECODE
            // FORMATS


        }

    }

    // methods for parsing individual parts of the Control Card:

    // DATA LIST:

    int read_DataList (String dataListCommand) {
        int readStatus = 0;

        // Read the first line (DATA LIST ...) to determine
        // the field separator:
        // This line should be "/"-terminated (?)

        dbgLog.fine("dataList command: "+dataListCommand);
 
        List<Integer> variableTypeList= new ArrayList<Integer>();
        Set<Integer> decimalVariableSet = new HashSet<Integer>();

        List<Integer> printFormatList = new ArrayList<Integer>(); // TODO: move
        Map<String, String> printFormatNameTable = new LinkedHashMap<String, String>(); // TODO: move

        String delimiterString = null;

        //String datalistRegex = "^data\\s+list\\s+list\\('(.)'\\)\\s+?/";
        String datalistRegex = "^list\\('(.)'\\).*/";
        Pattern datalistPattern = Pattern.compile(datalistRegex, java.util.regex.Pattern.CASE_INSENSITIVE);
        Matcher datalistMatcher = datalistPattern.matcher(dataListCommand);

        if (datalistMatcher.find()) {
            delimiterString = datalistMatcher.group(1);
            setDelimiterChar(delimiterString.charAt(0));
            dbgLog.fine("found delimiter: "+delimiterString);
        } else {
            return DATA_LIST_DELIMITER_NOT_FOUND;
            // No delimiter declaration found
            //(not a delimited file perhaps?)
        }

        // Cut off the remaining lines containing the variable definitions:

        int separatorIndex = dataListCommand.indexOf("/");

        if (separatorIndex == -1) {
            return DATA_LIST_NO_SLASH_SEPARATOR;
            // No slash found after the first line of the Data List command.
        }

        dataListCommand = dataListCommand.substring(separatorIndex+1);


        // Parse the variable section. For a delimited file this should be
        // a list of variable name + data type pairs.
        // "fortran" type definitions are assumed.

        dbgLog.fine ("parsing "+dataListCommand+" for variable declarations.");

        int variableCounter = 0;

        String varName = null;
        String varType = null;

        String varDeclarationRegex = "\\s*(\\S+)\\s+\\((\\S+)\\)";
        Pattern varDeclarationPattern = Pattern.compile(varDeclarationRegex);
        Matcher varDeclarationMatcher = varDeclarationPattern.matcher(dataListCommand);

        String stringVarDeclarationRegex = "^[aA]([0-9]+)";
        Pattern stringVarDeclarationPattern = Pattern.compile(stringVarDeclarationRegex);

        String numVarDeclarationRegex = "^[fF]([0-9]+)\\.*([0-9]*)";
        Pattern numVarDeclarationPattern = Pattern.compile(numVarDeclarationRegex);

        while (varDeclarationMatcher.find()) {
            varName = varDeclarationMatcher.group(1);
            varType = varDeclarationMatcher.group(2);

            dbgLog.fine ("found variable "+varName+", type "+varType);

            variableNameList.add(varName);

            // unfVariableTypes list holds extended type definitions for the
            // UNF calculation;
            // we need to be able to differentiate between Integers and
            // real numbers, in addition to the "numeric" and "string" values.

            if (varType.startsWith("a") || varType.startsWith("A")) {
                // String:

                Matcher stringVarDeclarationMatcher = stringVarDeclarationPattern.matcher(varType);
                if (stringVarDeclarationMatcher.find()) {
                   variableTypeList.add(new Integer(stringVarDeclarationMatcher.group(1)));
                } else {
                    // set to default if the string size is not explicitely
                    // specified:
                    variableTypeList.add(1);
                }

                formatCategoryTable.put(varName, SPSSConstants.FORMAT_CATEGORY_TABLE.get("A"));

                unfVariableTypes.put(varName, -1);

                printFormatList.add(1);
                //printFormatNameTable.put(varName, "A");

            } else if (varType.startsWith("f") || varType.startsWith("F")) {
                // "minimal" format value is 0 -- numeric
                variableTypeList.add(0);
                formatCategoryTable.put(varName, SPSSConstants.FORMAT_CATEGORY_TABLE.get("F"));

                if ( varType.equals("f") || varType.equals("F")) {
                    // abbreviated numeric type definition;
                    // defaults to f10.0

                    // for the purposes of the UNF calculations this is an integer:
                    unfVariableTypes.put(varName, 0);

                    printFormatList.add(5); 
                    //printFormatNameTable.put(varName, "F10.0");

                } else {
                    Matcher numVarDeclarationMatcher = numVarDeclarationPattern.matcher(varType);
                    if (numVarDeclarationMatcher.find()) {
                        Integer numLength = new Integer(numVarDeclarationMatcher.group(1));
                        Integer numDecimal = 0;
                        String  optionalToken = numVarDeclarationMatcher.group(2);

                        if (optionalToken != null && !optionalToken.equals("")) {
                            numDecimal = new Integer (optionalToken);

                            if ((int)numDecimal > 0) {
                                unfVariableTypes.put(varName, 1);
                                decimalVariableSet.add(variableCounter);
                                printFormatNameTable.put(varName, "F"+numLength+"."+numDecimal);
                            }
                        }

                        printFormatList.add(5); // TODO: verify (should it be 0 instead?)

                    } else {
                        // This does not look like a valid numeric type
                        // definition.
                        return DATA_LIST_ILLEGAL_NUMERIC_TYPE;
                    }
                }

            } else if (varType.matches("^[1-9]$")) {
                // Another allowed SPSS abbreviation:
                // type (N) where N is [1-9] means a numeric decimal with
                // N decimal positions:

                variableTypeList.add(0);
                formatCategoryTable.put(varName, SPSSConstants.FORMAT_CATEGORY_TABLE.get("F"));

                Integer numDecimal = new Integer(varType);
                unfVariableTypes.put(varName, 1);
                decimalVariableSet.add(variableCounter);

                printFormatList.add(5); // TODO: verify (should it be 0 instead?)
                printFormatNameTable.put(varName, "F10."+numDecimal);

            } else {
                // invalid variable type definition.
                return DATA_LIST_UNSUPPORTED_VARIABLE_TYPE;
            }

            variableCounter++;

        }

        // TODO: validate variable entries;
        // return error code if any unsupported variable declarations are
        // found (for example, fixed width columns).

        smd.getFileInformation().put("varQnty", variableCounter);
        setVarQnty(variableCounter);
        dbgLog.fine("varQnty="+getVarQnty());

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

        return readStatus;
    }

    // VARIABLE LABELS:

    int read_VarLabels (String varLabelsCommand) {
        int readStatus = 0;

        // List of variable labels.
        // These are variable name + variable label pairs.

        dbgLog.fine ("parsing "+varLabelsCommand+" for variable labels.");

        Map<String, String> variableLabelMap = new LinkedHashMap<String, String>();

        String varName = null;
        String varLabel = null;

        String varLabelRegex = "\\s*(\\S+)\\s+\"([^\"]+)\"";
        Pattern varLabelPattern = Pattern.compile(varLabelRegex);
        Matcher varLabelMatcher = varLabelPattern.matcher(varLabelsCommand);

        while (varLabelMatcher.find()) {
            varName = varLabelMatcher.group(1);
            varLabel = varLabelMatcher.group(2);

            dbgLog.fine ("found variable label for "+varName+": "+varLabel);
            variableLabelMap.put(varName, varLabel);
        }

        // TODO:
        // Validate the entries, make sure that the variables are legit, etc.

        smd.setVariableLabel(variableLabelMap);

        return readStatus;
    }

    int read_ValLabels (String valLabelsCommand) {

        int readStatus = 0;

        Map<String, Map<String, String>> valueLabelTable =
                new LinkedHashMap<String, Map<String, String>>();
        Map<String, String> valueVariableMappingTable = new LinkedHashMap<String, String>();


        // Value Labels are referenced by the (declared) variable names,
        // followed by a number of value-"label" pairs; for every variable
        // the entry is terminated with a "/".

        dbgLog.fine ("parsing "+valLabelsCommand+" for value labels.");


        String varName = null;
        String valLabelDeclaration = null;

        String varValue = null;
        String valueLabel = null;

        String valLabelRegex = "\\s*(\\S+)\\s+([^/]+)/";
        Pattern valLabelPattern = Pattern.compile(valLabelRegex);
        Matcher valLabelMatcher = valLabelPattern.matcher(valLabelsCommand);

        String labelDeclarationRegex = "\\s*(\\S+)\\s+(\"[^\"]*\")";
        Pattern labelDeclarationPattern = Pattern.compile(labelDeclarationRegex);


        while (valLabelMatcher.find()) {
            varName = valLabelMatcher.group(1);
            valLabelDeclaration = valLabelMatcher.group(2);

            dbgLog.fine ("found value label declaration for "+varName+": "+valLabelDeclaration);

            Map <String, String> valueLabelPairs = new LinkedHashMap<String, String>();


            Matcher labelDeclarationMatcher = labelDeclarationPattern.matcher(valLabelDeclaration);

            while (labelDeclarationMatcher.find()) {
                varValue = labelDeclarationMatcher.group(1);
                valueLabel = labelDeclarationMatcher.group(2);

                dbgLog.fine ("found label "+valueLabel+" for value "+varValue);

                Boolean isNumeric = false; // dummy placeholder

                if (isNumeric){
                    // Numeric variable:
                    dbgLog.fine("processing numeric value label");
                    valueLabelPairs.put(doubleNumberFormatter.format(new Double(varValue)), valueLabel);
                } else {
                    // String variable
                    dbgLog.fine("processing string value label");
                    valueLabelPairs.put(varValue, valueLabel );
                }
            }

            valueLabelTable.put(varName,valueLabelPairs);
            valueVariableMappingTable.put(varName, varName);

            // TODO:
            // Do SPSS cards support shared value label sets -- ?

        }

        smd.setValueLabelTable(valueLabelTable);
        smd.setValueLabelMappingTable(valueVariableMappingTable);


        // TODO: 
        // Better validation, error reporting.

        return readStatus;
    }

    int read_MisValues (String misValuesCommand) {
        int readStatus = 0;

        // Missing Values:
        // These are declared by the (previously declared) variable name,
        // followed by a comma-separated list of values in parentheses.
        //  for ex.: FOOBAR (1, 2, 3)

        dbgLog.fine ("parsing "+misValuesCommand+" for missing values.");

        Map<String, List<String>> missingValueTable = new LinkedHashMap<String, List<String>>();

        String varName = null;
        String misValuesDeclaration = null;

        String misValue = null;

        String misValuesRegex = "\\s*(\\S+)\\s+\\(([^\\)]+)\\)";
        Pattern misValuesPattern = Pattern.compile(misValuesRegex);
        Matcher misValuesMatcher = misValuesPattern.matcher(misValuesCommand);

        String misValDeclarationRegex = "\\s*([^,]+)\\s*,*";
        Pattern misValDeclarationPattern = Pattern.compile(misValDeclarationRegex);


        while (misValuesMatcher.find()) {
            varName = misValuesMatcher.group(1);
            misValuesDeclaration = misValuesMatcher.group(2);

            dbgLog.fine ("found missing values declaration for "+varName+": "+misValuesDeclaration);

            Matcher misValDeclarationMatcher = misValDeclarationPattern.matcher(misValuesDeclaration);

            Boolean isNumericVariable = false;
            List<String> mv = new ArrayList<String>();

            while (misValDeclarationMatcher.find()) {
                misValue = misValDeclarationMatcher.group(1);

                dbgLog.fine ("found missing value: "+misValue);

                if (isNumericVariable) {
                    // No support for ranges (yet?)
                    // TODO: find out if SPSS cards support ranges of
                    // missing values.
                    mv.add(doubleNumberFormatter.format(new Double(misValue)));
                } else {
                    mv.add(misValue);
                }
            }

            missingValueTable.put(varName, mv);

        }

        // TODO:
        // Better validation, error reporting.

        smd.setMissingValueTable(missingValueTable);

        return readStatus;
    }


    // method for creating the output writer for the temporary tab file.
    // this shouldn't be in the plugins really.

    PrintWriter createOutputWriter (BufferedInputStream stream) throws IOException {
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
        dbgLog.fine("variableName="+variableNameList.get(variablePosition));

        switch(variableType){
            case 0:
                // Integer case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

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

                dbgLog.finer("sumstat:long case=" + Arrays.deepToString(
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata))));

                smd.getSummaryStatisticsTable().put(variablePosition,
                        ArrayUtils.toObject(StatHelper.calculateSummaryStatistics(ldata)));


                Map<String, Integer> catStat = StatHelper.calculateCategoryStatistics(ldata);
                smd.getCategoryStatisticsTable().put(variableNameList.get(variablePosition), catStat);

                break;

            case 1:
                // double case
                // note: due to DecimalFormat class is used to
                // remove an unnecessary decimal point and 0-padding
                // numeric (double) data are now String objects

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
                // String case
                dbgLog.finer("string case");


                String[] strdata = Arrays.asList(varData).toArray(
                    new String[varData.length]);
                dbgLog.finer("string array passed to calculateUNF: "+Arrays.deepToString(strdata));
                //unfValue = UNF5Util.calculateUNF(strdata, dateFormats);
                unfValue = UNF5Util.calculateUNF(strdata);
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

            try {
                dbgLog.finer("j = "+j);

                unfValues[j] = getUNF(csvData.getData()[j], null, variableTypeNumer,
                    unfVersionNumber, j);
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
        BufferedInputStream spssCardStream = null;
        SDIOData processedCard = null;
        SPSSFileReader spssReader = null;

        String testCardFile = args[0];
        String csvRawDataFile = args[1];

        try {

            spssCardStream = new BufferedInputStream(new FileInputStream(testCardFile));

            spssReader = new SPSSFileReader(null);
            processedCard = spssReader.read(spssCardStream, new File(args[1]));
        } catch (IOException ex) {
            System.out.println("exception caught!");
            if (spssReader == null) {
                System.out.println("failed to create an SPSS file reader.");
            }
        }


    }

}
