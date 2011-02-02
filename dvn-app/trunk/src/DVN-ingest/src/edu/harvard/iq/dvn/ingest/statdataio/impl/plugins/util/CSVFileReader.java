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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.util;

import java.io.*;
import java.util.logging.*;
import org.apache.commons.lang.StringUtils;


import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;

/**
 * This is a reader for a CSV (character-separated values) data file.
 * Note that this is not a fully-functional data file reader plugin
 * (it doesn't extend StatDataFileReader), this class only reads the
 * data (i.e., DataTable) from a plain text file. The metadata describing
 * the data set and its variables should be supplied elsewhere.
 * (For example, via an SPSS control card; the assumption is that in the
 * future we'll be offering support for other data-less metadata declarations,
 * then all these different readers will be able to use this data reader)
 *
 * @author Leonid Andreev
 *
 */
public class CSVFileReader implements java.io.Serializable {
    private char delimiterChar='\t';

    private static Logger dbgLog =
       Logger.getLogger(CSVFileReader.class.getPackage().getName());


    public CSVFileReader () {
    }

    public CSVFileReader (char delimiterChar) {
        this.delimiterChar = delimiterChar;
    }


    // version of the read method that parses the CSV file and stores
    // its content in the data table matrix (in memory).
    // TODO: remove this method.
    // Only the version that reads the file and stores it in a TAB file
    // should be used.
    public DataTable read(BufferedReader csvReader, SDIOMetadata smd) throws IOException {
        DataTable csvData = new DataTable();
        Object[][] dataTable = null;
        int varQnty = new Integer(smd.getFileInformation().get("varQnty").toString());
        int caseQnty = new Integer(smd.getFileInformation().get("caseQnty").toString());

        dataTable = new Object[varQnty][caseQnty];

        String line;
        String[] valueTokens = new String[varQnty];
        int lineCounter = 0;

        boolean[] isCharacterVariable = smd.isStringVariable();
        boolean[] isContinuousVariable = smd.isContinuousVariable();

        dbgLog.fine("CSV reader; varQnty: "+varQnty);
        dbgLog.fine("CSV reader; caseQnty: "+caseQnty);
        dbgLog.fine("CSV reader; delimiter: "+delimiterChar);

        while ((line = csvReader.readLine()) != null) {
            // chop the line:
            line = line.replaceFirst("[\r\n]*$", "");
            valueTokens = line.split(""+delimiterChar, varQnty);

            //dbgLog.fine("case: "+lineCounter);
            
            for ( int i = 0; i < varQnty; i++ ) {
                //dbgLog.fine("value: "+valueTokens[i]);

                if (isCharacterVariable[i]) {
                    // String. Adding to the table, quoted.
                    // Empty strings stored as " " (one white space):
                    if (valueTokens[i] != null && (!valueTokens[i].equals(""))) {
                        dataTable[i][lineCounter] = valueTokens[i];
                    } else {
                        dataTable[i][lineCounter] = " ";
                    }
                    
                } else if (isContinuousVariable[i]) {
                    // Numeric, Double:
                    try {
                        Double testDoubleValue = new Double(valueTokens[i]);
                        dataTable[i][lineCounter] = testDoubleValue.toString();//valueTokens[i];
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = (new Double(0)).toString();
                        dataTable[i][lineCounter] = "";
                    }
                } else {
                    // Numeric, Integer:
                    try {
                        Integer testIntegerValue = new Integer(valueTokens[i]);
                        dataTable[i][lineCounter] = testIntegerValue.toString();
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = "0";
                        dataTable[i][lineCounter] = "";
                    }
                }
            }
            lineCounter++;
        }

        csvData.setData(dataTable);
        return csvData;
    }


    public int read(BufferedReader csvReader, SDIOMetadata smd, PrintWriter pwout) throws IOException {

        //DataTable csvData = new DataTable();
        int varQnty = 0;

        try {
            varQnty = new Integer(smd.getFileInformation().get("varQnty").toString());
        } catch (Exception ex) {
            //return -1;
            throw new IOException ("CSV File Reader: Could not obtain varQnty from the dataset metadata.");
        }

        if (varQnty == 0) {
            //return -1;
            throw new IOException ("CSV File Reader: varQnty=0 in the dataset metadata!");
        }

        String[] caseRow = new String[varQnty];

        String line;
        String[] valueTokens;

        int lineCounter = 0;

        boolean[] isCharacterVariable = smd.isStringVariable();
        boolean[] isContinuousVariable = smd.isContinuousVariable();

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
                        charToken = "\"" + charToken + "\"";
                        caseRow[i] = charToken;
                    } else {
                        caseRow[i] = "\" \"";
                    }

                } else if (isContinuousVariable[i]) {
                    // Numeric, Double:
                    try {
                        Double testDoubleValue = new Double(valueTokens[i]);
                        caseRow[i] = testDoubleValue.toString();//valueTokens[i];
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = (new Double(0)).toString();
                        caseRow[i] = "";
                    }
                } else {
                    // Numeric, Integer:
                    try {
                        Integer testIntegerValue = new Integer(valueTokens[i]);
                        caseRow[i] = testIntegerValue.toString();
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        //dataTable[i][lineCounter] = "0";
                        caseRow[i] = "";
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

}