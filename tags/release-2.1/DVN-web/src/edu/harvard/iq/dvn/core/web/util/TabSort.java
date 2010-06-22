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