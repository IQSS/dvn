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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author ekraffmiller
 */
@Entity
public class LockssConfig implements Serializable {

    public enum ServerAccess { GROUP, ALL };

    

    private boolean allowRestricted;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private Long id;

    @Enumerated(EnumType.STRING)
    private ServerAccess serverAccess;


    @Column(columnDefinition="TEXT")
    private String licenseText;


    @OneToOne
    @JoinColumn(unique=true)
    private VDC vdc;

    @OneToOne(mappedBy = "lockssConfig", cascade = { CascadeType.MERGE})
    private OAISet oaiSet;

    @OneToOne
    private LicenseType licenseType;
 /**
     * Holds value of property studyAbstracts.
     */
    @OneToMany(mappedBy="lockssConfig", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<LockssServer> lockssServers;

    public VDC getVdc() {
        return vdc;
    }
    public void setVDC(VDC vdc) {
        this.vdc = vdc;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OAISet getOaiSet() {
        return oaiSet;
    }

    public void setOaiSet(OAISet oaiSet) {
        this.oaiSet = oaiSet;
    }

   

    public ServerAccess getserverAccess() {
        return serverAccess;
    }

    public void setserverAccess(ServerAccess serverAccess) {
        this.serverAccess = serverAccess;
    }

    

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseText() {
        return licenseText;
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }

    public boolean isAllowRestricted() {
        return allowRestricted;
    }

    public void setAllowRestricted(boolean allowRestricted) {
        this.allowRestricted = allowRestricted;
    }

    public List<LockssServer> getLockssServers() {
        return lockssServers;
    }

    public void setLockssServers(List<LockssServer> lockssServers) {
        this.lockssServers = lockssServers;
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
        if (!(object instanceof LockssConfig)) {
            return false;
        }
        LockssConfig other = (LockssConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.LockssConfig[id=" + id + "]";
    }

}
