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
 * TabSort.java
 *
 * Created onFebruary 13, 5:00 PM
 *
 * @author wbossons
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.util;

import edu.harvard.iq.dvn.core.web.util.Tab;
import java.io.Serializable;
import java.util.Comparator;

public class TabSort implements Serializable {
        public final Comparator<Tab> TAB_ORDER =
                                     new Comparator<Tab>() {
            public int compare(Tab e1, Tab e2) {
                return e1.getOrder().compareTo(e2.getOrder());
            }
        };
    } 