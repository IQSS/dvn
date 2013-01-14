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
