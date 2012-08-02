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
package edu.harvard.iq.dvn.core.study;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author gdurand
 */
public class GlobalId implements java.io.Serializable {

    public GlobalId(String identifier) {

        int index1 = identifier.indexOf(':');
        int index2 = identifier.indexOf('/');
        if (index1 == -1) {
            throw new IllegalArgumentException("Error parsing identifier: " + identifier + ". ':' not found in string");
        } else {
            protocol = identifier.substring(0, index1);
        }
        if (index2 == -1) {
            throw new IllegalArgumentException("Error parsing identifier: " + identifier + ". '/' not found in string");

        } else {
            authority = identifier.substring(index1 + 1, index2);
        }
        studyId = identifier.substring(index2 + 1).toUpperCase();

    }

    public GlobalId(String protocol, String authority, String studyId) {
        this.protocol = protocol;
        this.authority = authority;
        this.studyId = studyId;
    }


    private String protocol;
    private String authority;
    private String studyId;


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

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String toString() {
        return protocol + ":" + authority + "/" + studyId;
    }
    
    public URL toURL() {
        // TODO: add logic to support multiple protocols
        URL url = null;
        try {
            url = new URL("http://hdl.handle.net/" + authority + "/" + studyId);
        } catch (MalformedURLException ex) {
            Logger.getLogger(GlobalId.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return url;
    }    


}
