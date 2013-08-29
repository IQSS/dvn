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
import edu.harvard.iq.dvn.core.study.Study;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.Collection;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionListManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;
import org.swordapp.server.UriRegistry;

public class CollectionListManagerImpl implements CollectionListManager {

    private static final Logger logger = Logger.getLogger(CollectionListManagerImpl.class.getCanonicalName());
    @EJB
    VDCServiceLocal vdcService;
    @Inject
    SwordAuth swordAuth;
    @Inject
    UrlManager urlManager;

    @Override
    public Feed listCollectionContents(IRI iri, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordServerException, SwordAuthException, SwordError {
        VDCUser vdcUser = swordAuth.auth(authCredentials);

        urlManager.processUrl(iri.toString());
        String dvAlias = urlManager.getTargetIdentifier();
        if (urlManager.getTargetType().equals("dataverse") && dvAlias != null) {

            VDC dv = vdcService.findByAlias(dvAlias);

            if (dv != null) {
                if (swordAuth.hasAccessToModifyDataverse(vdcUser, dv)) {
                    Abdera abdera = new Abdera();
                    Feed feed = abdera.newFeed();
                    feed.setTitle(dv.getName());
                    Collection<Study> studies = dv.getOwnedStudies();
                    String baseUrl = urlManager.getHostnamePlusBaseUrlPath(iri.toString());
                    for (Study study : studies) {
                        String editUri = baseUrl + "/edit/study/" + study.getGlobalId();
                        String editMediaUri = baseUrl + "/edit-media/study/" + study.getGlobalId();
                        Entry entry = feed.addEntry();
                        entry.setId(editUri);
                        entry.setTitle(study.getLatestVersion().getMetadata().getTitle());
                        entry.setBaseUri(new IRI(editUri));
                        entry.addLink(editMediaUri, "edit-media");
                        feed.addEntry(entry);
                    }
                    Boolean dvHasBeenReleased = dv.isRestricted() ? false : true;
                    feed.addSimpleExtension(new QName(UriRegistry.SWORD_STATE, "dataverseHasBeenReleased"), dvHasBeenReleased.toString());
                    return feed;
                } else {
                    throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "user " + vdcUser.getUserName() + " is not authorized to list studies in dataverse " + dv.getAlias());
                }

            } else {
                throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Could not find dataverse: " + dvAlias);
            }
        } else {
            throw new SwordError(UriRegistry.ERROR_BAD_REQUEST, "Couldn't determine target type or identifer from URL: " + iri);
        }
    }
}
