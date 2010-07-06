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

import static java.lang.System.*;
import java.util.Locale;

/**
 * A superclass that provides functionality common to all StatData I/O
 * service provider interface classes.
 * 
 * @author akio sone at UNC-Odum
 */
public abstract class SDIOServiceProvider  implements RegisterableService {

    /**
     * The name of the vendor that is responsible for coding this
     * service provider and its associated implementation.
     * The constructor assigns a non-<code>null</code> value to this field.
     */
    protected String vendorName;
    
    /**
     * A string that describes the version number of this service provider
     * and its implementation.
     * The constructor assigns a non-<code>null</code> value to this field.
     */
    protected String version;

    /**
     * Constructs an <code>SDIOServiceProvider</code> with a given set of
     * vender name and version identifier.
     * 
     * @param vendorName the vendor name.
     * @param version the version identifier.
     * @exception IllegalArgumentException if <code>vendorName</code>
     * is <code>null</code>.
     * @exception IllegalArgumentException if <code>version</code>
     * is <code>null</code>.
     */
    public SDIOServiceProvider(String vendorName, String version) {
        if (vendorName == null){
            throw new IllegalArgumentException("vendorName is null!");
        }
        if (version == null){
            throw new IllegalArgumentException("version string is null");
        }
        this.vendorName = vendorName;
        this.version = version;
        //out.println("SDIOServiceProvder is called");
    }

    /**
     * Constructs an empty <code>SDIOServiceProvider</code> instance.
     */
    public SDIOServiceProvider() {
    }

    /**
     * A callback to be called exactly once after this Spi class
     * has been instantiated and registered in a 
     * <code>ServiceRegistry</code>.
     *
     * @param registry the ServiceRegistry instance.
     * @param category a <code>Class</code>  object that indicatges
     * its registry category under which this object has been registered.
     * category.
     */
    public void onRegistration(ServiceRegistry registry,
                               Class<?> category) {}
                               
    /**
     * A callback whenever this Spi class is deregistered from
     * a <code>ServiceRegistry</code>.
     *
     * @param registry the ServiceRegistry instance.
     * @param category a <code>Class</code> object that indicatges
     * its registry category from which this object is being de-registered.
     */
    public void onDeregistration(ServiceRegistry registry,
                                 Class<?> category) {}
    /**
     * Gets the value of the version field.
     *
     * @return the value of the version field.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the value of the vendorName field.
     *
     * @return the value of the vendorName field.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Returns a short, human-readable description of this 
     * service provider and its associated implementation.
     * The returned string should be localized for the 
     * supplied <code>lcoale</code>, if possible.
     *
     * @param locale a <code>Locale</code> that localizes the returned
     * string.
     *
     * @return a <code>String</code> that describes this
     * service provider.
     */
    public abstract String getDescription(Locale locale);
    
}
