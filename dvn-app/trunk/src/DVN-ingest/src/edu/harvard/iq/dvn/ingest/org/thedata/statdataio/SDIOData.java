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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.data.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;

/**
 * A class that represents a statistical data file with a pair of a metadata and 
 * data fields.
 * 
 * @author akio sone
 */
public class SDIOData {

    /**
     * The <code>metadata</code> field of a <code>SDIOData</code> object
     * represented by an instance of <code>SDIOMetadata</code>
     */
    protected SDIOMetadata metadata;

    /**
     * The <code>data</code> field of a <code>SDIOData</code> object 
     * represented by an instance of <code>DataTable</code>,
     */
    protected Data data;

    /**
     * Creates a new <code>SDIOData</code> object without initialization.
     */
    public SDIOData(){

    }
    /**
     * Constructs an new <code>SDIOData</code> object using the given
     * <code>metadata</code> and <code>data</code> field values.
     * 
     * @param metadata an instance of <code>SDIOMetadata</code>
     * @param data an instance of <code>DataTable</code>
     */
    public SDIOData(SDIOMetadata metadata, Data data) {
        this.metadata = metadata;
        this.data = data;
    }

    /**
     * Returns an object of <code>DataTable</code> as the data
     * field of the given <code>SDIOData</code> object.
     * 
     * @return an DataTable object 
     */
    public Data getData() {
        return data;
    }

    /**
     * set the <code>data</code> field of the given <code>SDIOData</code> 
     * object with the given <code>DataTable</code>.
     *
     * @param data an object of <code>DataTable</code>
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Returns an object of <code>SDIOMetadata</code> as the metadata
     * field of the given <code>SDIOData</code> object.
     *
     * @return an object of <code>SDIOMetadata</code>.
     */
    public SDIOMetadata getMetadata() {
        return metadata;
    }

    /**
     * set the <code>metadata</code> field of the given <code>SDIOData</code> 
     * object with the given <code>SDIOMetadata</code>.
     *
     * @param metadata an object of <code>SDIOMetadata</code>
     */
    public void setMetadata(SDIOMetadata metadata) {
        this.metadata = metadata;
    }

}
