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
 * VDCServiceLocal.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.vdc;

import java.util.List;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author roberttreacy
 */
@Local
public interface VDCServiceLocal extends java.io.Serializable  {
    public void create(VDC vDC);
    
    public void create(Long userId, String name, String alias, String dType);
    
    public void createScholarDataverse(Long userId, String firstName, String lastName, String name, String affiliation, String alias, String dataverseType);

    public VDC findScholarDataverseByAlias(String alias);
    
    public VDC findScholarDataverseById(Long id);

    public VDC updateScholarDVs(VDC scholarDV);
    
    public void edit(VDC vDC);

    public void destroy(VDC vDC);

    public VDC find(Object pk);
    
    public VDC findById(Long id);

    public List findAll();

    public List<VDC> findAllPublic();

    public VDC findByAlias(String alias);

    public VDC findByName(String name);

    public VDC getVDCFromRequest(HttpServletRequest request);
    
    public void addContributorRequest(Long vdcId, Long userId);

    java.util.List getLinkedCollections(VDC vdc);

    java.util.List getLinkedCollections(VDC vdc, boolean getHiddenCollections);
     
    public void delete (Long vdcId);
    
    public List findAllNonHarvesting();

    public List findVdcsNotInGroups();
    
    public List<VDC> findVdcsNotInGroups(String dtype);
    
    
    public void updateDefaultTemplate(Long vdcId, Long templateId);
    

    public List<VDC> getUserVDCs(Long userId); 
    
    public java.util.List<Long> getOwnedStudyIds(Long vdcId);
    
    public Long getOwnedStudyCount(Long vdcId);

    public Long getReleasedStudyCount(Long vdcId);

    public List getPagedData(Long vdcGroupId, int firstRow, int totalRows, String orderBy, String order);

    public List getPagedDataByActivity(Long vdcGroupId, int firstRow, int totalRows, String order);

    public List getPagedDataByLastUpdateTime(Long vdcGroupId, int firstRow, int totalRows, String order);

    public List getManagedPagedDataByOwnedStudies(int firstRow, int totalRows, String order);

    public List getManagedPagedDataByLastUpdated(int firstRow, int totalRows, String order);

    public List getManagedPagedData(int firstRow, int totalRows, String orderBy, String order);

    public Long getUnrestrictedVdcCount(long vdcGroupId);

    public Long getVdcCount();

   public List<Long> getOrderedVDCIds (String orderBy);
   public List<Long> getOrderedVDCIds(String orderBy, boolean hideRestrictedVDCs);
   public List<Long> getOrderedVDCIds (String letter, String orderBy);
   public List<Long> getOrderedVDCIds (Long classificationId, String orderBy);
   public List<Long> getOrderedVDCIds (Long classificationId, String letter, String orderBy);
   public List<Long> getOrderedVDCIds (Long classificationId, String letter, String orderBy, boolean hideRestrictedVDCs);

   public double getMaxDownloadCount();

    @javax.ejb.Remove
    @javax.ejb.TransactionAttribute(value = javax.ejb.TransactionAttributeType.REQUIRED)
    public void save(edu.harvard.iq.dvn.core.vdc.VDC vDC);

}
