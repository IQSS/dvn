/*
 * VDCUI.java
 *
 * Created on March 9, 2007, 2:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author gdurand
 */
public class VDCUI {
    
    VDC vdc;
    
    /** Creates a new instance of VDCUI */
    public VDCUI(VDC vdc) {
        this.vdc = vdc;
    }
    
    public List getLinkedCollections() {
        return getLinkedCollections(false);
    }
    
    public List getLinkedCollections(boolean getHiddenCollections) {
        if (getHiddenCollections) {
            return vdc.getLinkedCollections();
        } else {
            List linkedColls = new ArrayList();
            Iterator iter = vdc.getLinkedCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection link = (VDCCollection) iter.next();
               if (link.isVisible()) {
                    linkedColls.add( link );
               }
            }
            
            return linkedColls;
        }
    }
}
