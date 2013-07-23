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
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.swordapp.server.AtomStatement;
import org.swordapp.server.AuthCredentials;
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

    @Override
    public Statement getStatement(String editUri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration sc) throws SwordServerException, SwordError, SwordAuthException {
        if (authCredentials == null) {
            throw new SwordError("auth credentials are null");
        }
        if (swordAuth == null) {
            throw new SwordError("swordAuth is null");
        }

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        URI uriReference;
        try {
            uriReference = new URI(editUri);
        } catch (URISyntaxException ex) {
            throw new SwordServerException("problem with collection URI: " + editUri);
        }
        logger.info("edit URI path: " + uriReference.getPath());
        String[] parts = uriReference.getPath().split("/");

        String globalId;
        String namingAuthority;
        try {
            //             0 1   2   3            4       5    6          7
            // for example: /dvn/api/data-deposit/swordv2/edit/hdl:1902.1/12345
            namingAuthority = parts[6];
            String uniqueLocalName = parts[7];
            globalId = namingAuthority + "/" + uniqueLocalName;
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new SwordError("could not extract global ID from edit URI: " + editUri);
        }

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
             * @todo: make this more generic, for non-OJS use cases for SWORD
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

        String feedUri = "fakeFeedUri";
        String author = study.getLatestVersion().getMetadata().getAuthorsStr();
        String title = study.getLatestVersion().getMetadata().getTitle();
        /**
         * @todo: null date becomes "now" ... get actual date
         */
        String datedUpdated = null;
        Statement statement = new AtomStatement(feedUri, author, title, datedUpdated);
        return statement;
    }
}
