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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author gdurand
 */
@Entity
public class VDCActivity implements Serializable {
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
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VDCActivity)) {
            return false;
        }
        VDCActivity other = (VDCActivity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.VDCActivity[id=" + id + "]";
    }


    @OneToOne 
    @JoinColumn(unique=true)
    VDC vdc;

    @Column(nullable=false)
    private int localStudyLocalDownloadCount;
    @Column(nullable=false)
    private int localStudyForeignDownloadCount;
    @Column(nullable=false)
    private int foreignStudyLocalDownloadCount;
    @Column(nullable=false)
    private int localStudyNetworkDownloadCount;



    public VDC getVDC() {
        return vdc;
    }

    public void setVDC(VDC vdc) {
        this.vdc = vdc;
    }

    public int getLocalStudyLocalDownloadCount() {
        return localStudyLocalDownloadCount;
    }

    public void setLocalStudyLocalDownloadCount(int localStudyLocalDownloadCount) {
        this.localStudyLocalDownloadCount = localStudyLocalDownloadCount;
    }

    public int getLocalStudyForeignDownloadCount() {
        return localStudyForeignDownloadCount;
    }

    public void setLocalStudyForeignDownloadCount(int localStudyForeignDownloadCount) {
        this.localStudyForeignDownloadCount = localStudyForeignDownloadCount;
    }

    public int getForeignStudyLocalDownloadCount() {
        return foreignStudyLocalDownloadCount;
    }

    public void setForeignStudyLocalDownloadCount(int foreignStudyLocalDownloadCount) {
        this.foreignStudyLocalDownloadCount = foreignStudyLocalDownloadCount;
    }

    public int getLocalStudyNetworkDownloadCount() {
        return localStudyNetworkDownloadCount;
    }

    public void setLocalStudyNetworkDownloadCount(int localStudyNetworkDownloadCount) {
        this.localStudyNetworkDownloadCount = localStudyNetworkDownloadCount;
    }

}
