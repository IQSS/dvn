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

import cern.colt.list.*;

/**
 *
 * @author Akio Sone
 */
public class DataFrame extends Data{

    protected ObjectArrayList data;

    protected String[] columnNames;

    protected String[] unf;


    public static RecognizedDataTypes organization = RecognizedDataTypes.TABULAR;

    public DataFrame() {
    }

    public String[] getColumnNames(){
        return columnNames;
    }

    public ObjectArrayList getData(){
        return data;
    }

    public String[] getUnf(){
        return unf;
    }

    public void setUnf(String[] unf){
        this.unf = unf;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void setData(ObjectArrayList data) {
        this.data = data;
    }


}
