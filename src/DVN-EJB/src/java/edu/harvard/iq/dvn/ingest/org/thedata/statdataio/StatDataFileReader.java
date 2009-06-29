/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.metadata.*;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;
import java.io.*;
import static java.lang.System.*;

/**
 *
 * @author akio sone
 */
public abstract class StatDataFileReader {

    /**
     *
     */
    public static String SDIO_VERSION = "0.1";


    /**
     *
     */
    protected StatDataFileReaderSpi originatingProvider;

    /**
     *
     * @param originatingProvider
     */
    protected StatDataFileReader(StatDataFileReaderSpi originatingProvider){
        this.originatingProvider = originatingProvider;
    }

    /**
     *
     * @return
     */
    public StatDataFileReaderSpi getOriginatingProvider() {
        return originatingProvider;
    }
    
    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public String getFormatName() throws IOException {
        return originatingProvider.getFormatNames()[0];
    }
    /**
     *
     */
    public void dispose() {
    }
    
    
    /**
     *
     * @param stream
     * @return
     * @throws java.io.IOException
     */
    public SDIOData read(BufferedInputStream stream) throws IOException{
        SDIOData sd = null;
//        SDIOMetadata metadata = readHeader(stream);
//        readMetadata(stream, metadata);
//        SDIOData sd = readData(stream, metadata);
        return sd;
    }
    
//    public abstract SDIOMetadata readHeader(BufferedInputStream stream);
//
//    public abstract void readMetadata(BufferedInputStream stream, SDIOMetadata metadata);
//
//    public abstract SDIOData readData(BufferedInputStream stream, SDIOMetadata metadata);


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
            out.print(String.format("%02X ", buff[i]));
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

    /**
     * 
     * @param rawString
     * @return
     */
    protected String getNullStrippedString(String rawString){
        String nullRemovedString = null;
        int null_position = rawString.indexOf(0);
        if (null_position >= 0){
            // string is terminated by the null
            nullRemovedString = rawString.substring(0, null_position);
        } else {
            // not null-termiated (sometimes space-paddded, instead)
            // get up to the length
            nullRemovedString = rawString.substring(0, rawString.length());
        }
        return nullRemovedString;
    }

}
