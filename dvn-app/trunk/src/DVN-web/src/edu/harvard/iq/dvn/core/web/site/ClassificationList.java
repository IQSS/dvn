/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.site;

import edu.harvard.iq.dvn.core.vdc.VDCGroup;
import edu.harvard.iq.dvn.core.vdc.VDCGroupServiceLocal;
import edu.harvard.iq.dvn.core.web.SortableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.naming.InitialContext;

/**
 *
 * @author Ellen Kraffmiller
 */
public class ClassificationList extends SortableList {
    @EJB
    VDCGroupServiceLocal vdcGroupService;
    private Collection<ClassificationUI> classificationUIs;
    // TODO: verify that this does not break the Add/Edit Site pages. If so those need to be adapted, or
    // I can start a separate array list for what I need and keep this a collection
    private ArrayList<ClassificationUI> visibleClassificationUIs        = new ArrayList<ClassificationUI>();
    // END TODO

    private static final String NAME_COLUMN_NAME               = "Name";
    private static final String DESCRIPTION_COLUMN_NAME        = "Description";
    private static final String SUBCLASSIFICATIONS_COLUMN_NAME = "Subclassifications";
    
    public ClassificationList() {
        super(NAME_COLUMN_NAME);
    }


    private void init() {
        Long vdcgroupId   = null;
        Long childGroupId = null;
        initVdcGroupService(); // TODO - remove this in favor of managed bean and @EJB
        classificationUIs = new ArrayList<ClassificationUI>();
        Collection<VDCGroup> allTopGroups = vdcGroupService.findByParentId(null);
        int count = 0;
        for (VDCGroup group : allTopGroups) {
            ClassificationUI classificationUI = new ClassificationUI(group,1);
            classificationUI.setExpanded(!classificationUI.isExpanded());
            classificationUI.setParentId(new Long("-1"));
            classificationUIs.add(classificationUI);
            visibleClassificationUIs.add(classificationUI);
            vdcgroupId = group.getId();
            Collection<VDCGroup> childGroups = vdcGroupService.findByParentId(vdcgroupId);
            for (VDCGroup child : childGroups) {
                count++;
                ClassificationUI childClassificationUI = new ClassificationUI(child,2);
                childClassificationUI.setParentId(vdcgroupId);
                classificationUIs.add(childClassificationUI);
                childGroupId = child.getId();
                Collection<VDCGroup> grandChildGroups = vdcGroupService.findByParentId(childGroupId);
                for (VDCGroup grandChild : grandChildGroups) {
                    count++;
                    ClassificationUI grandChildClassification = new ClassificationUI(grandChild,3);
                    grandChildClassification.setParentId(childGroupId);
                    classificationUIs.add(grandChildClassification);
                }
                childClassificationUI.setTotalSubclassifications(grandChildGroups.size());
            }
            classificationUI.setTotalSubclassifications(count);
            count = 0;
        }
    }

