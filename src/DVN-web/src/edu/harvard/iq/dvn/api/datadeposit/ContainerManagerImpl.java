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
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.EditStudyService;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class ContainerManagerImpl implements ContainerManager {

    private static final Logger logger = Logger.getLogger(ContainerManagerImpl.class.getCanonicalName());
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    EditStudyService editStudyService;
    @EJB
    IndexServiceLocal indexService;
    @Inject
    SwordAuth swordAuth;

    @Override
    public DepositReceipt getEntry(String string, Map<String, String> map, AuthCredentials ac, SwordConfiguration sc) throws SwordServerException, SwordError, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt replaceMetadata(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt replaceMetadataAndMediaResource(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt addMetadataAndResources(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt addMetadata(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt addResources(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteContainer(String uri, AuthCredentials authCredentials, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        logger.info("deleteContainer called with url: " + uri);
        UrlManager urlManager = new UrlManager(uri);
        logger.info("original url: " + urlManager.getOriginalUrl());
        if (!"edit".equals(urlManager.getServlet())) {
            throw new SwordError("edit servlet expected, not " + urlManager.getServlet());
        }
        List<String> target = urlManager.getTarget();
        if (!target.isEmpty()) {
            logger.info("operating on target: " + urlManager.getTarget());
            if ("dataverse".equals(target.get(0))) {
                logger.info("a dataverse has been targeted");
                String dvAlias;
                try {
                    dvAlias = target.get(1);
                } catch (IndexOutOfBoundsException ex) {
                    throw new SwordError("No dataverse alias provided in url: " + uri);
                }

                VDCUser vdcUser = swordAuth.auth(authCredentials);
                List<VDC> userVDCs = vdcService.getUserVDCs(vdcUser.getId());
                VDC dataverseToEmpty = vdcService.findByAlias(dvAlias);
                if (dataverseToEmpty != null) {
                    if (swordAuth.hasAccessToModifyDataverse(vdcUser, dataverseToEmpty)) {

                        /**
                         * @todo: this is the deleteContainer method... should
                         * move this to some sort of "emptyContainer" method
                         */
                        // curl --insecure -s -X DELETE https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/dataverse/sword 
                        Collection<Study> studies = dataverseToEmpty.getOwnedStudies();
                        for (Study study : studies) {
                            System.out.println("In dataverse " + dataverseToEmpty.getAlias() + " about to delete study id " + study.getId());
                            studyService.deleteStudy(study.getId());
                        }
                    } else {
                        throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify " + dataverseToEmpty.getAlias());
                    }
                } else {
                    throw new SwordError("Couldn't find dataverse to delete from url: " + uri);
                }
            } else {
                throw new SwordError("A non-dataverse has been targeted");
            }
        } else {
            throw new SwordError("No target for deletion specified");
        }
    }

    @Override
    public DepositReceipt useHeaders(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isStatementRequest(String string, Map<String, String> map, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        return true;
    }
}
