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