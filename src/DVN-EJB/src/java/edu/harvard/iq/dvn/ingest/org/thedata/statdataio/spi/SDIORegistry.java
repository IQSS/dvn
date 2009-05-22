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
import static java.lang.System.*;


/**
 *
 * @author Akio Sone
 */
public final class SDIORegistry extends ServiceRegistry{

    private static final Vector initialCategories = new Vector(2);

    static {
        initialCategories.add(StatDataFileReaderSpi.class);
        initialCategories.add(StatDataFileWriterSpi.class);
    }

    /**
     *
     */
    protected SDIORegistry() {
        super(initialCategories.iterator());
        registerStandardSpis();
        registerApplicationClasspathSpis();
    }

    /**
     *
     * @return
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
        // Hardwire standard SPIs
        //out.println("within registerStandardSpis");
        //registerServiceProvider(new DTAFileReaderSpi());
        //registerServiceProvider(new DTAFileWriterSpi());
        registerInstalledProviders();
    }

    /**
     * 
     */
    public void registerApplicationClasspathSpis() {
        //out.println("within registerApplicationClassthSpis");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Iterator categories = getCategories();
        while (categories.hasNext()) {
            Class<SDIOServiceProvider> c = (Class)categories.next();
            //out.println("category class="+c);
            Iterator<SDIOServiceProvider> riter =
                    ServiceLoader.load(c, loader).iterator();
            while (riter.hasNext()) {
                try {
                    SDIOServiceProvider r = riter.next();
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
    }

    private void registerInstalledProviders() {
        //out.println("+++++ registerInstalledProviders: begins +++++");
        PrivilegedAction doRegistration =
            new PrivilegedAction() {
                public Object run() {
                    Iterator categories = getCategories();
                    while (categories.hasNext()) {
                        Class<SDIOServiceProvider> c = (Class)categories.next();
                        //out.println("c="+c.getName());
                        for (SDIOServiceProvider p : ServiceLoader.loadInstalled(c)) {
                            registerServiceProvider(p);
                        }
                    }
                    return this;
                }
            };
        AccessController.doPrivileged(doRegistration);
    }

}
