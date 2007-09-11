/*
 * UrlValidatorTest.java
 * JUnit based test
 *
 * Created on September 7, 2007, 4:31 PM
 */

package edu.harvard.hmdc.vdcnet.util;

import junit.framework.*;
import java.net.URL;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

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
