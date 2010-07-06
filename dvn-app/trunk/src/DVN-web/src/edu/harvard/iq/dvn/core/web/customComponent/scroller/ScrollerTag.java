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

/*
 * ScrollerTag.java
 *
 * Created on November 13, 2006, 9:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web.customComponent.scroller;

// original copyright notice
/* Copyright 2005 Sun Microsystems, Inc.  All rights reserved.  You may not modify, use, reproduce, or distribute this software except in compliance with the terms of the License at: 
 http://developer.sun.com/berkeley_license.html
 $Id: ScrollerTag.java,v 1.1 2006/11/15 06:54:53 asone Exp $ */

//package com.sun.javaee.blueprints.components.ui.taglib;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.webapp.UIComponentELTag;

import java.util.Map;

//import com.sun.javaee.blueprints.components.ui.components.ScrollerComponent;
import edu.harvard.iq.dvn.core.web.customComponent.scroller.ScrollerComponent;

/**
 * <p> ScrollerTag is the tag handler class for <code>ScrollerComponent.
 */
public class ScrollerTag extends UIComponentELTag implements java.io.Serializable  {

    protected MethodExpression actionListener = null;
    protected ValueExpression navFacetOrientation = null;
    protected ValueExpression forValue = null;


    /**
     * <code>MethodExpression</code> to handle an action event generated as 
     * a result of clicking on a link that points a particular page in the 
     * result-set.
     */
    public void setActionListener(MethodExpression actionListener) {
        this.actionListener = actionListener;
    }


    /*
     * When rendering a widget representing "page navigation" where
     * should the facet markup be rendered in relation to the page
     * navigation widget?  Values are "NORTH", "SOUTH", "EAST", "WEST".
     * Case insensitive. 
     */
    public void setNavFacetOrientation(ValueExpression navFacetOrientation) {
        this.navFacetOrientation = navFacetOrientation;
    }


    /*
     * The data grid component for which this acts as a scroller.     
     */
    public void setFor(ValueExpression forValue) {
        this.forValue = forValue;
    }


    public String getComponentType() {
        //return ("Scroller");
        return ("scroller");
    }


    public String getRendererType() {
        return (null);
    }

    @Override
    public void release() {
        super.release();
        navFacetOrientation = null;
        actionListener = null;
        forValue = null;
    }

    @Override
    protected void setProperties(UIComponent component) {
        super.setProperties(component);        
        ScrollerComponent scroller = (ScrollerComponent) component;
        Map<String,Object> attributes = scroller.getAttributes();

        if (actionListener != null) {
            scroller.addActionListener(
                  new MethodExpressionActionListener((actionListener)));
        }        

        // if the attributes are values set them directly on the component, if
        // not set the ValueExpression reference so that the expressions can be
        // evaluated lazily.
        if (navFacetOrientation != null) {
            if (!navFacetOrientation.isLiteralText()) {
                scroller.setValueExpression("navFacetOrientation",
                                            navFacetOrientation);
            } else {
                attributes.put("navFacetOrientation",
                               navFacetOrientation.getExpressionString());
            }
        }

        if (forValue != null) {
            if (!forValue.isLiteralText()) {
                scroller.setValueExpression("for", forValue);
            } else {
                attributes.put("for", forValue.getExpressionString());
            }
        }       
    }
}

