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
