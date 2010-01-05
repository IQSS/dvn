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

package edu.harvard.iq.dvn.core.gnrs;

import javax.ejb.Local;


/**
 * This is the business interface for GNRSService enterprise bean.
 */
@Local
public interface GNRSServiceLocal  extends java.io.Serializable {
    public String getNewObjectId(String protocol, String authority);

    public void delete(String authority, String studyId);

    public String resolveHandleUrl(String handle);

    public void deleteHandle(String handle);

    public void createHandle(String handle);
    
    public void fixHandle(String handle);

    public void registerAll();
    
    public void deleteAll();

    public void fixAll();
}
