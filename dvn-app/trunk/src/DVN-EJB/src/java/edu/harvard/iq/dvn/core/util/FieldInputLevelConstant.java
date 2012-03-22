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
    private final static String hidden = "hidden";
    
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
    
    public  final static String getHidden() {
        return hidden;
    }
    
}
