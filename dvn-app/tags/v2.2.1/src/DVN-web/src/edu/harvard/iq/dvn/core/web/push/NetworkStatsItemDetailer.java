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

package edu.harvard.iq.dvn.core.web.push;

import edu.harvard.iq.dvn.core.web.push.beans.NetworkStatsItemBean;
import edu.harvard.iq.dvn.core.web.push.beans.NetworkStatsBean;
import edu.harvard.iq.dvn.core.web.push.stubs.ItemType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class used to populate network stats items with information. 
 * All item detailing is done in a separate thread so user interaction is
 * not blocked
 */
public class NetworkStatsItemDetailer implements Runnable {
    private static Log log =
            LogFactory.getLog(NetworkStatsItemDetailer.class);
    private NetworkStatsItemBean[] searchItemBeans;
    private NetworkStatsBean networkStatsBean;

    public NetworkStatsItemDetailer(NetworkStatsBean networkStatsBean,
                                      NetworkStatsItemBean[] searchItemBeans) {
        this.networkStatsBean = networkStatsBean;
        this.searchItemBeans =
                (NetworkStatsItemBean[]) searchItemBeans.clone();
    }

    public void run() {
        NetworkStatsItemBean itemBean;
        for (int i = 0, max = searchItemBeans.length; i < max; i++) {
            try {
                itemBean = searchItemBeans[i];
                ItemType item = networkStatsBean.getItem(itemBean.getItemID());
            } catch (NullPointerException npe) {
                // intentionally left blank
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("A threaded item detailer failed because of " + e);
                }
            }
        }
    }
}