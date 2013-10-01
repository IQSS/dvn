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
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */
package edu.harvard.iq.dvn.core.web.push.beans;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import edu.harvard.iq.dvn.core.web.push.NetworkStatsState;
import edu.harvard.iq.dvn.core.web.push.ReleaseEvent;
import edu.harvard.iq.dvn.core.web.push.stubs.ItemType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.context.FacesContext;




/**
 * Class used to handle searching and sorting of auction items, as well as front
 * end interpretation
 */
public class NetworkStatsItemBean extends ItemType {
    private static Log log = LogFactory.getLog(NetworkStatsItemBean.class);
    private Effect effect;
    
    //Network Stats
    private String dataverseTotal;
    private String studyTotal;
    private String filesTotal;

    private static final String SUCCESS = "success";

    public NetworkStatsItemBean(ItemType item) {
        super(item);
    }
    
    // action methods
    
    public String doRelease() {
        setDataverseTotal(Long.toString(new Long(dataverseTotal) + 1));
        NetworkStatsState.getNetworkStatsMap().put(getItemID() + ".dataverseTotal", new String(dataverseTotal));
        NetworkStatsState networkStatsState = NetworkStatsState.getInstance();
        getDataverseTotal();
        if (null != networkStatsState) {
            networkStatsState.fireNetworkStatsEvent(
                    new ReleaseEvent(getItemID(), dataverseTotal));
        }
        fireEffect();
        return SUCCESS;
    }
        
    public String doAddStudy() {
        setStudyTotal(Long.toString(new Long(studyTotal) + 1));
        NetworkStatsState.getNetworkStatsMap().put(getItemID() + ".studyTotal", new String(studyTotal));
        NetworkStatsState networkStatsState = NetworkStatsState.getInstance();
        getStudyTotal();
        if (null != networkStatsState) {
            networkStatsState.fireNetworkStatsEvent(
                    new ReleaseEvent(getItemID(), studyTotal));
        }
        fireEffect();
        return SUCCESS;
    }
    
    public String doAddFiles() {
        setFilesTotal(Long.toString(new Long(filesTotal) + 1));
        NetworkStatsState.getNetworkStatsMap().put(getItemID() + ".filesTotal", new String(filesTotal));
        NetworkStatsState networkStatsState = NetworkStatsState.getInstance();
        getFilesTotal();
        if (null != networkStatsState) {
            networkStatsState.fireNetworkStatsEvent(
                    new ReleaseEvent(getItemID(), filesTotal));
        }
        fireEffect();
        return SUCCESS;
    }
    
    /** Method to fire effect when needed for push
     * 
     */
    private void fireEffect() {
        
         if (effect == null) {
            effect = new Highlight("#FFCC0B");
         } 
         effect.setFired(false);
    }
    
    /**
     * Method to reset the network state This will perform 3 actions on each
     * item: reset dataverseTotal, reset studyTotal, reset expiry filesTotal
     */
    public static void resetStatistics() {
        String endDate;

        String bidId, prefix;
        Iterator keys = NetworkStatsState.getNetworkStatsMap().keySet().iterator();

        // Loop through the global list of available IDs
        while (keys.hasNext()) {
            bidId = keys.next().toString();
            if (bidId.indexOf(".id") > 0) {
                bidId = bidId.substring(0, bidId.indexOf(".id"));
                prefix = bidId + ".";

                // Reset the price and bid count of the current item
                NetworkStatsState.getNetworkStatsMap().put(prefix + "dataverseTotal", 
                        NetworkStatsState.getNetworkStatsMap().get(prefix + "initialDataverseTotal"));
                NetworkStatsState.getNetworkStatsMap().put(prefix + "studyTotal",
                        NetworkStatsState.getNetworkStatsMap().get(prefix + "initialStudyTotal"));
                NetworkStatsState.getNetworkStatsMap().put(prefix + "filesTotal",
                        NetworkStatsState.getNetworkStatsMap().get(prefix + "initialFilesTotal"));
            }
        }
    }
    
    //getters
    public Effect getEffect() {
        return effect;
    }

     //NETWORK STATS
     public String getDataverseTotal() {
            dataverseTotal = NetworkStatsState.getNetworkStatsMap().get(getItemID() + ".dataverseTotal").toString();
            
         
         return dataverseTotal;
    }
     
     public String getStudyTotal() {
            studyTotal = NetworkStatsState.getNetworkStatsMap().get(getItemID() + ".studyTotal").toString();
         
         return studyTotal;
    }
     
     public String getFilesTotal() {
            filesTotal = NetworkStatsState.getNetworkStatsMap().get(getItemID() + ".filesTotal").toString();
         
         return filesTotal;
    }
     
    //setters
    public void setEffect(Effect effect) {
        this.effect = effect;
    }

   // NETWORK TOTALS
   public void setDataverseTotal(String dataverseTotal) {
        this.dataverseTotal = dataverseTotal;
    }
   
   public void setStudyTotal(String studyTotal) {
        this.studyTotal = studyTotal;
    }
   
   public void setFilesTotal(String filesTotal) {
        this.filesTotal = filesTotal;
    }
  
}
