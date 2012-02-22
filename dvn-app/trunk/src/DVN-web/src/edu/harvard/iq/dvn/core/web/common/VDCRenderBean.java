/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.common;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import org.jboss.seam.faces.context.RenderScoped;

/**
 *
 * @author gdurand
 */

@Named("VDCRender")
@RenderScoped
public class VDCRenderBean  implements java.io.Serializable  {
    
    Map<String,Object> flash = new HashMap();

    public Map getFlash() {
        return flash;
    }

    public void setFlash(Map flash) {
        this.flash = flash;
    }

}