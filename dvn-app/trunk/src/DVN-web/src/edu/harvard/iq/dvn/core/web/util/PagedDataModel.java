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
