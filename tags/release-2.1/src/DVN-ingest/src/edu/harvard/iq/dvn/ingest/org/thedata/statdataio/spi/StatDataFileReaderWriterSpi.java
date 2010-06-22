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

import java.nio.MappedByteBuffer;
import java.util.logging.*;
import static java.lang.System.*;

/**
 * A superclass that contains fields and methods common to 
 * <code>StatDataFileReaderSpi</code> and <code>StatDataFileWriterSpi</code>.
 * 
 * @author akio sone at UNC-Odum
 */
public abstract class StatDataFileReaderWriterSpi extends SDIOServiceProvider{

    private static Logger dbgLog = 
    Logger.getLogger(StatDataFileReaderWriterSpi.class.getPackage().getName());
    /**
     * A <code>String</code> array that contains human-readable format names
     * and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] names = null;

    /**
     * Gets the value of names.
     * @return the value of names.
     */
    public String[] getFormatNames() {
        return (String[])names.clone();
    }

    /**
     * A <code>String</code> array that contains format extensions 
     *  and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] suffixes = null;
    
    /**
     * Gets the value of suffixes
     *
     * @return the value of suffixes
     */
    public String[] getFileSuffixes() {
        return suffixes == null ? null : (String[])suffixes.clone();
    }
    
    
    /**
     * A <code>String</code> array that contains MIME types 
     * and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] MIMETypes = null;
    
    /**
     * Gets the value of MIMETypes
     *
     * @return the value of MIMETypes
     */
    public String[] getMIMETypes() {
        return MIMETypes == null ? null : (String[])MIMETypes.clone();
    }
    
    /**
     * A <code>String</code> that contains the name of the plug-in class.
     */
    protected String pluginClassName = null;

    /**
     * Gets the value of pluginClassName
     *
     * @return the value of pluginClassName
     */
    public String getPluginClassName() {
        return pluginClassName;
    }

    /**
     * Constructs an empty <code>StatDataFileReaderWriterSpi</code> instance.
     * The subclasses are to initialize instance fields and/or override
     * methods.
     */
    public StatDataFileReaderWriterSpi() {
    }

    /**
     * Constructs an empty <code>StatDataFileReaderWriterSpi</code> instance
     * with given values.
     * 
     * @param vendorName  the vendor name.
     * @param version     a version identifier.
     * @param names       at least one format name or more.
     * @param suffixes    at least one format extensions or more.
     * @param MIMETypes   at least one format's MIME type or more.
     * @param pluginClassName the fully qualified name of the associated
     * <code>StatDataFileReaderSpi</code> or 
     * <code>StatDataFileWriterSpi</code> class.
     */
    public StatDataFileReaderWriterSpi(
            String vendorName,
            String version,
            String[] names,
            String[] suffixes,
            String[] MIMETypes,
            String pluginClassName
            ) {
        super(vendorName, version);

        dbgLog.fine("StatDataFileReaderWriterSpi is called");

        if (names == null) {
            throw new IllegalArgumentException("names is null!");
        }

        if (names.length == 0) {
            throw new IllegalArgumentException("names.length is 0!");
        }
        this.names = (String[])names.clone();
        if (pluginClassName == null) {
            throw new IllegalArgumentException("pluginClassName is null!");
        }

        if (suffixes != null && suffixes.length > 0) {
            this.suffixes = (String[])suffixes.clone();
        }

        if (MIMETypes != null && MIMETypes.length > 0) {
            this.MIMETypes = (String[])MIMETypes.clone();
        }

        this.pluginClassName = pluginClassName;
    }

    /**
     * Writes a <code>MappedByteBuffer</code> object in hexadecimal.
     *
     * @param buff a MappedByteBuffer object.
     * @param hdr the title string.
     */
    public void printHexDump(MappedByteBuffer buff, String hdr) {
        int counter = 0;
        if (hdr != null) {
            out.println(hdr);
        }
        for (int i = 0; i < buff.capacity(); i++) {
            counter = i + 1;
            out.println(String.format("%02X ", buff.get()));
            if (counter % 16 == 0) {
                out.println();
            } else {
                if (counter % 8 == 0) {
                    out.print(" ");
                }
            }
        }
        out.println();
        buff.rewind();
    }
    /**
     * Writes the <code>byte</code> array in hexadecimal.
     *
     * @param buff a <code>byte</code> array.
     * @param hdr the title string.
     */
    public void printHexDump(byte[] buff, String hdr) {
        int counter = 0;
        if (hdr != null) {
            out.println(hdr);
        }
        for (int i = 0; i < buff.length; i++) {
            counter = i + 1;
            out.println(String.format("%02X ", buff[i]));
            if (counter % 16 == 0) {
                out.println();
            } else {
                if (counter % 8 == 0) {
                    out.print(" ");
                }
            }
        }
        out.println();
    }
}
