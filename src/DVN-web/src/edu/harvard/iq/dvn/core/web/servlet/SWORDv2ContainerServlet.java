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

import edu.harvard.iq.dvn.api.datadeposit.ContainerManagerImpl;
import edu.harvard.iq.dvn.api.datadeposit.StatementManagerImpl;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.apache.log4j.Logger;
import org.swordapp.server.ContainerAPI;
import org.swordapp.server.ContainerManager;
import org.swordapp.server.StatementManager;
import org.swordapp.server.servlets.ContainerServletDefault;
import org.swordapp.server.servlets.SwordServlet;

public class SWORDv2ContainerServlet extends SwordServlet {

    @Inject
    ContainerManagerImpl containerManagerImpl;
    @Inject
    StatementManagerImpl statementManagerImpl;
    private ContainerManager cm;
    private ContainerAPI api;
    private StatementManager sm;

    public void init() throws ServletException {
        super.init();

        // load the container manager implementation
        this.cm = containerManagerImpl;

        // load the statement manager implementation
        this.sm = statementManagerImpl;

        // initialise the underlying servlet processor
        this.api = new ContainerAPI(this.cm, this.sm, this.config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.get(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.head(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.put(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.post(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.delete(req, resp);
    }
}
