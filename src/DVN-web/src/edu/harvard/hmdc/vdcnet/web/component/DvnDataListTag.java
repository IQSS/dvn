/**
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
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * DvnDataListTag.java
 *
 * Created on November 15, 2007, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.component;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 *
 * @author wbossons
 */
public class DvnDataListTag  extends UIComponentTag {
    
    private ValueExpression contents;
    private ValueExpression tabs;
    private ValueExpression tab;
    private ValueExpression idName;
    private ValueExpression lastRecord;
    private ValueExpression firstRecord;
    private ValueExpression pagingDirection;
    private ValueExpression targetGroup;
    private ValueExpression defaultDisplayNumber;
    
    /** Creates a new instance of DvnDataListTag */
    public DvnDataListTag() {
    }
    
    public String getComponentType() {
        //Associates tag with UI Component registered in faces-config.xml
        return "DvnDataList";
    }
  
    public String getRendererType() {
        //renderer is embedded in the component, return null
        return null;
    }
    
    /**
     * process the superclass pros and then process the
     * incoming tag's custom attributes
     *
     * @author wbossons
     *
     */
    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        DvnDataList datalist = (DvnDataList)component;
         if(contents != null) {
              if (!contents.isLiteralText()) {
                datalist.setValueExpression("contents", contents);
              }
              else {
                datalist.getAttributes().put("contents",contents.getExpressionString());
              }
        }
        if(tabs != null) {
              if (!tabs.isLiteralText()) {
                datalist.setValueExpression("tabs", tabs);
              }
              else {
                datalist.getAttributes().put("tabs", tabs.getExpressionString());
              }
        }
        if(tab != null) {
              if (!tab.isLiteralText()) {
                datalist.setValueExpression("tab", tab);
              }
              else {
                datalist.getAttributes().put("tab", tab.getExpressionString());
              }
        }
        if (idName != null) {
            if (!idName.isLiteralText()) {
                datalist.setValueExpression("idName", idName);
              }
              else {
                datalist.getAttributes().put("idName", idName.getExpressionString());
              }
        }
        if (lastRecord != null) {
            if (!lastRecord.isLiteralText()) {
                datalist.setValueExpression("lastRecord", lastRecord);
            } else {
                datalist.getAttributes().put("lastRecord", lastRecord);
            }
        }
        if (firstRecord != null) {
            if (!firstRecord.isLiteralText()) {
                datalist.setValueExpression("firstRecord", firstRecord);
            } else {
                datalist.getAttributes().put("firstRecord", firstRecord);
            }
        }
        if (pagingDirection != null) {
            if (!pagingDirection.isLiteralText()) {
                datalist.setValueExpression("pagingDirection", pagingDirection);
            } else {
                datalist.getAttributes().put("pagingDirection", pagingDirection);
            }
        }
        if (targetGroup != null) {
            if (!targetGroup.isLiteralText()) {
                datalist.setValueExpression("targetGroup", targetGroup);
            } else {
                datalist.getAttributes().put("targetGroup", targetGroup);
            }
        }
        if (defaultDisplayNumber != null) {
            if (!defaultDisplayNumber.isLiteralText()) {
                datalist.setValueExpression("defaultDisplayNumber", defaultDisplayNumber);
            } else {
                datalist.getAttributes().put("defaultDisplayNumber", defaultDisplayNumber);
            }
        }
    }
    

    /** 
     * setter and getter the contents
     * @author wbossons
     *
     * 
     */
    public ValueExpression getContents() {
        return this.contents;
    }
    public void setContents(ValueExpression contents) {
    this.contents = contents;
  }
    
    /** 
     * setter and getter the tabs
     * @author wbossons
     *
     * 
     */
    public ValueExpression getTabs() {
        return this.tabs;
    }
    public void setTabs(ValueExpression tabs) {
        this.tabs = tabs;
  }
    
    /** 
     * setter and getter the tab
     *
     * @author wbossons
     *
     * 
     */
    public void setTab(ValueExpression tab) {
        this.tab = tab;
    }
    
    public ValueExpression getTab(){
        return tab;
    }
    
        /** 
     * setter and getter the idName
     *
     * @author wbossons
     *
     * 
     */
    public void setIdName(ValueExpression idName) {
        this.idName = idName;
    }
    
    public ValueExpression getIdName(){
        return idName;
    }
   
    /** 
     * setter and getter the lastRecord
     *
     * @author wbossons
     *
     * 
     */
    public void setLastRecord(ValueExpression lastrecord) {
        this.lastRecord = lastrecord;
    }
    
    public ValueExpression getLastRecord(){
        return lastRecord;
    }
    
     /** 
     * setter and getter the firstRecord
     *
     * @author wbossons
     *
     * 
     */
    public void setFirstRecord(ValueExpression firstrecord) {
        this.firstRecord = firstrecord;
    }
    
    public ValueExpression getFirstRecord(){
        return firstRecord;
    }
    
    /** 
     * setter and getter the pagingDirection
     *
     * @author wbossons
     *
     * 
     */
    public void setPagingDirection(ValueExpression pagingdirection) {
        this.pagingDirection = pagingdirection;
    }
    
    public ValueExpression getPagingDirection(){
        return pagingDirection;
    }
    
   /** 
     * setter and getter the targetGroup
     *
     * @author wbossons
     *
     * 
     */
    public void setTargetGroup(ValueExpression targetgroup) {
        this.targetGroup = targetgroup;
    }
    
    public ValueExpression getTargetGroup(){
        return targetGroup;
    }
    
         /** 
     * setter and getter the defaultDisplayNumber
     *
     * @author wbossons
     *
     * 
     */
    public void setDefaultDisplayNumber(ValueExpression defaultdisplaynumber) {
        this.defaultDisplayNumber = defaultdisplaynumber;
    }
    
    public ValueExpression getDefaultDisplayNumber(){
        return defaultDisplayNumber;
    }


    
    public void release() {
        super.release();
        contents = null;
  }
}
