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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.spss;

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
public class SPSSFileReaderSpi extends StatDataFileReaderSpi{

    private static Logger dbgLog = Logger.getLogger(SPSSFileReaderSpi.class.getPackage().getName());

    private static String[] formatNames = {"spss", "SPSS"};
    private static String[] extensions = {"spss", "sps"};
    private static String[] mimeType = {"text/plain"};

    
    
    /**
     *
     */
    public SPSSFileReaderSpi() {
        super(
            "HU-IQSS-DVN-project",
            "0.1",
            formatNames, extensions, mimeType, SPSSFileReaderSpi.class.getName());
        dbgLog.fine(SPSSFileReaderSpi.class.getName()+" is called");
    }

    /**
     * Returns the value of the description of the corresponding
     * SPSSFileReader class.
     *
     * @param locale
     * @return the value of the description of the corresponding
     * SPSSFileReader class
     */
    public String getDescription(Locale locale) {
        return "HU-IQSS-DVN-project SPSS Control Card Reader";
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

        dbgLog.fine("\napplying the spss card test: inputstream case\n");

        if (stream.markSupported()){
            stream.mark(0);
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        Boolean supported = false;

        while ((line = rd.readLine()) != null && (supported == false)) {

            if (line.matches("(?i)^\\s*data\\s*list\\s*list\\(")) {
                // This looks like a valid SPSS card for a character-delimited
                // data file. This plugin does NOT support other kinds of
                // data files such as fixed-field, as of yet.
                dbgLog.fine("found valid-looking datalist command: "+line);
                supported = true;
            }

        }

        if (stream.markSupported()){
            stream.reset();
        }
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

        dbgLog.fine("skipping the spss test\n");

        return true;
    }


    @Override
    public StatDataFileReader createReaderInstance(Object ext) throws
        IOException {
        return new SPSSFileReader(this);
    }

}
