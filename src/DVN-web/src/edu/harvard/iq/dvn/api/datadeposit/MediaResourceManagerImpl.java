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
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Deposit;
import org.swordapp.server.DepositReceipt;
import org.swordapp.server.MediaResource;
import org.swordapp.server.MediaResourceManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class MediaResourceManagerImpl implements MediaResourceManager {

    @EJB
    StudyServiceLocal studyService;
    @Inject
    SwordAuth swordAuth;
    private static final Logger logger = Logger.getLogger(MediaResourceManagerImpl.class.getCanonicalName());

    @Override
    public MediaResource getMediaResourceRepresentation(String string, Map<String, String> map, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt replaceMediaResource(String uri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordError, SwordServerException, SwordAuthException {

        VDCUser vdcUser = swordAuth.auth(authCredentials);

        URI uriReference;
        try {
            uriReference = new URI(uri);
        } catch (URISyntaxException ex) {
            throw new SwordServerException("problem with collection URI: " + uri);
        }

        String[] parts = uriReference.getPath().split("/");

        logger.info("attempting binary deposit");
        String globalId;
        String namingAuthority;
        try {
            //             0 1   2   3            4       5          6          7
            // for example: /dvn/api/data-deposit/swordv2/edit-media/hdl:1902.1/19189
            namingAuthority = parts[6];
            String uniqueLocalName = parts[7];
            globalId = namingAuthority + "/" + uniqueLocalName;
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new SwordError("could not extract global ID from collection URI: " + uri);
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
        VDC dv = study.getOwner();
        /**
         * @todo: is there a better way to check if user is authorized to edit a
         * study?
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

        String tempDirectory = swordConfiguration.getTempDirectory();
        String uploadDirPath = tempDirectory + File.separator + "uploads";
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdir()) {
                throw new SwordServerException("couldn't create directory: " + uploadDir.getAbsolutePath());
            }
        }
        String filename = uploadDirPath + File.separator + deposit.getFilename();
        /**
         * @todo: fix known "first character is removed from filename" issue
         * with sword2-server-1.0-classes.jar (SWORD v2 :: Common Server
         * Library) described at
         * https://github.com/swordapp/JavaServer2.0/pull/2
         *
         * The author of the jar has not published it (
         * http://www.mail-archive.com/sword-app-tech@lists.sourceforge.net/msg00318.html
         * ) so we build it ourselves from the "master" branch of the official
         * repo: https://github.com/swordapp/JavaServer2.0
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
    }

    @Override
    public void deleteMediaResource(String string, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DepositReceipt addResource(String string, Deposit dpst, AuthCredentials ac, SwordConfiguration sc) throws SwordError, SwordServerException, SwordAuthException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
