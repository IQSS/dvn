/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.admin;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author skraffmiller
 */
@ViewScoped
@Named("ChooseDataverseForCreateStudy")
public class ChooseDataverseForCreateStudy extends VDCBaseBean {
    
    @EJB VDCServiceLocal vdcService;
    @EJB UserServiceLocal userService;
    
    private List dataverses;
    
    public List getDataverses() {
        if (dataverses==null) {
            initDataverses();
        }
        return dataverses;
    }

    private void initDataverses() {
        
        VDCUser user = getVDCSessionBean().getUser();
        
        if (user!=null) {
            // first refresh the user
            user = userService.find(user.getId());
            List<VDC> vdcs= vdcService.getUserVDCs(user.getId());
            dataverses = new ArrayList();
            for (VDC vdc: vdcs) {
                Object[] row = new Object[1];
                row[0]=(vdc);

                dataverses.add(row);
            }
        }
       
    }

}