    private void initVdcGroupService() {
        if (vdcGroupService == null) {
            try {
                vdcGroupService = (VDCGroupServiceLocal) new InitialContext().lookup("java:comp/env/vdcGroupService");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<ClassificationUI> getClassificationUIs() {
        if (classificationUIs == null) {
            init();
        }
        return classificationUIs;
    }

    public Collection<ClassificationUI> getVisibleClassificationUIs() {
        if (classificationUIs == null) {
            init();
        }
        return visibleClassificationUIs;
    }

    ArrayList<ClassificationUI> manageClassificationsList = new ArrayList<ClassificationUI>();
    

     /** getClassificationUIs
      *
      * Expands and contracts the visible classifications.
      *
      * @param parentNodeId  the node that was clicked
      *
      * @author wbossons
     *
     */
    public ArrayList<ClassificationUI> getClassificationUIs(Long parentNodeId) {
            if (parentNodeId.equals(new Long("-1"))) {
                getClassificationUIs();
                return visibleClassificationUIs;
            }
            ArrayList removeList = new ArrayList();
            Iterator iterator    = classificationUIs.iterator();
            int index            = 0;
            int childCount       = 0;
            boolean isExpanded   = false;
            ClassificationUI classificationUI       = null;
            ClassificationUI parentClassificationUI = null;
            while (iterator.hasNext()) {
                classificationUI = (ClassificationUI)iterator.next();
                if (parentClassificationUI == null && classificationUI.getVdcGroup().getId().equals(parentNodeId)) {
                    parentClassificationUI = classificationUI;
                    classificationUI       = (ClassificationUI)iterator.next();
                }
                if (classificationUI.getParentId().equals(parentNodeId)) {
                    if (!visibleClassificationUIs.contains(classificationUI)) {
                     childCount++;
                     index = visibleClassificationUIs.indexOf(parentClassificationUI) + childCount;
                     visibleClassificationUIs.add(index, classificationUI);
                     isExpanded = true;
                     classificationUI.setExpanded(!isExpanded);
                    } else {
                         removeList.add(classificationUI);
                    }
                }
            }
            parentClassificationUI.setExpanded(isExpanded);
            if (!removeList.isEmpty())
                    removeFromList(removeList);
            return visibleClassificationUIs;
        }


    /** removeFromList
     *
     * Takes an ArrayList<ClassificationUI> and then removes
     * the members from the visible classifications. Used by contraction
     * to contract all the levels when a top classification is closed.
     *
     * @param removeList the child classifications that are being removed
     *
     *
     * @author wbossons
     *
     */
    private void removeFromList(ArrayList<ClassificationUI> removeList) {
        Iterator outerIterator = classificationUIs.iterator();
        while (outerIterator.hasNext()) {
            ClassificationUI classificationUI = (ClassificationUI)outerIterator.next();
            if (removeList.contains(classificationUI) && visibleClassificationUIs.contains(classificationUI)) {
                visibleClassificationUIs.remove(classificationUI);
            }
            Iterator innerIterator = removeList.iterator();
            while (innerIterator.hasNext()) {
                ClassificationUI childClassificationUI = (ClassificationUI)innerIterator.next();
                if (classificationUI.getParentId().equals(childClassificationUI.getVdcGroup().getId()) &&
                        visibleClassificationUIs.contains(classificationUI)) {
                    visibleClassificationUIs.remove(classificationUI);
                }
            }
        }
        removeList.clear();
    }

        protected void sort() {
        Comparator comparator = new Comparator() {
        public int compare(Object o1, Object o2) {
                ClassificationUI c1 = (ClassificationUI) o1;
                ClassificationUI c2 = (ClassificationUI) o2;
                if (sortColumnName == null) {
                    return 0;
                }
                try {
                    if (sortColumnName.equals(NAME_COLUMN_NAME)) {
                        return ascending ?
                                new String(c1.getVdcGroup().getName().toUpperCase()).compareTo(new String(c2.getVdcGroup().getName().toUpperCase())) :
                                new String(c2.getVdcGroup().getName().toUpperCase()).compareTo(new String(c1.getVdcGroup().getName().toUpperCase()));
                    } else if (sortColumnName.equals(DESCRIPTION_COLUMN_NAME)) {
                        return ascending ? c1.getVdcGroup().getDescription().toUpperCase().compareTo(c2.getVdcGroup().getDescription().toUpperCase()) :
                                c2.getVdcGroup().getDescription().toUpperCase().compareTo(c1.getVdcGroup().getDescription().toUpperCase());
                    } else if (sortColumnName.equals(SUBCLASSIFICATIONS_COLUMN_NAME)) {
                        return ascending ? c1.getTotalSubclassifications().compareTo(c2.getTotalSubclassifications()) :
                                c2.getTotalSubclassifications().compareTo(c1.getTotalSubclassifications());
                    } else {
                      return 0;
                    }
                } catch (Exception npe) {
                    return 1;
                }
        }
        };
        Iterator iterator = visibleClassificationUIs.iterator();
        // a sort always contracts the list, sorting the top level only
        while (iterator.hasNext())  {
            ClassificationUI classificationui = (ClassificationUI)iterator.next();
            if (classificationui.getLevel() == 2 || classificationui.getLevel() == 3)
                iterator.remove();
            else
                classificationui.setExpanded(false);
        }
        Collections.sort(visibleClassificationUIs, comparator);
        
    }

    public String getOldSort() {
        return this.oldSort;
    }

    public void setOldSort(String oldSort) {
        this.oldSort = oldSort;
    }

    public boolean getOldAscending() {
        return this.oldAscending;
    }
    
        public void setClassificationUIs(ArrayList<ClassificationUI> classificationUIs) {
        this.classificationUIs = classificationUIs;
    }

    public void setTopClassificationUIs(ArrayList<ClassificationUI> topclassifications) {
        this.visibleClassificationUIs = topclassifications;
    }

    public String getNameColumnName()                { return NAME_COLUMN_NAME; }
    public String getDescriptionColumnName()         { return DESCRIPTION_COLUMN_NAME; }
    public String getSubclassificationsColumnName()  { return SUBCLASSIFICATIONS_COLUMN_NAME; }

    public boolean isDefaultAscending(String columnName) {
        return true;
    }

    public boolean getAscending() {
        return ascending;
    }

}
