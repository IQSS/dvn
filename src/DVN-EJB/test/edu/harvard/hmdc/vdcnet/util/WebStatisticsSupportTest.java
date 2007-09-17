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
