/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * DeaccessionStudyPage.java
 *
 * Created on March 19, 2010, 1:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;

/**
 *
 * @author gdurand
 */
public class DeaccessionStudyPage extends VDCBaseBean implements java.io.Serializable  {

    @EJB StudyServiceLocal studyService;

    /** Creates a new instance of VariablePage */
    public DeaccessionStudyPage() {
    }

    private Long studyId;
    private StudyVersion studyVersion;

    public void init() {
        super.init();

        // we need to create the studyServiceBean
        if (studyId != null) {
            Study study = studyService.getStudy(studyId);
            studyVersion = study.getReleasedVersion();
        } else {
            // WE SHOULD HAVE A STUDY ID, throw an error
            System.out.println("ERROR: in DeaccessionStudyPage, without a studyId");
        }

    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public StudyVersion getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(StudyVersion studyVersion) {
        this.studyVersion = studyVersion;
    }


    public String deaccession_action() {

        studyService.deaccessionStudy(studyVersion.getStudy().getId());
        getVDCRequestBean().setStudyId(studyVersion.getStudy().getId());
        getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        return "viewStudy";
    }

    public String cancel_action() {
        getVDCRequestBean().setStudyId(studyVersion.getStudy().getId());
        getVDCRequestBean().setStudyVersionNumber(studyVersion.getVersionNumber());
        return "viewStudy";
    }

}
