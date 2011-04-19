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
