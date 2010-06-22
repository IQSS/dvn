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

package edu.harvard.iq.dvn.core.catalog;

import ORG.oclc.oai.server.verb.NoItemsMatchException;
import edu.harvard.iq.dvn.core.study.Study;
import javax.ejb.Local;


/**
 * This is the business interface for CatalogService enterprise bean.
 */
@Local
public interface CatalogServiceLocal extends java.io.Serializable  {
    public String [] listRecords(String from, String until, String set, String metadataPrefix) throws NoItemsMatchException;

    public String getRecord(Study study, String metadataPrefix);

    public Study[] listStudies(String from, String until, String set, String metadataPrefix);
    
}
