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

import com.icesoft.faces.async.render.OnDemandRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.ext.HtmlInputHidden;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;

import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.web.push.NetworkStatsItemDetailer;
import edu.harvard.iq.dvn.core.web.push.NetworkStatsListener;
import edu.harvard.iq.dvn.core.web.push.NetworkStatsState;
import edu.harvard.iq.dvn.core.web.push.ReleaseEvent;
import edu.harvard.iq.dvn.core.web.push.stubs.ItemType;
import edu.harvard.iq.dvn.core.web.push.stubs.NetworkStatsStubServer;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.html.HtmlOutputText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class used to handle searching and sorting of auction items, as well as front
 * end interpretation
 */
public class NetworkStatsBean implements NetworkStatsListener, Renderable, DisposableBean {
    private static Log log = LogFactory.getLog(NetworkStatsBean.class);
    private static int userCount = 0;
    public static final String RENDERER_NAME = "demand";
    private static final String SUCCESS = "success";
    private String queryItemID;
    private String queryString;
    private NetworkStatsItemBean[] searchItemBeans;
    private String autoLoad = " ";
    private PersistentFacesState persistentState = null;
    private boolean isFreshSearch;
    private NetworkStatsItemBean queryItem;
    private OnDemandRenderer renderer = null;

    // style related constants

    // title text
    private static final String TABLE_HEADER_ASC_TITLE = "tableHeaderSelAsc1";
    private static final String TABLE_HEADER_DESC_TITLE = "tableHeaderSelDesc1";
    private static final String TABLE_HEADER_TITLE = "column1";

    // bids text
    private static final String TABLE_HEADER_ASC_BID = "tableHeaderSelAsc2";
    private static final String TABLE_HEADER_DESC_BID = "tableHeaderSelDesc2";
    private static final String TABLE_HEADER_BID = "column2";

    // price text
    private static final String TABLE_HEADER_ASC_PRICE = "tableHeaderSelAsc3";
    private static final String TABLE_HEADER_DESC_PRICE = "tableHeaderSelDesc3";
    private static final String TABLE_HEADER_PRICE = "column3";

    // time left column
    private static final String TABLE_HEADER_ASC_TIME = "tableHeaderSelAsc4";
    private static final String TABLE_HEADER_DESC_TIME = "tableHeaderSelDesc4";
    private static final String TABLE_HEADER_TIME = "column4";


    public NetworkStatsBean() {
        NetworkStatsState.getInstance().addNetworkStatsListener(this);
        persistentState = PersistentFacesState.getInstance();
    }

    public ItemType getItem(String itemIDStr) throws Exception {
        return NetworkStatsStubServer.getInstance().getItem(itemIDStr);
    }

    public ItemType[] getSearchResults(String filterString) throws Exception {
        return NetworkStatsStubServer.getInstance().getSearchResults();
    }

