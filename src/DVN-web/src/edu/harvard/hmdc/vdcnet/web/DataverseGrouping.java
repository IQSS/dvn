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
import javax.faces.event.ActionEvent;
import java.sql.Timestamp;

// Referenced classes of package test:
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

public class DataverseGrouping extends SortableList {

     // Images used to represent expand/contract, spacer by default
    protected String expandImage;   // + or >
    protected String contractImage; // - or v
    protected static final String DEFAULT_IMAGE_DIR = "/resources/icefaces/dvn_rime/css-images/";
    String SPACER_IMAGE = "tree_line_blank.gif";
    // style for column that holds expand/contract image toggle, in the group
    // record row.
    protected String indentStyleClass = "";
    protected boolean selected; //used by the AddClassificationsPage
        /**
     * Gets the style class name used to define the first column of a files
     * record row.  This first column is where a expand/contract image is
     * placed.
     *
     * @return indent style class as defined in css file
     */
    public String getIndentStyleClass() {
        return indentStyleClass;
    }

    // dataTableColumn Names
    private static final String nameColumnName          = "Name";
    private static final String affiliationColumnName   = "Affiliation";
    private static final String createdByColumnName     = "Creator";
    private static final String dateCreatedColumnName   = "Created";
    private static final String dateReleasedColumnName  = "Released";
    private static final String lastUpdatedColumnName   = "Last Updated";
    private static final String activityColumnName      = "Activity";
    // network admin fields
    private static final String shortDescriptionColumnName   = "Description";
    private static final String subclassificationsColumnName = "Subclassifications";
    private static final String idColumnName                 = "Id";
    private static final String numberOwnedStudiesColumnName = "Owned Studies";
    private static final String typeColumnName          = "Type";

    ArrayList parentItems = new ArrayList();
    ArrayList childItems = new ArrayList();

    public DataverseGrouping() {
        super(nameColumnName);
    }

    public DataverseGrouping(Long id, String name, String recordType, ArrayList parentItems, boolean isExpanded, String expandImage, String contractImage, Long parentClassification) {
        super(nameColumnName);
        this.groupKey    = name.replaceAll(" ", "").toLowerCase();
        this.name        = name;
        this.recordType  = recordType;
        this.parentItems = parentItems;
        this.parentItems.add(this);
        this.isExpanded  = isExpanded;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.id          = id;
        this.parentClassification         = parentClassification;
        // update the default state of the node.
        if (this.isExpanded) {
            expandNodeAction();
        }
        this.classificationSelect = false;
    }

    //subgroups
    public DataverseGrouping(Long id, String name, String recordType, boolean isExpanded, String expandImage, String contractImage, Long parentClassification) {
        super(nameColumnName);
        this.groupKey    = name.replaceAll(" ", "").toLowerCase();
        this.name        = name;
        this.recordType  = recordType;
        this.isExpanded  = isExpanded;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.id          = id;
        this.parentClassification         = parentClassification;
        this.indentStyleClass = "childRowIndentStyle";
        // update the default state of the node.
        if (this.isExpanded) {
            expandNodeAction();
        }
        this.classificationSelect = false;
    }


    public DataverseGrouping(String name, String alias, String affiliation, Timestamp releaseDate, Timestamp lastUpdateTime, String shortDescription, String recordType, String activity) {
        super(nameColumnName);
        this.id             = id;
        this.name           = name;
        this.alias          = alias;
        this.affiliation    = affiliation;
        this.releaseDate    = releaseDate;
        this.lastUpdateTime = lastUpdateTime;
        this.shortDescription = shortDescription;
        this.recordType     = recordType;
        this.activity       = activity;
        this.indentStyleClass = "childRowIndentStyle";
        this.classificationSelect = false;
    }

    /** DataverseGrouping
     * Overloaded constructor for the
     * Add/Remove Dataverse to/from Page.
     *
     *
     * @param name
     * @param affiliation
     *
     *
     */
    public DataverseGrouping(Long id, String name, String affiliation) {
        super(nameColumnName);
        this.id             = id;
        this.name           = name;
        this.affiliation    = affiliation;
    }


    public void addChildItem(DataverseGrouping dvGroupRecord) {
        if (this.childItems != null && dvGroupRecord != null) {
            this.childItems.add(dvGroupRecord);
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
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
            recurseAndExpandNodeAction();
        }
        // remove items from list
        else {
            recurseAndContractNodeAction();
        }
    }

    /**
     * Toggles the expanded state of this dataverse group at
     * page load time.
     *
     */
     public void toggleSubGroupAction() {
        // toggle expanded state
            recurseAndContractNodeAction();
            recurseAndExpandNodeAction();
    }

