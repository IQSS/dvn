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
package edu.harvard.iq.dvn.core.study;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Local;

/**
 * This is the business interface for EditStudyService enterprise bean.
 */
@Local
public interface EditStudyService extends java.io.Serializable { 
    public void setStudyVersion( Long studyVersionId);
    public void newStudy(Long vdcId, Long userId, Long templateId);
    public void cancel();
    public void save(Long vdcId, Long userId);
    public StudyVersion getStudyVersion();
    public void removeCollectionElement(Collection coll, Object elem);
    public void removeCollectionElement(List list,int index);
    public void removeCollectionElement(Iterator iter, Object elem);
    public Study getStudyByGlobalId(String globalId);
    public void setStudyVersionByGlobalId(String globalId );
    
    java.util.List getCurrentFiles();

    void setCurrentFiles(List currentFiles);

    java.util.List getNewFiles();

    void setNewFiles(List newFiles);

  
    
    public HashMap getStudyMap();
    public void setStudyMap(HashMap studyMap);

    void setIngestEmail(String ingestEmail);
    
    public boolean isNewStudy();
    
    public void changeTemplate(Long templateId);


}
