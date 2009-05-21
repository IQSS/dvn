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
 * DvnInlineHelpSupport.java
 *
 * Created on May 3, 2007, 2:07 PM
 */

package edu.harvard.iq.dvn.core.web.component;

import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Generated tag handler class.
 * @author  wbossons
 * @version
 */

public class DvnInlineHelpSupport extends UIComponentBase implements java.io.Serializable  {
    
    /** Creates new instance of tag handler */
    public DvnInlineHelpSupport() {
        super();
    }
    
    ////////////////////////////////////////////////////////////////
    ///                                                          ///
    ///   User methods.                                          ///
    ///                                                          ///
    ////////////////////////////////////////////////////////////////
    
    public String getFamily() {
        return null;
    }
    
    public void encodeBegin(FacesContext facescontext)
        throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
        //the source javascript
            writer.startElement("script", this);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("src", "/dvn/resources/javascript/PopupWindow.js", null);
            writer.endElement("script");
            writer.startElement("script", this);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("src", "/dvn/resources/javascript/popupSupport.js", null);
            writer.endElement("script");
    }
    
     public void encodeChildren(FacesContext facescontext)
        throws IOException {
        //Nothing to do
        
    }
     
     public void encodeEnd(FacesContext facescontext) 
        throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
        boolean writeHelpDiv = Boolean.parseBoolean((String)getAttributes().get("writeHelpDiv"));
        if (writeHelpDiv) {
            writer.startElement("div", this);
            writer.writeAttribute("id", "helpDiv", null);
            writer.writeAttribute("style", "visibility:hidden;", null);
            writer.writeAttribute("class", "vdcInlineHelp", null);
            writer.writeText("", null);
            writer.endElement("div");
        }
        boolean writeTipDiv = Boolean.parseBoolean((String)getAttributes().get("writeTipDiv"));
        if (writeTipDiv) {
            writer.startElement("div", this);
            writer.writeAttribute("id", "tooltipDiv", null);
            writer.writeAttribute("style", "visibility:hidden;", null);
            writer.writeAttribute("class", "vdcTooltip", null);
            writer.writeText("", null);
            writer.endElement("div");
        }
        boolean writePopupDiv = Boolean.parseBoolean((String)getAttributes().get("writePopupDiv"));
        if (writePopupDiv) {
            writer.startElement("div", this);
            writer.writeAttribute("id", "popupDiv", null);
            writer.writeAttribute("style", "visibility:hidden;", null);
            writer.writeAttribute("class", "vdcPopup", null);
            writer.writeText("", null);
            writer.endElement("div");
        }
    }
    
    
}
