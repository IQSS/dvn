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
 * VDCNetwork.java
 *
 * Created on July 28, 2006, 3:08 PM
 *
 */
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.Template;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class VDCNetworkStats implements java.io.Serializable  {
  
   
    /** Creates a new instance of VDCNetwork */
    public VDCNetworkStats() {
    }
   
   
 

    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }

    private Long studyCount;

    public Long getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(Long studyCount) {
        this.studyCount = studyCount;
    }


    private Long fileCount;

    public Long getFileCount() {
        return fileCount;
    }

    public void setFileCount(Long fileCount) {
        this.fileCount = fileCount;
    }
    

 
}
