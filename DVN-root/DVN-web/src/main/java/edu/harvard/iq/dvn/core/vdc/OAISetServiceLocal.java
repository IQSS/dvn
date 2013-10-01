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
 * OAISetServiceLocal.java
 *
 * Created on Oct 2, 2007, 5:13:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Ellen Kraffmiller
 */
@Local
public interface OAISetServiceLocal extends java.io.Serializable  {
   public OAISet findBySpec(String spec) throws ORG.oclc.oai.server.verb.NoItemsMatchException;
   public void remove(Long id);
   public List<OAISet> findAll();
   public List<OAISet> findAllOrdered();
   public List<OAISet> findAllOrderedSorted();
   public OAISet findById(Long id);
   public void update(OAISet oaiSet);
   public boolean specExists(String spec);
}
