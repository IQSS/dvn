package edu.harvard.iq.dvn.core.web.dataaccess;

// java core imports:
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.ejb.EJB;


// Apache toolkit imports:
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;



// DVN App imports:
import edu.harvard.iq.dvn.core.study.StudyFile;
import edu.harvard.iq.dvn.core.study.StudyServiceLocal;
import edu.harvard.iq.dvn.core.study.RemoteAccessAuth;
import edu.harvard.iq.dvn.core.web.dvnremote.DvnTermsOfUseAccess;
import edu.harvard.iq.dvn.core.web.dvnremote.ICPSRauth;


public class CensusAccessObject extends DataAccessObject {

    public CensusAccessObject () throws IOException {
        this(null);
    }

    public CensusAccessObject(StudyFile file) throws IOException {
        this (file, null);
    }

    public CensusAccessObject(StudyFile file, DataAccessRequest req) throws IOException {

        super(file, req);

        if (!file.isRemote()) {
            throw new IOException ("Not a remote file!");
        }

        // Should also check if the file is from census.gov

        this.setIsRemoteAccess(true);
        this.setIsHttpAccess(true);
        this.setIsSubsetSupported(true);
    }

    @EJB StudyServiceLocal studyService;


    public boolean canAccess (String location) throws IOException{
        return true;
    }

    //public void open (String location) throws IOException{

    //}

    //private void open (StudyFile file, Object req) throws IOException {
    public void open () throws IOException {

        StudyFile file = this.getFile();
        DataAccessRequest req = this.getRequest(); 

        if (req.getParameter("noVarHeader") != null) {
            this.setNoVarHeader(true);
        }

        if (req.getParameter("vars") == null || req.getParameter("vars").equals("")) {
            throw new IOException ("CensusAccess open called without the vars parameter. "+
                    "Only subsetting calls are supported.");
        }


        String remoteFileUrl = file.getFileSystemLocation();
        remoteFileUrl = remoteFileUrl+"?vars="+req.getParameter("vars");

        if (remoteFileUrl != null) {
            remoteFileUrl = remoteFileUrl.replaceAll(" ", "+");
            this.setRemoteUrl(remoteFileUrl);
        }


        Boolean zippedStream = false;


        GetMethod method = null;
        int status = 200;

        try {
            // No authentication is required for census.gov access
            method = new GetMethod(remoteFileUrl);

            // normally, the HTTP client follows redirects
            // automatically, so we need to explicitely tell it
            // not to:

            // method.setFollowRedirects(false); -- not necessary for census

            status = (new HttpClient()).executeMethod(method);

        } catch (IOException ex) {
            //if (method != null) {
            //    method.releaseConnection();
            //}
            status = 404;
        }

        this.setStatus(status);

        if (status != 200) {
            if (method != null) {
                method.releaseConnection();
            }

            throw new IOException ("CensusAccess: HTTP access failed; status: "+status);
        }

        InputStream in = null;
        String[] valueTokens;

        try {
            in = method.getResponseBodyAsStream();
            CensusInputStream cin = new CensusInputStream(in);
            String[] requestedVariables = req.getParameter("vars").split(",", -2);
            cin.setRequestedVariables(requestedVariables);

            this.setInputStream(cin);

        } catch (IOException ex) {
            this.setStatus(404);
            String errorMessage = "I/O error has occured while attempting to retreive a Census data file: "+ex.getMessage()+". Please try again later and if the problem persists, report it to your DVN technical support contact.";
            this.setErrorMessage(errorMessage);

            throw new IOException ("I/O error has occured while attempting to retreive a Census data file: "+ex.getMessage());
        }

        this.setResponseHeaders(method.getResponseHeaders());
        this.setHTTPMethod(method);

    } // End of open;



}