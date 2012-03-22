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
 * SetDetailBean.java
 *
 * Created on May 3, 2007, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.harvest;

/**
 *
 * @author Ellen Kraffmiller
 */
public class SetDetailBean implements java.io.Serializable  {
    
    /** Creates a new instance of SetDetailBean */
    public SetDetailBean() {
    }

    /**
     * Holds value of property name.
     */
    private String name;

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property spec.
     */
    private String spec;

    /**
     * Getter for property spec.
     * @return Value of property spec.
     */
    public String getSpec() {
        return this.spec;
    }

    /**
     * Setter for property spec.
     * @param spec New value of property spec.
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }
    
}
