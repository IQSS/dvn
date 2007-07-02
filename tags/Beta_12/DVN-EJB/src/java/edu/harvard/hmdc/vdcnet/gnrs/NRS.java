/*
 * NRS.java
 *
 * Created on February 14, 2007, 2:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.gnrs;

/**
 *
 * @author roberttreacy
 */
public abstract class NRS {
    private String protocol;
    private String authority;
    
    /** Creates a new instance of NRS */
    public NRS() {
    }
    
    abstract public String getNewObjectId();

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
