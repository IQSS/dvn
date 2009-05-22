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
import java.util.logging.Logger;
import static java.lang.System.*;

/**
 *
 * @author akio sone
 */
public abstract class StatDataFileReaderWriterSpi extends SDIOServiceProvider{

    private static Logger dbgLog = Logger.getLogger(StatDataFileReaderWriterSpi.class.getPackage().getName());
    /**
     *
     */
    protected String[] names = null;
    /**
     *
     */
    protected String[] suffixes = null;
    /**
     *
     */
    protected String[] MIMETypes = null;
    /**
     * 
     */
    protected String pluginClassName = null;

    /**
     * Get the value of pluginClassName
     *
     * @return the value of pluginClassName
     */
    public String getPluginClassName() {
        return pluginClassName;
    }

    /**
     * Get the value of MIMETypes
     *
     * @return the value of MIMETypes
     */
    public String[] getMIMETypes() {
        return MIMETypes == null ? null : (String[])MIMETypes.clone();
    }

    /**
     * Get the value of suffixes
     *
     * @return the value of suffixes
     */
    public String[] getSuffixes() {
        return suffixes == null ? null : (String[])suffixes.clone();
    }


    /**
     *
     * @return
     */
    public String[] getFormatNames() {
        return (String[])names.clone();
    }

    /**
     *
     * @return
     */
    public String[] getFileSuffixes() {
        return suffixes == null ? null : (String[])suffixes.clone();
    }

    /**
     * Get the value of names
     *
     */
//    public String[] getNames() {
//        return (String[])names.clone();
//    }

    public StatDataFileReaderWriterSpi() {
    }

    /**
     *
     * @param vendorName
     * @param version
     * @param names
     * @param suffixes
     * @param MIMETypes
     * @param pluginClassName
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

        //out.println("StatDataFileReaderWriterSpi is called");

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
     * dump the data buffer in HEX
     *
         * @param buff
         * @param hdr 
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
     * dump the data buffer in HEX
     *
         * @param buff
         * @param hdr
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
