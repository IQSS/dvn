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
import static java.lang.System.*;
import java.io.*;

/**
 *
 * @author akio sone
 */
public abstract class StatDataFileReaderSpi extends StatDataFileReaderWriterSpi{



    /**
     *
     */
    protected String[] writerSpiNames = null;
    
    /**
     *
     */
    protected StatDataFileReaderSpi() {
    }

    /**
     *
     * @param vendorName
     * @param version
     * @param names
     * @param suffixes
     * @param MIMETypes
     * @param readerClassName
     */
    public StatDataFileReaderSpi(
            String vendorName,
            String version,
            String[] names,
            String[] suffixes,
            String[] MIMETypes,
            String readerClassName) {
        super(vendorName,
                version,
                names,
                suffixes,
                MIMETypes,
                readerClassName);
        //out.println("StatDataFileReaderSpi is called");
    }
    /**
     *
     * @param source
     * @return
     * @throws java.io.IOException
     */
    public abstract boolean canDecodeInput(Object source) throws IOException;
    /**
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public abstract boolean canDecodeInput(File file) throws IOException;

    /**
     *
     * @param stream
     * @return
     * @throws java.io.IOException
     */
    public abstract boolean canDecodeInput(BufferedInputStream stream) throws IOException;
    
    /**
     *
     * @param extension
     * @return
     * @throws java.io.IOException
     */
    public abstract StatDataFileReader createReaderInstance(Object extension)
        throws IOException;
    
    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public StatDataFileReader createReaderInstance() throws IOException{
        return createReaderInstance(null);
    }
    /**
     *
     * @param reader
     * @return
     */
    public boolean isOwnReader(StatDataFileReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        String name = reader.getClass().getName();
        return name.equals(pluginClassName);
    }

    /**
     *
     * @return
     */
    public String[] getStatDataFileWriterSpiNames() {
        return writerSpiNames == null ?
            null : (String[])writerSpiNames.clone();
    }
}
