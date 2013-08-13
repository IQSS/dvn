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
import edu.harvard.iq.dvn.core.study.EditStudyFilesService;
import edu.harvard.iq.dvn.core.study.EditStudyService;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.abdera.i18n.iri.IRI;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.MediaResource;
import org.swordapp.server.MediaResourceManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.UriRegistry;

@EJB(name = "editStudyFiles", beanInterface = edu.harvard.iq.dvn.core.study.EditStudyFilesService.class)
public class MediaResourceManagerImpl implements MediaResourceManager {

    @EJB
    StudyServiceLocal studyService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;
    private static final Logger logger = Logger.getLogger(MediaResourceManagerImpl.class.getCanonicalName());

    @Override
    public MediaResource getMediaResourceRepresentation(String uri, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(uri);
        String globalId = urlManager.getTargetIdentifier();
        if (urlManager.getTargetType().equals("study") && globalId != null) {
            EditStudyService editStudyService;
            Context ctx;
            try {
                ctx = new InitialContext();
                editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
            } catch (NamingException ex) {
                throw new SwordServerException("problem looking up editStudyService");
            }
            logger.info("looking up study with globalId " + globalId);
            Study study = editStudyService.getStudyByGlobalId(globalId);
            if (study != null) {
                VDC dv = study.getOwner();
                if (isAuthorizedToEditStudy(vdcUser, dv, study)) {
                    InputStream fixmeInputStream = new ByteArrayInputStream("FIXME: replace with zip of all study files".getBytes());
                    String contentType = "application/zip";
                    String packaging = UriRegistry.PACKAGE_SIMPLE_ZIP;
                    boolean isPackaged = true;
                    MediaResource mediaResource = new MediaResource(fixmeInputStream, contentType, packaging, isPackaged);
                    return mediaResource;
                } else {
                    throw new SwordError("user " + vdcUser.getUserName() + " is not authorized to modify study with global ID " + study.getGlobalId());
                }
            } else {
                throw new SwordError("couldn't find study with global ID of " + globalId);
            }
        } else {
            throw new SwordError("Couldn't dermine target type or identifier from URL: " + uri);
        }
    }

