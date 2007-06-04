/*
 * NRSFactory.java
 *
 * Created on February 14, 2007, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.gnrs;

import edu.harvard.hmdc.vdcnet.vdc.*;

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
