/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother.impl.plugins.fits;
import edu.harvard.iq.dvn.ingest.specialother.*;
import edu.harvard.iq.dvn.ingest.specialother.spi.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.logging.*;
import java.util.Locale;

import static java.lang.System.*;

import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author leonidandreev
 */
public class FITSFileIngesterSpi extends FileIngesterSpi{
    private static Logger dbgLog = Logger.getLogger(FITSFileIngesterSpi.class.getPackage().getName());
    private static int FITS_HEADER_SIZE = 10;
    private static String FITS_FILE_SIGNATURE = "SIMPLE  = ";

    private static String[] formatNames = {"fits", "fits"};
    private static String[] extensions = {"fits"};
    private static String[] mimeType = {"application/fits", "image/fits"};

    
    
    /**
     *
     */
    public FITSFileIngesterSpi() {
        super(
            "HU-IQSS-DVN-project",
            "0.1",
            formatNames, extensions, mimeType, FITSFileIngesterSpi.class.getName());
        dbgLog.fine(FITSFileIngesterSpi.class.getName()+" is called");
    }

    
    public String getDescription(Locale locale) {
        return "HU-IQSS-DVN-project FITS File Ingester";
    }

    // (of the canDecodeInput methods below, the BufferedInputStream-based
    // one should be used);
    
    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        out.println("this method is actually called: object");
        if (!(source instanceof BufferedInputStream)) {
            return false;
        } else if (source instanceof File){
            out.println("source is a File object");
        } else {
            out.println("not File object");
        }
        if (source  == null){
            throw new IllegalArgumentException("source == null!");
        }
        
        BufferedInputStream stream = (BufferedInputStream)source;

        //
        
        return true; 
    }


    @Override
    public boolean canDecodeInput(BufferedInputStream stream) throws IOException {
        if (stream ==null){
            throw new IllegalArgumentException("stream == null!");
        }

        byte[] b = new byte[FITS_HEADER_SIZE];
        
        if (stream.markSupported()){
            stream.mark(0);
        }
        int nbytes = stream.read(b, 0, FITS_HEADER_SIZE);

        if (nbytes == 0){
            throw new IOException();
        }
        //printHexDump(b, "hex dump of the byte-array");
        dbgLog.info("hex dump of the 1st "+FITS_HEADER_SIZE+" bytes:"+
                (new String (Hex.encodeHex(b))).toUpperCase());


        if (stream.markSupported()){
            stream.reset();
        }

        boolean DEBUG = false;

        String hdr4fits = new String(b);


        if (hdr4fits.equals(FITS_FILE_SIGNATURE)) {
            dbgLog.fine("this is a fits file");
            return true;
        } else {
            dbgLog.fine("this is NOT a fits file");
            return false;
        }
    }

    @Override
    public boolean canDecodeInput(File file) throws IOException {
        if (file ==null){
            throw new IllegalArgumentException("file == null!");
        }
        if (!file.canRead()){
            throw new IOException("cannot read the input file");
        }

        return true;
    }


    @Override
    public FileIngester createIngesterInstance(Object ext) throws
        IOException {
        return new FITSFileIngester(this);
    }   
}
