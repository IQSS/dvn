package edu.harvard.hmdc.vdcnet.util;
import junit.framework.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
/*
 * WebStatisticsSupportTest.java
 * JUnit based test
 *
 * Created on August 23, 2007, 12:11 PM
 */

/**
 *
 * @author wbossons
 */
public class WebStatisticsSupportTest extends TestCase {
    
    public WebStatisticsSupportTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getParameterFromHeader method, of class edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport.
     */
    public void testGetParameterFromHeader() {
        System.out.println("getParameterFromHeader");
        
        String headername = "X-Forwarded-For";
        edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport instance = new edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport();
        
        String expResult = "0";
        int result = instance.getParameterFromHeader(headername);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getQSArgument method, of class edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport.
     */
    public void testGetQSArgument() {
        System.out.println("getQSArgument");
        
        String arg = "isMIT";
        int val = 0;
        edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport instance = new edu.harvard.hmdc.vdcnet.util.WebStatisticsSupport();
        
        String expResult = "&isMIT=0";
        String result = instance.getQSArgument(arg, val);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