      /**
     * Utility method to add all child nodes to the parent dataTable list.
     */
    @SuppressWarnings("unchecked")
    private void expandNodeAction() {
        if (childItems != null && childItems.size() > 0) {
            // get index of current node
            int index = parentItems.indexOf(this);
            parentItems.addAll(index + 1, childItems);
        }
    }



    /**
     * Utility method to remove all child nodes from the parent dataTable list.
     */
    private void contractNodeAction() {

        if (childItems != null && childItems.size() > 0) {
            // remove all items in childItems from the parent list
           parentItems.removeAll(childItems);
        }
    }


    //BEGIN RECURSIVE NODE ACTIONS

    List removeFromList = new ArrayList();
      /**
     * Utility method to recursively add all child nodes to their parents in the data table.
     */

    private void recurseAndExpandNodeAction() {
        if (childItems != null && childItems.size() > 0) {
            int index = parentItems.indexOf(this) + 1;
            synchronized(parentItems) {
                parentItems.addAll(index, childItems);
            }
            Iterator iterator = childItems.iterator();
            DataverseGrouping childitem = (DataverseGrouping)iterator.next();
            
            recurseAndExpandNodeAction(childitem);
            
                if (!removeFromList.isEmpty()) {
                    synchronized(parentItems) {
                      parentItems.addAll(index + 1, removeFromList);
                      removeFromList.clear();
                    }
                }
            
        }
    }

    private void recurseAndExpandNodeAction(DataverseGrouping childItem) {
            Iterator iterator = parentItems.iterator();
            parentItems.contains(childItem);
              while (iterator.hasNext()) {
                  DataverseGrouping item = (DataverseGrouping)iterator.next();
                  if (item.parentClassification.equals(childItem.id)) {
                      removeFromList.add(item);
                      if (item.childItems.size() >= 1)
                        recurseAndExpandNodeAction(item);
                  }
              }
        }

    /**
     * Recursive utility method to remove all child nodes from their parents in the dataTable list.
     */
    public void recurseAndContractNodeAction() {
        if (childItems != null && childItems.size() > 0) {
            Iterator iterator = childItems.iterator();
            while(iterator.hasNext()) {
                DataverseGrouping childitem = (DataverseGrouping)iterator.next();
                recurseAndContractNodeAction(childitem);
            }
            parentItems.removeAll(childItems);
            if (!removeFromList.isEmpty()) {
                  parentItems.removeAll(removeFromList);
            }
        }
    }

