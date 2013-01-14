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
 * HandlePrefixServiceBean.java
 *
 * Created on June 14, 2007, 11:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class HandlePrefixServiceBean implements edu.harvard.iq.dvn.core.vdc.HandlePrefixServiceLocal {
      @PersistenceContext(unitName="VDCNet-ejbPU")
      private EntityManager em;
 
    
      public List<HandlePrefix> findAll() {
         return  (List <HandlePrefix>) em.createQuery("SELECT hp from HandlePrefix hp").getResultList();
      }
    
}
