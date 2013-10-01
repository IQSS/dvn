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
 * ExceptionMessageWriter.java
 *
 * Created on November 28, 2006, 3:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author wbossons
 */
public class ExceptionMessageWriter implements java.io.Serializable  {
    
    /** Creates a new instance of ExceptionMessageWriter */
    public ExceptionMessageWriter() {
    }
    
    public static void addGlobalMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
    }
    
    public static void removeGlobalMessage(String compareAgainst) {
        Iterator iterator = FacesContext.getCurrentInstance().getMessages(null);
            while (iterator.hasNext()){
                if (iterator.next().equals(compareAgainst)) iterator.remove();
            }
    }
    
    public static void logException(Exception e) {
        String msg = "An error occurred: " + e.getCause().toString();
        System.out.println(msg);
    }
            
    
}
