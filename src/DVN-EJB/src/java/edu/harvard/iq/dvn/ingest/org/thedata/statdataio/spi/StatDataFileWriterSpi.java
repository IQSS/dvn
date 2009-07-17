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
