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
 * UserServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.*;
import javax.ejb.Local;

/**
 *
 * 
 */
@Local
public interface RoleRequestServiceLocal extends java.io.Serializable  {
    public RoleRequest find(Long roleRequestId);
    public RoleRequest findByUserVDCRole(Long vdcUserId, Long vdcId, String roleName);
    public RoleRequest findContributorRequest(Long vdcUserId, Long vdcId);
    public void create(Long vdcUserId, Long roleId, Long vdcId);
    public void create(Long vdcUserId, Long roleId); 
}
