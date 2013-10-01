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
package edu.harvard.iq.dvn.core.admin;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;


/**
 * This is the business interface for EditUserService enterprise bean.
 */
@Local
public interface EditUserService extends java.io.Serializable  {
    public Long getRequestStudyId();
    public void setRequestStudyId(Long studyId);
    public Long getRequestStudyFileId();
    public void setRequestStudyFileId(Long fileId); 
    public String getNewPassword1();
    public void setNewPassword1(String password);
    public String getNewPassword2();
    public void setNewPassword2(String password);
    public String getCurrentPassword();
    public void setCurrentPassword(String password);    
    public void setUser(Long id);
    public void newUser();
    public void cancel();
    public void save();
    public void save(Long contributorRequestVdcId, boolean creatorRequest, Long studyRequestId);
    public VDCUser getUser();
    public void deleteUser();

    
}
