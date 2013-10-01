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
