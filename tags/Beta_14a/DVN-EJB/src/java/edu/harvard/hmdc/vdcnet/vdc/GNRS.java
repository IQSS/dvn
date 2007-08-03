/*
 * GNRS.java
 *
 * Created on August 7, 2006, 12:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

/**
 *
 * @author Ellen Kraffmiller
 */
public class GNRS {
    
    /** Creates a new instance of GNRS */
    public GNRS() {
    }

    /**
     * Holds value of property url.
     */
    private String url;

    /**
     * Getter for property url.
     * @return Value of property url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Setter for property url.
     * @param url New value of property url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return Newly created namespace (for study handle)
     */
    public String newNameSpace(String vdcAddress, String vdcDescription, String vdcPassword) {
        // TODO: implementation
        return null;
    }
    /**
     * Register object in the namespace (need param's from Leonid)
     */
    public String register(String nameSpace, String objectId, String vdcPassword){
         // TODO: implementation
        return null;
    }
    /**
     *
     */
    public String lookup(String nameSpace, String objectId){
        // TODO: implementation
        return null;
    }
    
}
