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
 * GNRS.java
 *
 * Created on August 7, 2006, 12:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

/**
 *
 * @author Ellen Kraffmiller
 */
public class GNRS implements java.io.Serializable  {
    
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
