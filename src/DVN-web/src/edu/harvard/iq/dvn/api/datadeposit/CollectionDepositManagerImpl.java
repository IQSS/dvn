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
import edu.harvard.iq.dvn.core.study.EditStudyService;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.commons.io.FileUtils;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionDepositManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class CollectionDepositManagerImpl implements CollectionDepositManager {

    private static final Logger logger = Logger.getLogger(CollectionDepositManagerImpl.class.getCanonicalName());
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    StudyServiceLocal studyService;
    @Inject
    SwordAuth swordAuth;

    @Override
    public DepositReceipt createNew(String collectionUri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {

        VDCUser vdcUser = swordAuth.auth(authCredentials);

        URI uriReference;
        try {
            uriReference = new URI(collectionUri);
        } catch (URISyntaxException ex) {
            throw new SwordServerException("problem with collection URI: " + collectionUri);
        }

        logger.info("collection URI path: " + uriReference.getPath());
        String[] parts = uriReference.getPath().split("/");
        String dvAlias;
        try {
            //             0 1   2   3            4       5          6         7
            // for example: /dvn/api/data-deposit/swordv2/collection/dataverse/sword
            dvAlias = parts[7];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new SwordServerException("could not extract dataverse alias from collection URI: " + collectionUri);
        }

        logger.info("attempting deposit into this dataverse alias: " + dvAlias);

        VDC dv = vdcService.findByAlias(dvAlias);

        if (dv != null) {

            boolean authorized = false;
            List<VDC> userVDCs = vdcService.getUserVDCs(vdcUser.getId());
            for (VDC userVdc : userVDCs) {
                if (userVdc.equals(dv)) {
                    authorized = true;
                    break;
                }
            }
            if (!authorized) {
                throw new SwordServerException("user " + vdcUser.getUserName() + " is not authorized to modify dataverse " + dv.getAlias());
            }
            if (userVDCs.size() != 1) {
                throw new SwordServerException("the account used to modify a Journal Dataverse can only have access to 1 dataverse, not " + userVDCs.size());
            }

            logger.info("multipart: " + deposit.isMultipart());
            logger.info("binary only: " + deposit.isBinaryOnly());
            logger.info("entry only: " + deposit.isEntryOnly());
            logger.info("in progress: " + deposit.isInProgress());
            logger.info("metadata relevant: " + deposit.isMetadataRelevant());

            if (deposit.isEntryOnly()) {
                // require title *and* exercise the SWORD jar a bit
                Map<String, List<String>> dublinCore = deposit.getSwordEntry().getDublinCore();
                if (dublinCore.get("title") == null || dublinCore.get("title").get(0) == null) {
                    throw new SwordError("title field is required");
                }

                // instead of writing a tmp file, maybe importStudy() could accept an InputStream?
                String tmpDirectory = config.getTempDirectory();
                String uploadDirPath = tmpDirectory + File.separator + "import" + File.separator + dv.getId();
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    if (!uploadDir.mkdirs()) {
                        throw new SwordServerException("couldn't create directory: " + uploadDir.getAbsolutePath());
                    }
                }
                String tmpFilePath = uploadDirPath + File.separator + "newStudyViaSwordv2.xml";
                File tmpFile = new File(tmpFilePath);
                try {
                    FileUtils.writeStringToFile(tmpFile, deposit.getSwordEntry().getEntry().toString());
                } catch (IOException ex) {
                    throw new SwordServerException("Could write temporary file");
                } finally {
                    uploadDir.delete();
                }

                Long dcmiTermsFormatId = new Long(4);
                Study study;
                try {
                    study = studyService.importStudy(tmpFile, dcmiTermsFormatId, dv.getId(), vdcUser.getId());
                } catch (Exception ex) {
                    throw new SwordError("Couldn't import study: " + ex.getMessage());
                } finally {
                    tmpFile.delete();
                    uploadDir.delete();
                }
                DepositReceipt fakeDepositReceipt = new DepositReceipt();
                IRI fakeIri = new IRI("fakeIriFromMetadataDeposit/" + study.getGlobalId());
                fakeDepositReceipt.setLocation(fakeIri);
                fakeDepositReceipt.setEditIRI(fakeIri);
                fakeDepositReceipt.setVerboseDescription("Title: " + study.getLatestVersion().getMetadata().getTitle());
                return fakeDepositReceipt;
            } else if (deposit.isBinaryOnly()) {
                /**
                 * @todo: should binaries be handled by the
                 * CollectionDepositManager or by the MediaResourceManager?
                 *
                 * The SWORDv2 spec lead says, "It is my plan and hope to back
                 * all of the multipart OUT of the sword spec for a future
                 * version (like a 2.1), so I strongly recommend not using
                 * multipart deposit. Instead do a POST of an Atom Entry and a
                 * PUT of the Media Resource in two distinct HTTP requests." --
                 * http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00327.html
                 *
                 * However, CollectionServletDefault only supports POST while
                 * MediaResourceServletDefault supports both POST and PUT.
                 */
                logger.info("attempting binary deposit");
                String globalId;
                String namingAuthority;
                try {
                    //             0 1   2   3            4       5          6         7     8          9
                    // for example: /dvn/api/data-deposit/swordv2/collection/dataverse/sword/hdl:1902.1/19189
                    namingAuthority = parts[8];
//                    String uniqueLocalName = parts[9];
//                    globalId = namingAuthority + "/" + uniqueLocalName;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new SwordError("could not extract global ID from collection URI: " + collectionUri);
                }
                try {
                    //             0 1   2   3            4       5          6         7     8          9
                    // for example: /dvn/api/data-deposit/swordv2/collection/dataverse/sword/hdl:1902.1/19189
                    String uniqueLocalName = parts[9];
                    if ("new".equals(uniqueLocalName)) {
                        throw new SwordError("Please create a study before attempting to deposit a file.");
                    }
                    globalId = namingAuthority + "/" + uniqueLocalName;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new SwordError("could not extract global ID from collection URI: " + collectionUri);
                }

                String tempDirectory = config.getTempDirectory();
                String uploadDirPath = tempDirectory + File.separator + "uploads";
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    if (!uploadDir.mkdir()) {
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
                try {
                    InputStream inputstream = deposit.getInputStream();
                    OutputStream outputstream = new FileOutputStream(new File(filename));
                    try {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputstream.read(buf)) > 0) {
                            outputstream.write(buf, 0, len);
                        }
                    } finally {
                        inputstream.close();
                        outputstream.close();
                        logger.info("write to " + filename + " complete");
                    }
                } catch (IOException e) {
                    throw new SwordServerException(e);
                }


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
                /**
                 * @todo: is there a better way to check if user is authorized
                 * to edit a study?
                 */
                boolean authorizedToEditStudy = false;
                Collection<Study> ownedStudies = dv.getOwnedStudies();
                for (Study ownedStudy : ownedStudies) {
                    if (study.equals(ownedStudy)) {
                        authorizedToEditStudy = true;
                        break;
                    }
                }
                if (!authorizedToEditStudy) {
                    throw new SwordError("user " + vdcUser.getUserName() + " is not authorized to modify study with global ID " + globalId);
                }
                editStudyService.setStudyVersion(studyId);

                List<StudyFileEditBean> fileList = new ArrayList();
                try {
                    File file = new File(filename);
                    logger.info("attaching file: " + filename);
                    StudyFileEditBean studyFileEditBean = new StudyFileEditBean(file, studyService.generateFileSystemNameSequence(), study, true);
                    fileList.add(studyFileEditBean);
                } catch (IOException ex) {
                    throw new SwordError("couldn't attach file");
                }
                StudyFileServiceLocal studyFileService;
                try {
                    studyFileService = (StudyFileServiceLocal) ctx.lookup("java:comp/env/studyFileService");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up studyFileService");
                }
                editStudyService.save(dv.getId(), vdcUser.getId());
                studyFileService.addFiles(study.getLatestVersion(), fileList, vdcUser);
                DepositReceipt fakeDepositReceipt = new DepositReceipt();
                IRI fakeIri = new IRI("fakeIriFromBinaryDeposit");
                fakeDepositReceipt.setLocation(fakeIri);
                fakeDepositReceipt.setEditIRI(fakeIri);
                return fakeDepositReceipt;
            } else if (deposit.isMultipart()) {
                // get here with this:
                // wget https://raw.github.com/swordapp/Simple-Sword-Server/master/tests/resources/multipart.dat
                // curl --insecure --data-binary "@multipart.dat" -H 'Content-Type: multipart/related; boundary="===============0670350989=="' -H "MIME-Version: 1.0" https://sword:sword@localhost:8181/dvn/api/data-deposit/swordv2/collection/dataverse/sword
                // but...
                // "Yeah, multipart is critically broken across all implementations" -- http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00327.html
                throw new UnsupportedOperationException("Not yet implemented");
            } else {
                throw new SwordError("expected deposit types are isEntryOnly, isBinaryOnly, and isMultiPart");
            }
        } else {
            throw new SwordServerException("Could not find dataverse: " + dvAlias);
        }
    }
}
