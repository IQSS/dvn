/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother.impl.plugins.fits;

import edu.harvard.iq.dvn.ingest.specialother.*;
import edu.harvard.iq.dvn.ingest.specialother.spi.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException; 
import java.util.HashMap;
import java.util.Map;
import java.util.Set; 
import java.util.HashSet;
import java.util.logging.Logger;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Data;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.fits.ImageHDU;
import nom.tam.fits.TableHDU;
import nom.tam.fits.UndefinedHDU;

/**
 *
 * @author leonidandreev
 */
public class FITSFileIngester extends FileIngester {
    private static Logger dbgLog = Logger.getLogger(FITSFileIngester.class.getPackage().getName());
    
    private static final Map<String, Integer> recognizedFitsMetadataKeys = new HashMap<String, Integer>();
    // the integer value in the map is reserved for the type of the metadata 
    // keyword; it's not being used as of now. 
    
    static {
        recognizedFitsMetadataKeys.put("DATE", 1);
        recognizedFitsMetadataKeys.put("DATE-OBS", 1); 
        recognizedFitsMetadataKeys.put("ORIGIN", 1);
        recognizedFitsMetadataKeys.put("AUTHOR", 1);
        recognizedFitsMetadataKeys.put("REFERENC", 1);
        recognizedFitsMetadataKeys.put("COMMENT", 1);
        recognizedFitsMetadataKeys.put("HISTORY", 1);
        recognizedFitsMetadataKeys.put("OBSERVER", 1);
        recognizedFitsMetadataKeys.put("TELESCOP", 1);
        recognizedFitsMetadataKeys.put("INSTRUME", 1);
        recognizedFitsMetadataKeys.put("EQUINOX", 1);
        recognizedFitsMetadataKeys.put("EXTNAME", 1);

        
    }
    
    private static final Map<String, Integer> recognizedFitsColumnKeys = new HashMap<String, Integer>();
    // these are the column-level metadata keys; these are defined as XXXXn in 
    // the "FITS Standard, Appendix C" document; for example, "TTYPEn", meaning 
    // that the Header section of the table HDU will contain the keys TTYPE1, 
    // TTYPE2, ... TTYPEN - where N is the number of columns. 
    
    static {
        recognizedFitsColumnKeys.put("TTYPE", 1);
        recognizedFitsColumnKeys.put("TCOMM", 1);
        recognizedFitsColumnKeys.put("TUCD", 1);

    }
    
    private static final Map<String, String> indexableFitsMetaKeys= new HashMap<String, String>(); 
    // This map defines the names of the keys under which they will be indexed
    // and made searchable in the application
    
    static {
        indexableFitsMetaKeys.put("DATE", "Date");
        indexableFitsMetaKeys.put("DATE-OBS", "Observation-Date");
        indexableFitsMetaKeys.put("ORIGIN", "Origin");
        indexableFitsMetaKeys.put("AUTHOR", "Author");
        indexableFitsMetaKeys.put("REFERENC", "Reference");
        indexableFitsMetaKeys.put("COMMENT", "Comment");
        indexableFitsMetaKeys.put("HISTORY", "History");
        indexableFitsMetaKeys.put("OBSERVER", "Observer");
        indexableFitsMetaKeys.put("TELESCOP", "Telescope");
        indexableFitsMetaKeys.put("INSTRUME", "Instrument");
        indexableFitsMetaKeys.put("EQUINOX", "Equinox");
        indexableFitsMetaKeys.put("EXTNAME", "Extension-Name");
        indexableFitsMetaKeys.put("TTYPE", "Column-Label");
        indexableFitsMetaKeys.put("TCOMM", "Column-Comment");
        indexableFitsMetaKeys.put("TUCD", "Column-UCD");
    }
    
    /**
     * Constructs a <code>FITSFileIngester</code> instance with a 
     * <code>FITSFileIngesterSpi</code> object.
     * 
     * @param originator a <code>FITSFileIngesterSpi</code> object.
     */
    public FITSFileIngester(FileIngesterSpi originator) {
        super(originator);
    }
    
    public FITSFileIngester() {
        super(null); 
    }
           
