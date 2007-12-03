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
 * DataList.java
 *
 * Created on November 15, 2007, 11:16 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.component;

import com.sun.rave.web.ui.component.Hyperlink;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import org.ajax4jsf.ajax.html.HtmlAjaxCommandLink;

/**
 *
 * @author wbossons
 */
public class DataList extends UIComponentBase {

    /** Creates a new instance of DataList */
    public DataList() {
    }

    public String getFamily() {
        return null;
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facescontext)
                            throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
        writer.startElement("div", this);
        //
    }

    public void encodeChildren(FacesContext facescontext)
        throws IOException {
        Map map = (Map)getAttributes().get("contents");
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        for (int i = 0; i < map.size(); i++){
          String key = iterator.next().toString();
          Object object = map.get(key);
          List<DataListing> datalistings = (List<DataListing>)map.get(key);
          formatHeading(key);
          formatChildTable(datalistings, key);
          formatNavigationLinks(key);
        }
    }

    public void encodeEnd(FacesContext facescontext)
        throws IOException {
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        writer.endElement("div");
    }

    // helper methods
      //utils
    private void formatHeading(String heading) {
        HtmlPanelGroup panelGroup = new HtmlPanelGroup();
        panelGroup.setStyleClass("dvnMainPanel");
        UIOutput headingText = new UIOutput();
        ValueBinding outervaluebinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(heading);
        headingText.setValueBinding("value", outervaluebinding);
        try {
            panelGroup.getChildren().add(headingText);
            this.getChildren().add(panelGroup);
            panelGroup.encodeBegin(FacesContext.getCurrentInstance());
            if (panelGroup.getRendersChildren()) {
                panelGroup.encodeChildren(FacesContext.getCurrentInstance());
            }
            panelGroup.encodeEnd(FacesContext.getCurrentInstance());
        } catch (IOException ioe) {
            System.out.println("A problem occurred in the custom tag while formatting the heading.");
        }
    }
    
     /* formatChildTable
     *
     * formats the nested tables
     * of the Vdc groups
     *
     * @author wbossons
     */
    private void formatChildTable(List ndvs, String heading) {
        //Set no. of records per column
        int totalColumns = 3;
        //TODO: use and/or calculate the 1st arg of setColumnLength from the current rows property
        //DEBUG
        int columns = 0;
        int startNew = 0;
        if (totalColumns <= ndvs.size())
            startNew = setColumnLength(ndvs.size(), totalColumns);
        else
            startNew = setColumnLength(3);//was (ndvs.size(), totalColumns);
        int startPos = 0;
        ListIterator iterator = ndvs.listIterator();
        HtmlPanelGrid childTable = new HtmlPanelGrid();
        childTable = new HtmlPanelGrid(); // start the child table which eventually must be added to the view
        childTable.setId(heading);
        childTable.setStyleClass("dvnChildTable");
        childTable.setColumns(3);
        childTable.setColumnClasses("dvnChildColumn");
        UIColumn column              = null;
        HtmlPanelGroup linkPanel     = null;
        HtmlOutputText startLinkTag  = null;
        HtmlOutputText endLinkTag    = null;
        HtmlOutputText affiliationTag = null;
        Hyperlink nodelink           = null;
        HtmlGraphicImage image       = null;
        HtmlOutputText textTag       = null;
        
            while (iterator.hasNext()) {
                DataListing ndv  = (DataListing)iterator.next();
                if (startPos == 0) {
                column = new UIColumn();
            }
            startLinkTag = new HtmlOutputText();
            startLinkTag.setEscape(false);
            startLinkTag.setValue("<ul><li class='activeBullet'>");
            nodelink = new Hyperlink();
            nodelink.setText(ndv.getName());
            nodelink.setToolTip(ndv.getTooltip() + " dataverse");
            nodelink.setUrl("/dv/" + ndv.getAlias());
            endLinkTag = new HtmlOutputText();
            endLinkTag.setEscape(false);
            endLinkTag.setValue("</li></ul>");
            linkPanel = new HtmlPanelGroup();
            linkPanel.getChildren().add(startLinkTag);
            linkPanel.getChildren().add(nodelink);
            if ( ndv.getRestricted().equals("yes") ) {
                
                textTag =  new HtmlOutputText();
                textTag.setEscape(false);
                textTag.setValue("<span class='dvn_dvNotReleased'>Not Released</span>");
                linkPanel.getChildren().add(textTag);
                nodelink.setToolTip("This dataverse is not released.");
            } 
            if (ndv.getAffiliation() != null && !ndv.getAffiliation().equals("")) {
                affiliationTag = new HtmlOutputText();
                affiliationTag.setEscape(false);
                affiliationTag.setValue("<ul class='affiliationBullet'><li class='affiliationBullet'>" + ndv.getAffiliation() + "</li></ul>");
                linkPanel.getChildren().add(affiliationTag);
            }
            linkPanel.getChildren().add(endLinkTag);
            column.getChildren().add(linkPanel);
            startPos++;
            if (startPos == startNew || iterator.hasNext() == false) {
                childTable.getChildren().add(column);
                startPos = 0;
            }
        }
        //manage the condition where there were only enough records
        // to build two columns -- to achieve balance in the presentation
        if (childTable.getChildCount() < totalColumns) {
            int remainder = totalColumns - childTable.getChildCount();
            int i = 0;
            HtmlOutputText placeholder = null;
            while (i < remainder) {
                column = new UIColumn();
                placeholder = new HtmlOutputText();
                placeholder.setEscape(false);
                placeholder.setValue("<br />");
                column.getChildren().add(placeholder);
                childTable.getChildren().add(column);
                i++;
            }
        }
        
        try {
            this.getChildren().add(childTable);
            childTable.encodeBegin(FacesContext.getCurrentInstance());
            if (childTable.getRendersChildren()) {
                childTable.encodeChildren(FacesContext.getCurrentInstance());
            }
            childTable.encodeEnd(FacesContext.getCurrentInstance());
            FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ExpressionFactory expressionFactory = application.getExpressionFactory();

        HtmlAjaxCommandLink previousLink = new HtmlAjaxCommandLink();
        previousLink.setId("previous_" + heading); // Custom ID is required in dynamic UIInput and UICommand.
        //previousLink.setValue("Previous | ");
        previousLink.setReRender("form1" + heading);//content:homePageView:form1:
        //HtmlOutputText uiText = new HtmlOutputText();
        //uiText.setValue(heading);
        MethodExpression previousActionListener = expressionFactory.createMethodExpression(elContext, "#{HomePage.page_action}", null, new Class[] {ActionEvent.class});
        previousLink.addActionListener(new MethodExpressionActionListener(previousActionListener));
        
        
        } catch (IOException ioe) {
            System.out.println("A problem occurred in the custom tag while formatting the child table.");
        }
    }
    
    private void formatNavigationLinks(String heading) {
        try {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ExpressionFactory expressionFactory = application.getExpressionFactory();

        HtmlAjaxCommandLink previousLink = new HtmlAjaxCommandLink();
        previousLink.setId("previous_" + heading); // Custom ID is required in dynamic UIInput and UICommand.
        previousLink.setValue("Previous | ");
        previousLink.setReRender("content:homePageView:form1:" + heading);//content:homePageView:form1:
        MethodExpression previousActionListener = expressionFactory.createMethodExpression(elContext, "#{HomePage.page_action}", null, new Class[] {ActionEvent.class});
        previousLink.addActionListener(new MethodExpressionActionListener(previousActionListener));
        
        HtmlAjaxCommandLink nextLink = new HtmlAjaxCommandLink();
        nextLink.setId("next_" + heading); // Custom ID is required in dynamic UIInput and UICommand.
        nextLink.setValue("Next");
        nextLink.setReRender("content:homePageView:form1:" + heading);//
        MethodExpression nextActionListener = expressionFactory.createMethodExpression(elContext, "#{HomePage.page_action}", null, new Class[] {ActionEvent.class});
        nextLink.addActionListener(new MethodExpressionActionListener(nextActionListener));
        
        this.getChildren().add(previousLink);
        previousLink.encodeBegin(FacesContext.getCurrentInstance());
        previousLink.encodeEnd(FacesContext.getCurrentInstance());
        this.getChildren().add(nextLink);
        nextLink.encodeBegin(FacesContext.getCurrentInstance());
        nextLink.encodeEnd(FacesContext.getCurrentInstance());
        } catch (IOException ioe) {
           System.out.println("There was an IO Exception while creating the previous and next links." + ioe.toString());
        } catch (Exception e) {
            System.out.println("There was an issue in the create links area " + e.toString());
        } /*finally {
            Debug
            FacesContext context = FacesContext.getCurrentInstance();
            Iterator iterator = this.getChildren().iterator();
            System.out.println("this component is " + this.getClientId(context));
            while (iterator.hasNext()) {
                UIComponent childComponent = (UIComponent)iterator.next();
                if (childComponent.getChildCount() != 0) {
                    Iterator innerIterator = childComponent.getChildren().iterator();
                    while (innerIterator.hasNext()) {
                        UIComponent innerChildComponent = (UIComponent)innerIterator.next();
                        System.out.println("the inner children are " + innerChildComponent.getClientId(context));
                    }
                }
                System.out.println("The client id of the next child component is ... " + childComponent.getClientId(context) + " and it's id is " + childComponent.getId());
            }
        }*/
    }
    
        /** setColumnLength();
     *
     * used to set the no of
     * records per column
     *
     * @param numRecords
     * @param numColumns
     * @return int
     *
     * @author wbossons
     *
     */
    private int setColumnLength(int numRecords, int numColumns) {
        int startNew = 0;
        double doubleRecords = ((Integer)numRecords).doubleValue();
        double doubleColumns = ((Integer)numColumns).doubleValue();
        startNew = ((Number)Math.ceil(doubleRecords/doubleColumns)).intValue();
        return startNew;
    }
    
    //overload (under?) for when the records are contracted
    private int setColumnLength(int rows) {
        return rows;
    }
}
