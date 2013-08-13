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
import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
import edu.harvard.iq.dvn.core.study.EditStudyService;
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import edu.harvard.iq.dvn.core.web.admin.OptionsPage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
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

public class ContainerManagerImpl extends VDCBaseBean implements ContainerManager {

    private static final Logger logger = Logger.getLogger(ContainerManagerImpl.class.getCanonicalName());
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    IndexServiceLocal indexService;
    @EJB
    StudyServiceLocal studyService;
    @EJB
    StudyFileServiceLocal studyFileService;
    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
    @EJB
    DDIServiceLocal ddiService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;

    @Override
    public DepositReceipt getEntry(String uri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordServerException, SwordError, SwordAuthException {
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.info("getEntry called with url: " + uri);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.info("operating on target type: " + urlManager.getTargetType());
            if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                Study study = studyService.getStudyByGlobalId(globalId);
                if (study != null) {
                    ReceiptGenerator receiptGenerator = new ReceiptGenerator();
                    String baseUrl = urlManager.getHostnamePlusBaseUrlPath(uri);
                    DepositReceipt depositReceipt = receiptGenerator.createReceipt(baseUrl, study);
                    if (depositReceipt != null) {
                        return depositReceipt;
                    } else {
                        throw new SwordError("Could not generate deposit receipt.");
                    }
                } else {
                    throw new SwordError("Could not find study based on URL: " + uri);
                }
            } else {
                throw new SwordError("Unsupported target type (" + targetType + ") in URL: " + uri);
            }
        } else {
            throw new SwordError("Unable to determine target type from URL: " + uri);
        }
    }

    @Override
    public DepositReceipt replaceMetadata(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.info("replaceMetadata called with url: " + uri);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.info("operating on target type: " + urlManager.getTargetType());
            if ("dataverse".equals(targetType)) {
                throw new SwordError("metadata replace of dataverse supported yet.");
            } else if ("study".equals(targetType)) {
                logger.info("replacing metadata for study");
                logger.info("deposit XML received: " + deposit.getSwordEntry());

                String globalId = urlManager.getTargetIdentifier();

                EditStudyService editStudyService;
                Context ctx;
                try {
                    ctx = new InitialContext();
                    editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up editStudyService");
                }
                StudyServiceLocal studyService;
                try {
                    ctx = new InitialContext();
                    studyService = (StudyServiceLocal) ctx.lookup("java:comp/env/studyService");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up studyService");
                }
                /**
                 * @todo: investigate OptimisticLockException
                 */
                editStudyService.setStudyVersion(studyService.getStudyByGlobalId(globalId).getId());
                Study studyToEdit = editStudyService.getStudyVersion().getStudy();
                VDC dvThatOwnsStudy = studyToEdit.getOwner();
                if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {

                    String tmpDirectory = swordConfiguration.getTempDirectory();
                    String uploadDirPath = tmpDirectory + File.separator + "import" + File.separator + studyToEdit.getId();
                    Long dcmiTermsHarvetsFormatId = new Long(4);
                    HarvestFormatType dcmiTermsHarvestFormatType = em.find(HarvestFormatType.class, dcmiTermsHarvetsFormatId);
//                    File ddiFile = studyService.transformToDDI(tmpFile, dcmiTermsHarvestFormatType.getStylesheetFileName());
                    String xmlAtomEntry = deposit.getSwordEntry().getEntry().toString();
                    File ddiFile = studyService.transformToDDI(xmlAtomEntry, dcmiTermsHarvestFormatType.getStylesheetFileName(), uploadDirPath);
                    // erase all metadata before running ddiService.mapDDI() because
                    // for multivalued fields (such as author) that function appends
                    // values rather than replacing them
                    studyToEdit.getLatestVersion().setMetadata(new Metadata());
                    ddiService.mapDDI(ddiFile, studyToEdit.getLatestVersion(), true);
                    editStudyService.save(dvThatOwnsStudy.getId(), vdcUser.getId());

                    DepositReceipt fakeDepositReceipt = new DepositReceipt();
                    fakeDepositReceipt.setVerboseDescription("Title: " + studyToEdit.getLatestVersion().getMetadata().getTitle());
                    IRI fakeIri = new IRI("fakeIri");
                    fakeDepositReceipt.setEditIRI(fakeIri);
                    return fakeDepositReceipt;
                } else {
                    throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvThatOwnsStudy.getAlias());
                }
            } else {
                throw new SwordError("Unknown target type specified on which to replace metadata: " + uri);
            }
        } else {
            throw new SwordError("No target specified on which to replace metadata: " + uri);
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
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        logger.info("deleteContainer called with url: " + uri);
        urlManager.processUrl(uri);
        logger.info("original url: " + urlManager.getOriginalUrl());
        if (!"edit".equals(urlManager.getServlet())) {
            throw new SwordError("edit servlet expected, not " + urlManager.getServlet());
        }
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.info("operating on target type: " + urlManager.getTargetType());

            StudyServiceLocal studyService;
            Context ctx;
            try {
                ctx = new InitialContext();
                studyService = (StudyServiceLocal) ctx.lookup("java:comp/env/studyService");
            } catch (NamingException ex) {
                throw new SwordServerException("problem looking up studyService");
            }


            if ("dataverse".equals(targetType)) {
                String dvAlias = urlManager.getTargetIdentifier();
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
            } else if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                if (globalId != null) {
                    Study studyToDelete = studyService.getStudyByGlobalId(globalId);
                    if (studyToDelete != null) {
                        VDC dvThatOwnsStudy = studyToDelete.getOwner();
                        if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                            studyService.deleteStudy(studyToDelete.getId());
                        } else {
                            throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify " + dvThatOwnsStudy.getAlias());
                        }
                    }
                } else {
                    throw new SwordError("Could not find study to delete from url: " + uri);
                }
            } else if ("file".equals(targetType)) {
                String fileIdString = urlManager.getTargetIdentifier();
                if (fileIdString != null) {
                    Long fileIdLong;
                    try {
                        fileIdLong = Long.valueOf(fileIdString);
                    } catch (NumberFormatException ex) {
                        throw new SwordError("File id must be a number, not '" + fileIdString + "'. uri was: " + uri);
                    }
                    if (fileIdLong != null) {
                        logger.info("preparing to delete file id " + fileIdLong);
                        StudyFile fileToDelete;
                        try {
                            fileToDelete = studyFileService.getStudyFile(fileIdLong);
                        } catch (EJBException ex) {
                            throw new SwordError("Unable to find file id " + fileIdLong);

                        }
                        if (fileToDelete != null) {
                            String globalId = fileToDelete.getStudy().getGlobalId();
                            VDC dvThatOwnsFile = fileToDelete.getStudy().getOwner();
                            if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsFile)) {
                                EditStudyFilesService editStudyFilesService;
                                try {
                                    editStudyFilesService = (EditStudyFilesService) ctx.lookup("java:comp/env/editStudyFiles");
                                } catch (NamingException ex) {
                                    throw new SwordServerException("problem looking up editStudyFilesService");
                                }
                                editStudyFilesService.setStudyVersionByGlobalId(globalId);
                                // editStudyFilesService.findStudyFileEditBeanById() would be nice
                                List studyFileEditBeans = editStudyFilesService.getCurrentFiles();
                                for (Iterator it = studyFileEditBeans.iterator(); it.hasNext();) {
                                    StudyFileEditBean studyFileEditBean = (StudyFileEditBean) it.next();
                                    if (studyFileEditBean.getStudyFile().getId().equals(fileToDelete.getId())) {
                                        logger.info("marked for deletion: " + studyFileEditBean.getStudyFile().getFileName());
                                        studyFileEditBean.setDeleteFlag(true);
                                    } else {
                                        logger.info("not marked for deletion: " + studyFileEditBean.getStudyFile().getFileName());
                                    }
                                }
                                editStudyFilesService.save(dvThatOwnsFile.getId(), vdcUser.getId());
                            } else {
                                throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify " + dvThatOwnsFile.getAlias());
                            }
                        } else {
                            throw new SwordError("Unable to find file id " + fileIdLong + " from url: " + uri);
                        }
                    } else {
                        throw new SwordError("Unable to find file id in url: " + uri);
                    }
                } else {
                    throw new SwordError("Could not file file to delete in url: " + uri);
                }
            } else {
                throw new SwordError("Unsupported delete target in url:" + uri);
            }
        } else {
            throw new SwordError("No target for deletion specified");
        }
    }

    @Override
    public DepositReceipt useHeaders(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        logger.info("uri was " + uri);
        logger.info("isInProgress:" + deposit.isInProgress());
        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(uri);
        String targetType = urlManager.getTargetType();
        if (!targetType.isEmpty()) {
            logger.info("operating on target type: " + urlManager.getTargetType());
            if ("study".equals(targetType)) {
                String globalId = urlManager.getTargetIdentifier();
                if (globalId != null) {
                    Study studyToRelease = studyService.getStudyByGlobalId(globalId);
                    if (studyToRelease != null) {
                        VDC dvThatOwnsStudy = studyToRelease.getOwner();
                        if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvThatOwnsStudy)) {
                            if (!deposit.isInProgress()) {
                                studyService.setReleased(studyToRelease.getId());
                                DepositReceipt fakeDepositReceipt = new DepositReceipt();
                                IRI fakeIri = new IRI("fakeIri");
                                fakeDepositReceipt.setEditIRI(fakeIri);
                                fakeDepositReceipt.setVerboseDescription("Title: " + studyToRelease.getLatestVersion().getMetadata().getTitle());
                                return fakeDepositReceipt;
                            } else {
                                throw new SwordError("Pass 'In-Progress: false' header to release a study.");
                            }
                        } else {
                            throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvThatOwnsStudy.getAlias());
                        }
                    } else {
                        throw new SwordError("Could not find study using globalId " + globalId);
                    }
                } else {
                    throw new SwordError("Unable to find globalId for study in url:" + uri);
                }
            } else if ("dataverse".equals(targetType)) {
                String dvAlias = urlManager.getTargetIdentifier();
                if (dvAlias != null) {
                    VDC dvToRelease = vdcService.findByAlias(dvAlias);
                    if (swordAuth.hasAccessToModifyDataverse(vdcUser, dvToRelease)) {
                        if (dvToRelease != null) {
                            if (deposit.isInProgress()) {
                                throw new SwordError("Changing a dataverse to 'not released' is not supported");
                            } else {
                                try {
                                    getVDCRequestBean().setVdcNetwork(dvToRelease.getVdcNetwork());
                                } catch (ContextNotActiveException ex) {
                                    /**
                                     * todo: observe same rules about dataverse
                                     * release via web interface such as a study
                                     * or a collection must be release
                                     */
                                    throw new SwordError("Releasing a dataverse is not yet supported");
                                }
                                OptionsPage optionsPage = new OptionsPage();
                                if (optionsPage.isReleasable()) {
                                    if (dvToRelease.isRestricted()) {
                                        logger.info("releasing dataverse via SWORD: " + dvAlias);
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
                                        throw new SwordError("Dataverse has already been released: " + dvAlias);
                                    }
                                } else {
                                    throw new SwordError("dataverse is not releaseable");
                                }
                            }
                        } else {
                            throw new SwordError("Could not find dataverse based on alias in url: " + uri);
                        }
                    } else {
                        throw new SwordError("User " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dvAlias);
                    }
                } else {
                    throw new SwordError("Unable to find dataverse alias in URL: " + uri);
                }
            } else {
                throw new SwordError("unsupported target type (" + targetType + ") in url:" + uri);
            }
        } else {
            throw new SwordError("Target type missing from url: " + uri);
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
            throw new SwordError("Unable to determine requested IRI from URL: " + uri);
        }

    }
}
