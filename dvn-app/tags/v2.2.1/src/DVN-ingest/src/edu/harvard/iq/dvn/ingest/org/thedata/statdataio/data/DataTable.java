/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2009
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
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
