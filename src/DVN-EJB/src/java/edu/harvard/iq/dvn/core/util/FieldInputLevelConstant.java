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
 * FieldInputLevelConstant.java
 *
 * Created on September 26, 2006, 2:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.util;

/**
 *
 * @author Ellen Kraffmiller
 */
public final class FieldInputLevelConstant implements java.io.Serializable  {
    private final static String required = "required";
    private final static String recommended = "recommended";
    private final static String optional = "optional";
    
    /** Creates a new instance of FieldInputLevelConstant */
    public  FieldInputLevelConstant() {
    }

    public final static String getRequired() {
        return required;
    }

    public  final static String getRecommended() {
        return recommended;
    }

    public  final static String getOptional() {
        return optional;
    }
    
}
