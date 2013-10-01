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
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */
package edu.harvard.iq.dvn.core.web.common;

import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;

/**
 * <p>Application scope data bean for your application.  Create properties
 *  here to represent cached data that should be made available to all users
 *  and pages in the application.</p>
 *
 * <p>An instance of this class will be created for you automatically,
 * the first time your application evaluates a value binding expression
 * or method binding expression that references a managed bean using
 * this class.</p>
 */

@Named("VDCApplication")
@ApplicationScoped
public class VDCApplicationBean extends VDCBaseBean implements java.io.Serializable  {
 

    /** 
     * <p>Construct a new application data bean instance.</p>
     */
    public VDCApplicationBean() {
    }



    /** 
     * <p>This method is called when this bean is removed from
     * application scope.  Typically, this occurs as a result of
     * the application being shut down by its owning container.</p>
     * 
     * <p>You may customize this method to clean up resources allocated
     * during the execution of the <code>init()</code> method, or
     * at any later time during the lifetime of the application.</p>
     */
    public void destroy() {
    }
    
    /**
     * <p>Return an appropriate character encoding based on the
     * <code>Locale</code> defined for the current JavaServer Faces
     * view.  If no more suitable encoding can be found, return
     * "UTF-8" as a general purpose default.</p>
     *
     * <p>The default implementation uses the implementation from
     * our superclass, <code>AbstractApplicationBean</code>.</p>
     */
    public String getLocaleCharacterEncoding() {
        return super.getLocaleCharacterEncoding();
    }

    /**
     * Holds value of property minimumDate.
     */
    private Date minimumDate;

    /**
     * Getter for property minimumDate.
     * @return Value of property minimumDate.
     */
    public Date getMinimumDate() {

        return this.minimumDate;
    }

    /**
     * Setter for property minimumDate.
     * @param minimumDate New value of property minimumDate.
     */
    public void setMinimumDate(Date minimumDate) {

        this.minimumDate = minimumDate;
    }
    
    @EJB StudyServiceLocal studyService;


    private Map<Long, List> allStudyIdsByDownloadCountMap = new HashMap();
    private Map<Long, List> allStudyIdsByReleaseDateMap = new HashMap(); 

    public void setAllStudyIdsByDownloadCountMap(Map<Long, List> allStudyIdsByDownloadCountMap) {
        this.allStudyIdsByDownloadCountMap = allStudyIdsByDownloadCountMap;
    }

    public void setAllStudyIdsByReleaseDateMap(Map<Long, List> allStudyIdsByReleaseDateMap) {
        this.allStudyIdsByReleaseDateMap = allStudyIdsByReleaseDateMap;
    }
  
        
    public List getAllStudyIdsByDownloadCount(Long vdcNetworkId) {
        if (allStudyIdsByDownloadCountMap.get(vdcNetworkId) == null) {
            allStudyIdsByDownloadCountMap.put(vdcNetworkId, studyService.getMostDownloadedStudyIds(null, vdcNetworkId, -1));
        }
        return allStudyIdsByDownloadCountMap.get(vdcNetworkId);
    }

    

    public List getAllStudyIdsByReleaseDate(Long vdcNetworkId) {
        if (allStudyIdsByReleaseDateMap.get(vdcNetworkId) == null) {
            allStudyIdsByReleaseDateMap.put(vdcNetworkId, studyService.getRecentlyReleasedStudyIds(null, vdcNetworkId, -1));
        }
        return allStudyIdsByReleaseDateMap.get(vdcNetworkId);
    }    
}
