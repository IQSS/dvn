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
