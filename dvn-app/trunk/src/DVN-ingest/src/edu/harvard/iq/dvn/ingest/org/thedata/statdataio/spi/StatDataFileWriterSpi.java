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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import java.io.*;

/**
 * The service provider interface (SPI) for <code>StatDataFileWriter</code>.
 * This abstract class supplies several types of information about the associated
 * <code>StatDataFileWriter</code> class.
 *
 * @author akio sone at UNC-Odum
 */
public abstract class StatDataFileWriterSpi extends StatDataFileReaderWriterSpi{

    /**
     * A <code>String</code> array of the fully qualified names of 
     * all the <code>StatDataFileReaderSpi</code> classes associated with this
     * <code>StatDataFileReader</code> class.
     */
    protected String[] readerSpiNames = null;

    
    /**
     * Gets the value of the readerSpiNames field.
     * 
     * @return the value of the readerSpiNames field.
     */
    public String[] getStatDataFileReaderSpiNames() {
        return readerSpiNames == null ?
            null : (String[])readerSpiNames.clone();
    }
    
    /**
     * Constructs an empty <code>StatDataFileWriterSpi</code> instance.
     */
    protected StatDataFileWriterSpi() {
    }
    
    
    
    
    /**
     * Returns an instance of the <code>StatDataFileWriter</code> 
     * implementation associated with the service provider.
     * <p> The default implementation simply returns createWriterInstance(null).
     *
     * @return a <code>StatDataFileWriter</code> instance.
     * @throws java.io.IOException if an writing error occurs during 
     * its loading or initialization or instantiation.
     */
    public StatDataFileWriter createWriterInstance() throws IOException {
        return createWriterInstance(null);
    }

    /**
     * Returns an instance of the <code>StatDataFileWriter</code> 
     * implementation associated with the service provider.
     * 
     * @param extension a plug-in specific format extension object
     * @return a <code>StatDataFileWriter</code> instance.
     * @throws java.io.IOException if an instantiation attempt of the writer
     * fails.
     */
    public abstract StatDataFileWriter createWriterInstance(Object extension)
        throws IOException;



    /**
     * Returns <code>true</code> if the <code>StatDataFileWriter</code> object
     * supplied in is an instance of the <code>StatDataFileWriter</code>
     * associated with this service provider.
     * 
     * @param writer  an <code>StatDataFileWriter</code> object.
     * @return <code>true</code> if <code>writer</code> is recognized.
     */
    public boolean isOwnWriter(StatDataFileWriter writer) {
            if (writer == null) {
                throw new IllegalArgumentException("writer == null!");
            }
            String name = writer.getClass().getName();
            return name.equals(pluginClassName);
    }
    

}
