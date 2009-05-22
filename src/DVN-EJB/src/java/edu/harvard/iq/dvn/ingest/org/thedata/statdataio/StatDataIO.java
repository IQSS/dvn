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

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio;


import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import java.util.logging.*;
import static java.lang.System.*;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.*;

/**
 *
 * @author akio sone
 */
public final class StatDataIO {

    private static Logger dbgLog = Logger.getLogger(StatDataIO.class.getPackage().getName());

    private static final SDIORegistry theRegistry =
        SDIORegistry.getDefaultInstance();
        
        
    /**
     *
     */
    public static void scanForPlugins() {
        theRegistry.registerApplicationClasspathSpis();
    }
    
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
     *
     * @return
     */
    public static String[] getReaderFormatNames() {
        return getReaderWriterInfo(StatDataFileReaderSpi.class, SpiInfo.FORMAT_NAMES);
    }

    /**
     *
     * @return
     */
    public static String[] getReaderMIMETypes() {
        return getReaderWriterInfo(StatDataFileReaderSpi.class, SpiInfo.MIME_TYPES);
    }

    /**
     *
     * @return
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
     *
     * @param input
     * @return
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
     *
     * @param formatName
     * @return
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersByFormatName(String formatName)
    {
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
     *
     * @param fileSuffix
     * @return
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersBySuffix(String fileSuffix)
    {
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
     *
     * @param MIMEType
     * @return
     */
    public static Iterator<StatDataFileReader>
        getStatDataFileReadersByMIMEType(String MIMEType)
    {
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
     *
     * @return
     */
    public static String[] getWriterFormatNames() {
        return getReaderWriterInfo(StatDataFileWriterSpi.class, 
                                   SpiInfo.FORMAT_NAMES);
    }


    /**
     *
     * @return
     */
    public static String[] getWriterMIMETypes() {
        return getReaderWriterInfo(StatDataFileWriterSpi.class, 
                                   SpiInfo.MIME_TYPES);
    }


    /**
     *
     * @return
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
     *
     * @param formatName
     * @return
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
     * 
     * @param fileSuffix
     * @return
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
     *
     * @param MIMEType
     * @return
     */
    public static Iterator<StatDataFileWriter>
    getStatDataFileWritersByMIMEType(String MIMEType)
    {
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
     *
     * @param reader
     * @return
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
     *
     * @param writer
     * @return
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
    /**
     *
     * @param input
     * @return
     * @throws java.io.IOException
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
        SDIOData sd = read(stream);
        
        dbgLog.fine("after file: read");
        if (sd == null) {
            stream.close();
        }
        return sd;
    }


    /**
     *
     * @param input
     * @return
     * @throws java.io.IOException
     */
    public static SDIOData read(BufferedInputStream input) throws IOException {
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
            sd = reader.read(input);
        } finally {
            input.close();
        }
        return sd;
    }


    /**
     *
     * @param input
     * @return
     * @throws java.io.IOException
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
            sd = read(istream);
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
     *
     * @param sd
     * @param formatName
     * @param output
     * @return
     * @throws java.io.IOException
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
     *
     * @param sd
     * @param formatName
     * @param stream
     * @return
     * @throws java.io.IOException
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
