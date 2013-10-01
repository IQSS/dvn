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

import java.io.File;
import java.util.logging.Logger;
import org.swordapp.server.SwordConfiguration;

public class SwordConfigurationImpl implements SwordConfiguration {

    private static final Logger logger = Logger.getLogger(SwordConfigurationImpl.class.getCanonicalName());

    String getBaseUrlPath() {
        return "/dvn/api/data-deposit/v1/swordv2";
    }

//    boolean allowNetworkAdminDeleteAllStudies() {
//        String jvmOption = "dvn.dataDeposit.allowNetworkAdminDeleteAllStudies";
//        String allowNetworkAdminDeleteAllStudies = System.getProperty(jvmOption);
//        if (allowNetworkAdminDeleteAllStudies != null && allowNetworkAdminDeleteAllStudies.equals("true")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
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
        String tmpFileDir = System.getProperty("vdc.temp.file.dir");
        if (tmpFileDir != null) {
            return tmpFileDir + File.separator + "sword";
        } else {
            return null;
        }
    }

    @Override
    public int getMaxUploadSize() {
        int unlimited = -1;
        String jvmOption = "dvn.dataDeposit.maxUploadInBytes";
        String maxUploadInBytes = System.getProperty(jvmOption);
        if (maxUploadInBytes != null) {
            try {
                int maxUploadSizeInBytes = Integer.parseInt(maxUploadInBytes);
                return maxUploadSizeInBytes;
            } catch (NumberFormatException ex) {
                logger.fine("Could not convert " + maxUploadInBytes + " from JVM option " + jvmOption + " to int. Setting Data Deposit APU max upload size limit to unlimited.");
                return unlimited;
            }
        } else {
            logger.fine("JVM option " + jvmOption + " is undefined. Setting Data Deposit APU max upload size limit to unlimited.");
            return unlimited;

        }
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
