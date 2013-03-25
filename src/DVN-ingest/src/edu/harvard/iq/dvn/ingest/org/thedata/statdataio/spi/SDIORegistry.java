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
package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi;

import java.util.*;
import java.security.*;
import java.util.logging.*;

import edu.harvard.iq.dvn.ingest.specialother.spi.FileIngesterSpi;
/**
 * A registry class for StatData I/O service provider instances.
 * This class is coded after javax.imageio.spi.IIORegistry of OpenJDK 6.0.
 * Wth the <code>Registry</code> class in the package, an instance of 
 * this class remains singleton.
 * 
 * @author Akio Sone at UNC-Odum
 */
public final class SDIORegistry extends ServiceRegistry{

   private static Logger dbgLog =
       Logger.getLogger(SDIORegistry.class.getPackage().getName());
       
    /**
     * A <code>Vector</code> that contains the valid SDIO registry
     * categories (reader and writer only) to be used in the constructor. 
     */    
    private static final int numberOfSupportedCategories = 3; 
    private static final Vector initialCategories = new Vector(numberOfSupportedCategories);

    static {
        initialCategories.add(StatDataFileReaderSpi.class);
        initialCategories.add(StatDataFileWriterSpi.class);
        initialCategories.add(FileIngesterSpi.class);
    }

    /**
     * Sets up the valid provider categories (reader and writer) and 
     * automatically register all (those in initialCategories, 
     * those hardwired here, and those on the application class path )
     * available service provider classes.
     */
    SDIORegistry() {
        super(initialCategories.iterator());
        
        registerStandardSpis();
        registerApplicationClasspathSpis();
    }

    /**
     * Returns the default <code>SDIORegistry</code> instance used by 
     * the StatData I/O API.  The instance is created only if it is not 
     * registered in Registry REGISTRY.
     * 
     * @return the default registry
     */
    public static SDIORegistry getDefaultInstance() {

        SDIORegistry registry = null;
        //if (!Registry.isThisClassRegistered(SDIORegistry.class.getName())) {
        registry = (SDIORegistry) Registry.REGISTRY.getInstance(
                SDIORegistry.class.getName());
        //}

        // Hmm. So, let me get this straight, if the object is not registered, 
        // we request it from the Registry; which creates a new instance of the
        // object, gives it to us, and registers it in the process. If however
        // the instance IS already registered - we say, or sweet, already 
        // registered!... and return null. (WTF??)
        // (commenting out the "isThisClassRegistered" check to fix this)
        // - L.A. 
        // P.S. This was working all these years simply because we were only 
        // using one registerable class of plugins - subsettable readers; 
        // (writers were not used). But once the extractable metadata plugins
        // were added, this stopped working - only the service that asked 
        // first was getting the registry instance. The other one was getting 
        // null. :)
        return registry;
    }

    private void registerStandardSpis() {
        // No Hardwired standard SPIs
        dbgLog.fine("within registerStandardSpis");
        registerInstalledProviders();
    }

    private void registerInstalledProviders() {
        dbgLog.fine("+++++ registerInstalledProviders: begins +++++");
        PrivilegedAction doRegistration =
            new PrivilegedAction() {
                public Object run() {
                    Iterator categories = getCategories();
                    while (categories.hasNext()) {
                        Class<SDIOServiceProvider> c = (Class)categories.next();
                        dbgLog.fine("c="+c.getName());
                        for (SDIOServiceProvider p : ServiceLoader.loadInstalled(c)) {
                            // the following method found in ServiceRegistry class
                            registerServiceProvider(p);
                        }
                    }
                    return this;
                }
            };
        AccessController.doPrivileged(doRegistration);
        dbgLog.fine("+++++ registerInstalledProviders: ends +++++");
    }

    /**
     * Registers all available service provider classes found on the
     * application class path, using the default <code>ClassLoader</code>.
     * This method is usually invoked by the 
     * <code>StatDataIO.scanForPlugins</code> method.
     */
    
    public void registerApplicationClasspathSpis() {
        dbgLog.fine("+++++ SDIORegistry.registerApplicationClassthSpis: start");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Iterator categories = getCategories();
        while (categories.hasNext()) {
            Class<SDIOServiceProvider> c = (Class)categories.next();
            dbgLog.fine("category class="+c);
            Iterator<SDIOServiceProvider> riter =
                    ServiceLoader.load(c, loader).iterator();
            while (riter.hasNext()) {
                try {
                    SDIOServiceProvider r = riter.next();
                    // the following method found in ServiceRegistry class
                    registerServiceProvider(r);
                } catch (ServiceConfigurationError err) {
                    if (System.getSecurityManager() != null) {
                        err.printStackTrace();
                    } else {
                        throw err;
                    }
                }
            }
        }
        dbgLog.fine("+++++ SDIORegistry.registerApplicationClassthSpis: end");
    }



}
