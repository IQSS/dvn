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
import java.util.Map;
import javax.inject.Inject;
import org.swordapp.server.AtomStatement;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Statement;
import org.swordapp.server.StatementManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class StatementManagerImpl implements StatementManager {

    @Inject
    SwordAuth swordAuth;

    /**
     * @todo: needs a lot of work
     */
    @Override
    public Statement getStatement(String string, Map<String, String> map, AuthCredentials authCredentials, SwordConfiguration sc) throws SwordServerException, SwordError, SwordAuthException {
        if (authCredentials == null) {
            throw new SwordError("auth credentials are null");
        }
        if (swordAuth == null) {
            throw new SwordError("swordAuth is null");
        }

        VDCUser vdcUser = swordAuth.auth(authCredentials);
        System.out.println("request for sword statement by user " + vdcUser.getUserName());
        System.out.println("String was: " + string);

        String feedUri = "fakeFeedUri";
        String author = "fakeAuthor";
        String title = "fakeTitle";
        // null date become "now"
        String datedUpdated = null;
        Statement statement = new AtomStatement(feedUri, author, title, datedUpdated);
        return statement;
    }
}
