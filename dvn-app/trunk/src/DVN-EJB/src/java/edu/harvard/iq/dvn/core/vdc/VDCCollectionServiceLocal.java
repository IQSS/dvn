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
 * VDCCollectionServiceLocal.java
 *
 * Created on September 22, 2006, 11:02 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import edu.harvard.iq.dvn.core.study.Study;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCCollectionServiceLocal extends java.io.Serializable  {
    void create(VDCCollection vDCCollection);

    void edit(VDCCollection vDCCollection);

    void destroy(VDCCollection vDCCollection);

    VDCCollection find(Object pk);

    List findAll();
    
    public List findSubCollections(Long id);

    java.util.List findSubCollections(Long id, boolean getAllCollections);


    public VDCCollection findByNameWithinDataverse(String name, VDC dataverse);
    
    public List<Long> getStudyIds(VDCCollection coll);
    public List<Study> getStudies(VDCCollection coll);
    
    public List<VDCCollection> getCollectionList(VDC vdc);
    public List<VDCCollection> getCollectionList(VDC vdc, VDCCollection collectionToExclude);
    public List<VDCCollection> getCollectionList(VDCCollection coll);
    
}
