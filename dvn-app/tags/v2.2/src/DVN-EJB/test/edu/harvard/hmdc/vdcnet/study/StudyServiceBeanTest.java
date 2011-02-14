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

package edu.harvard.hmdc.vdcnet.study;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.*;
import org.w3c.dom.Document;
/*
 * StudyServiceBeanTest.java
 * JUnit based test
 *
 * Created on August 23, 2006, 12:12 PM
 */

/**
 *
 * @author Ellen Kraffmiller
 */
public class StudyServiceBeanTest extends TestCase {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public StudyServiceBeanTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory("VDCNet-ejbPU");
        em = emf.createEntityManager();
    }

    protected void tearDown() throws Exception {
        if (em!=null) {
            em.close();
        }
        if (emf!=null) {
            emf.close();
        }
    }

    /**
     * Test of importStudy method, of class edu.harvard.hmdc.vdcnet.study.StudyServiceBean.
     */
    public void testImportStudy() {
        System.out.println("importStudy");
        
        Document doc = null;
        edu.harvard.hmdc.vdcnet.study.StudyServiceBean instance = new edu.harvard.hmdc.vdcnet.study.StudyServiceBean();
        
        edu.harvard.hmdc.vdcnet.study.Study expResult = null;
    //    edu.harvard.hmdc.vdcnet.study.Study result = instance.importStudy(doc);
    //    assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exportStudy method, of class edu.harvard.hmdc.vdcnet.study.StudyServiceBean.
     */
    public void testExportStudy() {
        System.out.println("exportStudy");
        
        String schemaType = null;
        edu.harvard.hmdc.vdcnet.study.Study study = null;
        edu.harvard.hmdc.vdcnet.study.StudyServiceBean instance = new edu.harvard.hmdc.vdcnet.study.StudyServiceBean();
        
        Document expResult = null;
     //   Document result = instance.exportStudy(schemaType, study);
    //    assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addStudy method, of class edu.harvard.hmdc.vdcnet.study.StudyServiceBean.
     */
    public void testAddStudy() {
        System.out.println("addStudy");
        
        edu.harvard.hmdc.vdcnet.study.Study study = null;
        study.setTitle("Test Title");
        StudyAuthor sa = new StudyAuthor();
     //   sa.setValue("Jane Researcher");
        study.getStudyAuthors().add(sa);
        study.setCreateTime(new Date());
        edu.harvard.hmdc.vdcnet.study.StudyServiceBean instance = new edu.harvard.hmdc.vdcnet.study.StudyServiceBean();
        
        instance.addStudy(study);
        
      
    }

    /**
     * Test of updateStudy method, of class edu.harvard.hmdc.vdcnet.study.StudyServiceBean.
     */
    public void testUpdateStudy() {
        System.out.println("updateStudy");
        
        edu.harvard.hmdc.vdcnet.study.Study study = null;
        edu.harvard.hmdc.vdcnet.study.StudyServiceBean instance = new edu.harvard.hmdc.vdcnet.study.StudyServiceBean();
        
        instance.updateStudy(study);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteStudy method, of class edu.harvard.hmdc.vdcnet.study.StudyServiceBean.
     */
    public void testDeleteStudy() {
        System.out.println("deleteStudy");
        
        edu.harvard.hmdc.vdcnet.study.Study study = null;
        edu.harvard.hmdc.vdcnet.study.StudyServiceBean instance = new edu.harvard.hmdc.vdcnet.study.StudyServiceBean();
        
     //   instance.deleteStudy(study);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
