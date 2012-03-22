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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;
import static java.lang.System.*;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;

/**
 * An entry-point class of the StatData I/O API and contains
 * static convenience methods that locate <code>StatDataFileReader</code>s and
 * <code>StatDataFileWriter</code>s, and read and write a statistical data file.
 * 
 * @author akio sone at UNC-Odum
 * @version $Revision$($Date$)
 */
public final class StatDataIO {

    private static Logger dbgLog = Logger.getLogger(StatDataIO.class.getPackage().getName());

    /**
     * The static registry serves as a singleton that prevents
     * duplicated loading of plug-in classes.  The singleton 
     * attribute of <code>theRegistry</code> is implemented by the
     * <code>edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.Regsitry</code> class.
     */
    private static final SDIORegistry theRegistry =
        SDIORegistry.getDefaultInstance();
        
        
    /**
     * Scans for StatDataIO-plug-ins on the application class path, 
     * loads their service provider classes, and registers
     * a service provider instance for each of those found with the 
     * <code>SDIORegistry</code>.
     * 
     * The application class path is scanned only on the first invocation
     * of this API and theRegistry is a singleton.
     */
    public static void scanForPlugins() {
        theRegistry.registerApplicationClasspathSpis();
    }
    /**
     * Constructor is private to avoid instantiation.
     */
    private StatDataIO() {
    }



    private static enum SpiInfo {
        FORMAT_NAMES {
            @Override
            String[] info(StatDataFileReaderWriterSpi spi) {
                return spi.getFormatNames();
            }
        },
        MIME_TYPES {
            @Override
            String[] info(StatDataFileReaderWriterSpi spi) {
                return spi.getMIMETypes();
            }
        },
        FILE_SUFFIXES {
            @Override
            String[] info(StatDataFileReaderWriterSpi spi) {
                return spi.getFileSuffixes();
            }
        };

        abstract String[] info(StatDataFileReaderWriterSpi spi);
    }



    private static <S extends StatDataFileReaderWriterSpi>
        String[] getReaderWriterInfo(Class<S> spiClass, SpiInfo spiInfo){
        // Ensure category is present
        Iterator<S> iter;
        try {
            iter = theRegistry.getServiceProviders(spiClass, true);
        } catch (IllegalArgumentException e) {
            return new String[0];
        }

        HashSet<String> s = new HashSet<String>();
        while (iter.hasNext()) {
            StatDataFileReaderWriterSpi spi = iter.next();
            Collections.addAll(s, spiInfo.info(spi));
        }

        return s.toArray(new String[s.size()]);
    }

    // Readers

    /**
     * Returns a <code>String</code> array that lists all of
     * the format names understood by 
     * the currently registered readers
     *
     * @return a <code>String</code> array
     */
    public static String[] getReaderFormatNames() {
        return getReaderWriterInfo(StatDataFileReaderSpi.class, SpiInfo.FORMAT_NAMES);
    }

    /**
     * Returns a <code>String</code> array that lists all of
     * the MIME types understood by 
     * the currently registered readers
     *
     * @return a <code>String</code> array
     */
    public static String[] getReaderMIMETypes() {
        return getReaderWriterInfo(StatDataFileReaderSpi.class, SpiInfo.MIME_TYPES);
    }

    /**
     * Returns a <code>String</code> array that lists all of
     * the file extensions
     * associated with the formats understood by 
     * the currently registered readers
     *
     * @return a <code>String</code> array
     */
    public static String[] getReaderFileSuffixes() {
        return getReaderWriterInfo(StatDataFileReaderSpi.class, SpiInfo.FILE_SUFFIXES);
    }



    static class StatDataFileReaderIterator implements Iterator<StatDataFileReader> {
        // Contains StatDataFileReaderSpis
        public Iterator iter;

