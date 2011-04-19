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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.ddi;

import java.io.*;
//import java.nio.*;
//import java.nio.channels.*;
import java.util.logging.*;
import java.util.Locale;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;

/**
 *
 * @author landreev
 */
public class DDIFileReaderSpi extends StatDataFileReaderSpi{

    private static Logger dbgLog = Logger.getLogger(DDIFileReaderSpi.class.getPackage().getName());

    private static String[] formatNames = {"ddi"};
    private static String[] extensions = {"xml", "ddi"};
    private static String[] mimeType = {"text/xml"};

    
    
    /**
     *
     */
    public DDIFileReaderSpi() {
        super(
            "HU-IQSS-DVN-project",
            "0.1",
            formatNames, extensions, mimeType, DDIFileReaderSpi.class.getName());
        dbgLog.fine(DDIFileReaderSpi.class.getName()+" is called");
    }

    /**
     * Returns the value of the description of the corresponding
     * DDIFileReader class.
     *
     * @param locale
     * @return the value of the description of the corresponding
     * DDIFileReader class
     */
    public String getDescription(Locale locale) {
        return "HU-IQSS-DVN-project DDI Control Card Reader";
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        dbgLog.fine("canDecodeInput(Object) method is called.");
        if (!(source instanceof BufferedInputStream)) {
            return false;
        }

        return canDecodeInput((BufferedInputStream)source);
     }


    @Override
    public boolean canDecodeInput(BufferedInputStream stream) throws IOException {
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        Boolean supported = true;

        return supported;
    }

    @Override
    public boolean canDecodeInput(File file) throws IOException {
        if (file ==null){
            throw new IllegalArgumentException("file == null!");
        }
        if (!file.canRead()){
            throw new IOException("cannot read the input file");
        }

        dbgLog.fine("skipping the validation test");

        return true;
    }

    public boolean fileIsValid() throws IOException {
        return false; 
    }


    @Override
    public StatDataFileReader createReaderInstance(Object ext) throws
        IOException {
        return new DDIFileReader(this);
    }

}
