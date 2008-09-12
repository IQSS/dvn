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

package edu.harvard.hmdc.vdcnet.web.push.stubs;

import edu.harvard.hmdc.vdcnet.web.push.NetworkStatsState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.naming.InitialContext;

import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;

/**
 * NetworkStatsStubServer
 * Based on the ICESoft auctionMonitor application
 */
public class NetworkStatsStubServer {
    
    
    // Variables
    private ItemType[] itemList = null;
    private static NetworkStatsStubServer ourInstance = new NetworkStatsStubServer();
    private static Log log = LogFactory.getLog(NetworkStatsStubServer.class);

   /**
     * Private constructor to fulfill singleton requirements
     */
    private NetworkStatsStubServer () {
        if (NetworkStatsState.getNetworkStatsMap().isEmpty()) {
            initNetworkData(); //For network home page modeling of push content 
        }
    }

    public static synchronized NetworkStatsStubServer getInstance() {
        return ourInstance;
    }

    /**
     * Method used to find and read an item properties file The item values will
     * then be loaded into the global network state In the getSearchResults
     * method, these values will be converted into a list of ItemTypes
     */
    
    //Network Data
      /**
     * Holds value of property total.
     */
    VDCNetworkServiceLocal vdcNetworkService = null;
    
    private static final String DVNITEM                 = "item";
    private static final String DVNID                   = "id";
    private static final String DATAVERSETOTAL          = "dataverseTotal";
    private static final String STUDYTOTAL              = "studyTotal";
    private static final String FILESTOTAL              = "filesTotal";
    private static final String INITIAL_DATAVERSETOTAL  = "initialDataverseTotal";
    private static final String INITIAL_STUDYTOTAL      = "initialStudyTotal";
    private static final String INITIAL_FILESTOTAL      = "initialFilesTotal";
    private static final String DATAVERSELABEL          = "dataverseLabel";
    private static final String STUDYLABEL              = "studyLabel";
    private static final String FILESLABEL              = "filesLabel";
    
    private String dataverseTotal;
    private String studyTotal;
    private String filesTotal;
    
     private void initNetworkData() {
        boolean isReleased         = true;
         // Loop through each property and put the values in the global network stats state
        // this can be made more dynamic by using a while loop if outputting a set of these values,
        // for example if each group had a listing of the number of dataverses. For now, it will only
        // have a size of 1.
        int itemCounter = 0;
        String itemPrefix, idValue, key;
        itemPrefix = DVNITEM + itemCounter + ".";
        //idValue = props.getProperty(itemPrefix + ID);
        try {
            vdcNetworkService = (VDCNetworkServiceLocal) new InitialContext().lookup("java:comp/env/vdcNetworkService");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        idValue = DVNITEM + itemCounter;
        key = itemPrefix;
        // No more item sets were found, so break the loop
        // TODO: increment this if we become interested in the # of dataverses per group
            //if (idValue == null) {
               // break;
            //}
        
        NetworkStatsState.getNetworkStatsMap().put(key + DVNID, idValue);
        NetworkStatsState.getNetworkStatsMap().put(key + DATAVERSETOTAL, this.getTotalDataverses(isReleased));
        NetworkStatsState.getNetworkStatsMap().put(key + STUDYTOTAL, this.getTotalStudies(isReleased));
        NetworkStatsState.getNetworkStatsMap().put(key + FILESTOTAL, this.getTotalFiles(isReleased));
        NetworkStatsState.getNetworkStatsMap().put(key + INITIAL_DATAVERSETOTAL, new String("0"));
        NetworkStatsState.getNetworkStatsMap().put(key + INITIAL_STUDYTOTAL, new String("0"));
        NetworkStatsState.getNetworkStatsMap().put(key + INITIAL_FILESTOTAL, new String("0"));
        NetworkStatsState.getNetworkStatsMap().put(key + DATAVERSELABEL, "Dataverses: ");
        NetworkStatsState.getNetworkStatsMap().put(key + STUDYLABEL, "Studies: ");
        NetworkStatsState.getNetworkStatsMap().put(key + FILESLABEL, "Files: ");
       // itemCounter++; TODO: increment this if we become interested in the # of dataverses per group
     }
    
    /**
     * Getters 
     */
    public String getTotalDataverses(boolean released) {
        try {
            vdcNetworkService = (VDCNetworkServiceLocal) new InitialContext().lookup("java:comp/env/vdcNetworkService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("the total dataverses are " + vdcNetworkService.getTotalDataverses(released));
        return vdcNetworkService.getTotalDataverses(released).toString();
    }
    
    public String getTotalStudies(boolean released) {
        try {
            vdcNetworkService = (VDCNetworkServiceLocal) new InitialContext().lookup("java:comp/env/vdcNetworkService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vdcNetworkService.getTotalStudies(released).toString();
    }
    
    public String getTotalFiles(boolean released) {
        try {
            vdcNetworkService = (VDCNetworkServiceLocal) new InitialContext().lookup("java:comp/env/vdcNetworkService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vdcNetworkService.getTotalFiles(released).toString();
    }

    /**
     * Method to convert the global properties file values into ItemTypes
     * Ideally this method would use the Ebay SDK, but for now will "fake it"
     * with the property file
     *
     * @return ItemType[] resulting list of auction items
     */
    public ItemType[] getSearchResults() {
        // Ensure that the item list is only read once
        if (itemList == null) {
            ItemType it;
            ArrayList items = new ArrayList();
            String targetId, prefix;

            // Loop until no new properties remain
            Iterator keys = NetworkStatsState.getNetworkStatsMap().keySet().iterator();
            while (keys.hasNext()) {
                targetId = keys.next().toString();
                // Ensure a valid ID is present before using this property
                if (targetId.indexOf("." +  DVNID) > 0) {
                    // Break down the target ID into a usable value
                    targetId = targetId.substring(0, targetId.indexOf(".id"));
                    prefix = targetId + ".";

                    // Create a new ItemType and start to populate it with the required values
                    it = new ItemType();

                    // Set the ID
                    it.setItemID(targetId);
                    it.setDataverseLabel(NetworkStatsState.getNetworkStatsMap().get(prefix + DATAVERSELABEL).toString());
                    it.setStudyLabel(NetworkStatsState.getNetworkStatsMap().get(prefix + STUDYLABEL).toString());
                    it.setFilesLabel(NetworkStatsState.getNetworkStatsMap().get(prefix + FILESLABEL).toString());
                    items.add(it);
                }
            }

            // Convert the results to a simple array and store them
            itemList = (ItemType[]) items.toArray(new ItemType[items.size()]);
        }

        return (itemList);
    }

    /**
     * Method to retrieve a single ItemType with a matching itemID
     *
     * @param itemID itemID to match
     * @return ItemType matching item, or null if not found
     */
    public ItemType getItem(String itemID) {
        // Get the whole list of items
        ItemType[] items = getSearchResults();
        ItemType item;

        // Loop through the list of items looking for a matching ID 
        for (int index = 0; index < items.length; index++) {
            item = items[index];
            if (itemID.equals(item.getItemID())) {
                return item;
            }
        }

        return null;
    }
}
