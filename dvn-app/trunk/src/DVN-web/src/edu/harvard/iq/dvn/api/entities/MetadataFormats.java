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

import java.io.File; 
import java.io.UnsupportedEncodingException; 
import java.util.List;
import java.util.ArrayList;

import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.MetadataFormatType; 


/**
 *
 * @author leonidandreev
 */
public class MetadataFormats {

    private String globalStudyId; 
    private Long studyId;

    private List<MetadataFormatType> formatTypesAvailable; 
    
    public MetadataFormats() {
    }
    
    public MetadataFormats(String globalId) {
        globalStudyId = globalId;

        formatTypesAvailable = new ArrayList<MetadataFormatType>();
        //lookupMetadataFiles(); 
        
    }

    public MetadataFormats(Long localId) {
        studyId = localId;
        
        formatTypesAvailable = new ArrayList<MetadataFormatType>();
        //lookupMetadataFiles(); 
    }
     
    public Long getStudyId() {
        return studyId;
    }
    
    public void setStudyId(Long localId) {
        studyId = localId; 
    }
    
    public String getGlobalStudyId() {
        return globalStudyId; 
    }
    
    public void setGlobalStudyId(String globalId) {
        globalStudyId = globalId; 
    }
    
    public void addFormatType(MetadataFormatType formatType) {
        formatTypesAvailable.add(formatType);
    }
    
    public List<MetadataFormatType> getFormatTypesAvailable() {
	return formatTypesAvailable; 
    }
    
    public Boolean isFormatAvailable(MetadataFormatType formatType) {
	        
        if (globalStudyId != null) {
            int index1 = globalStudyId.indexOf(':');
            int index2 = globalStudyId.indexOf('/');
            String idAuthority = globalStudyId.substring(index1 + 1, index2);
            String idToken = globalStudyId.substring(index2 + 1).toUpperCase();

            File studyFileDir = FileUtil.getStudyFileDir(idAuthority, idToken);

            String formatName = formatType.getName();
            String cachedFileName = null;
            
            if (studyFileDir != null && studyFileDir.exists() && formatName != null) {
                cachedFileName = studyFileDir.getAbsolutePath() + File.separator + "export_" + formatName +".xml";
            } else {
                return false;
            }
            
            File lookupFile = new File (cachedFileName);
            
            if (lookupFile.exists()) {
                return true; 
            }
        }
        return false; 
    }
    
}