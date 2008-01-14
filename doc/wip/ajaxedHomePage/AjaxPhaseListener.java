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
 * AjaxPhaseListener.java
 *
 * Created on January 04, 2007
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.util;

import java.util.Iterator;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.ajax4jsf.framework.ajax.AjaxContext;

/**
 *
 * @author Wendy Bossons
 */
public class AjaxPhaseListener implements PhaseListener {
  
    
     public PhaseId getPhaseId() {
            return PhaseId.RENDER_RESPONSE;
     } 
     public void beforePhase(PhaseEvent pe){
        System.out.println("I am in the before phase ...");
        System.out.println("I am still in the before phase");
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (request.getParameter("AjaxRequest") != null || request.getAttribute("AjaxRequest") != null) {
            handleAjaxRequest(pe);
        }

     }
     
     private void handleAjaxRequest(PhaseEvent pe) {
         FacesContext context = pe.getFacesContext();
          String contenttype = context.getExternalContext().getResponseContentType();
          AjaxContext ajaxcontext = AjaxContext.getCurrentInstance(context);
          HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
          response.setContentType("text/html");
         // if (contenttype != null) {
              //try {
          response.setContentType("text/html");
          //Render the response if it's ajax
           HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
           String name = (String)request.getAttribute("AjaxRequest");
           //if (name != null && name.equals("page_action")) {
                try {
                UIComponent component = context.getViewRoot().findComponent(ajaxcontext.getSubmittedRegionClientId(context));
                System.out.println("the component to rerender is in" + component.getClientId(context));
                ajaxcontext.renderAjaxRegion(context, component, true);
                //context.responseComplete();
                }catch(Exception exception) {
                    exception.printStackTrace();
                }
                    System.out.println("The content type is " + response.getContentType());
           // }
         // } 
     }
     
     public void afterPhase(PhaseEvent pe) {
         System.out.println("in render response after phase ...");
     }
     
     //private void writeAjaxResponse(FacesContext context)
       //      throws IOException {
       //  context.renderResponse();
     //}
}
