/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.ingest.specialother.spi;

import edu.harvard.iq.dvn.ingest.specialother.*;
import java.util.logging.*;
import java.io.*;

import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.RegisterableService;
import edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi.ServiceRegistry;
import java.nio.MappedByteBuffer;
import java.util.Locale;

/**
 *
 * @author leonidandreev
 * - a simplified version of Akio Sone's StatDataFileReader SPI.
 */
public abstract class FileIngesterSpi implements RegisterableService {
    private static Logger dbgLog = 
    Logger.getLogger(FileIngesterSpi.class.getPackage().getName());

    
    /**
     * Constructs an empty <code>FileIngesterSpi</code> instance.
     */
    protected FileIngesterSpi() {
    }

    
    protected String vendorName;
    protected String version;

    public FileIngesterSpi(String vendorName, String version) {
        if (vendorName == null){
            throw new IllegalArgumentException("vendorName is null!");
        }
        if (version == null){
            throw new IllegalArgumentException("version string is null");
        }
        this.vendorName = vendorName;
        this.version = version;
    }

    public void onRegistration(ServiceRegistry registry,
                               Class<?> category) {}
                               
    
    public void onDeregistration(ServiceRegistry registry,
                                 Class<?> category) {}
    
    public String getVersion() {
        return version;
    }

    public String getVendorName() {
        return vendorName;
    }

    
    public abstract String getDescription(Locale locale);
    
    /**
     * A <code>String</code> array that contains human-readable format names
     * and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] names = null;

    /**
     * Gets the value of names.
     * @return the value of names.
     */
    public String[] getFormatNames() {
        return (String[])names.clone();
    }

    /**
     * A <code>String</code> array that contains format extensions 
     *  and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] suffixes = null;
    
    /**
     * Gets the value of suffixes
     *
     * @return the value of suffixes
     */
    public String[] getFileSuffixes() {
        return suffixes == null ? null : (String[])suffixes.clone();
    }
    
    
    /**
     * A <code>String</code> array that contains MIME types 
     * and are used by the <code>StatDataFileReader</code> or 
     * <code>StatDataFileWriter</code> implementation related to this
     * class.
     */
    protected String[] MIMETypes = null;
    
    /**
     * Gets the value of MIMETypes
     *
     * @return the value of MIMETypes
     */
    public String[] getMIMETypes() {
        return MIMETypes == null ? null : (String[])MIMETypes.clone();
    }
    
    /**
     * A <code>String</code> that contains the name of the plug-in class.
     */
    protected String pluginClassName = null;

    /**
     * Gets the value of pluginClassName
     *
     * @return the value of pluginClassName
     */
    public String getPluginClassName() {
        return pluginClassName;
    }

   

    /**
     * Constructs an empty <code>StatDataFileReaderWriterSpi</code> instance
     * with given values.
     * 
     * @param vendorName  the vendor name.
     * @param version     a version identifier.
     * @param names       at least one format name or more.
     * @param suffixes    at least one format extensions or more.
     * @param MIMETypes   at least one format's MIME type or more.
     * @param pluginClassName the fully qualified name of the associated
     * <code>StatDataFileReaderSpi</code> or 
     * <code>StatDataFileWriterSpi</code> class.
     */
    public FileIngesterSpi(
            String vendorName,
            String version,
            String[] names,
            String[] suffixes,
            String[] MIMETypes,
            String pluginClassName
            ) {
        
        this(vendorName, version);

        if (names == null) {
            throw new IllegalArgumentException("names is null!");
        }

        if (names.length == 0) {
            throw new IllegalArgumentException("names.length is 0!");
        }
        this.names = (String[])names.clone();
        if (pluginClassName == null) {
            throw new IllegalArgumentException("pluginClassName is null!");
        }

        if (suffixes != null && suffixes.length > 0) {
            this.suffixes = (String[])suffixes.clone();
        }

        if (MIMETypes != null && MIMETypes.length > 0) {
            this.MIMETypes = (String[])MIMETypes.clone();
        }

        this.pluginClassName = pluginClassName;
    }

    /**
     * Writes a <code>MappedByteBuffer</code> object in hexadecimal.
     *
     * @param buff a MappedByteBuffer object.
     * @param hdr the title string.
     */
    public void printHexDump(MappedByteBuffer buff, String hdr) {
        int counter = 0;
        if (hdr != null) {
            System.out.println(hdr);
        }
        for (int i = 0; i < buff.capacity(); i++) {
            counter = i + 1;
            System.out.println(String.format("%02X ", buff.get()));
            if (counter % 16 == 0) {
                System.out.println();
            } else {
                if (counter % 8 == 0) {
                    System.out.print(" ");
                }
            }
        }
        System.out.println();
        buff.rewind();
    }
    /**
     * Writes the <code>byte</code> array in hexadecimal.
     *
     * @param buff a <code>byte</code> array.
     * @param hdr the title string.
     */
    public void printHexDump(byte[] buff, String hdr) {
        int counter = 0;
        if (hdr != null) {
            System.out.println(hdr);
        }
        for (int i = 0; i < buff.length; i++) {
            counter = i + 1;
            System.out.println(String.format("%02X ", buff[i]));
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
     * Returns true if the supplied source object starts with a sequence of bytes
     * that is of the format supported by this reader.  Returning true from this
     * method does not guarantee that reading will successfully end.
     * 
     * @param source    typically a <code>BufferedInputStream</code> object.
     *                  object to be read.
     * @return          true if the stream can be read.
     * @throws java.io.IOException if an I/O error occurs
     *                             during reading the stream.
     */
    public abstract boolean canDecodeInput(Object source) throws IOException;
    
    
    /**
     * Similar methods for File and BufferedInputStream inputs:
     */
    public abstract boolean canDecodeInput(File file) throws IOException;
    public abstract boolean canDecodeInput(BufferedInputStream stream) throws IOException;
    
    /**
     * Returns an instance of <code>FileIngester</code> implementation associated with
     * this service provider.
     * 
     * @param extension     a plug-in specific extension object.
     * @return              a <code>FileIngester</code> instance.
     * @throws IOException  if the instantiation attempt of the reader fails.
     */
    public abstract FileIngester createIngesterInstance(Object extension)
        throws IOException;
    
    public FileIngester createIngesterInstance() throws IOException{
        return createIngesterInstance(null);
    }
    
    /**
     * Returns <code>true</code> if the <code>FileIngester</code> object
     * supplied in is an instance of the <code>FileIngester</code>
     * associated with this service provider.
     * 
     * @param reader  an <code>StatDataFileReader</code> object.
     * @return <code>true</code> if <code>reader</code> is recognized.
     */
    public boolean isOwnReader(FileIngester reader) {
        if (reader == null) {
            throw new IllegalArgumentException("reader == null!");
        }
        String name = reader.getClass().getName();
        return name.equals(pluginClassName);
    }

    
}
