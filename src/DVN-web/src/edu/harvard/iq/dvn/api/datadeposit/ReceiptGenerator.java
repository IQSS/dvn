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

import edu.harvard.iq.dvn.core.study.Study;
import org.apache.abdera.i18n.iri.IRI;
import org.swordapp.server.DepositReceipt;

public class ReceiptGenerator {

    DepositReceipt createReceipt(String baseUrl, Study study) {
        DepositReceipt depositReceipt = new DepositReceipt();
        depositReceipt.setLocation(new IRI("location" + baseUrl + study.getGlobalId()));
        depositReceipt.setEditIRI(new IRI(baseUrl + "/edit/" + study.getGlobalId()));
        depositReceipt.setEditMediaIRI(new IRI(baseUrl + "/edit-media/" + study.getGlobalId()));
        depositReceipt.setVerboseDescription("Title: " + study.getLatestVersion().getMetadata().getTitle());
        depositReceipt.setStatementURI("application/atom+xml;type=feed", baseUrl + "/statement/" + study.getGlobalId());
        depositReceipt.addDublinCore("bibliographicCitation", study.getLatestVersion().getMetadata().getCitation(false));
        return depositReceipt;
    }
}
