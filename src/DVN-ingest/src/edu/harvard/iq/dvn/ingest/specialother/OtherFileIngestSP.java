/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.SDIORegistry;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.ServiceRegistry;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;
import static java.lang.System.*;

import edu.harvard.iq.dvn.ingest.specialother.*;
import edu.harvard.iq.dvn.ingest.specialother.spi.*;

/**
 *
 * @author leonidandreev
 */
public final class OtherFileIngestSP {
    
    
    private static Method ingesterMIMETypesMethod;
    
    static {
        try {
            ingesterMIMETypesMethod =
                FileIngesterSpi.class.getMethod("getMIMETypes");

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * The static registry serves as a singleton that prevents
     * duplicated loading of plug-in classes.  The singleton 
     * attribute of <code>theRegistry</code> is implemented by the
     * <code>edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.Regsitry</code> class.
     * 
     */
    private final static SDIORegistry theRegistry =
        SDIORegistry.getDefaultInstance();

    /**
     * Constructor is private to avoid instantiation.
     */
    private OtherFileIngestSP() {
    }
    
    private static enum SpiInfo {
        
        MIME_TYPES {
            @Override
            String[] info(FileIngesterSpi spi) {
                return spi.getMIMETypes();
            }
        };

        abstract String[] info(FileIngesterSpi spi);
    }
    
    public static Iterator<FileIngester>
        getFileIngestersByMIMEType(String MIMEType){
        if (MIMEType == null) {
            throw new IllegalArgumentException("MIMEType == null!");
        }
        
        Iterator iter;
        try {
            iter = theRegistry.getServiceProviders(FileIngesterSpi.class,
                                      new ContainsFilter(ingesterMIMETypesMethod,
                                                         MIMEType),
                                              true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new FileIngesterIterator(iter);
    }
    
    static class ContainsFilter implements ServiceRegistry.Filter {

        Method method;
        String name;

        // method returns an array of Strings
        public ContainsFilter(Method method,
                              String name) {
            this.method = method;
            this.name = name;
        }

        public boolean filter(Object elt) {
            try {
                return contains((String[])method.invoke(elt), name);
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    private static boolean contains(String[] names, String name) {
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i])) {
                return true;
            }
        }

        return false;
    }
    
    static class FileIngesterIterator implements Iterator<FileIngester> {
        // Contains StatDataFileReaderSpis
        public Iterator iter;

        public FileIngesterIterator(Iterator iter) {
            this.iter = iter;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public FileIngester next() {
            FileIngesterSpi spi = null;
            try {
                spi = (FileIngesterSpi)iter.next();
                //dbgLog.fine("spi(StatDataFileReaderIterator:next())="+spi.getClass().getName());
                return spi.createIngesterInstance();
            } catch (IOException e) {
                // Deregister the spi in this case, but only as
                // a StatDataFileReaderSpi
                theRegistry.deregisterServiceProvider(spi, FileIngesterSpi.class);
                //dbgLog.fine("deregistering="+spi.getClass().getName());
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
