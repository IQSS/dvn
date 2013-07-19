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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Feed;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionListManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class CollectionListManagerImpl implements CollectionListManager {

    @Override
    public Feed listCollectionContents(IRI iri, AuthCredentials authCredentials, SwordConfiguration swordConfiguration) throws SwordServerException, SwordAuthException, SwordError {
        throw new SwordError("HTTP GET of collection URL pattern is not supported yet.");
    }
}
