/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
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
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * NRSFactory.java
 *
 * Created on February 14, 2007, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.gnrs;

import edu.harvard.iq.dvn.core.vdc.*;

/**
 *
 * @author roberttreacy
 */
public class NRSFactory {
    private boolean testMode = true; // TODO - distinguish a test environment from production
    
    /** Creates a new instance of NRSFactory */
    public NRSFactory() {
    }
    
    public NRS getNRS(String protocol, String authority){
        NRS requestedNRS = null;
        if (testMode) {
            requestedNRS = new TestNRS();
            requestedNRS.setProtocol(protocol);
            requestedNRS.setAuthority(authority);
        } else {
            if (protocol.equalsIgnoreCase("hdl")){
                requestedNRS = new TestNRS(protocol, authority);
            }
        }
       
        return requestedNRS;
    }
}
