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
   Version 3.1.
*/
package edu.harvard.iq.dvn.core.vdc;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author skraffmiller
 */
@Entity
public class GuestBookQuestionnaire implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
     /**
     * Holds value of the vdc
     */
    @OneToOne
    @JoinColumn(nullable=false)
    private VDC vdc;
    
    @OneToMany(mappedBy="guestBookQuestionnaire",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST},orphanRemoval=true)
    private List<CustomQuestion> customQuestions;
    
    private boolean firstNameRequired;
    private boolean lastNameRequired;
    private boolean emailRequired;
    private boolean institutionRequired;
    private boolean positionRequired;
    private boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    
    public boolean isEmailRequired() {
        return emailRequired;
    }

    public void setEmailRequired(boolean emailRequired) {
        this.emailRequired = emailRequired;
    }

    public boolean isFirstNameRequired() {
        return firstNameRequired;
    }

    public void setFirstNameRequired(boolean firstNameRequired) {
        this.firstNameRequired = firstNameRequired;
    }

    public boolean isInstitutionRequired() {
        return institutionRequired;
    }

    public void setInstitutionRequired(boolean institutionRequired) {
        this.institutionRequired = institutionRequired;
    }

    public boolean isLastNameRequired() {
        return lastNameRequired;
    }

    public void setLastNameRequired(boolean lastNameRequired) {
        this.lastNameRequired = lastNameRequired;
    }

    public boolean isPositionRequired() {
        return positionRequired;
    }

    public void setPositionRequired(boolean positionRequired) {
        this.positionRequired = positionRequired;
    }

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }
    
    public List<CustomQuestion> getCustomQuestions() {
        return customQuestions;
    }

    public void setCustomQuestions(List<CustomQuestion> customQuestions) {
        this.customQuestions = customQuestions;
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
        if (!(object instanceof GuestBookQuestionnaire)) {
            return false;
        }
        GuestBookQuestionnaire other = (GuestBookQuestionnaire) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.GuestBookQuestionnaire[ id=" + id + " ]";
    }
    
}
