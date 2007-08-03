/*
 * VariablePage.java
 *
 * Created on March 14, 2007, 5:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.study;

import edu.harvard.hmdc.vdcnet.study.DataVariable;
import edu.harvard.hmdc.vdcnet.study.VariableServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 *
 * @author gdurand
 */
public class VariablePage extends VDCBaseBean {
    
    @EJB VariableServiceLocal varService;
    
    /** Creates a new instance of VariablePage */
    public VariablePage() {
    }

    private Long dvId;
    private DataVariable variable;
    
    public void init() {
        super.init();
        
        // we need to create the studyServiceBean
        if (getDvId() != null) {
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
