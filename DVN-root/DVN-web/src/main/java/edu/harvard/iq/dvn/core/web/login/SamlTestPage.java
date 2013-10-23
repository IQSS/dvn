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
package edu.harvard.iq.dvn.core.web.login;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAttribute;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@ViewScoped
@Named("SamlTestPage")
public class SamlTestPage extends VDCBaseBean implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(SamlTestPage.class.getCanonicalName());
    private final String assertName = "dk.itst.oiosaml.userassertion";
    // an IdP is a Shibboleth Identity Provider
    private Map<String, String> attributesFromIdp = new HashMap<String, String>();

    public Map<String, String> getAttributesFromIdp() {
        return attributesFromIdp;
    }

    public SamlTestPage() {
        super();
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        UserAssertion userAssert = (UserAssertion) session.getAttribute(assertName);
        Collection<UserAttribute> allAttributesCollection = userAssert.getAllAttributes();
        for (UserAttribute userAttribute : allAttributesCollection) {
            logger.info(userAttribute.getName() + "/" + userAttribute.getValue());
            /*
             urn:oid:1.3.6.1.4.1.5923.1.1.1.10/<?xml version="1.0" encoding="UTF-8"?><saml2:NameID xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" Format="urn:oasis:names:tc:SAML:2.0:nameid-format:persistent" NameQualifier="https://idp.testshib.org/idp/shibboleth" SPNameQualifier="dvn-alpha.hmdc.harvard.edu">8n0ZErIMuWAc6Plj76IMMam3fAw=</saml2:NameID>
             urn:oid:1.3.6.1.4.1.5923.1.1.1.1/Member
             urn:oid:0.9.2342.19200300.100.1.1/myself
             urn:oid:2.5.4.3/Me Myself And I
             urn:oid:1.3.6.1.4.1.5923.1.1.1.6/myself@testshib.org
             urn:oid:2.5.4.20/555-5555
             urn:oid:1.3.6.1.4.1.5923.1.1.1.9/Member@testshib.org
             urn:oid:1.3.6.1.4.1.5923.1.1.1.7/urn:mace:dir:entitlement:common-lib-terms
             urn:oid:2.5.4.42/Me Myself
             urn:oid:2.5.4.4/And I
             */
            attributesFromIdp.put(userAttribute.getName(), userAttribute.getValue());
//            attributeValueList.add(userAttribute.getValue());
        }
        /**
         * for testing locally with no Shibboleth Identity Provider (IdP)
         */
//        for (int i = 0; i < 10; i++) {
//            attributesFromIdp.put("foo", "bar");
//        }
    }
}
