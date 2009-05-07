/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
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