    @Override
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        boolean shouldReplace = true;
        return replaceOrAddFiles(uri, deposit, authCredentials, swordConfiguration, shouldReplace);
    }

    @Override
    public void deleteMediaResource(String string, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt addResource(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {
        boolean shouldReplace = false;
        return replaceOrAddFiles(uri, deposit, authCredentials, swordConfiguration, shouldReplace);
    }

    DepositReceipt replaceOrAddFiles(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration, boolean shouldReplace) throws SwordError, SwordAuthException, SwordServerException {
        if (!deposit.getPackaging().equals(UriRegistry.PACKAGE_SIMPLE_ZIP)) {
            throw new SwordError(UriRegistry.ERROR_CONTENT, 415, "Package format " + UriRegistry.PACKAGE_SIMPLE_ZIP + " is required but format specified in 'Packaging' HTTP header was " + deposit.getPackaging());
        }

        VDCUser vdcUser = swordAuth.auth(authCredentials);

        urlManager.processUrl(uri);
        String globalId = urlManager.getTargetIdentifier();
        if (urlManager.getTargetType().equals("study") && globalId != null) {
            EditStudyService editStudyService;
            Context ctx;
            try {
                ctx = new InitialContext();
                editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
            } catch (NamingException ex) {
                throw new SwordServerException("problem looking up editStudyService");
            }
            logger.info("looking up study with globalId " + globalId);
            Study study = editStudyService.getStudyByGlobalId(globalId);
            Long studyId;
            try {
                studyId = study.getId();
            } catch (NullPointerException ex) {
                throw new SwordError("couldn't find study with global ID of " + globalId);
            }
            VDC dv = study.getOwner();
            if (isAuthorizedToEditStudy(vdcUser, dv, study)) {
                editStudyService.setStudyVersion(studyId);
                editStudyService.save(dv.getId(), vdcUser.getId());

                EditStudyFilesService editStudyFilesService;
                try {
                    editStudyFilesService = (EditStudyFilesService) ctx.lookup("java:comp/env/editStudyFiles");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up editStudyFilesService");
                }
                editStudyFilesService.setStudyVersionByGlobalId(globalId);
                List studyFileEditBeans = editStudyFilesService.getCurrentFiles();
                for (Iterator it = studyFileEditBeans.iterator(); it.hasNext();) {
                    StudyFileEditBean studyFileEditBean = (StudyFileEditBean) it.next();
                    if (shouldReplace) {
                        studyFileEditBean.setDeleteFlag(true);
                        logger.info("marked for deletion: " + studyFileEditBean.getStudyFile().getFileName());
                    }
                }
                editStudyFilesService.save(dv.getId(), vdcUser.getId());

                String tempDirectory = swordConfiguration.getTempDirectory();
                String uploadDirPath = tempDirectory + File.separator + "uploads" + File.separator + study.getId().toString();
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    if (!uploadDir.mkdirs()) {
                        throw new SwordServerException("couldn't create directory: " + uploadDir.getAbsolutePath());
                    }
                }
                String filename = uploadDirPath + File.separator + deposit.getFilename();
                /**
                 * @todo: fix known "first character is removed from filename"
                 * issue with sword2-server-1.0-classes.jar (SWORD v2 :: Common
                 * Server Library) described at
                 * https://github.com/swordapp/JavaServer2.0/pull/2
                 *
                 * The author of the jar has not published it (
                 * http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00318.html
                 * ) so we build it ourselves from the "master" branch of the
                 * official repo: https://github.com/swordapp/JavaServer2.0
                 *
                 */
                logger.info("attempting write to " + filename);
                ZipInputStream ziStream = new ZipInputStream(deposit.getInputStream());
                ZipEntry zEntry;
                FileOutputStream tempOutStream = null;
                List<StudyFileEditBean> fbList = new ArrayList<StudyFileEditBean>();

                try {
                    // copied from createStudyFilesFromZip in AddFilesPage
                    while ((zEntry = ziStream.getNextEntry()) != null) {
                        // Note that some zip entries may be directories - we 
                        // simply skip them:
                        if (!zEntry.isDirectory()) {

                            String fileEntryName = zEntry.getName();
                            logger.info("file found: " + fileEntryName);

                            String dirName = null;
                            String finalFileName = null;

                            int ind = fileEntryName.lastIndexOf('/');

                            if (ind > -1) {
                                finalFileName = fileEntryName.substring(ind + 1);
                                if (ind > 0) {
                                    dirName = fileEntryName.substring(0, ind);
                                    dirName = dirName.replace('/', '-');
                                }
                            } else {
                                finalFileName = fileEntryName;
                            }


                            File tempUploadedFile = null;
                            try {
                                tempUploadedFile = FileUtil.createTempFile(tempDirectory, finalFileName);
                            } catch (Exception ex) {
                                Logger.getLogger(MediaResourceManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            tempOutStream = new FileOutputStream(tempUploadedFile);

                            byte[] dataBuffer = new byte[8192];
                            int i = 0;

                            while ((i = ziStream.read(dataBuffer)) > 0) {
                                tempOutStream.write(dataBuffer, 0, i);
                                tempOutStream.flush();
                            }

                            tempOutStream.close();

                            // We now have the unzipped file saved in the upload directory;

                            StudyFileEditBean tempFileBean = new StudyFileEditBean(tempUploadedFile, studyService.generateFileSystemNameSequence(), study);
                            tempFileBean.setSizeFormatted(tempUploadedFile.length());

                            // And, if this file was in a legit (non-null) directory, 
                            // we'll use its name as the file category: 

                            if (dirName != null) {
                                tempFileBean.getFileMetadata().setCategory(dirName);
                            }

                            fbList.add(tempFileBean);

                        } else {
                            logger.info("directory found: " + zEntry.getName());
                        }
                    }
                } catch (IOException ex) {
                    logger.info("Problem getting zip entry");
                }
                StudyFileServiceLocal studyFileService;
                try {
                    studyFileService = (StudyFileServiceLocal) ctx.lookup("java:comp/env/studyFileService");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up studyFileService");
                }
                studyFileService.addFiles(study.getLatestVersion(), fbList, vdcUser);
                if (!uploadDir.delete()) {
                    logger.info("Unable to delete " + uploadDir.getAbsolutePath());
                }
                /**
                 * @todo: when should we release the study?
                 */
//        studyService.setReleased(studyId);
                DepositReceipt fakeDepositReceipt = new DepositReceipt();
                IRI fakeIri = new IRI("fakeIriFromBinaryDeposit");
                fakeDepositReceipt.setLocation(fakeIri);
                fakeDepositReceipt.setEditIRI(fakeIri);
                return fakeDepositReceipt;
            } else {
                throw new SwordError("user " + vdcUser.getUserName() + " is not authorized to modify study with global ID " + study.getGlobalId());
            }
        } else {
            throw new SwordError("Unable to determine target type or identifier from url: " + uri);
        }
    }

    boolean isAuthorizedToEditStudy(VDCUser vdcUser, VDC dv, Study study) throws SwordError {
        /**
         * @todo: we are relying on the fact that the vdcUser has write access
         * to only one "journal" dataverse. Need a better way
         */
        boolean authorizedToEditStudy = false;
        Collection<Study> ownedStudies = dv.getOwnedStudies();
        for (Study ownedStudy : ownedStudies) {
            if (study.equals(ownedStudy)) {
                authorizedToEditStudy = true;
                break;
            }
        }
        if (authorizedToEditStudy) {
            return true;
        } else {
            throw new SwordError("user " + vdcUser.getUserName() + " is not authorized to modify study with global ID " + study.getGlobalId());
        }

    }
}
