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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data;

import static java.lang.System.*;
import org.apache.commons.lang.*;

/**
 * <code>DataTable</code> represents a tabular data of a statistical
 * data file by two-dimensional <code>Object</code> array.
 * 
 * @author Akio Sone
 */
public class DataTable extends Data {


    /**
     * A tabular data of a statistical data file
     * represented by two-dimensional <code>Object</code> array.
     * <p>Note: data are stored in variable-wise for convenience of
     * calculating variable-wise statistics.
     * 
     */
    Object[][] data;


    /**
     * Sets the value of data
     *
     * @param data new value of data
     */
    public void setData(Object[][] data) {
        this.data = data;
    }
    
    /**
     * Gets the value of data
     * 
     * @return the value of data
     */
    public Object[][] getData(){
        return data;
    }

    /**
     * The data type of this class.
     */
    public static final RecognizedDataTypes organization = RecognizedDataTypes.TABULAR;

    /**
     *  Constructs a <code>DataFrame</code> object  without initialization
     */
    public DataTable() {
    }


    /**
     * Prints the data field of a <code>DataTable</code> object.
     * @param title a labeling<code>String</code>
     * @param separator a <code>String</code> that separates each
     * datum,  "|" is used if it is null.
     */
    public void printData(String title, String separator){
        if (separator == null){
            separator ="|";
        }
        out.println(title);
        for (int i=0; i< this.data.length; i++){
            out.println(StringUtils.join(this.data[i], separator));
        }
    }

    /**
     * Prints the data field of a <code>DataTable</code> object 
     * with the default settings (default separator is "|").
     */
    public void printData(){
        String title = "Contents of the DataTable instance";
        String separator = "|";
        printData(title, separator);
    }

}
