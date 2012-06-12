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
import javax.persistence.*;

/**
 *
 * @author skraffmiller
 */
@Entity
public class CustomQuestionResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private GuestBookResponse guestBookResponse;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CustomQuestion customQuestion;
    
    private String response;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public GuestBookResponse getGuestBookResponse() {
        return guestBookResponse;
    }

    public void setGuestBookResponse(GuestBookResponse guestBookResponse) {
        this.guestBookResponse = guestBookResponse;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    
    
    public CustomQuestion getCustomQuestion() {
        return customQuestion;
    }

    public void setCustomQuestion(CustomQuestion customQuestion) {
        this.customQuestion = customQuestion;
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
        if (!(object instanceof CustomQuestionResponse)) {
            return false;
        }
        CustomQuestionResponse other = (CustomQuestionResponse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.CustomQuestionResponse[ id=" + id + " ]";
    }
}
