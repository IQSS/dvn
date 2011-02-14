/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.util.ResourceBundle;

/**
 *
 * @author wbossons
 */
public class BundleReader implements java.io.Serializable  {
    private String messageKey;
    private String bundleName;
    private String messageValue;
    
    
    public BundleReader() {
        //default constructor
    }
    
    public BundleReader(String bundlename) {
        this.bundleName = bundlename;
    }
    
    public BundleReader(String bundlename, String messagekey) {
        this.bundleName = bundlename;
        this.messageKey = messagekey;
    }
    
    /* Getters */
    
    /**
     * 
     * @return
     */
    public String getMessageKey() {
        return this.messageKey;
    }
    
        /**
     * 
     * @return
     */
    public String getBundleName() {
        return this.bundleName;
    }
    
    public String getMessageValue() {
        try {
            setMessageValue(ResourceBundle.getBundle(this.bundleName).getString(this.messageKey));
        } catch (NullPointerException npe) {
            setMessageValue("The resource was not found due to a null value: " + npe.getMessage());
        } catch (Exception e) {
            setMessageValue("An unexpected exception occurred " + e.getMessage());
        } finally {
            return this.messageValue;
        }
    }
    
    public String getMessageValue(String messagekey) {
        try {
            setMessageValue(ResourceBundle.getBundle(this.bundleName).getString(messagekey));
        } catch (NullPointerException npe) {
            setMessageValue("The resource was not found due to a null value: " + npe.getMessage());
        } catch (Exception e) {
            setMessageValue("An unexpected exception occurred " + e.getMessage());
        } finally {
            return this.messageValue;
        }
    }
    
    
    /* Setters*/
    
    public void setBundleName(String bundlename) {
        this.bundleName = bundlename;
    }
    
    public void setMessageKey(String messagekey) {
        this.messageKey = messagekey;
    }
    
    public void setMessageValue(String messagevalue) {
        this.messageValue = messagevalue;
    }
}
