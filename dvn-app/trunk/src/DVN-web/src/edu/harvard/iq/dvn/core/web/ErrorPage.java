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
 * ErrorPage.java
 *
 * Created on March 29, 2007, 3:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Ellen Kraffmiller
 * (Leonid: added mechanism for returning HTTP status codes)
 */
public class ErrorPage extends VDCBaseBean implements java.io.Serializable  {
    private Integer errorCode = 0;
    
    /** Creates a new instance of ErrorPage */
    public ErrorPage() {
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer ec) {
        errorCode = ec;
    }

    public void init() {
        super.init();

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (javax.servlet.http.HttpServletResponse) context.getExternalContext().getResponse();

        if (errorCode !=null && errorCode != 0) {
            response.setStatus(errorCode);
        }
    }
    
    public List getMessages() {
        List msgs = new ArrayList();
        FacesContext context = FacesContext.getCurrentInstance();
        for (Iterator it = context.getMessages(); it.hasNext();) {
            FacesMessage elem = (FacesMessage) it.next();
            msgs.add(elem.getSummary());
        }
        return msgs;
    }
}
