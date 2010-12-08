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
            line = line.replaceFirst("[ \t\n]*$", "");
            valueTokens = line.split(""+delimiterChar, varQnty);

            //dbgLog.fine("case: "+lineCounter);
            
            for ( int i = 0; i < varQnty; i++ ) {
                //dbgLog.fine("value: "+valueTokens[i]);

                if (isCharacterVariable[i]) {
                    // String. Adding to the table as is.
                    dataTable[i][lineCounter] = valueTokens[i];
                    
                } else if (isContinuousVariable[i]) {
                    // Numeric, Double:
                    try {
                        Double testDoubleValue = new Double(valueTokens[i]);
                        dataTable[i][lineCounter] = testDoubleValue.toString();//valueTokens[i];
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        dataTable[i][lineCounter] = (new Double(0)).toString();
                    }
                } else {
                    // Numeric, Integer:
                    try {
                        Integer testIntegerValue = new Integer(valueTokens[i]);
                        dataTable[i][lineCounter] = testIntegerValue.toString();
                    } catch (Exception ex) {
                        dbgLog.fine("caught exception reading numeric value; variable: "+i+", case: "+lineCounter+"; value: "+valueTokens[i]);

                        dataTable[i][lineCounter] = "0";
                    }
                }
            }
            lineCounter++;
        }

        csvData.setData(dataTable);
        return csvData;
    }

}