    private void recurseAndContractNodeAction(DataverseGrouping childItem) {
        Iterator iterator = parentItems.iterator();
              while (iterator.hasNext()) {
                  DataverseGrouping item = (DataverseGrouping)iterator.next();
                  if (item.parentClassification.equals(childItem.id)) {
                      removeFromList.add(item);
                      if (item.childItems.size() >= 1)
                        recurseAndContractNodeAction(item);
                  }
              }
    }
    //END RECURSIVE NODE ACTIONS


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
     *  Sorts the list of data.
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
                } else if (sortColumnName.equals(createdByColumnName)) {
                    return ascending ? c1.getCreatedBy().compareTo(c2.getCreatedBy()) :
                        c2.getCreatedBy().compareTo(c1.getCreatedBy());
                } else if (sortColumnName.equals(dateReleasedColumnName)) {
                    return ascending ?
                        c1.getReleaseDate().compareTo(c2.getReleaseDate()) :
                        c2.getReleaseDate().compareTo(c1.getReleaseDate());
                } else if (sortColumnName.equals(dateCreatedColumnName)) {
                    return ascending ?
                        c1.getCreationDate().compareTo(c2.getCreationDate()) :
                        c2.getCreationDate().compareTo(c1.getCreationDate());
                } else if (sortColumnName.equals(lastUpdatedColumnName)) {
                    return ascending ?
                            c1.getLastUpdateTime().compareTo(c2.getLastUpdateTime()) :
                            c2.getLastUpdateTime().compareTo(c1.getLastUpdateTime());
                } else if (sortColumnName.equals(activityColumnName)) {
                    return ascending ?
                            c1.getActivity().compareTo(c2.getActivity()) :
                            c2.getActivity().compareTo(c1.getActivity());
                } else if (sortColumnName.equals(shortDescriptionColumnName)) {
                    return ascending ? c1.getShortDescription().compareTo(c2.getShortDescription()) :
                            c2.getShortDescription().compareTo(c1.getShortDescription());
                } else if (sortColumnName.equals(subclassificationsColumnName)) {
                    return ascending ? c1.getSubclassification().compareTo(c2.getSubclassification()) :
                            c2.getSubclassification().compareTo(c1.getSubclassification());
                } else if (sortColumnName.equals(numberOwnedStudiesColumnName)) {
                    return ascending ?
                            c1.getNumberOwnedStudies().compareTo(c2.getNumberOwnedStudies()) :
                            c2.getNumberOwnedStudies().compareTo(c1.getNumberOwnedStudies());
                } else if (sortColumnName.equals(shortDescriptionColumnName)) {
                    return ascending ? c1.getType().compareTo(c2.getType()) :
                        c2.getType().compareTo(c1.getType());
                } else {
                    return 0;
                }
            }
        };
        if (!childItems.isEmpty())
            Collections.sort(childItems, comparator);
        else
            Collections.sort(parentItems, comparator);
    }

    public String getNameColumnName() {
        return nameColumnName;
    }

    public String getAffiliationColumnName() {
        return affiliationColumnName;
    }

    public String getCreatedByColumnName() {
        return createdByColumnName;
    }

    public String getDateCreatedColumnName() {
        return dateCreatedColumnName;
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

    public String getShortDescriptionColumnName() {
        return shortDescriptionColumnName;
    }

    public String getSubclassificationsColumnName() {
        return subclassificationsColumnName;
    }

    public String getNumberOwnedStudiesColumnName() {
        return numberOwnedStudiesColumnName;
    }

    public String getTypeColumnName() {
        return typeColumnName;
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
        if (expandImage != null && contractImage != null) {
            String dir = DEFAULT_IMAGE_DIR;
            String img = isExpanded ? contractImage : expandImage;
            return dir + img;
        } else {
            return DEFAULT_IMAGE_DIR + SPACER_IMAGE;
        }
    }

    public boolean isSelected() {
        return this.selected;
    }

    // mutators
    public void setSelected(boolean select) {
        this.selected = select;
    }

   /** DataverseGrouping display attributes
    *
    */

    private Long id; //TBD if needed
    private String alias;
    private String groupKey;
    private String name;
    private String affiliation;
    private String shortDescription;
    private String createdBy;
    private Long downloads;
    private Timestamp releaseDate;
    private Timestamp lastUpdateTime;
    private Timestamp creationDate;
    private LocalizedDate localizedDate = new LocalizedDate();
    private String recordType;
    private String activity;
    private Long parentClassification;
    // Manage Classifications specific fields
    private Long subclassification;
    private boolean classificationSelect = false;
    private Integer numberOwnedStudies;
    private String type;



     public void changeClassificationSelect(ValueChangeEvent event) {
        boolean newValue = (Boolean) event.getNewValue();
        this.classificationSelect = newValue;
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        // request.setAttribute("dataverseType", newValue);
        FacesContext.getCurrentInstance().renderResponse();
    }

    public boolean getClassificationSelect() {
        return classificationSelect;
    }

    public void setClassificationSelect(boolean classificationSelect) {
        this.classificationSelect = classificationSelect;
    }

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

    public Long getParentClassification() {
        return parentClassification;
    }

    public void setParentClassification(Long parentclass) {
        this.parentClassification = parentclass;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Long getSubclassification() {
        return subclassification;
    }

    public void setSubclassification(Long subclassification) {
        this.subclassification = subclassification;
    }

    public Integer getNumberOwnedStudies() {
        return numberOwnedStudies;
    }

    public void setNumberOwnedStudies(Integer numberOwnedStudies) {
        this.numberOwnedStudies = numberOwnedStudies;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    private String textIndent;

    public String getTextIndent() {
        return textIndent;
    }

    public void setTextIndent(String textIndent) {
        this.textIndent = textIndent;
    }

    public void setIndentStyleClass(String indentstyle) {
        this.indentStyleClass = indentstyle;
    }

    //******************* UTILS ***************************
    public String toString() {
          String dataverseToString = new String("");
          dataverseToString+="[ name = " + name + "; ";
          dataverseToString+=" id = " + ((id != null) ? id : "") + "; ";
          dataverseToString+=" parentClassification = " + ((parentClassification != null) ? parentClassification : "No parents") + "; ";
          dataverseToString+=" affiliation = " + ((affiliation != null) ? affiliation : "") + "; ";
          dataverseToString+=" shortDescription = " + ((shortDescription != null) ? shortDescription : "") + "; ";
          dataverseToString+=" nameColumnName = " + ((nameColumnName != null) ? nameColumnName : "") + "; ";
          dataverseToString+=" recordType = " + ((recordType != null) ? recordType : "") + "; ]\n\r";
          return  dataverseToString;
      }

    private String strToString;

    public String getStrToString() {
        return this.toString();
    }

 }