/*
 * Tooltip.java
 *
 * Created on April 30, 2007, 2:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.component;

import javax.faces.context.FacesContext;
import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.ResponseWriter;
/**
 *
 * @author wbossons
 */
public class Tooltip extends UIComponentBase {
    
    /** Creates a new instance of Tooltip */
    public Tooltip() {
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
        if (heading == null)
            heading = "";
        writer.startElement("a", this);
        writer.writeAttribute("id", getClientId(facescontext), null);
        writer.writeAttribute("name", getClientId(facescontext), null);
        writer.writeAttribute("href", linkUrl, null);
        if (eventType.equals("mouseover")) {
            writer.writeAttribute("onmouseover", new String("javascript:popupTooltip('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event);"), null);
            writer.writeAttribute("onmouseout", new String("javascript:hideTooltip();"), null);
        } else if (eventType.equals("click")) {
            writer.writeAttribute("title", tooltipText, null);
            writer.writeAttribute("onclick", new String("javascript:popupTooltip('" + getClientId(facescontext) + "', document.getElementById('" + new String(getClientId(facescontext) + ":hidField") + "').value" + ", '" + heading + "', event);"), null);
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
