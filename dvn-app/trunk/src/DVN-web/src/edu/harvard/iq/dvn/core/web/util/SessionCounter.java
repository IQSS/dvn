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
 * SessionCounter.java
 *
 * Created on March 19, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Ellen Kraffmiller
 */
public class SessionCounter implements java.io.Serializable  {
    
    /** Creates a new instance of SessionCounter */
    public SessionCounter() {
      
    }
   public final static synchronized Long getNext() {
            HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            Long currentVal = (Long)session.getAttribute("sessionCounter");
            if (currentVal==null) {
                currentVal = new Long(-1);
            } else {
                currentVal = new Long(currentVal.intValue()-1);
            }
            session.setAttribute("sessionCounter",currentVal);
            return currentVal;
            
        }    
}