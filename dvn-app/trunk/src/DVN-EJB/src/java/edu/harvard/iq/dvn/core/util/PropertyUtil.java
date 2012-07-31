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
package edu.harvard.iq.dvn.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Ellen Kraffmiller
 */
public class PropertyUtil implements java.io.Serializable  {
    public static String getHostUrl() {
            String hostUrl = System.getProperty("dvn.inetAddress");
            if (hostUrl==null) {
                try {
                        hostUrl= InetAddress.getLocalHost().getCanonicalHostName();
                } catch(UnknownHostException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return hostUrl;
        }
    /**
     * Returns the value of JVM property dvn.timerServer
     * The default value of this property is true (so that in a simple single server
     * configuration, the single server is automatically the "timer server" )
     * @return
     */
    public static boolean isTimerServer() {
        boolean isTimerServer = true;
        String timerServer = System.getProperty("dvn.timerServer");
        if (timerServer!=null) {
            isTimerServer = Boolean.parseBoolean(timerServer);
        }
        return isTimerServer;

    }
    public PropertyUtil() {
      
    }

    /**
     * This method returns the hostname of the timer server, which is needed for the LOCKSS manifest page,
     * to display the OAI URL for lockss harvesting. (We may find other uses for this in the future.)
     *
     * If this instance is the timer server, returns the canonical host name.  Else, it will look for
     * a JVM option 'dvn.timerServerHost'. If this is not the timer server, and the JVM option is not set,
     * @return hostname of the timer server
     * @throws RuntimeException - Exception is thrown if this is not the timer server, and the JVM option 'dvn.timerServer' is not set.
     */
    public static String getTimerServerHost() {
        if (isTimerServer()) {
            try {
                return InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            String timerServerHost = System.getProperty("dvn.timerServerHost");
            if (timerServerHost == null) {
                throw new RuntimeException("Missing JVM option: dvn.timerServerHost.  If JVM option dvn.timerServer is set to 'false', then timerServerHost must be defined.");
            }
            return timerServerHost;
        }
    }
    
    
    public boolean isTwitterConsumerConfigured() {
        if (System.getProperty("twitter4j.oauth.consumerKey") != null && System.getProperty("twitter4j.oauth.consumerSecret") != null ) {
            return true;
        }
        
        return false;
    }      

}
