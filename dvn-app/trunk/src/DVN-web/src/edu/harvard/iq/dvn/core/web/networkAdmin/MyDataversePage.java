/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.networkAdmin;

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
 * @author Ellen Kraffmiller
 */
@ViewScoped
@Named("MyDataversePage")
public class MyDataversePage extends VDCBaseBean {
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
        
        VDCUser user = VDCBaseBean.getVDCSessionBean().getUser();
        
        if (user!=null) {
            // first refresh the user
            user = userService.find(user.getId());
            List<VDC> vdcs= vdcService.getUserVDCs(user.getId());
            dataverses = new ArrayList();
            for (VDC vdc: vdcs) {
                Object[] row = new Object[2];
                row[0]=(vdc);
                row[1]=(user.getVDCRole(vdc).getRole().getName());
                dataverses.add(row);
            }
        }
       
    }

    public String createDataverse() {
        VDCUser user = VDCBaseBean.getVDCSessionBean().getUser();
        userService.makeCreator(user.getId());
        return "addSite";
    }

}
