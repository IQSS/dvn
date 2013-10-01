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
/**
 * RemoteAccessAuth.java
 *
 * @author landreev
 */
package edu.harvard.iq.dvn.core.study;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class RemoteAccessAuth implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.RemoteAccessAuth[id=" + id + "]";
    }
    
    private String hostName;
    private String authType; 
    private String authCred1; 
    private String authCred2; 
    private String authCred3; 
    private String authCred4; 



    public String getType() {
	return authType; 
    }

    public String getAuthCred1() {
        return authCred1;
    }

    public String getAuthCred2() {
        return authCred2;
    }

    public String getAuthCred3() {
        return authCred3;
    }

    public String getAuthCred4() {
        return authCred4;
    }
}
