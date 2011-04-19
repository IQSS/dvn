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
 * An abstract superclass for reading and writing of a statistical data file.
 * A class that implements a reader in the context of StatData I/O
 * framework must subclasse this superclass.
 *
 * @author akio sone
 */
public abstract class StatDataFileReader {

    /**
     * StatData I/O API version number as a <code>String</code>
     */
    public static String SDIO_VERSION = "0.1";


    /**
     * The <code>StatDataFileReaderSpi</code> object that instantiated this 
     * object, or  <code>null</code> if its identity is not known or none
     * exists.  By default it is initially set to <code>null</code>.
     */
    protected StatDataFileReaderSpi originatingProvider;

    /**
     * Constructs an <code>StatDataFileReader</code> and sets its 
     * <code>originatingProvider</code> field to the given value.
     * 
     * @param originatingProvider the <code>StatDataFileReaderSpi</code>
     * that invokes this constructor, or <code>null</code>.
     */
    protected StatDataFileReader(StatDataFileReaderSpi originatingProvider){
        this.originatingProvider = originatingProvider;
    }

    /**
     * Returns the <code>StatDataFileReaderSpi</code> that was supplied to the
     * the constructor. This value may be <code>null</code>.
     * 
     * @return <code>StatDataFileReaderSpi</code>, or <code>null</code>.
     */
    public StatDataFileReaderSpi getOriginatingProvider() {
        return originatingProvider;
    }
    
    /**
     * Returns a <code>String</code> that identifies the format of 
     * the input source
     * 
     * @return the format name as a <code>String</code>.
     * 
     * @throws java.io.IOException if a reading error occurs.
     */
    public String getFormatName() throws IOException {
        return originatingProvider.getFormatNames()[0];
    }
    
    /**
     * Releases any resources held by this instance.
     * 
     * <p>The current default implementation does not take any
     * action.
     */
    public void dispose() {
    
    }
    
    
    /**
     * Reads the statistical data file from a supplied
     * <code>BufferedInputStream</code> and 
     * returns its contents as a <code>SDIOData</code>.
     *
     * The second parameter, dataFile has been added to the method
     * declaration in for implementation by plugins that provide
     * 2 file ingest, with the data set metadata in one file
     * (for ex., SPSS control card) and the raw data in a separate
     * file (character-delimited, fixed-field, etc.)
     *
     * 
     * @param stream  a <code>BufferedInputStream</code>
     * where a statistical data file is connected.
     *
     * @param dataFile <code>File</code> optional parameter
     * representing the raw data file. For the plugins that only support
     * single file ingest, this should be set to null.
     *
     *
     * @return reading results as a <code>SDIOData</code>
     *
     * @throws java.io.IOException if a reading error occurs.
     */
    public abstract SDIOData read(BufferedInputStream stream, File dataFile)
        throws IOException;

    
    // should this be an abstract method as well? 
    
    public boolean isValid(File ddiFile) throws IOException {
        return false;
    }

    // Utility methods


    /**
     * Writes the contents of the given <code>byte</code> array 
     * as a hexadecimal string
     *
     * @param buff a <code>byte</code> array
     * @param hdr  a <code>String</code> line before the hexadecimal string
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
     * Returns a new null-character-free <code>String</code> object 
     * from an original <code>String</code> one that may contains
     * null characters.
     * 
     * @param rawString a<code>String</code> object
     * @return a new, null-character-free <code>String</code> object
     */
    protected String getNullStrippedString(String rawString){
        String nullRemovedString = null;
        int null_position = rawString.indexOf(0);
        if (null_position >= 0){
            // string is terminated by the null
            nullRemovedString = rawString.substring(0, null_position);
        } else {
            // not null-termiated (sometimes space-paddded, instead)
            nullRemovedString = rawString;
        }
        return nullRemovedString;
    }

}