        public StatDataFileReaderIterator(Iterator iter) {
            this.iter = iter;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public StatDataFileReader next() {
            StatDataFileReaderSpi spi = null;
            try {
                spi = (StatDataFileReaderSpi)iter.next();
                dbgLog.fine("spi(StatDataFileReaderIterator:next())="+spi.getClass().getName());
                return spi.createReaderInstance();
            } catch (IOException e) {
                // Deregister the spi in this case, but only as
                // a StatDataFileReaderSpi
                theRegistry.deregisterServiceProvider(spi, StatDataFileReaderSpi.class);
                dbgLog.fine("deregistering="+spi.getClass().getName());
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    static class CanDecodeInputFilter implements ServiceRegistry.Filter {

        Object input;

        public CanDecodeInputFilter(Object input) {
            this.input = input;
        }

        public boolean filter(Object elt) {
                BufferedInputStream stream = null;
                File infile = null;        
                boolean canDecode = false;
            try {
                StatDataFileReaderSpi spi = (StatDataFileReaderSpi)elt;

                if (input instanceof BufferedInputStream) {
                    dbgLog.fine("bis object case");
                    stream = (BufferedInputStream)input;

                    if (stream != null) {
                        if (stream.markSupported()){
                            stream.mark(0);
                        }
                    }
                    dbgLog.fine("going canDecode:"+spi.getClass().getName());

                    canDecode = spi.canDecodeInput(stream);

                    dbgLog.fine(spi.getClass().getName()+": canDecode="+canDecode);

                    if (stream != null) {
                        if (stream.markSupported()){
                            stream.reset();
                        }
                    }
                    dbgLog.fine("canDecode result="+canDecode);

                } else if (input instanceof File){
                    dbgLog.fine("file object case");
                    infile = (File) input;
                    dbgLog.fine("going canDecode:"+spi.getClass().getName());

                    canDecode = spi.canDecodeInput(infile);

                    dbgLog.fine(spi.getClass().getName()+": canDecode="+canDecode);
                    
                }
                    return canDecode;
                
            } catch (IOException e) {
                return false;
            }
        }
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




    /**
     * Returns an <code>Iterator</code> that contains all of the 
     * currently registered <code>StatDataFileReader</code>s that
     * claim to read the supplied <code>Object</code>,
     * usually <code>BufferedInputStream</code>
     *
     * <p>The position of the stream is set back to the initial position
     * upon exit from this method
     * 
     * @param input a <code>BufferedInputStream</code> or other
     * <code>Object</code> that contains statistical data.
     *
     * @return an <code>Iterator</code> that contains
     * <code>StatDataFileReader</code>
     *
     * @exception IllegalArgumentException if <code>input</code> is
     * <code>null</code>.
     * 
     */
    public static Iterator<StatDataFileReader> getStatDataFileReaders(Object input) {
        dbgLog.fine("********** within getStatDataFileReaders **********");
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }
        Iterator iter;

        try {
            iter = theRegistry.getServiceProviders(StatDataFileReaderSpi.class,
                                              new CanDecodeInputFilter(input),
                                              true);

                if (iter.hasNext()){
                    dbgLog.fine("has next yes");
                } else {
                    dbgLog.fine("has next no");
                }

            dbgLog.fine("iter="+iter.getClass().getName());
        } catch (IllegalArgumentException e) {
            return  new HashSet().iterator();
        }

        return new StatDataFileReaderIterator(iter);
    }

    private static Method readerFormatNamesMethod;
    private static Method readerFileSuffixesMethod;
    private static Method readerMIMETypesMethod;
    private static Method writerFormatNamesMethod;
    private static Method writerFileSuffixesMethod;
    private static Method writerMIMETypesMethod;

    static {
        try {
            readerFormatNamesMethod =
                StatDataFileReaderSpi.class.getMethod("getFormatNames");
            readerFileSuffixesMethod =
                StatDataFileReaderSpi.class.getMethod("getFileSuffixes");
            readerMIMETypesMethod =
                StatDataFileReaderSpi.class.getMethod("getMIMETypes");

            writerFormatNamesMethod =
                StatDataFileWriterSpi.class.getMethod("getFormatNames");
            writerFileSuffixesMethod =
                StatDataFileWriterSpi.class.getMethod("getFileSuffixes");
            writerMIMETypesMethod =
                StatDataFileWriterSpi.class.getMethod("getMIMETypes");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns an <code>Iterator</code> that contains all of the
     * currently registered <code>StatDataFileReader</code>s that
     * claims to read the named format.
     *
     * @param formatName a <code>String</code> that contains the 
     * conventional name of a format (<i>e.g.</i>, "dta" or "sav").
     
     * @return an <code>Iterator</code> that contains 
     * <code>StatDataFileReader</code>s.
     * 
     * @exception IllegalArgumentException if <code>formatName</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersByFormatName(String formatName){
        if (formatName == null) {
            throw new IllegalArgumentException("formatName == null!");
        }
        Iterator iter;
        // Ensure category is present
        try {
            iter = theRegistry.getServiceProviders(StatDataFileReaderSpi.class,
                                    new ContainsFilter(readerFormatNamesMethod,
                                                       formatName),
                                                true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new StatDataFileReaderIterator(iter);
    }


    /**
     * Returns an <code>Iterator</code> that contains all of the 
     * currently registered <code>StatDataFileReader</code>s that
     * claim to read files with the given file extension.
     * 
     * @param fileSuffix a <code>String</code> that contains a file
     * extension (<i>e.g.</i>, "dta" or "sav").
     *
     * @return an <code>Iterator</code> that contains 
     * <code>StatDataFileReader</code>s.
     * 
     * @exception IllegalArgumentException if <code>fileSuffix</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersBySuffix(String fileSuffix){
        if (fileSuffix == null) {
            throw new IllegalArgumentException("fileSuffix == null!");
        }
        // Ensure category is present
        Iterator iter;
        try {
            iter = theRegistry.getServiceProviders(StatDataFileReaderSpi.class,
                                   new ContainsFilter(readerFileSuffixesMethod,
                                                      fileSuffix),
                                              true);
        } catch (IllegalArgumentException e) {
            return  new HashSet().iterator();
        }
        return new StatDataFileReaderIterator(iter);
    }




    /**
     * Returns an <code>Iterator</code> that contains all of the
     * currently registered <code>StatDataFileReader</code>s that
     * claim to read files with the given MIME type.
     *
     * @param MIMEType a <code>String</code> that contains a MIME-
     * type string (<i>e.g.</i>, "application/x-stata" or
     * "application/x-spss-sav").
     * 
     * @return an <code>Iterator</code> that contains
     * <code>StatDataFileReader</code>
     *
     * @exception IllegalArgumentException if <code>MIMEType</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersByMIMEType(String MIMEType){
        if (MIMEType == null) {
            throw new IllegalArgumentException("MIMEType == null!");
        }
        // Ensure category is present
        Iterator iter;
        try {
            iter = theRegistry.getServiceProviders(StatDataFileReaderSpi.class,
                                      new ContainsFilter(readerMIMETypesMethod,
                                                         MIMEType),
                                              true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new StatDataFileReaderIterator(iter);
    }



    // Writers

    /**
     * Returns a <code>String</code> array that lists all of
     * the MIME types understood by 
     * the currently registered writers
     * 
     * @return a <code>String</code> array.
     */
    public static String[] getWriterFormatNames() {
        return getReaderWriterInfo(StatDataFileWriterSpi.class, 
                                   SpiInfo.FORMAT_NAMES);
    }


    /**
     * Returns a <code>String</code> array that lists all of
     * the MIME types understood by 
     * the currently registered writers
     *
     * @return a <code>String</code> array.
     */
    public static String[] getWriterMIMETypes() {
        return getReaderWriterInfo(StatDataFileWriterSpi.class, 
                                   SpiInfo.MIME_TYPES);
    }


    /**
     * Returns a <code>String</code> array that lists all of
     * the file extensions
     * associated with the formats understood by 
     * the currently registered writers
     *
     * @return a <code>String</code> array.
     */
    public static String[] getWriterFileSuffixes() {
        return getReaderWriterInfo(StatDataFileWriterSpi.class, 
        SpiInfo.FILE_SUFFIXES);
    }


    static class StatDataFileWriterIterator implements
            Iterator<StatDataFileWriter> {
        // Contains StatDataFileWriterSpis
        public Iterator iter;

        public StatDataFileWriterIterator(Iterator iter) {
            this.iter = iter;
        }
        
        public boolean hasNext() {
            return iter.hasNext();
        }

        public StatDataFileWriter next() {
            StatDataFileWriterSpi spi = null;
            try {
                spi = (StatDataFileWriterSpi)iter.next();
                return spi.createWriterInstance();
            } catch (IOException e) {
                // Deregister the spi in this case, but only as a writerSpi
                theRegistry.deregisterServiceProvider(spi, StatDataFileWriterSpi.class);
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
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

    /**
     * Returns an <code>Iterator</code> that contains all of the
     * currently registered <code>StatDataFileWriter</code>s that
     * claims to write the named format.
     *
     * @param formatName a <code>String</code> that contains the 
     * conventional name of a format (<i>e.g.</i>, "dta" or "sav").
     
     * @return an <code>Iterator</code> that contains 
     * <code>StatDataFileWriter</code>s.
     * 
     * @exception IllegalArgumentException if <code>formatName</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileWriter>
            getStatDataFileWritersByFormatName(String formatName){
        if (formatName == null) {
            throw new IllegalArgumentException("formatName == null!");
        } 
        Iterator iter;
        // Ensure category is present
        try {
            iter = theRegistry.getServiceProviders(StatDataFileWriterSpi.class,
                                    new ContainsFilter(writerFormatNamesMethod,
                                                       formatName),
                                            true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new StatDataFileWriterIterator(iter);
    }


    /**
     * Returns an <code>Iterator</code> that contains all of the 
     * currently registered <code>StatDataFileWriter</code>s that
     * claim to write files with the given file extension.
     * 
     * @param fileSuffix a <code>String</code> that contains a file
     * extension (<i>e.g.</i>, "dta" or "sav").
     *
     * @return an <code>Iterator</code> that contains 
     * <code>StatDataFileWriter</code>s.
     * 
     * @exception IllegalArgumentException if <code>fileSuffix</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileWriter>
            getStatDataFileWritersBySuffix(String fileSuffix){
        if (fileSuffix == null) {
            throw new IllegalArgumentException("fileSuffix == null!");
        }
        Iterator iter;
        // Ensure category is present
        try {
            iter = theRegistry.getServiceProviders(StatDataFileWriterSpi.class,
                                   new ContainsFilter(writerFileSuffixesMethod,
                                                      fileSuffix),
                                            true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new StatDataFileWriterIterator(iter);
    }


    /**
     * Returns an <code>Iterator</code> that contains all of the
     * currently registered <code>StatDataFileWriter</code>s that
     * claim to write files with the given MIME type.
     *
     * @param MIMEType a <code>String</code> that contains a MIME-
     * type string (<i>e.g.</i>, "application/x-stata" or
     * "application/x-spss-sav").
     * 
     * @return an <code>Iterator</code> that contains
     * <code>StatDataFileWriter</code>
     *
     * @exception IllegalArgumentException if <code>MIMEType</code>
     * is <code>null</code>
     */
    public static Iterator<StatDataFileWriter>
        getStatDataFileWritersByMIMEType(String MIMEType){
        if (MIMEType == null) {
            throw new IllegalArgumentException("MIMEType == null!");
        }
        Iterator iter;
        // Ensure category is present
        try {
            iter = theRegistry.getServiceProviders(StatDataFileWriterSpi.class,
                                      new ContainsFilter(writerMIMETypesMethod,
                                                         MIMEType),
                                            true);
        } catch (IllegalArgumentException e) {
            return new HashSet().iterator();
        }
        return new StatDataFileWriterIterator(iter);
    }


    /**
     * Returns <code>StatDataFileWriter</code> corresponding to the given
     * <code>StatDataFileReader</code> instance if there is one, or 
     * <code>null</code> if information of this correspondence is not 
     * available.
     *
     * @param reader an instance of a registered <code>StatDataFileReader</code>.
     *
     * @return a <code>StatDataFileWriter</code> instance, or null.
     *
     * @exception IllegalArgumentException if <code>writer</code> is
     * <code>null</code>.
     */
    public static StatDataFileWriter getStatDataFileWriter(
            StatDataFileReader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null!");
        }

        StatDataFileReaderSpi readerSpi = reader.getOriginatingProvider();
        if (readerSpi == null) {
            Iterator readerSpiIter;
            // Ensure category is present
            try {
                readerSpiIter = 
                    theRegistry.getServiceProviders(StatDataFileReaderSpi.class,
                                                    false);
            } catch (IllegalArgumentException e) {
                return null;
            }

            while (readerSpiIter.hasNext()) {
                StatDataFileReaderSpi temp =
                        (StatDataFileReaderSpi) readerSpiIter.next();
                if (temp.isOwnReader(reader)) {
                    readerSpi = temp;
                    break;
                }
            }
            if (readerSpi == null) {
                return null;
            }
        }

        String[] writerNames = readerSpi.getStatDataFileWriterSpiNames();
        if (writerNames == null) {
            return null;
        }

        Class writerSpiClass = null;
        try {
            writerSpiClass = Class.forName(writerNames[0], true,
                                           ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }

        StatDataFileWriterSpi writerSpi = (StatDataFileWriterSpi)
            theRegistry.getServiceProviderByClass(writerSpiClass);
        if (writerSpi == null) {
            return null;
        }

        try {
            return writerSpi.createWriterInstance();
        } catch (IOException e) {
            // Deregister the spi in this case, but only as a writerSpi
            theRegistry.deregisterServiceProvider(writerSpi,
                                                  StatDataFileWriterSpi.class);
            return null;
        }
    }


    /**
     * Returns <code>StatDataFileReader</code> corresponding to the given
     * <code>StatDataFileWriter</code> instance if there is one, or 
     * <code>null</code> if information of this correspondence is not 
     * available.
     * 
     * @param writer an instance of a registered <code>StatDataFileWriter</code>.
     *
     * @return a <code>StatDataFileReader</code> instance, or null.
     *
     * @exception IllegalArgumentException if <code>writer</code> is
     * <code>null</code>.
     */
    public static StatDataFileReader getStatDataFileReader(StatDataFileWriter writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer == null!");
        }

        StatDataFileWriterSpi writerSpi = writer.getOriginatingProvider();
        if (writerSpi == null) {
            Iterator writerSpiIter;
            // Ensure category is present
            try {
                writerSpiIter =
                    theRegistry.getServiceProviders(StatDataFileWriterSpi.class,
                                                    false);
            } catch (IllegalArgumentException e) {
                return null;
            }

            while (writerSpiIter.hasNext()) {
                StatDataFileWriterSpi temp = (StatDataFileWriterSpi) writerSpiIter.next();
                if (temp.isOwnWriter(writer)) {
                    writerSpi = temp;
                    break;
                }
            }
            if (writerSpi == null) {
                return null;
            }
        }

        String[] readerNames = writerSpi.getStatDataFileReaderSpiNames();
        if (readerNames == null) {
            return null;
        }

        Class readerSpiClass = null;
        try {
            readerSpiClass = Class.forName(readerNames[0], true,
                                   ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }

        StatDataFileReaderSpi readerSpi = (StatDataFileReaderSpi)
            theRegistry.getServiceProviderByClass(readerSpiClass);
        if (readerSpi == null) {
            return null;
        }

        try {
            return readerSpi.createReaderInstance();
        } catch (IOException e) {
            // Deregister the spi in this case, but only as a readerSpi
            theRegistry.deregisterServiceProvider(readerSpi,
                        StatDataFileReaderSpi.class);
            return null;
        }
    }


    // All-in-one methods

    // read() methods: 3 varities (File, InputStream, URL)
    // (note: the InputStream method has been updated to accept an extra
    // argument, raw data filename -- for control card + datafile ingest.
    
    /**
     * Returns a <code>SDIOData</code> instance as the result of reading
     * a given <code>File</code> with a <code>StatDataFileReader</code>
     * that is automatically selected from among those currently
     * registered.
     * 
     * @param input a <code>File</code>instance to read from.
     * 
     * @return a <code>SDIOData</code> that contains the reading results of
     * the input, or <code>null</code>.
     * 
     * @throws java.io.IOException if a reading error is detected.
     */
    public static SDIOData read(File input) throws IOException {
        dbgLog.fine("\n\n***** within read: file case *****");
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }
        if (!input.canRead()) {
            throw new IOException("Can't read input file!");
        }

        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(input));
        if (stream == null) {
            throw new IOException("Can't create an InputStream!");
        }
        SDIOData sd = read(stream, null);
        
        dbgLog.fine("after file: read");
        if (sd == null) {
            stream.close();
        }
        return sd;
    }


    /**
     * Returns a <code>SDIOData</code> instance as the result of reading
     * a given <code>BufferedInputStream</code> 
     * with an <code>StatDataFileReader</code> that is automatically 
     * selected from among those currently registered.
     *
     * @param input a <code>BufferedInputStream</code> instance to read from
     *
     * @return a <code>SDIOData</code> that contains the reading results of
     * the input, or <code>null</code>.
     *
     * @throws java.io.IOException if a reading error is detected.
     */
    public static SDIOData read(BufferedInputStream input, File rawData) throws IOException {
        dbgLog.fine("\n\n***** within read: bis case *****");
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }
        dbgLog.fine("before getStatDataFileReaders");
        Iterator iter = getStatDataFileReaders(input);
        dbgLog.fine("before getStatDataFileReaders");
        if (!iter.hasNext()) {
            return null;
        }
        dbgLog.fine("read: bis case:iter class name"+iter.getClass().getName());

        StatDataFileReader reader = (StatDataFileReader)iter.next();
        
        SDIOData sd;
        try {
            sd = reader.read(input, rawData);
        } finally {
            input.close();
        }
        return sd;
    }


    /**
     * Returns a <code>SDIOData</code> instance as the result of reading
     * a given <code>URL</code> with an <code>StatDataFileReader</code>
     * that is automatically selected from among those currently registered.
     * A <code>BufferedInputStream</code> instance is obtained from the 
     * <code>URL</code>. If there is no registered <code>StatDataFileReader</code>
     * that is supposed to be able to read the resulting stream, 
     * <code>null</code> is returned.
     *
     * @param input a <code>URL</code> instance to read from.
     *
     * @return a <code>SDIOData</code> that contains the reading results of
     * the input, or <code>null</code>.
     *
     * @throws java.io.IOException if a reading error is detected.
     */
    public static SDIOData read(URL input) throws IOException {
        dbgLog.fine("***** within read: URL case *****");
        if (input == null) {
            throw new IllegalArgumentException("input == null!");
        }

        BufferedInputStream istream = null;
        try {
            istream = (BufferedInputStream)input.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SDIOData sd;
        try {
            sd = read(istream, null);
            if (sd == null) {
                istream.close();
            }
        } finally {
            istream.close();
        }
        return sd;
    }


    // write(): two varieties File and Outputstream

    /**
     * Writes a statistical data  using an <code>StatDataFileWriter</code>
     * supporting the given file format to a <code>File</code> instance.
     * if this <code>File</code> instance already exists, it is overwritten.
     * 
     * @param sd a <code>SDIOData</code> instance that contains the data and metadata
     * to be saved
     * 
     * @param formatName    a<code>String</code> that contains the conventional
     * name of the format
     *
     * @param output a <code>File</code> instance to be written to.
     * 
     * @return <code>false</code> if an appropriate writer is not found.
     *
     * @exception IllegalArgumentException if any parameter is <code>null</code>.
     *
     * @throws java.io.IOException  if a writing error is detected.
     */
    public static boolean write(SDIOData sd,
                                String formatName,
                                File output) throws IOException {
        if (output == null) {
            throw new IllegalArgumentException("output == null!");
        }
        OutputStream stream = null;
        try {
            output.delete();
            stream = new FileOutputStream(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean val;
        try {
            val = write(sd, formatName, stream);
        } finally {
            stream.close();
        }
        return val;
    }


    /**
     * Writes a statistical data  using an <code>StatDataFileWriter</code>
     * supporting the given file format to a <code>OutputStream</code> instance.
     *
     * <p>This method <em>does not</em> close the supplied <code>OutputStream</code>
     * after the writer finishes writing.</p>
     * 
     * @param sd a <code>SDIOData</code> instance that contains the data and metadata
     * to be saved
     * 
     * @param formatName    a<code>String</code> that contains the conventional
     * name of the format
     * 
     * @param stream an <code>OutputStream</code> instance to be written to.
     *
     * @return <code>false</code> if an appropriate writer is not found.
     *
     * @exception IllegalArgumentException if any parameter is <code>null</code>.
     *
     * @throws java.io.IOException  if a writing error is detected.
     */
    public static boolean write(SDIOData sd,
                                String formatName,
                                OutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("output == null!");
        }

        boolean val;
        try {
            val = write(sd, formatName, stream);
        } finally {
            stream.close();
        }
        return val;
    }
}
