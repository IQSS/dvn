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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
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

    @Override
    public DepositReceipt createNew(String collectionUri, Deposit deposit, AuthCredentials authCredentials, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {

        String tempDirectory = config.getTempDirectory();
        String uploadDirPath = tempDirectory + File.separator + "uploads";
        File uploadDir = new File(uploadDirPath);
        try {
            uploadDir.mkdir();
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
        } catch (Exception e) {
            logger.info("unable to create directory: " + uploadDirPath);
            throw new SwordAuthException(e);
        }

        DepositReceipt fakeDepositReceipt = new DepositReceipt();
        IRI fakeIri = new IRI("fakeIri");
        fakeDepositReceipt.setLocation(fakeIri);
        fakeDepositReceipt.setEditIRI(fakeIri);
        return fakeDepositReceipt;
    }
}
