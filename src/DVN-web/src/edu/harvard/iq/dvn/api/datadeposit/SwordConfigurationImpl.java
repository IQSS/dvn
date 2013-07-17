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

import org.swordapp.server.SwordConfiguration;

public class SwordConfigurationImpl implements SwordConfiguration {

    @Override
    public boolean returnDepositReceipt() {
        return true;
    }

    @Override
    public boolean returnStackTraceInError() {
        return true;
    }

    @Override
    public boolean returnErrorBody() {
        return true;
    }

    @Override
    public String generator() {
        return "http://www.swordapp.org/";
    }

    @Override
    public String generatorVersion() {
        return "2.0";
    }

    @Override
    public String administratorEmail() {
        return null;
    }

    @Override
    public String getAuthType() {
        // using "Basic" here to match what's in SwordAPIEndpoint
        return "Basic";
    }

    @Override
    public boolean storeAndCheckBinary() {
        return true;
    }

    @Override
    public String getTempDirectory() {
        /**
         * @todo: change this to something more secure
         */
        return "/tmp";
    }

    @Override
    public int getMaxUploadSize() {
        return -1;
    }

    @Override
    public String getAlternateUrl() {
        return null;
    }

    @Override
    public String getAlternateUrlContentType() {
        return null;
    }

    @Override
    public boolean allowUnauthenticatedMediaAccess() {
        return false;
    }
}
