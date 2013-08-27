/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author landreev
 */

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
package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.rdata;

import java.io.*;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a customized version of CSVFileReader (see ../util/CSVFileReader;
 
 * Tab files saved by R need some special post-processing, unique to the
 * R Ingest, so a specialized version of the file parser was needed. 
 * 
 *
 * @author Leonid Andreev
 *
 */
public class RTabFileParser implements java.io.Serializable {
    private char delimiterChar='\t';

    private static Logger dbgLog =
       Logger.getLogger(RTabFileParser.class.getPackage().getName());


    public RTabFileParser () {
    }

    public RTabFileParser (char delimiterChar) {
        this.delimiterChar = delimiterChar;
    }


    // version of the read method that parses the CSV file and stores
    // its content in the data table matrix (in memory).
    // TODO: remove this method.
    // Only the version that reads the file and stores it in a TAB file
    // should be used.


  public int read(BufferedReader csvReader, SDIOMetadata smd, PrintWriter pwout) throws IOException {
    dbgLog.warning("RTabFileParser: Inside R Tab file parser");
      
        //DataTable csvData = new DataTable();
        int varQnty = 0;

        try {
            varQnty = new Integer(smd.getFileInformation().get("varQnty").toString());
        } catch (Exception ex) {
            //return -1;
            throw new IOException ("R Tab File Parser: Could not obtain varQnty from the dataset metadata.");
        }

        if (varQnty == 0) {
            //return -1;
            throw new IOException ("R Tab File Parser: varQnty=0 in the dataset metadata!");
        }

        String[] caseRow = new String[varQnty];

        String line;
        String[] valueTokens;

        int lineCounter = 0;

        boolean[] isCharacterVariable = smd.isStringVariable();
        boolean[] isContinuousVariable = smd.isContinuousVariable();
        boolean[] isDateVariable = smd.isDateVariable();
        boolean[] isCategoricalVariable = smd.isCategoricalVariable();
        boolean[] isBooleanVariable = smd.isBooleanVariable();
        
        //Map<Integer, Map<String, String>> ReverseValueLabelTable = createReverseCatValueTable(smd); 
        
        
        Map<String, String> valueLabelMappingTable = smd.getValueLabelMappingTable();
        Map<String, Map<String, String>> valueLabelTable = smd.getValueLabelTable();
        
        
        Map <String, String> valueLabelPairs = new LinkedHashMap<String, String>();


        dbgLog.fine("CSV reader; varQnty: "+varQnty);
        dbgLog.fine("CSV reader; delimiter: "+delimiterChar);

        while ((line = csvReader.readLine()) != null) {
            // chop the line:
            line = line.replaceFirst("[\r\n]*$", "");
            valueTokens = line.split(""+delimiterChar, -2);

            if (valueTokens == null) {
                throw new IOException("Failed to read line "+(lineCounter+1)+" of the Data file.");

            }

            if (valueTokens.length != varQnty) {
                throw new IOException("Reading mismatch, line "+(lineCounter+1)+" of the Data file: " +
                        varQnty + " delimited values expected, "+valueTokens.length+" found.");
            }

            //dbgLog.fine("case: "+lineCounter);

            for ( int i = 0; i < varQnty; i++ ) {
                //dbgLog.fine("value: "+valueTokens[i]);

                if (isCharacterVariable[i]) {
                    // String. Adding to the table, quoted.
                    // Empty strings stored as " " (one white space):
                    if (valueTokens[i] != null && (!valueTokens[i].equals(""))) {
                        String charToken = valueTokens[i];
                        // Dealing with quotes: 
                        // remove the leading and trailing quotes, if present:
                        charToken = charToken.replaceFirst("^\"", "");
                        charToken = charToken.replaceFirst("\"$", "");
                        // escape the remaining ones:
                        charToken = charToken.replace("\"", "\\\"");
                        // final pair of quotes:
                        if (isDateVariable==null || (!isDateVariable[i])) {
                            charToken = "\"" + charToken + "\"";
                        }
                        caseRow[i] = charToken;
                    } else {
                        /*
                         * Note the commented-out code below; it was converting
                         * missing values into " " (a double-quoted space - ?)
                         * I'm pretty positive that was a result of a misreading 
                         * of some spec many years ago. I'm also fairly positive
                         * that the only way to store an NA unambiguously is as 
                         * an *empty* string, no quotes, no nothing!
                         * TODO: 
                         * Not sure about the above anymore; need to figure it out. 
                         * -- L.A.
                         */
                        /*
                        if (isDateVariable==null || (!isDateVariable[i])) {
                           caseRow[i] = " ";
                        } else {
                        */
                           caseRow[i] = ""; 
                        /*}*/
                    }

                } else if (isContinuousVariable[i]) {
                    // Numeric, Double:
                    // This is the major case of special/custom processing,
                    // specific for R ingest. It was found to be impossible
                    // to write a numeric/continuous column into the tab file
                    // while unambiguously preserving both NA and NaNs, if both
                    // are present. At least, not if using the standard 
                    // write.table function. So it seemed easier to treat this
                    // as a special case, rather than write our own write.table
                    // equivalent in R. On the R side, if any special values 
                    // are present in the columns, the values will be 
                    // converted into a character vector. The NAs and NaNs will 
                    // be replaced with the character tokens "NA" and "NaN" 
                    // respectively. Of course R will add double quotes around 
                    // the tokens, hence the post-processing - we'll just need 
                    // to remove all these quotes, and then we'll be fine. 
                    
                    dbgLog.fine("R Tab File Parser; double value: "+valueTokens[i]); 
                    // Dealing with quotes: 
                    // remove the leading and trailing quotes, if present:
                    valueTokens[i] = valueTokens[i].replaceFirst("^\"", "");
                    valueTokens[i] = valueTokens[i].replaceFirst("\"$", "");
                    if (valueTokens[i] != null && valueTokens[i].equalsIgnoreCase("NA")) {
                        caseRow[i] = "";
                    } else if (valueTokens[i] != null && valueTokens[i].equalsIgnoreCase("NaN")) {
                        caseRow[i] = "NaN";
                    } else if (valueTokens[i] != null && 
                            ( valueTokens[i].equalsIgnoreCase("Inf")
                            || valueTokens[i].equalsIgnoreCase("+Inf"))) {
                        caseRow[i] = "Inf";
                    } else if (valueTokens[i] != null && valueTokens[i].equalsIgnoreCase("-Inf")) {
                        caseRow[i] = "-Inf";
                    } else {
                        try {
                            Double testDoubleValue = new Double(valueTokens[i]);
                            caseRow[i] = testDoubleValue.toString();//valueTokens[i];
                        } catch (Exception ex) {
                            dbgLog.fine("caught exception reading numeric value; variable: " + i + ", case: " + lineCounter + "; value: " + valueTokens[i]);

                            //dataTable[i][lineCounter] = (new Double(0)).toString();
                            caseRow[i] = "";
                            
                            // TODO:
                            // decide if we should rather throw an exception and exit here; 
                            // all the values in this file at this point must be 
                            // legit numeric values (?) -- L.A.
                        }
                    }
                } else if (isBooleanVariable[i]) {
                    if (valueTokens[i] != null) {
                        String charToken = valueTokens[i];
                        // remove the leading and trailing quotes, if present:
                        charToken = charToken.replaceFirst("^\"", "");
                        charToken = charToken.replaceFirst("\"$", "");
                        
                        if (charToken.equals("FALSE")) {
                            caseRow[i] = "0";
                        } else if (charToken.equals("TRUE")) {
                            caseRow[i] = "1";
                        } else if (charToken.equals("")) {
                            // Legit case - Missing Value!
                            caseRow[i] = charToken;
                        } else {
                            throw new IOException("Unexpected value for the Boolean variable ("+i+"): "+charToken);
                        }
                    } else {
                        throw new IOException("Couldn't read Boolean variable ("+i+")!");
                    }

                    
                }/* 
                 * [experimental] special case for categorical variables. 
                 * See the comment for the [commented-out] createReverseCatValueTable
                 * method below for more info. -- L.A.
                 else if (isCategoricalVariable[i]) {
                    // an R factor: 
                    // The entry in the saved tab file is in fact the character
                    // label of the factor. We want to replace it with the actual
                    // numeric value in the final tab file.
                    dbgLog.info("RTabFile reader; factor value: "+valueTokens[i]);
                    
                    if (valueTokens[i] != null && (!valueTokens[i].equals(""))) {
                        String charToken = valueTokens[i];
                        // Dealing with quotes: 
                        // remove the leading and trailing quotes, if present:
                        charToken = charToken.replaceFirst("^\"", "");
                        charToken = charToken.replaceFirst("\"$", "");
                        
                        if (ReverseValueLabelTable.get(i).get(charToken) != null && 
                                !(ReverseValueLabelTable.get(i).get(charToken).equals(""))) {
                            caseRow[i] = ReverseValueLabelTable.get(i).get(charToken);
                        } else {
                            // Not quite sure what to do if the label is NOT 
                            // on the map... - ??
                            // (this really should not happen...)
                            
                            caseRow[i] = "";
                        }
                    } else {
                           caseRow[i] = ""; 
                    }
                    
                } */ else {
                    // Numeric, Integer:
                    // One special case first: R NA (missing value) needs to be 
                    // converted into the DVN's missing value - an empty String;
                    // (strictly speaking, this isn't necessary - an attempt to 
                    // create an Integer object from the String "NA" would
                    // result in an exception, that would be intercepted below,
                    // with the same end result)
                    dbgLog.fine("R Tab File Parser; integer value: "+valueTokens[i]);
                    if (valueTokens[i] != null && valueTokens[i].equalsIgnoreCase("NA")) {
                        caseRow[i] = "";
                    } else {
                        try {
                            Integer testIntegerValue = new Integer(valueTokens[i]);
                            caseRow[i] = testIntegerValue.toString();
                        } catch (Exception ex) {
                            dbgLog.fine("caught exception reading numeric value; variable: " + i + ", case: " + lineCounter + "; value: " + valueTokens[i]);

                            //dataTable[i][lineCounter] = "0";
                            caseRow[i] = "";
                        }
                    }
                }
            }

            pwout.println(StringUtils.join(caseRow, "\t"));

            lineCounter++;
        }

        //csvData.setData(dataTable);
        //return csvData;

        pwout.close();
        return lineCounter;
    }
  
    /*
     * The [experimental] code below is for creating a "reverse" categorical 
     * map, that links *labels* to *values*. This would be used to replace the
     * character labels in the tab file with the corresponding numerical values. 
     * We are NOT doing this however, not for R ingest anyway. 
    Map<Integer, Map<String, String>> createReverseCatValueTable(SDIOMetadata smd) {
        
        Map<Integer, Map<String, String>> reverseValueLabelTable = new LinkedHashMap<Integer, Map<String, String>>();
        
        Map<String, String> valueLabelMappingTable = smd.getValueLabelMappingTable();
        Map<String, Map<String, String>> valueLabelTable = smd.getValueLabelTable();
        
        boolean[] isCategoricalVariable = smd.isCategoricalVariable();
        
        
        for (int i = 0; i < smd.getVariableName().length; i++) {

            if (isCategoricalVariable[i]) {
                String variableName = smd.getVariableName()[i];

                String valueLabelTableName = valueLabelMappingTable.get(variableName);
                Map<String, String> labelsToValuePairs = new LinkedHashMap<String, String>();


                for (String value : valueLabelTable.get(valueLabelTableName).keySet()) {

                    String valueLabel = valueLabelTable.get(valueLabelTableName).get(value);
                    
                    labelsToValuePairs.put(valueLabel, value);
                }
                
                reverseValueLabelTable.put(i, labelsToValuePairs);
            }
        }
        
        return reverseValueLabelTable; 
    }
    */


}