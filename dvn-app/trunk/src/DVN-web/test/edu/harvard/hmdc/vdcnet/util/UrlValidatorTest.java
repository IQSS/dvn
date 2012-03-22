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
/*
 * UrlValidatorTest.java
 * JUnit based test
 *
 * Created on September 7, 2007, 4:31 PM
 */
package edu.harvard.hmdc.vdcnet.util;

import edu.harvard.iq.dvn.core.web.util.UrlValidator;
import junit.framework.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 *
 * @author wbossons
 */
public class UrlValidatorTest extends TestCase {
    
    public UrlValidatorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of validate method, of class edu.harvard.hmdc.vdcnet.util.UrlValidator.
     * @author wbossons
     */
    public void testValidate() {
        System.out.println("validate");
        
        FacesContext context = null;
        UIComponent component = null;
        Object value = null;
        UrlValidator instance = new UrlValidator();
        
        instance.validate(context, component, value);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype. Expect fail.");
    }

    /**
     * Test of validateUrl method, of class edu.harvard.hmdc.vdcnet.util.UrlValidator.
     * @author wbossons
     */
    public void testValidateUrl() throws Exception {
        System.out.println("validateUrl Test ...");
        
        String aUrl = "http://dvn.iq.harvard.edu/dvn";
        UrlValidator instance = new UrlValidator();
        
        boolean expResult = true;
        boolean result = instance.validateUrl(aUrl);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype. Expect fail.");
    }

    /**
     * Test of buildInContextUrl method, of class edu.harvard.hmdc.vdcnet.util.UrlValidator.
     * @author wbossons
     */
    public void testBuildInContextUrl() {
        System.out.println("buildInContextUrl");
        String alias = "http://localhost:8080/dv/cgi/awstats/js";
        UrlValidator instance = new UrlValidator();
        String expResult = "http://localhost:8080/dv/cgi/awstats/js";
        String result = instance.buildInContextUrl(null, alias);
        assertEquals(expResult, result);
    }

    /**
     * Test of buildCrossContextUrl method, of class edu.harvard.hmdc.vdcnet.util.UrlValidator.
     *  @author wbossons
     */
    public void testBuildCrossContextUrl() {
        System.out.println("buildCrossContextUrl");
        String contextPath = "http://localhost:8080/DvnAwstats";
        String scriptName = "/cgi/awstats/js";
        UrlValidator instance = new UrlValidator();
        String expResult = "http://localhost:8080/DvnAwstats/cgi/awstats/js";
        String result = instance.buildCrossContextUrl(contextPath, scriptName);
        assertEquals(expResult, result);
    }
    
}
