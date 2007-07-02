/*
 * InlineHelpSupport.java
 *
 * Created on May 3, 2007, 2:07 PM
 */

package edu.harvard.hmdc.vdcnet.web.component;

import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Generated tag handler class.
 * @author  wbossons
 * @version
 */

public class InlineHelpSupport extends UIComponentBase {
    
    /** Creates new instance of tag handler */
    public InlineHelpSupport() {
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
            writer.writeAttribute("language", "JavaScript", null);
            writer.writeAttribute("src", "/dvn/resources/PopupWindow.js", null);
            writer.endElement("script");
            writer.startElement("script", this);
            writer.writeAttribute("language", "JavaScript", null);
            writer.writeAttribute("src", "/dvn/resources/popupSupport.js", null);
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
    }
    
    
}
