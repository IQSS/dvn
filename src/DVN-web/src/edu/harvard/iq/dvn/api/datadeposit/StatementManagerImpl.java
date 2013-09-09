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
import edu.harvard.iq.dvn.core.study.StudyLock;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.iri.IRISyntaxException;
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
import org.swordapp.server.UriRegistry;

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
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "auth credentials are null");
        }
        if (swordAuth == null) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "swordAuth is null");
        }

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(editUri);
        String globalId = urlManager.getTargetIdentifier();
        if (urlManager.getTargetType().equals("study") && globalId != null) {

            logger.fine("request for sword statement by user " + vdcUser.getUserName());

            Study study = null;
            try {
                study = studyService.getStudyByGlobalId(globalId);
            } catch (EJBException ex) {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + editUri);
            }
            Long studyId;
            try {
                studyId = study.getId();
            } catch (NullPointerException ex) {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "couldn't find study with global ID of " + globalId);
            }

            List<VDC> vdcList = vdcService.getUserVDCs(vdcUser.getId());

            VDC dvThatOwnsStudy = study.getOwner();
            if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                String feedUri = urlManager.getHostnamePlusBaseUrlPath(editUri) + "/edit/study/" + study.getGlobalId();
                String author = study.getLatestVersion().getMetadata().getAuthorsStr();
                String title = study.getLatestVersion().getMetadata().getTitle();
                AtomDate atomDate = new AtomDate(study.getLatestVersion().getLastUpdateTime());
                String datedUpdated = atomDate.toString();
                Statement statement = new AtomStatement(feedUri, author, title, datedUpdated);
                Map<String, String> states = new HashMap<String, String>();
                states.put("latestVersionState", study.getLatestVersion().getVersionState().toString());
                StudyLock lock = study.getStudyLock();
                if (lock != null) {
                    states.put("locked", "true");
                    states.put("lockedDetail", lock.getDetail());
                    states.put("lockedStartTime", lock.getStartTime().toString());
                } else {
                    states.put("locked", "false");
                }
                statement.setStates(states);
                List<FileMetadata> fileMetadatas = study.getLatestVersion().getFileMetadatas();
                for (FileMetadata fileMetadata : fileMetadatas) {
                    StudyFile studyFile = fileMetadata.getStudyFile();
                    // We are exposing the filename for informational purposes. The file id is what you
                    // actually operate on to delete a file, etc.
                    //
                    // Replace spaces to avoid IRISyntaxException
                    String fileNameFinal = studyFile.getFileName().replace(' ', '_');
                    String studyFileUrlString = urlManager.getHostnamePlusBaseUrlPath(editUri) + "/edit-media/file/" + studyFile.getId() + "/" + fileNameFinal;
                    IRI studyFileUrl;
                    try {
                        studyFileUrl = new IRI(studyFileUrlString);
                    } catch (IRISyntaxException ex) {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Invalid URL for file ( " + studyFileUrlString + " ) resulted in " + ex.getMessage());
                    }
                    ResourcePart resourcePart = new ResourcePart(studyFileUrl.toString());
                    resourcePart.setMediaType(studyFile.getFileType());
                    /**
                     * @todo: Why are properties set on a ResourcePart not
                     * exposed when you GET a Statement?
                     */
//                    Map<String, String> properties = new HashMap<String, String>();
//                    properties.put("filename", studyFile.getFileName());
//                    properties.put("category", studyFile.getLatestCategory());
//                    properties.put("originalFileType", studyFile.getOriginalFileType());
//                    properties.put("id", studyFile.getId().toString());
//                    properties.put("UNF", studyFile.getUnf());
//                    resourcePart.setProperties(properties);
                    statement.addResource(resourcePart);
                }
                return statement;
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "user " + vdcUser.getUserName() + " is not authorized to view study with global ID " + globalId);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not determine target type or identifier from URL: " + editUri);
        }
    }
}
