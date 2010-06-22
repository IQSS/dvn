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
 * <code>DataFrame</code> represents a tabular data in a statistical
 * data file by <code>ObjectArrayList</code> of Colt-list package.
 * 
 * @author Akio Sone
 */
public class DataFrame extends Data{

    /**
     * Tabular data of a statistical data file
     * represented by <code>ObjectArrayList</code> of Colt-list package
     */
    ObjectArrayList data;

    /**
     * Sets the value of data
     *
     * @param data new value of data
     */
    public void setData(ObjectArrayList data) {
        this.data = data;
    }

    /**
     * Gets the value of data
     * 
     * @return the value of data
     */
    public ObjectArrayList getData(){
        return data;
    }

    /**
     * The data type of this class.
     */
    public static final RecognizedDataTypes organization = RecognizedDataTypes.TABULAR;

    /**
     * Constructs a <code>DataFrame</code> object  without initialization
     */
    public DataFrame() {
    
    }

}
