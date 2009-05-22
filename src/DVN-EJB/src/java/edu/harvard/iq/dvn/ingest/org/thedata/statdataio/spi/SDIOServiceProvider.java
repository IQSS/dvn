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

/**
 *
 * @author akio sone
 */
public abstract class SDIOServiceProvider  implements RegisterableService {

    /**
     *
     */
    protected String vendorName;
    
    /**
     *
     */
    protected String version;

    /**
     *
     * @param vendorName
     * @param version
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
     * 
     */
    public SDIOServiceProvider() {
    }

    public void onRegistration(ServiceRegistry registry,
                               Class<?> category) {}

    public void onDeregistration(ServiceRegistry registry,
                                 Class<?> category) {}
    /**
     * Get the value of version
     *
     * @return the value of version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the value of vendorName
     *
     * @return the value of vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

}
