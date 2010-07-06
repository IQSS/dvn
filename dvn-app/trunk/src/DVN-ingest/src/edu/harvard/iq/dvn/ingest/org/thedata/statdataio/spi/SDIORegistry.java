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

package edu.harvard.iq.dvn.ingest.org.thedata.statdataio.spi;

import java.util.*;
import java.security.*;
import java.util.logging.*;


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
    private static final Vector initialCategories = new Vector(2);

    static {
        initialCategories.add(StatDataFileReaderSpi.class);
        initialCategories.add(StatDataFileWriterSpi.class);
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
     * @return the default resistry
     */
    public static SDIORegistry getDefaultInstance() {

        SDIORegistry registry = null;
        if (!Registry.isThisClassRegistered(SDIORegistry.class.getName())) {
            registry = (SDIORegistry)Registry.REGISTRY.getInstance(
                    SDIORegistry.class.getName());
        }
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
