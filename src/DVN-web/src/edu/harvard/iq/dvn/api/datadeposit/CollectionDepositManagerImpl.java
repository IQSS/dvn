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
import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.study.StudyAbstract;
import edu.harvard.iq.dvn.core.study.StudyAuthor;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.StudyFileServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.StudyVersion;
import edu.harvard.iq.dvn.core.util.DateUtil;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.abdera.i18n.iri.IRI;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionDepositManager;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordEntry;
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


            System.out.println("multipart: " + deposit.isMultipart());
            System.out.println("binary only: " + deposit.isBinaryOnly());
            System.out.println("entry only: " + deposit.isEntryOnly());
            System.out.println("in progress: " + deposit.isInProgress());
            System.out.println("metadata relevant: " + deposit.isMetadataRelevant());

            if (deposit.isEntryOnly()) {
                SwordEntry swordEntry = deposit.getSwordEntry();
                Map<String, List<String>> dublinCore = swordEntry.getDublinCore();
                for (Map.Entry<String, List<String>> entry : dublinCore.entrySet()) {
                    logger.info(entry.getKey() + "/" + entry.getValue());
                }

                EditStudyService editStudyService;
                Context ctx;
                try {
                    ctx = new InitialContext();
                    editStudyService = (EditStudyService) ctx.lookup("java:comp/env/editStudy");
                } catch (NamingException ex) {
                    throw new SwordServerException("problem looking up editStudyService");
                }
                editStudyService.newStudy(dv.getId(), vdcUser.getId(), dv.getDefaultTemplate().getId());
                StudyVersion studyVersion = editStudyService.getStudyVersion();
                Study study = studyVersion.getStudy();
                String studyId = studyService.generateStudyIdSequence(study.getProtocol(), study.getAuthority());
                study.setStudyId(studyId);
                Metadata metadata = study.getLatestVersion().getMetadata();
                if (dublinCore.get("title").get(0) != null) {
                    metadata.setTitle(dublinCore.get("title").get(0));
                } else {
                    throw new SwordError("title field is required");
                }
                List<StudyAbstract> studyAbstractList = new ArrayList<StudyAbstract>();
                StudyAbstract studyAbstract = new StudyAbstract();
                studyAbstract.setText(dublinCore.get("description").get(0));
                studyAbstract.setMetadata(metadata);
                studyAbstractList.add(studyAbstract);
                metadata.setStudyAbstracts(studyAbstractList);

                if (dublinCore.get("date") != null) {
                    String dateProvided = dublinCore.get("date").get(0);
                    if (DateUtil.validateDate(dateProvided)) {
                        metadata.setProductionDate(dateProvided);
                    } else {
                        throw new SwordError("Invalid Date Format: (" + dateProvided + "). Valid formats are YYYY-MM-DD, YYYY-MM, or YYYY. Optionally, 'BC' can be appended to the year. (By default, AD is assumed.)");
                    }
                }
                // always set the depositDate
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date now = new Date();
                String depositDate = simpleDateFormat.format(now);
                metadata.setDateOfDeposit(depositDate);

                List<StudyAuthor> studyAuthors = new ArrayList<StudyAuthor>();
                for (String creator : dublinCore.get("creator")) {
                    StudyAuthor studyAuthor = new StudyAuthor();
                    studyAuthor.setName(creator);
                    studyAuthor.setMetadata(metadata);
                    studyAuthors.add(studyAuthor);
                }
                metadata.setStudyAuthors(studyAuthors);
                editStudyService.save(dv.getId(), vdcUser.getId());

                DepositReceipt fakeDepositReceipt = new DepositReceipt();
                IRI fakeIri = new IRI("fakeIriFromMetadataDeposit");
                fakeDepositReceipt.setLocation(fakeIri);
                fakeDepositReceipt.setEditIRI(fakeIri);
                fakeDepositReceipt.setVerboseDescription("Title: " + metadata.getTitle());
                return fakeDepositReceipt;
            } else if (deposit.isBinaryOnly()) {
                logger.info("attempting binary deposit");
                String globalId;
                try {
                    //             0 1   2   3            4       5          6         7     8          9
                    // for example: /dvn/api/data-deposit/swordv2/collection/dataverse/sword/hdl:1902.1/19189
                    String namingAuthority = parts[8];
                    String uniqueLocalName = parts[9];
                    globalId = namingAuthority + "/" + uniqueLocalName;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new SwordServerException("could not extract global ID from collection URI: " + collectionUri);
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
                Long studyId = study.getId();
                editStudyService.setStudyVersion(studyId);
//                Metadata metadata = editStudyService.getStudyVersion().getMetadata();
//                String currentTitle = metadata.getTitle();
//                logger.info("currentTitle: " + currentTitle);
//                metadata.setTitle(currentTitle + "9");
//                String newTitle = metadata.getTitle();
//                logger.info("newTitle: " + newTitle);

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
                try {
                    logger.info("running editStudyService.save()");
                    editStudyService.save(dv.getId(), vdcUser.getId());
                    studyFileService.addFiles(study.getLatestVersion(), fileList, vdcUser);
                } catch (Exception ex) {
                    throw new SwordError("couldn't add file to study");
                }
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
