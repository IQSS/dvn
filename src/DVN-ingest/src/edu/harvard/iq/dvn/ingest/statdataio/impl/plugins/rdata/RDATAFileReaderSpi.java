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
package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.rdata;

import java.io.*;
import java.util.logging.*;
import java.util.Locale;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;

/*
 * @author Matt Owen
 */
public class RDATAFileReaderSpi extends StatDataFileReaderSpi{

  private static Logger LOG = Logger.getLogger(RDATAFileReaderSpi.class.getPackage().getName());

  private static String[] formatNames = {"Rdata", "rdata", "RDATA"};
  private static String[] extensions = {"Rdata", "rdata"};
  private static String[] mimeType = {"application/x-rlang-transport"};

  /*
   * Construct RDATAFileReaderSpi Object
   */
  public RDATAFileReaderSpi() {
    super("HU-IQSS-DVN-project", "0.1", formatNames, extensions, mimeType, RDATAFileReaderSpi.class.getName());
    LOG.fine(RDATAFileReaderSpi.class.getName()+" is called");
  }
  /*
   * Return the DVN Description of this File
   * @param locale ignored parameter
   * @return the description, which will always be "HU-IQSS-DVN-project RDATA"
   */
  public String getDescription(Locale locale) {
    return "HU-IQSS-DVN-project RDATA";
  }
  /*
   * Whether RDataFileReaderSpi Can Decode the Source Object
   * @param source a generic object
   * @return a boolean representing whether this input is decodable
   */
  @Override
  public boolean canDecodeInput(Object source) throws IOException {
    
    if (!(source instanceof BufferedInputStream))
      return false;

    return canDecodeInput((BufferedInputStream)source);
  }
  /*
   * Whether RDataFileReaderSpi Can Decode a Buffered Input Stream
   * @param source a buffered input stream
   * @return FALSE
   */
  @Override
  public boolean canDecodeInput(BufferedInputStream stream) throws IOException {
    return false;
  }
  /*
   * Whether RDataFileReaderSpi Can Decode a File Object
   * @param source a file object
   * @return TRUE
   */
  @Override
  public boolean canDecodeInput(File file) throws IOException {
    return true;
  }
  /*
   * Whether the fileIsValid
   * @param source a generic object
   * @return FALSE
   */
  public boolean fileIsValid() throws IOException {
    return false; 
  }
  /*
   * Create the File Reader Instance
   * @param ext an ignored parameter
   * @return an object of type <code>RDATAFileReader</code>
   */
  @Override
  public StatDataFileReader createReaderInstance(Object ext) throws IOException {
    return new RDATAFileReader(this);
  }
}