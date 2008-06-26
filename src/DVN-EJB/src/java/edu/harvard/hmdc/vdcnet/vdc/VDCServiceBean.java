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

/*
 * VDCServiceBean.java
 *
 * Created on September 21, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.vdc;

import edu.harvard.hmdc.vdcnet.admin.Role;
import edu.harvard.hmdc.vdcnet.admin.RoleRequest;
import edu.harvard.hmdc.vdcnet.admin.RoleServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.UserServiceLocal;
import edu.harvard.hmdc.vdcnet.admin.VDCRole;
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.harvest.HarvesterServiceLocal;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Template;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;


/**
 *
 * @author roberttreacy
 */
@Stateless
public class VDCServiceBean implements VDCServiceLocal {
    @EJB VDCCollectionServiceLocal vdcCollectionService;
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    @EJB StudyFieldServiceLocal studyFieldService;
    @EJB RoleServiceLocal roleService;
    @EJB UserServiceLocal userService;
    @EJB ReviewStateServiceLocal reviewStateService;
    @EJB StudyServiceLocal studyService;
    @EJB HarvesterServiceLocal harvesterService;
    @Resource(name="jdbc/VDCNetDS") DataSource dvnDatasource;

    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /**
     * Creates a new instance of VDCServiceBean
     */
    public VDCServiceBean() {
    }

    public void create(VDC vDC) {
        em.persist(vDC);
    }
    
