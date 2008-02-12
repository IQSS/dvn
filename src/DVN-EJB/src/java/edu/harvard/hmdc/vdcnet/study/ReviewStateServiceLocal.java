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
 * ReviewStateServiceLocal.java
 *
 * Created on November 17, 2006, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.vdc.ReviewState;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface ReviewStateServiceLocal extends java.io.Serializable {
    public static final String REVIEW_STATE_NEW = "New";
    public static final String REVIEW_STATE_IN_REVIEW = "In Review";
    public static final String REVIEW_STATE_RELEASED = "Released";
    
   public ReviewState findByName(String name);
    
}
