/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother;

import edu.harvard.iq.dvn.ingest.specialother.spi.*;
import java.io.*;
import java.util.Map; 
import java.util.Set; 

/**
 *
 * @author leonidandreev
 */
public abstract class FileIngester {
    /**
     * StatData I/O API version number as a <code>String</code>
     */
    public static String INGESTER_VERSION = "0.1";


    /**
     * The <code>FileIngesterSpi</code> object that instantiated this 
     * object, or  <code>null</code> if its identity is not known or none
     * exists.  By default it is initially set to <code>null</code>.
     */
    protected FileIngesterSpi originatingProvider;

    /**
     * Constructs an <code>FileIngester</code> and sets its 
     * <code>originatingProvider</code> field to the given value.
     * 
     * @param originatingProvider the <code>FileIngesterSpi</code>
     * that invokes this constructor, or <code>null</code>.
     */
    protected FileIngester(FileIngesterSpi originatingProvider){
        this.originatingProvider = originatingProvider;
    }

    /**
     * Returns the <code>FileIngesterSpi</code> that was supplied to the
     * the constructor. This value may be <code>null</code>.
     * 
     * @return <code>FileIngesterSpi</code>, or <code>null</code>.
     */
    public FileIngesterSpi getOriginatingProvider() {
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
     * Reads the supplied <code>BufferedInputStream</code> and 
     * returns the <code>Map</code> containing the extracted 
     * metadata.
     *
     * 
     * @param stream  a <code>BufferedInputStream</code>
     * where a statistical data file is connected.
     *
     * @return <code>Map</code>
     *
     * @throws java.io.IOException if a reading error occurs.
     */
    public abstract Map<String, Set<String>> ingest(BufferedInputStream stream)
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
            System.out.println(hdr);
        }
        for (int i = 0; i < buff.length; i++) {
            counter = i + 1;
            System.out.print(String.format("%02X ", buff[i]));
            if (counter % 16 == 0) {
                System.out.println();
            } else {
                if (counter % 8 == 0) {
                    System.out.print(" ");
                }
            }
        }
        System.out.println();
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
