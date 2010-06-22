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
