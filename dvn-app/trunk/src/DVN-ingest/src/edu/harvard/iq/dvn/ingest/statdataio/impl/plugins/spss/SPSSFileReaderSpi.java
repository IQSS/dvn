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

    public boolean fileIsValid() throws IOException {
        return false; 
    }


    @Override
    public StatDataFileReader createReaderInstance(Object ext) throws
        IOException {
        return new SPSSFileReader(this);
    }

}
