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
import java.lang.String;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIColumn;
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
        writer.writeAttribute("class", "TabGrp", null);
        writer.startElement("a", this);
        writer.writeAttribute("id", "tabSet1skipLink_skipHyperlinkId", null);
        writer.writeAttribute("href", "#tabSet1skipLink_skipHyperlinkId", null);
        writer.startElement("img", this);
        writer.writeAttribute("id", "tabSet1skipLink_skipHyperlinkId_image", null);
        writer.writeAttribute("src", "/dvn/theme/com/sun/rave/web/ui/defaulttheme/images/other/dot.gif", null);
        writer.writeAttribute("alt", "Jump Over Tab Navigation Area. Current selection is ", null);
        writer.writeAttribute("border", "1", null);
        writer.endElement("img");
        writer.endElement("a");
    }

    public void encodeChildren(FacesContext facescontext)
        throws IOException {
        String tab = (String)getAttributes().get("tab");
        Map tabsMap = (Map)getAttributes().get("tabs");
        Set tabsKeys = tabsMap.keySet();
        Iterator tabsIterator = tabsKeys.iterator();
        String[] tabNames = new String[tabsMap.size()];
        String[] tabkeys = new String[tabsMap.size()];
        String nodisplaymsg = new String("");
        for (int i = 0; i < tabsMap.size(); i++){
              String key = tabsIterator.next().toString();
              Object object = tabsMap.get(key);
              //object is a Tab, get it
              try {
                  if (key.equals(tab)) {
                        Method getNoDisplayMsg = object.getClass().getMethod("getNoDisplayMsg", new Class[0]);
                        nodisplaymsg = (String)getNoDisplayMsg.invoke(object);
                  }
                  Method getTabName = object.getClass().getMethod("getName", new Class[0]);
                  tabNames[i] = (String)getTabName.invoke(object);
                  Method getKey = object.getClass().getMethod("getKey", new Class[0]);
                  tabkeys[i] = (String)getKey.invoke(object);
             } catch (NoSuchMethodException nme) {
                  throw new FacesException(nme.toString());
             } catch (Exception e) {
                  throw new FacesException(e.toString());
             }
        }
        formatTabs(tabNames, tab, tabkeys);
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        writer.startElement("div", this);
        writer.writeAttribute("class", "TabGrpBox", null);
        Map map = (Map)getAttributes().get("contents");
        if (map.isEmpty()) 
            formatMessage(nodisplaymsg);
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        for (int i = 0; i < map.size(); i++){
          String key = iterator.next().toString();
          Object object = map.get(key);
          List<DataListing> datalistings = (List<DataListing>)map.get(key);
          if (datalistings.isEmpty()) { //TODO: remove and test this.
              continue;
          } else {
            formatHeading(key);
            formatChildTable(datalistings, key);
          }
        }
        
        writer.endElement("div");
    }

    public void encodeEnd(FacesContext facescontext)
        throws IOException {
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        writer.endElement("div");
    }
    
    private void formatTabs(String[] tabNames, String tab, String[]keys) {
        /** write this out
         *  because there is some kind of bug where this
         *  is not formatting the table correctly when written
         *  similarly to the formatChild method
         */
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        try {
        writer.startElement("div", this);
        writer.writeAttribute("class", "MniTabDiv", null);
        writer.startElement("table", this);
        writer.writeAttribute("class", "MniTabTbl", null);
        //title="" border="0" cellpadding="0" cellspacing="0"
        writer.writeAttribute("title", "", null);
        writer.writeAttribute("border", "0", null);
        writer.writeAttribute("cellpadding", "0", null);
        writer.writeAttribute("cellspacing", "0", null);
        writer.startElement("tr", this);
        for (int i = 0; i < tabNames.length; i++) {
            writer.startElement("td", this);
            if (keys[i].equals(tab)) {
                writer.writeAttribute("class", "MniTabTblSelTd", null);//TODO parameterize this from the url or jsp
                writer.startElement("div", this);
                // title="Current Selection: Cataloging Information"
                writer.writeAttribute("title", "Current Selection: " + tabNames[i], null);//TODO: set this according to url param
                writer.writeAttribute("class", "MniTabSelTxt", null);
                writer.startElement("span", this);
                writer.writeAttribute("id", keys[i], null);//TODO, set this to the key
                writer.writeAttribute("class", "disabled", null);
                writer.writeText(tabNames[i], null);
                writer.endElement("span");
                writer.endElement("div");
            } else {
                writer.startElement("a", this);
                writer.writeAttribute("id", keys[i], null);
                writer.writeAttribute("class", "MniTabLnk", null);
                writer.writeAttribute("title", tabNames[i], null);
                writer.writeAttribute("href", "/dvn/?tab=" + keys[i], null);//TODO parameterize the link to the home page.
                writer.writeText(tabNames[i], null);
                writer.endElement("a");
            }
            writer.endElement("td");
        }
        writer.endElement("tr");
        writer.endElement("table");
        writer.endElement("div");
        writer.startElement("div", this);
        writer.startElement("a", this);
        writer.writeAttribute("id", "tabSet1skipLink_skipHyperlinkId", null);
        writer.writeAttribute("name", "tabSet1skipLink_skipHyperlinkId", null);
        writer.endElement("a");
        writer.endElement("div");
    } catch (Exception ioe) {
            throw new FacesException();
        }
        
    }

    // helper methods
      //utils
    private void formatHeading(String heading) {
        if (heading != "") {
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
                throw new FacesException();
            }
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
        int totalColumns = 4;
        int columns = 0;
        int startNew = 0;
        if (totalColumns <= ndvs.size())
            startNew = setColumnLength(ndvs.size(), totalColumns);
        else
            startNew = setColumnLength(totalColumns);//was (ndvs.size(), totalColumns);
        int startPos = 0;
        ListIterator iterator = ndvs.listIterator();
        HtmlPanelGrid childTable = new HtmlPanelGrid();
        childTable = new HtmlPanelGrid(); // start the child table which eventually must be added to the view
        childTable.setId(formatId(heading));
        childTable.setStyleClass("dvnChildTable");
        childTable.setColumns(totalColumns);
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
       
            } catch (IOException ioe) {
            throw new FacesException();
        } //TODO: implment ajax command links 
        //finally {
            //createNavigationLinks(heading);
        //}
    }
    
    private void formatMessage(String message) {
        HtmlOutputText output = new HtmlOutputText();
        output.setValue(message);
        this.getChildren().add(output);
        try {
            output.encodeBegin(FacesContext.getCurrentInstance());
            if (output.getRendersChildren())
                output.encodeChildren(FacesContext.getCurrentInstance());
            output.encodeEnd(FacesContext.getCurrentInstance());
        } catch (IOException ioe) {
            throw new FacesException();
        }
    }
    
    private void createNavigationLinks(String heading) {
        try {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        ExpressionFactory expressionFactory = application.getExpressionFactory();

        HtmlAjaxCommandLink previousLink = new HtmlAjaxCommandLink();
        previousLink.setId("previous_" + heading); // Custom ID is required in dynamic UIInput and UICommand.
        previousLink.setValue("<< Previous | ");
        MethodExpression previousActionListener = expressionFactory.createMethodExpression(elContext, "#{list.page_action}", null, new Class[] {ActionEvent.class});
        previousLink.addActionListener(new MethodExpressionActionListener(previousActionListener));
        this.getChildren().add(previousLink);

        HtmlAjaxCommandLink nextLink = new HtmlAjaxCommandLink();
        nextLink.setId("next_" + heading); // Custom ID is required in dynamic UIInput and UICommand.
        nextLink.setValue("Next >>");
        MethodExpression nextActionListener = expressionFactory.createMethodExpression(elContext, "#{list.page_action}", null, new Class[] {ActionEvent.class});
        nextLink.addActionListener(new MethodExpressionActionListener(nextActionListener));

        this.getChildren().add(nextLink);
        } catch (Exception e) {
            System.out.println("there was an issue in the create links area " + e.toString());
        } 
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
    
    /**  formatId
     * @description
     * 
     * headings are used to build ids
     * This function munges the heading
     * into a safe form for ids
     * 
     * 
     * @author wbossons
     * 
     */
    private String formatId(String heading) {
        String safeId = heading;
        //String regexp = "['\\@\\#\\$%\\^&\\*\\(\\)_\\+\\:\\<\\>\\/\\[\\]\\\\{\\}\\|\\p{Punct}\\p{Space}]";
        String regexp = "[^A-Za-z0-9]";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(heading);
        Boolean isInvalidChars  = matcher.find();
        if (isInvalidChars)
            safeId = heading.replaceAll(regexp, "");
        safeId = "dvn" + safeId;
        return safeId.toLowerCase();
    }
}
