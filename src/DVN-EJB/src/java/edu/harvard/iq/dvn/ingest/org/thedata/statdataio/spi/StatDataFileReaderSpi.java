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
import java.util.logging.*;
import java.io.*;

/**
 * The service provider interface (SPI) for <code>StatDataFileReader</code>.
 * This abstract class supplies several types of information about the associated
 * <code>StatDataFileReader</code> class.
 * 
 * @author akio sone at UNC-Odum
 */
public abstract class StatDataFileReaderSpi extends StatDataFileReaderWriterSpi{

    private static Logger dbgLog = 
    Logger.getLogger(StatDataFileReaderSpi.class.getPackage().getName());

    /**
     * A <code>String</code> array of the fully qualified names of 
     * all the <code>StatDataFileWriterSpi</code> classes associated with this 
     * <code>StatDataFileReader</code> class
     */
    protected String[] writerSpiNames = null;

    /**
     * Gets the value of the writerSpiNames field.
     * 
     * @return the value of the writerSpiNames field.
     */
    public String[] getStatDataFileWriterSpiNames() {
        return writerSpiNames == null ?
            null : (String[])writerSpiNames.clone();
    }
    
    /**
     * Constructs an empty <code>StatDataFileReaderSpi</code> instance.
     */
    protected StatDataFileReaderSpi() {
    }

    /**
     * Constructs a <code>StatDataFileReaderSpi</code> instance with a given
     * set of values.
     *
     * @param vendorName the vendor name.
     * @param version    a version identifier.
     * @param names      at least one format name or more.
     * @param suffixes   at least one format extensions or more.
     * @param MIMETypes  at least one format's MIME type or more.
     * @param readerClassName the fully qualified name of the associated
     *                        <code>StatDataFileReaderSpi</code>.
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
        dbgLog.fine("StatDataFileReaderSpi is called");
    }
    
    /**
     * Returns true if the supplied source object starts with a sequence of bytes
     * that is of the format supported by this reader.  Returning true from this
     * method does not guarantee that reading will successfully end.
     * 
     * @param source    typically a <code>BufferedInputStream</code> object.
     *                  object to be read.
     * @return          true if the stream can be read.
     * @throws java.io.IOException if an I/O error occurs
     *                             during reading the stream.
     */
    public abstract boolean canDecodeInput(Object source) throws IOException;
    
    
    /**
     * Returns true if the supplied <code>File</code> object starts with 
     * a sequence of bytes that is of the format supported by this reader.
     * Returning true from this method does not guarantee that reading will 
     * successfully end.
     * 
     * @param file  a <code>File</code> object to be read.
     * @return      true if the stream can be read.
     * @throws java.io.IOException if an I/O error occurs
     * during reading the stream.
     */
    public abstract boolean canDecodeInput(File file) throws IOException;

    /**
     * Returns true if the supplied <code>BufferedInputStream</code> object
     * starts with a sequence of bytes that is of the format 
     * supported by this reader.
     * Returning true from this method does not guarantee that reading will 
     * successfully end.
     * 
     * @param stream a <code>BufferedInputStream</code> object.
     * @return       true if the stream can be read.
     * @throws java.io.IOException  if an I/O error occurs
     * during reading the stream.
     */
    public abstract boolean canDecodeInput(BufferedInputStream stream) throws IOException;
    
    /**
     * Returns an instance of <code>StatDataFileReader</code> implementation associated with
     * this service provider.
     * 
     * @param extension     a plug-in specific extension object.
     * @return              a <code>StatDataFileReader</code> instance.
     * @throws IOException  if the instantiation attempt of the reader fails.
     */
    public abstract StatDataFileReader createReaderInstance(Object extension)
        throws IOException;
    
    /**
     * Returns an instance of <code>StatDataFileReader</code> implementation 
     * associated witht his service provider.
     *
     * @return  a <code>StatDataFileReader</code> instance.
     * @throws java.io.IOException if an error occurs during the 
     *         reader instantiation.
     */
    public StatDataFileReader createReaderInstance() throws IOException{
        return createReaderInstance(null);
    }
    
    /**
     * Returns <code>true</code> if the <code>StatDataFileReader</code> object
     * supplied in is an instance of the <code>StatDataFileReader</code>
     * associated with this service provider.
     * 
     * @param reader  an <code>StatDataFileReader</code> object.
     * @return <code>true</code> if <code>reader</code> is recognized.
     */
    public boolean isOwnReader(StatDataFileReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        String name = reader.getClass().getName();
        return name.equals(pluginClassName);
    }

}
