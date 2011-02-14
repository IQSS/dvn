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
 * DvnInlineHelp.java
 *
 * Created on March 29, 2007, 11:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.component;

import javax.faces.context.FacesContext;
import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author wbossons
 */
public class DvnInlineHelp extends UIComponentBase implements java.io.Serializable  {
    
    /** Creates a new instance of DvnInlineHelp */
    public DvnInlineHelp() {
    }
    
    public String getFamily() {
        return null;
    }
    
    public void encodeBegin(FacesContext facescontext)
        throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
                
        //the link.
        String linkText    = (String)getAttributes().get("linkText");
        String cssClass    = (String)getAttributes().get("cssClass");
        String tooltipText = (String)getAttributes().get("tooltipText");
        String eventType   = (String)getAttributes().get("eventType");
        String heading     = (String)getAttributes().get("heading");
        writer.startElement("a", this);
        writer.writeAttribute("id", getClientId(facescontext), null);
        writer.writeAttribute("name", getClientId(facescontext), null);
        
        if (eventType.equals("mouseover")) {
            writer.writeAttribute("onmouseover", new String("javascript:popupInlineHelp('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event);"), null);
            writer.writeAttribute("onmouseout", new String("javascript:hideInlineHelp();"), null);
        } else if (eventType.equals("click")) {
            writer.writeAttribute("title", tooltipText, null);
            writer.writeAttribute("onclick", new String("javascript:popupInlineHelp('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event);"), null);
        }
        
        if (cssClass != null) {
            writer.writeAttribute("class", cssClass, null);
            writer.writeText(linkText, null);
        } else {
            writer.writeText(linkText, null);
        }
        
        writer.endElement("a");
    }
    
    public void encodeChildren(FacesContext facescontext)
        throws IOException {
        //Nothing to do
        
    }
    
    public void encodeEnd(FacesContext facescontext) 
        throws IOException {
        String helpText = (String)getAttributes().get("helpMessage");
        ResponseWriter writer = facescontext.getResponseWriter();
        writer.startElement("input", this);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", new String(getClientId(facescontext) + ":hidField"), null);
        writer.writeAttribute("name", new String("hid" + getClientId(facescontext)), null);
        writer.writeAttribute("value", helpText, null);
        writer.endElement("input");
    }
}
