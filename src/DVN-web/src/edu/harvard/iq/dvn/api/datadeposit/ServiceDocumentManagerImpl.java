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

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ServiceDocument;
import org.swordapp.server.ServiceDocumentManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordCollection;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.SwordWorkspace;

public class ServiceDocumentManagerImpl implements ServiceDocumentManager {

    @Override
    public ServiceDocument getServiceDocument(String sdUri, AuthCredentials authCredentials, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {
        if (authCredentials != null) {
            String username = authCredentials.getUsername();
            String password = authCredentials.getPassword();
            System.out.println("Checking username " + username + " ...");

            try {
                Context ctx = new InitialContext();
                // "vdcUserService" comes from edu.harvard.iq.dvn.core.web.servlet.LoginFilter
                UserServiceLocal userService = (UserServiceLocal) ctx.lookup("java:comp/env/vdcUserService");

                VDCUser vdcUser = userService.findByUserName(username);
                if (vdcUser != null) {
                    if (userService.validatePassword(vdcUser.getId(), password)) {
                        ServiceDocument service = new ServiceDocument();
                        SwordWorkspace swordWorkspace = new SwordWorkspace();
                        VDCServiceLocal vdcService = (VDCServiceLocal) ctx.lookup("java:comp/env/vdcService");
                        List<VDC> vdcList = vdcService.getUserVDCs(vdcUser.getId());

                        if (vdcList.size() != 1) {
                            System.out.println("accounts used to look up up a Journal Dataverse should find a single dataverse");
                            // should throw different exception
                            throw new SwordAuthException();
                        }

                        if (vdcList.get(0) != null) {
                            VDC journalDataverse = vdcList.get(0);
                            String dvAlias = journalDataverse.getAlias();
                            swordWorkspace.setTitle(journalDataverse.getVdcNetwork().getName());
                            SwordCollection swordCollection = new SwordCollection();
                            swordCollection.setTitle(journalDataverse.getName());
                            try {
                                URI u = new URI(sdUri);
                                int port = u.getPort();
                                String hostName = System.getProperty("dvn.inetAddress");
                                // hard coding https on purpose
                                swordCollection.setHref("https://" + hostName + ":" + port + "/dvn/api/data-deposit/swordv2/collection/dataverse/" + dvAlias);
                                swordWorkspace.addCollection(swordCollection);
                                service.addWorkspace(swordWorkspace);
                                service.setMaxUploadSize(config.getMaxUploadSize());
                                return service;
                            } catch (URISyntaxException ex) {
                                System.out.println("problem with URL: " + sdUri);
                                // should throw a different exception
                                throw new SwordAuthException();
                            }
                        } else {
                            System.out.println("could not retrieve journal dataverse");
                            // should throw a different exception
                            throw new SwordAuthException();
                        }
                    } else {
                        System.out.println("wrong password");
                        throw new SwordAuthException();
                    }
                } else {
                    System.out.println("could not find username: " + username);
                    throw new SwordAuthException();
                }
            } catch (NamingException ex) {
                System.out.println("exception looking up userService: " + ex.getMessage());
                // would prefer to throw SwordError or SwordServerException here by they don't seem to be caught anywhere
                throw new SwordAuthException();
            }
        } else {
            // it seems this is never reached... eaten somewhere by way of ServiceDocumentServletDefault -> ServiceDocumentAPI -> SwordAPIEndpoint
            System.out.println("no auth credentials...");
            throw new SwordAuthException();
        }
    }
}
