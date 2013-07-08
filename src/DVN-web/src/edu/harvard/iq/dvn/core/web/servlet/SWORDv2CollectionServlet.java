package edu.harvard.iq.dvn.core.web.servlet;

import edu.harvard.iq.dvn.api.datadeposit.CollectionDepositManagerImpl;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.swordapp.server.CollectionAPI;
import org.swordapp.server.CollectionListManager;
import org.swordapp.server.servlets.SwordServlet;

public class SWORDv2CollectionServlet extends SwordServlet {

    @Inject
    CollectionDepositManagerImpl collectionDepositManagerImpl;
    protected CollectionListManager clm = null;
    protected CollectionAPI api;

    public void init() throws ServletException {
        super.init();

        // load the collection list manager implementation
        Object possibleClm = this.loadImplClass("collection-list-impl", true); // allow null
        this.clm = possibleClm == null ? null : (CollectionListManager) possibleClm;

        this.api = new CollectionAPI(this.clm, collectionDepositManagerImpl, this.config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.get(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.api.post(req, resp);
    }
}
