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
 *
 * Created on July 28, 2006, 2:46 PM
 *
 */
package edu.harvard.iq.dvn.core.vdc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.*;

/**
 *  Pre-defined email subject text for contacting an administrator of
 *  VDC/VDCNetword
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class NetworkContactSubject implements java.io.Serializable  {
    private String subjectText;
    

    public NetworkContactSubject() {
    }

    public String getSubjectText() {
        return subjectText;
    }

    public void setSubjectText(String subjectText) {
        this.subjectText = subjectText;
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

    /**
     * Holds value of property vdcNetwork.
     */
    @ManyToOne
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NetworkContactSubject)) {
            return false;
        }
        NetworkContactSubject other = (NetworkContactSubject)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }        
}