    public synchronized NetworkStatsItemBean[] getSearchItems() {
        if (null == queryString) {
            return null;
        }
        if (!isFreshSearch) {
            return searchItemBeans;
        }

        try {
            ItemType searchItems[] = getSearchResults(queryString);
            if (null == searchItems) {
                return null;
            }
            searchItemBeans = new NetworkStatsItemBean[searchItems.length];

            for (int i = 0; i < searchItems.length; i++) {
                searchItemBeans[i] = new NetworkStatsItemBean(searchItems[i]);
            }
            isFreshSearch = false;
            Thread t = new Thread(
                    new NetworkStatsItemDetailer(this, searchItemBeans));
            t.start();
            return searchItemBeans;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to read the available search items because of " + e);
            }
        }
        return null;
    }

    public void reSearchItems() {
        isFreshSearch = true;
        getSearchItems();
    }

    public void setQueryItemID(String queryItemID) {
        this.queryItemID = queryItemID;
        try {
            queryItem = new NetworkStatsItemBean(getItem(queryItemID));
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to retrieve a query item based on ID " +
                         queryItemID + " because of " + e);
            }
        }
    }

    public String getQueryItemID() {
        return queryItemID;
    }

    public void setQueryItem(NetworkStatsItemBean queryItem) {
        this.queryItem = queryItem;
    }

    public NetworkStatsItemBean getQueryItem() {
        return queryItem;
    }

    public void setQueryString(String queryString) {
        if ("".equals(queryString)) {
            return;
        }

        if (queryString.equals(this.queryString)) {
            return;
        }

        this.queryString = queryString;
        isFreshSearch = true;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getAutoLoad() {
        if (this.autoLoad.equals(" ")) {
            this.autoLoad = "Loaded";
            this.setQueryString("ice");
        }

        return this.autoLoad;
    }

    public void handleReleaseEvent(ReleaseEvent releaseEvent) {
        reRender();
    }

    public static synchronized void incrementUsers() {
        userCount++;
    }

    public static synchronized void decrementUsers() {
        userCount--;

        if (userCount <= 0) {
            userCount = 0;
            //NetworkStatsStubServer.resetStatistics();
        }
    }

    private HtmlOutputText inlineDataverseKey;
    private String inlineDataverseValue;
    private HtmlOutputText inlineStudyKey;
    private String inlineStudyValue;
    private HtmlOutputText inlineFilesKey;
    private String inlineFilesValue;

    // Getters -----------------------------------------------------------------------------------

    public HtmlOutputText getInlineDataverseKey() {
        return inlineDataverseKey;
    }

    public synchronized String getInlineDataverseValue() {
        if (null == queryString) {
            return null;
        }
        if (!isFreshSearch) {
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getDataverseLabel() + itembean.getDataverseTotal();
                }
            }
        }
        try {
            ItemType searchItems[] = getSearchResults(queryString);
            if (null == searchItems) {
                return null;
            }
            searchItemBeans = new NetworkStatsItemBean[searchItems.length];

            for (int i = 0; i < searchItems.length; i++) {
                searchItemBeans[i] = new NetworkStatsItemBean(searchItems[i]);
            }
            isFreshSearch = false;
            Thread t = new Thread(
                    new NetworkStatsItemDetailer(this, searchItemBeans));
            t.start();
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getDataverseLabel() + itembean.getDataverseTotal();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Failed to read the available search items because of " + e);
            }
        }
        return null;
    }

    public HtmlOutputText getInlineStudyKey() {
        return inlineStudyKey;
    }

    public synchronized String getInlineStudyValue() {
        if (null == queryString) {
            return null;
        }
        if (!isFreshSearch) {
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getStudyLabel() + itembean.getStudyTotal();
                }
            }
        }
        try {
            ItemType searchItems[] = getSearchResults(queryString);
            if (null == searchItems) {
                return null;
            }
            searchItemBeans = new NetworkStatsItemBean[searchItems.length];

            for (int i = 0; i < searchItems.length; i++) {
                searchItemBeans[i] = new NetworkStatsItemBean(searchItems[i]);
            }
            isFreshSearch = false;
            Thread t = new Thread(
                    new NetworkStatsItemDetailer(this, searchItemBeans));
            t.start();
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getStudyLabel() + itembean.getStudyTotal();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Failed to read the available search items because of " + e);
            }
        }
        return null;
    }

    public HtmlOutputText getInlineFilesKey() {
        return inlineFilesKey;
    }

    public synchronized String getInlineFilesValue() {
        if (null == queryString) {
            return null;
        }
        if (!isFreshSearch) {
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getFilesLabel() + itembean.getFilesTotal();
                }
            }
        }
        try {
            ItemType searchItems[] = getSearchResults(queryString);
            if (null == searchItems) {
                return null;
            }
            searchItemBeans = new NetworkStatsItemBean[searchItems.length];

            for (int i = 0; i < searchItems.length; i++) {
                searchItemBeans[i] = new NetworkStatsItemBean(searchItems[i]);
            }
            isFreshSearch = false;
            Thread t = new Thread(
                    new NetworkStatsItemDetailer(this, searchItemBeans));
            t.start();
            for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals((String) inlineDataverseKey.getAttributes().get("itemid"))){
                    return itembean.getFilesLabel() + itembean.getFilesTotal();
                }
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Failed to read the available search items because of " + e);
            }
        }
        return null;
    }

    // Setters -----------------------------------------------------------------------------------

    public void setInlineDataverseKey(HtmlOutputText dataversekey) {
        this.inlineDataverseKey = dataversekey;
    }

    public void setInlineStudyKey(HtmlOutputText studykey) {
        this.inlineStudyKey = studykey;
    }

    public void setInlineFilesKey(HtmlOutputText fileskey) {
        this.inlineFilesKey = fileskey;
    }

    public void releaseAndUpdateInlineDataverseValue(Long id, List<VDCGroup> groups) {
        String dataversevalue;
        String itemid = null;
        Iterator iterator = groups.iterator();
        Long newTotal;
        long localTotal;
        NetworkStatsState networkStatsState;
        if (groups.isEmpty()) {
            for (int i = 0; i < searchItemBeans.length; i++) {
                    NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                    if (itembean.getItemID().equals("item0")) { // increment the top level -- item0;
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal + 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                                networkStatsState.fireNetworkStatsEvent(
                                new ReleaseEvent("item0", newTotal.toString()));
                                NetworkStatsState.getNetworkStatsMap().put("item0.dataverseTotal", newTotal.toString());
                        }
                    }
            }
        } else {
        while (iterator.hasNext()) {
            VDCGroup group = (VDCGroup)iterator.next();
            itemid = "item" + group.getId().toString();
            for (int i = 0; i < searchItemBeans.length; i++) {
                    NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                    if (itembean.getItemID().equals("item0")) { // increment the top level -- item0;
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal + 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                                networkStatsState.fireNetworkStatsEvent(
                                new ReleaseEvent("item0", newTotal.toString()));
                                NetworkStatsState.getNetworkStatsMap().put("item0.dataverseTotal", newTotal.toString());
                        }
                    }
                    if (itembean.getItemID().equals(itemid)){
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal + 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                            networkStatsState.fireNetworkStatsEvent(
                            new ReleaseEvent(itemid, newTotal.toString()));
                            NetworkStatsState.getNetworkStatsMap().put(itemid + ".dataverseTotal", newTotal.toString());
                        }
                    }
                }
            }
        }
    }

    public void restrictAndUpdateInlineDataverseValue(Long id, List<VDCGroup> groups) {
        String dataversevalue;
        String itemid = null;
        Iterator iterator = groups.iterator();
        Long newTotal;
        long localTotal;
        NetworkStatsState networkStatsState;
        if (groups.isEmpty()) {
            for (int i = 0; i < searchItemBeans.length; i++) {
                    NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                    if (itembean.getItemID().equals("item0")) { // decrement the top level -- item0;
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal - 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                                networkStatsState.fireNetworkStatsEvent(
                                new ReleaseEvent("item0", newTotal.toString()));
                                NetworkStatsState.getNetworkStatsMap().put("item0.dataverseTotal", newTotal.toString());
                        }
                    }
            }
        } else {
            while (iterator.hasNext()) {
                VDCGroup group = (VDCGroup)iterator.next();
                itemid = "item" + group.getId().toString();
                for (int i = 0; i < searchItemBeans.length; i++) {
                    NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                    if (itembean.getItemID().equals("item0")) { // decrement the top level -- item0;
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal - 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                                networkStatsState.fireNetworkStatsEvent(
                                new ReleaseEvent("item0", newTotal.toString()));
                                NetworkStatsState.getNetworkStatsMap().put("item0.dataverseTotal", newTotal.toString());
                        }
                    }
                    if (itembean.getItemID().equals(itemid)){
                        localTotal = new Long(itembean.getDataverseTotal());
                        newTotal = localTotal - 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                            networkStatsState.fireNetworkStatsEvent(
                            new ReleaseEvent(itemid, newTotal.toString()));
                            NetworkStatsState.getNetworkStatsMap().put(itemid + ".dataverseTotal", newTotal.toString());
                        }
                    }
                }
            }
          }
        }

    public void releaseAndUpdateInlineStudiesValue(Long id, List<VDCGroup> groups) {
        //String dataversevalue;
        String itemid = null;
        Iterator iterator = groups.iterator();
        Long newTotal;
        long localTotal;
        NetworkStatsState networkStatsState;
        for (int i = 0; i < searchItemBeans.length; i++) {
                NetworkStatsItemBean itembean = (NetworkStatsItemBean) searchItemBeans[i];
                if (itembean.getItemID().equals("item0")) { // increment the top level -- item0;
                    localTotal = new Long(itembean.getStudyTotal());
                    newTotal = localTotal + 1;
                    networkStatsState = NetworkStatsState.getInstance();
                    if (null != networkStatsState) {
                            networkStatsState.fireNetworkStatsEvent(
                            new ReleaseEvent("item0", newTotal.toString()));
                            NetworkStatsState.getNetworkStatsMap().put("item0.studyTotal", newTotal.toString());
                    }
                }
                while (iterator.hasNext()) {
                    VDCGroup group = (VDCGroup)iterator.next();
                    itemid = "item" + group.getId().toString();
                    if (itembean.getItemID().equals(itemid)){
                        localTotal = new Long(itembean.getStudyTotal());
                        newTotal = localTotal + 1;
                        networkStatsState = NetworkStatsState.getInstance();
                        if (null != networkStatsState) {
                            networkStatsState.fireNetworkStatsEvent(
                            new ReleaseEvent(itemid, newTotal.toString()));
                            NetworkStatsState.getNetworkStatsMap().put(itemid + ".studyTotal", newTotal.toString());
                        }
                    }
                }
            }
        }

    
    public void reRender() {
        if (renderer != null) {
            renderer.requestRender();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("OnDemandRenderer was not available (it was null)");
            }
        }
    }

    public void setRenderManager(RenderManager manager) {
        if (manager != null) {
            renderer = manager.getOnDemandRenderer(RENDERER_NAME);
            renderer.add(this);
        }
    }

    public PersistentFacesState getState() {
        return persistentState;
    }

    public void renderingException(RenderingException renderingException) {
        if (log.isDebugEnabled() &&
                renderingException instanceof TransientRenderingException) {
            log.debug("NetworkStatsBean Transient Rendering exception:", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (log.isDebugEnabled()) {
                log.debug("NetworkStatsBean Fatal rendering exception: ", renderingException);
            }
            performCleanup();
        }
    }

    protected boolean performCleanup() {
        try {
            // remove ourselves from the render group.
            if (renderer != null){
                renderer.remove(this);
            }
            return true;
        } catch (Exception failedCleanup) {
            if (log.isErrorEnabled()) {
                log.error("Failed to cleanup a clock bean", failedCleanup);
            }
        }
        return false;
    }

    /**
     * View has been disposed either by window closing or a session timeout.
     */
    public void dispose() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("NetworkStatsBean Dispose called - cleaning up");
        }
        performCleanup();
    }
}
