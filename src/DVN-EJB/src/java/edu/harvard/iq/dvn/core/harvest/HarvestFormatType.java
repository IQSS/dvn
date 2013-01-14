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
 * HarvestFormatType.java
 * 
 * Created on Oct 3, 2007, 3:23:21 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.harvest;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Gustavo
 */
@Entity
public class HarvestFormatType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String metadataPrefix;
    private String stylesheetFileName;

    
    
    public HarvestFormatType() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }

    public String getStylesheetFileName() {
        return stylesheetFileName;
    }

    public void setStylesheetFileName(String stylesheetFileName) {
        this.stylesheetFileName = stylesheetFileName;
    }    
    
}
