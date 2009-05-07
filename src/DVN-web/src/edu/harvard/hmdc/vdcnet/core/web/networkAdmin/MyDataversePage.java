/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.core.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.core.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.core.vdc.VDC;
import edu.harvard.hmdc.vdcnet.core.vdc.VDCServiceLocal;
import edu.harvard.hmdc.vdcnet.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author Ellen Kraffmiller
 */
public class MyDataversePage  {
    @EJB VDCServiceLocal vdcService;
    
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

}
