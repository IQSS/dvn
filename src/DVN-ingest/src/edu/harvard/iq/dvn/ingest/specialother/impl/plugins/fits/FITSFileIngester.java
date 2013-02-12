/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother.impl.plugins.fits;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import edu.harvard.iq.dvn.ingest.specialother.*;
import edu.harvard.iq.dvn.ingest.specialother.spi.*;

/**
 *
 * @author leonidandreev
 */
public class FITSFileIngester extends FileIngester {
    
    /**
     * Constructs a <code>FITSFileIngester</code> instance with a 
     * <code>FITSFileIngesterSpi</code> object.
     * 
     * @param originator a <code>FITSFileIngesterSpi</code> object.
     */
    public FITSFileIngester(FileIngesterSpi originator) {
        super(originator);
        //init();
    }
    
    public FITSFileIngester() {
        super(null); 
    }
           
    public void ingest (BufferedInputStream stream) throws IOException{
        
    }
    
    public static void main(String[] args) {
        BufferedInputStream fitsStream = null;
        
        String fitsFile = args[0]; 
        
        try {
           fitsStream = new BufferedInputStream(new FileInputStream(fitsFile)); 
           
           FITSFileIngester fitsIngester = new FITSFileIngester();
           
           fitsIngester.ingest(fitsStream); 
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
}
