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
package edu.harvard.iq.dvn.api.datadeposit;

import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.study.FileMetadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.apache.abdera.model.AtomDate;
import org.swordapp.server.AtomStatement;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ResourcePart;
import org.swordapp.server.Statement;
import org.swordapp.server.StatementManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class StatementManagerImpl implements StatementManager {

    private static final Logger logger = Logger.getLogger(StatementManagerImpl.class.getCanonicalName());
    @EJB
    StudyServiceLocal studyService;
    @EJB
    VDCServiceLocal vdcService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;
    SwordConfigurationImpl swordConfiguration = new SwordConfigurationImpl();

    @Override
    public Statement getStatement(String editUri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordServerException, SwordError, SwordAuthException {
        this.swordConfiguration = (SwordConfigurationImpl) swordConfiguration;
        swordConfiguration = (SwordConfigurationImpl) swordConfiguration;
        if (authCredentials == null) {
            throw new SwordError("auth credentials are null");
        }
        if (swordAuth == null) {
            throw new SwordError("swordAuth is null");
        }

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(editUri);
        String globalId = urlManager.getTargetIdentifier();
        if (urlManager.getTargetType().equals("study") && globalId != null) {

            logger.info("request for sword statement by user " + vdcUser.getUserName());

            Study study = studyService.getStudyByGlobalId(globalId);
            Long studyId;
            try {
                studyId = study.getId();
            } catch (NullPointerException ex) {
                throw new SwordError("couldn't find study with global ID of " + globalId);
            }

            List<VDC> vdcList = vdcService.getUserVDCs(vdcUser.getId());

            if (vdcList.size() != 1) {
                /**
                 * @todo: make this more generic, for non-OJS use cases for
                 * SWORD
                 */
                String msg = "accounts used to look up a Journal Dataverse should find a single dataverse, not " + vdcList.size();
                logger.info(msg);
                throw new SwordError(msg);
            }

            VDC dv = null;
            if (vdcList.get(0) != null) {
                dv = vdcList.get(0);
            }

            boolean authorizedToViewStudy = false;
            Collection<Study> ownedStudies = dv.getOwnedStudies();
            for (Study ownedStudy : ownedStudies) {
                if (study.equals(ownedStudy)) {
                    authorizedToViewStudy = true;
                    System.out.println("setting to true... ownedStudy: " + ownedStudy.getGlobalId());
                    break;
                } else {
                    System.out.println("ownedStudy: " + ownedStudy.getGlobalId() + " ... moving on");
                }
            }
            if (!authorizedToViewStudy) {
                throw new SwordError("user " + vdcUser.getUserName() + " is not authorized to view study with global ID " + globalId);
            }

            String feedUri = urlManager.getHostnamePlusBaseUrlPath(editUri) + "/edit/study/" + study.getGlobalId();
            String author = study.getLatestVersion().getMetadata().getAuthorsStr();
            String title = study.getLatestVersion().getMetadata().getTitle();
            AtomDate atomDate = new AtomDate(study.getLatestVersion().getLastUpdateTime());
            String datedUpdated = atomDate.toString();
            Statement statement = new AtomStatement(feedUri, author, title, datedUpdated);
            Boolean isReleased = study.isReleased();
            Boolean isInDraft = study.getLatestVersion().isDraft();
            Map<String, String> states = new HashMap<String, String>();
            states.put("isReleased", isReleased.toString());
            states.put("isInDraft", isInDraft.toString());
            statement.setStates(states);
            List<FileMetadata> fileMetadatas = study.getLatestVersion().getFileMetadatas();
            for (FileMetadata fileMetadata : fileMetadatas) {
                StudyFile studyFile = fileMetadata.getStudyFile();
                String studyFileUrl = urlManager.getHostnamePlusBaseUrlPath(editUri) + "/edit/file/" + studyFile.getId();
                ResourcePart resourcePart = new ResourcePart(studyFileUrl);
                resourcePart.setMediaType(studyFile.getFileType());
                statement.addResource(resourcePart);
            }
            return statement;
        } else {
            throw new SwordError("Could not determine target type or identifier from URL: " + editUri);
        }
    }
}
