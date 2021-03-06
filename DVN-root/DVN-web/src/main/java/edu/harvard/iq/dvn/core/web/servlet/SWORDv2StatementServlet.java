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
package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.api.datadeposit.StatementManagerImpl;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.swordapp.server.StatementAPI;
import org.swordapp.server.StatementManager;
import org.swordapp.server.servlets.SwordServlet;

public class SWORDv2StatementServlet extends SwordServlet {

    @Inject
    StatementManagerImpl statementManagerImpl;
    private StatementManager sm;
    private StatementAPI statementApi;

    public void init() throws ServletException {
        super.init();

        // load the container manager implementation
        this.sm = statementManagerImpl;

        // initialise the underlying servlet processor
        this.statementApi = new StatementAPI(this.sm, this.config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.statementApi.get(req, resp);
    }
}
