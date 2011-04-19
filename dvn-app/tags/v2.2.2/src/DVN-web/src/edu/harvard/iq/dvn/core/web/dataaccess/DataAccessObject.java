/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */


package edu.harvard.iq.dvn.core.web.dataaccess;


import edu.harvard.iq.dvn.core.study.StudyFile;

import java.io.IOException;
import java.io.InputStream;


import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;


/**
 *
 * @author landreev
 */

public abstract class DataAccessObject {

        public DataAccessObject () {

        }

        //public DataAccessObject (String location) {
        //    this.setLocation(location);
        //}

        public DataAccessObject (StudyFile file) {
            this(file, null);
        }

        public DataAccessObject (StudyFile file, DataAccessRequest req) {
            this.file = file;
            this.req = req;
            
            if (this.req == null) {
                this.req = new DataAccessRequest();
            }
        }

        private StudyFile file;
        private DataAccessRequest req;


        private int status;
        private long size;

        private String location;
        private InputStream in;

        private String mimeType;
        private String fileName;
        private String varHeader;
        private String errorMessage;

        private Boolean isLocalFile = false;
        private Boolean isRemoteAccess = false;
        private Boolean isHttpAccess = false;
        private Boolean isNIOSupported = false;
        private Boolean isDownloadSupported = false;
        private Boolean isSubsetSupported = false;


        private Boolean isZippedStream = false;
        private Boolean noVarHeader = false;


        // For remote, URL-based downloads:

        private String remoteUrl;
        private GetMethod method = null;

        private Header[] responseHeaders;

        public abstract void open () throws IOException;
        //public abstract void open (String location) throws IOException;
        //public abstract void open (String location, Object args[]) throws IOException;

        public abstract boolean canAccess (String location) throws IOException;

        // getters:

        public StudyFile getFile () {
            return file;
        }

        public DataAccessRequest getRequest() {
            return req;
        }

        public int getStatus () {
            return status;
        }

        private long getSize () {
            return size;
        }

        public String getLocation () {
            return location;
        }

        public InputStream getInputStream () {
            return in;
        }

        public String getMimeType () {
            return mimeType;
        }

        public String getFileName () {
            return fileName;
        }

        public String getVarHeader () {
            return varHeader;
        }

        public String getErrorMessage () {
            return errorMessage;
        }

        public String getRemoteUrl () {
            return remoteUrl;
        }

        public GetMethod getHTTPMethod () {
            return method;
        }

        public Header[] getResponseHeaders () {
            return responseHeaders;
        }

        public Boolean isLocalFile () {
            return isLocalFile;
        }

        public Boolean isRemoteAccess () {
            return isRemoteAccess;
        }

        public Boolean isHttpAccess () {
            return isHttpAccess;
        }

        public Boolean isDownloadSupported () {
            return isDownloadSupported;
        }

        public Boolean isSubsetSupported () {
            return isSubsetSupported;
        }

        public Boolean isNIOSupported () {
            return isNIOSupported;
        }


        public Boolean isZippedStream () {
            return isZippedStream;
        }

        public Boolean noVarHeader () {
            return noVarHeader;
        }

        // setters:

        public void setFile (StudyFile f) {
            file = f;
        }

        public void setRequest (DataAccessRequest dar) {
            req = dar; 
        }

        public void setStatus (int s) {
            status = s;
        }

        public void setSize (long s) {
            size = s;
        }

        public void setLocation (String l) {
            location = l; 
        }

        public void setInputStream (InputStream is) {
            in = is;
        }

        public void setMimeType (String mt) {
            mimeType = mt;
        }

        public void setFileName (String fn) {
            fileName = fn;
        }

        public void setVarHeader (String vh) {
            varHeader = vh;
        }

        public void setErrorMessage (String em) {
            errorMessage = em;
        }

        public void setRemoteUrl (String u) {
            remoteUrl = u;
        }

        public void setHTTPMethod (GetMethod hm) {
            method = hm;
        }

        public void setResponseHeaders (Header[] headers) {
            responseHeaders = headers;
        }

        public void setIsLocalFile (Boolean f) {
            isLocalFile = f;
        }

        public void setIsRemoteAccess (Boolean r) {
            isRemoteAccess = r;
        }

        public void setIsHttpAccess (Boolean h) {
            isHttpAccess = h; 
        }

        public void setIsDownloadSupported (Boolean d) {
            isDownloadSupported = d;
        }

        public void setIsSubsetSupported (Boolean s) {
            isSubsetSupported = s; 
        }

        public void setIsNIOSupported (Boolean n) {
            isNIOSupported = n; 
        }

        public void setIsZippedStream (Boolean zs) {
            isZippedStream = zs;
        }

        public void setNoVarHeader (Boolean nvh) {
            noVarHeader = nvh;
        }

        // connection management methods:

        public void releaseConnection () {
            if (method != null) {
                method.releaseConnection();
            }
        }

        public void closeInputStream () {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // we really don't care.
                    String eMsg = "Warning: IO exception closing input stream.";
                    if (errorMessage == null) {
                        errorMessage = eMsg;
                    } else {
                        errorMessage = eMsg + "; " + errorMessage;
                    }
                }
            }
        }
    }