    /** scholar dataverse */
    public void createScholarDataverse(Long userId, String firstName, String lastName, String name, String affiliation, String alias, String dataverseType) {
        VDC sDV = new VDC();
        em.persist(sDV);
        sDV.setName(name);
        sDV.setFirstName(firstName);
        sDV.setLastName(lastName);
        sDV.setAffiliation(affiliation);
        sDV.setName(name);
        sDV.setAlias(alias);
        sDV.setDtype(dataverseType);
        VDCCollection addedRootCollection = new VDCCollection();
        addedRootCollection.setName(name);
        addedRootCollection.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED));
        addedRootCollection.setVisible(true);
        vdcCollectionService.create(addedRootCollection);
        sDV.setRootCollection(addedRootCollection);
        sDV.getOwnedCollections().add(addedRootCollection);
        VDCNetwork vdcNetwork = vdcNetworkService.find(new Long(1));
        sDV.setDefaultTemplate(vdcNetwork.getDefaultTemplate());
        sDV.setHeader(vdcNetwork.getDefaultVDCHeader());
        sDV.setFooter(vdcNetwork.getDefaultVDCFooter());
        sDV.setRestricted(true);
        sDV.setDisplayAnnouncements(false);
        ArrayList advancedSearchFields = new ArrayList();
        ArrayList searchResultsFields = new ArrayList();
        List postgresStudyFields = studyFieldService.findAll();
        for (Iterator it = postgresStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isAdvancedSearchField()){
                advancedSearchFields.add(elem);
            }
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        sDV.setAdvSearchFields(advancedSearchFields);
        sDV.setSearchResultFields(searchResultsFields);
        sDV.setCreator(em.find(VDCUser.class,userId));
   
        addedRootCollection.setOwner(sDV);
        vdcCollectionService.edit(addedRootCollection);
        userService.addVdcRole(userId,findByAlias(sDV.getAlias()).getId(), roleService.ADMIN);
        
    }
    
    public VDC findScholarDataverseByAlias(String alias){
       String query="SELECT sd from VDC sd where sd.alias = :fieldName and sd.dtype = 'Scholar'";
       VDC sDV = null;
       try {
           sDV = (VDC) em.createQuery(query).setParameter("fieldName", alias).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return sDV;
    }
    
    public VDC findScholarDataverseById(Long id) {
        VDC o = (VDC) em.find(VDC.class, id);
        return o;
    }
    
    /* updateScholarDVs
     *
     * This is not currently used. It was 
     * developed for 16a, but there is an 
     * issue with casting and the java
     * persistence layer. 
     * Leaving it here as a placeholder.
     *
     *
     * @author wbossons
     */
    public VDC updateScholarDVs(VDC scholarDV) {
        //
        String updateString = "update vdc set firstname = '" 
                + scholarDV.getFirstName() + "', lastname='" 
                + scholarDV.getLastName() + "', name='"
                + scholarDV.getName() + "', alias='"
                + scholarDV.getAlias() + "', affiliation='"
                + scholarDV.getAffiliation() + "', dtype = 'Scholar' where id = " + scholarDV.getId();
        Connection conn=null;
        PreparedStatement updateStatement=null;
        try {
            conn = dvnDatasource.getConnection();
            updateStatement = conn.prepareStatement(updateString);
            int rowcount = updateStatement.executeUpdate();
        } catch (java.sql.SQLException e) {
           // Do nothing, just return null. 
        }
        em.flush();
        VDC scholardataverse = em.find(VDC.class, scholarDV.getId());
        return scholardataverse;
        //
    }
    /** end scholar dataverse methods */
    
    public void edit(VDC vDC) {
        em.merge(vDC);
    }

    public void destroy(VDC vDC) {
        em.merge(vDC);
        em.remove(vDC);
    }

    public VDC find(Object pk) {
        VDC vdc = (VDC) em.find(VDC.class, pk);
        VDCCollection rootCollection = vdc.getRootCollection();
        rootCollection.getId();
        Collection <VDCCollection> subcollections = rootCollection.getSubCollections();
        traverseCollections(subcollections);
        return vdc;
    }
        
    private void traverseCollections(  Collection<VDCCollection> collections) {
        for (Iterator it = collections.iterator(); it.hasNext();) {
            VDCCollection elem = (VDCCollection) it.next();
            elem.getId();
            
            Collection <VDCCollection> subcollections = elem.getSubCollections();
            if (subcollections.size() >0){
                traverseCollections(subcollections);
            }
        }
    }
        
    public VDC findByAlias(String alias) {
     String query="SELECT v from VDC v where v.alias = :fieldName";
       VDC vdc=null;
       try {
            vdc=(VDC)em.createQuery(query).setParameter("fieldName",alias).getSingleResult();
            em.refresh(vdc); // Refresh because the cached object doesn't include harvestingDataverse object - need to review why this is happening
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }

       return vdc;
    }
    
    public VDC findById(Long id) {
        VDC o = (VDC) em.find(VDC.class, id);
        return o;
    }
        
    public VDC findByName(String name) {
     String query="SELECT v from VDC v where v.name = :fieldName";
       VDC vdc=null;
       try {
           vdc=(VDC)em.createQuery(query).setParameter("fieldName",name).getSingleResult();
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return vdc;
    }
    public List findAll() {
        List myList = (List<VDC>) em.createQuery("select object(o) from VDC as o where o.dtype = 'Basic' order by o.name").getResultList();
        Iterator iterator = myList.iterator();
        return em.createQuery("select object(o) from VDC as o where o.dtype = 'Basic' order by o.name").getResultList();
    }

    public void create(Long userId,String name, String alias) {
        VDC addedSite = new VDC();
        em.persist(addedSite);
        addedSite.setName(name);
        addedSite.setAlias(alias);
        VDCCollection addedRootCollection = new VDCCollection();
        addedRootCollection.setName(name);
        addedRootCollection.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED));
        addedRootCollection.setVisible(true);
        vdcCollectionService.create(addedRootCollection);
        addedSite.setRootCollection(addedRootCollection);
        addedSite.getOwnedCollections().add(addedRootCollection);
        VDCNetwork vdcNetwork = vdcNetworkService.find(new Long(1));
        addedSite.setDefaultTemplate(vdcNetwork.getDefaultTemplate());
        addedSite.setHeader(vdcNetwork.getDefaultVDCHeader());
        addedSite.setFooter(vdcNetwork.getDefaultVDCFooter());
        addedSite.setRestricted(true);
        addedSite.setDisplayAnnouncements(false);
        ArrayList advancedSearchFields = new ArrayList();
        ArrayList searchResultsFields = new ArrayList();
        List postgresStudyFields = studyFieldService.findAll();
        for (Iterator it = postgresStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isAdvancedSearchField()){
                advancedSearchFields.add(elem);
            }
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        addedSite.setAdvSearchFields(advancedSearchFields);
        addedSite.setSearchResultFields(searchResultsFields);
        addedSite.setCreator(em.find(VDCUser.class,userId));
   
        addedRootCollection.setOwner(addedSite);
        vdcCollectionService.edit(addedRootCollection);
        userService.addVdcRole(userId,findByAlias(addedSite.getAlias()).getId(), roleService.ADMIN);
        
    }
    
    public void create(Long userId,String name, String alias, String affiliation) {
        VDC addedSite = new VDC();
        em.persist(addedSite);
        addedSite.setName(name);
        addedSite.setAlias(alias);
        addedSite.setAffiliation(affiliation);
        VDCCollection addedRootCollection = new VDCCollection();
        addedRootCollection.setName(name);
        addedRootCollection.setReviewState(reviewStateService.findByName(ReviewStateServiceLocal.REVIEW_STATE_RELEASED));
        addedRootCollection.setVisible(true);
        vdcCollectionService.create(addedRootCollection);
        addedSite.setRootCollection(addedRootCollection);
        addedSite.getOwnedCollections().add(addedRootCollection);
        VDCNetwork vdcNetwork = vdcNetworkService.find(new Long(1));
        addedSite.setDefaultTemplate(vdcNetwork.getDefaultTemplate());
        addedSite.setHeader(vdcNetwork.getDefaultVDCHeader());
        addedSite.setFooter(vdcNetwork.getDefaultVDCFooter());
        addedSite.setRestricted(true);
        addedSite.setDisplayAnnouncements(false);
        ArrayList advancedSearchFields = new ArrayList();
        ArrayList searchResultsFields = new ArrayList();
        List postgresStudyFields = studyFieldService.findAll();
        for (Iterator it = postgresStudyFields.iterator(); it.hasNext();) {
            StudyField elem = (StudyField) it.next();
            if (elem.isAdvancedSearchField()){
                advancedSearchFields.add(elem);
            }
            if (elem.isSearchResultField()){
                searchResultsFields.add(elem);
            }
        }
        addedSite.setAdvSearchFields(advancedSearchFields);
        addedSite.setSearchResultFields(searchResultsFields);
        addedSite.setCreator(em.find(VDCUser.class,userId));
   
        addedRootCollection.setOwner(addedSite);
        vdcCollectionService.edit(addedRootCollection);
        userService.addVdcRole(userId,findByAlias(addedSite.getAlias()).getId(), roleService.ADMIN);
        
    }
    
    public VDC getVDCFromRequest(HttpServletRequest request) {
        VDC vdc = (VDC)request.getAttribute("vdc");
        
        if (vdc==null) {
            Iterator iter = request.getParameterMap().keySet().iterator();
            while (iter.hasNext()) {
                Object key = (Object) iter.next();
                if ( key instanceof String && ((String) key).indexOf("vdcId") != -1 ) {
                    try {
                        Long vdcId = new Long( (String) request.getParameter((String) key) );
                        vdc = find(vdcId);
                        request.setAttribute("vdc",vdc);
                    } catch (NumberFormatException e) {} // param is not a Long, ignore it 
                    
                    break;
                }
            }
        }
        return vdc; 
    }
    
    public void addContributorRequest(Long vdcId, Long userId) {
            
             Role contributor = (Role)roleService.findByName(RoleServiceLocal.CONTRIBUTOR);
             VDC vdc = em.find(VDC.class, vdcId);
             VDCUser user= em.find(VDCUser.class,userId);
             RoleRequest roleRequest = new RoleRequest();
             roleRequest.setRole(contributor);
             roleRequest.setVdcUser(user);
             roleRequest.setVdc(vdc);
             vdc.getRoleRequests().add(roleRequest);

        
    }

    public List getLinkedCollections(VDC vdc) {
        return getLinkedCollections(vdc,false);
    }    
    
    public List getLinkedCollections(VDC vdc, boolean getHiddenCollections) {
        if (getHiddenCollections) {
            return vdc.getLinkedCollections();
        } else {
            List linkedColls = new ArrayList();
            Iterator iter = vdc.getLinkedCollections().iterator();
            while (iter.hasNext()) {
                VDCCollection link = (VDCCollection) iter.next();
               if (link.isVisible()) {
                    linkedColls.add( link );
               }
            }
            
            return linkedColls;
        }
    }
    
    public void delete (Long vdcId) {
        VDC vdc = em.find(VDC.class,vdcId);
        em.refresh(vdc);
        List studyIds = new ArrayList();
        
        // Get the studyIds separately, to avoid a ConcurrentAccess Exception
        // (This is necessary because the studies are deleted in Native SQL)
        for (Iterator it = vdc.getOwnedStudies().iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            studyIds.add(elem.getId());
        }       
        
        if (!studyIds.isEmpty()) {
            studyService.deleteStudyList(studyIds);
        }
        
        vdc.getOwnedStudies().clear();
        
       
        vdc.setRootCollection(null);
       
        for (Iterator it = vdc.getOwnedCollections().iterator(); it.hasNext();) {
           VDCCollection elem = (VDCCollection) it.next();
           elem.setParentCollection(null);
           elem.setOwner(null);
           // Remove this Collection from all linked VDCs
           for (Iterator itc = elem.getLinkedVDCs().iterator(); itc.hasNext();) {
               VDC linkedVDC = (VDC)itc.next();
               linkedVDC.getLinkedCollections().remove(elem);
           }    
        }
        
       for (Iterator it = vdc.getLinkedCollections().iterator(); it.hasNext();) {
           VDCCollection elem = (VDCCollection) it.next();
           VDCCollection coll = em.find(VDCCollection.class, elem.getId());
           coll.getLinkedVDCs().remove(vdc);
       }
        
       for (Iterator it = vdc.getVdcGroups().iterator(); it.hasNext();) {
            VDCGroup vdcGroup = (VDCGroup)it.next();
            vdcGroup.getVdcs().remove(vdc);
       }
        
         for (Iterator it = vdc.getVdcRoles().iterator(); it.hasNext();) {
            VDCRole vdcRole=(VDCRole)it.next();
            VDCUser vdcUser = vdcRole.getVdcUser();
            vdcUser.getVdcRoles().remove(vdcRole);
       }
        
      
        
        if (vdc.isHarvestingDataverse()) {
            harvesterService.removeHarvestTimer(vdc.getHarvestingDataverse());
        }
        em.remove(vdc);
    
    }
    
      public List findAllNonHarvesting() {
        return em.createQuery("select object(o) from VDC as o where o.harvestingDataverse is null order by o.name").getResultList();
           
      }
      /**
       * findVdcsNotInGroups
       *
       * A method to find vdcs
       * that are not associated with
       * a vdc group. This is for the network
       * level (DVN) where the home page
       * display will be a list.
       *
       */
      
       public List<VDC> findVdcsNotInGroups() {
           String query = "select object(o) FROM VDC as o WHERE o.dtype = 'Scholar' AND o.id NOT IN (SELECT gvdcs.id FROM VDCGroup as groups JOIN groups.vdcs as gvdcs)";
           return (List)em.createQuery(query).getResultList();
           
       }   
       
       /** An overloaded method to make the transition to 
        * the scholar dataverses no longer being their own type
        * 
        * @param dtype  the dataverse type
        * @ author wbossons
        */
       public List<VDC> findVdcsNotInGroups(String dtype) {
           String query = "select object(o) FROM VDC as o WHERE o.dtype = :fieldName AND o.id NOT IN (SELECT gvdcs.id FROM VDCGroup as groups JOIN groups.vdcs as gvdcs)";
           return (List)em.createQuery(query).setParameter("fieldName",dtype).getResultList();
       } 
       
        public Map getVdcTemplatesMap(Long vdcId) {
            VDC vdc = em.find(VDC.class, vdcId);
            Map templatesMap = new LinkedHashMap();
            Template defaultNetworkTemplate = vdcNetworkService.find().getDefaultTemplate();
            templatesMap.put(defaultNetworkTemplate.getName(), defaultNetworkTemplate.getId());
            Collection<Template> vdcTemplates = vdc.getTemplates();
            if (vdcTemplates!=null) {
                for (Template template: vdcTemplates) {
                    templatesMap.put(template.getName(), template.getId());
                }
            }
            return templatesMap;
        
        }
        
        public List<Template> getOrderedTemplates(Long vdcId) {
             
            String query = "select object(o) FROM Template as o WHERE o.vdc.id = :fieldName ORDER BY o.name";
            return (List)em.createQuery(query).setParameter("fieldName",vdcId).getResultList();
           
           
        }
        
        public void updateDefaultTemplate(Long vdcId, Long templateId) {
            VDC vdc = em.find(VDC.class, vdcId);
            Template template= em.find(Template.class, templateId);
            vdc.setDefaultTemplate(template);
        }
        
       
}
