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
package edu.harvard.iq.dvn.core.web.networkAdmin;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import edu.harvard.iq.dvn.core.vdc.OAISet;
import edu.harvard.iq.dvn.core.vdc.OAISetServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Ellen Kraffmiller
 */
@ViewScoped
@Named("EditOAISetPage")
public class EditOAISetPage extends VDCBaseBean implements java.io.Serializable  {
    @EJB OAISetServiceLocal oaiSetService;
    public EditOAISetPage() {
    }
    
    public void init() {
        super.init();
        oaiSet = new OAISet();
        System.out.println("oaiSetId is "+oaiSetId);
        if ( !isFromPage("EditOAISetPage") && oaiSetId!=null) {
            oaiSet = oaiSetService.findById(oaiSetId);
        }
        originalSpec = oaiSet.getSpec();
        originalName = oaiSet.getName();
    }

    public OAISet getOaiSet() {
        return oaiSet;
    }

    public void setOaiSet(OAISet oaiSet) {
        this.oaiSet = oaiSet;
    }

    public Long getOaiSetId() {
        return oaiSetId;
    }

    public void setOaiSetId(Long oaiSetId) {
        this.oaiSetId = oaiSetId;
    }
    public void validateSpec(FacesContext context,
            UIComponent toValidate,
            Object value) {
 
        String spec = (String)value;
        boolean valid = true;
        /*
        // This isn't working because of a class loader issue - we aren't able
        // to catch the excecption because the instance doesn't match the class loader of the class.
        // Saving this so we can revisit it later
         
        if (spec.equals(originalSpec) ) {
            valid = true;
        } else {
            try {
                OAISet set = this.oaiSetService.findBySpec(spec);
                //ORG.oclc.oai.server.verb.NoItemsMatchException
                //ORG.oclc.oai.server.verb.NoItemsMatchException
            } catch (ORG.oclc.oai.server.verb.NoItemsMatchException e) {
                valid = true;
            } catch (Exception e) {
                System.out.println("class classloader: "+ORG.oclc.oai.server.verb.NoItemsMatchException.class.getClassLoader() );
                System.out.println("object classloader: "+e.getClass().getClassLoader());
                System.out.println("EXCEPTION!!!! class is " + e.getClass().getCanonicalName()+", "+e.getClass().getSimpleName());
                e.printStackTrace();
                if (e instanceof ORG.oclc.oai.server.verb.NoItemsMatchException) {
                    valid=true;
                }
            }
        }

       */

        if(oaiSetService.specExists(spec)) {
            valid = false;
        }
        //SEK added 11/02/2012 - based on commented code above this should be OK
 
        if (spec.equals(originalSpec) ) {
            valid = true;
        }
        if (!valid) {
            ((UIInput) toValidate).setValid(false);
            FacesMessage message = new FacesMessage("OAI Spec already exists.");
            context.addMessage(toValidate.getClientId(context), message);
        }


    }

    public String save() {
        oaiSetService.update(oaiSet);
        getVDCRenderBean().getFlash().put("successMessage",SUCCESS_MESSAGE);
        return "/networkAdmin/NetworkOptionsPage?faces-redirect=true&tab=harvesting&tab2=oaisets";    
    }    
    
    public String cancel() {
        return "/networkAdmin/NetworkOptionsPage?faces-redirect=true&tab=harvesting&tab2=oaisets";
    }
    
    OAISet oaiSet;
    Long oaiSetId;

    private String originalSpec;
    private String originalName;
    
      private String SUCCESS_MESSAGE = new String("Update Successful!");
  
    
}
