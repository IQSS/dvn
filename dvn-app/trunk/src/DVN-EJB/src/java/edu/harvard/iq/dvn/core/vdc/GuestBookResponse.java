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

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author skraffmiller
 */
@Entity
public class GuestBookResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(nullable=false)
    private GuestBookQuestionnaire guestBookQuestionnaire;
    
    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyFile studyFile;
    
    @ManyToOne
    @JoinColumn(nullable=false)
    private Study study;

    @ManyToOne
    @JoinColumn(nullable=true)
    private VDCUser vdcUser;

    @OneToMany(mappedBy="guestBookResponse",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST},orphanRemoval=true)
    @OrderBy ("id")
    private List<CustomQuestionResponse> customQuestionResponses;


    private String firstname;
    private String lastname;
    private String email;
    private String institution;
    private String position;
    private String downloadtype;
    private String sessionId;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date responseTime;

    public GuestBookResponse(){
        
    }
    
    public GuestBookResponse(GuestBookResponse source){
        //makes a clone of a response for adding of studyfiles in case of multiple downloads
        this.setFirstname(source.getFirstname());
        this.setLastname(source.getLastname());
        this.setEmail(source.getEmail());
        this.setInstitution(source.getInstitution());
        this.setPosition(source.getPosition());
        this.setResponseTime(source.getResponseTime());
        this.setStudy(source.getStudy());
        this.setVdcUser(source.getVdcUser());
        this.setSessionId(source.getSessionId());
        List <CustomQuestionResponse> customQuestionResponses = new ArrayList();
        if (!source.getCustomQuestionResponses().isEmpty()){
            for (CustomQuestionResponse customQuestionResponse : source.getCustomQuestionResponses() ){
                CustomQuestionResponse customQuestionResponseAdd = new CustomQuestionResponse();
                customQuestionResponseAdd.setResponse(customQuestionResponse.getResponse());  
                customQuestionResponseAdd.setCustomQuestion(customQuestionResponse.getCustomQuestion());
                customQuestionResponseAdd.setGuestBookResponse(this);
                customQuestionResponses.add(customQuestionResponseAdd);
            }           
        }
        this.setCustomQuestionResponses(customQuestionResponses);
        this.setGuestBookQuestionnaire(source.getGuestBookQuestionnaire());
    }
    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GuestBookQuestionnaire getGuestBookQuestionnaire() {
        return guestBookQuestionnaire;
    }

    public void setGuestBookQuestionnaire(GuestBookQuestionnaire guestBookQuestionnaire) {
        this.guestBookQuestionnaire = guestBookQuestionnaire;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public List<CustomQuestionResponse> getCustomQuestionResponses() {
        return customQuestionResponses;
    }

    public void setCustomQuestionResponses(List<CustomQuestionResponse> customQuestionResponses) {
        this.customQuestionResponses = customQuestionResponses;
    }
    
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public StudyFile getStudyFile() {
        return studyFile;
    }

    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }
    
    public VDCUser getVdcUser() {
        return vdcUser;
    }

    public void setVdcUser(VDCUser vdcUser) {
        this.vdcUser = vdcUser;
    }
    
    public String getDownloadtype() {
        return downloadtype;
    }

    public void setDownloadtype(String downloadtype) {
        this.downloadtype = downloadtype;
    }
    
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
        if (!(object instanceof GuestBookResponse)) {
            return false;
        }
        GuestBookResponse other = (GuestBookResponse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.vdc.GuestBookResponse[ id=" + id + " ]";
    }
    
}
