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
 * DvnTooltip.java
 *
 * Created on April 30, 2007, 2:44 PM
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
public class DvnTooltip extends UIComponentBase implements java.io.Serializable  {
    
    /** Creates a new instance of DvnTooltip */
    public DvnTooltip() {
    }
    
    public String getFamily() {
        return null;
    }
    
    public void encodeBegin(FacesContext facescontext)
        throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
        
        //the link.
        String linkText    = (String)getAttributes().get("linkText");
        String linkUrl     = (String)getAttributes().get("linkUrl");
        String cssClass    = (String)getAttributes().get("cssClass");
        String tooltipText = (String)getAttributes().get("tooltipText");
        String eventType   = (String)getAttributes().get("eventType");
        String heading     = (String)getAttributes().get("heading");
        if (heading == null) heading="";
        String imageLink   = (String)getAttributes().get("imageLink");
        String imageSource = (String)getAttributes().get("imageSource");
        
        String closeText = (String)getAttributes().get("closeText");
        writer.startElement("a", this);
        writer.writeAttribute("id", getClientId(facescontext), null);
        writer.writeAttribute("name", getClientId(facescontext), null);
        writer.writeAttribute("href", linkUrl, null);
        
        if (eventType.equals("mouseover")) {
            writer.writeAttribute("onmouseover", new String("javascript:popupTooltip('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event);"), null);
            writer.writeAttribute("onmouseout", new String("javascript:hideTooltip();"), null);
        } else if (eventType.equals("click")) {
            // this is a popup -- a 400 px window.
            if (closeText != null)
                closeText = "<span style=\"display:block;width:400;text-align:right;margin-bottom:2px;\"><a href=\"javascript:void(0);hidePopup();\" title=\"Close Window\">" + closeText + "</a></span>";
            else
                closeText = "<span style=\"display:block;width:400;text-align:right;margin-bottom:2px;\"><a href=\"javascript:void(0);hidePopup();\" title=\"Close Window\">Close the Window</a></span>";
            writer.writeAttribute("title", tooltipText, null);
            writer.writeAttribute("onclick", new String("popupPopup('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event, '" + closeText + "');"), null);
        }
        
        if (cssClass != null) 
            writer.writeAttribute("class", cssClass, null);
        if (imageLink != null && imageLink.equals("true")) {
            writer.startElement("img", this);
            writer.writeAttribute("src", imageSource, null);
            writer.writeAttribute("alt", tooltipText, null);
            writer.writeAttribute("title", tooltipText, null);
            writer.endElement("img");
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
        String helpText = (String)getAttributes().get("tooltipMessage");
        ResponseWriter writer = facescontext.getResponseWriter();
        writer.startElement("input", this);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", new String(getClientId(facescontext) + ":hidField"), null);
        writer.writeAttribute("name", new String("hid" + getClientId(facescontext)), null);
        writer.writeAttribute("value", helpText, null);
        writer.endElement("input");
    }
}
