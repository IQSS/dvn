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
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.ServiceDocument;
import org.swordapp.server.ServiceDocumentManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordCollection;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.SwordWorkspace;
import org.swordapp.server.UriRegistry;

public class ServiceDocumentManagerImpl implements ServiceDocumentManager {

    private static final Logger logger = Logger.getLogger(ServiceDocumentManagerImpl.class.getCanonicalName());
    @EJB
    VDCServiceLocal vdcService;
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;

    @Override
    public ServiceDocument getServiceDocument(String sdUri, AuthCredentials authCredentials, SwordConfiguration config)
            throws SwordError, SwordServerException, SwordAuthException {

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        urlManager.processUrl(sdUri);

        ServiceDocument service = new ServiceDocument();
        SwordWorkspace swordWorkspace = new SwordWorkspace();
        String dvnNetworkName = vdcNetworkService.findRootNetwork().getName();
        swordWorkspace.setTitle(dvnNetworkName);
        service.addWorkspace(swordWorkspace);
        service.setMaxUploadSize(config.getMaxUploadSize());
        String hostnamePlusBaseUrl = urlManager.getHostnamePlusBaseUrlPath(sdUri);
        List<VDC> vdcList = vdcService.getUserVDCs(vdcUser.getId());
        for (VDC dataverse : vdcList) {
            if (swordAuth.hasAccessToModifyDataverse(vdcUser, dataverse)) {
                String dvAlias = dataverse.getAlias();
                if (dvAlias != null && !dvAlias.isEmpty()) {
                    SwordCollection swordCollection = new SwordCollection();
                    swordCollection.setTitle(dataverse.getName());
                    swordCollection.setHref(hostnamePlusBaseUrl + "/collection/dataverse/" + dvAlias);
                    swordCollection.addAcceptPackaging(UriRegistry.PACKAGE_SIMPLE_ZIP);
                    swordCollection.setCollectionPolicy(dvnNetworkName + " deposit terms of use: " + vdcNetworkService.findRootNetwork().getDepositTermsOfUse() + "\n---\n" + dataverse.getName() + " deposit terms of use: " + dataverse.getDepositTermsOfUse());
//                    swordCollection.setCollectionPolicy(dataverse.getDepositTermsOfUse());
                    swordWorkspace.addCollection(swordCollection);
                }
            }
        }
        return service;
    }
}
