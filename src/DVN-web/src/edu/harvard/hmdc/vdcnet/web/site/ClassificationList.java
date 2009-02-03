/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.site;

import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import java.util.ArrayList;
import java.util.Collection;
import javax.ejb.EJB;

/**
 *
 * @author Ellen Kraffmiller
 */
public class ClassificationList {
    @EJB
    VDCGroupServiceLocal vdcGroupService;
    private Collection<ClassificationUI> classificationUIs;
    
    public ClassificationList() {}


    private void init() {
        classificationUIs = new ArrayList<ClassificationUI>();
        Collection<VDCGroup> allTopGroups = vdcGroupService.findByParentId(null);
        for (VDCGroup group : allTopGroups) {
            classificationUIs.add(new ClassificationUI(group));
            Collection<VDCGroup> childGroups = vdcGroupService.findByParentId(group.getId());
            for (VDCGroup child : childGroups) {
                classificationUIs.add(new ClassificationUI(child));
                Collection<VDCGroup> grandChildGroups = vdcGroupService.findByParentId(child.getId());
                for (VDCGroup grandChild : grandChildGroups) {
                    classificationUIs.add(new ClassificationUI(grandChild));
                }
            }
        }
    }

    public Collection<ClassificationUI> getClassificationUIs() {
        if (classificationUIs == null) {
            init();
        }
        return classificationUIs;
    }

    public void setClassificationUIs(Collection<ClassificationUI> classificationUIs) {
        this.classificationUIs = classificationUIs;
    }

}
