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
 * TemplateServiceBean.java
 *
 * Created on July 22, 2008, 11:47 AM
 * Happy Birthday Gustavo
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.vdc.VDC;
import edu.harvard.hmdc.vdcnet.vdc.VDCNetworkServiceLocal;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Ellen Kraffmiller
 */
@Stateless
public class TemplateServiceBean implements edu.harvard.hmdc.vdcnet.study.TemplateServiceLocal, java.io.Serializable {

    @PersistenceContext(unitName = "VDCNet-ejbPU")
    EntityManager em;
   
    @EJB
    VDCNetworkServiceLocal vdcNetworkService;
   
  
    private static final Logger logger = Logger.getLogger("edu.harvard.hmdc.vdcnet.study.TemplateServiceBean");
  
    /**
     * Creates a new instance of TemplateServiceBean
     */
    public TemplateServiceBean() {
    }

    public void createTemplate(String templateName, Long studyId, Long vdcId) {
        Template template = new Template();
        template.setName(templateName);
        Study study = em.find(Study.class, studyId);
        study.getMetadata().copyMetadata(template.getMetadata());
        template.setVdc(em.find(VDC.class, vdcId));
        em.persist(template);

    }
    public void deleteTemplate(Long templateId) {
        Template template = em.find(Template.class, templateId);
        em.remove(template);
    }
    public Template getTemplate(Long templateId) {
        return em.find(Template.class, templateId);
    }
    
    public boolean isTemplateUsed(Long templateId) {
        String queryStr = "SELECT count(id) from study s where s.template_id="+templateId;
        Query query = em.createNativeQuery(queryStr);
        Object object = ((List)query.getSingleResult()).get(0);
        Long count = (Long)object;
        return count.compareTo(new Long(0))>0;
        
    }
}
