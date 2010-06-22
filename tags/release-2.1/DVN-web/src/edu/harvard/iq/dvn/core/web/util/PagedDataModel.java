/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.util;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.DataModel;

/**
 *
 * @author wbossons
 */
public class PagedDataModel extends DataModel {
    private int rowIndex = -1;
  
  private int rowCount;
  
  private int pageSize;
  
  private List list;
  
  public PagedDataModel() {
    super();
  }
  
  public PagedDataModel(List list, int totalNumRows, int pageSize) {
    super();
    setWrappedData(list);
    this.rowCount = totalNumRows;
    this.pageSize = pageSize;
  }
  
  public boolean isRowAvailable() {
    if(list == null)
      return false;
    
    int rowIndex = getRowIndex();
    if(rowIndex >=0 && rowIndex < list.size())
      return true;
    else
      return false;
  }

  public int getRowCount() {
    return rowCount;
  }

  public Object getRowData() {
    if(list == null)
      return null;
    else if(!isRowAvailable())
      throw new IllegalArgumentException();
    else {
      int dataIndex = getRowIndex();
      return list.get(dataIndex);
    }
  }

  public int getRowIndex() {
    return (rowIndex % pageSize);
  }

  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

  public Object getWrappedData() {
    return list;
  }
  
  public void setWrappedData(Object list) {
    this.oldPage = (List)this.getWrappedData();
    this.list = (List) list;
    //this.oldPage = (List) list;
  }

  private List oldPage = new ArrayList();

  public List getOldPage() {
      return this.oldPage;
  }

  public void setOldPage(List oldpage)  {
      this.oldPage = oldpage;
  }
 
}
