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
