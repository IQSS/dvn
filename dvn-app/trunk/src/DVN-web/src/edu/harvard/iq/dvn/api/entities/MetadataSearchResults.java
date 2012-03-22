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
package edu.harvard.iq.dvn.api.entities;

import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author leonidandreev
 */
public class MetadataSearchResults {

    private List<String> studyIds = null;
    private String queryString; 
    
    public MetadataSearchResults(List<String> ids) {
        studyIds = ids;        
    }

    public MetadataSearchResults() {        
        studyIds = new ArrayList<String>();
    }
    
    public List<String> getStudyIds() {
	return studyIds; 
    }
    
    public void setStudyIds(List<String> ids) {
        studyIds = ids; 
    }
    
    public void addStudyId(String id) {
        if (studyIds == null) {
            studyIds = new ArrayList<String>(); 
        }
        studyIds.add(id);
    }
    
    public String getQueryString () {
        return queryString; 
    }
    
    public void setQueryString (String q) {
        queryString = q; 
    }
   
}