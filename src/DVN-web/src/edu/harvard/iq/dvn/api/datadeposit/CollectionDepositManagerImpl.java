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
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.apache.abdera.i18n.iri.IRI;
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
             * @todo: fix known "first character is removed from filename" issue
             * with sword2-server-1.0-classes.jar (SWORD v2 :: Common Server
             * Library) described at
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

            DepositReceipt fakeDepositReceipt = new DepositReceipt();
            IRI fakeIri = new IRI("fakeIri");
            fakeDepositReceipt.setLocation(fakeIri);
            fakeDepositReceipt.setEditIRI(fakeIri);
            return fakeDepositReceipt;
        } else {
            throw new SwordServerException("Could not find dataverse: " + dvAlias);
        }
    }
}