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
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
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

    public void encodeBegin(FacesContext facescontext)
                            throws IOException {
        ResponseWriter writer = facescontext.getResponseWriter();
        Map map = (Map)getAttributes().get("contents");
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        for (int i = 0; i < map.size(); i++){
          String key = iterator.next().toString();
          Object object = map.get(key);
          List<DataListing> datalistings = (List<DataListing>)map.get(key);
          formatHeading(key);
          formatChildTable(datalistings, key);
        }
    }

    public void encodeChildren(FacesContext facescontext)
        throws IOException {
        //Nothing to do

    }

    public void encodeEnd(FacesContext facescontext)
        throws IOException {
        // nothing to do
    }

    // helper methods
      //utils
    private void formatHeading(String heading) {
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        try {
          writer.startElement("span", this);
          writer.writeAttribute("class", "dvnMainPanel", null);
          writer.writeText(heading, null);
          writer.endElement("span");
        } catch (IOException ioe) {
            throw new FacesException();
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
         
        ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
        try {
            //Begin writing the child table associated with this heading
            writer.startElement("table", this);
            writer.writeAttribute("id", heading, null);
            writer.writeAttribute("class", "dvnChildTable", null);
            writer.startElement("tr", this);
            //writer.startElement("td", this);
            //writer.writeAttribute("class", "dvnChildColumn", null);
            //writer.writeAttribute("width", "33%", null);
            ListIterator iterator = ndvs.listIterator();
            while (iterator.hasNext()) {
                DataListing ndv  = (DataListing)iterator.next();
                if (startPos == 0) {
                    columns++;
                    writer.startElement("td", this);
                    writer.writeAttribute("class", "dvnChildColumn", null);
                }
                writer.startElement("ul", this);
                writer.startElement("li", this);
                writer.writeAttribute("class", "activeBullet", null);
                writer.startElement("a", this);
                writer.writeAttribute("href", "/dvn/dv/" + ndv.getAlias(), null);
                if ( ndv.getRestricted().equals("yes") ) {
                    writer.writeAttribute("title", "This dataverse is not released.", null);
                    writer.writeText(ndv.getName(), null);
                    writer.endElement("a");
                    writer.startElement("span", this);
                    writer.writeAttribute("class", "dvn_dvNotReleased", null);
                    writer.writeText("Not Released", null);
                    writer.endElement("span");
                } else {
                    writer.writeAttribute("title", ndv.getTooltip() + " dataverse", null);
                    writer.writeText(ndv.getName(), null);
                    writer.endElement("a");
                }
                if (ndv.getAffiliation() != null && !ndv.getAffiliation().equals("")) {
                    //affiliation bullets
                    writer.startElement("ul", this);
                    writer.writeAttribute("class", "affiliationBullet", null);
                    writer.startElement("li", this);
                    writer.writeAttribute("class", "affiliationBullet", null);
                    writer.writeText(ndv.getAffiliation(), null);
                    writer.endElement("li");
                    writer.endElement("ul");
                } else {
                    writer.startElement("ul", this);
                    writer.startElement("li", this);
                    writer.writeAttribute("class", "dvnLineSpacer", null);
                    writer.writeText("&nbsp;&nbsp;", null);
                    writer.endElement("li");
                    writer.endElement("ul");
                }
                // end outside bullet (represents dv name bullets)
                writer.endElement("li");
                writer.endElement("ul");
                startPos++;
                // TODO: make columns var
                if (startPos == startNew || !iterator.hasNext()) {
                    writer.endElement("td");
                    startPos = 0;
                }
                if (startPos == startNew && !iterator.hasNext()) {
                    break;

                }


            }
            //manage the condition where there were only enough records
            // to build two columns -- to achieve balance in the presentation
            if (columns < totalColumns) {
                int remainder = totalColumns - columns;
                int i = 0;
                HtmlOutputText placeholder = null;
                while (i < remainder) {
                    writer.startElement("td", this);
                    writer.startElement("br", this);
                    writer.endElement("br");
                    writer.endElement("td");
                    i++;
                }
            }
            writer.endElement("tr");
            writer.endElement("table");
            } catch (IOException ioe) {
            throw new FacesException();
        } //TODO: implment ajax command links 
        //finally {
            //createNavigationLinks(heading);
        //}
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
}
