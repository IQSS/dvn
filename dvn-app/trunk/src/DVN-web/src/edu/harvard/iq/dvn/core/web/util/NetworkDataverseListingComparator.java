/**
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * NetworkDataverseListingComparator.java
 *
 * Created on October 17, 2007, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.util.Comparator;

/**
 * This class is used by the
 * NetworkDataverseListing to 
 * set the natural sort order
 * for the same.
 *
 * @author wbossons
 */
public class NetworkDataverseListingComparator implements Comparator, java.io.Serializable  {
    
    /** Creates a new instance of NetworkDataverseListingComparator */
    public NetworkDataverseListingComparator() {
    }
    
    public int compare(Object obj1, Object obj2) {
    NetworkDataverseListing ndv1 = (NetworkDataverseListing) obj1;
    NetworkDataverseListing ndv2 = (NetworkDataverseListing) obj2;

    int nameComparison = ndv1.getName().toUpperCase().compareTo(ndv2.getName().toUpperCase());

    return ((nameComparison == 0) ? ndv1.getAlias().compareTo(
        ndv2.getAlias()) : nameComparison);
  }
}
