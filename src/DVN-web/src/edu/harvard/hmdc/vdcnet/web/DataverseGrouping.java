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


/** A class to represent the VDC class
 * so that various sorting and paging 
 * operations can be performed.
 * 
 * DataverseGrouping defines the sortColumnNames
 * and also stores the parent and child relationships
 * while providing methods for their manipulation.
 * 
 * @author wbossons
 */

package edu.harvard.hmdc.vdcnet.web;

import edu.harvard.hmdc.vdcnet.util.DateUtils;
import edu.harvard.hmdc.vdcnet.web.push.beans.NetworkStatsBean;
import java.awt.event.ActionEvent;
import java.sql.Timestamp;

// Referenced classes of package test:
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataverseGrouping extends SortableList {
     
     // Images used to represent expand/contract, spacer by default
    protected String expandImage;   // + or >
    protected String contractImage; // - or v
    protected static final String DEFAULT_IMAGE_DIR = "/resources/";
     
    // dataTableColumn Names
    private static final String nameColumnName          = "Name";
    private static final String affiliationColumnName   = "Affiliation";
    private static final String dateReleasedColumnName  = "Date Released";
    private static final String lastUpdatedColumnName   = "Last Updated";
    private static final String activityColumnName      = "Activity";
    
    ArrayList parentItems = new ArrayList();
    //ArrayList childItems = new ArrayList();
    
    DataverseGrouping[] items;
    
    
    public DataverseGrouping() {
        super(nameColumnName);
    }
    private Map allChildren = new LinkedHashMap();//used by addchilditem to collect all of the children for expansion and contraction support
    
    public DataverseGrouping(Long id, String name, String recordType, ArrayList parentItems, boolean isExpanded, String expandImage, String contractImage, boolean isTop) {
        super(nameColumnName);
        this.groupKey    = name.replaceAll(" ", "").toLowerCase();
        this.name        = name;
        this.recordType  = recordType;
        this.parentItems = parentItems;
        this.parentItems.add(this);
        this.isExpanded  = isExpanded;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.id = id;
        this.top = isTop;
        // update the default state of the node.
        if (this.isExpanded) {
            expandNodeAction();
        }
    }
    
    public DataverseGrouping(String name, String alias, String affiliation, Timestamp releaseDate, Timestamp lastUpdateTime, String dvnDescription, String recordType, String activity) {
        super(nameColumnName);
        this.id          = id;
        this.name        = name;
        this.alias       = alias;
        this.affiliation = affiliation;
        this.releaseDate = releaseDate;
        this.lastUpdateTime = lastUpdateTime;
        this.dvnDescription = dvnDescription;
        this.recordType     = recordType;
        //this.activity     = calculateActivity(alias);
        this.activity       = activity;
    }
    
    ArrayList childItems = new ArrayList();
    public void addChildItem(DataverseGrouping dvGroupRecord) {
        if (this.childItems != null && dvGroupRecord != null) {
            this.childItems.add(dvGroupRecord);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void addAllChildren() {
            Iterator iterator = this.childItems.iterator();
            ArrayList list = new ArrayList();
            while (iterator.hasNext()) {
                DataverseGrouping bean = (DataverseGrouping)iterator.next();
                list.add(bean);
            }
            allChildren.put(this.getGroupKey(), list);
    }
    
    public void removeChildItem(DataverseGrouping dvGroupRecord) {
        if (this.childItems != null && dvGroupRecord != null) {
            this.childItems.remove(dvGroupRecord);
        }
    }
    
    
    //TODO: removeChildItem from the lists when action toggled.
    
    //************ EXPAND/CONTRACT EVENTS *****************
     // indicates if node is in expanded state.
    protected boolean isExpanded;
    
   /**
     * Toggles the expanded state of this dataverse group.
     *
     * @param event
     */
    public void toggleSubGroupAction(ActionEvent event) {
        // toggle expanded state
        isExpanded = !isExpanded;
        // add sub elements to list
        if (isExpanded) {
            expandNodeAction();
        }
        // remove items from list
        else {
           contractNodeAction();
        }
    }
    
    public void toggleSubGroupAction() {
        System.out.println("committed the action");
        // toggle expanded state
        isExpanded = !isExpanded;
        // add sub elements to list
        if (isExpanded) {
            expandNodeAction();
        }
        // remove items from list
        else {
           contractNodeAction();
        }
    }
    
      /**
     * Utility method to add all child nodes to the parent dataTable list.
     */
    @SuppressWarnings("unchecked")
    private void expandNodeAction() {
        if (childItems != null && childItems.size() > 0) {
            // get index of current node
            int index = parentItems.indexOf(this);
            // add all items in childFilesRecords to the parent list
            parentItems.addAll(index + 1, childItems);
        } else if (!allChildren.isEmpty()) {
            // get index of current node
            int index = parentItems.indexOf(this);
            // add all items in childItems to the parent list
            System.out.println("the index of the current node is " + index);
            DataverseGrouping bean = (DataverseGrouping)parentItems.get(index);
            System.out.println("the children of this parent are in a list of size " + bean.childItems.size());
            ArrayList list = (ArrayList)allChildren.get(bean.getGroupKey());
            bean.childItems.addAll(list);
            //parentItems.addAll(index + 1, list);
        }
    }

    /**
     * Utility method to remove all child nodes from the parent dataTable list.
     */
    private void contractNodeAction() {
        
        if (childItems != null && childItems.size() > 0) {
            // remove all items in childItems from the parent list
            //removes all the parents. I just want the children TODO: work it that way
            Iterator iterator = childItems.iterator();
           while (iterator.hasNext()) {
              DataverseGrouping grouping = (DataverseGrouping)iterator.next();
              System.out.println("The parent item name is " + grouping.getName());
              iterator.remove();
            }
           //parentItems.removeAll(childItems);   
        }
    }
    
    // ************  SORTING **************
    
   /**
     * Determines the sortColumnName order.
     *
     * @param   sortColumn to sortColumnName by.
     * @return  whether sortColumnName order is ascending or descending.
     */
    protected boolean isDefaultAscending(String sortColumn) {
        return true;
    }

    /**
     *  Sorts the list of car data.
     */
    protected void sort() {
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                DataverseGrouping c1 = (DataverseGrouping) o1;
                DataverseGrouping c2 = (DataverseGrouping) o2;
                if (sortColumnName == null) {
                    return 0;
                }
                if (sortColumnName.equals(nameColumnName)) {
                    return ascending ?
                            new String(c1.getName()).compareTo(new String(c2.getName())) :
                            new String(c2.getName()).compareTo(new String(c1.getName()));
                } else if (sortColumnName.equals(affiliationColumnName)) {
                    return ascending ? c1.getAffiliation().compareTo(c2.getAffiliation()) :
                            c2.getAffiliation().compareTo(c1.getAffiliation());
                } else if (sortColumnName.equals(dateReleasedColumnName)) {
                    return ascending ? 
                        c1.getReleaseDate().compareTo(c2.getReleaseDate()) :
                        c2.getReleaseDate().compareTo(c1.getReleaseDate());
                } else if (sortColumnName.equals(lastUpdatedColumnName)) {
                    return ascending ?
                            c1.getLastUpdateTime().compareTo(c2.getLastUpdateTime()) :
                            c2.getLastUpdateTime().compareTo(c1.getLastUpdateTime());
                    } else if (sortColumnName.equals(activityColumnName)) {
                    return ascending ?
                            c1.getActivity().compareTo(c2.getActivity()) :
                            c2.getActivity().compareTo(c1.getActivity());
                } else {
                    return 0;
                }
            }
        };
        Collections.sort(childItems, comparator);
    }
    
    public String getNameColumnName() {
        return nameColumnName;
    }

    public String getAffiliationColumnName() {
        return affiliationColumnName;
    }

    public String getDateReleasedColumnName() {
        return dateReleasedColumnName;
    }

    public String getLastUpdatedColumnName() {
        return lastUpdatedColumnName;
    }
    
    public String getActivityColumnName() {
        return activityColumnName;
    }
    
  public String toString() {
      return "[ name = " + name + " ]; [ affiliation = " + affiliation + " ]";
  }
            // end sorting related stuff
  
    
    //************  ACCESSORS/MUTATORS ********************
    
    public ArrayList getParentItems () {
        return parentItems;
    }

    /**
     * Gets the child dataverses
     * @return arraylist of member dataverses.
     */
   @SuppressWarnings("unchecked")
   public ArrayList<DataverseGrouping> getChildItems() {
        // we only want to sortColumnName if the column or ordering has changed.
            if (!oldSort.equals(sortColumnName) ||
                oldAscending != ascending){
                sort();
                oldSort = sortColumnName;
                oldAscending = ascending;
            }
            return childItems;
        }

       /**
     * Gets the image which will represent either the expanded or contracted
     * state of the <code>FilesGroupRecordBean</code>.
     *
     * @return name of image to draw
     */
    public String getExpandContractImage() {
        //if (styleBean != null) {
            String dir = DEFAULT_IMAGE_DIR;
            String img = isExpanded ? contractImage : expandImage;
            return dir + img;
        //}
        //return DEFAULT_IMAGE_DIR + SPACER_IMAGE;
    }


   /** DataverseGrouping display attributes
    * 
    */
    
    private Long id; //TBD if needed
    private String alias;
    private String groupKey;
    private String name;
    private String affiliation;
    private String dvnDescription;
    private Long downloads;
    private Timestamp releaseDate;
    private Timestamp lastUpdateTime;
    private Timestamp creationDate;
    private LocalizedDate localizedDate = new LocalizedDate();
    private String recordType;
    private String activity;
    private boolean top;
        
    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getReleaseDate() {
        if(releaseDate != null)
            return localizedDate.getLocalizedDate(releaseDate, DateFormat.MEDIUM);
        else
            return "--";
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getLastUpdateTime() {
        if(lastUpdateTime != null)
            return getLastUpdatedTime(lastUpdateTime.getTime());
        else
            return "--";
    }
    
    private String getLastUpdatedTime(Long lastupdatetime) {
        //TODO: convert this to n (hours, months days) time ago
        String timestampString = DateUtils.getTimeInterval(lastupdatetime);
        return timestampString;
    }

    public void setLastUpdateTime(Timestamp lastDateUpdated) {
        lastUpdateTime = lastDateUpdated;
    }

    public String getCreationDate() {
        if(creationDate != null)
            return localizedDate.getLocalizedDate(creationDate, 2);
        else
            return "";
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getAffiliation() {
        if(affiliation != null)
            return affiliation;
        else
            return "";
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
     public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id.toString();
    }

    public void setId(Long Id) {
        id = Id;
    }

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean istop) {
        this.top = istop;
    }

    public String getDvnDescription() {
        return dvnDescription;
    }

    public void setDvnDescription(String dvnDescription) {
        this.dvnDescription = dvnDescription;
    }
    
    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupkey) {
        this.groupKey = groupkey;
    }
    
    /**
     * Get the value of activity
     *
     * @return the value of activity
     */
    public String getActivity() {
       return activity;
    }

    /**
     * Set the value of activity
     *
     * @param activity new value of activity
     */
    public void setActivity(String activity) {
        this.activity = activity;
    }
    
    
 }