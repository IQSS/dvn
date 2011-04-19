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

package edu.harvard.iq.dvn.ingest.statdataio.impl.plugins.dta;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.logging.*;

import javax.imageio.IIOException;
import static java.lang.System.*;
import java.util.*;

import org.apache.commons.codec.binary.Hex;


/**
 *
 * @author akio sone at UNC-Odum
 */
public class DTAFileReaderSpi extends StatDataFileReaderSpi{

    private static Map<Byte, String> stataReleaseNumber = new HashMap<Byte, String>();

    static {
        stataReleaseNumber.put((byte) 104, "rel_3");
        stataReleaseNumber.put((byte) 105, "rel_4or5");
        stataReleaseNumber.put((byte) 108, "rel_6");
        stataReleaseNumber.put((byte) 110, "rel_7first");
        stataReleaseNumber.put((byte) 111, "rel_7scnd");
        stataReleaseNumber.put((byte) 113, "rel_8_or_9");
        stataReleaseNumber.put((byte) 114, "rel_10");
    }

    private static String[] formatNames = {"dta", "DTA"};
    private static String[] extensions = {"dta"};
    private static String[] mimeType = {"application/x-stata"};
    
    
    private static Logger dbgLog = Logger.getLogger(
            DTAFileReaderSpi.class.getPackage().getName());

    private static int DTA_HEADER_SIZE = 4;
    
    
    
    /**
     * 
     */
    public DTAFileReaderSpi() {
        super("HU-IQSS-DVN-project",
            "0.1",
            formatNames,
            extensions,
            mimeType,
            "edu.harvard.hmdc.vdcnet.util.statdataio.plugins.dta.DTAFileReaderSpi");
         dbgLog.fine("DTAFileReaderSpi is called");
    }

    /**
     * Returns the value of the description of the corresponding
     * DTAFileReader class.
     * 
     * @param locale
     * @return the value of the description of the corresponding
     * DTAFileReader class
     */
    public String getDescription(Locale locale) {
        return "HU-IQSS-DVN-project Stata File Reader";
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof BufferedInputStream)) {
            return false;
        }
        if (source ==null){
            throw new IllegalArgumentException("stream == null!");
        }
        BufferedInputStream stream = (BufferedInputStream)source;
        dbgLog.fine("applying the dta test\n");

        byte[] b = new byte[DTA_HEADER_SIZE];

        if (stream.markSupported()){
            stream.mark(0);
        }
        int nbytes = stream.read(b, 0, DTA_HEADER_SIZE);

        if (nbytes == 0){
            throw new IOException();
        }
        //printHexDump(b, "hex dump of the byte-array");

        if (stream.markSupported()){
            stream.reset();
        }

       dbgLog.info("hex dump: 1st 4bytes =>" +
                new String(Hex.encodeHex(b)) + "<-");

        if (b[2] != 1) {
            dbgLog.fine("3rd byte is not 1: given file is not stata-dta type");
            return false;
        } else if ((b[1] != 1) && (b[1] != 2)) {
            dbgLog.fine("2nd byte is neither 0 nor 1: this file is not stata-dta type");
            return false;
        } else if (!DTAFileReaderSpi.stataReleaseNumber.containsKey(b[0])) {
            dbgLog.fine("1st byte (" + b[0]+
                    ") is not within the ingestable range [rel. 3-10]:"+
                    "this file is NOT stata-dta type");
            return false;
        } else {
            dbgLog.fine("this file is stata-dta type: " +
                    DTAFileReaderSpi.stataReleaseNumber.get(b[0]) +
                    "(No in byte=" + b[0] + ")");
            return true;
        }
    }

    @Override
    public boolean canDecodeInput(BufferedInputStream stream) throws IOException {
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }

        dbgLog.fine("applying the dta test\n");

        byte[] b = new byte[DTA_HEADER_SIZE];
        
        if (stream.markSupported()){
            stream.mark(0);
        }
        int nbytes = stream.read(b, 0, DTA_HEADER_SIZE);

        if (nbytes == 0){
            throw new IOException();
        }
        //printHexDump(b, "hex dump of the byte-array");

        if (stream.markSupported()){
            stream.reset();
        }

        boolean DEBUG=false;
        
       dbgLog.info("hex dump: 1st 4bytes =>" +
                new String(Hex.encodeHex(b)) + "<-");

        if (b[2] != 1) {
            dbgLog.fine("3rd byte is not 1: given file is not stata-dta type");
            return false;
        } else if ((b[1] != 1) && (b[1] != 2)) {
            dbgLog.fine("2nd byte is neither 0 nor 1: this file is not stata-dta type");
            return false;
        } else if (!DTAFileReaderSpi.stataReleaseNumber.containsKey(b[0])) {
            dbgLog.fine("1st byte (" + b[0]+
                    ") is not within the ingestable range [rel. 3-10]:"+
                    "this file is NOT stata-dta type");
            return false;
        } else {
            dbgLog.fine("this file is stata-dta type: " +
                    DTAFileReaderSpi.stataReleaseNumber.get(b[0]) +
                    "(No in HEX=" + b[0] + ")");
            return true;
        }

    }


    @Override
    public boolean canDecodeInput(File file) throws IOException {
        if (file ==null){
            throw new IllegalArgumentException("file == null!");
        }
        if (!file.canRead()){
            throw new IIOException("cannot read the input file");
        }

        // set-up a FileChannel instance for a given file object
        FileChannel srcChannel = new FileInputStream(file).getChannel();

        // create a read-only MappedByteBuffer
        MappedByteBuffer buff = srcChannel.map(FileChannel.MapMode.READ_ONLY, 0, DTA_HEADER_SIZE);

        //printHexDump(buff, "hex dump of the byte-buffer");

        buff.rewind();

        boolean result = false;

        dbgLog.fine("applying the dta test\n");

        byte[] hdr4 = new byte[4];
        buff.get(hdr4, 0, 4);

       dbgLog.info("hex dump: 1st 4bytes =>" +
                new String(Hex.encodeHex(hdr4)) + "<-");

        if (hdr4[2] != 1) {
            dbgLog.fine("3rd byte is not 1: given file is not stata-dta type");
            return false;
        } else if ((hdr4[1] != 1) && (hdr4[1] != 2)) {
            dbgLog.fine("2nd byte is neither 0 nor 1: this file is not stata-dta type");
            return false;
        } else if (!stataReleaseNumber.containsKey(hdr4[0])) {
            dbgLog.fine("1st byte (" + hdr4[0] +
            ") is not within the ingestable range [rel. 3-10]: this file is NOT stata-dta type");
            return false;
        } else {
            dbgLog.fine("this file is stata-dta type: " +
                    stataReleaseNumber.get(hdr4[0]) +
                    "(No in HEX=" + hdr4[0] + ")");
            return true;
        }
    }
    
    @Override
    public StatDataFileReader createReaderInstance(Object ext) throws
        IIOException{
        return new DTAFileReader(this);
    }
}
