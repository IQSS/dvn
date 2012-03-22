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
 * LockssServerAuth.java
 *
 * Created on Mar 23, 2007, 3:13 PM
 *
 */
package edu.harvard.iq.dvn.core.admin;

import edu.harvard.iq.dvn.core.vdc.VDC;
import javax.servlet.http.HttpServletRequest;

import javax.ejb.Local;

/**
 *
 * @author landreev
 */
@Local
public interface LockssAuthServiceLocal extends java.io.Serializable{

    public Boolean isAuthorizedLockssServer ( VDC vdc, HttpServletRequest req);
    public Boolean isAuthorizedRestrictedFiles ( VDC vdc, HttpServletRequest req);
    public Boolean isAuthorizedLockssDownload ( VDC vdc, HttpServletRequest req, Boolean fileIsRestricted);

}
