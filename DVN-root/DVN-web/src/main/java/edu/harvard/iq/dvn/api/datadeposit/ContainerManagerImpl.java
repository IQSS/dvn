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
import edu.harvard.iq.dvn.core.ddi.DDIServiceLocal;
import edu.harvard.iq.dvn.core.harvest.HarvestFormatType;
import edu.harvard.iq.dvn.core.index.IndexServiceLocal;
import edu.harvard.iq.dvn.core.study.EditStudyService;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyLock;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.study.StudyVersion.VersionState;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.admin.OptionsPage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.abdera.i18n.iri.IRI;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.UriRegistry;

public class ContainerManagerImpl extends VDCBaseBean implements ContainerManager {

    private static final Logger logger = Logger.getLogger(ContainerManagerImpl.class.getCanonicalName());
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    StudyServiceLocal studyService;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @EJB
    DDIServiceLocal ddiService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;
//    SwordConfigurationImpl swordConfiguration = new SwordConfigurationImpl();

    @Override
    public DepositReceipt getEntry(String uri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordServerException, SwordError, SwordAuthException {
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.fine("getEntry called with url: " + uri);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.fine("operating on target type: " + urlManager.getTargetType());
            if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                Study study;
                try {
                    study = studyService.getStudyByGlobalId(globalId);
                } catch (EJBException ex) {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + uri);
                }
                if (study != null) {
                    VDC dvThatOwnsStudy = study.getOwner();
                    if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                        ReceiptGenerator receiptGenerator = new ReceiptGenerator();
                        String baseUrl = urlManager.getHostnamePlusBaseUrlPath(uri);
                        DepositReceipt depositReceipt = receiptGenerator.createReceipt(baseUrl, study);
                        if (depositReceipt != null) {
                            return depositReceipt;
                        } else {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not generate deposit receipt.");
                        }
                    } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "User " + vdcUser.getUserName() + " is not authorized to retrieve entry for " + study.getGlobalId());
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on URL: " + uri);
                }
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unsupported target type (" + targetType + ") in URL: " + uri);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine target type from URL: " + uri);
        }
    }

    @Override
    public DepositReceipt replaceMetadata(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.fine("replaceMetadata called with url: " + uri);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.fine("operating on target type: " + urlManager.getTargetType());
            if ("dataverse".equals(targetType)) {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Metadata replace of dataverse is not supported.");
            } else if ("study".equals(targetType)) {
                logger.fine("replacing metadata for study");
                logger.fine("deposit XML received by replaceMetadata():\n" + deposit.getSwordEntry());

                String globalId = urlManager.getTargetIdentifier();

                EditStudyService editStudyService;
                Context ctx;
                try {
                    ctx = new InitialContext();
                    editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
                } catch (NamingException ex) {
                    logger.info("problem looking up editStudyService");
                    throw new SwordServerException("problem looking up editStudyService");
                }
                StudyServiceLocal studyService;
                try {
                    ctx = new InitialContext();
                    studyService = (StudyServiceLocal) ctx.lookup("java:comp/env/studyService");
                } catch (NamingException ex) {
                    logger.info("problem looking up studyService");
                    throw new SwordServerException("problem looking up studyService");
                }
                Study studyToLookup;
                try {
                    /**
                     * @todo: why doesn't
                     * editStudyService.setStudyVersionByGlobalId(globalId)
                     * work?
                     */
                    studyToLookup = studyService.getStudyByGlobalId(globalId);
                } catch (EJBException ex) {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + uri);
                }
                if (studyToLookup != null) {
                    StudyLock lockOnStudyLookedup = studyToLookup.getStudyLock();
                    if (lockOnStudyLookedup != null) {
                        String message = Util.getStudyLockMessage(lockOnStudyLookedup, studyToLookup.getGlobalId());
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, message);
                    }
                    editStudyService.setStudyVersion(studyToLookup.getId());
                    Study studyToEdit = editStudyService.getStudyVersion().getStudy();
                    VDC dvThatOwnsStudy = studyToEdit.getOwner();
                    if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                        Map<String, List<String>> dublinCore = deposit.getSwordEntry().getDublinCore();
                        if (dublinCore.get("title") == null || dublinCore.get("title").get(0) == null || dublinCore.get("title").get(0).isEmpty()) {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "title field is required");
                        }
                        if (dublinCore.get("date") != null) {
                            String date = dublinCore.get("date").get(0);
                            if (date != null) {
                                boolean isValid = DateUtil.validateDate(date);
                                if (!isValid) {
                                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Invalid date: '" + date + "'.  Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY.");
                                }
                            }
                        }
                        String tmpDirectory = swordConfiguration.getTempDirectory();
                        if (tmpDirectory == null) {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not determine temp directory");
                        }
                        String uploadDirPath = tmpDirectory + File.separator + "import" + File.separator + studyToEdit.getId();
                        Long dcmiTermsHarvetsFormatId = new Long(4);
                        HarvestFormatType dcmiTermsHarvestFormatType = em.find(HarvestFormatType.class, dcmiTermsHarvetsFormatId);
                        String xmlAtomEntry = deposit.getSwordEntry().getEntry().toString();
                        File ddiFile = studyService.transformToDDI(xmlAtomEntry, dcmiTermsHarvestFormatType.getStylesheetFileName(), uploadDirPath);
                        // erase all metadata before running ddiService.mapDDI() because
                        // for multivalued fields (such as author) that function appends
                        // values rather than replacing them
                        studyToEdit.getLatestVersion().setMetadata(new Metadata());
                        ddiService.mapDDI(ddiFile, studyToEdit.getLatestVersion(), true);
                        try {
                            editStudyService.save(dvThatOwnsStudy.getId(), vdcUser.getId());
                        } catch (EJBException ex) {
                            // OptimisticLockException
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to replace cataloging information for study " + studyToEdit.getGlobalId() + " (may be locked). Please try again later.");
                        }
                        ReceiptGenerator receiptGenerator = new ReceiptGenerator();
                        String baseUrl = urlManager.getHostnamePlusBaseUrlPath(uri);
                        DepositReceipt depositReceipt = receiptGenerator.createReceipt(baseUrl, studyToEdit);
                        return depositReceipt;
                    } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvThatOwnsStudy.getAlias());
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + uri);
                }
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unknown target type specified on which to replace metadata: " + uri);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "No target specified on which to replace metadata: " + uri);
        }
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
//        swordConfiguration = (SwordConfigurationImpl) sc;
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.fine("deleteContainer called with url: " + uri);
        urlManager.processUrl(uri);
        logger.fine("original url: " + urlManager.getOriginalUrl());
        if (!"edit".equals(urlManager.getServlet())) {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "edit servlet expected, not " + urlManager.getServlet());
        }
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.fine("operating on target type: " + urlManager.getTargetType());

            StudyServiceLocal studyService;
            Context ctx;
            try {
                ctx = new InitialContext();
                studyService = (StudyServiceLocal) ctx.lookup("java:comp/env/studyService");
            } catch (NamingException ex) {
                logger.info("problem looking up studyService");
                throw new SwordServerException("problem looking up studyService");
            }


            if ("dataverse".equals(targetType)) {
                String dvAlias = urlManager.getTargetIdentifier();
                List<VDC> userVDCs = vdcService.getUserVDCs(vdcUser.getId());
                VDC dataverseToEmpty = vdcService.findByAlias(dvAlias);
                if (dataverseToEmpty != null) {
                    if ("Admin".equals(vdcUser.getNetworkRole().getName())) {
//                        if (swordConfiguration.allowNetworkAdminDeleteAllStudies()) {
//
//                            /**
//                             * @todo: this is the deleteContainer method...
//                             * should move this to some sort of "emptyContainer"
//                             * method
//                             */
//                            // curl --insecure -s -X DELETE https://sword:sword@localhost:8181/dvn/api/data-deposit/v1/swordv2/edit/dataverse/sword 
//                            Collection<Study> studies = dataverseToEmpty.getOwnedStudies();
//                            for (Study study : studies) {
//                                logger.info("In dataverse " + dataverseToEmpty.getAlias() + " about to delete study id " + study.getId());
//                                studyService.deleteStudy(study.getId());
//                            }
//                        } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "DELETE on a dataverse is not supported");
//                        }

                    } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Role was " + vdcUser.getNetworkRole().getName() + " but admin required.");
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Couldn't find dataverse to delete from URL: " + uri);
                }
            } else if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                if (globalId != null) {
                    Study study = null;
                    try {
                        study = studyService.getStudyByGlobalId(globalId);
                    } catch (EJBException ex) {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + uri);
                    }
                    if (study != null) {
                        VDC dvThatOwnsStudy = study.getOwner();
                        if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                            VersionState studyState = study.getLatestVersion().getVersionState();
                            if (studyState.equals(VersionState.DRAFT)) {
                                logger.fine("destroying working copy version of study " + study.getGlobalId());
                                studyService.destroyWorkingCopyVersion(study.getLatestVersion().getId());
                            } else if (studyState.equals(VersionState.RELEASED)) {
                                logger.fine("deaccessioning latest version of study " + study.getGlobalId());
                                studyService.deaccessionStudy(study.getLatestVersion());
                            } else if (studyState.equals(VersionState.DEACCESSIONED)) {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Lastest version of study " + study.getGlobalId() + " has already been deaccessioned.");
                            } else if (studyState.equals(VersionState.ARCHIVED)) {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Lastest version of study " + study.getGlobalId() + " has been archived and can not be deleted or deaccessioned.");
                            } else if (studyState.equals(VersionState.IN_REVIEW)) {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Lastest version of study " + study.getGlobalId() + " is in review and can not be deleted or deaccessioned.");
                            } else {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Operation not valid for study " + study.getGlobalId() + " in state " + studyState);
                            }
                        } else {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "User " + vdcUser.getUserName() + " is not authorized to modify " + dvThatOwnsStudy.getAlias());
                        }
                    } else {
                        throw new SwordError(404);
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study to delete from URL: " + uri);
                }
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unsupported delete target in URL:" + uri);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "No target for deletion specified");
        }
    }

    @Override
    public DepositReceipt useHeaders(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        logger.fine("uri was " + uri);
        logger.fine("isInProgress:" + deposit.isInProgress());
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.fine("operating on target type: " + urlManager.getTargetType());
            if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                if (globalId != null) {
                    Study studyToRelease = null;
                    try {
                        studyToRelease = studyService.getStudyByGlobalId(globalId);
                    } catch (EJBException ex) {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study based on global id (" + globalId + ") in URL: " + uri);
                    }
                    if (studyToRelease != null) {
                        VDC dvThatOwnsStudy = studyToRelease.getOwner();
                        if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                            if (!deposit.isInProgress()) {
                                /**
                                 * We are considering a draft version of a study
                                 * to be incomplete and are saying that sending
                                 * isInProgress=false means the study version is
                                 * complete and can be released.
                                 *
                                 * 9.2. Deposit Incomplete
                                 *
                                 * "If In-Progress is true, the server SHOULD
                                 * expect the client to provide further updates
                                 * to the item some undetermined time in the
                                 * future. Details of how this is implemented is
                                 * dependent on the server's purpose. For
                                 * example, a repository system may hold items
                                 * which are marked In-Progress in a workspace
                                 * until such time as a client request indicates
                                 * that the deposit is complete." --
                                 * http://swordapp.github.io/SWORDv2-Profile/SWORDProfile.html#continueddeposit_incomplete
                                 */
                                if (!studyToRelease.getLatestVersion().getVersionState().equals(StudyVersion.VersionState.RELEASED)) {
                                    studyService.setReleased(studyToRelease.getId());
                                    ReceiptGenerator receiptGenerator = new ReceiptGenerator();
                                    String baseUrl = urlManager.getHostnamePlusBaseUrlPath(uri);
                                    DepositReceipt depositReceipt = receiptGenerator.createReceipt(baseUrl, studyToRelease);
                                    return depositReceipt;
                                } else {
                                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Latest version of study " + globalId + " has already been released.");
                                }
                            } else {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Pass 'In-Progress: false' header to release a study.");
                            }
                        } else {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvThatOwnsStudy.getAlias());
                        }
                    } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find study using globalId " + globalId);
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to find globalId for study in URL:" + uri);
                }
            } else if ("dataverse".equals(targetType)) {
                String dvAlias = urlManager.getTargetIdentifier();
                if (dvAlias != null) {
                    VDC dvToRelease = vdcService.findByAlias(dvAlias);
                    if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvToRelease)) {
                        if (dvToRelease != null) {
                            String optionalPort = "";
                            URI u;
                            try {
                                u = new URI(uri);
                                int port = u.getPort();
                                if (port != -1) {
                                    // https often runs on port 8181 in dev
                                    optionalPort = ":" + port;
                                }
                            } catch (URISyntaxException ex) {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "unable to part URL");
                            }
                            String hostName = System.getProperty("dvn.inetAddress");
                            String dvHomePage = "https://" + hostName + optionalPort + "/dvn/dv/" + dvToRelease.getAlias();
                            if (deposit.isInProgress()) {
                                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Changing a dataverse to 'not released' is not supported. Please change to 'not released' from the web interface: " + dvHomePage);
                            } else {
                                try {
                                    getVDCRequestBean().setVdcNetwork(dvToRelease.getVdcNetwork());
                                } catch (ContextNotActiveException ex) {
                                    /**
                                     * todo: observe same rules about dataverse
                                     * release via web interface such as a study
                                     * or a collection must be release:
                                     * https://redmine.hmdc.harvard.edu/issues/3225
                                     */
                                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Releasing a dataverse is not yet supported. Please release from the web interface: " + dvHomePage);
                                }
                                OptionsPage optionsPage = new OptionsPage();
                                if (optionsPage.isReleasable()) {
                                    if (dvToRelease.isRestricted()) {
                                        logger.fine("releasing dataverse via SWORD: " + dvAlias);
                                        /**
                                         * @todo: tweet and send email about
                                         * release
                                         */
                                        dvToRelease.setReleaseDate(DateUtil.getTimestamp());
                                        dvToRelease.setRestricted(false);
                                        vdcService.edit(dvToRelease);
                                        DepositReceipt fakeDepositReceipt = new DepositReceipt();
                                        IRI fakeIri = new IRI("fakeIriDvWasJustReleased");
                                        fakeDepositReceipt.setEditIRI(fakeIri);
                                        fakeDepositReceipt.setVerboseDescription("Dataverse alias: " + dvAlias);
                                        return fakeDepositReceipt;
                                    } else {
                                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Dataverse has already been released: " + dvAlias);
                                    }
                                } else {
                                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "dataverse is not releaseable");
                                }
                            }
                        } else {
                            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find dataverse based on alias in URL: " + uri);
                        }
                    } else {
                        throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvAlias);
                    }
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to find dataverse alias in URL: " + uri);
                }
            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "unsupported target type (" + targetType + ") in URL:" + uri);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Target type missing from URL: " + uri);
        }
    }

    @Override
    public boolean isStatementRequest(String uri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(uri);
        String servlet = urlManager.getServlet();
        if (servlet != null) {
            if (servlet.equals("statement")) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Unable to determine requested IRI from URL: " + uri);
        }

    }
}
