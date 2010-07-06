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
 * VariablePage.java
 *
 * Created on March 14, 2007, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 *
 * @author gdurand
 */
public class VariablePage extends VDCBaseBean implements java.io.Serializable  {
    
    @EJB VariableServiceLocal varService;
    
    /** Creates a new instance of VariablePage */
    public VariablePage() {
    }

    private Long dvId;
    private DataVariable variable;
    
    public void init() {
        super.init();
        
        // we need to create the studyServiceBean
        if (dvId != null) {
            setVariable(varService.getDataVariable(getDvId()));
            
        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in variablePage, without a dvId");
        }
        
    }

    public Long getDvId() {
        return dvId;
    }

    public void setDvId(Long dvId) {
        this.dvId = dvId;
    }

    public DataVariable getVariable() {
        return variable;
    }

    public void setVariable(DataVariable variable) {
        this.variable = variable;
    }
    
}
