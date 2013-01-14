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
package edu.harvard.iq.dvn.core.web.util;

import java.io.Serializable;

/*
 * Tab.java
 *
 * Created onFebruary 13, 5:00 PM
 *
 * @author wbossons
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
  public class Tab implements Comparable, Serializable {
        
        private String key;
        private String name;
        private String noDisplayMsg;
        private Integer order;
        public Tab() {

        }

        public Tab(String key, String name, String noDisplayMsg, int order) {
            this.key        = key;
            this.name     = name;
            this.noDisplayMsg = noDisplayMsg;
            this.order      = order;
        }

        public String getKey() {
                return this.key;
        }

        public String getName() {
            return this.name;
        }

        public String getNoDisplayMsg() {
                return this.noDisplayMsg;
        }
        
        public Integer getOrder() {
                return this.order;
        }
        
        public String toString() {
        return "[key=" + key + " | name=" + name + " | noDisplayMsg=" + noDisplayMsg + " | order=" + order + "\n\r";
      }
        
        public int compareTo(Object obj) {
            Tab tab = (Tab) obj;
            int keyComparison = key.toUpperCase().compareTo(tab.getKey().toUpperCase());
            return ((keyComparison == 0) ? name.compareTo(tab.getName()) : keyComparison);
          }

          public boolean equals(Object obj) {
            if (!(obj instanceof Tab)) {
              return false;
            }
            Tab tab = (Tab) obj;
            return key.equals(tab.getKey())
                && name.equals(tab.getName());
          }

          public int hashCode() {
            return 31 * key.hashCode() + name.hashCode();
          }
      }