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