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
import edu.harvard.hmdc.vdcnet.admin.VDCUser;
import edu.harvard.hmdc.vdcnet.study.ReviewStateServiceLocal;
import edu.harvard.hmdc.vdcnet.study.Study;
import edu.harvard.hmdc.vdcnet.study.StudyField;
import edu.harvard.hmdc.vdcnet.study.StudyFieldServiceLocal;
import edu.harvard.hmdc.vdcnet.study.StudyServiceLocal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;


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
       } catch (javax.persistence.NoResultException e) {
           // Do nothing, just return null. 
       }
       return vdc;
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
        return em.createQuery("select object(o) from VDC as o order by o.name").getResultList();
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
        
        for (Iterator it = vdc.getOwnedStudies().iterator(); it.hasNext();) {
            Study elem = (Study) it.next();
            studyService.deleteStudy(elem.getId());    
        }
   //    vdc.getOwnedStudies().clear();
       
        vdc.setRootCollection(null);
       
        for (Iterator it = vdc.getOwnedCollections().iterator(); it.hasNext();) {
           VDCCollection elem = (VDCCollection) it.next();
           elem.setParentCollection(null);
           elem.setOwner(null);           
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
           String query = "select object(o) FROM VDC as o WHERE o.id NOT IN (SELECT gvdcs.id FROM VDCGroup as groups JOIN groups.vdcs as gvdcs) order by o.name";
           return (List)em.createQuery(query).getResultList();
           
       }    
}