    public Map<String, Set<String>> ingest (BufferedInputStream stream) throws IOException{
        dbgLog.fine("Attempting to read FITS file;");
        
        Map<String, Set<String>> fitsMetaMap = new HashMap<String, Set<String>>();
        
        Fits fitsFile = null; 
        try {
            fitsFile = new Fits (stream);
        } catch (FitsException fEx) {
            throw new IOException ("Failed to open FITS stream; "+fEx.getMessage());
        }
        
        if (fitsFile == null) {
            throw new IOException ("Failed to open FITS stream; null Fits object");
        }
              
        int n = fitsFile.getNumberOfHDUs(); 
        
        dbgLog.fine("Total number of HDUs: "+n);
        
        BasicHDU hdu = null;
        int i = 0; 
        
        try {

            while ((hdu = fitsFile.readHDU()) != null) {
                dbgLog.fine("reading HDU number " + i);
                
                if (hdu instanceof TableHDU) {
                    dbgLog.fine("this is a table HDU");
                } else if (hdu instanceof ImageHDU) {
                    dbgLog.fine("this is an image HDU");
                } else if (hdu instanceof UndefinedHDU) {
                    dbgLog.fine("this is an undefined HDU");
                } else {
                    dbgLog.fine("this is an UKNOWN HDU");
                }
                               
                i++;

                Header hduHeader = hdu.getHeader();
                HeaderCard headerCard = null;

                int j = 0;
                while ((headerCard = hduHeader.nextCard()) != null) {

                    String headerKey = headerCard.getKey();
                    String headerValue = headerCard.getValue();
                    String headerComment = headerCard.getComment();

                    boolean recognized = false; 
                    
                    if (headerKey != null) {
                        if (isRecognizedKey(headerKey)) {
                            dbgLog.fine("recognized key: " + headerKey);
                            recognized = true; 
                        } else if (isRecognizedColumnKey(headerKey)) {
                            dbgLog.fine("recognized column key: " + headerKey);
                            recognized = true;
                        }
                    } 
                    
                    if (recognized) {

                        String indexableKey = 
                                getIndexableMetaKey(headerKey) != null ? 
                                getIndexableMetaKey(headerKey) : 
                                headerKey; 
                        
                        if (headerValue != null) {
                            dbgLog.fine("value: " + headerValue);
                            if (fitsMetaMap.get(indexableKey) == null) {
                                fitsMetaMap.put(indexableKey, new HashSet<String>());
                            } 
                            fitsMetaMap.get(indexableKey).add(headerValue); 

                        } else if (headerKey.equals("COMMENT") && headerComment != null) {
                            dbgLog.fine("comment: " + headerComment);
                            if (fitsMetaMap.get(indexableKey) == null) {
                                fitsMetaMap.put(indexableKey, new HashSet<String>());
                            } 
                            fitsMetaMap.get(indexableKey).add(headerComment);
                        } else {
                            dbgLog.fine("value is null");
                        }

                        /*
                         * TODO:
                         * decide what to do with regular key comments:
                         
                        if (headerComment != null) {
                            dbgLog.fine("comment: " + headerComment);
                        } else {
                            dbgLog.fine("comment is null");
                        }
                        * */
                    }
                    j++;
                }
                dbgLog.fine ("processed "+j+" cards total;");
                
                Data fitsData = hdu.getData(); 
                
                dbgLog.fine ("data size: "+fitsData.getSize());
                dbgLog.fine("total size of the HDU is "+hdu.getSize());
                               
            }

        } catch (FitsException fEx) {
            throw new IOException("Failed to read HDU number " + i);
        }
            
        dbgLog.fine ("processed "+i+" HDUs total;");
        
        n = fitsFile.getNumberOfHDUs(); 
        
        dbgLog.fine("Total (current) number of HDUs: "+n);
        
        return fitsMetaMap; 
    }
    
    private boolean isRecognizedKey (String key) {
        if (recognizedFitsMetadataKeys.containsKey(key)) {
            return true;
        }
        return false; 
    }
    
    private boolean isRecognizedColumnKey (String key) {
        if (key.matches(".*[0-9]$")) {
            String trimmedKey = key.replaceFirst("[0-9][0-9]*$", "");
            if (recognizedFitsColumnKeys.containsKey(trimmedKey)) {
                return true; 
            }
        }
        return false; 
    }
    
    private String getIndexableMetaKey (String key) {
        String indexableKey = null; 
        
        if (isRecognizedKey(key)) {
            indexableKey = indexableFitsMetaKeys.get(key);
        } else if (isRecognizedColumnKey(key)) {
            indexableKey = indexableFitsMetaKeys.get(key.replaceFirst("[0-9][0-9]*$", ""));
        }
        
        return indexableKey; 
    }
    
    
    /**
     * main() method, for testing
     * usage: java edu.harvard.iq.dvn.ingest.specialother.impl.plugins.fits.FITSFileIngester testfile.fits
     * make sure the CLASSPATH contains fits.jar
     * 
     */
    
    public static void main(String[] args) {
        BufferedInputStream fitsStream = null;
        
        String fitsFile = args[0]; 
        Map<String, Set<String>> fitsMetadata = null; 
        
        try {
           fitsStream = new BufferedInputStream(new FileInputStream(fitsFile)); 
           
           FITSFileIngester fitsIngester = new FITSFileIngester();
           
           fitsMetadata = fitsIngester.ingest(fitsStream); 
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        for (String mKey : fitsMetadata.keySet()) {
            
            Set<String> mValues = fitsMetadata.get(mKey); 
            System.out.println("key: " + mKey);
            
            if (mValues != null) {
                for (String mValue : mValues) {
                    if (mValue != null) {
                        System.out.println("value: " + mValue);               
                    } else {
                        System.out.println("value is null");
                    }
                }   
            }
        }
    }
}
