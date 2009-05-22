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
 *
 * @author Akio Sone
 */
public class DataTable {

    protected String[] columnNames;

    protected Object[][] data;

    protected String[] unf;

    protected String fileUnf;

    public static RecognizedDataTypes organization = RecognizedDataTypes.TABULAR;

    public DataTable() {
    }

    public String[] getColumnNames(){
        return columnNames;
    }

    public Object[][] getData(){
        return data;
    }

    public String[] getUnf(){
        if (unf == null){
            unf = calculateUnf();
        }
        return unf;
    }

    public void setUnf(String[] unf){
        this.unf = unf;
    }
    
    public String getFileUnf(){
        if (fileUnf == null){
            fileUnf = calculateFileUnf();
        }
        return fileUnf;
    }

    public void setFileUnf(String unf){
        this.fileUnf = unf;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    public void printData(String title, String separator){
        out.println(title);
        for (int i=0; i< this.data.length; i++){
            out.println(StringUtils.join(this.data[i], separator));
        }
    }

    public void printData(String title){
        String separator = "|";
        printData(title, separator);
    }

    public void printData(){
        String title = "Contents of the DataTable instance";
        String separator = "|";
        printData(title, separator);
    }

    private String[] calculateUnf(){
        String[] var_unf_set=null;
        int nrow = data.length;
        int ncol = data[0].length;

        for (int j=0;j<ncol; j++){
            
            for (int i=0;i<nrow;i++){
                //data[i][j];
            }
        }

        return  var_unf_set;
    }
    private String calculateFileUnf(){
        String file_unf = null;

        return file_unf;
    }
}
